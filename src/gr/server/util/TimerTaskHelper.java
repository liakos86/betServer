package gr.server.util;

import gr.server.impl.client.MongoClientHelperImpl;

import java.util.Date;
import java.util.TimerTask;

public class TimerTaskHelper {
	
	public static TimerTask getMonthChangeCheckerTask(){
		return new TimerTask() {
	        public void run() {
	            System.out.println("Checking month change on: " + new Date() + "n" +
	              "Thread's name: " + Thread.currentThread().getName());
	            
//	            if (!DateUtils.isFirstDayOfMonth()){
//	            	return;
//	            }
	            
	            String monthToSettle = DateUtils.getPastMonthAsString(1);
	           	new MongoClientHelperImpl().settleMonthlyAward(monthToSettle);
	        }
	    };
	}

}
