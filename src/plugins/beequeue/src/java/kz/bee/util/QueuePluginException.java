package kz.bee.util;

public class QueuePluginException extends Exception {

	private int id;
	
	public QueuePluginException(String message) {
		super(message);
	}
	
	public QueuePluginException(int id,String message){
		super(message);
		this.id = id;
	}
	
	public int getId(){
		return id;
	}
	
	@Override
	public String toString(){
		return "QPE("+id+":"+getMessage()+")";
	}

}
