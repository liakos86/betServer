package gr.server.data.user.model;

import java.io.Serializable;
import java.util.List;


/**
 * A bet that is placed by a {@link User}.
 * Involves a list of predictions.
 * 
 * @author liakos
 *
 */
public class UserBet implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	String mongoId;

	String mongoUserId;
	
	int betStatus;
	
	Integer betAmount;
	
	List<UserPrediction> predictions;
	
	String betPlaceDate;
	
	
	
	public String getMongoId() {
		return mongoId;
	}

	public void setMongoId(String mongoId) {
		this.mongoId = mongoId;
	}

	public String getMongoUserId() {
		return mongoUserId;
	}

	public void setMongoUserId(String mongoUserId) {
		this.mongoUserId = mongoUserId;
	}

	public String getBetPlaceDate() {
		return betPlaceDate;
	}

	public void setBetPlaceDate(String betPlaceDate) {
		this.betPlaceDate = betPlaceDate;
	}
	
	public List<UserPrediction> getPredictions() {
		return predictions;
	}

	public void setPredictions(List<UserPrediction> predictions) {
		this.predictions = predictions;
	}

	public Integer getBetAmount() {
		return betAmount;
	}

	public void setBetAmount(Integer betAmount) {
		this.betAmount = betAmount;
	}


	public int getBetStatus() {
		return betStatus;
	}

	public void setBetStatus(int betStatus) {
		this.betStatus = betStatus;
	}

	public Double getPossibleEarnings() {
		 Double possibleEarnings = this.getBetAmount().doubleValue();
	      for (UserPrediction userPrediction : this.getPredictions()) {
			possibleEarnings *= userPrediction.getOddValue();
		}
	    return possibleEarnings;
	}


}
