package gr.server.impl.client;
import gr.server.application.exception.UserExistsException;
import gr.server.data.CollectionNames;
import gr.server.data.Fields;
import gr.server.data.ServerConstants;
import gr.server.data.api.model.Competition;
import gr.server.data.api.model.CountryWithCompetitions;
import gr.server.data.api.model.events.Event;
import gr.server.data.bet.enums.BetStatus;
import gr.server.data.bet.enums.PredictionStatus;
import gr.server.data.bet.enums.PredictionValue;
import gr.server.data.enums.SupportedLeague;
import gr.server.data.user.model.User;
import gr.server.data.user.model.UserAward;
import gr.server.data.user.model.UserBet;
import gr.server.data.user.model.UserPrediction;
import gr.server.def.client.MongoClientHelper;
import gr.server.mongo.util.Executor;
import gr.server.mongo.util.MongoCollectionUtils;
import gr.server.util.DateUtils;
import gr.server.util.TimerTaskHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	/*
	 * PUBLIC API
	 * 
	 */
	
	@Override
	public User createUser(User user) throws UserExistsException {
		MongoClient client = getMongoClient();
		MongoCollection<Document> users = client.getDatabase("BETDB").getCollection(CollectionNames.USERS);
		
		 Document existingUser = new Document("username", user.getUsername());
		 FindIterable<Document> find = users.find(existingUser);
		 
		 Document first = find.first();
		 if (first != null){
			 throw new UserExistsException("User " + user.getUsername() + " already exists");
		 }
		 
		 Document newUser = MongoCollectionUtils.getNewUserDocument(user.getUsername());
		 users.insertOne(newUser);
		 
		User createdUser = new User(newUser.getObjectId("_id").toString());
		createdUser.setUsername(newUser.getString("username"));
		createdUser.setPosition(MongoCollectionUtils.userPosition(createdUser));
		return createdUser;
	}

	@Override
	public User getUser(String id) {
		MongoClient client = getMongoClient();
		MongoCollection<Document> users = client.getDatabase("BETDB").getCollection(CollectionNames.USERS);
		FindIterable<Document> usersFromMongo = users.find(new Document(Fields.MONGO_ID, new ObjectId(id)));
		Document userFromMongo = usersFromMongo.iterator().next();
		String userString = userFromMongo.toJson();
		User finalUser = new Gson().fromJson(userString, new TypeToken<User>() {}.getType());
		finalUser.setUserBets(getBetsForUser(id));
		finalUser.setUserAwards(getAwardsForUser(id));
		finalUser.setMongoId(userFromMongo.getObjectId(Fields.MONGO_ID).toString());
		finalUser.setPosition(MongoCollectionUtils.userPosition(finalUser));
		return finalUser;
	}
	
	@Override
	public void storeCompetitionsWithEventsAndOdds(Map<SupportedLeague, List<Competition>> competitionsPerCountry) {
		List<Document> newCompetitions = new ArrayList<Document>();
		for(Map.Entry<SupportedLeague, List<Competition>> entry : competitionsPerCountry.entrySet()){
			List<Competition> comps = entry.getValue();
			BasicDBList newComps = new BasicDBList();
			for (Competition competition : comps) {
				Document newCompetition = MongoCollectionUtils.getCompetitionDocument(competition);
				newComps.add(newCompetition);
			}
			
			SupportedLeague supportedLeague = entry.getKey();
			Document newSupportedLeague = MongoCollectionUtils.getSupportedLeagueDocument(supportedLeague, newComps);
			newCompetitions.add(newSupportedLeague);
		}
		
		MongoClient client = getMongoClient();
		MongoCollection<Document> events = client.getDatabase("BETDB").getCollection("competition");
		events.insertMany(newCompetitions);
	}

	@Override
	public List<CountryWithCompetitions> retrieveCompetitionsWithEventsAndOdds() {
		Executor<CountryWithCompetitions> executor = new Executor<CountryWithCompetitions>(new TypeToken<CountryWithCompetitions>() { });
		List<CountryWithCompetitions> countriesWithCompetitions = MongoCollectionUtils.get(CollectionNames.COMPETITIONS, new Document(), executor);
		for (CountryWithCompetitions countryWithComp : countriesWithCompetitions){
			for (Competition comp : countryWithComp.getCompetitions()){
				Document eventsFilter = new Document("league_id", comp.getLeagueId()).append("country_id", comp.getCountryId());
				Executor<Event> eventsExecutor = new Executor<Event>(new TypeToken<Event>() { });
				List<Event> compEvents = MongoCollectionUtils.get(CollectionNames.EVENTS, eventsFilter, eventsExecutor);
				comp.setEvents(compEvents);
			}
		}
		
		return countriesWithCompetitions;
	}
	
	@Override
	public UserBet placeBet(UserBet userBet) {
		MongoClient client = getMongoClient();
		MongoDatabase database = client.getDatabase("BETDB");
		MongoCollection<Document> betsCollection = database.getCollection(CollectionNames.BETS);
		Document newBet = MongoCollectionUtils.getBetDocument(userBet);
		betsCollection.insertOne(newBet);
		userBet.setMongoId(newBet.getObjectId(Fields.MONGO_ID).toString()); 
		MongoCollectionUtils.updateUser(userBet);
		return userBet;
	}
	
	@Override
	public void settleBets(){
		List<Document> possibleValues = Arrays.asList(new Document[]{new Document("betStatus", BetStatus.PENDING.getCode()), new Document("betStatus", BetStatus.PENDING_LOST.getCode())});
		Document openPendingBetsFilter = MongoCollectionUtils.getOrDocument("betStatus", possibleValues);
		
		Executor<UserBet> executor = new Executor<UserBet>(new TypeToken<UserBet>() { });
		List<UserBet> openBets = MongoCollectionUtils.get(CollectionNames.BETS, openPendingBetsFilter, executor);

		Document eventFilter = new Document("match_status", "FT");
		Executor<Event> executorEvent = new Executor<Event>(new TypeToken<Event>() { });
		List<Event> finished = MongoCollectionUtils.get(CollectionNames.EVENTS, eventFilter, executorEvent);

		Map<String, Event> finishedEvents = new HashMap<String, Event>();
		for (Event event : finished){
			finishedEvents.put(event.getMatchId(), event);
		}
		
		for (UserBet userBet : openBets){
			settleBet(userBet, finishedEvents);
		}
	}
	
	//END OF PUBLIC API
	
	void settleBet(UserBet userBet, Map<String, Event> finishedEvents) {
		for (UserPrediction prediction : userBet.getPredictions()){
			if (PredictionStatus.PENDING.getCode() != prediction.getPredictionStatus()){
				continue;
			}
			
			Event predictionEvent = finishedEvents.get(prediction.getEventId());
			PredictionValue result = calculateEventResult(predictionEvent);
			if (result.getCode() == prediction.getPrediction()){
				prediction.setPredictionStatus(PredictionStatus.CORRECT.getCode());
			}else{
				prediction.setPredictionStatus(PredictionStatus.MISSED.getCode());
				userBet.setBetStatus(BetStatus.PENDING_LOST.getCode());
			}
		}
		
		if (shouldSettleFavourably(userBet)){
			settleFavourably(userBet);
		}
		
		if (shouldSettleUnfavourably(userBet)){
			settleUnfavourably(userBet);
		}
		
		userBet = MongoCollectionUtils.updateBet(userBet);
		MongoCollectionUtils.updateUser(userBet);
	}

	void settleUnfavourably(UserBet userBet) {
		userBet.setBetStatus(BetStatus.SETTLED_INFAVOURABLY.getCode());
	}
	
	void settleFavourably(UserBet userBet) {
		userBet.setBetStatus(BetStatus.SETTLED_FAVOURABLY.getCode());
	}

	PredictionValue calculateEventResult(Event predictionEvent) {
		Integer matchHometeamScore = Integer.parseInt(predictionEvent.getMatchHometeamScore());
		Integer matchAwayTeamScore = Integer.parseInt(predictionEvent.getMatchAwayteamScore());
		PredictionValue predictionEventResult = matchHometeamScore > matchAwayTeamScore ? PredictionValue.HOME : (matchHometeamScore.equals(matchAwayTeamScore) ? PredictionValue.DRAW : PredictionValue.AWAY);
		return predictionEventResult;
	}

	/**
	 * If user missed one prediction, the {@link UserBet} is considered lost.
	 * 
	 */
	boolean shouldSettleUnfavourably(UserBet userBet) {
		boolean atLeastOneMissed = false;
		for (UserPrediction prediction : userBet.getPredictions()){
			if (PredictionStatus.PENDING.getCode() == prediction.getPredictionStatus()){
				return false;
			}
			if (PredictionStatus.MISSED.getCode() == prediction.getPredictionStatus()){
				atLeastOneMissed = true;
			}
		}
		
		if (atLeastOneMissed){
			return true;
		}
		return false;
	}

	boolean shouldSettleFavourably(UserBet userBet) {
		for (UserPrediction prediction : userBet.getPredictions()){
			if (PredictionStatus.CORRECT.getCode() != prediction.getPredictionStatus()){
				return false;
			}
		}
		return true;
	}

	public List<UserBet> getBetsForUser(String userId) {
		Document userBetsFilter = new Document(Fields.USER_ID, userId);
		Executor<UserBet> betsExecutor = new Executor<UserBet>(new TypeToken<UserBet>() { });
		List<UserBet> bets = MongoCollectionUtils.get(CollectionNames.EVENTS, userBetsFilter, betsExecutor);
		 return bets;
	}
	
	public List<UserAward> getAwardsForUser(String userId) {
		Document userAwardsFilter = new Document(Fields.USER_ID, userId);
		Executor<UserAward> awardsExecutor = new Executor<UserAward>(new TypeToken<UserAward>() { });
		List<UserAward> awards = MongoCollectionUtils.get(CollectionNames.EVENTS, userAwardsFilter, awardsExecutor);
		 return awards;
	}

	public List<User> retrieveLeaderBoard() {
		Document sortField = new Document(Fields.USER_BALANCE, -1);
		Executor<User> usersExecutor = new Executor<User>(new TypeToken<User>() { });
		List<User> users = MongoCollectionUtils.getSorted(CollectionNames.USERS, usersExecutor, new Document(), sortField, 10);
		return users;
	}
	
	public void storeEvents(List<Event> events) {
		List<Document> newEvents = new ArrayList<Document>();
		for (Event event : events) {
			Document newEvent = MongoCollectionUtils.getEventDocument(event);
			newEvents.add(newEvent);
		}
		
		MongoClient client = getMongoClient();
		MongoCollection<Document> eventsCollection = client.getDatabase("BETDB").getCollection(CollectionNames.EVENTS);
		eventsCollection.insertMany(newEvents);
	}
	
	/**
	 * Runs on the midnight of the first day of the month.
	 * Finds the winner.
	 * Stores an award.
	 * Updates the winner's fields.
	 * Restores every user's balance to {@link ServerConstants#STARTING_BALANCE}.
	 * Deletes bets going 2 months ago.
	 * 
	 * @see TimerTaskHelper#getMonthChangeCheckerTask()
	 */
	public void settleMonthlyAward(String monthToSettle){
		Document sortField = new Document(Fields.USER_BALANCE, -1);
		Executor<User> usersExecutor = new Executor<User>(new TypeToken<User>() { });
		List<User> monthWinners = MongoCollectionUtils.getSorted(CollectionNames.USERS, usersExecutor, new Document(), sortField, 1);
		//TODO tied users
		Document awardDocument = MongoCollectionUtils.createAwardFor(monthWinners.get(0));
		MongoCollectionUtils.updateUserAwards(monthWinners.get(0), awardDocument.getObjectId(Fields.MONGO_ID));
		
		MongoCollectionUtils.restoreUserBalance();
		MongoCollectionUtils.deleteUserBetsFor(DateUtils.getPastMonthAsString(2));
	}

}
