package kz.bee.cloud.queue.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostPersist;
import javax.persistence.Table;

import kz.bee.util.Constants;
import kz.bee.util.QLog;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.pubsub.CollectionNode;
import org.jivesoftware.openfire.pubsub.LeafNode;

@Entity
@Table(name = "Q_GROUP")
public class Group implements Serializable {

	private static final long serialVersionUID = 1635501999608866625L;

	@Id @Column(name = "NAME_", length=100)
	private String name;
	
	@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="PARENT_")
	private Group parent;
	
	@OneToMany(mappedBy = "parent")
	private List<Group> subgroups;
	
	@OneToMany(mappedBy = "group")
	private List<AnketaQuestions> anketaQuestions;
	
	@OneToOne(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
	@JoinColumn(name="PROPERTIES_")
	private Properties properties;
	
	@Basic(fetch=FetchType.LAZY,optional=true)
	@Column(name="KIOSK_",columnDefinition="TEXT")//TODO привязка к POSTGRES
	private String kioskTemplate;
	
	@Basic(fetch=FetchType.LAZY,optional=true)
	@Column(name="UNIT_",columnDefinition="TEXT")//TODO привязка к POSTGRES
	private String unitTemplate;
	
	@Basic(fetch=FetchType.LAZY,optional=true)
	@Column(name="DASHBOARD_",columnDefinition="TEXT")//TODO привязка к POSTGRES
	private String dashboardTemplate;
	
	@Basic(fetch=FetchType.LAZY,optional=true)
	@Column(name="MONITOR_",columnDefinition="TEXT")//TODO привязка к POSTGRES
	private String monitorTemplate;

	@Column(name="KIOSK_LOGO_")
	private String kioskLogo;
	
	@Column(name="DASHBOARD_LOGO_")
	private String dashboardLogo;
	
	@Column(name="TICKET_LOGO_")
	private String ticketLogo;
		
	@Column(name="VIDEO_PATH_")
	private String videoPath;
	
	@Column(name="running_line_")
	private String runningLine;
	
	@Column(name="LASTKIOSKUPDATE_")
	private int counter = 0;
	
	@Column(name="KIOSKTOTALPAPERLENGTH_")
	private int length = 0;
	
	@Column(name="KIOSKPAPERLENGTH_")
	private int paperLength = 0;
	
	public Group(){
		
	}
	
	@PostPersist
	public void afterPersist(){
		if(XMPPServer.getInstance()!=null){
			CollectionNode pNode = null;
			if(parent!=null){
				pNode = (CollectionNode) XMPPServer.getInstance().getPubSubModule().getNode(parent.getName()+".pubsub");
			}
			CollectionNode cNode = new CollectionNode(XMPPServer.getInstance().getPubSubModule(),pNode,name+".pubsub",Constants.QUEUE);
			cNode.saveToDB();
			LeafNode lNode = new LeafNode(XMPPServer.getInstance().getPubSubModule(), cNode, name+".kiosk.pubsub", Constants.QUEUE);
			lNode.addPublisher(Constants.QUEUE);
			lNode.saveToDB();
			QLog.info("created collection node and leaf node (for kiosks) for #0",this.toString());
		}
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getPaperLength() {
		return paperLength;
	}

	public void setPaperLength(int paperLength) {
		this.paperLength = paperLength;
	}

	public Group(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<AnketaQuestions> getAnketaQuestions() {
		return anketaQuestions;
	}

	public void setAnketaQuestions(List<AnketaQuestions> anketaQuestions) {
		this.anketaQuestions = anketaQuestions;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Group getParent() {
		return parent;
	}

	public void setParent(Group parent) {
		this.parent = parent;
	}

	public String getRunningLine() {
		return runningLine;
	}

	public void setRunningLine(String runningLine) {
		this.runningLine = runningLine;
	}

	public List<Group> getSubgroups() {
		return subgroups;
	}

	public void setSubgroups(List<Group> subgroups) {
		this.subgroups = subgroups;
	}
	
	public Properties getProperties() {
		if(properties == null){
			if(getName() != null)
				properties = new Properties("G_" + getName());
			else 
				properties = new Properties();
		}
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	public String getKioskTemplate() {
		return kioskTemplate;
	}

	public void setKioskTemplate(String kioskTemplate) {
		this.kioskTemplate = kioskTemplate;
	}

	public String getUnitTemplate() {
		return unitTemplate;
	}

	public void setUnitTemplate(String unitTemplate) {
		this.unitTemplate = unitTemplate;
	}

	public String getDashboardTemplate() {
		return dashboardTemplate;
	}

	public void setDashboardTemplate(String dashboardTemplate) {
		this.dashboardTemplate = dashboardTemplate;
	}

	public String getMonitorTemplate() {
		return monitorTemplate;
	}

	public void setMonitorTemplate(String monitorTemplate) {
		this.monitorTemplate = monitorTemplate;
	}
	
	public String getKioskLogo() {
		return kioskLogo;
	}

	public void setKioskLogo(String kioskLogo) {
		this.kioskLogo = kioskLogo;
	}

	public String getDashboardLogo() {
		return dashboardLogo;
	}

	public void setDashboardLogo(String dashboardLogo) {
		this.dashboardLogo = dashboardLogo;
	}

	public String getTicketLogo() {
		return ticketLogo;
	}

	public void setTicketLogo(String ticketLogo) {
		this.ticketLogo = ticketLogo;
	}

	public String getVideoPath() {
		return videoPath;
	}

	public void setVideoPath(String videoPath) {
		this.videoPath = videoPath;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Group other = (Group) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public static Group getGroup(EntityManager em, String group_name){
		Group resultGroup = (Group) em.createQuery("from Group g where g.name = :group_name")
				.setParameter("group_name", group_name)
				.setMaxResults(1)
				.getResultList()
				.get(0);
		return resultGroup;
	}
}
