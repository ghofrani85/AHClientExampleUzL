package eu.arrowhead;

import java.io.BufferedWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MyCallback implements MqttCallback
{
	
	@Override
	public void connectionLost(Throwable cause) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		
	    // Get current time and date
	    Date date = new Date();
	    
	    System.out.println("Arrived message: "+message);
	    
	    // Insert message, topic and date into database
	    String messageAsString = ""+message;	
	    ConnectionManager.insertDocToDB(topic, messageAsString, date);
		
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	

}
