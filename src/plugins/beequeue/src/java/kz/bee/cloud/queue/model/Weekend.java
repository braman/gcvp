package kz.bee.cloud.queue.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Id;

import kz.bee.cloud.queue.model.User.Language;

@Entity
@Table(name="Q_WEEKEND")
public class Weekend {
	
	private Long id;
	private Date start;
	private Group group;
	private String status;
	private String weekends;
	
	@Id
	@GeneratedValue
	@Column(name="ID_")
	public Long getId(){
		return id;
	}
	public void setId(Long id){
		this.id = id;
	}
	
	@Column(name="START_")
	public Date getStart(){
		return start;
	}
	public void setStart(Date start){
		this.start = start;
	}
	
	@ManyToOne
	@JoinColumn(name="GROUP_")
	public Group getGroup(){
		return group;
	}
	public void setGroup(Group group){
		this.group = group;
	}
	
	@Column(name="WEEKENDS_")
	public String getWeekends(){
		return weekends;
	}
	public void setWeekends(String weekends){
		this.weekends = weekends;
	}
}