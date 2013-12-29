package kz.bee.cloud.queue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import kz.bee.cloud.queue.model.Dashboard;
import kz.bee.cloud.queue.model.Group;
import kz.bee.cloud.queue.model.Lane;
import kz.bee.cloud.queue.model.Ticket;
import kz.bee.cloud.queue.model.Token;
import kz.bee.cloud.queue.model.Unit;
import kz.bee.cloud.queue.model.User;
import kz.bee.cloud.queue.model.User.Language;
import kz.bee.cloud.queue.reservation.Reservation;
import kz.bee.cloud.queue.reservation.Reservation.Status;
import kz.bee.hibernate.connection.DBManager;
import kz.bee.util.QLog;
import kz.bee.util.QueuePluginException;

@SuppressWarnings("unchecked")
public class QueueWrapper{

	private EntityManager em;
	private CloudQueue queue;
	private ObjectMapper mapper;

	public QueueWrapper(EntityManager em){
		this.em = em;
		queue = new CloudQueue(em);
		this.mapper = new ObjectMapper();
	}
	
	public Ticket enqueue(Group group, String clientId, List<String> laneNames, String language) throws QueuePluginException {
		if(group==null){
			throw new QueuePluginException("wrapper.group.notfound");
		}
		if(laneNames.size()==0){
			throw new QueuePluginException("wrapper.no.lanes.chosen");
		}
		Language lang = Language.get(language);
		List<Lane> lanes = em.createQuery("" +
				" from" +
				" 	Lane l" +
				" where" +
				" 	l.group=:group" +
				" 	and l.username in (:names)")
				.setParameter("group", group)
				.setParameter("names", laneNames)
				.getResultList();
		
		List<Lane> sortedLanes = new ArrayList<Lane>();
		for(String laneName:laneNames){
			for(Lane lane:lanes){
				if(lane.getUsername().equalsIgnoreCase(laneName)){
					sortedLanes.add(lane);
				}
			}
		}
		
		Ticket ticket;
		
		QLog.info("Searching reservation group " + group + " clientId " + clientId);
		List<Date> reservationDateList = em.createQuery(
				"select r.date " +
				"from Reservation r " +
				"where year(r.date)=year(CURRENT_DATE()) " +
				"  and month(r.date)=month(CURRENT_DATE()) " +
				"  and day(r.date)=day(CURRENT_DATE()) " +
				"  and r.group=:group " +
				"  and r.status='OPEN' " +
				"  and r.personalId=:clientId ")
				.setParameter("group",group)
				.setParameter("clientId",clientId)
				.setMaxResults(1)
				.getResultList();
		

		Date now = new Date();
		boolean reserved = false;
		System.out.println ("queue wrapper Sultan");
		System.out.println (reservationDateList.size());
		QLog.info("QueueWrapper enqueue checking if reserved..");
		if (reservationDateList != null) {
			if (!reservationDateList.isEmpty()) {
				int tReserved, tNow;
				
				for (Date reservationDate: reservationDateList) {
					System.out.println (reservationDate+"reservation Date");
					tReserved = reservationDate.getHours() * 60 + reservationDate.getMinutes();
					tNow = now.getHours() * 60 + now.getMinutes();
					reserved = ( tReserved <= tNow && tNow < (tReserved + 30) ); 
					System.out.println (tReserved+" "+tNow +" "+ reserved+"111111111111111111111111111");
				}
			}
			else
				QLog.info("rList is Empty");
		}
		else
			QLog.info("rList is null");
				
		
		if (reserved) {
			ticket = queue.enqueue(group, clientId, sortedLanes, lang, 10);
			QLog.info("QueueWrapper enqueue Reserved");
		}
		else
			 ticket = queue.enqueue(group, clientId, sortedLanes, lang, 0);
		
		Reservation.processTicket(ticket,em); // open or closed
		for (Token toke: ticket.getTokenList())
			QLog.info("ticket token laneName " + toke.getLaneName() + " priority " + toke.getPriority());
		return ticket;
	}
	
	/**Requires active transaction*/
	public Token call(Unit unit) throws QueuePluginException{
		if(unit==null){
			throw new QueuePluginException("wrapper.unit.notfound");
		}
		return queue.call(unit);
	}
	
	public Token recall(final Long tokenId, final String groupName) throws QueuePluginException {
		Token token = em.find(Token.class,tokenId);
		if(!token.getTicket().getGroup().getName().equalsIgnoreCase(groupName)){
			throw new QueuePluginException("wrapper.restrict.group");
		}	
		return queue.recall(token);
	}
	
	/**Requires active transaction*/
	public Token start(Unit unit) throws QueuePluginException{
		if(unit==null){
			throw new QueuePluginException("wrapper.unit.notfound");
		}
		return queue.start(unit);
	}
	
	/**Requires active transaction*/
	public Token advertisement(Unit unit) throws QueuePluginException{
		if(unit==null){
			throw new QueuePluginException("wrapper.unit.notfound");
		}
		return queue.advertisement(unit);
	}
	
	public Token postpone(final String unitname) throws QueuePluginException {
		List<Token> tokenList = em.createQuery("from Token t join fetch t.ticket tt" +
				" where" +
				" 	t.status in('RECALLED','CALLED','STARTED')" +
				" 	and t.target.id=:unit")
			.setParameter("unit", unitname).getResultList();
		if(tokenList.isEmpty()){
			throw new QueuePluginException("postpone.no.token");
		}
		return queue.postpone(tokenList.get(0));
	}
	
	/**Requires active transaction*/
	public Token end(Unit unit) throws QueuePluginException{
		if(unit==null){
			throw new QueuePluginException("wrapper.unit.notfound");
		}
		return queue.end(unit);
	}
	
	/**Requires active transaction*/
	public Token current(Unit unit) {
		return queue.current(unit);
	}
	
	public List<Lane> getLanes(final String groupName){
		System.out.println("-------------------------------------------------2");
		List<Lane> lanes= em.createQuery("from Lane l" +
				" where" +
				" 	l.group.id=:groupName" +
				" 	and l.status='ENABLED' order by username")
			.setParameter("groupName",groupName).getResultList();
		for(Lane l:lanes){
			System.out.println(l.getUsername());
		}
		System.out.println("------------------------------------------------------3");
		return lanes;
	}
	
	public List<Lane> getLanes(){
		return em.createQuery("from Lane l where l.status='ENABLED'").getResultList();
	}

	public List<User> getUsers(){
		return em.createQuery("from User").getResultList();
	}

	public List<User> getUsers(final String groupName){
		return em.createQuery("from User u where u.group.id=:groupName")
			.setParameter("groupName", groupName)
			.getResultList();
	}

	public List<Group> getGroups(){
		return em.createQuery("from Group g where g.name!='main'").getResultList();
	}

	public <A> A find(final String username,final Class<A> entityClass) throws QueuePluginException{
		return (A) em.find(entityClass,username);	
	}
	
	public User findUser(String username) throws QueuePluginException {
		return find(username,User.class);
	}
	
	public Unit findUnit(final String username) throws QueuePluginException {
		return find(username,Unit.class);
	}

	public Dashboard findDashboard(String username) throws QueuePluginException {
		return find(username,Dashboard.class);
	}

	public Token transfer(final Long tokenId,String laneName, final String destination, final String groupName,String radioButton,boolean returnToken) throws QueuePluginException {
		Token token = em.find(Token.class,tokenId);
		if(token==null){
			throw new QueuePluginException("wrapper.token.notfound");
		}
		if(!token.getTicket().getGroup().getName().equalsIgnoreCase(groupName)){
			throw new QueuePluginException("wrapper.token.restrict.group");	
		}
		User user = null;
		if(destination!=null){
			user = em.find(User.class, destination);
		}
		return queue.transfer(token,laneName,user,radioButton,returnToken);
	}
	
	public Token transfer(Long tokenId,String laneName, String groupName, String radioButton,boolean returnToken) throws QueuePluginException {
		return transfer(tokenId, laneName, null, groupName,radioButton,returnToken);
	}
	public Token transfer(Long tokenId,String groupName, String radioButton) throws QueuePluginException {
		return transfer(tokenId, null,null, groupName,radioButton,false);
	}
	public List<Dashboard> fetchDashboards(final String groupName) throws QueuePluginException {
		return em.createQuery("from" +
				" 	Dashboard d" +
				" 	join fetch d.dashboardLanes l" +
				" where" +
				" 	d.group.id=:groupName" +
				"	and d.status='ENABLED'")
			.setParameter("groupName",groupName).getResultList();
	}
	
	public List<User> fetchUsers(final String groupName) throws QueuePluginException{
		return em.createQuery("select distinct u" +
				" from" +
				" 	User u " +
				"	left join fetch u.lanes l " +
				"	left join fetch u.dashboardLanes" +
				" where" +
				"	u.group.id=:groupName")
			.setParameter("groupName",groupName).getResultList();
	}
	
	public List<Unit> fetchUnits(final String groupName) throws QueuePluginException {
		return em.createQuery(" from" +
				" 	Unit u" +
				" 	join fetch u.lanes l" +
				" where" +
				" 	u.group.id=:groupName" +
				" 	and u.status='ENABLED'")
			.setParameter("groupName",groupName).getResultList();
	}
	
	public Group fetchGroup(final String groupName) throws QueuePluginException {
		List<Group> groupList = em.createQuery(" from" +
				" 	Group g" +
//				" 	join fetch g.kioskTemplate" +
//				" 	join fetch g.unitTemplate" +
//				"	join fetch g.dashboardTemplate" +
				" where" +
				" 	g.id=:groupName")
			.setParameter("groupName",groupName).getResultList();
		if(groupList.isEmpty()){
			throw new QueuePluginException("group.not.found");
		}
		return groupList.get(0);
	}
	
	public Group getGroup(String groupName) throws QueuePluginException {
		return em.find(Group.class,groupName);
	}

	public Group getUserGroup(String from) throws QueuePluginException {
		List<Group> groupList = em.createQuery("select u.group" +
				" from" +
				" 	User u" +
				" where" +
				" 	u.id=:username")
			.setParameter("username",from).getResultList();
		if(groupList.isEmpty()){
			throw new QueuePluginException("group.not.found");
		}
		return groupList.get(0);
	}

	public List<Unit> getUnits(final String groupName) throws QueuePluginException {
		return em.createQuery("from Unit u" +
				" where" +
				" 	u.status='ENABLED'" +
				"	and u.group.id=:groupName order by username")
			.setParameter("groupName",groupName).getResultList();
	}

	public EntityManager getEntityManager() {
		return em;
	}

	
	public Token back(Unit unit) throws QueuePluginException {
		if(unit==null){
			throw new QueuePluginException("wrapper.unit.notfound");
		}
		return queue.back(unit);
	}

	public Token callByToken(Unit unit, Token ticketToken) throws QueuePluginException {
		if(unit==null){
			throw new QueuePluginException("wrapper.unit.notfound");
		}
		return queue.callByToken(unit,ticketToken);	
	}
	public List<Group> getListGroups(Group userGroup) {
		List<Group> groups = null;
		try {
			groups = em
					.createQuery(
							"select g from Group g where g.parent = :parent")
					.setParameter("parent", userGroup).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return groups;
	}
	public ArrayNode getGroups(Group userGroup) {
//		EntityManager em = DBManager.newEm();
//		em.getTransaction().begin();
		ArrayNode arrayNode = mapper.createArrayNode();
		try {
			List<Group> groups = em
					.createQuery(
							"select g from Group g where g.parent = :parent")
					.setParameter("parent", userGroup).getResultList();

			for (Group group : groups)
				arrayNode.add(mapper.createObjectNode()
						.put("address", group.getProperties().get("address"))
						.put("name", group.getName()));
//			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
//			em.getTransaction().rollback();
		}
//		em.close();

		return arrayNode;
	}
}
