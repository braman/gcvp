package kz.bee.hibernate.connection;

import javax.persistence.EntityManager;

import kz.bee.util.QueuePluginException;

public abstract class Work<T> {

	private EntityManager em;
	
	private boolean isRollbackRequired = true;
	
	public Work(){
		System.out.println("New transactional work begins");
	}
	
	public Work(boolean isRollbackRequired){
		this.isRollbackRequired = isRollbackRequired; 
	}
	
	protected abstract T work(EntityManager em) throws QueuePluginException;
	
	public final T workInTransaction() throws QueuePluginException{
		long start = System.currentTimeMillis();
		em = DBManager.newEm();
		em.getTransaction().begin();
		try{
			T result = work(em);
			em.getTransaction().commit();
			em.close();
			long end = System.currentTimeMillis();
			System.out.println("Work succesfully done in:"+(end-start)+" ms");
			return result;
		}catch(Exception e){
			if(e instanceof QueuePluginException){
				QueuePluginException qpe = (QueuePluginException)e;
				throw qpe;
			}
			if(isRollbackRequired){
				em.getTransaction().rollback();	
			}
			e.printStackTrace();
			em.close();
			throw new RuntimeException("Transaction rolled back",e);
		}
		
	}
	
}
