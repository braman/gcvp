package kz.bee.util;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QLog {
	
	private static Logger log = LoggerFactory.getLogger("CQ");
	
	private QLog(){
		//not instantiable
	}
	
	static String getCallerInformation() {
        StackTraceElement[] callStack = Thread.currentThread().getStackTrace();
        StackTraceElement caller = callStack[3];
        return caller.getFileName().replace(".java","")+"."+caller.getMethodName();
    }
	
	public static void info(String message){
		log.info(getCallerInformation()+" "+message);
	}
	
	public static void info(String message,Object ... params){
		log.info(getCallerInformation()+" "+getFormattedMessage(message, params));
	}
	
	public static void warn(String message){
		log.warn(message);
	}
	
	public static void warn(String message,Object ... params){
		log.warn(getFormattedMessage(message, params));
	}
	
	public static void error(String message){
		log.error(message);
	}
	
	public static void error(String message,Object ... params){
		log.error(getFormattedMessage(message, params));
	}
	
	public static void debug(String message){
		log.debug(message);
	}
	
	public static void debug(String message,Object ... params){
		log.debug(getFormattedMessage(message, params));
	}
	
	private static String getFormattedMessage(String message, Object ...params){
		List list = Arrays.asList(params);
		for(int i=0;i<list.size();i++){
			Object value = list.get(i);
			message = message.replace("#".concat(String.valueOf(i)),value!=null?value.toString():"null");	
		}
		return message;
	}
}
