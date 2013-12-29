package kz.bee.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
	
	public static String toTime(Date date){
		if(date==null){
			return "null";
		}
		return new SimpleDateFormat("HH:mm:ss").format(date);
	}
}
