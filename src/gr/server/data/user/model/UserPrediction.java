package gr.server.data.user.model;

import gr.server.data.api.model.events.Event;

import java.io.Serializable;

/**
 * This class represents a prediction on an {@link Event} by a {@link User}.
 * It participates along with other {@link UserPrediction}s into a {@link UserBet}
 *
 */
public class UserPrediction implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String eventId;
	
	String prediction;
	
	String result;
	
	Double multiplier;
	
	public Double getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(Double multiplier) {
		this.multiplier = multiplier;
	}
	
	public String getEventId() {
		return eventId;
	}
	
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	public String getPrediction() {
		return prediction;
	}

	public void setPrediction(String prediction) {
		this.prediction = prediction;
	}

}
