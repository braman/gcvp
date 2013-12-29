package kz.bee.cloud.queue.mobile;

import kz.bee.util.QueuePluginException;

public class MobileException extends QueuePluginException {

	public MobileException(int id, String message) {
		super(id, message);
	}

}
