package gr.server.impl.client;
import gr.server.application.exception.UserExistsException;
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
