package kz.bee.cloud.queue.model;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;

import kz.bee.util.Constants;
import kz.bee.util.QLog;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.pubsub.LeafNode;
import org.jivesoftware.openfire.pubsub.NodeSubscription;
import org.xmpp.packet.JID;

@Entity
@DiscriminatorValue("O")
public class Unit extends User {
	
	public Unit(){
		setRole(Role.UNIT);
	}
	
	public Unit(Set<Lane> lanes){
		this.lanes = lanes;
		setRole(Role.UNIT);
	}
	
	@Column(name="WINDOW_")
	private String window;
	
	@Column(name="FIRSTNAME_")
	private String firstName;
	
	@Column(name="workStart_")
	private String workStart;
	
	@Column(name="workFinish_")
	private String workFinish;
	
	@Column(name="LASTNAME_")
	private String lastName;
	
	@ManyToMany(fetch=FetchType.LAZY) @JoinTable(name="Q_UNIT_LANES",joinColumns={@JoinColumn(name="UNIT_")},inverseJoinColumns={@JoinColumn(name="LANE_")})
	private Set<Lane> lanes = new LinkedHashSet<Lane>();
	
	@OneToOne @JoinColumn(name="SECOND_MONITOR_")
	private User monitorUnit;
	
	@OneToOne @JoinColumn(name="SECOND_MONITOR_DEMO_")
	private User monitorUnitDemo;
	
	public static void beforeUpdate(Unit unit) {
		QLog.info("before update---------------------------------");
		if(XMPPServer.getInstance()!=null){
			JID user = new JID(unit.getUsername().concat("@cq.b2e.kz"));
			for(Lane lane:unit.lanes){
				LeafNode node = (LeafNode) XMPPServer.getInstance().getPubSubModule().getNode(unit.getGroup().getName()+"."+lane.getUsername()+".pubsub");
				NodeSubscription subscription = node.getSubscription(user);
				if(subscription!=null){
					QLog.info(subscription+ " deleted subscription");
					node.cancelSubscription(subscription);
				}	
			}
		}
	}
	@PostPersist
	@PostUpdate
	public void afterPersist(){
//		System.out.println(getUsername()+"-----------------");
		if(XMPPServer.getInstance()!=null){
			JID user = new JID(getUsername().concat("@cq.b2e.kz"));
			for(Lane lane:lanes){
				LeafNode node = (LeafNode) XMPPServer.getInstance().getPubSubModule().getNode(getGroup().getName()+"."+lane.getUsername()+".pubsub");
				System.out.println(getGroup().getName()+"."+lane.getUsername()+".pubsub----------------------");
				if(node.getSubscription(user)==null){
					node.createSubscription(null,Constants.QUEUE,user,false,null);
					QLog.info("created subscriptions for #0",user);
				}
			}
		}
	}
	
	public User getMonitorUnit() {
		return monitorUnit;
	}

	public void setMonitorUnit(User monitorUnit) {
		this.monitorUnit = monitorUnit;
	}
		
	public User getMonitorUnitDemo() {
		return monitorUnitDemo;
	}

	public void setMonitorUnitDemo(User monitorUnitDemo) {
		this.monitorUnitDemo = monitorUnitDemo;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getWorkStart() {
		return workStart;
	}

	public void setWorkStart(String workStart) {
		this.workStart = workStart;
	}

	public String getWorkFinish() {
		return workFinish;
	}

	public void setWorkFinish(String workFinish) {
		this.workFinish = workFinish;
	}

	public Set<Lane> getLanes() {
		return lanes;
	}

	public void setLanes(Set<Lane> lanes) {
		this.lanes = lanes;
	}
	
	public String getWindow() {
		return window;
	}

	public void setWindow(String window) {
		this.window = window;
	}

	@Override
	public String toString(){
		return "Unit("+getUsername()+")";
	}
	
}
