package kz.bee.cloud.queue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.monitor.Monitor;
import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Order;
import org.hibernate.ejb.HibernateEntityManager;
import org.jivesoftware.openfire.session.GetSessionsCountTask;

import kz.bee.cloud.queue.model.Dashboard;
import kz.bee.cloud.queue.model.Group;
import kz.bee.cloud.queue.model.Lane;
import kz.bee.cloud.queue.model.MonitorUnit;
import kz.bee.cloud.queue.model.Properties;
import kz.bee.cloud.queue.model.Token;
import kz.bee.cloud.queue.model.Unit;
import kz.bee.cloud.queue.model.User;
import kz.bee.cloud.queue.model.User.Language;
import kz.bee.cloud.queue.model.User.Role;
import kz.bee.cloud.queue.model.User.Status;
import kz.bee.cloud.queue.model.Weekend;
import kz.bee.util.HashUtils;
import kz.bee.util.Messages;
import kz.bee.util.QLog;
import kz.bee.util.QueuePluginException;

/**
 * This class shouldn't contain public accessible methods
 * 
 * */
public class Admin {

	private EntityManager em;
	
	public Admin() {
		
	}
	
	public Admin(EntityManager em) {
		QLog.info("started");
		this.em = em;
		QLog.info("end");
	}
	
	public void createUnit(String username,String password,String firstName,String lastName,
			Group group,Set<Lane> lanes,String window,String workStart,String workFinish) throws QueuePluginException{
		QLog.info("started");
		if(em.find(Unit.class,username)!=null){
			throw new QueuePluginException("unit.exist:"+username);
		}
		Unit unit = new Unit(lanes);
		unit.setUsername(username);
		unit.setGroup(group);
		unit.setFirstName(firstName);
		unit.setLastName(lastName);
		unit.setWindow(window);
		unit.setWorkStart(workStart);
		unit.setWorkFinish(workFinish);
		try {
			unit.setPasswordSalt(HashUtils.getSaltString());
			unit.setPasswordHash(HashUtils.getHashString(password, unit.getPasswordSalt()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		em.persist(unit);
		QLog.info("Unit #0 created",username);
		QLog.info("end");
	}
	
	public void createDashboard(String username,String password,Group group,Set<Lane> lanes) throws QueuePluginException{
		QLog.info("started");
		if(em.find(Dashboard.class,username)!=null){
			throw new QueuePluginException("dashboard.exist:"+username);
		}
		Dashboard dash = new Dashboard(lanes);
		dash.setUsername(username);
		dash.setGroup(group);
		try {
			dash.setPasswordSalt(HashUtils.getSaltString());
			dash.setPasswordHash(HashUtils.getHashString(password, dash.getPasswordSalt()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		em.persist(dash);
		QLog.info("Dashboard #0 created",username);
		QLog.info("end");
	}
	
	public void createKiosk(String username,String password,Group group,String jsonTemplate) throws QueuePluginException{
		QLog.info("started");
		if(em.find(User.class,username)!=null){
			throw new QueuePluginException("user.exist:"+username);
		}
		User user = new User();
		user.setUsername(username);
		user.setGroup(group);
		user.setRole(Role.KIOSK);
		user.setData(jsonTemplate);
		try {
			user.setPasswordSalt(HashUtils.getSaltString());
			user.setPasswordHash(HashUtils.getHashString(password, user.getPasswordSalt()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		em.persist(user);
		QLog.info("end");
		QLog.info("Kiosk #0 created",username);
	}
	public void createSecondMonitor(String username, String password,Group group) throws QueuePluginException {
		QLog.info("started");
		if(em.find(MonitorUnit.class,username)!=null){
			throw new QueuePluginException("user.exist:"+username);
		}
		MonitorUnit mu1 = new MonitorUnit();
		mu1.setUsername(username);
		mu1.setGroup(group);
		
		try {
			mu1.setPasswordSalt(HashUtils.getSaltString());
			mu1.setPasswordHash(HashUtils.getHashString(password, mu1.getPasswordSalt()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		em.persist(mu1);
		QLog.info("end");
		QLog.info("Second Monitor #0 created",username);
	}
	
	public void createMonitor(String username, String password,Group group) throws QueuePluginException {
		QLog.info("started");
		if(em.find(User.class,username)!=null){
			throw new QueuePluginException("user.exist:"+username);
		}
		User monitor = new User();
		monitor.setRole(Role.MONITOR);
		monitor.setGroup(group);
		monitor.setUsername(username);
		try {
			monitor.setPasswordSalt(HashUtils.getSaltString());
			monitor.setPasswordHash(HashUtils.getHashString(password, monitor.getPasswordSalt()));
		} catch (Exception e) {
			
		}
		em.persist(monitor);
		QLog.info("end");
		QLog.info("Monitor #0 created",username);
	}
	
	public void createLane(String laneName,String laneKK,String laneRU,String startRange,
			String endRange,String color,Group group,String priority) throws QueuePluginException{
		QLog.info("started");
		if(em.find(Lane.class,laneName)!=null){
			throw new QueuePluginException("lane.exist:"+laneName);
		}
		Lane lane = new Lane();
		lane.setPasswordSalt("lane");
		lane.setPasswordHash("lane");
		lane.setUsername(laneName);
		lane.setGroup(group);
		if(priority.length()>0){
			lane.setPriority(Integer.parseInt(priority));
		}
		lane.setRangeStart(startRange);
		lane.setRangeEnd(endRange);
		lane.setColor(color);
		em.persist(lane);
		
		Messages.set(Language.RU,String.format("lane.%s.name",laneName),laneRU);
		Messages.set(Language.KK,String.format("lane.%s.name",laneName),laneKK);
		QLog.info("end");
		QLog.info("Lane #0 created",laneName);
	}
	
	public void createWeekend(String date, String time, String groupName) throws QueuePluginException{
		QLog.info("started");
		Group group = em.find(Group.class,groupName);
		Weekend weekend = new Weekend();
		weekend.setGroup(group);
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.parseInt(date.substring(6, 10)), Integer.parseInt(date.substring(3, 5))-1, Integer.parseInt(date.substring(0, 2)), Integer.parseInt(time.substring(0, 2)), Integer.parseInt(time.substring(3, 5)), 0);

		weekend.setStart(new Date(cal.getTime().getTime()));
		em.persist(weekend);
		QLog.info("Weekend #0 created", date);
		QLog.info("end");
	}
	
	public Weekend getLastItem(){
		Session session = em.unwrap(Session.class);
		
		Criteria c = session.createCriteria(Weekend.class);
		c.addOrder(Order.desc("id"));
		c.setMaxResults(1);
		return (Weekend)c.uniqueResult();
	}
	
	public void createWeekendDays(String days, String groupName){
		try{
			Weekend w = (Weekend)em.createQuery("SELECT w FROM Weekend w WHERE w.group.id =:group " +
				" AND w.start = null")
				.setParameter("group", groupName)
				.setMaxResults(1)
				.getResultList()
				.get(0);
			w.setWeekends(days);
			
		}catch(Exception e){
			Group group = em.find(Group.class,groupName);
			Weekend weekend = new Weekend();
			weekend.setGroup(group);
			weekend.setWeekends(days);
			
			em.persist(weekend);
		}
		// *************************************************
	}
	
	public void deleteWeekend(String id){
		Weekend w = em.find(Weekend.class, Long.parseLong(id));
	    em.remove(w);
	}
	
	public void createGroup(String groupName, String parentGroupName,Map<String, String> props) throws QueuePluginException {
		if(em.find(Group.class,groupName)!=null){
			throw new QueuePluginException("group.exist:"+groupName);
		}
		Group parent = em.find(Group.class,parentGroupName);
		
		Properties properties = new Properties("GROUP_"+groupName);
		properties.setEntries(props);
		Group group = new Group(groupName);
		group.setProperties(properties);
		if(parent!=null){
			group.setParent(parent);	
		}
		em.persist(group);
		QLog.info("Created new group #0",group);
	}

	public void disableUser(String username) throws QueuePluginException{
		QLog.info(username);
		User user = (User) em.find(User.class, username);
		QLog.info(user.toString());
		if(user != null){
			user.setStatus(Status.DISABLED);
			user.setModified(new Date());
			QLog.info("#0 disabled",username);	
		}
		
	}

	/**
	 * Update templates if they exist or create them if do not exist
	 * */
//	public void updateTemplates(List<Template> templates) throws QueuePluginException {
//		List<Template> dtTemplates = em.createQuery("from" +
//				"	 Template t" +
//				" where" +
//				"	 t.group=:group")
//				.setParameter("group", templates.get(0).getGroup())
//				.getResultList();
//		Map<String,Template> templateMap = new HashMap<String, Template>();
//		for(Template template:dtTemplates){
//			templateMap.put(template.toString(),template);
//		}
//		for(Template template:templates){
//			if(templateMap.containsKey(template.toString())){
//				Template t = templateMap.get(template.toString());
//				t.setData(template.getData());
//				QLog.info("#0 updated",template);
//			}else{
//				em.persist(template);
//				QLog.info("#0 created",template);
//			}
//		}
//	}

	public void enableUser(String username) {
		User user = (User) em.find(User.class, username);
		user.setStatus(Status.ENABLED);
		user.setModified(new Date());
		QLog.info("#0 enabled",username);
	}

	public void editUnit(String username, String password, MonitorUnit monitorUnit,
			String firstname,String lastname, Group group, Set<Lane> lanes, String window,
			String workStart,String workFinish) throws QueuePluginException {
		Unit unit = em.find(Unit.class, username);
		if(unit==null){
			throw new QueuePluginException("admin.edit.unit.notfound:"+username);
		}
		Unit.beforeUpdate(unit);
		if(password!=null && !password.trim().isEmpty()){
			try {
				unit.setPasswordHash(HashUtils.getHashString(password,unit.getPasswordSalt()));
			}catch (Exception e) {
				e.printStackTrace();
			}	
		}
		unit.setWindow(window);
		unit.setFirstName(firstname);
		unit.setMonitorUnit(monitorUnit);
		unit.setWorkStart(workStart);
		unit.setWorkFinish(workFinish);
		unit.setLanes(lanes);
		unit.setModified(new Date());
		unit.setLastName(lastname);
		unit.setGroup(group);
		QLog.info("Unit #0 edited",unit);
	}

	public void editDashboard(String username, String password, Group group,Set<Lane> lanes) throws QueuePluginException {
		Dashboard dash = em.find(Dashboard.class, username);
		if(dash==null){
			throw new QueuePluginException("admin.edit.dashboard.notfound:"+username);
		}
		Dashboard.beforeUpdate(dash);
		if(password!=null && !password.trim().isEmpty()){
			try {
				dash.setPasswordHash(HashUtils.getHashString(password,dash.getPasswordSalt()));
			}catch (Exception e) {
				e.printStackTrace();
			}	
		}
		dash.setDashboardLanes(lanes);
		dash.setModified(new Date());
		dash.setGroup(group);
		QLog.info("Dashboard #0 edited",dash);
	}

	public void editLane(String username, String startRange, String endRange,String color, Group group,
			String kk, String ru, String en, String priority) throws QueuePluginException {
		Lane lane = em.find(Lane.class,username);
		if(lane==null){
			throw new QueuePluginException("admin.edit.lane.notfound");
		}
		lane.setRangeStart(startRange);
		lane.setRangeEnd(endRange);
		lane.setColor(color);
		lane.setModified(new Date());
		lane.setGroup(group);
		lane.setPriority(Integer.parseInt(priority));
		Messages.set(Language.RU, String.format("lane.%s.name",username),ru);
		Messages.set(Language.KK, String.format("lane.%s.name",username),kk);
		Messages.set(Language.EN, String.format("lane.%s.name",username),en);
		QLog.info("Lane #0 edited",lane);
	}

	public void editKiosk(String username, String password, Group group,Role role,String jsonTemplate) throws QueuePluginException {
		User user = em.find(User.class,username);
		if(user==null){
			throw new QueuePluginException("admin.edit.user.notfound");
		}
		user.setModified(new Date());
		user.setGroup(group);
		user.setRole(role);
		user.setData(jsonTemplate);
		
		if(password!=null && !password.trim().isEmpty()){
			try {
				user.setPasswordHash(HashUtils.getHashString(password,user.getPasswordSalt()));
			}catch (Exception e) {
				e.printStackTrace();
			}	
		}
		QLog.info("User #0 edited",user);
	}
	
	public void editMonitor(String username, String password, Group group) throws QueuePluginException {
		User user = em.find(User.class,username);
		if(user==null){
			throw new QueuePluginException("admin.edit.user.notfound");
		}
		
		if(password!=null && !password.trim().isEmpty()){
			user.setModified(new Date());
			try {
				user.setPasswordHash(HashUtils.getHashString(password,user.getPasswordSalt()));
			}catch (Exception e) {
				e.printStackTrace();
			}	
		}
		QLog.info("User #0 edited",user);
	}
	public void editSecondMonitor(String username, String password,Group group) throws QueuePluginException {
		MonitorUnit secondMonitor = em.find(MonitorUnit.class, username);
		if(secondMonitor == null){
			throw new QueuePluginException("admin.edit.user.notfound");
		}
		
		if(password!=null && !password.trim().isEmpty()){
			secondMonitor.setModified(new Date());
			try {
				secondMonitor.setPasswordHash(HashUtils.getHashString(password,secondMonitor.getPasswordSalt()));
			}catch (Exception e) {
				e.printStackTrace();
			}	
		}
		
		QLog.info("Second Monitor #0 edited",username);
	}
}