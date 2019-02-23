package gr.server.impl.client;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gr.server.application.exception.UserExistsException;
import gr.server.client.theoddsapi.data.UpcomingEvent;
import gr.server.data.Server;
import gr.server.data.ServerConstants;
import gr.server.data.user.model.User;
import gr.server.data.user.model.UserPrediction;
import gr.server.def.client.MongoClientHelper;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertManyOptions;



public class MongoClientHelperImpl 
implements MongoClientHelper {
	
	/**
	 * Singleton for the mongo client.
	 */
	private MongoClient mongoClient;

	public MongoClient connect(){
		MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
		return mongoClient;
	}

	@Override
	public Document placePrediction(UserPrediction userPrediction) {
		MongoClient client = getMongoClient();
		MongoDatabase database = client.getDatabase("BETDB");
		MongoCollection<Document> collection = database.getCollection("userBets");
		
		 Document prediction = new Document("userId", userPrediction.getUserId())
         .append("prediction", userPrediction.getPrediction())
		 .append("betAmount", userPrediction.getBetAmount())
		 .append("eventId", userPrediction.getEventId())
		 .append("status", userPrediction.getStatus().ordinal());
		 
		 collection.insertOne(prediction);
		 
		 return prediction;
	}

	@Override
	public Document createUser(User user) throws UserExistsException {
		MongoClient client = getMongoClient();
		MongoCollection<Document> users = client.getDatabase("BETDB").getCollection("user");
		
		 Document existingUser = new Document("username", user.getUsername());
		 FindIterable<Document> find = users.find(existingUser);
		 
		 Document first = find.first();
		 if (first != null){
			 throw new UserExistsException("User " + user.getUsername() + " already exists");
		 }
		 
		 Document newUser = new Document("username", user.getUsername())
		 .append("wonEventsCount", 0)
		 .append("lostEventsCount", 0)
		 .append("wonSlipsCount", 0)
		 .append("lostSlipsCount", 0)
		 .append("balance", ServerConstants.STARTING_BALANCE)
		 ;
		
		 users.insertOne(newUser);
		 
		return newUser;
	}

	@Override
	public void updateEvents(List<UpcomingEvent> list) {
		
		List<Document> newEvents = new ArrayList<Document>();
		for (UpcomingEvent upcomingEvent : list) {
			
			 Document newEvent = new Document("league", upcomingEvent.getSport_key())
			 .append("sport", "socccer")
			 .append("commenceTime", upcomingEvent.getCommence_time())
			 .append("homeTeam", upcomingEvent.getHome_team())
			 .append("teams", "[" + new Document("team1", upcomingEvent.getTeams().get(0)).append("team2", upcomingEvent.getTeams().get(0)) +"]")
			// .append("odds", new Document ("h2h" , upcomingEvent.getSites().get(0).getOdds().getH2h()))
			 ;
			 
				
			newEvents.add(newEvent);
		}
		MongoClient client = getMongoClient();
		MongoCollection<Document> events = client.getDatabase("BETDB").getCollection("event");
		
		events.insertMany(newEvents);	

		for (Map.Entry<String, List<String>> entry : Server.AVAILABLE_LEAGUES.entrySet()) {
			
			
			
		}
		
	}

	/**
	 * mongoClient is a Singleton.
	 * We make sure here.
	 * 
	 * @return
	 */
	private MongoClient getMongoClient() {
		if (mongoClient == null){
			mongoClient = connect();
		}
		return mongoClient;
	}

	
}
