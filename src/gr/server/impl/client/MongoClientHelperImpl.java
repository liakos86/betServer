package gr.server.impl.client;
import gr.server.application.exception.UserExistsException;
import gr.server.data.ServerConstants;
import gr.server.data.api.model.Competition;
import gr.server.data.api.model.CountryWithCompetitions;
import gr.server.data.api.model.Odd;
import gr.server.data.api.model.events.Event;
import gr.server.data.user.enums.SupportedLeagues;
import gr.server.data.user.model.User;
import gr.server.data.user.model.UserBet;
import gr.server.data.user.model.UserPrediction;
import gr.server.def.client.MongoClientHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.BasicDBList;
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
	public void settleBets(){
		
		Set<Event> finishedEvents = new HashSet<Event>();
		
		List<CountryWithCompetitions> retrieveCompetitionsWithEventsAndOdds = retrieveCompetitionsWithEventsAndOdds();
		for (CountryWithCompetitions countryWithCompetitions : retrieveCompetitionsWithEventsAndOdds) {
			for (Competition competition : countryWithCompetitions.getCompetitions()) {
				for (Event event : competition.getEvents()) {
					if (event.getMatchStatus().equals("FT")){
						finishedEvents.add(event);
					}
				}
				
			}
		}
		
		
		//get open bets from mongo colleaton
		
		//iterate through the predictions
		//if all predictions close settle the bet
		
		//client will later receive it as settled
		
		
	}
	
	
	@Override
	public UserBet placeBet(UserBet userBet) {
		MongoClient client = getMongoClient();
		MongoDatabase database = client.getDatabase("BETDB");
		MongoCollection<Document> collection = database.getCollection("userBets");
		
		Document newBet = new Document("userId", userBet.getUserId())
				.append("betAmount", userBet.getBetAmount());
				//.append("betStatus", userBet.getStatus().ordinal());
		
		BasicDBList newBetPredictions = new BasicDBList();
		
		for (UserPrediction prediction : userBet.getPredictions()) {
			Document newBetPrediction = new Document("eventId", prediction.getEventId())
			 .append("prediction", prediction.getPrediction());
			newBetPredictions.add(newBetPrediction);
		}
		 
		newBet.append("predictions", newBetPredictions);
		collection.insertOne(newBet);
		 
		userBet.setBetId(newBet.getObjectId("_id").toString()); //needed for the client to search
		 
		return userBet;
	}

	@Override
	public User createUser(User user) throws UserExistsException {
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
		 
		User createdUser = new User(newUser.getObjectId("_id").toString());
		createdUser.setUsername(newUser.getString("username"));
		return createdUser;
	}

	
	public List<UserBet> getBetsForUser(String userId) {
		MongoClient client = getMongoClient();
		MongoCollection<Document> betsCollection = client.getDatabase("BETDB").getCollection("userBets");
		
		 Document userBets = new Document("userId", userId);
		 FindIterable<Document> find = betsCollection.find(userBets);
		 
		 List<UserBet> bets  = new ArrayList<UserBet>();
		 for (Document document : find) {
			 String json = document.toJson();
			 UserBet prediction = new Gson().fromJson(json, new TypeToken<UserBet>() {}.getType());
			 prediction.setBetId(document.getObjectId("_id").toString());
			 bets.add(prediction);
		 }
		 
		 return bets;
	}

	@Override
	public User getUser(String id) {
		MongoClient client = getMongoClient();
		MongoCollection<Document> users = client.getDatabase("BETDB").getCollection("user");
		FindIterable<Document> usersFromMongo = users.find(new Document("_id", new ObjectId(id)));
		Document userFromMongo = usersFromMongo.iterator().next();
		String userString = userFromMongo.toJson();
		User finalUser = new Gson().fromJson(userString, new TypeToken<User>() {}.getType());
		finalUser.setUserBets(getBetsForUser(id));
		finalUser.setId(id);
		return finalUser;
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

	@Override
	public void storeCompetitionsWithEventsAndOdds(Map<SupportedLeagues, List<Competition>> competitionsPerCountry) {
		List<Document> newCompetitions = new ArrayList<Document>();
		for(Map.Entry<SupportedLeagues, List<Competition>> entry : competitionsPerCountry.entrySet()){
			SupportedLeagues supportedLeague = entry.getKey();
			Document newSupportedLeague = new Document("country_id", supportedLeague.getCountryId())
			 .append("country_name", supportedLeague.getCountryName());
			List<Competition> comps = entry.getValue();
			BasicDBList newComps = new BasicDBList();
			for (Competition competition : comps) {
			
			Document newComp = new Document("league_id", competition.getLeagueId())
			 .append("country_id", competition.getCountryId())
			 .append("league_name", competition.getLeagueName())
			 .append("country_name", competition.getCountryName());
			
			BasicDBList newEvents = new BasicDBList();
			for (Event event : competition.getEvents()) {
				Odd odd = event.getOdd();
				Document newOdd = new Document("odd_1", odd.getOdd1())
				.append("odd_2", odd.getOdd2())
				.append("odd_x", odd.getOddX());
				
				Document newEvent = new Document("match_id", event.getMatchId())
				.append("match_hometeam_name", event.getMatchHometeamName())
				.append("match_awayteam_name", event.getMatchAwayteamName())
				.append("match_date", event.getMatchDate())
				.append("match_status", event.getMatchStatus())
				.append("odd", newOdd);
				
				newEvents.add(newEvent);
			}
			newComp.append("events", newEvents);
			newComps.add(newComp);
			}
			
			newSupportedLeague.append("competitions", newComps);
			newCompetitions.add(newSupportedLeague);
		}
		
		MongoClient client = getMongoClient();
		MongoCollection<Document> events = client.getDatabase("BETDB").getCollection("competition");
		events.insertMany(newCompetitions);
	}

	@Override
	public List<CountryWithCompetitions> retrieveCompetitionsWithEventsAndOdds() {
		List<CountryWithCompetitions> countriesWithCompetitions = new ArrayList<CountryWithCompetitions>();
		
		MongoClient client = getMongoClient();
		MongoCollection<Document> competitions = client.getDatabase("BETDB").getCollection("competition");
		FindIterable<Document> find = competitions.find();
		for (Document comp : find){
			String json = comp.toJson();
			CountryWithCompetitions newComp = new Gson().fromJson(json,
					new TypeToken<CountryWithCompetitions>() {}.getType());
			countriesWithCompetitions.add(newComp);
		}
		
		return countriesWithCompetitions;
	}
	
}
