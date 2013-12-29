package kz.bee.cloud.queue.mobile;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kz.bee.cloud.queue.Messenger;
import kz.bee.cloud.queue.QueueWrapper;
import kz.bee.cloud.queue.model.Lane;
import kz.bee.cloud.queue.model.Token;
import kz.bee.cloud.queue.model.Unit;
import kz.bee.cloud.queue.model.User.Language;
import kz.bee.cloud.queue.reservation.Reservation;
import kz.bee.hibernate.connection.DBManager;
import kz.bee.util.Messages;
import kz.bee.util.QLog;
import kz.bee.util.TimeUtils;

import org.eclipse.jetty.util.log.Log;
import org.jivesoftware.admin.AuthCheckFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class MobileServlet extends HttpServlet {
	private ObjectMapper mapper;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		AuthCheckFilter.addExclude("beequeue/mobile");
		mapper = new ObjectMapper();
	}

	// http://localhost:9090/plugins/beequeue/cloud?group=atf&lanes=a,b&time=12:00&date=12.12.2012
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		QLog.info("doGet ");
		
		response.setHeader("Access-Control-Allow-Origin", "*");

		String content = (String) request.getParameter("request");
		EntityManager em = DBManager.newEm();
		em.getTransaction().begin();
		QueueWrapper queue = new QueueWrapper(em);

		System.out.println(content);
		if (content.equals("ticket")) {
			String group = (String) request.getParameter("group");
			System.out.println(group);

			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json; charset=UTF-8");

			PrintWriter out = response.getWriter();

			ObjectNode resultNode = mapper.createObjectNode();

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
									+ "  and t.status in ('WAITING')"
									+ "order by t.create asc")
					.setParameter("group", group)
					.setParameter("dayFrom", Integer.parseInt(dateFromArray[0]))
					.setParameter("monthFrom",
							Integer.parseInt(dateFromArray[1]))
					.setParameter("yearFrom",
							Integer.parseInt(dateFromArray[2])).getResultList();

			ArrayNode tokensNode = mapper.createArrayNode();
			for (Token t : ticket_tokens) {
				ObjectNode tokenNode = mapper.createObjectNode();
				tokenNode
						.put("ticket", t.getTicket().getCode())
						.put("laneRu",
								Messages.get("lane.#0.name_RU", t.getLaneName()))
						.put("waiting", TimeUtils.toTime(t.getCreate()));
				tokensNode.add(tokenNode);
			}

			resultNode.put("tickets", tokensNode);
			String message = mapper.writeValueAsString(resultNode);
			QLog.info("reservationServlet response " + content + " " + message);
			out.println(message);
			out.flush();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setHeader("Access-Control-Allow-Origin", "*");
		doGet(req, resp);
	}

}
