package gr.server.def.service;

import gr.server.application.exception.UserExistsException;
import gr.server.data.user.model.User;
import gr.server.data.user.model.UserPrediction;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.bson.Document;

/**
 * Rest service for sending data to the mobile devices.
 *  
 */
public interface MyBetOddsService {

	public String getLeagues() throws IOException;
	
	public String placeBet(InputStream incoming);
	
	public Document createUser(User user) throws UserExistsException;

	List<UserPrediction> getMyOpenBets(String id);

	String getSportsWithEvents();

	User getUser(String id);

	String getLeaderBoard();


}
