package kz.bee.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import kz.bee.cloud.queue.model.BundleMessage;
import kz.bee.cloud.queue.model.User.Language;
import kz.bee.hibernate.connection.DBManager;
import kz.bee.hibernate.connection.Work;

/**
 * Current impl only for RU
 * */
public class Messages {

	private static Map<String,String> map;
	
	private Messages() {
		// TODO Auto-generated constructor stub
	}
	
	public static Map<String,String> get(Language lang){
		if(lang==Language.RU){
			return getMessages();
		}
		else if(lang==Language.EN){
			return getMessages();
		}
		else if(lang==Language.KK){
			return getMessages();
		}
		QLog.error("Current impl do not support languages except RU");
		return null;
	}
	
	public static String get(String key){
		String value = map.get(key);
		if(value==null){
			return key;
		}
		return value;
	}
	
	public static String get(String formatted,String ... keys){
		List<String> keyList = Arrays.asList(keys);
		for(int i=0;i<keyList.size();i++){
			String value = keyList.get(i);
			formatted = formatted.replace("#".concat(String.valueOf(i)),value!=null?value:"");
		}
		return get(formatted);
	}
	
	public static String getKey(String formatted,String ... keys){
		List<String> keyList = Arrays.asList(keys);
		for(int i=0;i<keyList.size();i++){
			String value = keyList.get(i);
			formatted = formatted.replace("#".concat(String.valueOf(i)),value!=null?value:"");	
		}
		return get(formatted);
	}
	
	public static Map<String,String> getMessages(){
		return Collections.unmodifiableMap(map);
	}
	
	public static void loadMessages(){
		if(DBManager.emf==null){
			throw new IllegalStateException("DBManager is not loaded");
		}
		try {
			map = new Work<Map<String,String>>(){
				protected Map<String, String> work(EntityManager em) {
					List<BundleMessage> list = em.createQuery("from BundleMessage").getResultList();
					Map<String,String> map = new HashMap<String,String>();
					for(BundleMessage m:list){
						map.put(m.getKey()+"_"+m.getLang(),m.getValue());
						System.out.println(m.getKey()+"_"+m.getLang()+" -- "+m.getValue());
					}
					return map;
				}
			}.workInTransaction();
		} catch (QueuePluginException e) {
			e.printStackTrace();
		}
	}

	public static void set(final Language language, final String key, final String value) throws QueuePluginException {
		if(key==null || key.trim().isEmpty()){
			QLog.error("Messages \"set\" method called for empty key element");
			return;
		}
		new Work<Object>(){
			protected Object work(EntityManager em) throws QueuePluginException {
				List<BundleMessage> messages = em.createQuery("from BundleMessage bm where bm.key=:key and bm.lang=:lang")
					.setParameter("key", key)
					.setParameter("lang", language)
					.getResultList();
				if(messages.size()>0){
					for(BundleMessage bm:messages){
						bm.setValue(value);
					}
				}else{
					BundleMessage message = new BundleMessage();
					message.setKey(key);
					message.setLang(language);
					message.setValue(value);
					em.persist(message);	
				}
				return null;
			}
		}.workInTransaction();
		loadMessages();
	}

}
