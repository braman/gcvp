package kz.bee.cloud.queue;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import javax.persistence.EntityManager;

import kz.bee.cloud.queue.model.AnketaAnswers;
import kz.bee.cloud.queue.model.AnketaQuestions;
import kz.bee.cloud.queue.model.Dashboard;
import kz.bee.cloud.queue.model.Group;
import kz.bee.cloud.queue.model.Lane;
import kz.bee.cloud.queue.model.MonitorUnit;
import kz.bee.cloud.queue.model.Number;
import kz.bee.cloud.queue.model.Ticket;
import kz.bee.cloud.queue.model.Token;
import kz.bee.cloud.queue.model.Token.Status;
import kz.bee.cloud.queue.model.Unit;
import kz.bee.cloud.queue.model.User;
import kz.bee.cloud.queue.model.User.Language;
import kz.bee.cloud.queue.model.User.Role;
import kz.bee.cloud.queue.model.Weekend;
import kz.bee.cloud.queue.reservation.Reservation;
import kz.bee.cloud.queue.reservation.ReservationException;
import kz.bee.hibernate.connection.DBManager;
import kz.bee.util.Constants;
import kz.bee.util.HashUtils;
import kz.bee.util.Messages;
import kz.bee.util.QLog;
import kz.bee.util.QueuePluginException;
import kz.bee.util.TimeUtils;

import org.dom4j.Element;
import org.hibernate.Session;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.pubsub.LeafNode;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Message.Type;

import com.cenqua.shaj.log.Log;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Manages communication with clients and generates responses Serves as an
 * object wrapper which produces JSON results
 * */
@SuppressWarnings("unchecked")
public class Messenger {

	private static Messenger instance;
	public ObjectMapper mapper;

	private Messenger() {
		mapper = new ObjectMapper();
	}

	public static void init() {
		// We may only have one instance of the messenger running on the JVM
		if (instance != null) {
			throw new IllegalStateException("A messenger is already running");
		}
		instance = new Messenger();
	}

	public static Messenger getInstance() {
		return instance;
	}

	/**
	 * Sends message to given JID from queue
	 * */
	public void sendMessage(String text, JID jid) {
		QLog.info("start");
		Message message = new Message();
		message.setType(Type.chat);
		message.setFrom(Constants.QUEUE);
		message.setTo(jid);
		message.setBody(text);
		XMPPServer.getInstance().getMessageRouter().route(message);
		QLog.info("end");
	}

	public void sendMessage(String text, String jid) {
		QLog.info("start");
		sendMessage(text, new JID(jid));
		QLog.info("end");
	}

	public boolean checkTwoHours(String date, String time) {
		Date now = new Date();

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, Integer.parseInt(date.substring(6)));
		cal.set(Calendar.MONTH, Integer.parseInt(date.substring(3, 5)) - 1);
		cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(0, 2)));
		cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, 2)));
		cal.set(Calendar.MINUTE, Integer.parseInt(time.substring(3)));
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		Date registrationDate = cal.getTime();

		// if(now.after(registrationDate)){
		// return true;
		// }
		// else if((registrationDate.get Time()-now.getTime())/(1000*60*60)<2) {
		// return true;
		// }

		return false;
	}

	public String doReservation(String groupName, String client, String time,
			String date, String lanes) {
		String response = "";
		EntityManager em = DBManager.newEm();
		em.getTransaction().begin();
		ObjectNode resultNode = mapper.createObjectNode();
		boolean checkIfReserved = Reservation.checkIfReserved(client, em,
				groupName, date, time);
		Date reservedAtDay = null;
		boolean checkTwoHours = false;
		boolean checkFiveReservedInTime = false;

		if (!checkIfReserved) {
			reservedAtDay = Reservation.reservedAtDay(client, em, groupName,
					date);
			if (reservedAtDay == null)
				checkTwoHours = checkTwoHours(date, time);
			if (!checkTwoHours) {
				checkFiveReservedInTime = Reservation.checkFiveReservedInTime(
						em, groupName, date, time);
			}
		}
		QLog.info("Checked for reserved ticket: " + checkIfReserved);
		if (checkIfReserved) {
			Date now = new Date();
			int minutes = Integer.parseInt(time.substring(3));
			now.setHours(minutes == 30 ? Integer.parseInt(time.substring(0, 2)) + 1
					: Integer.parseInt(time.substring(0, 2)));
			String hour = Integer.parseInt(time.substring(0, 2)) + "";
			String hour_ = now.getHours() + "";
			String minute_ = (time.substring(3).equals("00") ? "30" : "00");
			String minute = (minute_.equals("00") ? "30" : "00");
			resultNode
					.put("busy", "Вы уже забронировали билет на это же время "
							+ date + " " + hour + ":" + minute + "-" + hour_
							+ ":" + minute_);
		} else if (reservedAtDay != null) {
			resultNode.put(
					"busy",
					"Вы уже забронировали билет на время "
							+ date
							+ " "
							+ reservedAtDay.getHours()
							+ ":"
							+ (reservedAtDay.getMinutes() == 0 ? "00" : "30")
							+ "-"
							+ (reservedAtDay.getMinutes() == 30 ? reservedAtDay
									.getHours() + 1 : reservedAtDay.getHours())
							+ ":"
							+ (reservedAtDay.getMinutes() == 30 ? "00" : "30"));
		} else if (checkTwoHours == true) {
			resultNode.put("busy",
					"Нельзя бронировать за 2 часа до регистрации");
		} else if (checkFiveReservedInTime) {
			resultNode
					.put("busy",
							"На это время уже забронировано 5 человек, вы не можете быть забронированы.");
		} else {
			try {
				Reservation reservation = Reservation.makeReservation(
						groupName, client, time, date, lanes, em);
				resultNode.put("id", reservation.getPersonalId());

				ArrayNode lanesNode = mapper.createArrayNode();
				for (Lane lane : reservation.getLanes()) {
					lanesNode.add(lane.getUsername());
				}

				ObjectNode reservationNode = mapper.createObjectNode();
				reservationNode.put("id", reservation.getPersonalId());
				reservationNode.put("date", new SimpleDateFormat("dd.MM HH:mm")
						.format(reservation.getDate()));
				reservationNode.put("lanes", lanesNode);

				ObjectNode rootNode = mapper.createObjectNode();
				rootNode.put("reservation", reservationNode);
				rootNode.put("pubsub", true);

				publish(reservation.getGroup().getName() + ".kiosk.pubsub",
						rootNode);
				em.getTransaction().commit();
			} catch (ReservationException e) {
				resultNode.put("exception", e.getMessage());
				resultNode.put("code", e.getId());
				em.getTransaction().rollback();
			} catch (Exception e) {
				resultNode.put("exception", "unexpected");
				e.printStackTrace();
				em.getTransaction().rollback();
			}
		}
		em.close();
		try {
			response = mapper.writeValueAsString(resultNode);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

	public ArrayNode getLanes(String groupName) {
		QLog.info("started");
		EntityManager em = DBManager.newEm();
		em.getTransaction().begin();
		ArrayNode arrayNode = mapper.createArrayNode();
		try {
			List<Lane> lanes = em
					.createQuery(
							"from Lane l " + "where l.group.id=:groupName "
									+ "and l.status='ENABLED'")
					.setParameter("groupName", groupName).getResultList();
			String kk = "";
			String en = "";
			String ru = "";
			for (Lane lane : lanes) {
				kk = Messages.get("lane.#0.name_KK", lane.getUsername());
				ru = Messages.get("lane.#0.name_RU", lane.getUsername());
				en = Messages.get("lane.#0.name_EN", lane.getUsername());
				if (kk.equals("lane." + lane.getUsername() + ".name_KK")) {
					kk = "";
				}
				if (ru.equals("lane." + lane.getUsername() + ".name_RU")) {
					ru = "";
				}
				if (en.equals("lane." + lane.getUsername() + ".name_EN")) {
					en = "";
				}

				arrayNode.add(mapper.createObjectNode().put("kk", kk)
						.put("ru", ru).put("en", en)
						.put("id", lane.getUsername()));
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			em.getTransaction().rollback();
		}
		em.close();
		QLog.info("end");
		return arrayNode;
	}

	public ArrayNode getWeekends(String group) {
		EntityManager em = DBManager.newEm();
		em.getTransaction().begin();
		ArrayNode arrayNode = mapper.createArrayNode();
		try {
			List<Weekend> weekends = em
					.createQuery(
							"SELECT w FROM Weekend w WHERE w.group.id =:group AND w.start != null")
					.setParameter("group", group).getResultList();

			for (Weekend weekend : weekends) {
				arrayNode.add(mapper.createObjectNode().put("start",
						weekend.getStart().toString()));
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			em.getTransaction().rollback();
		}
		em.close();

		return arrayNode;
	}

	public ArrayNode getNotWorkingWeekends(String group) {
		EntityManager em = DBManager.newEm();
		em.getTransaction().begin();
		ArrayNode arrayNode = mapper.createArrayNode();
		try {
			List<Weekend> weekends = em
					.createQuery(
							"SELECT w FROM Weekend w WHERE w.group.id =:group AND w.start IS NULL")
					.setParameter("group", group).getResultList();

			for (Weekend weekend : weekends) {
				arrayNode.add(mapper.createObjectNode().put("weekends",
						weekend.getWeekends()));
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			em.getTransaction().rollback();
		}
		em.close();

		return arrayNode;
	}

	public ArrayNode getNotWorkingHolidays(String groupName) {
		Date date = new Date();

		EntityManager em = DBManager.newEm();
		em.getTransaction().begin();
		ArrayNode arrayNode = mapper.createArrayNode();
		try {
			List<Weekend> holidays = em
					.createQuery(
							"SELECT w FROM Weekend w WHERE w.group.id =:group AND "
									+ " w.weekends IS NULL AND "
									+ " hour(w.start) =:hour AND "
									+ " minute(w.start) =:minute AND "
									+ " w.start >= :date")
					.setParameter("group", groupName).setParameter("hour", 8)
					.setParameter("minute", 30).setParameter("date", date)
					.getResultList();

			for (Weekend holiday : holidays) {
				arrayNode.add(mapper.createObjectNode()
				// .put("holidays_date", holiday.getStart().getDate())
				// .put("holidays_month", holiday.getStart().getMonth())
				// .put("holidays_year", holiday.getStart().getYear()));
						.put("holidays", holiday.getStart().toString()));
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			em.getTransaction().rollback();
		}
		em.close();

		return arrayNode;
	}

	public ArrayNode getGroups() {
		QLog.info("started");
		EntityManager em = DBManager.newEm();
		em.getTransaction().begin();
		ArrayNode arrayNode = mapper.createArrayNode();
		try {
			List<Group> groups = em.createQuery("select g from Group g")
					.getResultList();

			for (Group group : groups)
				if (group.getProperties().get("address") != null) {
					arrayNode.add(mapper
							.createObjectNode()
							.put("address",
									group.getProperties().get("address"))
							.put("name", group.getName()));
				}
			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			em.getTransaction().rollback();
		}
		em.close();
		QLog.info("end");
		return arrayNode;
	}

	private void seMonitorTicket(Token token) throws JsonGenerationException,
			JsonMappingException, IOException {
		QLog.info("started");
		ObjectNode resultNode = mapper.createObjectNode();

		ObjectNode ticketNode = mapper.createObjectNode();
		ticketNode.put("ticketId", token.getTicket().getId());
		ticketNode.put("tokenId", token.getId());
		ticketNode.put("code", token.getTicket().getCode());
		resultNode.put("ticket", ticketNode);

		String message = mapper.writeValueAsString(resultNode);

		Unit u = (Unit) token.getTarget();
		System.out.println("---------------------------------------------");
		System.out.println(u.getMonitorUnit());
		String jid = u.getMonitorUnit().getUsername() + "@cq.b2e.kz";
		// String jid1 = u.getMonitorUnitDemo().getUsername()+"@cq.b2e.kz";

		sendMessage(message, jid);
		QLog.info("end");
		// sendMessage(message, jid1);
	}

	private void seMonitorRefresh(Token token) throws JsonGenerationException,
			JsonMappingException, IOException {
		QLog.info("started");
		ObjectNode resultNode = mapper.createObjectNode();
		resultNode.put("refresh", true);
		String message = mapper.writeValueAsString(resultNode);

		Unit u = (Unit) token.getTarget();
		System.out.println("---------------------------------------------");
		System.out.println(u.getMonitorUnit());
		String jid = u.getMonitorUnit().getUsername() + "@cq.b2e.kz";
		// String jid1 = u.getMonitorUnitDemo().getUsername()+"@cq.b2e.kz";

		sendMessage(message, jid);
		QLog.info("end");
		// sendMessage(message, jid1);
	}

	private void seMonitorAdvertisement(Token token)
			throws JsonGenerationException, JsonMappingException, IOException {
		ObjectNode resultNode = mapper.createObjectNode();
		resultNode.put("advertisement", true);
		String message = mapper.writeValueAsString(resultNode);

		Unit u = (Unit) token.getTarget();
		String jid = u.getMonitorUnit().getUsername() + "@cq.b2e.kz";
		// String jid1 = u.getMonitorUnitDemo().getUsername()+"@cq.b2e.kz";

		sendMessage(message, jid);
		// sendMessage(message, jid1);
	}
	
	private void sendMonitorNumbers(Token token, ObjectNode tokenNode, String sender)
			throws JsonGenerationException, JsonMappingException, IOException {
		ObjectNode resultNode = mapper.createObjectNode();
		resultNode.put("numbers", true);

		Unit u = (Unit) token.getTarget();
		String jid = u.getMonitorUnit().getUsername() + "@cq.b2e.kz";
		System.out.println("JID = " + jid);
		// String jid1 = u.getMonitorUnitDemo().getUsername()+"@cq.b2e.kz";
		System.out.println("Sending to Second Monitor");
		
		resultNode.put("content", tokenNode);
		resultNode.put("sender", sender + "@cq.b2e.kz");
		String message = mapper.writeValueAsString(resultNode);
		
		sendMessage(message, jid);
	}

	private void sendChosenNumber(ObjectNode tokenNode, String receiver)
			throws JsonGenerationException, JsonMappingException, IOException {
		ObjectNode resultNode = mapper.createObjectNode();
		resultNode.put("number", true);

		resultNode.put("content", tokenNode);
		String message = mapper.writeValueAsString(resultNode);

		String jid = receiver;
		System.out.println("JID = " + jid);
		sendMessage(message, jid);
	}
	
	private void seMonitorVideo(Token token) throws JsonGenerationException,
			JsonMappingException, IOException {
		ObjectNode resultNode = mapper.createObjectNode();
		resultNode.put("video", true);
		String message = mapper.writeValueAsString(resultNode);
		System.out
				.println("seMonitorVideo---------------------------------------------------");
		Unit u = (Unit) token.getTarget();
		String jid = u.getMonitorUnit().getUsername() + "@cq.b2e.kz";
		// String jid1 = u.getMonitorUnitDemo().getUsername()+"@cq.b2e.kz";

		sendMessage(message, jid);
		// sendMessage(message, jid1);
	}

	private void seMonitorBack(Unit u) throws JsonGenerationException,
			JsonMappingException, IOException {
		QLog.info("start");
		ObjectNode resultNode = mapper.createObjectNode();
		resultNode.put("back", true);
		String message = mapper.writeValueAsString(resultNode);
		System.out
				.println("seMonitorSmile---------------------------------------------------");
		// Unit u = (Unit) token.getTarget();
		String jid = u.getMonitorUnit().getUsername() + "@cq.b2e.kz";
		// String jid1 = u.getMonitorUnitDemo().getUsername()+"@cq.b2e.kz";

		sendMessage(message, jid);
		QLog.info("end");
		// sendMessage(message, jid1);
	}

	private void seMonitorSmile(Token token) throws JsonGenerationException,
			JsonMappingException, IOException {
		ObjectNode resultNode = mapper.createObjectNode();
		resultNode.put("smile", true);
		resultNode.put("lang", token.getTicket().getLang().toString());
		String message = mapper.writeValueAsString(resultNode);
		System.out
				.println("seMonitorSmile---------------------------------------------------");
		Unit u = (Unit) token.getTarget();
		System.out.println(token + " token");

		String jid = u.getMonitorUnit().getUsername() + "@cq.b2e.kz";
		// String jid1 = u.getMonitorUnitDemo().getUsername()+"@cq.b2e.kz";

		sendMessage(message, jid);
		// sendMessage(message, jid1);
	}

	public Message createResponse(Message incoming) {
		long start = System.currentTimeMillis();
		if (incoming == null || incoming.getFrom() == null) {
			QLog.error("createResponse called for null message or null sender");
			return null;
		}
		if (incoming.getFrom().toString().contains("pubsub")) {
			QLog.debug("Skipping message from pubsub!");
			return null;
		}
		String username = incoming.getFrom().getNode();
		System.out.println(username);
		String text = incoming.getBody();
		String jsonStr = text.replace("&quot;", "\"").replace("&gt;", ">")
				.replace("&lt;", "<").replace("&apos;", "'");
		QLog.info("Processing #0", jsonStr);
		String response = null;
		EntityManager em = DBManager.newEm();
		em.getTransaction().begin();
		try {
			Token token = null;
			QueueWrapper queue = new QueueWrapper(em);
			ObjectNode resultNode = mapper.createObjectNode();
			User user = queue.findUser(username);
			if (text.equalsIgnoreCase("call")) {
				token = queue.call((Unit) user);
			} else if (text.equalsIgnoreCase("anketa")) {
				token = queue.advertisement((Unit) user);
				System.out.println(token);
				if (token != null) {
					Unit u = (Unit) token.getTarget();
					if (u.getMonitorUnit() != null) {
						seMonitorAdvertisement(token);
					}
				}
			} else if (text.equalsIgnoreCase("numbers")) {
				System.out.println("User: " + user.getUsername());
				token = queue.advertisement((Unit) user);
				System.out.println(token);
				
				List<Number> numbers = Number.getNumbers(em, user);
				ObjectNode tokenNode = mapper.createObjectNode();
//				
//				ArrayNode arrayNodeId = mapper.createArrayNode();
				ArrayNode arrayNodeNumber = mapper.createArrayNode();
				
				for(int i = 0; i < numbers.size(); i++){
					arrayNodeNumber.add(numbers.get(i).getNumber());
				}
				
//				tokenNode.put("id", arrayNodeId);
				tokenNode.put("number", arrayNodeNumber);
				
				System.out.println(numbers.size());
				System.out.println("Token: " + token.getLaneName());
//				
				if (token != null) {
					System.out.println("Token is not NULL");
					Unit u = (Unit) token.getTarget();
					if (u.getMonitorUnit() != null) {
						System.out.println("Starting to send to Second MONITOR");
						sendMonitorNumbers(token, tokenNode, user.getUsername());
					}
				}
			} else if (text.equalsIgnoreCase("start")) {
				token = queue.start((Unit) user);
				if (token != null) {
					Unit u = (Unit) token.getTarget();
					if (u.getMonitorUnit() != null) {
						seMonitorVideo(token);
					}
				}
			} else if (text.equalsIgnoreCase("end")) {
				token = queue.end((Unit) user);
				if (token != null) {
					Unit u = (Unit) token.getTarget();
					if (u.getMonitorUnit() != null) {
						seMonitorSmile(token);
					}
				}
			} else if (text.equalsIgnoreCase("postpone")) {
				token = queue.postpone(user.getUsername());
				if (token != null) {
					Unit u = (Unit) token.getTarget();
					if (u.getMonitorUnit() != null) {
						seMonitorRefresh(token);
					}
				}
			} else if (text.startsWith("broadcast")) {
				Message msg = new Message();
				msg.setBody(text);
				msg.setFrom(Constants.QUEUE);
				XMPPServer.getInstance().getSessionManager().broadcast(msg);
				QLog.info("Broadcasting...");
			} else if (text.equalsIgnoreCase("init")) {
				ObjectNode initResult = init(user, queue, "");
				resultNode.putAll(initResult);
			} else if (text.equalsIgnoreCase("initLocalAdmin")) {
				ObjectNode initResult = init(user, queue, text);
				resultNode.putAll(initResult);
			} else if (text.equalsIgnoreCase("getUsername")) {
				Unit unit = (Unit) user;
				String firstName = unit.getFirstName() + " "
						+ unit.getLastName();
				System.out.println("First Name: " + firstName);
				resultNode.put("firstname", firstName);
			} else {
				ObjectNode rootNode = null;
				try {
					rootNode = mapper.readValue(jsonStr, ObjectNode.class);
				} catch (Exception e) {
					e.printStackTrace();
					throw new QueuePluginException("JSON read exception");
				}

				JsonNode actionNode = rootNode.get("action");
				String groupName = user.getGroup().getName();
				if (actionNode == null || actionNode.isMissingNode()) {
					resultNode.put("error", "'action' node not found");
				} else {
					String action = actionNode.textValue();
					if (action.equalsIgnoreCase("reserve")) {
						QLog.info("Reserve JSON:#0", text);
					} else if (action.equalsIgnoreCase("callByTicket")) {
						System.out.println(rootNode.get("ticketTokenId")
								.asLong());
						Token ticketToken = em.find(Token.class,
								rootNode.get("ticketTokenId").asLong());
						System.out.println(ticketToken);
						token = queue.callByToken((Unit) user, ticketToken);
					} else if (action.equalsIgnoreCase("admin")) {
						System.out.println("111111111111111111111111111111111");
						System.out.println(rootNode);
						System.out.println(user);
						System.out.println(em);
						String result = wrapAdminAction(rootNode, user, em);
						resultNode.put("result", result);
					} else if (action.equalsIgnoreCase("send_number")) {
						System.out.println("User: " + user.getUsername());
						
						String number = rootNode.get("number").textValue();
						System.out.println("Number: " + number);
						
						String receiver = rootNode.get("receiver").textValue();
						System.out.println("Receiver: " + receiver);
						
						ObjectNode tokenNode = mapper.createObjectNode();
						
						tokenNode.put("number", number);
						
						Number.deactivateNumber(em, number);
						
						sendChosenNumber(tokenNode, receiver);
						
					}
					else if (action.equalsIgnoreCase("parseNumbers")) {
						System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
						String numbers = rootNode.get("numbers").asText();
						System.out.println("Numbers: " + numbers);
						
						StringTokenizer st = new StringTokenizer(numbers);
				 
						while (st.hasMoreElements()) {
							String tokenStr = st.nextElement().toString();
							StringTokenizer st2 = new StringTokenizer(tokenStr, "\n");
							while (st2.hasMoreElements()) {
								try{
									Number number = new Number(user, st2.nextElement().toString(), false);
									em.persist(number);
								}
								catch(Exception e){
									e.printStackTrace();
								}
							}
						}
						
					} else if (action.equalsIgnoreCase("admin_holiday")) {
						new Admin(em).createWeekend(rootNode.get("date")
								.textValue(), rootNode.get("time").textValue(),
								rootNode.get("group_name").textValue());

						// DateFormat df = new
						// SimpleDateFormat("mm-dd-yyyy hh:mm:ss");
						// Date startDate =
						// df.parse(rootNode.get("date").textValue()+" "+rootNode.get("time").textValue()+":00");

						Calendar cal = Calendar.getInstance();
						cal.set(Integer.parseInt(rootNode.get("date")
								.textValue().substring(6, 10)),
								Integer.parseInt(rootNode.get("date")
										.textValue().substring(3, 5)) - 1,
								Integer.parseInt(rootNode.get("date")
										.textValue().substring(0, 2)),
								Integer.parseInt(rootNode.get("time")
										.textValue().substring(0, 2)),
								Integer.parseInt(rootNode.get("time")
										.textValue().substring(3, 5)), 0);

						// Weekend weekend =
						// (Weekend)queue.getEntityManager().createQuery("from Weekend w"
						// +
						// " WHERE" +
						// "	w.start = :date " +
						// "   AND w.group = :group")
						// .setParameter("date", new
						// Date(cal.getTime().getTime()))
						// .setParameter("group", Group.getGroup(em,
						// rootNode.get("group_name").textValue()))
						// .getResultList()
						// .get(0);
						//
						// resultNode.put("time_success",
						// weekend.getStart().toString());
						// resultNode.put("group_success",
						// weekend.getGroup().getName());
						// resultNode.put("delete_id",
						// weekend.getId().toString());
						resultNode.put("put_success", "succeeded");
					} else if (action.equalsIgnoreCase("get_last_item")) {
						Weekend lastWeekend = new Admin(em).getLastItem();

						System.out
								.println("------------------------------------------------------------------------------------");
						System.out.println(lastWeekend.getGroup().getName());
						System.out.println(lastWeekend.getId());
						System.out.println(lastWeekend.getStart());
						System.out
								.println("------------------------------------------------------------------------------------");
						resultNode.put("last_item_id", lastWeekend.getId());
						resultNode.put("last_item_name", lastWeekend.getGroup()
								.getName());
						resultNode.put("last_item_time", lastWeekend.getStart()
								.toString());
					} else if (action.equalsIgnoreCase("delete_holiday")) {
						new Admin(em).deleteWeekend(rootNode.get("id")
								.toString());
					} else if (action.equalsIgnoreCase("save_weekend")) {
						new Admin(em).createWeekendDays(rootNode.get("days")
								.textValue(), rootNode.get("group_name")
								.textValue());
						resultNode.put("saved_weekend", "success");
					} else if (action.equalsIgnoreCase("lanes")) {
						if (rootNode.has("group")) {
							groupName = rootNode.get("group").textValue();
						}
						resultNode
								.putAll(lanes2JSON(queue.getLanes(groupName)));
					} else if (action.equalsIgnoreCase("saveVideoPath")) {
						if (rootNode.has("videoPath") && rootNode.has("group")) {
							String videoPath = rootNode.get("videoPath")
									.textValue();
							groupName = rootNode.get("group").textValue();
							// videoPath
							Group g = queue.fetchGroup(groupName);
							// Group g =
							// em.find(Group.class,Long.parseLong(groupName));
							if (g != null) {
								g.setVideoPath(videoPath);
								em.persist(g);
							}
							resultNode.put("saved", true);
						} else {
							resultNode.put("saved", false);
						}
					} else if (action.equalsIgnoreCase("saveRunningLine")) {
						if (rootNode.has("runningLine")
								&& rootNode.has("group")) {
							String runningLine = rootNode.get("runningLine")
									.textValue();
							groupName = rootNode.get("group").textValue();
							// running line
							Group g = queue.fetchGroup(groupName);
							// Group g =
							// em.find(Group.class,Long.parseLong(groupName));
							if (g != null) {
								g.setRunningLine(runningLine);
								em.persist(g);
							}
							resultNode.put("saved", true);

							updateRunningLine(groupName, "runningLine",
									runningLine, em);
						} else {
							resultNode.put("saved", false);
						}
					} else if (action
							.equalsIgnoreCase("UpdateKioskPaperLength")) {
						groupName = rootNode.get("group").textValue();
						Group g = queue.fetchGroup(groupName);

						if (rootNode.has("length")
								&& rootNode.has("totalLength")) {
							String length = rootNode.get("length").textValue();
							String totalLength = rootNode.get("totalLength")
									.textValue();
							if (g != null) {
								g.setLength(Integer.parseInt(totalLength));
								g.setPaperLength(Integer.parseInt(length));
								em.persist(g);
							}
							resultNode.put("saved", true);
						} else {
							resultNode.put("not saved", false);
						}
					} else if (action.equalsIgnoreCase("UpdateKiosk")) {
						groupName = rootNode.get("group").textValue();
						Group g = queue.fetchGroup(groupName);

						if (g != null) {
							g.setCounter(0);
							em.persist(g);
							resultNode.put("saved", true);
						} else {
							resultNode.put("not saved", false);
						}
					} else if (action.equalsIgnoreCase("reklamaRightPlay")) {
						groupName = rootNode.get("group").textValue();
						playVideo(groupName, "reklamaRightPlay", em);
					} else if (action.equalsIgnoreCase("reklamaLeftPlay")) {
						groupName = rootNode.get("group").textValue();
						playVideo(groupName, "reklamaLeftPlay", em);
					} else if (action.equalsIgnoreCase("videoPlay")) {
						groupName = rootNode.get("group").textValue();
						playVideo(groupName, "videoPlay", em);
					} else if (action.equalsIgnoreCase("reklamaRightStop")) {
						groupName = rootNode.get("group").textValue();
						playVideo(groupName, "reklamaRightStop", em);
					} else if (action.equalsIgnoreCase("reklamaLeftStop")) {
						groupName = rootNode.get("group").textValue();
						playVideo(groupName, "reklamaLeftStop", em);
					} else if (action.equalsIgnoreCase("videoStop")) {
						groupName = rootNode.get("group").textValue();
						playVideo(groupName, "videoStop", em);
					} else if (action.equalsIgnoreCase("groups")) {
						List<Group> groups = queue.getGroups();
						resultNode.putAll(groups2JSON(groups));
					} else if (action.equalsIgnoreCase("loadGroup")) {
						ArrayNode arrayNode = queue.getGroups(user.getGroup());
						ObjectNode groups = mapper.createObjectNode();
						groups.put("groups", arrayNode);
						resultNode.putAll(groups);

						List<AnketaQuestions> questions = queue
								.getEntityManager()
								.createQuery(
										"select q from" + " 	AnketaQuestions q"
												+ " where"
												+ "	q.group = :group)")
								.setParameter("group", user.getGroup())
								.getResultList();
						// System.out.println(user.getGroup().getParent());
						// System.out.println(questions.size()
						// + "questions size------------------");
						// for (int i = 0; i < questions.size(); i++) {
						// System.out.println(questions.get(i));
						// }
						ArrayNode weekendDatesNode = mapper.createArrayNode();
						ObjectNode holyDateNode = null;
						List<Weekend> weekends_days = queue
								.getEntityManager()
								.createQuery(
										"select w from" + " 	Weekend w"
												+ " WHERE" + "	w.start = null"
												+ " AND w.weekends != null")
								.getResultList();
						if (!weekends_days.isEmpty()) {
							// System.out.println("I am not NULL");
							for (Weekend weekend : weekends_days) {
								// System.out.println(weekend.getStart());
								holyDateNode = mapper.createObjectNode();
								holyDateNode.put("group", weekend.getGroup()
										.getName());
								holyDateNode.put("days", weekend.getWeekends());

								weekendDatesNode.add(holyDateNode);
							}
						}

						ArrayNode questionsNode = mapper.createArrayNode();
						ObjectNode questionNode = null;
						ArrayNode arrayNodeName = null;
						ArrayNode arrayNodeId = null;
						for (AnketaQuestions q : questions) {
							questionNode = mapper.createObjectNode();
							questionNode.put("id", q.getId());
							questionNode.put("question", q.getName());
							arrayNodeName = mapper.createArrayNode();
							for (AnketaAnswers a : q.getAnswers()) {
								arrayNodeName.add(mapper.createObjectNode()
										.put("Aname", a.getName()));
							}
							arrayNodeId = mapper.createArrayNode();
							for (AnketaAnswers a : q.getAnswers()) {
								arrayNodeId.add(mapper.createObjectNode().put(
										"AId", a.getId()));
							}
							questionNode.put("answersName", arrayNodeName);
							questionNode.put("answersId", arrayNodeId);
							questionsNode.add(questionNode);
						}
						resultNode.put("questions", questionsNode);

						if (rootNode.has("group")) {
							groupName = rootNode.get("group").textValue();
						}
						List<User> users = queue.fetchUsers(groupName);
						resultNode.putAll(users2JSON(users));
						resultNode.put("username", user.getUsername());
						resultNode.put("group", groupName);
						Group g = queue.fetchGroup(groupName);
						resultNode.put("videoPath", g.getVideoPath());
						resultNode.put("runningLine", g.getRunningLine());

						resultNode.put("kioskLogo", user.getGroup()
								.getKioskLogo());
						resultNode.put("dashboardLogo", user.getGroup()
								.getDashboardLogo());
						resultNode.put("ticketLogo", user.getGroup()
								.getTicketLogo());
						resultNode.put("weekends_days", weekendDatesNode);
					} else if (action.equalsIgnoreCase("get_all_weekends")) {
						ArrayNode weekendDatesNode = mapper.createArrayNode();
						ObjectNode holyDateNode = null;
						List<Weekend> weekends_days = queue
								.getEntityManager()
								.createQuery(
										"select w from" + " 	Weekend w"
												+ " WHERE" + "	w.start = null"
												+ " AND w.weekends != null")
								.getResultList();
						if (!weekends_days.isEmpty()) {
							System.out.println("I am not NULL");
							for (Weekend weekend : weekends_days) {
								System.out.println(weekend.getStart());
								holyDateNode = mapper.createObjectNode();
								holyDateNode.put("group", weekend.getGroup()
										.getName());
								holyDateNode.put("days", weekend.getWeekends());

								weekendDatesNode.add(holyDateNode);
							}
						}
						resultNode.put("weekends_days", weekendDatesNode);
					} else if (action.equalsIgnoreCase("loadTemplates")) {
						ArrayNode templatesNode = mapper.createArrayNode();

						ObjectNode kioskNode = mapper.createObjectNode();
						kioskNode.put("role", "KIOSK");
						kioskNode.put("html", user.getGroup()
								.getKioskTemplate());

						ObjectNode unitNode = mapper.createObjectNode();
						unitNode.put("role", "UNIT");
						unitNode.put("html", user.getGroup().getUnitTemplate());

						ObjectNode dashboardNode = mapper.createObjectNode();
						dashboardNode.put("role", "DASHBOARD");
						dashboardNode.put("html", user.getGroup()
								.getDashboardTemplate());

						templatesNode.add(dashboardNode);
						templatesNode.add(kioskNode);
						templatesNode.add(unitNode);
						resultNode.put("templates", templatesNode);
					} else if (action.equalsIgnoreCase("user_anketa")) {
						String answerId = rootNode.get("answer").textValue();
						AnketaAnswers answer = em.find(AnketaAnswers.class,
								Long.parseLong(answerId));
						answer.setScore(answer.getScore() + 1);
					} else if (action.equalsIgnoreCase("ticket_smile")) {
						long tokenId = rootNode.get("token").asLong();
						String condition = rootNode.get("condition")
								.textValue();

						QLog.info("ticket smile " + tokenId + " / condition "
								+ condition);
						Token tokenSmile = em.find(Token.class, tokenId);

						int mark = 3;
						if (condition.equals("1"))
							mark = 1;
						else if (condition.equals("5"))
							mark = 5;

						tokenSmile.setMark(mark);
						em.merge(tokenSmile);

						resultNode.put("refreshSmile", true);

						QLog.info("tokenSmile.getMark " + tokenSmile.getMark());

						ObjectNode rootMonitorNode = mapper.createObjectNode();
						rootMonitorNode.put("ticket", tokenSmile.getTicket()
								.getCode());
						rootMonitorNode.put("unit", tokenSmile.getTarget()
								.getUsername());
						rootMonitorNode.put("smile", true);
						rootMonitorNode.put("mark", mark);
						rootMonitorNode.put("lane", tokenSmile.getLaneName());
						rootMonitorNode.put("tokenId", tokenSmile.getId());

						// ObjectNode unitNode = mapper.createObjectNode();
						// unitNode.put("unit",
						// tokenSmile.getTarget().getUsername());

						List<Token> tokenUnit = queue
								.getEntityManager()
								.createQuery(
										"select t "
												+ "from Token t "
												+ "join t.ticket tt "
												+ "join t.target u "
												+ "where cast(tt.create as date) = current_date() "
												+ "  and u.username = :username")
								.setParameter("username",
										tokenSmile.getTarget().getUsername())
								.getResultList();

						int tokenAll = tokenUnit.size();
						int countMarkBad = 0, countMarkNorm = 0, countMarkGood = 0;

						double markAverage = 0;
						for (Token t : tokenUnit) {
							QLog.info("Token of Unit "
									+ t.getTarget().getUsername() + " mark "
									+ t.getMark());

							if (t.getMark() != 0)
								markAverage += t.getMark();

							switch (t.getMark()) {
							case 1:
								countMarkBad++;
								break;
							case 3:
								countMarkNorm++;
								break;
							case 5:
								countMarkGood++;
								break;
							}
						}

						int markedAll = countMarkBad + countMarkNorm
								+ countMarkGood;
						if (markedAll > 0)
							markAverage = markAverage / markedAll;

						rootMonitorNode.put("tokenAll", tokenAll);
						rootMonitorNode.put("countMarkBad", countMarkBad);
						rootMonitorNode.put("countMarkNorm", countMarkNorm);
						rootMonitorNode.put("countMarkGood", countMarkGood);
						rootMonitorNode.put(
								"markAverage",
								(markAverage == 0) ? "0.000" : String.format(
										"%.3g%n", markAverage));

						publish(tokenSmile.getTarget().getGroup().getName()
								+ "." + tokenSmile.getLaneName() + ".pubsub",
								rootMonitorNode);

						QLog.info("createResponse action ticket_smile");
					}
					// else if
					// (action.equalsIgnoreCase("monitor_representDate")) {
					// String dateFrom = rootNode.get("dateFrom").textValue();
					// String dateTo = rootNode.get("dateTo").textValue();
					//
					// QLog.info("dateFrom " + dateFrom + " dateTo " + dateTo);
					//
					// ObjectNode initResult = initStatistics(user, queue,
					// dateFrom, dateTo);
					// resultNode.putAll(initResult);
					// }
					// else if (action.equalsIgnoreCase("toStatictics")) {
					// Date now = new Date();
					// SimpleDateFormat formatDMY = new SimpleDateFormat(
					// "dd.MM.yyyy");
					// String dateNow = formatDMY.format(now);
					//
					// ObjectNode initResult = initStatistics(user, queue,
					// dateNow, dateNow);
					// resultNode.putAll(initResult);
					// }
					else if (action.equalsIgnoreCase("editPriority")) {
						long id = Long
								.parseLong(rootNode.get("id").textValue());
						int priority = Integer.parseInt(rootNode
								.get("priority").textValue());
						Token token1 = em.find(Token.class, id);
						token1.setPriority(priority);
						resultNode.put("tokenId", id);
						resultNode.put("priority", priority);
					}
					// else if (action.equalsIgnoreCase("toTickets")) {
					// Date now = new Date();
					// SimpleDateFormat formatDMY = new SimpleDateFormat(
					// "dd.MM.yyyy");
					// String dateNow = formatDMY.format(now);
					//
					// ObjectNode initResult = initTickets(user, queue,
					// dateNow);
					// resultNode.putAll(initResult);
					// }
					else if (action.equalsIgnoreCase("enqueue")) {
						// String iinFake = rootNode.get("client").textValue();
						List<String> laneList = new ArrayList<String>();
						if (rootNode.get("lanes") != null
								&& rootNode.get("lanes").isArray()) {
							Iterator<JsonNode> it = rootNode.get("lanes")
									.elements();
							while (it.hasNext()) {
								JsonNode node = it.next();
								laneList.add(node.textValue());
							}
						}

						Ticket ticket = queue.enqueue(user.getGroup(), rootNode
								.get("client").textValue(), laneList, rootNode
								.get("lang").textValue());
						List<Token> tokenList = ticket.getTokenList();
						ArrayNode lanesNode = mapper.createArrayNode();

						boolean isReserved = false;
						if (ticket.getTokenList() != null) {
							if (!ticket.getTokenList().isEmpty())
								isReserved = (tokenList.get(0).getPriority() == 10);
						}

						List<Reservation> listReservation = Reservation
								.fetchReservations(ticket.getGroup().getName(),
										em);

						for (Reservation r : listReservation) {
							/*
							 * if (r.getTicket().getId() == ticket.getId()) {
							 * r.setStatus
							 * (kz.bee.cloud.queue.reservation.Reservation
							 * .Status.CLOSED); em.merge(r); break; }
							 */

							if (r.getTicket() != null)
								if (r.getTicket().getId() == ticket.getId()) {
									r.setStatus(kz.bee.cloud.queue.reservation.Reservation.Status.CLOSED);
									em.merge(r);
									break;
								}
						}
						if (tokenList.size() > 0) {
							user.getGroup().setCounter(
									user.getGroup().getCounter()
											+ tokenList.size());
						}

						for (Token t : tokenList) {
							lanesNode.add(t.getTarget().getUsername());
						}
						ObjectNode ticketNode = mapper.createObjectNode();
						ticketNode.put("ticketId", ticket.getId());
						ticketNode.put("code", ticket.getCode());
						ticketNode.put("lanes", lanesNode);
						ticketNode.put("isReserved", isReserved);
						ticketNode.put("clientIin", rootNode.get("client")
								.textValue());

						/*
						 * long waitingClients = (Long)
						 * queue.getEntityManager().createQuery(
						 * "select count(t) " + "from " + "	Token t " +
						 * "	join t.ticket tt " + "where tt.id != :id "+
						 * "   and tt.group = :group " +
						 * "	and cast(tt.create as time) < current_time() " +
						 * "	and t.status in ('WAITING','POSTPONED') " +
						 * "	and t.laneName in (:lanes)") .setParameter("id",
						 * ticket.getId()) .setParameter("group",
						 * ticket.getGroup()) .setParameter("lanes", laneList)
						 * .();
						 * 
						 * ticketNode.put("waitingClients", waitingClients);
						 */

						resultNode.put("ticket", ticketNode);
						System.out.println("resultNode: " + ticketNode);
					} else if (action.equalsIgnoreCase("transfer")) {
						if (rootNode.has("destination")) {
							if (rootNode.has("returnToken")) {
								token = queue
										.transfer(rootNode.get("token")
												.longValue(),
												rootNode.get("laneName")
														.textValue(), rootNode
														.get("destination")
														.textValue(),
												groupName,
												rootNode.get("radioButton")
														.textValue(), rootNode
														.get("returnToken")
														.booleanValue());
							} else {
								token = queue
										.transfer(rootNode.get("token")
												.longValue(),
												rootNode.get("laneName")
														.textValue(), rootNode
														.get("destination")
														.textValue(),
												groupName,
												rootNode.get("radioButton")
														.textValue(), false);
							}
						} else {
							if (rootNode.has("returnToken")) {
								token = queue
										.transfer(rootNode.get("token")
												.longValue(),
												rootNode.get("laneName")
														.textValue(),
												groupName,
												rootNode.get("radioButton")
														.textValue(), rootNode
														.get("returnToken")
														.booleanValue());
							} else {
								token = queue
										.transfer(rootNode.get("token")
												.longValue(),
												rootNode.get("laneName")
														.textValue(),
												groupName,
												rootNode.get("radioButton")
														.textValue(), false);
							}
						}
					} else if (action.equalsIgnoreCase("recall")) {
						token = queue.recall(Long.parseLong(rootNode.get(
								"token").textValue()), groupName);
					} else if (action.equalsIgnoreCase("statistics")) {
						resultNode
								.putAll(getUnitStatistics((Unit) user, queue));
					} else if (action.equalsIgnoreCase("changeLang")) {
						String newLang = rootNode.get("language").asText();
						// String username_ = rootNode.get("user").toString();
						// System.out.println(username+" "+newLang);
						// user = queue.findUser(username_);
						User.changeLanguage(user, newLang);
						System.out
								.println("+++++++++++++++++++++++I have changed language.+++++++++++++++++++"
										+ newLang);
						resultNode.put("lang", newLang);
					} else if (action.equalsIgnoreCase("decline")) {
						Long token_id = rootNode.get("token_id").asLong();
						Token.setMissedById(em, token_id);
						Unit u = (Unit) user;
						if (u.getMonitorUnit() != null) {
							seMonitorBack(u);
						}
					} else if (action.equalsIgnoreCase("back")) {
						token = queue.back((Unit) user);
						System.out.println(token.getTarget()
								+ "-------------------------");
						Unit u = (Unit) user;
						if (u.getMonitorUnit() != null) {
							seMonitorBack(u);
						}
					}
				}
			}
			if (token != null) {
				resultNode.put("token", token2JSON(token, user.getLanguage()));
			}
			response = mapper.writeValueAsString(resultNode);
			em.getTransaction().commit();
		} catch (QueuePluginException e) {
			response = String.format("{'exception':'%s'}", e.getMessage())
					.replace("'", "\"");
			em.getTransaction().rollback();
		} catch (Exception e) {
			e.printStackTrace();
			em.getTransaction().rollback();
			response = "{'exception':'unexpected'}".replace("'", "\"");
		} finally {
			em.close();
		}
		// QLog.info("Generated json:#0",response);
		// router.route(response);
		Message responseMessage = new Message();
		responseMessage.setType(Type.chat);
		responseMessage.setFrom(Constants.QUEUE);
		responseMessage.setTo(incoming.getFrom());
		responseMessage.setBody(response);

		long end = System.currentTimeMillis();
		QLog.info("Response in:#0 ms", end - start);
		return responseMessage;
	}

	private ObjectNode token2JSON(Token token, Language lang) {
		QLog.info("start");
		ObjectNode tokenNode = mapper.createObjectNode();
		tokenNode.put("ticket", token.getTicket().getCode());
		tokenNode.put("id", token.getId());
		tokenNode.put("lane", token.getLaneName());
		tokenNode.put("status", token.getStatus().toString().toLowerCase());
		tokenNode.put("priority", token.getPriority());
		// Sultan
		/*
		 * if(token.getPostpone()!=null){ tokenNode.put("postpone", true); }
		 */
		if (lang == Language.RU)
			tokenNode.put("laneText",
					Messages.get("lane.#0.name_RU", token.getLaneName()));
		else if (lang == Language.KK)
			tokenNode.put("laneText",
					Messages.get("lane.#0.name_KK", token.getLaneName()));
		else if (lang == Language.EN)
			tokenNode.put("laneText",
					Messages.get("lane.#0.name_EN", token.getLaneName()));
		QLog.info("end");
		return tokenNode;
	}

	public void publish(String nodeName, ObjectNode jsonNode)
			throws JsonGenerationException, JsonMappingException, IOException {
		QLog.info("start");
		jsonNode.put("pubsub", true);
		String jsonStr = mapper.writeValueAsString(jsonNode);
		System.out.println("nodeName " + nodeName);
		QLog.info("publishing to #0: #1", nodeName, jsonStr);
		LeafNode node = (LeafNode) XMPPServer.getInstance().getPubSubModule()
				.getNode(nodeName);

		Message msg = new Message();
		msg.setBody(jsonStr);
		msg.setFrom(Constants.QUEUE);
		List<Element> elList = new ArrayList<Element>();
		elList.add(msg.getElement());
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++===");
		System.out.println("NODE: " + node);
		System.out.println("MSG: " + msg);
		System.out.println("elList: " + elList);
		node.publishItems(Constants.QUEUE, elList);
		QLog.info("end");
	}

	private ObjectNode users2JSON(List<User> users) throws QueuePluginException {
		QLog.info("started");
		// for (int i = 0; i < users.size(); i++) {
		// System.out.println(users.get(i).getUsername());
		// }
		ArrayNode arrayNode = mapper.createArrayNode();
		for (User user : users) {
			ObjectNode userNode = mapper.createObjectNode();
			userNode.put("user", user.getUsername())
					.put("role", user.getRole().toString())
					.put("status", user.getStatus().toString());
			if (user.getRole().equals(Role.KIOSK) && user.getData() != null) {
				userNode.put("data", user.getData());
			}
			if (user instanceof Dashboard) {
				Dashboard dashboard = (Dashboard) user;
				Set<Lane> lanes = dashboard.getDashboardLanes();
				ArrayNode lanesNodeRu = mapper.createArrayNode();
				ArrayNode lanesNodeKk = mapper.createArrayNode();
				ArrayNode lanesNodeEn = mapper.createArrayNode();
				ArrayNode lanesNode = mapper.createArrayNode();
				String ru = "";
				String kk = "";
				String en = "";
				for (Lane lt : lanes) {
					if (lt.isEnabled()) {
						kk = Messages.get("lane.#0.name_KK", lt.getUsername());
						ru = Messages.get("lane.#0.name_RU", lt.getUsername());
						en = Messages.get("lane.#0.name_EN", lt.getUsername());
						if (kk.equals("lane." + lt.getUsername() + ".name_KK")) {
							lanesNodeKk.add("");
						} else {
							lanesNodeKk.add(kk);
						}
						if (ru.equals("lane." + lt.getUsername() + ".name_RU")) {
							lanesNodeRu.add("");
						} else {
							lanesNodeRu.add(ru);
						}
						if (en.equals("lane." + lt.getUsername() + ".name_EN")) {
							lanesNodeEn.add("");
						} else {
							lanesNodeEn.add(en);
						}
						lanesNode.add(lt.getUsername());
					}
				}
				userNode.put("lanesRu", lanesNodeRu);
				userNode.put("lanesKk", lanesNodeKk);
				userNode.put("lanesEn", lanesNodeEn);
				userNode.put("lanes", lanesNode);
			} else if (user instanceof Unit) {
				Unit unit = (Unit) user;
				Set<Lane> lanes = unit.getLanes();
				ArrayNode lanesNodeRu = mapper.createArrayNode();
				ArrayNode lanesNodeKk = mapper.createArrayNode();
				ArrayNode lanesNodeEn = mapper.createArrayNode();
				ArrayNode lanesNode = mapper.createArrayNode();
				String ru = "";
				String kk = "";
				String en = "";
				for (Lane lt : lanes) {
					if (lt.isEnabled()) {
						kk = Messages.get("lane.#0.name_KK", lt.getUsername());
						ru = Messages.get("lane.#0.name_RU", lt.getUsername());
						en = Messages.get("lane.#0.name_EN", lt.getUsername());
						if (kk.equals("lane." + lt.getUsername() + ".name_KK")) {
							lanesNodeKk.add("");
						} else {
							lanesNodeKk.add(kk);
						}
						if (ru.equals("lane." + lt.getUsername() + ".name_RU")) {
							lanesNodeRu.add("");
						} else {
							lanesNodeRu.add(ru);
						}
						if (en.equals("lane." + lt.getUsername() + ".name_EN")) {
							lanesNodeEn.add("");
						} else {
							lanesNodeEn.add(en);
						}
						lanesNode.add(lt.getUsername());
					}
				}

				userNode.put("lanesRu", lanesNodeRu);
				userNode.put("lanesKk", lanesNodeKk);
				userNode.put("lanesEn", lanesNodeEn);
				userNode.put("lanes", lanesNode);
				if (unit.getMonitorUnit() != null) {
					userNode.put("secondMonitor", unit.getMonitorUnit()
							.getUsername());
				}
				userNode.put("firstname", unit.getFirstName());
				userNode.put("workStart", unit.getWorkStart() == null ? ""
						: unit.getWorkStart());
				userNode.put("workFinish", unit.getWorkFinish() == null ? ""
						: unit.getWorkFinish());
				userNode.put("window", unit.getWindow());
				userNode.put("lastname", unit.getLastName());
			} else if (user instanceof Lane) {
				Lane lane = (Lane) user;
				userNode.put("color", lane.getColor())
						.put("startRange", lane.getRangeStart())
						.put("endRange", lane.getRangeEnd())
						.put("priority", lane.getPriority())
						.put("counter", lane.getCount());
				if (Messages.get("lane.#0.name_KK", lane.getUsername()).equals(
						"lane." + lane.getUsername() + ".name_KK")) {
					userNode.put("kk", "");
				} else {
					userNode.put("kk",
							Messages.get("lane.#0.name_KK", lane.getUsername()));
				}
				if (Messages.get("lane.#0.name_RU", lane.getUsername()).equals(
						"lane." + lane.getUsername() + ".name_RU")) {
					userNode.put("ru", "");
				} else {
					userNode.put("ru",
							Messages.get("lane.#0.name_RU", lane.getUsername()));
				}
				if (Messages.get("lane.#0.name_EN", lane.getUsername()).equals(
						"lane." + lane.getUsername() + ".name_EN")) {
					userNode.put("en", "");
				} else {
					userNode.put("en",
							Messages.get("lane.#0.name_EN", lane.getUsername()));
				}
			}
			// System.out.println(userNode);
			arrayNode.add(userNode);
		}

		ObjectNode usersNode = mapper.createObjectNode();
		usersNode.put("users", arrayNode);
		QLog.info("end");
		return usersNode;
	}

	private ObjectNode groups2JSON(List<Group> groups)
			throws QueuePluginException {
		QLog.info("started");
		ArrayNode arrayNode = mapper.createArrayNode();
		for (Group group : groups) {
			arrayNode.add(mapper.createObjectNode()
					.put("name", group.getName()));
		}
		ObjectNode groupsNode = mapper.createObjectNode();
		groupsNode.put("groups", arrayNode);
		QLog.info("end");
		return groupsNode;
	}

	// TODO in separate class AdminWrapper
	// TODO only for local admin, need to make to global also
	private String wrapAdminAction(ObjectNode rootNode, User admin,
			EntityManager em) throws QueuePluginException {
		QLog.info("started");
		String action = rootNode.findPath("type").textValue();
		if (action.equalsIgnoreCase("createUser")
				|| action.equalsIgnoreCase("editUser")) {
			Set<String> laneNames = new HashSet<String>();
			if (rootNode.has("lanes") && rootNode.get("lanes").isArray()) {
				Iterator<JsonNode> it = rootNode.get("lanes").elements();
				while (it.hasNext()) {
					JsonNode node = it.next();
					laneNames.add(node.textValue());
				}
			}
			List<Group> groups = em
					.createQuery(
							"select g from Group g where g.parent = :parent")
					.setParameter("parent", admin.getGroup()).getResultList();

			Group group = null;
			if (groups.size() == 0) {
				group = admin.getGroup();
			} else {
				// System.out.println (rootNode.get("group").textValue());
				for (int i = 0; i < groups.size(); i++) {
					if (groups.get(i).getName()
							.equals(rootNode.get("group").textValue())) {
						group = groups.get(i);
					}
				}
			}

			Set<Lane> lanes = new HashSet<Lane>();
			if (laneNames.size() > 0) {
				lanes.addAll(em
						.createQuery("from Lane l where l.username in (:lanes)")
						.setParameter("lanes", laneNames).getResultList());
			}
			System.out.println(rootNode.get("role").textValue()
					+ "-----------------------");
			if (rootNode.get("role").textValue().equals("QUESTION")) {
				if (action.contains("create")) {
					String questionName = rootNode.get("question").textValue();
					String a1 = rootNode.get("answer1").textValue();
					String a2 = rootNode.get("answer2").textValue();
					String a3 = rootNode.get("answer3").textValue();
					String a4 = rootNode.get("answer4").textValue();
					String a5 = rootNode.get("answer5").textValue();

					AnketaQuestions question = new AnketaQuestions();
					question.setName(questionName);
					question.setGroup(admin.getGroup());
					em.persist(question);
					AnketaAnswers answer = null;
					if (a1.length() > 0) {
						answer = new AnketaAnswers();
						answer.setName(a1);
						answer.setQuestion(question);
						em.persist(answer);
					}
					if (a2.length() > 0) {
						answer = new AnketaAnswers();
						answer.setName(a2);
						answer.setQuestion(question);
						em.persist(answer);
					}
					if (a3.length() > 0) {
						answer = new AnketaAnswers();
						answer.setName(a3);
						answer.setQuestion(question);
						em.persist(answer);
					}
					if (a4.length() > 0) {
						answer = new AnketaAnswers();
						answer.setName(a4);
						answer.setQuestion(question);
						em.persist(answer);
					}
					if (a5.length() > 0) {
						answer = new AnketaAnswers();
						answer.setName(a5);
						answer.setQuestion(question);
						em.persist(answer);
					}
					QLog.info("Anketa #0 created ", question);
				} else if (action.contains("edit")) {
					String questionId = rootNode.get("questionId").textValue();
					String questionName = rootNode.get("question").textValue();
					String a1 = rootNode.get("answer1").textValue();
					String a2 = rootNode.get("answer2").textValue();
					String a3 = rootNode.get("answer3").textValue();
					String a4 = rootNode.get("answer4").textValue();
					String a5 = rootNode.get("answer5").textValue();
					Long id1 = rootNode.get("id1").longValue();
					Long id2 = rootNode.get("id2").longValue();
					Long id3 = rootNode.get("id3").longValue();
					Long id4 = rootNode.get("id4").longValue();
					Long id5 = rootNode.get("id5").longValue();

					AnketaQuestions question = em.find(AnketaQuestions.class,
							Long.parseLong(questionId));

					if (question == null) {
						throw new QueuePluginException(
								"admin.edit.anketaQuestion.notfound:"
										+ questionId);
					} else {
						question.setName(questionName);
					}
					AnketaAnswers answer1 = em.find(AnketaAnswers.class, id1);
					if (answer1 != null) {
						answer1.setName(a1);
					} else {
						AnketaAnswers answer = new AnketaAnswers();
						answer.setName(a1);
						answer.setQuestion(question);
						em.persist(answer);
					}
					AnketaAnswers answer2 = em.find(AnketaAnswers.class, id2);
					if (answer2 != null) {
						answer2.setName(a2);
					} else {
						AnketaAnswers answer = new AnketaAnswers();
						answer.setName(a2);
						answer.setQuestion(question);
						em.persist(answer);
					}
					AnketaAnswers answer3 = em.find(AnketaAnswers.class, id3);
					if (answer3 != null) {
						answer3.setName(a3);
					} else {
						AnketaAnswers answer = new AnketaAnswers();
						answer.setName(a3);
						answer.setQuestion(question);
						em.persist(answer);
					}
					AnketaAnswers answer4 = em.find(AnketaAnswers.class, id4);
					if (answer4 != null) {
						answer4.setName(a4);
					} else {
						AnketaAnswers answer = new AnketaAnswers();
						answer.setName(a4);
						answer.setQuestion(question);
						em.persist(answer);
					}
					AnketaAnswers answer5 = em.find(AnketaAnswers.class, id5);
					if (answer5 != null) {
						answer5.setName(a5);
					} else {
						AnketaAnswers answer = new AnketaAnswers();
						answer.setName(a5);
						answer.setQuestion(question);
						em.persist(answer);
					}
					QLog.info("Anketa #0 edited", question);
				}
			} else {
				Role role = Role.get(rootNode.get("role").textValue());
				if (role == null) {
					throw new QueuePluginException("wrapper.wrong.format:"
							+ rootNode.get("role").textValue());
				}
				System.out.println(role);
				System.out.println(rootNode);
				String username = rootNode.get("username").textValue()
						.toLowerCase();
				String password = rootNode.get("password").textValue();
				if (action.contains("create")) {
					System.out.println(role);
					switch (role) {
					case MONITOR:
						System.out.print("MONITOR");
						new Admin(em).createMonitor(username, password, group);
						break;
					case SECOND_MONITOR:
						System.out.print("SECOND_MONITOR");
						new Admin(em).createSecondMonitor(username, password,
								group);
						break;
					case UNIT:
						String workStart = "";
						String workFinish = "";
						if(rootNode.get("workStart")!=null){
							workStart = rootNode.get("workStart").textValue();
						}
						if(rootNode.get("workFinish")!=null){
							workStart = rootNode.get("workFinish").textValue();
						}
						new Admin(em).createUnit(username, password, rootNode
								.get("firstname").textValue(),
								rootNode.get("lastname").textValue(), group,
								lanes, rootNode.get("window").textValue(),
								workStart, workFinish);
						break;
					case DASHBOARD:
						new Admin(em).createDashboard(username, password,
								group, lanes);
						break;
					case LANE:
						System.out.println(em);
						new Admin(em).createLane(username, rootNode.get("kk")
								.textValue(), rootNode.get("ru").textValue(),
								rootNode.get("startRange").textValue(),
								rootNode.get("endRange").textValue(), rootNode
										.get("color").textValue(), group,
								rootNode.get("priority").textValue());
						break;
					case KIOSK:
						// System.out.println ("kiosk");
						if (rootNode.has("mainLanes")
								&& rootNode.get("mainLanes").isArray()) {
							System.out.println("mainLanes");
							ArrayNode mainLanesNode = mapper.createArrayNode();
							Iterator<JsonNode> it = rootNode.get("mainLanes")
									.elements();
							int id = 0;
							while (it.hasNext()) {
								JsonNode mainLaneNode = it.next();
								System.out.println(mainLaneNode);
								ObjectNode oNode = mapper.convertValue(
										mainLaneNode, ObjectNode.class);
								System.out.println(oNode);
								oNode.put("id", ++id);
								mainLanesNode.add(oNode);
							}
							// System.out.println (mainLanesNode);
							if (mainLanesNode.size() > 0) {
								try {
									new Admin(em)
											.createKiosk(
													username,
													password,
													group,
													mapper.writeValueAsString(mainLanesNode));
								} catch (Exception e) {
									throw new QueuePluginException(
											"JSON parse exception");
								}
								break;
							}
						}
						new Admin(em).createKiosk(username, password, group,
								null);
						break;
					}
				} else if (action.contains("edit")) {
					switch (role) {
					case MONITOR:
						new Admin(em).editMonitor(username, password, group);
						break;
					case SECOND_MONITOR:
						new Admin(em).editSecondMonitor(username, password,
								group);
						break;
					case UNIT:
						MonitorUnit monitorUnit = null;
						if (rootNode.has("secondMonitor")) {
							String sm = rootNode.get("secondMonitor")
									.textValue();
							monitorUnit = (MonitorUnit) em
									.createQuery(
											"from MonitorUnit m "
													+ "where m.username =:username")
									.setParameter("username", sm)
									.getSingleResult();
						}
						String workStart = "";
						if (rootNode.has("workStart")){
							workStart = rootNode.get("workStart").textValue();
						}
						String workFinish = "";
						if (rootNode.has("workFinish")){
							workStart = rootNode.get("workFinish").textValue();
						}
//						System.out.println(rootNode.get("workStart")+"-------------------------");
						new Admin(em).editUnit(username, password, monitorUnit,
								rootNode.get("firstname").textValue(), rootNode
										.get("lastname").textValue(), group,
								lanes, rootNode.get("window").textValue(),
								workStart, workFinish);
						break;
					case DASHBOARD:
						System.out.println("editDashboard"+username+" "+lanes);
						new Admin(em).editDashboard(username, password, group,
								lanes);
						break;
					case LANE:
						System.out.println(em);
						new Admin(em).editLane(username,
								rootNode.get("startRange").textValue(),
								rootNode.get("endRange").textValue(), rootNode
										.get("color").textValue(), group,
								rootNode.get("kk").textValue(),
								rootNode.get("ru").textValue(),
								rootNode.get("en").textValue(),
								rootNode.get("priority").textValue());
						break;
					case KIOSK:
						System.out.println("kiosk");
						if (rootNode.has("mainLanes")
								&& rootNode.get("mainLanes").isArray()) {
							System.out.println("mainLanes");
							ArrayNode mainLanesNode = mapper.createArrayNode();
							Iterator<JsonNode> it = rootNode.get("mainLanes")
									.elements();
							Iterator<JsonNode> it1 = rootNode.get("mainLanes")
									.elements();
							while (it1.hasNext()) {
								System.out.println(it1.next());
							}
							System.out.println("mainLanes");
							int id = 0;
							while (it.hasNext()) {
								JsonNode mainLaneNode = it.next();
								System.out
										.println(mainLaneNode + "-----------");
								ObjectNode oNode = mapper.convertValue(
										mainLaneNode, ObjectNode.class);
								System.out.println(oNode);
								oNode.put("id", ++id);
								mainLanesNode.add(oNode);
							}
							if (mainLanesNode.size() > 0) {
								String lanesNode = null;
								try {
									lanesNode = mapper
											.writeValueAsString(mainLanesNode);
									System.out.println(lanesNode);
								} catch (Exception e) {
									throw new QueuePluginException(
											"JSON parse exception");
								}
								new Admin(em).editKiosk(username, password,
										group, role, lanesNode);
								break;
							}
						}
						new Admin(em).editKiosk(username, password, group,
								role, null);
						break;
					}
				}
			}
		} else if (action.equalsIgnoreCase("createGroup")) {
			String groupName = rootNode.get("name").textValue();
			String parentGroupName = rootNode.get("parent").textValue();
			Map<String, String> props = new HashMap<String, String>();
			if (rootNode.get("props").isArray()) {
				Iterator<JsonNode> it = rootNode.get("props").elements();
				while (it.hasNext()) {
					JsonNode node = it.next();
					props.put(node.get("key").textValue(), node.get("value")
							.textValue());
				}
			}
			new Admin(em).createGroup(groupName, parentGroupName, props);
		} else if (action.equalsIgnoreCase("disableUser")) {
			new Admin(em).disableUser(rootNode.get("username").textValue());
		} else if (action.equalsIgnoreCase("enableUser")) {
			new Admin(em).enableUser(rootNode.get("username").textValue());
		} else if (action.equalsIgnoreCase("updateTemplates")) {
			if (rootNode.get("templates").isArray()) {
				Iterator<JsonNode> it = rootNode.get("templates").elements();
				while (it.hasNext()) {
					JsonNode node = it.next();
					Role role = Role.get(node.get("role").textValue());
					switch (role) {
					case KIOSK:
						admin.getGroup().setKioskTemplate(
								node.get("data").textValue());
						break;
					case UNIT:
						admin.getGroup().setUnitTemplate(
								node.get("data").textValue());
						break;
					case DASHBOARD:
						admin.getGroup().setDashboardTemplate(
								node.get("data").textValue());
						break;
					default:
						break;
					}
				}
			}
		} else if (action.equalsIgnoreCase("updatePictures")) {
			admin.getGroup().setDashboardLogo(
					rootNode.get("dashboardLogo").textValue());
			admin.getGroup()
					.setKioskLogo(rootNode.get("kioskLogo").textValue());
			admin.getGroup().setTicketLogo(
					rootNode.get("ticketLogo").textValue());
			QLog.info("Logos updated");
		}
		QLog.info("end");
		return "success";
	}

	private ObjectNode init(User user, QueueWrapper queue, String param)
			throws QueuePluginException {
		QLog.info("started");
		if (user.getRole().equals(Role.DASHBOARD)) {
			QLog.info("end");
			return initDashboard(user, queue);
		} else if (user.getRole().equals(Role.MONITOR)
				&& param.equals("initLocalAdmin")) {
			QLog.info("end");
			return initLocalAdmin(user, queue);
		} else if (user.getRole().equals(Role.MONITOR)) {
			QLog.info("end");
			return initMonitor(user, queue);
		} else if (user.getRole().equals(Role.UNIT)) {
			QLog.info("end");
			return initUnit(user, queue);
		} else if (user.getRole().equals(Role.KIOSK)) {
			QLog.info("end");
			return initKiosk(user, queue);
		} else if (user.getRole().equals(Role.LOCALADMIN)) {
			QLog.info("end");
			return initAdmin(user, queue);
		} else if (user.getRole().equals(Role.SECOND_MONITOR)) {
			QLog.info("end");
			return initMonitorUnit(user, queue);
		} else if (user.getRole().equals(Role.ANKETA)) {
			QLog.info("end");
			return initAnketa(user, queue);
		}
		QLog.info("end");
		return null;
	}

	// maybe REQUIRES TRANSACTION!
	public static void fire(Object param, EntityManager em) {
		QLog.info("started");
		if (XMPPServer.getInstance() != null) {
			if (param instanceof Token) {
				instance.onToken(param, em);
			}
			if (param instanceof Unit) {
				instance.onUnit(param); // TODO
			}
			if (param instanceof Ticket) {
				instance.onTicket(param, em);
			}
		}
		QLog.info("end");
	}

	private void onToken(Object obj, EntityManager em) {
		QLog.info("start");
		System.out.println("onToken");
		try {
			Token token = (Token) obj;

			// if (token.getStatus() != Status.POSTPONED && token.getStatus() !=
			// Status.WAITING)

			/*
			 * WAITING, CALLED, POSTPONED, STARTED, ENDED, MISSED, TRANSFERRED
			 */
			// {

			boolean isTicketReserved = Reservation.isTicketReserved(token
					.getTicket().getGroup().getName(), em, token.getTicket()
					.getId());

			ObjectNode rootNode = mapper.createObjectNode();

			rootNode.put("ticket", token.getTicket().getCode())
					.put("lane", token.getLaneName())
					.put("tokenId", token.getId())
					.put("color", token.getColor())
					.put("isTicketReserved", isTicketReserved)

					.put("pubsub", true);
			if (token.getTicket().getLang().toString().equals("RU")) {
				rootNode.put("lang", "ru");
			} else if (token.getTicket().getLang().toString().equals("KK")) {
				rootNode.put("lang", "kz");
			} else if (token.getTicket().getLang().toString().equals("EN")) {
				rootNode.put("lang", "en");
			}
			Unit unit = null;
			if (token.getTarget() != null) {
				System.out.println(token.getTarget().getUsername());
				System.out.println(token.getTarget().getStatus());
				unit = em.find(Unit.class, token.getTarget().getUsername());
				if (unit != null) {
					rootNode.put("unit", unit.getUsername()).put("window",
							unit.getWindow());
				}
			}

			switch (token.getStatus()) {
			case CALLED:
				rootNode.put(token.getStatus().toString().toLowerCase(),
						TimeUtils.toTime(token.getCreate()));
				if (unit.getMonitorUnit() != null) {
					seMonitorTicket(token);
				}
				break;
			case RECALLED:
				rootNode.put(token.getStatus().toString().toLowerCase(),
						TimeUtils.toTime(token.getCreate()));
				if (unit.getMonitorUnit() != null) {
					seMonitorTicket(token);
				}
				break;
			case STARTED:
				rootNode.put(token.getStatus().toString().toLowerCase(),
						TimeUtils.toTime(token.getStart()));
				break;
			case WAITING:
				rootNode.put(token.getStatus().toString().toLowerCase(), true);
				break;
			case ENDED:
				rootNode.put(token.getStatus().toString().toLowerCase(),
						TimeUtils.toTime(token.getEnd()));
				List<Token> tokenUnit = em
						.createQuery(
								"select t "
										+ "from Token t "
										+ "join t.ticket tt "
										+ "join t.target u "
										+ "where cast(tt.create as date) = current_date() "
										+ "  and u.username = :username")
						.setParameter("username",
								token.getTarget().getUsername())
						.getResultList();

				int tokenAll = tokenUnit.size();
				int countMarkBad = 0,
				countMarkNorm = 0,
				countMarkGood = 0;

				double markAverage = 0;
				for (Token t : tokenUnit) {
					// QLog.info("Token of Unit " + t.getTarget().getUsername()
					// + " mark " + t.getMark());

					if (t.getMark() != 0)
						markAverage += t.getMark();

					switch (t.getMark()) {
					case 1:
						countMarkBad++;
						break;
					case 3:
						countMarkNorm++;
						break;
					case 5:
						countMarkGood++;
						break;
					}
				}

				int markedAll = countMarkBad + countMarkNorm + countMarkGood;
				if (markedAll > 0)
					markAverage = markAverage / markedAll;

				rootNode.put("tokenAll", tokenAll);
				rootNode.put("countMarkBad", countMarkBad);
				rootNode.put("countMarkNorm", countMarkNorm);
				rootNode.put("countMarkGood", countMarkGood);
				rootNode.put("markAverage", (markAverage == 0) ? "0.000"
						: String.format("%.3g%n", markAverage));
				break;
			case MISSED:
				rootNode.put(token.getStatus().toString().toLowerCase(),
						TimeUtils.toTime(token.getEnd()));
				break;
			case TRANSFERRED:
				rootNode.put(token.getStatus().toString().toLowerCase(),
						TimeUtils.toTime(token.getCreate()));
				// .put("nextLane", token.getNextToken().getLaneName());
				if (unit != null && unit.getMonitorUnit() != null) {
					seMonitorRefresh(token);
				}
				break;
			default:
				break;
			}

			if (token.getStatus() != Token.Status.POSTPONED)
				publish(token.getTarget().getGroup().getName() + "."
						+ token.getLaneName() + ".pubsub", rootNode);
			// }
			// kiosk: refresh
			ObjectNode kioskNode = mapper.createObjectNode();

			ArrayNode arrayNode = mapper.createArrayNode();

			List<Lane> lanes = em
					.createQuery(
							"from Lane l " + "where l.group.id=:groupName "
									+ " 	and l.status='ENABLED'")
					.setParameter("groupName",
							token.getTicket().getGroup().getName())
					.getResultList();

			for (Lane lane : lanes) {
				long waitingClients = (Long) em
						.createQuery(
								"select count(t) "
										+ "from "
										+ "	Token t "
										+ "	join t.ticket tt "
										+ "where tt.group = :group "
										+ "	and cast(tt.create as time) < current_time() "
										+ "	and t.status in ('WAITING','POSTPONED') "
										+ "	and t.laneName = :laneName ")
						.setParameter("group", lane.getGroup())
						.setParameter("laneName", lane.getUsername())
						.getSingleResult();

				long waitingReservedClients = (Long) em
						.createQuery(
								"select count(t) "
										+ "from "
										+ "	Token t "
										+ "	join t.ticket tt "
										+ "where tt.group = :group "
										+ "	and cast(tt.create as time) < current_time() "
										+ "	and t.status in ('WAITING','POSTPONED') "
										+ "	and t.laneName = :laneName "
										+ "	and t.priority = 10")
						.setParameter("group", lane.getGroup())
						.setParameter("laneName", lane.getUsername())
						.getSingleResult();

				arrayNode.add(mapper.createObjectNode()
						.put("count", waitingClients)
						.put("reserved", waitingReservedClients)
						.put("id", lane.getUsername()));
			}
			ObjectNode lanesJSONNode = mapper.createObjectNode();
			lanesJSONNode.put("lanes", arrayNode);

			kioskNode.putAll(lanesJSONNode);
			// Sultan
			publish(token.getTicket().getGroup().getName() + ".kiosk.pubsub",
					kioskNode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		QLog.info("end");
	}

	private void playVideo(String group, String action, EntityManager em) {
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put("videoAction", action);
		try {
			publish(group + ".dashboard.pubsub", rootNode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateRunningLine(String group, String action, String text,
			EntityManager em) {
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put("videoAction", action);
		rootNode.put("runningLine", text);
		try {
			publish(group + ".dashboard.pubsub", rootNode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void onTicket(Object param, EntityManager em) {
		QLog.info("start");
		System.out.println("I just enter onTicket");

		Ticket ticket = (Ticket) param;

		boolean isReserved = false;
		if (ticket.getTokenList() != null) {
			if (!ticket.getTokenList().isEmpty())
				isReserved = (ticket.getTokenList().get(0).getPriority() == 10);
		}

		for (Token token : ticket.getTokenList()) {
			ObjectNode ticketNode = mapper.createObjectNode();
			// ticketNode.put("lane", token.getLaneName());
			ticketNode.put("id", ticket.getId());
			ticketNode.put("tokenId", token.getId());
			ticketNode.put("ticket", ticket.getCode());
			ticketNode.put("created", TimeUtils.toTime(token.getCreate()));
			ticketNode.put("priority", token.getPriority());
			ticketNode.put("lane", token.getLaneName());
			ticketNode.put("laneRu",
					Messages.get("lane.#0.name_RU", token.getLaneName()));

			ObjectNode rootNode = mapper.createObjectNode();
			rootNode.put("newticket", ticketNode);

			System.out
					.println("I am in onTicket after putting*****************************************************");

			try {
				publish(ticket.getGroup().getName() + "." + token.getLaneName()
						+ ".pubsub", rootNode);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// kiosk: refresh
		ObjectNode kioskNode = mapper.createObjectNode();

		ArrayNode arrayNode = mapper.createArrayNode();

		List<Lane> lanes = em
				.createQuery(
						"from Lane l " + "where l.group.id=:groupName "
								+ " 	and l.status='ENABLED'"
								+ "order by l.rangeStart asc ")
				.setParameter("groupName", ticket.getGroup().getName())
				.getResultList();

		// Sulta order by rangeStart
		// for (int i=0;i<lanes.size();i++){
		// System.out.println (lanes.get(i).getRangeStart());
		// }
		for (Lane lane : lanes) {
			long waitingClients = (Long) em
					.createQuery(
							"select count(t) "
									+ "from "
									+ "	Token t "
									+ "	join t.ticket tt "
									+ "where tt.group = :group "
									+ "	and cast(tt.create as time) < current_time() "
									+ "	and t.status in ('WAITING','POSTPONED') "
									+ "	and t.laneName = :laneName ")
					.setParameter("group", lane.getGroup())
					.setParameter("laneName", lane.getUsername())
					.getSingleResult();

			// long waitingReservedClients = (Long) em.createQuery(
			// "select count(t) " +
			// "from " +
			// "	Token t " +
			// "	join t.ticket tt " +
			// "where tt.group = :group " +
			// "	and cast(tt.create as time) < current_time() " +
			// "	and t.status in ('WAITING','POSTPONED') " +
			// "	and t.laneName = :laneName " +
			// "	and t.priority = 10")
			// .setParameter("group", lane.getGroup())
			// .setParameter("laneName", lane.getUsername())
			// .getSingleResult();

			long waitingReservedClients = 0;
			if (isReserved) {
				for (Token token : ticket.getTokenList())
					if (token.getLaneName().equals(lane.getUsername())) {
						waitingReservedClients++;
						break;
					}
			} else {
				for (Token token : ticket.getTokenList())
					if (token.getLaneName().equals(lane.getUsername())) {
						waitingClients++;
						break;
					}
			}

			QLog.info("count " + waitingClients + " : reserved "
					+ waitingReservedClients);
			arrayNode.add(mapper.createObjectNode()
					.put("count", waitingClients)
					.put("reserved", waitingReservedClients)
					.put("id", lane.getUsername()));
		}
		ObjectNode lanesJSONNode = mapper.createObjectNode();
		lanesJSONNode.put("lanes", arrayNode);

		kioskNode.putAll(lanesJSONNode);

		try {
			publish(ticket.getGroup().getName() + ".kiosk.pubsub", kioskNode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		QLog.info("end");
	}

	private void onUnit(Object param) {
	}

	public ArrayNode getQuestions(String groupName) {
		EntityManager em = DBManager.newEm();
		em.getTransaction().begin();
		ArrayNode arrayNode = mapper.createArrayNode();
		try {
			List<AnketaQuestions> questions = em
					.createQuery(
							"select q from" + " 	AnketaQuestions q" + " where"
									+ "	q.group.id = :groupName)")
					.setParameter("groupName", groupName).getResultList();
			System.out.println(questions.size()
					+ "questions size------------------");
			for (int i = 0; i < questions.size(); i++) {
				System.out.println(questions.get(i));
			}

			ObjectNode questionNode = null;
			ArrayNode arrayNodeName = null;
			ArrayNode arrayNodeId = null;
			for (AnketaQuestions q : questions) {
				questionNode = mapper.createObjectNode();
				questionNode.put("id", q.getId());
				questionNode.put("question", q.getName());
				arrayNodeName = mapper.createArrayNode();
				for (AnketaAnswers a : q.getAnswers()) {
					arrayNodeName.add(mapper.createObjectNode().put("Aname",
							a.getName()));
				}
				arrayNodeId = mapper.createArrayNode();
				for (AnketaAnswers a : q.getAnswers()) {
					arrayNodeId.add(mapper.createObjectNode().put("AId",
							a.getId()));
				}
				questionNode.put("answersName", arrayNodeName);
				questionNode.put("answersId", arrayNodeId);
				arrayNode.add(questionNode);
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			em.getTransaction().rollback();
		}
		em.close();

		return arrayNode;
	}

	private ObjectNode initAnketa(final User user, QueueWrapper queue)
			throws QueuePluginException {
		ObjectNode rootNode = mapper.createObjectNode();

		List<AnketaQuestions> questions = queue
				.getEntityManager()
				.createQuery(
						"select q from" + " 	AnketaQuestions q" + " where"
								+ "	q.group = :group)")
				.setParameter("group", user.getGroup()).getResultList();
		System.out.println(user.getGroup().getParent());
		System.out.println(questions.size()
				+ "questions size------------------");
		for (int i = 0; i < questions.size(); i++) {
			System.out.println(questions.get(i));
		}

		ArrayNode questionsNode = mapper.createArrayNode();
		ObjectNode questionNode = null;
		ArrayNode arrayNodeName = null;
		ArrayNode arrayNodeId = null;
		for (AnketaQuestions q : questions) {
			questionNode = mapper.createObjectNode();
			questionNode.put("id", q.getId());
			questionNode.put("question", q.getName());
			arrayNodeName = mapper.createArrayNode();
			for (AnketaAnswers a : q.getAnswers()) {
				arrayNodeName.add(mapper.createObjectNode().put("Aname",
						a.getName()));
			}
			arrayNodeId = mapper.createArrayNode();
			for (AnketaAnswers a : q.getAnswers()) {
				arrayNodeId.add(mapper.createObjectNode().put("AScore",
						a.getScore()));
			}
			questionNode.put("answersName", arrayNodeName);
			questionNode.put("answersScore", arrayNodeId);
			questionsNode.add(questionNode);
		}
		rootNode.put("questions", questionsNode);

//		return rootNode.put("url", "/" + user.getGroup() + "/anketa.html");
		return rootNode.put("url", "/anketa.html");
	}

	private ObjectNode initAdmin(final User user, QueueWrapper queue)
			throws QueuePluginException {
		QLog.info("start");
		List<Group> groups = queue.getListGroups(user.getGroup());
		ObjectNode rootNode = mapper.createObjectNode();
		
		if (groups != null) {
			List<AnketaQuestions> questions = queue
					.getEntityManager()
					.createQuery(
							"select q from" + " 	AnketaQuestions q" + " where"
									+ "	q.group = :group)")
					.setParameter("group", user.getGroup()).getResultList();
			// System.out.println(user.getGroup().getParent());
			// System.out.println(questions.size()
			// + "questions size------------------");
			// for (int i = 0; i < questions.size(); i++) {
			// System.out.println(questions.get(i));
			// }
			ArrayNode questionsNode = mapper.createArrayNode();
			ObjectNode questionNode = null;
			ArrayNode arrayNodeName = null;
			ArrayNode arrayNodeId = null;
			for (AnketaQuestions q : questions) {
				questionNode = mapper.createObjectNode();
				questionNode.put("id", q.getId());
				questionNode.put("question", q.getName());
				arrayNodeName = mapper.createArrayNode();
				for (AnketaAnswers a : q.getAnswers()) {
					arrayNodeName.add(mapper.createObjectNode().put("Aname",
							a.getName()));
				}
				arrayNodeId = mapper.createArrayNode();
				for (AnketaAnswers a : q.getAnswers()) {
					arrayNodeId.add(mapper.createObjectNode().put("AId",
							a.getId()));
				}
				questionNode.put("answersName", arrayNodeName);
				questionNode.put("answersId", arrayNodeId);
				questionsNode.add(questionNode);
			}

			// Alisher
			ArrayNode holyDatesNode = mapper.createArrayNode();
			ObjectNode holyDateNode = null;

			// System.out
			// .println("###################################################################################################");
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			// System.out
			// .println(dateFormat.format(date).toString().substring(0, 4)
			// + " "
			// + dateFormat.format(date).toString()
			// .substring(5, 7)
			// + " "
			// + dateFormat.format(date).toString()
			// .substring(8, 10));
			List<Weekend> weekends = queue
					.getEntityManager()
					.createQuery(
							"select w from" + " 	Weekend w" + " WHERE"
									+ "	w.start >= :date")
					.setParameter("date", date).getResultList();
			if (!weekends.isEmpty()) {
				// System.out.println("I am not NULL");
				for (Weekend weekend : weekends) {
					// System.out.println(weekend.getStart());
					holyDateNode = mapper.createObjectNode();
					holyDateNode.put("time", weekend.getStart().toString());
					holyDateNode.put("group", weekend.getGroup().getName());
					holyDateNode.put("delete_id", weekend.getId().toString());

					holyDatesNode.add(holyDateNode);
				}
			}
			// System.out
			// .println("###################################################################################################");

			rootNode.put("questions", questionsNode);
			
			// List<Group> groups_ = queue.getGroups();
			// rootNode.putAll(groups2JSON(groups_));
			ArrayNode arrayNode = queue.getGroups(user.getGroup());
			ObjectNode groups_ = mapper.createObjectNode();
			groups_.put("groups", arrayNode);
			rootNode.putAll(groups_);
			rootNode.put("weekends", holyDatesNode);
			// QLog.info(user.getGroup().getName());
			QLog.info("end");
			return rootNode.put("url", "/admin.html")
					.put("group", groups.get(0).getName())
					.put("lang", Language.toString(user.getLanguage()));
		}
		QLog.info("end");
		return mapper.createObjectNode()
				.put("url", "/admin.html")
				.put("group", "null")
				.put("lang", Language.toString(user.getLanguage()));
	}

	private ObjectNode initLocalAdmin(final User user, QueueWrapper queue)
			throws QueuePluginException {
		Group group = user.getGroup();
		ObjectNode rootNode = mapper.createObjectNode();

		if (group != null) {
			List<User> users = queue.fetchUsers(group.getName());
			rootNode.putAll(users2JSON(users));
			rootNode.put("username", user.getUsername());
			rootNode.put("group", group.getName());

			rootNode.put("videoPath", group.getVideoPath());
			rootNode.put("runningLine", group.getRunningLine());
			rootNode.put("totalLength", group.getLength());
			rootNode.put("paperLength", group.getPaperLength());

			return rootNode
					.put("url","/localAdmin.html")
					.put("group", group.getName())
					.put("lang", Language.toString(user.getLanguage()));
		}
		return mapper.createObjectNode()
				.put("url", "/admin.html")
				.put("group", "null")
				.put("lang", Language.toString(user.getLanguage()));
	}

	private ObjectNode initMonitorUnit(User user, QueueWrapper queue)
			throws QueuePluginException {
		QLog.info("start");
		ObjectNode rootNode = mapper.createObjectNode();

		QLog.info(user.getGroup().getParent().getName());

//		rootNode.put("url", "/" + user.getGroup().getParent() + "/secondMonitor.html");
		rootNode.put("url", "/secondMonitor.html");
		ObjectNode groupNode = mapper.createObjectNode();

		List<AnketaQuestions> questions = queue
				.getEntityManager()
				.createQuery(
						"select q from" + " 	AnketaQuestions q" + " where"
								+ "	q.group = :group)")
				.setParameter("group", user.getGroup().getParent())
				.getResultList();
		System.out.println(user.getGroup().getParent());
		System.out.println(questions.size()
				+ "questions size------------------");
		for (int i = 0; i < questions.size(); i++) {
			System.out.println(questions.get(i));
		}

		ArrayNode questionsNode = mapper.createArrayNode();
		ObjectNode questionNode = null;
		ArrayNode arrayNodeName = null;
		ArrayNode arrayNodeId = null;
		for (AnketaQuestions q : questions) {
			questionNode = mapper.createObjectNode();
			questionNode.put("id", q.getId());
			questionNode.put("question", q.getName());
			arrayNodeName = mapper.createArrayNode();
			for (AnketaAnswers a : q.getAnswers()) {
				arrayNodeName.add(mapper.createObjectNode().put("Aname",
						a.getName()));
			}
			arrayNodeId = mapper.createArrayNode();
			for (AnketaAnswers a : q.getAnswers()) {
				arrayNodeId
						.add(mapper.createObjectNode().put("AId", a.getId()));
			}
			questionNode.put("answersName", arrayNodeName);
			questionNode.put("answersId", arrayNodeId);
			questionsNode.add(questionNode);
		}
		groupNode.put("questions", questionsNode);
		groupNode.put("id", user.getGroup().getName());
		groupNode.put(
				"ru",
				Messages.get("group.#0.name_RU", user.getGroup().getParent()
						.getName()));
		groupNode.put(
				"rushort",
				Messages.get("group.#0.shortname_RU", user.getGroup()
						.getParent().getName()));
		rootNode.put("group", groupNode);
		QLog.info("end");
		return rootNode;
	}

	private ObjectNode initKiosk(User user, QueueWrapper queue)
			throws QueuePluginException {
		QLog.info("started");
		ObjectNode rootNode = mapper.createObjectNode();
		String laneTree = user.getData();
		if (laneTree != null) {
			rootNode.put("tree", laneTree);
		}
		List<Lane> lanes = queue.getLanes(user.getGroup().getName());
		// rootNode.putAll(lanes2JSON(lanes));

		ArrayNode arrayNode = mapper.createArrayNode();
		for (Lane lane : lanes) {
			long waitingClients = (Long) queue
					.getEntityManager()
					.createQuery(
							"select count(t) " + "from "
									+ "	Token t "
									+ "	join t.ticket tt "
									+ "where tt.group = :group "
									+
									// "	and cast(tt.create as time) < current_time() "
									// +
									"	and t.status in ('WAITING','POSTPONED') "
									+ "	and t.laneName = :laneName ")
					.setParameter("group", lane.getGroup())
					.setParameter("laneName", lane.getUsername())
					.getSingleResult();

			long waitingReservedClients = 0;
			// Sultan
			// long waitingReservedClients = (Long)
			// queue.getEntityManager().createQuery(
			// "select count(t) " +
			// "from " +
			// "	Token t " +
			// "	join t.ticket tt " +
			// "where tt.group = :group " +
			// "	and cast(tt.create as time) < current_time() " +
			// "	and t.status in ('WAITING','POSTPONED') " +
			// "	and t.laneName = :laneName " +
			// "	and t.priority = 10")
			// .setParameter("group", lane.getGroup())
			// .setParameter("laneName", lane.getUsername())
			// .getSingleResult();
			String kz = "";
			String ru = "";
			String en = "";
			if (Messages.get("lane.#0.name_RU", lane.getUsername()).equals(
					"lane." + lane.getUsername() + ".name_RU")) {
				ru = "";
			} else {
				ru = Messages.get("lane.#0.name_RU", lane.getUsername());
			}
			if (Messages.get("lane.#0.name_KK", lane.getUsername()).equals(
					"lane." + lane.getUsername() + ".name_KK")) {
				kz = "";
			} else {
				kz = Messages.get("lane.#0.name_KK", lane.getUsername());
			}
			if (Messages.get("lane.#0.name_EN", lane.getUsername()).equals(
					"lane." + lane.getUsername() + ".name_EN")) {
				en = "";
			} else {
				en = Messages.get("lane.#0.name_EN", lane.getUsername());
			}
			arrayNode.add(mapper.createObjectNode().put("kk", kz).put("ru", ru)
					.put("en", en).put("count", waitingClients)
					.put("reserved", waitingReservedClients)
					.put("id", lane.getUsername()));

		}
		ObjectNode lanesJSONNode = mapper.createObjectNode();
		lanesJSONNode.put("lanes", arrayNode);
		rootNode.putAll(lanesJSONNode);
		// rootNode.put("template",
		// user.getGroup().getParent().getKioskTemplate());
//		rootNode.put("url", "/" + user.getGroup().getParent() + "/kiosk.html");
		rootNode.put("url", "/kiosk.html");
		rootNode.put("ticketLogo", user.getGroup().getParent().getTicketLogo());
		// rootNode.put("kioskLogo", user.getGroup().getKioskLogo());

		ArrayNode reservationsNode = mapper.createArrayNode();
		List<Reservation> reservations = Reservation.fetchReservations(user
				.getGroup().getName(), queue.getEntityManager());
		for (Reservation r : reservations) {
			ArrayNode lanesNode = mapper.createArrayNode();
			for (Lane lane : r.getLanes()) {
				lanesNode.add(lane.getUsername());
			}
			ObjectNode reservationNode = mapper.createObjectNode();
			reservationNode.put("id", r.getPersonalId());
			reservationNode.put("date",
					new SimpleDateFormat("dd.MM HH:mm").format(r.getDate()));
			reservationNode.put("lanes", lanesNode);

			reservationsNode.add(reservationNode);
		}
		rootNode.put("reservations", reservationsNode);

		try {
			QLog.info("initKiosk "
					+ mapper.writeValueAsString(reservationsNode));
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String kioskLogo = user.getGroup().getParent().getKioskLogo();
		if (kioskLogo != null && !kioskLogo.trim().isEmpty()) {
			rootNode.put("logo", kioskLogo);
		}
		ObjectNode groupNode = mapper.createObjectNode();
		groupNode.put("id", user.getGroup().getName());
		groupNode.put(
				"ru",
				Messages.get("group.#0.name_RU", user.getGroup().getParent()
						.getName()));
		groupNode.put(
				"rushort",
				Messages.get("group.#0.shortname_RU", user.getGroup()
						.getParent().getName()));
		rootNode.put("group", groupNode);
		QLog.info("end");
		return rootNode;
	}

	private ObjectNode getUnitStatistics(Unit unit, QueueWrapper queue) {
		QLog.info("start");
		List<Token> tokens = queue
				.getEntityManager()
				.createQuery(
						"select t from" + " 	Token t join fetch t.ticket tt"
								+ " where" + "	(t.target=:unit"
								+ "	or t.target in (:lanes))"
								+ "	and cast(tt.create as date)=current_date()"
								+ "order by t.create")
				.setParameter("unit", unit)
				.setParameter("lanes", unit.getLanes()).getResultList();
		Map<String, HashMap<String, Integer>> laneCountMap = new HashMap<String, HashMap<String, Integer>>();
		for (Token t : tokens) {
			if (t.getStatus().equals(Status.WAITING)
					|| t.getStatus().equals(Status.ENDED)
					|| t.getStatus().equals(Status.POSTPONED)
					|| t.getStatus().equals(Status.TRANSFERRED)) {
				HashMap<String, Integer> statusMap = laneCountMap.get(t
						.getLaneName());
				if (statusMap == null) {
					statusMap = new HashMap<String, Integer>();
					laneCountMap.put(t.getLaneName(), statusMap);
				}
				Integer count = statusMap.get(t.getStatus().toString()
						.toLowerCase());
				if (count == null) {
					count = 1;
				} else {
					count += 1;
				}
				statusMap.put(t.getStatus().toString().toLowerCase(), count);
			}
		}
		ArrayNode laneStatsNode = mapper.createArrayNode();
		for (Lane lane : unit.getLanes()) {
			ObjectNode laneNode = mapper.createObjectNode();
			laneNode.put("lane", lane.getUsername());
			Integer waiting = null, ended = null, postponed = null, transferred = null;
			if (laneCountMap.containsKey(lane.getUsername())) {
				HashMap<String, Integer> statusMap = laneCountMap.get(lane
						.getUsername());
				waiting = statusMap.get("waiting");
				postponed = statusMap.get("postponed");
				transferred = statusMap.get("transferred");
				ended = statusMap.get("ended");
			}
			laneNode.put("waiting", waiting != null ? waiting : 0);
			laneNode.put("ended", ended != null ? ended : 0);
			laneNode.put("postponed", postponed != null ? postponed : 0);
			laneNode.put("transferred", transferred != null ? transferred : 0);
			laneStatsNode.add(laneNode);
		}
		ObjectNode resultNode = mapper.createObjectNode();

		Date now = new Date();
		SimpleDateFormat formatDMY = new SimpleDateFormat("dd.MM.yyyy");
		String dateNow = formatDMY.format(now);

		String[] dateFromArray = dateNow.split("\\.");

		// rootNode.put("template",unit.getGroup().getUnitTemplate());TODO
		List<Token> tokens1 = queue
				.getEntityManager()
				.createQuery(
						"select t from "
								+ " 	Token t join fetch t.ticket tt "
								+ " where year(tt.create) = :yearFrom "
								+ "  and month(tt.create) = :monthFrom "
								+ "  and day(tt.create) = :dayFrom "
								+ "  and t.status in ('WAITING','CALLED','STARTED')"
								+ "order by t.create")
				.setParameter("dayFrom", Integer.parseInt(dateFromArray[0]))
				.setParameter("monthFrom", Integer.parseInt(dateFromArray[1]))
				.setParameter("yearFrom", Integer.parseInt(dateFromArray[2]))
				.getResultList();

		ArrayNode tokensNode = mapper.createArrayNode();
		for (Token t : tokens1) {
			ObjectNode tokenNode = mapper.createObjectNode();
			tokenNode
					.put("ticket", t.getTicket().getCode())
					.put("id", t.getId())
					.put("lane",
							Messages.get("lane.#0.name_RU", t.getLaneName()))
					.put("laneId", t.getLaneName());
			tokensNode.add(tokenNode);
		}
		resultNode.put("tokens", tokensNode);

		resultNode.put("stats", laneStatsNode);
		QLog.info("end");
		return resultNode;
	}

	private ObjectNode initUnit(User user, QueueWrapper queue)
			throws QueuePluginException {
		QLog.info("start");
		Unit unit = (Unit) user;
		
		String firstName = unit.getFirstName() + " "
				+ unit.getLastName();
		
		Token token = queue.current(unit);
		System.out.println(token + " init unit");
		List<Token> postponedTokens = queue
				.getEntityManager()
				.createQuery(
						"select t from" + " 	Token t join fetch t.ticket tt"
								+ " where" + "	t.target=:unit"
								+ "	and t.status='POSTPONED'"
								+ "	and cast(tt.create as date)=current_date()")
				.setParameter("unit", unit).getResultList();

		ObjectNode rootNode = mapper.createObjectNode();
		ArrayNode postponedTokensNode = mapper.createArrayNode();
		for (Token t : postponedTokens) {
			ObjectNode postponedNode = mapper.createObjectNode();
			postponedNode.put("ticket", t.getTicket().getCode());
			postponedNode.put("id", t.getId());
			postponedNode.put("lane_text",
					Messages.get("lane.#0.name_RU", t.getLaneName()));
			postponedNode.put("time", TimeUtils.toTime(t.getPostpone()));
			postponedNode.put("called", TimeUtils.toTime(t.getCall()));
			postponedTokensNode.add(postponedNode);
		}
		if (postponedTokensNode.size() > 0) {
			rootNode.put("postponed", postponedTokensNode);
		}

		ObjectNode tokenNode = mapper.createObjectNode();
		if (token != null) {

			tokenNode.put("ticket", token.getTicket().getCode());
			tokenNode.put("id", token.getId());
			tokenNode.put("lane", token.getLaneName());
			tokenNode.put("called", TimeUtils.toTime(token.getCall()));
			Language lang = user.getLanguage();
			if (lang == Language.RU)
				tokenNode.put("laneText",
						Messages.get("lane.#0.name_RU", token.getLaneName()));
			else if (lang == Language.KK)
				tokenNode.put("laneText",
						Messages.get("lane.#0.name_KK", token.getLaneName()));
			else if (lang == Language.EN)
				tokenNode.put("laneText",
						Messages.get("lane.#0.name_EN", token.getLaneName()));

			if (token.getStatus().equals(Status.STARTED)) {
				tokenNode.put("started", TimeUtils.toTime(token.getStart()));
			}
			rootNode.put("current", tokenNode);
		}
		rootNode.put("firstname", firstName);
		rootNode.put("lang", Language.toString(unit.getLanguage()));
		rootNode.putAll(units2JSON(queue.getUnits(unit.getGroup().getName())));
		rootNode.putAll(lanes2JSON(queue.getLanes(unit.getGroup().getName())));
		// Sultan
		ObjectNode unitsNode = mapper.createObjectNode();
		ArrayNode arrayNode = mapper.createArrayNode();
		List<Unit> units = queue.getUnits(unit.getGroup().getName());
		for (Unit u : units) {
			for (Lane lane : u.getLanes()) {
				arrayNode.add(mapper.createObjectNode()
						.put("lane", lane.getUsername())
						.put("unit", u.getUsername())
						.put("name", u.getLastName() + " " + u.getFirstName()));
			}
		}
		unitsNode.put("unit_lanes", arrayNode);
		rootNode.putAll(unitsNode);

//		rootNode.put("url", "/" + user.getGroup().getParent() + "/unit.html");
		rootNode.put("url", "/unit.html");
		QLog.info("end");
		return rootNode;
	}

	private ObjectNode lanes2JSON(List<Lane> laneList) {
		QLog.info("start");
		ArrayNode arrayNode = mapper.createArrayNode();
		for (Lane lane : laneList) {
//			System.out.println(lane.getUsername());
			arrayNode
					.add(mapper
							.createObjectNode()
							.put("kk",
									Messages.get("lane.#0.name_KK",
											lane.getUsername()))
							.put("ru",
									Messages.get("lane.#0.name_RU",
											lane.getUsername()))
							.put("en",
									Messages.get("lane.#0.name_EN",
											lane.getUsername()))
							.put("id", lane.getUsername()));
		}
		ObjectNode lanesNode = mapper.createObjectNode();
		lanesNode.put("lanes", arrayNode);
		QLog.info("end");
		return lanesNode;
	}

	private ObjectNode units2JSON(List<Unit> unitList) {
		QLog.info("start");
		ArrayNode arrayNode = mapper.createArrayNode();
		for (Unit unit : unitList) {
			arrayNode
					.add(mapper
							.createObjectNode()
							.put("unit", unit.getUsername())
							.put("workStart", unit.getWorkFinish())
							.put("workFinish", unit.getWorkStart())
							.put("name",
									unit.getLastName() + " "
											+ unit.getFirstName())
							.put("window", unit.getWindow()));
		}
		ObjectNode unitsNode = mapper.createObjectNode();
		unitsNode.put("units", arrayNode);
		QLog.info("end");
		return unitsNode;
	}

	private ObjectNode units2JSON2(List<Unit> unitList) {
		QLog.info("start");
		ArrayNode arrayNode = mapper.createArrayNode();
		ObjectNode unitsNode = mapper.createObjectNode();
		for (Unit unit : unitList) {
			ObjectNode unitsNode2 = mapper.createObjectNode();
			// unit.getLanes()
			// unitsNode.putAll(lanes2JSON(new
			// List<Lane>().addAll(unit.getLanes()));
			System.out.println(unit.getUsername() + " ");
			ArrayNode arrayNode1 = mapper.createArrayNode();
			for (Lane lane : unit.getLanes()) {
				arrayNode1.add(mapper
						.createObjectNode()
						.put("kk",
								Messages.get("lane.#0.name_KK",
										lane.getUsername()))
						.put("ru",
								Messages.get("lane.#0.name_RU",
										lane.getUsername()))
						.put("id", lane.getUsername()));
				System.out.print(lane.getUsername() + " ");
			}
			System.out.println();
			unitsNode2.put("lanes", arrayNode1);
			arrayNode
					.add(mapper
							.createObjectNode()
							.put("unit", unit.getUsername())
							.put("name",
									unit.getLastName() + " "
											+ unit.getFirstName())
							.put("window", unit.getWindow())
							.put("lanes", unitsNode2));
		}
		unitsNode.put("units", arrayNode);
		QLog.info("end");
		return unitsNode;
	}

	private ObjectNode initDashboard(User user, QueueWrapper queue)
			throws QueuePluginException {
		QLog.info("start");
		Dashboard dashboard = (Dashboard) user;
		List<String> laneNameList = new ArrayList<String>();
		for (Lane lane : dashboard.getDashboardLanes()) {
			laneNameList.add(lane.getUsername());
		}
		List<Token> tokens = queue
				.getEntityManager()
				.createQuery(
						"from "
								+ "	Token t "
								+ "	join fetch t.target u"
								+ "	join fetch t.ticket tt "
								+ "where "
								+ "	tt.group = :group "
								+ "	and cast(tt.create as date) = current_date "
								+ "	and t.status in ('RECALLED','CALLED','STARTED') "
								+ "	and t.laneName in (:lanes) " + "order by "
								+ "	t.call desc")
				.setParameter("group", dashboard.getGroup())
				.setParameter("lanes", laneNameList).setMaxResults(8)
				.getResultList();

		ObjectNode rootNode = mapper.createObjectNode();
		ArrayNode tokensNode = mapper.createArrayNode();
		System.out.println(tokens.size()
				+ "-------------------------------------------");
		for (Token token : tokens) {
			System.out.println(token);
			Unit unit = (Unit) token.getTarget();
			Lane lane = queue.getEntityManager().find(Lane.class,
					token.getLaneName());
			ObjectNode tokenNode = mapper.createObjectNode();
			tokenNode.put("ticket", token.getTicket().getCode());
			tokenNode.put("unit", unit.getUsername());
			tokenNode.put("tokenId", token.getId());
			tokenNode.put("color", lane.getColor());
			tokenNode.put("isTicketReserved", token.getPriority());
			tokenNode.put("window", unit.getWindow());
			tokensNode.add(tokenNode);
		}
		// rootNode.put("template",user.getGroup().getDashboardTemplate());TODO
//		rootNode.put("url", "/" + user.getGroup().getParent() + "/dashboard2.html");
		rootNode.put("url", "/dashboard2.html");
		rootNode.put("tokens", tokensNode);
		QLog.info(user.getGroup() + " " + user.getGroup().getVideoPath());
		if (user.getGroup().getVideoPath() != null) {
			rootNode.put("videoPath", user.getGroup().getVideoPath());
		}
		QLog.info(user.getGroup() + " " + user.getGroup().getRunningLine());
		if (user.getGroup().getRunningLine() != null) {
			rootNode.put("runningLine", user.getGroup().getRunningLine());
		}
		rootNode.put(
				"group",
				Messages.get("group.#0.name_RU", user.getGroup().getParent()
						.getName()));
		QLog.info("end");
		return rootNode;
	}

	// TODO переделать чтобы названия линий не попадали в список а отдельно один
	// раз приходили
	private ObjectNode initMonitor(User user, QueueWrapper queue)
			throws QueuePluginException {
		QLog.info("start");
		List<Unit> units = queue
				.getEntityManager()
				.createQuery(
						"select distinct u from"
								+ " 	Unit u join fetch u.lanes l" + " where"
								+ " 	u.status='ENABLED'"
								+ "	and u.group.id=:groupName")
				.setParameter("groupName", user.getGroup().getName())
				.getResultList();

		List<Token> tokens = queue
				.getEntityManager()
				.createQuery(
						"select t from" + " Token t join fetch t.ticket tt"
								+ " where"
								+ "	cast(tt.create as date)=current_date()")
				.getResultList();

		// this list is for filling other two maps of data
		ObjectNode rootNode = mapper.createObjectNode();

		// this map contains all data about units
		Map<String, HashMap<String, Object>> unitMap = new HashMap<String, HashMap<String, Object>>();
		// this map contains all data about lanes
		Map<String, HashMap<String, Integer>> laneMap = new HashMap<String, HashMap<String, Integer>>();

		int countReserved = 0;
		for (Token token : tokens) {
			HashMap<String, Object> dataMap = unitMap.get(token.getTarget()
					.getUsername());
			if (dataMap == null) {
				dataMap = new HashMap<String, Object>();
				if (token.getTarget() instanceof Unit) {
					unitMap.put(token.getTarget().getUsername(), dataMap);
				}
			}
			if (token.getStatus().equals(Status.CALLED)
					|| token.getStatus().equals(Status.STARTED)) {
				dataMap.put("called", TimeUtils.toTime(token.getCall()));
				if (token.getStart() != null) {
					dataMap.put("started", TimeUtils.toTime(token.getStart()));
				}
				dataMap.put("lane", token.getLaneName());
				dataMap.put("laneText",
						Messages.get("lane.#0.name_RU", token.getLaneName()));
				dataMap.put("ticketCode", token.getTicket().getCode());
			} else {// counting statistical information about statuses for unit
				List<String> list = (List<String>) dataMap.get(token
						.getStatus().toString().toLowerCase());
				if (list == null) {
					list = new ArrayList<String>();
					dataMap.put(token.getStatus().toString().toLowerCase(),
							list);
				}
				// Sultan
				boolean isTicketReserved = false;
				if (token.getPriority() == 10) {
					isTicketReserved = true;
					countReserved++;
				}

				list.add(token.getLaneName() + ","
						+ token.getTicket().getCode() + ","
						+ TimeUtils.toTime(token.getStart()) + ","
						+ TimeUtils.toTime(token.getEnd()) + ","
						+ TimeUtils.toTime(token.getCall()) + ","
						+ token.getMark() + "," + token.getId() + ","
						+ isTicketReserved);
			}
			// begin of lane stats counting
			HashMap<String, Integer> laneStatusMap = laneMap.get(token
					.getLaneName());
			if (laneStatusMap == null) {
				laneStatusMap = new HashMap<String, Integer>();
				laneStatusMap
						.put(token.getStatus().toString().toLowerCase(), 1);
				laneMap.put(token.getLaneName(), laneStatusMap);
			} else {
				Integer count = laneStatusMap.get(token.getStatus().toString()
						.toLowerCase());
				if (count == null) {
					count = 0;
				}
				laneStatusMap.put(token.getStatus().toString().toLowerCase(),
						count + 1);
			}
			Integer createdCount = laneStatusMap.get("created");
			if (createdCount == null) {
				createdCount = 0;
			}
			laneStatusMap.put("created", createdCount + 1);
		}
		ArrayNode unitsNode = mapper.createArrayNode();
		for (Unit unit : units) {
			ObjectNode unitNode = mapper.createObjectNode();
			unitNode.put("username", unit.getUsername());
			unitNode.put("firstname", unit.getFirstName());
			unitNode.put("lastname", unit.getLastName());
			unitNode.put("window", unit.getWindow());

			ArrayNode lanesNode = mapper.createArrayNode();
			for (Lane lane : unit.getLanes()) {
				ObjectNode laneNode = mapper.createObjectNode();
				laneNode.put("lane", lane.getUsername()).put("laneText",
						Messages.get("lane.#0.name_RU", lane.getUsername()));
				lanesNode.add(laneNode);
			}
			unitNode.put("lanes", lanesNode);

			if (unitMap.get(unit.getUsername()) != null) {
				Map<String, Object> properties = unitMap
						.get(unit.getUsername());
				for (Entry<String, Object> prop : properties.entrySet()) {
					if (prop.getKey().equalsIgnoreCase("lane")) {
						unitNode.put("lane", prop.getValue().toString());
					} else if (prop.getKey().equalsIgnoreCase("laneText")) {
						unitNode.put("laneText", prop.getValue().toString());// TODO
																				// remove
					} else if (prop.getKey().equalsIgnoreCase("ticketCode")) {
						unitNode.put("ticketCode", prop.getValue().toString());
					} else if (prop.getKey().equalsIgnoreCase("started")
							|| prop.getKey().equalsIgnoreCase("called")) {
						unitNode.put(prop.getKey(), prop.getValue().toString());
					} else {
						ArrayNode lanesStatsNode = mapper.createArrayNode();
						List<String> list = (List<String>) prop.getValue();
						if (list != null) {
							for (String dataStr : list) {
								String dataArr[] = dataStr.split(",");// laneName,ticketCode,start,end,call
								ObjectNode laneStatNode = mapper
										.createObjectNode();

								laneStatNode
										.put("lane", dataArr[0])
										.put("ticket", dataArr[1])
										.put("start", dataArr[2])
										.put("end", dataArr[3])
										.put("call", dataArr[4])
										.put("mark",
												Integer.parseInt(dataArr[5]))
										.put("tokenId", dataArr[6])
										.put("isTicketReserved",
												Boolean.parseBoolean(dataArr[7]))
										.put("laneText",
												Messages.get("lane.#0.name_RU",
														dataArr[0]));
								lanesStatsNode.add(laneStatNode);
							}
						}
						unitNode.put(prop.getKey(), lanesStatsNode);
					}
				}
			}
			if (!unitNode.has("called")) {
				unitNode.put("called", mapper.createArrayNode());
			}
			if (!unitNode.has("started")) {
				unitNode.put("started", mapper.createArrayNode());
			}
			if (!unitNode.has("ended")) {
				unitNode.put("ended", mapper.createArrayNode());
			}
			if (!unitNode.has("missed")) {
				unitNode.put("missed", mapper.createArrayNode());
			}
			if (!unitNode.has("transferred")) {
				unitNode.put("transferred", mapper.createArrayNode());
			}

			List<Token> tokenUnit = queue
					.getEntityManager()
					.createQuery(
							"select t "
									+ "from Token t "
									+ "join t.ticket tt "
									+ "join t.target u "
									+ "where cast(tt.create as date) = current_date() "
									+ "  and u.username = :username"
									+ " order by t.start")
					.setParameter("username", unit.getUsername())
					.getResultList();

			int tokenAll = tokenUnit.size(), markedAll = 0;
			int countMarkBad = 0, countMarkNorm = 0, countMarkGood = 0, countMarkBest = 0, countMarkVeryBad = 0;

			double markAverage = 0;
			for (Token token : tokenUnit) {
				// QLog.info("Token of Unit " + token.getTarget().getUsername()
				// + " mark " + token.getMark());

				if (token.getMark() != 0 && token.getMark() != 2)
					markAverage += token.getMark();

				switch (token.getMark()) {
				case 1:
					countMarkVeryBad++;
					break;
				case 2:
					countMarkBad++;
					break;
				case 3:
					countMarkNorm++;
					break;
				case 4:
					countMarkGood++;
					break;
				case 5:
					countMarkBest++;
					break;
				}
			}

			markedAll = countMarkBad + countMarkNorm + countMarkGood
					+ countMarkVeryBad + countMarkBest;
			if (markedAll > 0)
				markAverage = markAverage / markedAll;

			// QLog.info("markAverage " + markAverage + " b " + countMarkBad +
			// " n " +countMarkNorm + " g " +countMarkGood);
			unitNode.put("tokenAll", tokenAll);
			unitNode.put("countReserved", countReserved);
			unitNode.put("countMarkBad", countMarkBad);
			unitNode.put("countMarkVeryBad", countMarkVeryBad);
			unitNode.put("countMarkBest", countMarkBest);
			unitNode.put("countMarkNorm", countMarkNorm);
			unitNode.put("countMarkGood", countMarkGood);
			unitNode.put(
					"markAverage",
					(markAverage == 0) ? "0.000" : String.format("%.3g%n",
							markAverage));
			unitsNode.add(unitNode);
		}
		ArrayNode lanesNode = mapper.createArrayNode();
		for (Lane lane : queue.getLanes(user.getGroup().getName())) {
			ObjectNode rowNode = mapper.createObjectNode();
			Map<String, Integer> statusMap = laneMap.get(lane.getUsername());
			if (statusMap == null) {
				statusMap = new HashMap<String, Integer>();
			}
			Integer created = statusMap.get("created");
			Integer started = statusMap.get("started");
			Integer called = statusMap.get("called");
			Integer ended = statusMap.get("ended");
			Integer missed = statusMap.get("missed");
			Integer transferred = statusMap.get("transferred");
			if (created == null) {
				created = 0;
			}
			if (started == null) {
				started = 0;
			}
			if (called == null) {
				called = 0;
			}
			if (ended == null) {
				ended = 0;
			}
			if (missed == null) {
				missed = 0;
			}
			if (transferred == null) {
				transferred = 0;
			}

			int countLaneReserved = 0;

			List<Token> list = queue
					.getEntityManager()
					.createQuery(
							"select t "
									+ "from Token t "
									+ "join t.ticket tt "
									+ "where tt.group.name = :group "
									+ "and cast(tt.create as date) = current_date() "
									+ "and t.laneName = :laneName "
									+ "order by t.start asc")
					.setParameter("group", user.getGroup().getName())
					.setParameter("laneName", lane.getUsername())
					.getResultList();

			QLog.info("List is empty " + list.isEmpty() + " and size "
					+ list.size());
			ArrayNode arrayNode = mapper.createArrayNode();

			for (Token t : list) {
				if (t.getTarget() instanceof Unit) {
					boolean isTicketReserved = Reservation.isTicketReserved(t
							.getTicket().getGroup().getName(),
							queue.getEntityManager(), t.getTicket().getId());
					if (isTicketReserved)
						countLaneReserved++;

					Unit unit = (Unit) t.getTarget();

					arrayNode.add(mapper
							.createObjectNode()
							.put("ticket", t.getTicket().getCode())
							.put("start", TimeUtils.toTime(t.getStart()))
							.put("call", TimeUtils.toTime(t.getCall()))
							.put("end", TimeUtils.toTime(t.getEnd()))
							.put("mark", t.getMark())
							.put("tokenId", t.getId())
							.put("isTicketReserved", isTicketReserved)
							.put("unitRealName",
									unit.getFirstName() + " "
											+ unit.getLastName()));
				}
			}

			rowNode.put("laneText",
					Messages.get("lane.#0.name_RU", lane.getUsername()))
					.put("created", created).put("called", called)
					.put("started", started).put("ended", ended)
					.put("missed", missed)
					.put("countReserved", countLaneReserved)
					.put("transferred", transferred)
					.put("lane", lane.getUsername()).put("history", arrayNode);
			lanesNode.add(rowNode);
		}
		rootNode.put("unitStats", unitsNode);
		rootNode.put("laneStats", lanesNode);
//		rootNode.put("url", "/" + user.getGroup().getParent() + "/monitor.html");
		rootNode.put("url", "/monitor.html");
		rootNode.put("lang", Language.toString(user.getLanguage()));
		rootNode.put("username", user.getUsername());

		// ticket
		Date now = new Date();
		SimpleDateFormat formatDMY = new SimpleDateFormat("dd.MM.yyyy");
		String dateNow = formatDMY.format(now);

		String[] dateFromArray = dateNow.split("\\.");
		List<Token> ticket_tokens = queue
				.getEntityManager()
				.createQuery(
						"select t "
								+ "from Token t "
								+ "join t.ticket tt "
								+ "where tt.group.name = :group "
								+ "and year(tt.create) = :yearFrom "
								+ "  and month(tt.create) = :monthFrom "
								+ "  and day(tt.create) = :dayFrom "
								+ "  and t.status in ('WAITING','CALLED','STARTED')"
								+ "order by t.start, t.call, t.create asc")
				.setParameter("group", user.getGroup().getName())
				.setParameter("dayFrom", Integer.parseInt(dateFromArray[0]))
				.setParameter("monthFrom", Integer.parseInt(dateFromArray[1]))
				.setParameter("yearFrom", Integer.parseInt(dateFromArray[2]))
				.getResultList();

		ArrayNode tokensNode = mapper.createArrayNode();
		for (Token t : ticket_tokens) {
			ObjectNode tokenNode = mapper.createObjectNode();
			tokenNode
					.put("ticket", t.getTicket().getCode())
					.put("tokenId", t.getId())
					.put("start", TimeUtils.toTime(t.getStart()))
					.put("call", TimeUtils.toTime(t.getCall()))
					.put("laneRu",
							Messages.get("lane.#0.name_RU", t.getLaneName()))
					.put("lane", t.getLaneName())
					.put("status", t.getStatus().toString())
					.put("priority", t.getPriority())
					.put("window", t.getTarget().getUsername())
					.put("waiting", TimeUtils.toTime(t.getCreate()));
			tokensNode.add(tokenNode);
		}

		rootNode.put("tokens", tokensNode);
		int totalP = user.getGroup().getLength();
		int singleP = user.getGroup().getPaperLength();
		if (singleP > 0 && totalP > 0) {
			int tokenLeft = totalP * 100 / singleP
					- user.getGroup().getCounter();
			rootNode.put("tokenLeft", tokenLeft);
		}

		// statistics

		String dateRepresent = dateNow;
		rootNode.put("dateRepresent", dateRepresent);
		QLog.info("end");
		return rootNode;
	}

	// private ObjectNode initStatistics(User user, QueueWrapper queue,
	// String dateFrom, String dateTo) throws QueuePluginException {
	// List<Unit> units = queue
	// .getEntityManager()
	// .createQuery(
	// "select distinct u from"
	// + " 	Unit u join fetch u.lanes l" + " where"
	// + " 	u.status='ENABLED'"
	// + "	and u.group.id=:groupName")
	// .setParameter("groupName", user.getGroup().getName())
	// .getResultList();
	//
	// QLog.info("dateFrom " + dateFrom);
	// QLog.info("dateTo " + dateTo);
	//
	// String[] dateFromArray = dateFrom.split("\\.");
	// String[] dateToArray = dateTo.split("\\.");
	//
	// QLog.info("dateFromArray");
	// for (String s : dateFromArray)
	// QLog.info(s);
	//
	// QLog.info("dateToArray");
	// for (String s : dateToArray)
	// QLog.info(s);
	//
	// List<Token> tokens = queue
	// .getEntityManager()
	// .createQuery(
	// "select t from " + " 	Token t join fetch t.ticket tt "
	// + " where year(tt.create) >= :yearFrom "
	// + "  and month(tt.create) >= :monthFrom "
	// + "  and day(tt.create) >= :dayFrom "
	// + "  and year(tt.create) <= :yearTo "
	// + "  and month(tt.create) <= :monthTo "
	// + "  and day(tt.create) <= :dayTo "
	// + "order by t.start")
	// .setParameter("dayFrom", Integer.parseInt(dateFromArray[0]))
	// .setParameter("monthFrom", Integer.parseInt(dateFromArray[1]))
	// .setParameter("yearFrom", Integer.parseInt(dateFromArray[2]))
	// .setParameter("dayTo", Integer.parseInt(dateToArray[0]))
	// .setParameter("monthTo", Integer.parseInt(dateToArray[1]))
	// .setParameter("yearTo", Integer.parseInt(dateToArray[2]))
	// .getResultList();
	//
	// // for (Token t: tokens)
	// // QLog.info("tokens are not empty " + t.getCreate().toString() );
	//
	// // this list is for filling other two maps of data
	// ObjectNode rootNode = mapper.createObjectNode();
	//
	// // this map contains all data about units
	// Map<String, HashMap<String, Object>> unitMap = new HashMap<String,
	// HashMap<String, Object>>();
	// // this map contains all data about lanes
	// Map<String, HashMap<String, Integer>> laneMap = new HashMap<String,
	// HashMap<String, Integer>>();
	//
	// int countReserved = 0;
	// for (Token token : tokens) {
	// HashMap<String, Object> dataMap = unitMap.get(token.getTarget()
	// .getUsername());
	// if (dataMap == null) {
	// dataMap = new HashMap<String, Object>();
	// if (token.getTarget() instanceof Unit) {
	// unitMap.put(token.getTarget().getUsername(), dataMap);
	// }
	// }
	// if (token.getStatus().equals(Status.CALLED)
	// || token.getStatus().equals(Status.STARTED)) {
	// dataMap.put("called", TimeUtils.toTime(token.getCall()));
	// if (token.getStart() != null) {
	// dataMap.put("started", TimeUtils.toTime(token.getStart()));
	// }
	// dataMap.put("lane", token.getLaneName());
	// dataMap.put("laneText",
	// Messages.get("lane.#0.name_RU", token.getLaneName()));
	// dataMap.put("ticketCode", token.getTicket().getCode());
	// } else {// counting statistical information about statuses for unit
	// List<String> list = (List<String>) dataMap.get(token
	// .getStatus().toString().toLowerCase());
	// if (list == null) {
	// list = new ArrayList<String>();
	// dataMap.put(token.getStatus().toString().toLowerCase(),
	// list);
	// }
	//
	// boolean isTicketReserved = Reservation.isTicketReserved(token
	// .getTicket().getGroup().getName(),
	// queue.getEntityManager(), token.getTicket().getId());
	//
	// if (isTicketReserved)
	// countReserved++;
	//
	// // QLog.info("initMonitor isTicketReserved : " +
	// // isTicketReserved);
	//
	// list.add(token.getLaneName() + ","
	// + token.getTicket().getCode() + ","
	// + TimeUtils.toTime(token.getStart()) + ","
	// + TimeUtils.toTime(token.getEnd()) + ","
	// + TimeUtils.toTime(token.getCall()) + ","
	// + token.getMark() + "," + token.getId() + ","
	// + isTicketReserved);
	// }
	// // begin of lane stats counting
	// HashMap<String, Integer> laneStatusMap = laneMap.get(token
	// .getLaneName());
	// if (laneStatusMap == null) {
	// laneStatusMap = new HashMap<String, Integer>();
	// laneStatusMap
	// .put(token.getStatus().toString().toLowerCase(), 1);
	// laneMap.put(token.getLaneName(), laneStatusMap);
	// } else {
	// Integer count = laneStatusMap.get(token.getStatus().toString()
	// .toLowerCase());
	// if (count == null) {
	// count = 0;
	// }
	// laneStatusMap.put(token.getStatus().toString().toLowerCase(),
	// count + 1);
	// }
	// Integer createdCount = laneStatusMap.get("created");
	// if (createdCount == null) {
	// createdCount = 0;
	// }
	// laneStatusMap.put("created", createdCount + 1);
	// }
	// ArrayNode unitsNode = mapper.createArrayNode();
	// for (Unit unit : units) {
	// ObjectNode unitNode = mapper.createObjectNode();
	// unitNode.put("username", unit.getUsername());
	// unitNode.put("firstname", unit.getFirstName());
	// unitNode.put("lastname", unit.getLastName());
	// unitNode.put("window", unit.getWindow());
	//
	// ArrayNode lanesNode = mapper.createArrayNode();
	// for (Lane lane : unit.getLanes()) {
	// ObjectNode laneNode = mapper.createObjectNode();
	// laneNode.put("lane", lane.getUsername()).put("laneText",
	// Messages.get("lane.#0.name_RU", lane.getUsername()));
	// lanesNode.add(laneNode);
	// }
	// unitNode.put("lanes", lanesNode);
	//
	// if (unitMap.get(unit.getUsername()) != null) {
	// Map<String, Object> properties = unitMap
	// .get(unit.getUsername());
	// for (Entry<String, Object> prop : properties.entrySet()) {
	// if (prop.getKey().equalsIgnoreCase("lane")) {
	// unitNode.put("lane", prop.getValue().toString());
	// } else if (prop.getKey().equalsIgnoreCase("laneText")) {
	// unitNode.put("laneText", prop.getValue().toString());// TODO
	// // remove
	// } else if (prop.getKey().equalsIgnoreCase("ticketCode")) {
	// unitNode.put("ticketCode", prop.getValue().toString());
	// } else if (prop.getKey().equalsIgnoreCase("started")
	// || prop.getKey().equalsIgnoreCase("called")) {
	// unitNode.put(prop.getKey(), prop.getValue().toString());
	// } else {
	// ArrayNode lanesStatsNode = mapper.createArrayNode();
	// List<String> list = (List<String>) prop.getValue();
	// if (list != null) {
	// for (String dataStr : list) {
	// String dataArr[] = dataStr.split(",");//
	// laneName,ticketCode,start,end,call
	// ObjectNode laneStatNode = mapper
	// .createObjectNode();
	//
	// laneStatNode
	// .put("lane", dataArr[0])
	// .put("ticket", dataArr[1])
	// .put("start", dataArr[2])
	// .put("end", dataArr[3])
	// .put("call", dataArr[4])
	// .put("mark",
	// Integer.parseInt(dataArr[5]))
	// .put("tokenId", dataArr[6])
	// .put("isTicketReserved",
	// Boolean.parseBoolean(dataArr[7]))
	// .put("laneText",
	// Messages.get("lane.#0.name_RU",
	// dataArr[0]));
	// lanesStatsNode.add(laneStatNode);
	// }
	// }
	// unitNode.put(prop.getKey(), lanesStatsNode);
	// }
	// }
	// }
	// if (!unitNode.has("called")) {
	// unitNode.put("called", mapper.createArrayNode());
	// }
	// if (!unitNode.has("started")) {
	// unitNode.put("started", mapper.createArrayNode());
	// }
	// if (!unitNode.has("ended")) {
	// unitNode.put("ended", mapper.createArrayNode());
	// }
	// if (!unitNode.has("missed")) {
	// unitNode.put("missed", mapper.createArrayNode());
	// }
	// if (!unitNode.has("transferred")) {
	// unitNode.put("transferred", mapper.createArrayNode());
	// }
	//
	// List<Token> tokenUnit = queue
	// .getEntityManager()
	// .createQuery(
	// "select t " + "from Token t " + "join t.ticket tt "
	// + "join t.target u "
	// + "where year(tt.create) >= :yearFrom "
	// + "  and month(tt.create) >= :monthFrom "
	// + "  and day(tt.create) >= :dayFrom "
	// + "  and year(tt.create) <= :yearTo "
	// + "  and month(tt.create) <= :monthTo "
	// + "  and day(tt.create) <= :dayTo "
	// + "  and u.username = :username"
	// + " order by t.start")
	// .setParameter("username", unit.getUsername())
	// .setParameter("dayFrom", Integer.parseInt(dateFromArray[0]))
	// .setParameter("monthFrom",
	// Integer.parseInt(dateFromArray[1]))
	// .setParameter("yearFrom",
	// Integer.parseInt(dateFromArray[2]))
	// .setParameter("dayTo", Integer.parseInt(dateToArray[0]))
	// .setParameter("monthTo", Integer.parseInt(dateToArray[1]))
	// .setParameter("yearTo", Integer.parseInt(dateToArray[2]))
	// .getResultList();
	//
	// int tokenAll = tokenUnit.size(), markedAll = 0;
	//
	// int countMarkBad = 0, countMarkNorm = 0, countMarkGood = 0, countMarkBest
	// = 0, countMarkVeryBad = 0;
	//
	// double markAverage = 0;
	// for (Token token : tokenUnit) {
	// // QLog.info("Token of Unit " + token.getTarget().getUsername()
	// // + " mark " + token.getMark());
	//
	// if (token.getMark() != 0 && token.getMark() != 2)
	// markAverage += token.getMark();
	//
	// switch (token.getMark()) {
	// case 1:
	// countMarkVeryBad++;
	// break;
	// case 2:
	// countMarkBad++;
	// break;
	// case 3:
	// countMarkNorm++;
	// break;
	// case 4:
	// countMarkGood++;
	// break;
	// case 5:
	// countMarkBest++;
	// break;
	// }
	// }
	//
	// markedAll = countMarkBad + countMarkNorm + countMarkGood
	// + countMarkVeryBad + countMarkBest;
	// if (markedAll > 0)
	// markAverage = markAverage / markedAll;
	//
	// // QLog.info("markAverage " + markAverage + " b " + countMarkBad +
	// // " n " +countMarkNorm + " g " +countMarkGood);
	// unitNode.put("tokenAll", tokenAll);
	// unitNode.put("countReserved", countReserved);
	// unitNode.put("countMarkBad", countMarkBad);
	// unitNode.put("countMarkVeryBad", countMarkVeryBad);
	// unitNode.put("countMarkBest", countMarkBest);
	// unitNode.put("countMarkNorm", countMarkNorm);
	// unitNode.put("countMarkGood", countMarkGood);
	// unitNode.put(
	// "markAverage",
	// (markAverage == 0) ? "0.000" : String.format("%.3g%n",
	// markAverage));
	//
	// unitsNode.add(unitNode);
	// }
	// ArrayNode lanesNode = mapper.createArrayNode();
	// for (Lane lane : queue.getLanes(user.getGroup().getName())) {
	// ObjectNode rowNode = mapper.createObjectNode();
	// Map<String, Integer> statusMap = laneMap.get(lane.getUsername());
	// if (statusMap == null) {
	// statusMap = new HashMap<String, Integer>();
	// }
	// Integer created = statusMap.get("created");
	// Integer started = statusMap.get("started");
	// Integer called = statusMap.get("called");
	// Integer ended = statusMap.get("ended");
	// Integer missed = statusMap.get("missed");
	// Integer transferred = statusMap.get("transferred");
	// if (created == null) {
	// created = 0;
	// }
	// if (started == null) {
	// started = 0;
	// }
	// if (called == null) {
	// called = 0;
	// }
	// if (ended == null) {
	// ended = 0;
	// }
	// if (missed == null) {
	// missed = 0;
	// }
	// if (transferred == null) {
	// transferred = 0;
	// }
	//
	// int countLaneReserved = 0;
	//
	// List<Token> list = queue
	// .getEntityManager()
	// .createQuery(
	// "select t " + "from Token t " + "join t.ticket tt "
	// + "where tt.group.name = :group "
	// + "and year(tt.create) >= :yearFrom "
	// + "  and month(tt.create) >= :monthFrom "
	// + "  and day(tt.create) >= :dayFrom "
	// + "  and year(tt.create) <= :yearTo "
	// + "  and month(tt.create) <= :monthTo "
	// + "  and day(tt.create) <= :dayTo "
	// + "and t.laneName = :laneName "
	// + "order by t.start, t.call, t.create asc")
	// .setParameter("group", user.getGroup().getName())
	// .setParameter("laneName", lane.getUsername())
	// .setParameter("dayFrom", Integer.parseInt(dateFromArray[0]))
	// .setParameter("monthFrom",
	// Integer.parseInt(dateFromArray[1]))
	// .setParameter("yearFrom",
	// Integer.parseInt(dateFromArray[2]))
	// .setParameter("dayTo", Integer.parseInt(dateToArray[0]))
	// .setParameter("monthTo", Integer.parseInt(dateToArray[1]))
	// .setParameter("yearTo", Integer.parseInt(dateToArray[2]))
	// .getResultList();
	//
	// // QLog.info("List is empty " + list.isEmpty() + " and size "
	// // +list.size());
	// ArrayNode arrayNode = mapper.createArrayNode();
	//
	// for (Token t : list) {
	// // QLog.info("test tokenList by Lane " + t.getLaneName() +
	// // " Call " + t.getCall() );
	//
	// if (t.getTarget() instanceof Unit) {
	// boolean isTicketReserved = Reservation.isTicketReserved(t
	// .getTicket().getGroup().getName(),
	// queue.getEntityManager(), t.getTicket().getId());
	// if (isTicketReserved)
	// countLaneReserved++;
	//
	// Unit unit = (Unit) t.getTarget();
	//
	// arrayNode.add(mapper
	// .createObjectNode()
	// .put("ticket", t.getTicket().getCode())
	// .put("start", TimeUtils.toTime(t.getStart()))
	// .put("call", TimeUtils.toTime(t.getCall()))
	// .put("end", TimeUtils.toTime(t.getEnd()))
	// .put("mark", t.getMark())
	// .put("tokenId", t.getId())
	// .put("isTicketReserved", isTicketReserved)
	// .put("unitRealName",
	// unit.getFirstName() + " "
	// + unit.getLastName()));
	// }
	// }
	//
	// rowNode.put("laneText",
	// Messages.get("lane.#0.name_RU", lane.getUsername()))
	// .put("created", created).put("called", called)
	// .put("started", started).put("ended", ended)
	// .put("missed", missed)
	// .put("countReserved", countLaneReserved)
	// .put("transferred", transferred)
	// .put("lane", lane.getUsername()).put("history", arrayNode);
	// lanesNode.add(rowNode);
	// }
	//
	// /*
	// * Date now = new Date(); SimpleDateFormat formatDMY = new
	// * SimpleDateFormat("dd.MM.yyyy"); String dateNow =
	// * formatDMY.format(now);
	// */
	// String dateRepresent;// = dateNow;
	// if (dateFrom.equals(dateTo))
	// dateRepresent = dateFrom;
	// else
	// dateRepresent = dateFrom + " - " + dateTo;
	//
	// rootNode.put("unitStats", unitsNode);
	// rootNode.put("laneStats", lanesNode);
	// rootNode.put("dateRepresent", dateRepresent);
	// rootNode.put("username", user.getUsername());
	// rootNode.put("lang", Language.toString(user.getLanguage()));
	// rootNode.put("url", "/statistics.html");
	// return rootNode;
	// }
	//
	// private ObjectNode initTickets(User user, QueueWrapper queue, String
	// date)
	// throws QueuePluginException {
	// String[] dateFromArray = date.split("\\.");
	// List<Token> tokens = queue
	// .getEntityManager()
	// .createQuery(
	// "select t "
	// + "from Token t "
	// + "join t.ticket tt "
	// + "where tt.group.name = :group "
	// + "and year(tt.create) = :yearFrom "
	// + "  and month(tt.create) = :monthFrom "
	// + "  and day(tt.create) = :dayFrom "
	// + "  and t.status in ('WAITING','CALLED','STARTED')"
	// + "order by t.start, t.call, t.create asc")
	// .setParameter("group", user.getGroup().getName())
	// .setParameter("dayFrom", Integer.parseInt(dateFromArray[0]))
	// .setParameter("monthFrom", Integer.parseInt(dateFromArray[1]))
	// .setParameter("yearFrom", Integer.parseInt(dateFromArray[2]))
	// .getResultList();
	//
	// // List<Token> tokens = queue.getEntityManager().createQuery(
	// // "select t from " +
	// // " 	Token t join fetch t.ticket tt " +
	// // " where year(tt.create) = :yearFrom " +
	// // "  and month(tt.create) = :monthFrom " +
	// // "  and day(tt.create) = :dayFrom " +
	// // "  and t.status in ('WAITING','CALLED','STARTED')"+
	// // " order by t.start t.call t.create asc")
	// // .setParameter("dayFrom", Integer.parseInt(dateFromArray[0]) )
	// // .setParameter("monthFrom", Integer.parseInt(dateFromArray[1]) )
	// // .setParameter("yearFrom", Integer.parseInt(dateFromArray[2]) )
	// // .getResultList();
	// System.out.println(tokens.size());
	// ObjectNode rootNode = mapper.createObjectNode();
	// ArrayNode tokensNode = mapper.createArrayNode();
	// for (Token t : tokens) {
	// ObjectNode tokenNode = mapper.createObjectNode();
	// tokenNode
	// .put("ticket", t.getTicket().getCode())
	// .put("tokenId", t.getId())
	// .put("start", TimeUtils.toTime(t.getStart()))
	// .put("call", TimeUtils.toTime(t.getCall()))
	// .put("laneRu",
	// Messages.get("lane.#0.name_RU", t.getLaneName()))
	// .put("lane", t.getLaneName())
	// .put("status", t.getStatus().toString())
	// .put("priority", t.getPriority())
	// .put("window", t.getTarget().getUsername())
	// .put("waiting", TimeUtils.toTime(t.getCreate()));
	// tokensNode.add(tokenNode);
	// }
	//
	// rootNode.put("tokens", tokensNode);
	// rootNode.put("username", user.getUsername());
	// rootNode.put("lang", Language.toString(user.getLanguage()));
	// rootNode.put("url", "/statistics.html");
	// return rootNode;
	// }
}