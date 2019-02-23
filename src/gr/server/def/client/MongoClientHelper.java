package gr.server.def.client;

import gr.server.application.exception.UserExistsException;
import gr.server.client.theoddsapi.data.UpcomingEvent;
import gr.server.data.user.model.User;
import gr.server.data.user.model.UserPrediction;

import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;

public interface MongoClientHelper {

	public MongoClient connect();
	
	public Document placePrediction(UserPrediction userPrediction);
	
	/**
	 * Creates a new {@link User} in the 'user' collection of the database.
	 * In case a {@link Document} with the same username already exists,
	 * a {@link UserExistsException} is thrown.
	 * 
	 * @param user
	 * @return
	 * @throws UserExistsException
	 */
	public Document createUser(User user) throws UserExistsException;
	
	/**
	 * This will run via a Thread every morning in order to fetch new events 
	 * and replace the existing ones.
	 * 
	 * @param leaguesToSports
	 */
	public void updateEvents(List<UpcomingEvent> leaguesToSports);
	
}
