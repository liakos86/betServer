package gr.server.mongo.util;

import gr.server.application.RestApplication;
import gr.server.data.CollectionNames;
import gr.server.data.Fields;
import gr.server.data.ServerConstants;
import gr.server.data.api.model.Competition;
import gr.server.data.api.model.events.Event;
import gr.server.data.bet.enums.BetStatus;
import gr.server.data.bet.enums.PredictionStatus;
import gr.server.data.enums.SupportedLeague;
import gr.server.data.user.model.User;
import gr.server.data.user.model.UserBet;
import gr.server.data.user.model.UserPrediction;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

public class MongoCollectionUtils {
	
	public static <E> ArrayList<E> get(String collectionString, Document filter, Executor<E> e){
		MongoClient client = RestApplication.getMongoClient();
		MongoCollection<Document> collection = client.getDatabase("BETDB").getCollection(collectionString);
		FindIterable<Document> find = collection.find(filter);
		ArrayList<E> list  = new ArrayList<E>();
		for (Document document : find) {
			 String json = document.toJson();
			 E object = (E) e.execute(json);
			 if (object instanceof UserBet){
				 ((UserBet)object).setMongoId(document.getObjectId(Fields.MONGO_ID).toString());
			 }
			 
			list.add(object);
		}
		return list;
	}
	
	public static <E> ArrayList<E> getSorted(String collectionString,  Executor<E> e, Document sortFilter, int limit){
		MongoClient client = RestApplication.getMongoClient();
		MongoCollection<Document> collection = client.getDatabase("BETDB").getCollection(collectionString);
		 FindIterable<Document> find = collection.find().limit(limit).sort(sortFilter);
		 ArrayList<E> list  = new ArrayList<E>();
		 for (Document document : find) {
			 String json = document.toJson();
			 E object = (E) e.execute(json);
			 if (object instanceof User){//very very dirty
				((User) object).setMongoId(document.getObjectId(Fields.MONGO_ID).toString()); 
			 }
			list.add(object);
		 }
		 return list;
	}
	
	public static UserBet updateBet(UserBet userBet){
		MongoClient client = RestApplication.getMongoClient();
		MongoCollection<Document> collection = client.getDatabase("BETDB").getCollection(CollectionNames.BETS);
		Document filter = new Document(Fields.MONGO_ID, new ObjectId(userBet.getMongoId()) );
		Document updateFieldDocument = new Document("betStatus", userBet.getBetStatus());
		for (int i =0; i < userBet.getPredictions().size(); i++){
			updateFieldDocument.append("predictions."+String.valueOf(i)+".predictionStatus", userBet.getPredictions().get(i).getPredictionStatus());
		}
		Document allFieldsDocument = new Document("$set", updateFieldDocument);	 
		collection.findOneAndUpdate(filter, allFieldsDocument);
		
		return userBet;
	}
	
	Document betDocument(UserBet userBet) {
		Document newBet = new Document("mongoUserId", userBet.getMongoUserId())
				.append("betAmount", userBet.getBetAmount())
				.append("betStatus", userBet.getBetStatus())
				.append("betPlaceDate", userBet.getBetPlaceDate());

		BasicDBList newBetPredictions = new BasicDBList();

		for (UserPrediction prediction : userBet.getPredictions()) {
			Document newBetPrediction = new Document("eventId",
					prediction.getEventId())
					.append("prediction", prediction.getPrediction())
					.append("predictionDescription",
							prediction.getPredictionDescription())
					.append("oddValue", prediction.getOddValue()

					);
			newBetPredictions.add(newBetPrediction);
		}

		newBet.append("predictions", newBetPredictions);

		return newBet;
	}

	/**
	 * {@link User} will be updated depending on his won/lost {@link UserBet}.
	 * 
	 * @param userBet
	 */
	public static void updateUser(UserBet userBet) {
		MongoClient client = RestApplication.getMongoClient();
		MongoCollection<Document> usersCollection = client.getDatabase("BETDB").getCollection(CollectionNames.USERS);
		Document filter = new Document(Fields.MONGO_ID, new ObjectId(userBet.getMongoUserId()));

		Document userFieldsDocument = new Document();
		if (userBet.getBetStatus() == BetStatus.SETTLED_FAVOURABLY.getCode()){//bet won
			userFieldsDocument.append("wonSlipsCount", 1)
			.append("wonEventsCount", userBet.getPredictions().size())
			.append("balance", userBet.getPossibleEarnings());
		}else if (userBet.getBetStatus() == BetStatus.SETTLED_INFAVOURABLY.getCode()){//bet lost
			int correctPredictions = numOfSuccessFullPredictions(userBet);
			userFieldsDocument.append("lostSlipsCount", 1)
			.append("wonEventsCount", correctPredictions)
			.append("lostEventsCount", userBet.getPredictions().size() - correctPredictions);
		}else if (userBet.getBetStatus() == BetStatus.PENDING.getCode()){// bet placed
			userFieldsDocument.append("balance", -1 * (userBet.getBetAmount()));
		}
		Document increaseOrDecreaseDocument = new Document("$inc", userFieldsDocument);
		usersCollection.findOneAndUpdate(filter, increaseOrDecreaseDocument);
	}
	
	static int numOfSuccessFullPredictions(UserBet userBet){
		int won =0;
		for(UserPrediction prediction : userBet.getPredictions()){
			if (prediction.getPredictionStatus() == PredictionStatus.CORRECT.getCode()){
				++won;
			}
		}
		return won;
	}

	public static Document getEventDocument(Event event) {
		//Odd odd = event.getOdd();
		Document newOdd = new Document("odd_1", "1,8")// odd.getOdd1())
		.append("odd_2", "3,3")// odd.getOdd2())
		.append("odd_x", "3,6");//odd.getOddX());
		
		Document newEvent = new Document("match_id", event.getMatchId())
		.append("match_hometeam_name", event.getMatchHometeamName())
		.append("match_awayteam_name", event.getMatchAwayteamName())
		.append("match_date", event.getMatchDate())
		.append("match_status", event.getMatchStatus())
		.append("match_hometeam_score", event.getMatchHometeamScore())
		.append("match_awayteam_score", event.getMatchAwayteamScore())
		.append("match_hometeam_extra_score", event.getMatchHometeamScore())
		.append("match_awayteam_extra_score", event.getMatchAwayteamScore())
		.append("odd", newOdd);
		
		return newEvent;
		
	}

	public static Document getBetDocument(UserBet userBet) {
		Document newBet = new Document("mongoUserId", userBet.getMongoUserId())
				.append("betAmount", userBet.getBetAmount())
				.append("betStatus", userBet.getBetStatus())
				.append("betPlaceDate", userBet.getBetPlaceDate());

		BasicDBList newBetPredictions = new BasicDBList();

		for (UserPrediction prediction : userBet.getPredictions()) {
			Document newBetPrediction = new Document("eventId",
					prediction.getEventId())
					.append("prediction", prediction.getPrediction())
					.append("predictionDescription",
							prediction.getPredictionDescription())
					.append("oddValue", prediction.getOddValue()

					);
			newBetPredictions.add(newBetPrediction);
		}

		newBet.append("predictions", newBetPredictions);
		return newBet;
	}

	public static Document getNewUserDocument(String userName) {
		 return new Document("username", userName)
		 .append("wonEventsCount", 0)
		 .append("lostEventsCount", 0)
		 .append("wonSlipsCount", 0)
		 .append("lostSlipsCount", 0)
		 .append("balance", ServerConstants.STARTING_BALANCE)
		 ;
	}

	public static Document getCompetitionDocument(Competition competition) {
		return new Document("league_id", competition.getLeagueId())
		 .append("country_id", competition.getCountryId())
		 .append("league_name", competition.getLeagueName())
		 .append("country_name", competition.getCountryName());
	}

	public static Document getSupportedLeagueDocument(
			SupportedLeague supportedLeague, BasicDBList newComps) {
		return new Document("country_id", supportedLeague.getCountryId())
		.append("country_name", supportedLeague.getCountryName())
		.append("competitions", newComps);
	}

	public static Document getOrDocument(String string,
			List<Document> possibleValues) {
		BasicDBList orList = new BasicDBList();
		for (Document document : possibleValues){
			orList.add(document);
		}
		return new Document("$or", orList);
	}
	
	public static long userPosition(User user){
		Document userBalance = new Document("$gte", user.getBalance());
		Document greaterBalancedUsers = new Document("balance", userBalance);
		MongoClient client = RestApplication.getMongoClient();
		MongoCollection<Document> usersCollection = client.getDatabase("BETDB").getCollection(CollectionNames.USERS);
		return usersCollection.count(greaterBalancedUsers);
	}

}
