package kz.bee.cloud.queue.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("K")
public class MonitorUnitDemo extends User {
	
	public MonitorUnitDemo(){
		setRole(Role.SECOND_MONITOR_DEMO);
	}
	
	@Override
	public String toString(){
		return "MonitorUnitDemo("+getUsername()+")";
	}
	
}