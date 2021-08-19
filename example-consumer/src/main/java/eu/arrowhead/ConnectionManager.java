package eu.arrowhead;

import java.io.BufferedReader;

import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.json.async.NonBlockingJsonParser;
import com.jayway.jsonpath.JsonPath;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

import javax.sound.midi.Soundbank;

import org.springframework.boot.json.GsonJsonParser;
import org.springframework.web.bind.annotation.GetMapping;

public class ConnectionManager {
	private static MongoClient myMongoClient;
	private static HttpURLConnection con;

	// Connect to mongo database
	public static MongoClient getConnection() {

		// return if there already is a connection to the mongo database
		if (myMongoClient != null) {
			return myMongoClient;
		}

		// New connection to the mongo database
		try {
			myMongoClient = new MongoClient("localhost", 27017);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		System.out.println("Connected successfully to mongo");

		return myMongoClient;
	}


	
	public static void insertDocToDB(String topic, String message, Date date) {

		// Build connection to mongo database
		MongoClient myMongoClient = ConnectionManager.getConnection();

		// Use the mqtt database and collection of received messages
		DB mydb = myMongoClient.getDB("mqtt_db");
		DBCollection mycoll = mydb.getCollection("received_messages");

		// Create new mongo database document with the message, topic and date
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");  
		String strDate = dateFormat.format(date);
		
		DBObject document = new BasicDBObject();
		document.put("topic", topic);
		document.put("message", message);
		document.put("time_stamp", new Date());
		System.out.println("Current date and time: "+date);

		// insert the document into the database
		mycoll.insert(document);
		
		System.out.println("Document successfully inserted into database");

	}

	public static List<MyMessage> readFromDBtoJSON(Date startDate, Date endDate, DBCollection collection, String top) {
		
		// Find data in specific date range
		BasicDBObject getQuery = new BasicDBObject();
		getQuery.append("time_stamp", new BasicDBObject("$gte", startDate).append("$lte", endDate));
		getQuery.append("topic", top);
		DBCursor cursor = collection.find(getQuery);
		List<MyMessage> responseList = new LinkedList<>();
		
		// Iterate over whole collection
		while (cursor.hasNext()) {
			String nojson = "" + cursor.next();
			String message = JsonPath.read(nojson, "$.message");
			String topic = JsonPath.read(nojson, "$.topic");
			String date = JsonPath.read(nojson, "$.time_stamp.$date");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			
			Date date1 = new Date();
			
			try {
				date1 = format.parse(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			MyMessage msg = new MyMessage(message, topic, date1);
			responseList.add(msg);
		}

		return responseList;
	}

}
