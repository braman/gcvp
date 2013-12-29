package kz.bee.cloud.queue.reservation;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kz.bee.cloud.queue.Messenger;
import kz.bee.cloud.queue.model.Lane;
import kz.bee.cloud.queue.model.User.Language;
import kz.bee.hibernate.connection.DBManager;
import kz.bee.util.Messages;
import kz.bee.util.QLog;

import org.eclipse.jetty.util.log.Log;
import org.jivesoftware.admin.AuthCheckFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ReservationServlet extends HttpServlet {
	private ObjectMapper mapper;
	
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		AuthCheckFilter.addExclude("beequeue/reservation");
		mapper = new ObjectMapper();
	}
	//http://localhost:9090/plugins/beequeue/cloud?group=atf&lanes=a,b&time=12:00&date=12.12.2012
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		
		String content = (String) request.getParameter("request");

		QLog.info("doGet ");
		if ( content.equals("group") ) {
			response.setCharacterEncoding("UTF-8");
		    response.setContentType("application/json; charset=UTF-8");
			
			PrintWriter out = response.getWriter();

			ObjectNode resultNode = mapper.createObjectNode();
			ArrayNode arrayNode = Messenger.getInstance().getGroups();
			
			ObjectNode groups = mapper.createObjectNode();
			groups.put("groups",arrayNode);	
			resultNode.putAll(groups);
			
			String message = mapper.writeValueAsString(resultNode);
			QLog.info("reservationServlet response " + content + " " + message);
			out.println(message);
			out.flush();
		}
		
		else if ( content.equals("contentPage") )
		{
			response.setCharacterEncoding("UTF-8");
		    response.setContentType("application/json; charset=UTF-8");
			
			String date = (String) request.getParameter("date");
			String group = (String) request.getParameter("group");
			QLog.info("Reserving:#0 #1",date,group);
			
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			ObjectNode resultNode = mapper.createObjectNode();
		
			Date now = new Date();
			int hours = now.getHours();
			System.out.println("------------------------------------------------");
			System.out.println(hours);
			int minutes= now.getMinutes();
			System.out.println(minutes);
			String hours_s = "";
			String minutes_s = "";
			if(hours>8 && minutes >=30)
			{
				hours = hours + 1;
				minutes = 0;
				hours_s = hours + "";
				minutes_s = "00";
			}
			else if(hours>8 && minutes < 30){
				minutes = 30;
				hours_s = hours + "";
				minutes_s = minutes + "";
			}
			
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			Date date_now = new Date();
			String _now = dateFormat.format(date_now);			
			
			String now_date = _now.substring(8);
			String now_month = _now.substring(5,7);
			
			ObjectNode timeNode = mapper.createObjectNode();
			timeNode.put("start", hours_s + ":" + minutes_s);
			timeNode.put("end", "18:30");
			timeNode.put("month", Integer.parseInt(now_month) - 1);
			timeNode.put("date", Integer.parseInt(now_date));
			resultNode.put("time", timeNode);
			
			ArrayNode weekendNode = Messenger.getInstance().getWeekends(group);
			resultNode.put("weekend", weekendNode);
			
			ArrayNode weekendsNode = Messenger.getInstance().getNotWorkingWeekends(group);
			resultNode.put("notWorkingWeekends", weekendsNode);
			
			ArrayNode daysOfHoliday = Messenger.getInstance().getNotWorkingHolidays(group);
			resultNode.put("notWorkingHolidays", daysOfHoliday);
			
			ArrayNode arrayNode = Messenger.getInstance().getLanes(group);

			ObjectNode lanesJSONNode = mapper.createObjectNode();
			lanesJSONNode.put("lanes",arrayNode);		
			resultNode.putAll(lanesJSONNode);
			
			String message = mapper.writeValueAsString(resultNode);
			QLog.info("reservationServlet response " + message);
			out.println(message);
			out.flush();
		}
		else if ( content.equals("reserveTicket") )
		{
			response.setCharacterEncoding("UTF-8");
		    response.setContentType("application/json; charset=UTF-8");
			
			String time = (String) request.getParameter("time");
			String date = (String) request.getParameter("date");
			String id = (String) request.getParameter("id");
			String lanes = (String) request.getParameter("lanes");
			String group = (String) request.getParameter("group");
			QLog.info("Reserving:#0 #1 #2 #3 #4",time,date,id,lanes,group);
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			String responseJSON = Messenger.getInstance().doReservation(group, id, time, date, lanes);
			QLog.info("reservationServlet response " + responseJSON);

			out.println(responseJSON);
			out.flush();
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setHeader("Access-Control-Allow-Origin", "*");
		doGet(req,resp);
	}

}
