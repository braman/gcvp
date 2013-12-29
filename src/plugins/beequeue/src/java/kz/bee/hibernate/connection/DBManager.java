package kz.bee.hibernate.connection;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DBManager {
	
	public static EntityManagerFactory emf;
	
	private static EntityManager em;

	private DBManager(){}
	
	public static void init(){
		emf = Persistence.createEntityManagerFactory("cqPU");
	}
	
	public static void init(Map<String,String> properties){
		emf = Persistence.createEntityManagerFactory("cqPU",properties);
	}
	
	public static void close(){
		emf.close();
	}
	
	public static EntityManager newEm(){
		return emf.createEntityManager();
	}
}
