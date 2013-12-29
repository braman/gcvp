package kz.bee.cloud.queue.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("M")
public class MonitorUnit extends User {
	
	public MonitorUnit(){
		setRole(Role.SECOND_MONITOR);
	}
	
	@Override
	public String toString(){
		return "MonitorUnit("+getUsername()+")";
	}
	
}