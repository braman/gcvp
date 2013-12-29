package kz.bee.cloud.queue.model;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Query;
import javax.persistence.Table;

@Entity
@Table(name = "Q_TOKEN")
public class Token {

	@Id @GeneratedValue @Column(name = "ID_")
	private Long id;
	
	@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name = "TICKET_") 
	private Ticket ticket;
	
	@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name = "TARGET_") 
	private User target;

	@Column(name = "CREATE_")
	private Date create;
	
	@Column(name = "CALL_")
	private Date call;

	@Column(name = "START_")
	private Date start;
	
	@Column(name = "END_")
	private Date end;
	
	@Column(name = "TIME_PERIOD_")
	private Date time_period;
	
	@Column(name = "POSTPONE")
	private Date postpone;
	
	@Column(name = "LANENAME_")
	private String laneName;
	
	@Column(name = "COLOR_")
	private String color;
	
	@Column(name = "STATUS_") @Enumerated(EnumType.STRING)
	private Status status = Status.WAITING;
	
	@Column(name = "PRIORITY_")
	private int priority = 0;
	
	@Column(name = "TOKEN_PRIORITY_")
	private int token_priority = 0;
	
	@OneToOne(fetch=FetchType.LAZY) @JoinColumn(name = "NEXT_TOKEN_")
	private Token nextToken;
	
	@Column(name = "MARK_")
	private int mark = 0;
	
	public enum Status {
		WAITING,
		CALLED,
		RECALLED,
		POSTPONED,
		STARTED,
		ENDED,
		MISSED,
		TRANSFERRED
	};
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Ticket getTicket() {
		return ticket;
	}

	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}

	public Date getCreate() {
		return create;
	}

	public void setCreate(Date create) {
		this.create = create;
	}
	
	public Date getCall() {
		return call;
	}

	public void setCall(Date call) {
		this.call = call;
	}
	
	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}
	
	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public void setTimePeriod(Date time_period) {
		this.time_period = time_period;
	}
	
	public Date getTimePeriod() {
		return time_period;
	}
	
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getToken_priority() {
		return token_priority;
	}

	public void setToken_priority(int token_priority) {
		this.token_priority = token_priority;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Token getNextToken() {
		return nextToken;
	}

	public void setNextToken(Token nextToken) {
		this.nextToken = nextToken;
	}
	
	public User getTarget() {
		return target;
	}

	public void setTarget(User target) {
		if(target instanceof Lane){
			this.laneName = target.getUsername();
		}
		this.target = target;
	}
	
	public String getLaneName() {
		return laneName;
	}

	public void setLaneName(String laneName) {
		this.laneName = laneName;
	}

	public Date getPostpone() {
		return postpone;
	}

	public void setPostpone(Date postpone) {
		this.postpone = postpone;
	}
	
	public int getMark() {
		return mark;
	}

	public void setMark(int mark) {
		this.mark = mark;
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
		Token other = (Token) obj;
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
		buffer.append("Token(id: ")
			  .append(id)
			  .append(",status: ")
			  .append(status)
			  .append(",laneName: ")
			  .append(laneName)
			  .append(")");
		return buffer.toString();
	}
	
	public static void setMissedById(EntityManager em, Long token_id) {
		List<Token> tokens_ = em.createQuery("from Token t where t.id = :token_id")
				.setParameter("token_id", token_id)
				.setMaxResults(1)
				.getResultList();
		
		Token token_ = tokens_.get(0);
		Ticket ticket = token_.getTicket();
		List<Token> ticketTokens = em.createQuery("from Token t where t.ticket = :ticket and t.status = 'WAITING'")
				.setParameter("ticket", ticket)
				.getResultList();
		System.out.println(ticketTokens.size()+" tickettoken size----------------------------");
		if(ticketTokens.size()>0){
			token_.getTicket().setStatus(Ticket.Status.WAITING);
		}else{
			token_.getTicket().setStatus(Ticket.Status.END);
		}
		token_.setStatus(Status.MISSED);
	}
}
