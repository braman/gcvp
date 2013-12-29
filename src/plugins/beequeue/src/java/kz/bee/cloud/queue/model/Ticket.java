package kz.bee.cloud.queue.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import kz.bee.cloud.queue.model.User.Language;

/**
 * Билет
 * 
 * Индексы БД: самый главный индекс idx_ticket_group_create_date для работы
 * вызывалок:
 * <code>CREATE INDEX idx_ticket_group_create_date ON q_ticket USING btree (group_, (create_::date));</code>
 * ограничивает скоуп билетов для билетов в указаннов ЦОНе на конкретную дату
 * 
 * @author daniyar.assanov@bee.kz
 * 
 */
@Entity
@Table(name = "Q_TICKET")
public class Ticket {

	@Id @GeneratedValue
	@Column(name = "ID_")
	private Long id;

	@Column(name = "CODE_")
	private String code;

	@ManyToOne @JoinColumn(name = "GROUP_")
	private Group group;

	@OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL) @MapKey(name = "target")
	private Map<User, Token> tokens = new HashMap<User, Token>();

	@Column(name = "CREATE_")
	private Date create;

	@Column(name = "CLOSE_")
	private Date close;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS_")
	private Status status = Status.WAITING;

	@Column(name = "PERSONALID_")
	private String personalId;

	@Enumerated(EnumType.STRING)
	@Column(name = "LANG_")
	private Language lang;

	public static enum Status {
		WAITING, BUSY, END
	};

	@Transient
	public List<Token> getTokenList() {
		List<Token> tokenList = new ArrayList<Token>();
		tokenList.addAll(this.getTokens().values());
		return tokenList;
	}
	
	@Transient
	public List<User> getTargetList() {
		List<User> laneList = new ArrayList<User>();
		laneList.addAll(this.getTokens().keySet());
		return laneList;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Map<User, Token> getTokens() {
		return tokens;
	}

	public void setTokens(Map<User, Token> tokens) {
		this.tokens = tokens;
	}

	public Date getCreate() {
		return create;
	}

	public void setCreate(Date create) {
		this.create = create;
	}

	public Date getClose() {
		return close;
	}

	public void setClose(Date close) {
		this.close = close;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getPersonalId() {
		return personalId;
	}

	public void setPersonalId(String personalId) {
		this.personalId = personalId;
	}

	public Language getLang() {
		return lang;
	}

	public void setLang(Language lang) {
		this.lang = lang;
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
		Ticket other = (Ticket) obj;
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
		buffer.append("Ticket(id: ").append(id).append(", name: ").append(code)
				.append(", group: ").append(group).append(", status: ")
				.append(status).append(")");
		return buffer.toString();
	}

}
