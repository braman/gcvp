package kz.bee.cloud.queue;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import kz.bee.cloud.queue.model.Group;
import kz.bee.cloud.queue.model.Lane;
import kz.bee.cloud.queue.model.Ticket;
import kz.bee.cloud.queue.model.Token;
import kz.bee.cloud.queue.model.Token.Status;
import kz.bee.cloud.queue.model.Unit;
import kz.bee.cloud.queue.model.User;

@SuppressWarnings("unchecked")
public class CloudQueue implements Queue {

	final NumberFormat nf = new DecimalFormat("000");
	
	private EntityManager em;
	
	public CloudQueue(EntityManager em){
		this.em = em;
	}
	
	@Override
	public Token call(Unit unit) {
		em.find(Group.class, unit.getGroup().getName(), LockModeType.PESSIMISTIC_WRITE);
		
		Token token = null;
		List<Token> tokenList = em.createQuery(
				"from " +
				"	Token t join fetch t.ticket tt " +
				"where " +
				"	tt.group = :group " +
				"	and cast(tt.create as date) = current_date() " +
				"	and t.status = 'TRANSFERRED' " +
				"	and (t.target = :unit or t.target in (:lanes))" +
				"	and tt.status = 'BUSY' " +
				"order by " +
				"	t.time_period asc, " +
				"	(case when current_timestamp() > t.create then 1 else 0 end) desc, " +
				"	t.priority desc, " +
				"	t.create asc," +
				"	t.token_priority asc, " +
				"	t.id asc," +
				"	tt.create asc")
				.setParameter("group", unit.getGroup())
				.setParameter("lanes", unit.getLanes())
				.setParameter("unit",unit)
				.setMaxResults(1).getResultList();//setMaxResults(1) убрал для того чтобы проставить статусы MISSED для предыдущих билетиков
		
		if(tokenList.isEmpty()){
			tokenList = em.createQuery(
					"from " +
					"	Token t join fetch t.ticket tt " +
					"where " +
					"	tt.group = :group " +
					"	and cast(tt.create as date) = current_date() " +
					"	and t.status = 'WAITING' " +
					"	and (t.target = :unit or t.target in (:lanes))" +
					"	and tt.status = 'WAITING' " +
					"order by " +
					"	t.time_period asc, " +
					"	(case when current_timestamp() > t.create then 1 else 0 end) desc, " +
					"	t.priority desc, " +
					"	t.create asc," +
					"	t.token_priority asc, " +
					"	t.id asc," +
					"	tt.create asc")
					.setParameter("group", unit.getGroup())
					.setParameter("lanes", unit.getLanes())
					.setParameter("unit",unit)
					.setMaxResults(1).getResultList();//setMaxResults(1) убрал для того чтобы проставить статусы MISSED для предыдущих билетиков
		}
		
		if(tokenList.size() > 0){
			token = tokenList.get(0);
		}
	
		if(token != null){
			token.setStatus(Status.CALLED);
			token.setTarget(unit);
			token.setCall(new Date());
			token.getTicket().setStatus(Ticket.Status.BUSY);
			Messenger.fire(token,em);
		}else {
			Messenger.fire(unit,em);
		}
		return token;
	}

	//TODO check if unit has such lane
	@Override
	public Token call(Unit unit, Lane lane) {
		em.find(Group.class, unit.getGroup().getName(), LockModeType.PESSIMISTIC_WRITE);
		
		Token token = null;
		List<Token> tokenList = em.createQuery(
				"from " +
				"	Token t join fetch t.ticket tt " +
				"where " +
				"	tt.group = :group " +
				"	and cast(tt.create as date) = current_date() " +
				"	and t.status = 'TRANSFERRED' " +
				"	and (t.target = :unit or t.target in (:lanes))" +
				"	and tt.status = 'BUSY' " +
				"order by " +
				"	t.time_period asc, " +
				"	(case when current_timestamp() > t.create then 1 else 0 end) desc, " +
				"	t.priority desc, " +
				"	t.create asc," +
				"	t.token_priority asc, " +
				"	t.id asc," +
				"	tt.create asc")
				.setParameter("group", unit.getGroup())
				.setParameter("lanes", unit.getLanes())
				.setParameter("unit",unit)
				.setMaxResults(1).getResultList();//setMaxResults(1) убрал для того чтобы проставить статусы MISSED для предыдущих билетиков
		
		if(tokenList.isEmpty()){
			tokenList = em.createQuery(
					"from " +
					"	Token t join fetch t.ticket tt " +
					"where " +
					"	tt.group = :group " +
					"	and cast(tt.create as date) = current_date() " +
					"	and t.status = 'WAITING' " +
					"	and (t.target = :unit or t.target in (:lanes))" +
					"	and tt.status = 'WAITING' " +
					"order by " +
					"	t.time_period asc, " +
					"	(case when current_timestamp() > t.create then 1 else 0 end) desc, " +
					"	t.priority desc, " +
					"	t.create asc," +
					"	t.token_priority asc, " +
					"	t.id asc," +
					"	tt.create asc")
					.setParameter("group", unit.getGroup())
					.setParameter("lanes", unit.getLanes())
					.setParameter("unit",unit)
					.setMaxResults(1).getResultList();//setMaxResults(1) убрал для того чтобы проставить статусы MISSED для предыдущих билетиков
		}
				
		if(tokenList.size() > 0){
			token = tokenList.get(0);
		}
	
		if(token != null){
			token.setStatus(Token.Status.CALLED);
			token.setTarget(unit);
			token.setCall(new Date());
			token.getTicket().setStatus(Ticket.Status.BUSY);
			Messenger.fire(token,em);
		}else {
			Messenger.fire(unit,em);
		}
		return token;
	}
	
	@Override
	public Token start(Unit unit) {
		Token token = null;
		Date currentDate = Calendar.getInstance().getTime();
		List<Token> tokenList = em.createQuery(
			"from " +
			"	Token t join fetch t.ticket tt " +
			"where " +
			"	tt.group = :group " +
			"	and cast(tt.create as date) = current_date()" +
			"	and t.target = :unit " +
			"	and t.status = 'CALLED' ")
			.setParameter("group", unit.getGroup())
			.setParameter("unit", unit)
			.getResultList();

		for(Token t : tokenList){
			t.setStatus(Token.Status.STARTED);
			t.setStart(currentDate);
			token = t;
			Messenger.fire(t,em);
		}
		if(token!=null){
			token.getTicket().setStatus(Ticket.Status.BUSY);	
		}
		return token;
	}
	
	@Override
	public Token advertisement(Unit unit) {
		Token token = null;
		List<Token> tokenList = em.createQuery(
			"from " +
			"	Token t join fetch t.ticket tt " +
			"where " +
			"	tt.group = :group " +
			"	and cast(tt.create as date) = current_date()" +
			"	and t.target = :unit " +
			"	and t.status = 'STARTED' ")
			.setParameter("group", unit.getGroup())
			.setParameter("unit", unit)
			.getResultList();
		System.out.println(tokenList.size()+"size of token list");
		for(Token t : tokenList){
			token = t;
			Messenger.fire(t,em);
		}
		return token;
	}
	public Token back(Unit unit) {
		Token token = null;
		List<Token> tokenList = em.createQuery(
			"from " +
			"	Token t join fetch t.ticket tt " +
			"where " +
			"	tt.group = :group " +
			"	and cast(tt.create as date) = current_date()" +
			"	and t.target = :unit " +
			"	and t.status in ('RECALLED','CALLED','STARTED')")
			.setParameter("group", unit.getGroup())
			.setParameter("unit", unit)
			.getResultList();
		for(Token t : tokenList){
			token = t;
//			token.setStatus(Status.ENDED);
//			Token newToken = new Token();
//			newToken.setStatus(Status.WAITING);
//			Ticket ticket = token.getTicket();
//			ticket.setStatus(Ticket.Status.WAITING);
//			newToken.setTicket(ticket);
//			em.persist(newToken);
			token.getTicket().setStatus(Ticket.Status.WAITING);
			token.setStatus(Status.WAITING);
			Lane lane = em.find(Lane.class,token.getLaneName());
			token.setTarget(lane);
			Messenger.fire(token,em);
		}
		return token;
	}
	@Override
	public Token end(Unit unit) {
		Token token = null;
		Date currentDate = Calendar.getInstance().getTime();
		// End all started tokens
		List<Token> tokenList = em.createQuery(
			"from " +
			"	Token t join fetch t.ticket tt " +
			"where " +
			"	tt.group = :group " +
			"	and cast(tt.create as date) = current_date()" +
			"	and t.target = :unit " +
			"	and t.status = 'STARTED' ")
			.setParameter("group", unit.getGroup())
			.setParameter("unit", unit)
			.getResultList();
		
		for(Token t : tokenList){
			t.setStatus(Token.Status.ENDED);
			t.setEnd(currentDate);
			t.setMark(0);
			t.getTicket().setStatus(Ticket.Status.END);
			Messenger.fire(t,em);
			token = t;
		}
		
		//Set WAITING status to ticket if there is another tokens to be served
		tokenList = em.createQuery(
			"from " +
			"	Token t " +
			"where " +
			"	t.ticket = :ticket " +
			"	and t.status = 'WAITING' ")
			.setParameter("ticket",token.getTicket())
			.getResultList();
		if(tokenList.size()!=0){
			token.getTicket().setStatus(Ticket.Status.WAITING);
		}
		
		// Miss all called tokens
		tokenList = em.createQuery(
			"from " +
			"	Token t join fetch t.ticket tt " +
			"where " +
			"	tt.group = :group " +
			"	and cast(tt.create as date) = current_date()" +
			"	and t.target = :unit " +
			"	and t.status = 'CALLED' ")
			.setParameter("group", unit.getGroup())
			.setParameter("unit", unit)
			.getResultList();
		
		for(Token t : tokenList){
			t.setStatus(Token.Status.MISSED);
			t.setEnd(currentDate);
			Messenger.fire(t,em);
			token = t;
		}
		if(token==null){
			return null;
		}
		
		return token;
	}

	@Override
	public Token current(Unit unit) {
		Token token = null;
		
		List<Token> tokenList = em.createQuery(
			"from " +
			"	Token t join fetch t.ticket tt " +
			"where " +
			"	tt.group = :group " +
			"	and cast(tt.create as date) = current_date() " +
			"	and t.target = :unit " +
			"	and t.status in ('RECALLED','CALLED','STARTED') " +
			"order by " +
			"	t.call desc")
			.setParameter("group", unit.getGroup())
			.setParameter("unit", unit)
			.setMaxResults(1)
			.getResultList();
		
		System.out.println(tokenList.size()+" token list size");
		if(tokenList.size() > 0){
			token = tokenList.get(0);
		}
		return token;
	}

	public Ticket enqueue(Group group, String personalId, List<Lane> lanes, User.Language lang) {
		return enqueue(group, personalId, lanes, lang, 0);
	}
	
	@Override
	public Ticket enqueue(Group group, String personalId, List<Lane> lanes, User.Language lang,int priority) {
		Ticket ticket = createTicket(group, personalId, lanes, lang, priority);
		em.persist(ticket);
		Messenger.fire(ticket,em);
		return ticket;
	}

	@Override
	public Ticket createTicket(Group group, String personalId, List<Lane> lanes, User.Language lang) {
		return createTicket(group, personalId, lanes, lang, 0);
	}
	
    public Ticket createTicket(Group group, String personalId, List<Lane> lanes, User.Language lang,int priority) {
		Date create = Calendar.getInstance().getTime();
		
		List<Lane> finalLaneList = new ArrayList<Lane>();
		finalLaneList.addAll(lanes);

		Ticket ticket = new Ticket();
		ticket.setCode(generateTicketCode(group, lanes.get(0)));
		ticket.setGroup(group);
		ticket.setCreate(create);
		Date timePeriod = new Date();
		int minutes = create.getMinutes();
		int hours = create.getHours();
		
		if(minutes < 60 && minutes > 30)
			minutes = 30;
		else
			minutes = 00;
		
		timePeriod.setTime(0);
		timePeriod.setMinutes(minutes);
		timePeriod.setHours(hours);
		timePeriod.setSeconds(0);
		timePeriod.setYear(create.getYear());
		timePeriod.setMonth(create.getMonth());
		timePeriod.setDate(create.getDay());
		
		ticket.setPersonalId(personalId);
		ticket.setLang(lang);
		int token_priority = 1;
		for (Lane lane:lanes) {
			Token token = new Token();
			token.setTarget(lane);
			token.setTicket(ticket);
			token.setCreate(create);
			token.setColor(lane.getColor());
			token.setTimePeriod(timePeriod);
			token.setToken_priority(token_priority);
			token.setPriority(priority);
			ticket.getTokens().put(lane,token);
			token_priority++;			
		}
		
		return ticket;
	}
	
	private String generateTicketCode(Group group, Lane lane) {
//		 Lock the counter
		lane = em.find(Lane.class, lane.getUsername(),LockModeType.PESSIMISTIC_WRITE);
		int startRange = 0, endRange = 999;
		// Trying to get ranges from lane
		try {
			startRange = Integer.parseInt(lane.getRangeStart());
		}catch(Exception e){}
		try {
			endRange = Integer.parseInt(lane.getRangeEnd());
		}catch(Exception e){}
		
		int value = lane.getCount();
		
		Calendar currentDateCld = Calendar.getInstance();
		currentDateCld.set(Calendar.MILLISECOND,0);
		currentDateCld.set(Calendar.SECOND,0);
		currentDateCld.set(Calendar.MINUTE,0);
		currentDateCld.set(Calendar.HOUR_OF_DAY,0);
		
		if(lane.getCountDate() != null){
			Calendar cld = Calendar.getInstance();
			cld.setTime(lane.getCountDate());
			if(cld.compareTo(currentDateCld)==0 && value < endRange){
				value++;
			}else{
				value = startRange;	
			}
		}else{
			value = startRange;
		}
		lane.setCountDate(currentDateCld.getTime());		
		lane.setCount(value);
		return nf.format(lane.getCount());
	}
	
	/**
	 * if no statuses provided then using default WAITING
	 * */
	@Override
	public Integer getTokenCount(Unit unit, Status ... statuses) {
		List<Status> statusList = Arrays.asList(statuses);
		if(statusList.size()==0){
			statusList.add(Status.WAITING);
		}
		
		Long count = (Long)em.createQuery(
				"select " +
				"	count(t.id) " +
				"from " +
				"	Token t join t.ticket tt " +
				"where " +
				"	tt.group = :group " +
				"	and cast(tt.create as date) = current_date() " +
				"	and t.status in (:statuses) " +
				"	and t.target in (:lanes) ")
				.setParameter("group", unit.getGroup())
				.setParameter("statuses",statusList)
				.setParameter("lanes", unit.getLanes())
				.getSingleResult();
		return count.intValue();
	}
	
	public Map<Status,Integer> getTokenCounts(Unit unit,Status ... statuses){
		//TODO
		return null;
	}

	@Override
	public Token transfer(Token token,String radioButton,boolean returnToken) {
		return transfer(token,null,null,radioButton,returnToken);
	}

	@Override
	public Token transfer(Token token,String laneName, User target,String radioButton,boolean returnToken) {
		em.find(Group.class, token.getTicket().getGroup().getName(), LockModeType.PESSIMISTIC_WRITE);
		
		Date now = new Date();
		
		Ticket ticket = token.getTicket();
		
		System.out.println("token.getLaneName()"+token.getLaneName());
		Token nextToken = new Token();
		//Sultan
		System.out.println (returnToken+" 33333333333333333333333333 returnToken");
		if (radioButton.equals("3")){
			nextToken.setPriority(token.getPriority());
			nextToken.setCreate(new Date());
		}else if (radioButton.equals("2")){
			nextToken.setPriority(10);
			nextToken.setCreate(token.getCreate());
		}else{
			nextToken.setPriority(token.getPriority());
			nextToken.setCreate(token.getCreate());	
		}
		nextToken.setTicket(ticket);
		if(laneName!=null){
			nextToken.setLaneName(laneName);
		}else{
			nextToken.setLaneName(token.getLaneName());
		}
		nextToken.setToken_priority(token.getToken_priority());
		System.out.println("target is "+target);
		if(target!=null){
			nextToken.setTarget(target);
			ticket.setStatus(Ticket.Status.BUSY);
		}else{
			//returning back to lane
			Lane lane = em.find(Lane.class,token.getLaneName());
			nextToken.setTarget(lane);
			ticket.setStatus(Ticket.Status.WAITING);
		}
		nextToken.setStatus(Token.Status.TRANSFERRED);
		em.persist(nextToken);
		if (returnToken){
//			Token rToken = new Token();
//			rToken.setPriority(token.getPriority());
//			rToken.setCreate(token.getCreate());
//			rToken.setTicket(ticket);
//			rToken.setLaneName(token.getLaneName());
//			rToken.setToken_priority(token.getToken_priority()+1);
//			em.persist(rToken);
//			Messenger.fire(token,em);
			token.setStatus(Token.Status.WAITING);			
			token.setToken_priority(token.getToken_priority()+1);
			Messenger.fire(token,em);
		}else{
			token.setStatus(Token.Status.ENDED);
			token.setNextToken(nextToken);
			if(token.getStart()==null){
				token.setStart(now);
			}
			if(token.getEnd()==null){
				token.setEnd(now);
			}
		}
		System.out.println(nextToken.getCreate()+"1---------------------------------------");
		Messenger.fire(nextToken,em);
		return nextToken;
	}

	@Override
	public Token postpone(Token token) {
		token.setStatus(Status.POSTPONED);
		token.setPostpone(new Date());
		
		Messenger.fire(token,em);
		return token;
	}

	@Override
	public Token recall(Token token) {
		em.find(Group.class, token.getTicket().getGroup().getName(), LockModeType.PESSIMISTIC_WRITE);
		token.setStatus(Status.RECALLED);
		token.setPostpone(null);
		Messenger.fire(token,em);
		return token;
	}

	public Token callByToken(Unit unit, Token token) {
		em.find(Group.class, unit.getGroup().getName(), LockModeType.PESSIMISTIC_WRITE);
		
		if(token != null){
			token.setStatus(Token.Status.CALLED);
			token.setTarget(unit);
			token.setCall(new Date());
			token.getTicket().setStatus(Ticket.Status.BUSY);
			Messenger.fire(token,em);
		}else {
			Messenger.fire(unit,em);
		}
		return token;
	}
}
