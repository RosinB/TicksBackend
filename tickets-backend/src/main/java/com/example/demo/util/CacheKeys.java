package com.example.demo.util;

public class CacheKeys {

	 public static class User {
	        public static final String ALL_USERS = "AllUser";
	        public static final String USERSDTO_PREFIX = "userDto:";
	        public static final String USEREMAIL_PREFIX="userEmail:";
	        public static final String USERTOKEN_PREFIX="userName:";
	        public static final String USERNAME_PREFIX="userName:";
	        public static final String VERIFICATION_CODE = "userName:%s:userEmail:%s:code:";
	        public static final String TOKEN_PREFIX="token:";
	        public static final String USERID_PREFIX="userId:";
	        public static final String USER_ISVERFIED_PREFIX="user:is_verified:";

	    }
	 public static class Event {
	        public static final String ALL_EVENTPIC="allEventsPic:";
	        public static final String EVENTID_PREFIX="eventId:";
	        public static final String EVENT_DETAIL_PREFIX="event:details:";
	        public static final String EVENT_PIC_PREFIX="event:pic:";
	        public static final String SECTION_QUANTITY="event:%d:section:%s:quantity";
	        public static final String EVENTNAME_PREFIX="eventName:";
	        
	        public static String getSectionQuantityKey(String eventId, String section) {
	            return String.format(SECTION_QUANTITY, eventId, section);
	        }
	        
	    }
	 
	 public static class Sales{
		 	public static final String STOCK= "event:%s:section:%s:stock";
		 	public static final String TICKETS_PREFIX="tickets:";
		 	public static final String SALESQUEUE_PREFIX="queue:";
		 
	 }
	 
	 public static class Order{
		 	public static final String ORDER_PREFIX="order:";
		 
		 
	 }
	 public static class util{
		 	public static final String CAPTCHA_PREFIX="captcha:";
		 	public static final String REQUEST_FREQUENCY="request_frequency:";
		 	public static final String TRAFFIC_EVENTID="traffic:eventId:";
		 	public static final String USERS_BEHAVIOR="user:behavior";
		 	public static final String USERS_IPS="user:ips:";
		 	public static final String SUSPICIOUS_USERS="user:susicious:";
		 	public static final String TRAFFIC_RECORD_EVENTID="traffic:record:eventId";
		 	
	 }
	 
}
