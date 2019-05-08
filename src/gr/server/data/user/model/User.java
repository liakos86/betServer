package gr.server.data.user.model;

import java.util.ArrayList;
import java.util.List;

import gr.server.data.ServerConstants;

/**
 * User of the application.
 * 
 * @author liakos
 *
 */
public class User {

	/**
	 * Unique id as defined by mongoDb during insert.
	 */
	String id;
	
	/**
	 * The unique username that the app user will demand.
	 */
	String username;

	/**
	 * Each user has a balance, i.e. a virtual amount.
	 */
	Double balance;
	
	/**
	 * Number of won slips.
	 */
	Integer wonSlipsCount;
	
	/**
	 * Number of lost slips.
	 */
	Integer lostSlipsCount;
	
	/**
	 * Number of won events.
	 */
	Integer wonEventsCount;
	
	/**
	 * Number of lost events.
	 */
	Integer lostEventsCount;
	
	List<UserBet> userBets;

	public User(String id) {
		this.id = id;
		this.balance = ServerConstants.STARTING_BALANCE;
		this.lostEventsCount = 0;
		this.lostSlipsCount = 0;
		this.wonEventsCount = 0;
		this.wonSlipsCount = 0;
		this.userBets = new ArrayList<UserBet>();
	}
	
	public List<UserBet> getUserBets() {
		return userBets;
	}

	public void setUserBets(List<UserBet> userBets) {
		this.userBets = userBets;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Integer getWonSlipsCount() {
		return wonSlipsCount;
	}

	public void setWonSlipsCount(Integer wonSlipsCount) {
		this.wonSlipsCount = wonSlipsCount;
	}

	public Integer getLostSlipsCount() {
		return lostSlipsCount;
	}

	public void setLostSlipsCount(Integer lostSlipsCount) {
		this.lostSlipsCount = lostSlipsCount;
	}

	public Integer getWonEventsCount() {
		return wonEventsCount;
	}

	public void setWonEventsCount(Integer wonEventsCount) {
		this.wonEventsCount = wonEventsCount;
	}

	public Integer getLostEventsCount() {
		return lostEventsCount;
	}

	public void setLostEventsCount(Integer lostEventsCount) {
		this.lostEventsCount = lostEventsCount;
	}
	
	
	
}
