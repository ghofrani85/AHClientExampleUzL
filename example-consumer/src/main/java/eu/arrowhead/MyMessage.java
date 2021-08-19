package eu.arrowhead;

import java.util.Date;

public class MyMessage {
	private String message;
	private String topic;
	private Date date;
	
	// Constructor
	public MyMessage(String message, String topic, Date date) {
		super();
		this.message = message;
		this.topic = topic;
		this.date = date;
	}
	
	// getters and setters
	public String getMessage(){
		return message;
	}
	
	public void setMessage(String newMessage) {
		this.message = newMessage;
	}
	
	public String getTopic() {
		return topic;
	}
	
	public void setTopic(String newTopic) {
		this.topic = newTopic;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date newDate) {
		this.date = newDate;
	}
}
