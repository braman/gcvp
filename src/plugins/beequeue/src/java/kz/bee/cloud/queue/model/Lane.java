package kz.bee.cloud.queue.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PostPersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import kz.bee.util.Constants;
import kz.bee.util.QLog;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.pubsub.CollectionNode;
import org.jivesoftware.openfire.pubsub.LeafNode;
import org.jivesoftware.openfire.pubsub.Node;

@Entity
@DiscriminatorValue(value="L")
public class Lane extends User{

	public Lane(){
		setRole(Role.LANE);
	}
	
	@Column(name="RANGESTART_")
	private String rangeStart;
	
	@Column(name="RANGEEND_")
	private String rangeEnd;
	
	@Column(name="COLOR_")
	private String color;
	
	@Column(name = "PRIORITY_")
	private int priority = 0;
	
	@Column(name="COUNTER_")
	private Integer count = 0;
	
	@Column(name="COUNTERDATE_") @Temporal(TemporalType.DATE)
	private Date countDate;
	
	public String getRangeStart() {
		return rangeStart;
	}
	
	@PostPersist
	public void afterPersist(){
		if(XMPPServer.getInstance()!=null){
			CollectionNode pNode = null;
			if(getGroup()!=null){
				pNode = (CollectionNode) XMPPServer.getInstance().getPubSubModule().getNode(getGroup().getName()+".pubsub");
			}
			LeafNode node = new LeafNode(XMPPServer.getInstance().getPubSubModule(), pNode, getGroup().getName()+"."+getUsername()+".pubsub",Constants.QUEUE);
			node.addPublisher(Constants.QUEUE);
			node.saveToDB();
			QLog.info("created leaf node for #0",this.toString());
		}
	}
	
	@Override
	public void setStatus(Status status) {
		System.out.println(status);
		if(status.equals(Status.DISABLED) && XMPPServer.getInstance()!=null){
//			Sultan
			Node node = XMPPServer.getInstance().getPubSubModule().getNode(getGroup().getName()+"."+getUsername()+".pubsub");
			node.delete();
			QLog.info("deleted leaf node for #0",this.toString());
		}else{			
			afterPersist();
		}
		super.setStatus(status);
	}

	public void setRangeStart(String rangeStart) {
		this.rangeStart = rangeStart;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getRangeEnd() {
		return rangeEnd;
	}

	public void setRangeEnd(String rangeEnd) {
		this.rangeEnd = rangeEnd;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Date getCountDate() {
		return countDate;
	}

	public void setCountDate(Date countDate) {
		this.countDate = countDate;
	}
	
	@Override
	public String toString(){
		return "Lane("+ getUsername()+")";
	}
}
