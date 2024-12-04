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

	    }
	 public static class Event {
	        public static final String ALL_EVENTPIC="allEventsPic:";
	        public static final String EVENTID_PREFIX="eventId:";
	        public static final String EVENT_DETAIL_PREFIX="event:details:";
	    }
	 
	 public static class Sales{
		 	public static final String STOCK= "event:%s:section:%s:stock";
		 	public static final String TICKETS_PREFIX="tickets:";

		 
	 }
}
