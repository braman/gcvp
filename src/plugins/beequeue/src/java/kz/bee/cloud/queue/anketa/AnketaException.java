package kz.bee.cloud.queue.anketa;

import kz.bee.util.QueuePluginException;

public class AnketaException extends QueuePluginException {

	public AnketaException(int id, String message) {
		super(id, message);
	}

}
