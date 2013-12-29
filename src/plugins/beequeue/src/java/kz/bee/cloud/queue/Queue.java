package kz.bee.cloud.queue;

import java.util.List;

import kz.bee.cloud.queue.model.Group;
import kz.bee.cloud.queue.model.Lane;
import kz.bee.cloud.queue.model.Ticket;
import kz.bee.cloud.queue.model.Token;
import kz.bee.cloud.queue.model.Unit;
import kz.bee.cloud.queue.model.User;

public interface Queue {

	public Token call(Unit unit);
	
	public Token call(Unit unit,Lane lane);
	
	public Token recall(Token token);
	
	public Token start(Unit unit);
	
	public Token end(Unit unit);
	
	public Token advertisement(Unit unit);
	
	/**
	 * Creates new ticket, persists to database
	 * */
	public Ticket enqueue(Group group, String personalId, List<Lane> lanes, User.Language lang, int priority);
	
	/**
	 * Creates new ticket, do NOT persist to database
	 * */
	public Ticket createTicket(Group group, String personalId, List<Lane> lanes, User.Language lang);

	/**
	 * Returns current token being served
	 * */
	public Token current(Unit unit);
	
	/**
	 * Changes token's lane type and unit.
	 * Creates new token and ends current token.
	 * Sets TRANSFERED status to token.
	 * @param token. Token to change
	 * @param laneType. Destination laneType
	 * @param unit. Destination unit
	 * @return new token
	 * */
	public Token transfer(Token token,String laneName, User target,String redioButton,boolean returnToken);
	
	/**
	 * Transfers token back to queue
	 * Creates new token and ends current token.
	 * @param token. Token to transfer
	 * @return new token
	 * */
	public Token transfer(Token token, String radioButton,boolean returnToken);
	
	/**
	 * Postpones token and sets status to POSTPONED
	 * Unit may call later this token by calling him manually from UI by ticketcode
	 * */
	public Token postpone(Token token);
	
	public Integer getTokenCount(Unit unit, Token.Status ... statuses);

	
	
}
