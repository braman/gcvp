package kz.bee.cloud.queue.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PreUpdate;

import kz.bee.util.Constants;
import kz.bee.util.QLog;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.pubsub.CollectionNode;
import org.jivesoftware.openfire.pubsub.LeafNode;
import org.jivesoftware.openfire.pubsub.NodeSubscription;
import org.jivesoftware.openfire.pubsub.PubSubPersistenceManager;
import org.xmpp.packet.JID;

@Entity
@DiscriminatorValue(value = "D")
public class Dashboard extends User {

	public Dashboard() {
		setRole(Role.DASHBOARD);
	}

	public Dashboard(Set<Lane> lanes) {
		setRole(Role.DASHBOARD);
		this.dashboardLanes = lanes;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "Q_DASHBOARD_LANES", joinColumns = { @JoinColumn(name = "DASHBOARD_") }, inverseJoinColumns = { @JoinColumn(name = "LANE_") })
	private Set<Lane> dashboardLanes = new HashSet<Lane>();

	public Set<Lane> getDashboardLanes() {
		return dashboardLanes;
	}

	public void setDashboardLanes(Set<Lane> dashboardLanes) {
		this.dashboardLanes = dashboardLanes;
	}

	public static void beforeUpdate(Dashboard dash) {
		QLog.info("before update---------------------------------");
		if (XMPPServer.getInstance() != null) {
			JID user = new JID(dash.getUsername().concat("@cq.b2e.kz"));
			for (Lane lane : dash.dashboardLanes) {
				LeafNode node = (LeafNode) XMPPServer
						.getInstance()
						.getPubSubModule()
						.getNode(dash.getGroup().getName() + "." + lane.getUsername()
										+ ".pubsub");
//				System.out.println(dash.getGroup().getName() + "." + lane.getUsername()+ ".pubsub");
//				System.out.println(node+"node---------------");
//				System.out.println(user+"user --------------------");
				NodeSubscription subscription = node.getSubscription(user);
				if(subscription!=null){
					QLog.info(subscription+ " is sqbscription");
					node.cancelSubscription(subscription);
				}				
			}
			if(dash.dashboardLanes.size()>0){
				LeafNode node = (LeafNode) XMPPServer
						.getInstance()
						.getPubSubModule()
						.getNode(
								dash.getGroup().getName() 
										+ ".pubsub");
				if (node.getSubscription(user) == null) {
					node.createSubscription(null, Constants.QUEUE, user, false,
							null);
					QLog.info("created subscriptions for #0", user);
				}
			}
		}
	}

	@PostPersist
	@PostUpdate
	public void afterPersist() {
		
		
		if (XMPPServer.getInstance() != null) {
			
			CollectionNode pNode = null;
			if(getGroup()!=null){
				pNode = (CollectionNode) XMPPServer.getInstance().getPubSubModule().getNode(getGroup().getName()+".pubsub");
			}
//			LeafNode node = (LeafNode) XMPPServer.getInstance().getPubSubModule().getNode(getGroup().getName()+".dashboard.pubsub");
			LeafNode node = (LeafNode) XMPPServer.getInstance().getPubSubModule().getNode(getGroup().getName()+".dashboard.pubsub");
//			QLog.info(node);
			if(node == null){
				System.out.println(getGroup().getName()+".dashboard.pubsub"+"----------------------------");
				node = new LeafNode(XMPPServer.getInstance().getPubSubModule(), pNode, getGroup().getName()+".dashboard.pubsub",Constants.QUEUE);
				node.addPublisher(Constants.QUEUE);
				node.saveToDB();
				QLog.info("created leaf node for #0",this.toString());
			}
			
			JID user = new JID(getUsername().concat("@cq.b2e.kz"));
			node = (LeafNode) XMPPServer.getInstance().getPubSubModule().getNode(getGroup().getName()+".dashboard.pubsub");
			if(node.getSubscription(user)==null){
				node.createSubscription(null,Constants.QUEUE,user,false,null);
				QLog.info("Created subscription for #0 to #1",getUsername(),getGroup().getName()+".pubsub");
			}
			
			for (Lane lane : dashboardLanes) {
				node = (LeafNode) XMPPServer
						.getInstance()
						.getPubSubModule()
						.getNode(
								getGroup().getName() + "." + lane.getUsername()
										+ ".pubsub");
				if(node!=null){
					if (node.getSubscription(user) == null) {
						node.createSubscription(null, Constants.QUEUE, user, false,
								null);
						QLog.info("created subscriptions for #0", user);
					}
				}				
			}
//			QLog.info("created node ");
//			LeafNode node = (LeafNode) XMPPServer
//					.getInstance()
//					.getPubSubModule()
//					.getNode(
//							getGroup().getName() +".q"
//									+ ".pubsub");
//			QLog.info("created subscriptions for node #0", node.toString());
//			QLog.info(node.getSubscription(user).toString());
//			if (node.getSubscription(user) == null) {
//				QLog.info("entered");
//				node.createSubscription(null, Constants.QUEUE, user, false,
//						null);
//				QLog.info("created subscriptions for #0", user);
//			}
		}
	}

	@Override
	public void setStatus(Status status) {
		if (status.equals(Status.DISABLED) && XMPPServer.getInstance() != null) {
			JID user = new JID(getUsername().concat("@cq.b2e.kz"));
			QLog.info(dashboardLanes.size()+"");
			for (Lane lane : dashboardLanes) {
				QLog.info(getGroup().getName());
				QLog.info( lane.getUsername());
				
				LeafNode node = (LeafNode) XMPPServer
						.getInstance()
						.getPubSubModule()
						.getNode(
								getGroup().getName() + "." + lane.getUsername()
										+ ".pubsub");
				if(node != null){
					System.out.println(node.toString());
					NodeSubscription subscription = node.getSubscription(user);
					if (subscription != null) {
						node.cancelSubscription(subscription);
						QLog.info("cancelled subscriptions for #0", user);
					}
				}			
			}
		} else {
			QLog.info("else");
			afterPersist();
		}
		super.setStatus(status);
	}

	@Override
	public String toString() {
		return "Dashboard(" + getUsername() + ")";
	}
}
