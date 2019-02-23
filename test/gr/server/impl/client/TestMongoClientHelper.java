package gr.server.impl.client;

import org.bson.Document;
import org.junit.Test;

import gr.server.application.exception.UserExistsException;
import gr.server.data.user.enums.BetStatus;
import gr.server.data.user.model.User;
import gr.server.data.user.model.UserPrediction;

public class TestMongoClientHelper {
	
	/**
	 * Calls mongo db in order to store a new document in bets collection
	 */
	//@Test
	public void testPlacePrediction(){
		MongoClientHelperImpl mHelper = new MongoClientHelperImpl();
		UserPrediction userPrediction = new UserPrediction();
		userPrediction.setUserId("user1");
		userPrediction.setBetAmount(30);
		userPrediction.setEventId("1232313");
		userPrediction.setStatus(BetStatus.PENDING);
		Document placePrediction = mHelper.placePrediction(userPrediction);
		System.out.println(placePrediction.getObjectId("_id"));
	}
	
	/**
	 * Calls mongo db in order to store a new {@link User} in user collection
	 * @throws UserExistsException 
	 */
	@Test(expected=UserExistsException.class)
	public void testCreateUser() throws UserExistsException{
		MongoClientHelperImpl mHelper = new MongoClientHelperImpl();
		User user = new User();
		user.setUsername("kostas");
		mHelper.createUser(user);
	}

}
