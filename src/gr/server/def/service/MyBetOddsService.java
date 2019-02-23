package gr.server.def.service;

import gr.server.application.exception.UserExistsException;
import gr.server.data.user.model.User;
import gr.server.data.user.model.UserPrediction;

import java.io.IOException;

import org.bson.Document;

/**
 * Rest service for sending data to the mobile devices.
 *  
 */
public interface MyBetOddsService {

	public String getUpcoming() throws IOException;

	public String getLeagues() throws IOException;
	
	public Document placeBet(UserPrediction userPrediction);
	
	public Document createUser(User user) throws UserExistsException;

}
