package kz.bee.cloud.queue.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import kz.bee.hibernate.connection.DBManager;

@Entity
@Table(name = "Q_NUMBER")
public class Number {

//	@Id
//	@GeneratedValue
//	@Column(name = "ID_")
//	private Long id;

	@Id
	@Column(name = "NUMBER_")
	private String number;
	
	@ManyToOne
	@JoinColumn(name="UNIT_")
	private User unit;
	
	@Column(name = "CHECKED_")
	private boolean checked;
	
	public Number(User unit, String number, boolean checked){
		this.number = number;
		this.unit = unit;
		this.checked = checked;
	}

	public Number(){}
	
//	public Long getId(){
//		return id;
//	}
	
	public void setUnit(User unit){
		this.unit = unit;
	}
	
	public User getUnit(){
		return unit;
	}
	
	public String getNumber(){
		return number;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}
	
	public boolean getChecked(){
		return checked;
	}
	
	public void setChecked(boolean checked){
		this.checked = checked;
	}

	public static List<Number> getNumbers(EntityManager em, User unit){

		List<Number> numbers = em.createQuery("select n from Number n where n.unit = :unit AND n.checked = false")
				.setParameter("unit", unit)
				.getResultList();
		
		System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		
		return numbers;
	}
	
	public static void deactivateNumber(EntityManager em, String number){
		Number number_ = (Number) em.createQuery("select n from Number n where n.number = :number")
			.setParameter("number", number).getResultList().get(0);
		number_.setChecked(true);
	}
}
