package kz.bee.cloud.queue.openfire.plugin;

import kz.bee.cloud.queue.Messenger;
import kz.bee.util.Constants;
import kz.bee.util.QLog;

import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

public class QueueInterceptor implements PacketInterceptor {

	public void interceptPacket(Packet packet, Session session, boolean incoming, boolean processed) throws PacketRejectedException {
		if(processed){	return;	 }
		if(!incoming){  return;  }
		
		if(packet instanceof Message){
			Message message = (Message) packet;
			QLog.debug("XML Message from #1: #0",message.toXML(),message.getFrom());
			Message response = null;
			if(message!=null && message.getTo()!=null && message.getTo().equals(Constants.QUEUE)){
				response = Messenger.getInstance().createResponse(message);
				if(response!=null){
					session.process(response);	
				}
				throw new PacketRejectedException();
			}
		}else if(packet instanceof Presence){
			Presence presence = (Presence) packet;
			QLog.debug("Presence:#0",presence.toXML());
		}
	}

}
