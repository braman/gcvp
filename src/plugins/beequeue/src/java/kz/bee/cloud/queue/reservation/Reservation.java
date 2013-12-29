package kz.bee.cloud.queue.reservation;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import kz.bee.cloud.queue.model.Group;
import kz.bee.cloud.queue.model.Lane;
import kz.bee.cloud.queue.model.Ticket;
import kz.bee.cloud.queue.model.Token;
import kz.bee.util.QLog;
import kz.bee.util.QueuePluginException;

/**
 * Бронь
 */
@Entity
@Table(name = "R_RESERVATION")
public class Reservation {

	private Long id;
	private Group group;
	private Date date;
	private Date created;
	private Set<Lane> lanes;
	private String personalId;
	private Status status = Status.OPEN;
	private Ticket ticket;
	//type WEB,SMS,OUTSIDE-KIOSK 

	public enum Status {
		OPEN, CLOSED, CANCELLED
	};

	@Id
	@GeneratedValue
	@Column(name = "ID_")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "GROUP_")
	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	@Column(name = "DATE_")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name = "PERSONALID_")
	public String getPersonalId() {
		return personalId;
	}
	
	public void setPersonalId(String personalId) {
		this.personalId = personalId;
	}
	
	@Column(name="CREATED_")
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@ManyToMany
	@JoinTable(name = "R_RESERVATION_LANES", joinColumns = @JoinColumn(name = "RESERVATION_"), inverseJoinColumns = @JoinColumn(name = "LANE_"))
	public Set<Lane> getLanes() {
		return lanes;
	}

	public void setLanes(Set<Lane> lanes) {
		this.lanes = lanes;
	}

	@Column(name = "STATUS_")
	@Enumerated(EnumType.STRING)
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "TICKET_")
	public Ticket getTicket() {
		return ticket;
	}

	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Reservation other = (Reservation) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Reservation(id: ")
			  .append(id)
			  .append(", group: ")
			  .append(group)
			  .append(", date: ")
			  .append(date)
			  .append(", laneTypes: ")
			  .append(lanes)
			  .append(", personalId: ")
			  .append(personalId)
			  .append(", status: ")
			  .append(status)
			  .append(", ticket: ")
			  .append(ticket)
			  .append(")");
		return buffer.toString();
	}

	public static Reservation makeReservation(String groupName, String client,String time, String date, String lanes, EntityManager em) throws QueuePluginException{
		String timeArr[] = time.split(":");
		if(timeArr.length!=2){
			throw new ReservationException(100,"Wrong time format");
		}
		Group group = em.find(Group.class,groupName);
		if(group==null){
			throw new ReservationException(101,"Group not found");
		}
		
		try {
			Date reservationDate = extractDate(date, timeArr);
			StringTokenizer tokenizer = new StringTokenizer(lanes,",");
			List<String> laneNames = new ArrayList<String>();
			while(tokenizer.hasMoreTokens()){
				laneNames.add(tokenizer.nextToken());
			}
			Set<Lane> laneSet = new HashSet<Lane>(em.createQuery("" +
				" from" +
				" 	Lane l" +
				" where" +
				" 	l.group=:group" +
				" 	and l.username in (:names)")
				.setParameter("group", group)
				.setParameter("names", laneNames)
				.getResultList());
			String clientId = client;
			if(clientId==null){
				String ms = String.valueOf(System.currentTimeMillis());
				clientId = "b".concat(ms.substring(2));
			}else{
				
				List<Reservation> reservations = em.createQuery("from Reservation r" +
					" where" +
					" 	r.group=:group" +
					" 	and r.status='OPEN'" +
					"	and r.personalId=:clientId" +
					"	and r.date=:date")
					.setParameter("group",group)
					.setParameter("clientId",clientId)
					.setParameter("date",reservationDate)
					.setMaxResults(1)
					.getResultList();
				if(!reservations.isEmpty()){
					throw new ReservationException(102,"Reservation already exists");
				}
			}
			//Sultan
			List<Reservation> oldReservations = em.createQuery("from Reservation r" +
					" where" +
					" 	r.group=:group" +
					" 	and r.status='OPEN'" +
					"	and r.personalId=:clientId")
					.setParameter("group",group)
					.setParameter("clientId",clientId)
					.getResultList();
			System.out.println(group+" "+clientId+" 11111111111111111111111111111111111111111111111111");
			System.out.println(oldReservations.size()+" size");
			for(int i=0;i<oldReservations.size();i++){
				System.out.println(oldReservations.get(i).getPersonalId());
			}
//			Reservation oldReservation = em.find(Reservation.class, clientId);
//			em.remove(oldReservation);
//			System.out.println(oldReservation.getPersonalId()+"reservation personal id");
			
			Reservation reservation = new Reservation();
			reservation.setLanes(laneSet);
			reservation.setCreated(new Date());
			reservation.setDate(reservationDate);
			reservation.setGroup(group);
			reservation.setPersonalId(clientId);
			em.persist(reservation);
			
			QLog.info("Persisting Reservation " + reservation.toString());
			return reservation;
		} catch (ParseException e) {
			e.printStackTrace();
			throw new ReservationException(103,"Date parse error");
		}
	}
	
	private static Date extractDate(String date,String timeArr[]) throws ParseException,QueuePluginException{
		SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy");
		Date reservationDate = fmt.parse(date);
		
		Calendar cld = Calendar.getInstance();
		cld.setTime(reservationDate);
		int hours = Integer.parseInt(timeArr[0]);
		if(hours>=24){
			throw new ReservationException(104,"Wrong hour provided");
		}
		cld.set(Calendar.HOUR,hours);
		int minutes = Integer.parseInt(timeArr[1]);
		if(minutes>=60){
			throw new ReservationException(105,"Wrong minute provided");
		}
		if(minutes<30){
			minutes = 0;
		}
		if(minutes>30){
			minutes = 30;
		}
		cld.set(Calendar.MINUTE,minutes);
		return cld.getTime();
	}

	public static void processTicket(Ticket ticket, EntityManager em) {
		List<Reservation> reservations = em.createQuery("from Reservation r" +
				" where " +
				" 	r.group = :group" +
				" 	and to_char(r.date,'dd.MM.yyyy') = to_char(current_date,'dd.MM.yyyy')" +
				" 	and r.personalId = :personalId" +
				" 	and r.status = 'OPEN'" +
				" order by r.date asc")
				.setParameter("group", ticket.getGroup())
				.setParameter("personalId", ticket.getPersonalId())
				.getResultList();
		
		if(!reservations.isEmpty()){
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			int minutes = calendar.get(Calendar.MINUTE);
			if (minutes >= 30) {
				calendar.set(Calendar.MINUTE, 30);
			} else {
				calendar.set(Calendar.MINUTE, 0);
			}
			
			for(Reservation r:reservations){
				Calendar cld = Calendar.getInstance();
				cld.setTime(r.getDate());
				if(calendar.get(Calendar.HOUR_OF_DAY)<=cld.get(Calendar.HOUR_OF_DAY) && calendar.get(Calendar.MINUTE)<=cld.get(Calendar.MINUTE)){
					r.setTicket(ticket);
					r.setStatus(Status.CLOSED);
					break;
				}else{
					calendar.add(Calendar.MINUTE,30);
				}
			}
			
		}
	}

	public static List<Reservation> fetchReservations(String groupName,EntityManager em) {
/*		return em.createQuery("from Reservation r" +
			" where" +
			" 	r.group.id=:groupName" +
			" 	and to_char(r.date,'dd.MM.yyyy') = to_char(current_date(),'dd.MM.yyyy')" +
			" 	and r.status='OPEN'" +
			" order by r.date asc")
			.setParameter("groupName",groupName)
			.getResultList();
*/	
		return em.createQuery(
				"from Reservation r " +
				"where r.group.id=:groupName " +
				"  and year(r.date)=year(CURRENT_DATE()) " +
				"  and month(r.date)=month(CURRENT_DATE()) " +
				"  and day(r.date)=day(CURRENT_DATE()) " +
				"  and r.status='OPEN' " +
				"order by r.date asc")
				.setParameter("groupName", groupName)
				.getResultList();
	}
	
	public static Date reservedAtDay(String personalId, EntityManager em, String groupName, String date) {
		try{
			Date reservedDate = new Date();
			
			reservedDate.setDate(Integer.parseInt(date.substring(0, 2)));
			reservedDate.setMonth(Integer.parseInt(date.substring(3, 5)));
			reservedDate.setYear(Integer.parseInt(date.substring(6)));
			
			List<Date> list = em.createQuery(
					"select r.date " +
					"from Reservation r " +
					"where r.personalId=:personalId " +
					"  and r.group.id=:groupName " +
					"  and year(r.date)=:year " +
					"  and month(r.date)=:month " +
					"  and day(r.date)=:day")
					.setParameter("personalId", personalId)
					.setParameter("groupName", groupName)
					.setParameter("year", reservedDate.getYear())
					.setParameter("month", reservedDate.getMonth())
					.setParameter("day", reservedDate.getDate())
					.setMaxResults(1)
					.getResultList();

			if(list.size() > 0) {
				Date newDate = list.get(0);
				
				return newDate;
			}
		}
		catch(Exception e){
			System.err.println("Exception: " + e);
		}
		
		return null;
	}
	
	//checking if user reserved ticket
	public static boolean checkIfReserved(String personalId, EntityManager em, String groupName, String date, String time) {
		String timeArr[] = time.split(":");
		try{
			Date reservationDate = extractDate(date, timeArr);
			
			List<Reservation> list = em.createQuery(
					"select r " +
					"from Reservation r " +
					"where r.personalId=:personalId " +
					"  and r.group.id=:groupName " +
					"  and r.date=:date ")
					.setParameter("personalId", personalId)
					.setParameter("groupName", groupName)
					.setParameter("date", reservationDate)
					.setMaxResults(1)
					.getResultList();

			if(list.size() > 0)
				return true;
		}
		catch(Exception e){
			System.err.println("Exception: " + e);
		}
		
		return false;
	}
	
	public static boolean isTicketReserved(String groupName, EntityManager em, long ticketId) { // Date date
		List<Reservation> list = em.createQuery( 
									"select r " +
									"from Reservation r join r.ticket t " +
									"where r.group.id=:groupName " +
									"  and year(r.date)=year(CURRENT_DATE()) " +
									"  and month(r.date)=month(CURRENT_DATE()) " +
									"  and day(r.date)=day(CURRENT_DATE()) " +
									" and r.ticket.id = :ticketId " +
									"order by r.date asc")
									.setParameter("groupName", groupName)
									.setParameter("ticketId", ticketId)
//									.setParameter("date", date)
									.setMaxResults(1)
									.getResultList();
		
		if (list.size() > 0)
			return true;
		
		return false;
	}
	
	public static boolean checkFiveReservedInTime(EntityManager em, String groupName, String date, String time) {
//		String timeArr[] = time.split(":");
		
		System.out.println("!!!!!!!!I have Entered method");
		/*
		try{
			Date reservedDate = extractDate(date, timeArr);
		
			List<Date> list = em.createQuery(
				"select r.date " + 
				"from Reservation r " +
				"where r.group.id=:groupName " +
				"  and year(r.date)=:year" +
				"  and month(r.date)=:month" +
				"  and day(r.date)=:day")
				.setParameter("groupName", groupName)
				.setParameter("year", reservedDate.getYear())
				.setParameter("month", reservedDate.getMonth())
				.setParameter("day", reservedDate.getDate())
				.setMaxResults(1)
				.getResultList();

			System.out.println("!!!!!!!!I have Finished Query: "+list.size());
			System.out.println(reservedDate);
			
			if(list.size()>=5) {
				int count = 0;
				for(int i=0;i<list.size();i++) {
					System.out.println("Counting....");
					if(list.get(i).getHours()==reservedDate.getHours() && 
							list.get(i).getMinutes()==reservedDate.getMinutes()) {
						++count;
						System.out.println("Counting "+count);
						if(count>=5)
							System.out.println("COUNT 5+++++++++++++++++++++++++++++++++++++++++++++");
							return true;
					}
				}
				
				return false; 
			}
		}
		catch(Exception e){
			System.err.println("Exception: " + e);
		} 
		
		
		*/
		
		
		
		
		
		
		
		
		
		
		try{
			Date reservedDate = new Date();
			reservedDate.setDate(Integer.parseInt(date.substring(0, 2)));
			reservedDate.setMonth(Integer.parseInt(date.substring(3, 5)));
			reservedDate.setYear(Integer.parseInt(date.substring(6)));
			reservedDate.setHours(Integer.parseInt(time.substring(0,2)));
			reservedDate.setMinutes(Integer.parseInt(time.substring(3)));
			
			System.out.println(reservedDate);
			
			List<Date> list = em.createQuery(
					"select r.date " +
					"from Reservation r " +
					"  where r.group.id=:groupName " +
					"  and year(r.date)=:year " +
					"  and month(r.date)=:month " +
					"  and day(r.date)=:day" +
					"  and hour(r.date)=:hour" + 
					"  and minute(r.date)=:minute")
					.setParameter("groupName", groupName)
					.setParameter("year", reservedDate.getYear())
					.setParameter("month", reservedDate.getMonth())
					.setParameter("day", reservedDate.getDate())
					.setParameter("hour", reservedDate.getHours())
					.setParameter("minute", reservedDate.getMinutes())
					.getResultList();

			if(list.size() >= 5) {
				Date newDate = list.get(0);
				return true;
			}
			return false;
		}
		catch(Exception e){
			System.err.println("Exception: " + e);
		}
		
		return false;
	}
}
