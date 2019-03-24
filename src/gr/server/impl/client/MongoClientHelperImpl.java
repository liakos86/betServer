package gr.server.impl.client;
import gr.client.android.model.AndroidUpcomingEvent;
import gr.server.application.exception.UserExistsException;
import gr.server.client.theoddsapi.data.UpcomingEvent;
import gr.server.data.ServerConstants;
import gr.server.data.user.enums.BetStatus;
import gr.server.data.user.model.User;
import gr.server.data.user.model.UserPrediction;
import gr.server.def.client.MongoClientHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;



public class MongoClientHelperImpl 
implements MongoClientHelper {
	
	/**
	 * Singleton for the mongo client.
	 */
	private MongoClient mongoClient;
	
	/**
	 * mongoClient is a Singleton.
	 * We make sure here.
	 * 
	 * TODO: make static?
	 * 
	 * @return
	 */
	private MongoClient getMongoClient() {
		if (mongoClient == null){
			mongoClient = connect();
		}
		return mongoClient;
	}

	public MongoClient connect(){
		MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
		return mongoClient;
	}

	@Override
	public UserPrediction placePrediction(UserPrediction userPrediction) {
		MongoClient client = getMongoClient();
		MongoDatabase database = client.getDatabase("BETDB");
		MongoCollection<Document> collection = database.getCollection("userBets");
		
		 Document prediction = new Document("userId", userPrediction.getUserId())
         .append("prediction", userPrediction.getPrediction())
		 .append("betAmount", userPrediction.getBetAmount())
		 .append("eventId", userPrediction.getEventId())
		 .append("status", BetStatus.PENDING.ordinal());
		 
		 collection.insertOne(prediction);
		 
		 userPrediction.setPredictionId(prediction.getObjectId("_id").toString());
		 
		 return userPrediction;
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
			
			double[] h2h = upcomingEvent.getSites().get(0).getOdds().getH2h();
			
			BasicDBList dbteams = new BasicDBList();
			dbteams.add(upcomingEvent.getTeams().get(0));
			dbteams.add(upcomingEvent.getTeams().get(1));
			
			BasicDBList h2hList = new BasicDBList();
			h2hList.add(h2h[0]);
			h2hList.add(h2h[1]);
			h2hList.add(h2h[2]);
			
			 Document newEvent = new Document("league", upcomingEvent.getSport_key())
			 .append("sport", "socccer")
			 .append("commenceTime", upcomingEvent.getCommence_time())
			 .append("homeTeam", upcomingEvent.getHome_team())
			 .append("teams", dbteams)
			 .append("odds", h2hList)
			 //.append("teams", "[" + new Document("team1", upcomingEvent.getTeams().get(0)).append("team2", upcomingEvent.getTeams().get(1)) +"]")
			 //.append("odds", "[" + new Document ("h2h" , "[" + new Document ("home", h2h[0]).append("away", h2h[1]).append("draw", h2h[2]) + "]"))
			 ;
			 
				
			newEvents.add(newEvent);
		}
		MongoClient client = getMongoClient();
		MongoCollection<Document> events = client.getDatabase("BETDB").getCollection("event");
		
		events.insertMany(newEvents);	

		//for (Map.Entry<String, List<String>> entry : Server.AVAILABLE_LEAGUES.entrySet()) {
			
			
			
		//}
		
	}

	@Override
	public List<UserPrediction> getOpenBetsFor(String id) {
		MongoClient client = getMongoClient();
		MongoCollection<Document> bets = client.getDatabase("BETDB").getCollection("userBets");
		
		 Document userBets = new Document("userId", id);
		 FindIterable<Document> find = bets.find(userBets);
		 
		 List<UserPrediction> predictions  = new ArrayList<UserPrediction>();
		 for (Document document : find) {
			 String json = document.toJson();
			 UserPrediction prediction = new Gson().fromJson(json,
						new TypeToken<UserPrediction>() {}.getType());
			 predictions.add(prediction);
		 }
		 
		 return predictions;
	}
	
	@Override
	public Map<String, Map<String, List<AndroidUpcomingEvent>>> retrieveSportsWithEvents(){
		Map<String, Map<String, List<AndroidUpcomingEvent>>> sportsWithEvents = new HashMap<String, Map<String,List<AndroidUpcomingEvent>>>();
		MongoClient client = getMongoClient();
		MongoCollection<Document> events = client.getDatabase("BETDB").getCollection("event");
		
		List<AndroidUpcomingEvent> allEvents = new ArrayList<AndroidUpcomingEvent>();
		FindIterable<Document> find = events.find();
		for (Document event : find){
			String json = event.toJson();
			AndroidUpcomingEvent newEvent = new Gson().fromJson(json,
					new TypeToken<AndroidUpcomingEvent>() {}.getType());
			allEvents.add(newEvent);
		}
		
		HashMap<String, List<AndroidUpcomingEvent>> englandMap = new HashMap<String, List<AndroidUpcomingEvent>>();
		englandMap.put("epl", allEvents);
		sportsWithEvents.put("soccer", englandMap);
		
		return sportsWithEvents;
	}

	@Override
	public User getUser(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<User> retrieveLeaderBoard() {
		MongoClient client = getMongoClient();
		MongoCollection<Document> users = client.getDatabase("BETDB").getCollection("user");
		
		
		Document sortField = new Document("balance", -1);
		FindIterable<Document> leaders = users.find( ).limit(10).sort( sortField );
		

		List<User> leaderBoardUsers = new ArrayList<User>();
		for (Document leader : leaders){
			String json = leader.toJson();
			User user = new Gson().fromJson(json,
					new TypeToken<User>() {}.getType());
			leaderBoardUsers.add(user);
		}
		
		
		return leaderBoardUsers;
	}
	
}
