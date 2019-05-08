package gr.server.def.client;

import gr.server.application.exception.UserExistsException;
import gr.server.data.api.model.Competition;
import gr.server.data.api.model.CountryWithCompetitions;
import gr.server.data.api.model.Odd;
import gr.server.data.api.model.events.Event;
import gr.server.data.user.enums.SupportedLeagues;
import gr.server.data.user.model.User;
import gr.server.data.user.model.UserBet;
import gr.server.data.user.model.UserPrediction;
import gr.server.impl.client.ApiFootballClient;
import gr.server.impl.service.MyBetOddsServiceImpl;

import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.mongodb.MongoClient;

public interface MongoClientHelper {

	public MongoClient connect();
	
	/**
	 * A {@link User} places a new {@link UserBet} that involves a list of {@link UserPrediction}s. 
	 * 
	 * @param userBet
	 * @return
	 */
	public UserBet placeBet(UserBet userBet);
	
	/**
	 * Creates a new {@link User} in the 'user' collection of the database.
	 * In case a {@link Document} with the same username already exists,
	 * a {@link UserExistsException} is thrown.
	 * 
	 * @param user
	 * @return
	 * @throws UserExistsException
	 */
	public User createUser(User user) throws UserExistsException;
	
	/**
	 * Called to fetch the open bets for a {@link User}.
	 * 
	 * @param id the user id
	 * @return
	 */
	User getUser(String id);
	
	/**
	 * {@link ApiFootballClient} will call this method to store the newly fetched leagues with their competitions, events, etc.
	 * 
	 * @param competitionsPerCountry
	 */
	void storeCompetitionsWithEventsAndOdds(Map<SupportedLeagues, List<Competition>> competitionsPerCountry);
	
	/**
	 * {@link MyBetOddsServiceImpl} will call this method upon user's REST call.
	 * 
	 * Returns a list of {@link CountryWithCompetitions}.
	 * Every competition contains a list of {@link Event}s,
	 * which in turns contains an {@link Odd} for the event.
	 * 
	 * @return
	 */
	List<CountryWithCompetitions> retrieveCompetitionsWithEventsAndOdds();

	/**
	 * For every open {@link UserBet}, the system will iterate through its {@link UserPrediction}s
	 * and will settle it favourably or not, depending on the FT status of all its predictions.
	 * 
	 */
	void settleBets();
	
}
