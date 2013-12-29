package kz.bee.cloud.queue.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PostPersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import kz.bee.util.Constants;
import kz.bee.util.QLog;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.pubsub.CollectionNode;
import org.jivesoftware.openfire.pubsub.LeafNode;
import org.jivesoftware.openfire.pubsub.NodeSubscription;
import org.xmpp.packet.JID;

@Entity
@Table(name="Q_USER")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="DISCRIMINATOR_", discriminatorType=DiscriminatorType.CHAR)
@DiscriminatorValue("U")
//extends org.jivesoftware.openfire.user.User 
public class User{
	
	@Id
	@Column(name="USERNAME_")
	private String username;
	
	@Column(name="PASSWORDHASH_")
	private String passwordHash;
	
	@Column(name="PASSWORDSALT_")
	private String passwordSalt;
	
	@Enumerated(EnumType.STRING)
	@Column(name="LANG_")
	private Language language = Language.RU;
	
	@Column(name="CREATED_")
	private Date created = new Date();
	
	@Column(name="MODIFIED_")
	private Date modified = new Date();
	
	
	@Enumerated(EnumType.STRING)
	@Column(name="ROLE_")
	private Role role;
	
	@Column(name="EMAIL_")
	private String email;
	
	@Column(name="DATA_",columnDefinition="TEXT") //TODO
	private String data;
	
	@Enumerated(EnumType.STRING)
	@Column(name="STATUS_")
	private Status status = Status.ENABLED;
	
	@ManyToOne @JoinColumn(name="GROUP_")
	private Group group;
	
	@OneToOne(fetch=FetchType.LAZY,cascade=CascadeType.ALL) @JoinColumn(name="PROPERTIES_")
	private Properties properties;
	
	public enum Status{
		ENABLED,DISABLED
	};
	
	public enum Role{
		LANE,KIOSK,UNIT,DASHBOARD,ADMIN,LOCALADMIN,MONITOR,SECOND_MONITOR,SECOND_MONITOR_DEMO,ANKETA;
		
		public static Role get(String str){
			str = str.toLowerCase();
			if(str.contains("kiosk")){
				return KIOSK;
			}else if(str.contains("unit")){
				return UNIT;
			}else if(str.contains("dashboard")){
				return DASHBOARD;
			}else if(str.contains("localadmin")){
				return LOCALADMIN;
			}else if(str.contains("admin")){
				return ADMIN;
			}else if(str.contains("lane")){
				return LANE;
			}else if(str.contains("second")){
				return SECOND_MONITOR;
			}else if(str.contains("demo")){
				return SECOND_MONITOR_DEMO;
			}else if(str.contains("monitor")){
				return MONITOR;
			}else if(str.contains("anketa")){
				return ANKETA;
			}
			return null;
		}
	};
	
	public enum Language{
		KK,RU,EN;
		
		public static Language get(String str){
			str = str.toLowerCase();
			return str.contains("ru")?Language.RU:(str.contains("kk")?Language.KK:Language.EN);
		}
		
		public static String toString(Language language){
			if(language==Language.EN)
				return "en";
			else if(language==Language.RU)
				return "ru";
			else if(language==Language.KK)
				return "kk";
			return null;
		}
	};

	@Transient
	public boolean isEnabled(){
		return status.equals(Status.ENABLED);
	}
	
	@PostPersist
	public void afterPersist(){
		if(XMPPServer.getInstance()!=null){
			if(role.equals(Role.MONITOR)){
				JID user = new JID(getUsername().concat("@cq.b2e.kz"));
				CollectionNode node = (CollectionNode) XMPPServer.getInstance().getPubSubModule().getNode(group.getName()+".pubsub");
				if(node.getSubscription(user)==null){
					node.createSubscription(null,Constants.QUEUE,user,false,Constants.DATAFORM);
					QLog.info("Created subscription for #0 to #1",username,group.getName()+".pubsub");
				}
			}else if(role.equals(Role.KIOSK)){
				JID user = new JID(getUsername().concat("@cq.b2e.kz"));
				LeafNode node = (LeafNode) XMPPServer.getInstance().getPubSubModule().getNode(group.getName()+".kiosk.pubsub");
				if(node.getSubscription(user)==null){
					node.createSubscription(null,Constants.QUEUE,user,false,null);
					QLog.info("Created subscription for #0 to #1",username,group.getName()+".pubsub");
				}
			}
		}
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getPasswordSalt() {
		return passwordSalt;
	}

	public void setPasswordSalt(String passwordSalt) {
		this.passwordSalt = passwordSalt;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		if(XMPPServer.getInstance()!=null){
			if(role.equals(Role.MONITOR)){
				JID user = new JID(getUsername().concat("@cq.b2e.kz"));
				CollectionNode node = (CollectionNode) XMPPServer.getInstance().getPubSubModule().getNode(group.getName()+".pubsub");
				NodeSubscription subscription = node.getSubscription(user);
				if(subscription!=null){
					node.cancelSubscription(subscription);
					QLog.info("Cancelled subscription for #0 to #1",username,group.getName()+".pubsub");
				}
			}	
		}
		this.status = status;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}
	
	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "User("+username+","+role+","+group+")";
	}

	@Override
	public int hashCode() {
		final int prime = 37;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		User other = (User) obj;
		if (!username.equals(other.getUsername()))
			return false;
		return true;
	}

	public static void changeLanguage(User user, String language){
		user.setLanguage(Language.get(language));
		
	}
}
