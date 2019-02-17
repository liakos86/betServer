package gr.server.client;

import gr.server.data.Server;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.ConnectionString;
import com.mongodb.async.client.FindIterable;
import com.mongodb.async.client.ListDatabasesIterable;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;

public class MongoDbClient {
	
	
	public static void main (String [] args){
		connect();
	}
	
	static void connect(){
		
//		ClusterSettings clusterSettings = ClusterSettings.builder()
//			      .hosts(Arrays.asList(new ServerAddress("")))
//			      .build();
//			  MongoClientSettings settings = MongoClientSettings.builder()
//			      .clusterSettings(clusterSettings).build();
//			   MongoClients.create(settings);
		
//		
	      MongoClient client = MongoClients.create(new ConnectionString("mongodb://liakos86:art78tha3m@ds063870.mlab.com:63870/admin"));
	      MongoDatabase database = client.getDatabase("auction");
	      ListDatabasesIterable<Document> listDatabases = client.listDatabases();
	      MongoCollection<Document> collection = database.getCollection("workout");
	      
	      FindIterable<Document> dbObject = collection.find();
	      
			
		

	      
	      
		
	}

}
