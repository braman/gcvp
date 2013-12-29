package kz.bee.cloud.queue.reservation;

import kz.bee.util.QueuePluginException;

public class ReservationException extends QueuePluginException {

	public ReservationException(int id, String message) {
		super(id, message);
	}

}
