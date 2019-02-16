package gr.server.client;

import java.net.ConnectException;

import org.bson.Document;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDbClient {
	
	
	public static void main (String [] args){
		MongoDatabase connect = connect();
		MongoCollection<Document> collection = connect.getCollection("counters");
		long count = collection.count();
		System.out.println(count);
	}
	
	static MongoDatabase connect(){
	        MongoClient client = MongoClients.create(new ConnectionString("mongodb://liakos86:eds5Ej6wUcEjEdM@ds063870.mlab.com:63870"));
	        MongoDatabase db = client.getDatabase("auction");
	        
	        return db;
	}

}
