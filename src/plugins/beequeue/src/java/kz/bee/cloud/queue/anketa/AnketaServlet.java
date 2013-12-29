package kz.bee.cloud.queue.anketa;

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
import kz.bee.cloud.queue.model.AnketaAnswers;
import kz.bee.cloud.queue.model.AnketaQuestions;
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

public class AnketaServlet extends HttpServlet {
	private ObjectMapper mapper;
	
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		AuthCheckFilter.addExclude("beequeue/anketa");
		mapper = new ObjectMapper();
	}
	//http://localhost:9090/plugins/beequeue/cloud?group=atf&lanes=a,b&time=12:00&date=12.12.2012
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "*");
		
		String content = (String) request.getParameter("request");

		QLog.info("doGet ");
		if ( content.equals("anketa") ) {
			response.setCharacterEncoding("UTF-8");
		    response.setContentType("application/json; charset=UTF-8");
			
		    String group = (String) request.getParameter("group");
			
		    
			PrintWriter out = response.getWriter();

			ObjectNode resultNode = mapper.createObjectNode();	
			
			ArrayNode questionsNode = Messenger.getInstance().getQuestions(group);
			
			
			resultNode.put("questions",questionsNode);			


			String message = mapper.writeValueAsString(resultNode);
			QLog.info("reservationServlet response " + content + " " + message);
			out.println(message);
			out.flush();
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setHeader("Access-Control-Allow-Origin", "*");
		doGet(req,resp);
	}

}
