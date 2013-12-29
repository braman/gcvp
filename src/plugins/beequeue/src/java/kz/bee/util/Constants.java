package kz.bee.util;

import java.text.SimpleDateFormat;

import org.xmpp.forms.DataForm;
import org.xmpp.forms.FormField;
import org.xmpp.packet.JID;

public final class Constants {
	public static final JID QUEUE;
	public static final DataForm DATAFORM;
	public static final SimpleDateFormat TIMEFORMAT;
	
	static{
		QUEUE = new JID("queue@cq.b2e.kz");
		TIMEFORMAT = new SimpleDateFormat("hh:mm:ss");
		DATAFORM = new DataForm(DataForm.Type.submit);
		FormField field = DATAFORM.addField();
		field = DATAFORM.addField();
	    field.setVariable("pubsub#subscription_type");
	    field.addValue("items");
	}
}
