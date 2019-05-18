package gr.server.util;

import gr.server.data.ServerConstants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
	
    private final static int MIDNIGHT = 0;
    private final static int ONE_MINUTE = 1;
	
	 @SuppressWarnings("deprecation")
	public static Date getTomorrowMidnight(){
	        Date date2am = new Date(); 
	           date2am.setHours(MIDNIGHT); 
	           date2am.setMinutes(ONE_MINUTE); 
	           return date2am;
	      }

	public static boolean isFirstDayOfMonth() {
		return Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 1;
	}

	@SuppressWarnings("deprecation")
	public static String getPastMonthAsString(Integer monthsToSubtract) {
		SimpleDateFormat df = new SimpleDateFormat(ServerConstants.AWARD_DATE_FORMAT);
		Date date = new Date();
		date.setMonth(date.getMonth() - monthsToSubtract);
		String format = df.format(date);
		return format;
	}

}
