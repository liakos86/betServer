package gr.server.def.client;

import org.bson.Document;

import gr.server.application.exception.UserExistsException;
import gr.server.data.user.model.User;
import gr.server.data.user.model.UserPrediction;

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
	
}
