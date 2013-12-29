package kz.bee.cloud.queue.anketa;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import kz.bee.cloud.queue.model.User;

@Entity
@DiscriminatorValue("A")
public class AnketaUser extends User {
	
	public AnketaUser(){
		setRole(Role.ANKETA);
	}
	
}
