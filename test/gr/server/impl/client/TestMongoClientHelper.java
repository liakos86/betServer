package gr.server.impl.client;

import gr.server.application.exception.UserExistsException;
import gr.server.data.api.model.CountryWithCompetitions;
import gr.server.data.user.model.User;
import gr.server.data.user.model.UserBet;
import gr.server.data.user.model.UserPrediction;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestMongoClientHelper {
	
	/**
	 * Calls mongo db in order to store a new document in bets collection
	 */
	@Test
	public void testPlacePrediction(){
		MongoClientHelperImpl mHelper = new MongoClientHelperImpl();
		UserBet userBet = new UserBet();
		userBet.setUserId("user1");
		userBet.setBetAmount(30);
		userBet.setBetStatus("open");
		
		List<UserPrediction> preds = new ArrayList<UserPrediction>();
		UserPrediction pred = new UserPrediction();
		pred.setEventId("395975");
		pred.setPrediction("1");
		preds.add(pred);
		userBet.setPredictions(preds);
		userBet = mHelper.placeBet(userBet);
		System.out.println(userBet.getBetId());//(placePrediction.getObjectId("_id"));
	}
	
	/**
	 * Calls mongo db in order to store a new {@link User} in user collection
	 * @throws UserExistsException 
	 */
	//@Test(expected=UserExistsException.class)
	public void testCreateUser() throws UserExistsException{
		MongoClientHelperImpl mHelper = new MongoClientHelperImpl();
		User user = new User("");
		user.setUsername("kostas");
		mHelper.createUser(user);
	}
	
	/**
	 * Calls mongo db in order to retrieve {@link User}'s open bets
	 */
	@Test
	public void testGetOpenBets() throws UserExistsException{
	
	
	
		MongoClientHelperImpl mHelper = new MongoClientHelperImpl();
		User jim = mHelper.getUser("5c718c6c67a90b10dc34c244");
		System.out.println(jim.getUserBets().get(0).getPredictions().get(0).getPrediction());
	}
	
	@Test
	public void testGetCountriesWithCompetitions(){
		
		List<CountryWithCompetitions> retrieveCompetitionsWithEventsAndOdds = new MongoClientHelperImpl().retrieveCompetitionsWithEventsAndOdds();
		System.out.println(retrieveCompetitionsWithEventsAndOdds.size());
		
	}
	

}
