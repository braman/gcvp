package kz.bee.cloud.queue.openfire.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import javax.persistence.EntityManager;

import kz.bee.cloud.queue.Messenger;
import kz.bee.cloud.queue.anketa.AnketaUser;
import kz.bee.cloud.queue.model.AnketaAnswers;
import kz.bee.cloud.queue.model.AnketaQuestions;

import kz.bee.cloud.queue.model.BundleMessage;
import kz.bee.cloud.queue.model.Dashboard;
import kz.bee.cloud.queue.model.Group;
import kz.bee.cloud.queue.model.Lane;
import kz.bee.cloud.queue.model.MonitorUnit;
import kz.bee.cloud.queue.model.MonitorUnitDemo;
import kz.bee.cloud.queue.model.Unit;
import kz.bee.cloud.queue.model.User;
import kz.bee.cloud.queue.model.User.Language;
import kz.bee.cloud.queue.model.User.Role;
import kz.bee.hibernate.connection.DBManager;
import kz.bee.hibernate.connection.Work;
import kz.bee.util.HashUtils;
import kz.bee.util.Messages;
import kz.bee.util.QLog;
import kz.bee.util.QueuePluginException;

import org.jivesoftware.admin.AuthCheckFilter;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.pubsub.PubSubModule;
import org.jivesoftware.util.JiveGlobals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class QueuePlugin implements Plugin {

	private InterceptorManager interceptorManager;
	private PacketInterceptor messageInterceptor;
	private PubSubModule pubsub;

	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		long start = System.currentTimeMillis();
		JiveGlobals.setProperty("xmpp.pubsub.service", "cqpubsub");

		QLog.info("Bee Q plugin initialize begin");
		/******************* DEBUG start *******************/
		boolean debug = false;
		if (debug) {
			Map<String, String> map = new HashMap<String, String>();
			// map.put("hibernate.show_sql", "true");
			map.put("hibernate.hbm2ddl.auto", "create");
			DBManager.init(map);
		} else {
			DBManager.init();
		}

		Messenger.init();

		AuthCheckFilter.addExclude("gcvp/reservation");
		AuthCheckFilter.addExclude("gcvp/mobile");

		pubsub = new PubSubModule();
		pubsub.initialize(XMPPServer.getInstance());
		pubsub.start();
		if (debug) {
			try {
				new Work<Object>() {
					@Override
					protected Object work(EntityManager em)
							throws QueuePluginException {
						em.createNativeQuery(
								"delete from ofpubsubaffiliation;delete from ofpubsubitem;delete from ofpubsubnode;delete from ofpubsubsubscription;")
								.executeUpdate();
						return null;
					}
				}.workInTransaction();
				insertInitialData(pluginDirectory.getAbsolutePath());
				// insertMessages();
			} catch (QueuePluginException e1) {
				e1.printStackTrace();
			}
		}
		/******************* DEBUG end *********************/

		// DBManager.init();//db & hibernate manager
		// Messenger.init();//messenger (message routing)
		QLog.info("Main components started");

		Messages.loadMessages();// i18n
		messageInterceptor = new QueueInterceptor();
		interceptorManager = InterceptorManager.getInstance();
		interceptorManager.addInterceptor(messageInterceptor);
		long end = System.currentTimeMillis();
		QLog.debug("DEBUG level");
		QLog.info("INFO level:");
		QLog.warn("WARN level");
		System.out.println("Bee Q plugin initialized in: " + (end - start)
				+ " ms");
	}

	public void destroyPlugin() {
		pubsub.stop();
		pubsub.destroy();
		DBManager.close();
		interceptorManager.removeInterceptor(messageInterceptor);
		System.out.println("Bee Q plugin destroyed");
	}

	public static void insertInitialData(String path) throws QueuePluginException{
		final String spank = path.replace("plugins/beequeue", "resources/spank");
		EntityManager em = DBManager.newEm();
		em.getTransaction().begin();
		
				Group main = new Group("main");
				em.persist(main);
				
				Group gcvp = new Group("gcvp");
				gcvp.setParent(main);
				
//				beeline.setKioskLogo("http://cq.b2e.kz/img/kiosk/beeline.png");
//				beeline.setTicketLogo("http://cq.b2e.kz/img/kiosk/beeline_ticket.png");
				
				em.persist(gcvp);
				
				
				//astana
				Group fil1 = new Group("fil1");
				fil1.setParent(gcvp);
				fil1.getProperties().put("address", "Астана, Алматинское р.о. Иманбаева 16");				
				em.persist(fil1);
				
				Group fil2 = new Group("fil2");
				fil2.setParent(gcvp);
				fil2.getProperties().put("address", "Астана, Сарыаркинское р.о. Сейфулина 22");
				em.persist(fil2);
				
				Group fil3 = new Group("fil3");
				fil3.setParent(gcvp);
				fil3.getProperties().put("address", "Астана, Есильское р.о. Достык 13. ЖК Нурсая-2");
				em.persist(fil3);
				//almaty
				Group fil4 = new Group("fil4");
				fil4.setParent(gcvp);
				fil4.getProperties().put("address", "Алматы, Алмалинское р.о. Абылай хана 74а");
				em.persist(fil4);
				
				Group fil5 = new Group("fil5");
				fil5.setParent(gcvp);
				fil5.getProperties().put("address", "Алматы, Жетысуское р.о. Абылай хана 74а");
				em.persist(fil5);
				
				Group fil6 = new Group("fil6");
				fil6.setParent(gcvp);
				fil6.getProperties().put("address", "Алматы, Бостандыкское р.о. Абылай хана 74а");
				em.persist(fil6);
				
				Group fil7 = new Group("fil7");
				fil7.setParent(gcvp);
				fil7.getProperties().put("address", "Алматы, Ауэзовское р.о. мкр. 3, дом 41В");
				em.persist(fil7);
				
				Group fil8 = new Group("fil8");
				fil8.setParent(gcvp);
				fil8.getProperties().put("address", "Алматы, Туркисибское р.о. Тынышбаева 3");
				em.persist(fil8);
				
				Group fil9 = new Group("fil9");
				fil9.setParent(gcvp);
				fil9.getProperties().put("address", "Алматы, Медеуское р.о. Жибек-жолы д.54, помещение №86");
				em.persist(fil9);
				
				Group fil10 = new Group("fil10");
				fil10.setParent(gcvp);
				fil10.getProperties().put("address", "Алматы, Алатауское р.о. мкр. Айгерим, ул. Ленина 89");
				em.persist(fil10);
				
				Group fil11 = new Group("fil11");
				fil11.setParent(gcvp);
				fil11.getProperties().put("address", "Караганда, Жамбыла 2 Операционный отдел №1");
				em.persist(fil11);
				
				Group fil12 = new Group("fil12");
				fil12.setParent(gcvp);
				fil12.getProperties().put("address", "Караганда, Жамбыла 2 Операционный отдел №2");
				em.persist(fil12);
				
				Group fil13 = new Group("fil13");
				fil13.setParent(gcvp);
				fil13.getProperties().put("address", "Караганда, Жамбыла 2 Операционный отдел №3");
				em.persist(fil13);
				
				Group fil14 = new Group("fil14");
				fil14.setParent(gcvp);
				fil14.getProperties().put("address", "Караганда, Жамбыла 2 Операционный отдел №4");
				em.persist(fil14);
				
				Group fil15 = new Group("fil15");
				fil15.setParent(gcvp);
				fil15.getProperties().put("address", "Темиртау, Димитрова 99");
				em.persist(fil15);
				
				
				
				User gcvpAdmin = new User();
				gcvpAdmin.setUsername("admin");
				gcvpAdmin.setRole(Role.LOCALADMIN);
				gcvpAdmin.setGroup(gcvp);
				try {
					gcvpAdmin.setPasswordSalt(HashUtils.getSaltString());
					gcvpAdmin.setPasswordHash(HashUtils.getHashString("q", gcvpAdmin.getPasswordSalt()));
				} catch (Exception e) {
					
				}
				em.persist(gcvpAdmin);
				
				List<Group> fils = new ArrayList<Group>();
				fils.add(fil1);
				fils.add(fil2);
				fils.add(fil3);
				fils.add(fil4);
				fils.add(fil5);
				fils.add(fil6);
				fils.add(fil7);
				fils.add(fil8);
				fils.add(fil9);
				fils.add(fil10);
				fils.add(fil11);
				fils.add(fil12);
				fils.add(fil13);
				fils.add(fil14);
				fils.add(fil15);
				
				List<Integer> operators = new ArrayList<Integer>();
				operators.add(20);
				operators.add(20);
				operators.add(15);
				operators.add(20);
				operators.add(20);
				operators.add(30);
				operators.add(40);
				operators.add(20);
				operators.add(20);
				operators.add(15);
				operators.add(15);
				operators.add(15);
				operators.add(20);
				operators.add(15);
				operators.add(28);
				
				String as1[]={
						"1. Пенсионная выплата, государственная базовая пенсионная выплата",
						"2. Государственные социальные пособия по инвалидности, по случаю потери кормильца и по возрасту (3 вида)",
						"3. Специальные государственные пособия  (20 видов)",
						"4. Государственные специальные пособия  по Списку № 1 и по Списку № 2  (2 вида)",
						"5. Социальные выплаты из ГФСС на случаи утраты трудоспособности,  потери кормильца, потери работы, в связи с беременностью и родами, в связи с уходом за ребенком по достижении возраста 1 года (5 видов)",
						"6. Единовременное государственное пособие по рождению ребенка, пособие по уходу за ребенком по достижении им возраста одного года  (из РБ)",
						"7. Пособие, воспитывающим ребенка-инвалида",
						"8. Единовременная выплата разницы – Государственная гарантия",
						"9. Единовременная  выплата на погребение",
						"10. Единовременные выплаты денежных компенсаций жертвам массовых политических репрессий",
						"11. Единовременной государственной денежной компенсации, пострадавшим на СИЯП",
						"12. Экологические надбавки",
						"13. Единовременное пособие оралманам, средства  на приобретение жилья ораламанам, возмещение  расходов по проезду к  ПМЖ (3 вида)",
						"14. Ежемесячные выплаты после завершения периода капитализации платежей по возмещению вреда (регрессники)",
						"15. Оформление документов в связи с выездом и прибытием получателей пенсий и пособий (в другие регионы Республики и за пределы Республики Казахстан)",
						"16. Отметка новых размеров пенсий и пособий в пенсионном удостоверении",
						"17.  Выдача справок о доходах в различные организации, справок о размере назначенной соцвыплаты по беременности и родам для предоставления по месту работы, для ЦОНа.",
						"18. Предоставление справок с места учебы для продления пособий по потере кормильца, спецгоспособий многодетным семьям",
						"19. Выдача оборотов по пенсионным взносам лицам достигшим пенсионного возраста,  выписок по социальным отчислениям",
						"20. Получение консультации по различным вопросам "
				};
				String as2[]={
					"1. Мемлекеттік базалық зейнетақы төлемі, зейнетақы төлемі",
					"2. Мүгедектігі бойынша, асыраушысынан айырылу жағдайы бойынша және жасына байланысты мемлекеттік әлеуметтік жәрдемақы (3 түрі)",
					"3. Арнаулы мемлекеттiк жәрдемақы (20 түрі)",
					"4. №1 және №2 тiзiм бойынша мемлекеттік арнайы жәрдемақы (2 түрі)",
					"5.Еңбек ету қабiлетiнен айрылу, асыраушысынан айрылу, жүктілігі мен босануына байланысты, бала бір жасқа толғанға дейінгі күтіміне байланысты жағдайларға МӘСҚ-дан әлеуметтік төлемдер (5 түрі)",
					"6.Бала туғанда берілетін біржолғы мемлекеттік жәрдемақы, бала бір жасқа толғанға дейінгі оның күтімі бойынша жәрдемақы (РБ)",
					"7.Мүгедек баланы тәрбиелеушілерге жәрдемақы.",
					"8. Мемлекет кепiлдiк – біржолғы төлем айырмашылығы  ",
					"9.Жерлеуге берiлетiн бiржолғы төлем",
					"10.Жаппай саяси  қуғын-сүргін құрбандарына біржолғы мемлекеттік ақшалай өтемақы төлемдері",
					"11.Семей сынақ ядролық полигонынан зардап шеккендерге біржолғы мемлекеттік ақшалай өтемақы.",
					"12.Экологиялық үстемеақылар.",
					"13.Оралмандарға біржолғы жәрдемақы, оралмандарға үй сатып алуына қаражаттар, ТМЖ жету шығындарын өтеу (3 түрі).",
					"14.Зиянды өтеу жөніндегі төлемдерді капиталдандыру кезеңі аяқталғаннан кейінгі ай сайынғы төлемдер (регрестер).",
					"15.Зейнетақы мен жәрдемақы алушылардың (республиканың басқа өңіріне және Қазақстан Республикасынан тыс жерлеріне) көшіп кетуі мен келуіне байланысты  құжаттарды ресімдеу",
					"16.Зейнеткер куәлігінде зейнетақы мен жәрдемақының  жаңа мөлшерлерін белгiлеу.",
					"17.Әр түрлі ұйымдарға табысы туралы анықтаманы, жұмыс орнына ұсыну үшін жүктілігі мен босануына байланысты тағайындалған әлеуметтік төлемдердің мөлшері туралы анықтаманы ХҚО  беру.",
					"18.Асыраушысынан айрылу бойынша жәрдемақы,көп балалы отбасыларына арнаулы мемлекеттік жәрдемақыларды ұзарту үшін оқу орнынан анықтамаларды ұсыну",
					"19.Зейнет жасына толған адамдарға зейнетақы жарналарының айналымдарын, әлеуметтік аударымдар бойынша үзінді-көшірмесін беру- Нақты белгілі бір терезеге жолдау ",
					"20.Әр түрлi сұрақтар бойынша  консультация алу"
				};
				String lane1[]={
						"fils01a01",
						"fils01a02",
						"fils01a03",
						"fils01a04",
						"fils01a05",
						"fils01a06",
						"fils01a07",
						"fils01a08",
						"fils01a09",
						"fils01a10",
						"fils01a11",
						"fils01a12",
						"fils01a13",
						"fils01a14",
						"fils01a15",
						"fils01a16",
						"fils01a17",
						"fils01a18",
						"fils01a19",
						"fils01a20",
				}; 
				String lane2[]={
						"a-b",
						"c-d",
						"a-b",
						"c-d",
						"a-b",
						"c-d",
						"a-b",
						"c-d",
						"a-b",
						"c-d",
						"a-b",
						"c-d",
						"a-b",
						"c-d",
						"a-b",
						"c-d",
						"a-b",
						"c-d",
						"a-b",
						"c-d",
				};
				List<Integer> dashs = new ArrayList<Integer>();
				dashs.add(3);
				dashs.add(3);
				dashs.add(3);
				
				dashs.add(3);
				dashs.add(3);
				dashs.add(4);
				dashs.add(4);
				dashs.add(3);
				dashs.add(3);
				dashs.add(3);
				
				dashs.add(3);
				dashs.add(3);
				dashs.add(4);
				dashs.add(3);
				dashs.add(3);
				
				em.getTransaction().commit();
				for(int j=0;j<fils.size();j++){
					Set<Lane> laneAll = new HashSet<Lane>();
					String str="";
//					if (!fils.get(j).equals(fil1)){
						int start = 1;
						int end = 999/as1.length;
						for(int i=1;i<=as1.length;i++){
							em.getTransaction().begin();
							Lane l1 = new Lane();
							l1.setGroup(fils.get(j));
							if((j+1)<10){
								if(i<10){
									l1.setUsername("fils0"+(j+1)+"a0"+i);
								}else{
									l1.setUsername("fils0"+(j+1)+"a"+i);
								}
							}else{
								if(i<10){
									l1.setUsername("fils"+(j+1)+"a0"+i);
								}else{
									l1.setUsername("fils"+(j+1)+"a"+i);
								}
							}
							
							if(start<10){
								l1.setRangeStart("00"+start);
							}else if(start<100){
								l1.setRangeStart("0"+start);
							}else{
								l1.setRangeStart(""+start);
							}
							if(end<100){
								l1.setRangeEnd("0"+end);
							}else{
								l1.setRangeEnd(""+end);
							}
							em.persist(l1);
							BundleMessage messageR = new BundleMessage();
							messageR.setKey(String.format("lane.%s.name",l1.getUsername()));
							messageR.setLang(Language.RU);
							messageR.setValue(as1[i-1]);
							em.persist(messageR);
							
							BundleMessage messageK = new BundleMessage();
							messageK.setKey(String.format("lane.%s.name",l1.getUsername()));
							messageK.setLang(Language.KK);
							messageK.setValue(as2[i-1]);
							em.persist(messageK);
							start = end+1;
							end = end+999/as1.length;
							laneAll.add(l1);
							em.getTransaction().commit();
						}
//					}else{
//						int start = 1;
//						int end = 999/as1.length;
//						for(int i=1;i<=lane1.length;i++){
//							em.getTransaction().begin();
//							Lane l1 = new Lane();
//							l1.setGroup(fils.get(j));
//							if((j+1)<10){
//								if(i<10){
//									l1.setUsername("fils0"+(j+1)+"a0"+i);
//								}else{
//									l1.setUsername("fils0"+(j+1)+"a"+i);
//								}
//							}else{
//								if(i<10){
//									l1.setUsername("fils"+(j+1)+"a0"+i);
//								}else{
//									l1.setUsername("fils"+(j+1)+"a"+i);
//								}
//							}
//							
//							if(start<10){
//								l1.setRangeStart("00"+start);
//							}else if(start<100){
//								l1.setRangeStart("0"+start);
//							}else{
//								l1.setRangeStart(""+start);
//							}
//							if(end<100){
//								l1.setRangeEnd("0"+end);
//							}else{
//								l1.setRangeEnd(""+end);
//							}
//							em.persist(l1);
//							BundleMessage messageR = new BundleMessage();
//							messageR.setKey(String.format("lane.%s.name",l1.getUsername()));
//							messageR.setLang(Language.RU);
//							messageR.setValue(lane2[i-1]);
//							em.persist(messageR);
//							
//							BundleMessage messageK = new BundleMessage();
//							messageK.setKey(String.format("lane.%s.name",l1.getUsername()));
//							messageK.setLang(Language.KK);
//							messageK.setValue(lane2[i-1]);
//							em.persist(messageK);
//							start = end+1;
//							end = end+999/lane1.length;
//							laneAll.add(l1);
//							em.getTransaction().commit();
//						}
//						str = "[{";
//						for(int i=0;i<as1.length;i++){
//							String lanes = "[";
//							if(fils.get(j).equals(fil1)){
//								for(int k=0;k<lane1.length;k++){
//									lanes+="\""+lane1[k]+"\"";
//									if(k!=lane1.length-1){
//										lanes+=",";
//									}
//								}
//							}
//							lanes+="]";
//							str+="\"kk\":\""+as2[i]+"\",\"ru\":\""+as1[i]+"\",\"sublanes\":"+lanes+",\"id\":"+(i+1);
//							if(i != as1.length-1){
//								str+="},{";
//							}
//						}
//						str+="}]";
//					
//					}
					
					em.getTransaction().begin();
					User gcvpKiosk1 = new User();
					gcvpKiosk1.setRole(Role.KIOSK);
					gcvpKiosk1.setGroup(fils.get(j));
					gcvpKiosk1.setUsername("kiosk"+(j+1));
					if(!str.equals("")){
						gcvpKiosk1.setData(str);
						str="";
					}
					try {
						gcvpKiosk1.setPasswordSalt(HashUtils.getSaltString());
						gcvpKiosk1.setPasswordHash(HashUtils.getHashString("q", gcvpKiosk1.getPasswordSalt()));
					} catch (Exception e) {
						
					}
					em.persist(gcvpKiosk1);
					em.getTransaction().commit();
					em.getTransaction().begin();
					User monitor = new User();
					monitor.setRole(Role.MONITOR);
					monitor.setGroup(fils.get(j));
					monitor.setUsername("monitor"+(j+1));
					try {
						monitor.setPasswordSalt(HashUtils.getSaltString());
						monitor.setPasswordHash(HashUtils.getHashString("q", monitor.getPasswordSalt()));
					} catch (Exception e) {
						
					}
					em.persist(monitor);
					em.getTransaction().commit();
					for(int i=1;i<=operators.get(j);i++){
						em.getTransaction().begin();
						MonitorUnit mu1 = new MonitorUnit();
						int s = j+1;
						if(s<10){
							mu1.setUsername("mu0"+(j+1)+""+i); 
						}else{
							mu1.setUsername("mu"+(j+1)+""+i);
						}
						
						mu1.setGroup(fils.get(j));
						try {
							mu1.setPasswordSalt(HashUtils.getSaltString());
							mu1.setPasswordHash(HashUtils.getHashString("q", mu1.getPasswordSalt()));
						} catch (Exception e) {
							
						}
						em.persist(mu1);
						em.getTransaction().commit();
						em.getTransaction().begin();
						Unit gcvpunit1 = new Unit();
						if(s<10){
							gcvpunit1.setUsername("unit0"+(j+1)+""+i);
						}else{
							gcvpunit1.setUsername("unit"+(j+1)+""+i);
						}
						gcvpunit1.setGroup(fils.get(j));
						if(i<10){
							gcvpunit1.setWindow("0"+i);
						}else{
							gcvpunit1.setWindow(""+i);
						}
						gcvpunit1.setLanes(laneAll);
						gcvpunit1.setMonitorUnit(mu1);
						try {
							gcvpunit1.setPasswordSalt(HashUtils.getSaltString());
							gcvpunit1.setPasswordHash(HashUtils.getHashString("q", gcvpunit1.getPasswordSalt()));
						} catch (Exception e) {
							
						}
						em.persist(gcvpunit1);
						em.getTransaction().commit();
					}
					em.getTransaction().begin();
					for(int i=1;i<=dashs.size();i++){
						int s = j+1;
						Dashboard beelineDash = new Dashboard();
						if(s<10){
							beelineDash.setUsername("dash0"+(j+1)+""+i);
						}else{
							beelineDash.setUsername("dash"+(j+1)+""+i);
						}
						beelineDash.setGroup(fils.get(j));
						beelineDash.setDashboardLanes(laneAll);
						try {
							beelineDash.setPasswordSalt(HashUtils.getSaltString());
							beelineDash.setPasswordHash(HashUtils.getHashString("q", beelineDash.getPasswordSalt()));
						} catch (Exception e) {
							
						}
						em.persist(beelineDash);
					}
					em.getTransaction().commit();
				}
				em.getTransaction().begin();
				
				
				try{
					File kioskFile = new File(spank+"/kiosk.html");
					Scanner kioskSc = new Scanner(kioskFile,"UTF-8");
					String kioskHtml = "";
					while(kioskSc.hasNextLine()){
						kioskHtml+=kioskSc.nextLine();
					}
					kioskSc.close();
					
					File unitFile = new File(spank+"/unit.html");
					Scanner unitSc = new Scanner(unitFile,"UTF-8");
					String unitHtml = "";
					while(unitSc.hasNextLine()){
						unitHtml+=unitSc.nextLine();
					}
					unitSc.close();
					
					File dashFile = new File(spank+"/dashboard2.html");
					Scanner dashSc = new Scanner(dashFile,"UTF-8");
					String dashHtml = "";
					while(dashSc.hasNextLine()){
						dashHtml+=dashSc.nextLine();
					}
					dashSc.close();
					
					File adminFile = new File(spank+"/admin.html");
					Scanner adminSc = new Scanner(adminFile,"UTF-8");
					String adminHtml = "";
					while(adminSc.hasNextLine()){
						adminHtml+=adminSc.nextLine();
					}
					adminSc.close();
					
					File monitorFile = new File(spank+"/monitor.html");
					Scanner monitorSc = new Scanner(monitorFile,"UTF-8");
					String monitorHtml = "";
					while(monitorSc.hasNextLine()){
						monitorHtml+=monitorSc.nextLine();
					}
					monitorSc.close();
					
					gcvp.setDashboardTemplate(dashHtml);
					gcvp.setKioskTemplate(kioskHtml);
					gcvp.setUnitTemplate(unitHtml);
					gcvp.setMonitorTemplate(monitorHtml);
				}catch(Exception e){
					e.printStackTrace();
				}
				em.getTransaction().commit();
	}

	public static void insertMessages() throws QueuePluginException {
		new Work<Object>() {
			@Override
			protected Object work(EntityManager em) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("group.gcvp.name", "gcvp");
				map.put("group.gcvp.shortname", "gcvp");
				map.put("lane.a.name", "Документирование");
				map.put("lane.b.name", "Выдача готовых документов");
				map.put("lane.c.name", "Касса");
				map.put("lane.d.name", "Операционный зал");
				map.put("lane.e.name", "Выдача кредитов");
				map.put("lane.f.name", "Операции с карточками");
				map.put("lane.g.name", "Платежи");

				map.put("token.status.waiting", "Клиент ожидает");
				map.put("token.status.called", "Клиент вызван");
				map.put("token.status.started", "Обслуживание");
				map.put("token.status.ended", "Обслуживание завершено");
				map.put("token.status.postponed", "Отложен");
				map.put("token.status.missed", "Клиент не пришел");
				map.put("token.status.transferred", "Клиент перенаправлен");

				for (Entry<String, String> entry : map.entrySet()) {
					BundleMessage message = new BundleMessage();
					message.setKey(entry.getKey());
					message.setValue(entry.getValue());
					em.persist(message);
				}

				Map<String, String> map_en = new HashMap<String, String>();
				map_en.put("lane.a.name", "Documents");
				map_en.put("lane.b.name", "Issuance of final documents");
				map_en.put("lane.c.name", "Cash");
				map_en.put("lane.d.name", "Операционный зал");
				map_en.put("lane.e.name", "Выдача кредитов");
				map_en.put("lane.f.name", "Operations");
				map_en.put("lane.g.name", "Payments");

				for (Entry<String, String> entry : map_en.entrySet()) {
					BundleMessage message = new BundleMessage();
					message.setKey(entry.getKey());
					message.setValue(entry.getValue());
					message.setLang(Language.EN);
					em.persist(message);
				}

				Map<String, String> map_kz = new HashMap<String, String>();
				map_kz.put("lane.a.name", "Құжаттау");
				map_kz.put("lane.b.name", "Даяр құжаттарды беру");
				map_kz.put("lane.c.name", "Касса");
				map_kz.put("lane.d.name", "Операциялық зал");
				map_kz.put("lane.e.name", "Выдача кредитов");
				map_kz.put("lane.f.name", "Операции с карточками");
				map_kz.put("lane.g.name", "Төлеулер");

				for (Entry<String, String> entry : map_kz.entrySet()) {
					BundleMessage message = new BundleMessage();
					message.setKey(entry.getKey());
					message.setValue(entry.getValue());
					message.setLang(Language.KK);
					em.persist(message);
				}
				return null;
			}
		}.workInTransaction();
	}
}
