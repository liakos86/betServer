package gr.server.impl.client;

import gr.server.data.ServerConstants;
import gr.server.data.api.model.Competition;
import gr.server.data.api.model.Country;
import gr.server.data.api.model.Odd;
import gr.server.data.api.model.events.Event;
import gr.server.data.enums.SupportedLeague;
import gr.server.data.util.FileHelperUtils;
import gr.server.util.HttpHelper;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ApiFootballClient {
	
	/**
	 * Gets a list of the available countries.
	 * 
	 * @throws IOException
	 */
	static List<Country> getCountries() throws IOException {
		String url = ServerConstants.GET_COUNTRIES_URL + ServerConstants.API_FOOTBALL_KEY;
		String content = new HttpHelper().fetchGetContent(url);
		Gson gson = new Gson();
		List<Country> countries = gson.fromJson(content,
				new TypeToken<List<Country>>() {
				}.getType());

		return countries;
	}
	
	/**
	 * Gets a list of the available competitions for the countries.
	 * 
	 * @throws IOException
	 */
	public static void getCompetitions() throws IOException {
		Map<SupportedLeague, List<Competition>> competitionsPerCountry = new HashMap<SupportedLeague, List<Competition>>();
		for (SupportedLeague league : SupportedLeague.values()) {
			String url = ServerConstants.GET_LEAGUES_FOR_COUNTRY_URL + league.getCountryId() + ServerConstants.API_FOOTBALL_KEY;
			String content = new HttpHelper().fetchGetContent(url);
			List<Competition> competitions = new Gson().fromJson(content,new TypeToken<List<Competition>>() {}.getType());
			getEventsFor(competitions);
			competitionsPerCountry.put(league, competitions);
		}

		new MongoClientHelperImpl().storeCompetitionsWithEventsAndOdds(competitionsPerCountry);
	}

	
	
	static void getEventsFor(List<Competition> competitions) throws IOException {
		for (Competition competition : competitions) {
			String url = ServerConstants.GET_EVENTS_FOR_DATES + ServerConstants.LEAGUE_URL + competition.getLeagueId() + ServerConstants.API_FOOTBALL_KEY;
			
			//Calendar calendar = Calendar.getInstance();
			//Date today = calendar.getTime();
			//DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");  
			String todayString = "2019-05-09";//dateFormat.format(today);
			
			//calendar.add(Calendar.DAY_OF_MONTH, 2);
			String endDayString = "2019-05-12";// dateFormat.format(calendar.getTime());
			
			url = url.replace(ServerConstants.REPLACE_DATE_FROM, todayString).replace(ServerConstants.REPLACE_DATE_TO, endDayString);
			
			String content = new HttpHelper().fetchGetContent(url);
			
			if (content.contains("error")){
				competition.setEvents(new ArrayList<Event>());
				continue;
			}
			
			Gson gson = new Gson();
			List<Event> events = gson.fromJson(content,new TypeToken<List<Event>>() {}.getType());
			new MongoClientHelperImpl().storeEvents(events);
			
			//competition.setEvents(events);
			
		}
		getOddsFor(competitions);
		
	}

	static void getOddsFor(List<Competition> competitions2) throws IOException {

		String url = ServerConstants.GET_ODDS_FOR_DATES_URL + ServerConstants.API_FOOTBALL_KEY;
		
		Calendar calendar = Calendar.getInstance();
		Date today = calendar.getTime();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");  
		String todayString = dateFormat.format(today);
		
		calendar.add(Calendar.DAY_OF_MONTH, 2);
		String endDayString = dateFormat.format(calendar.getTime());
		
		url = url.replace(ServerConstants.REPLACE_DATE_FROM, todayString).replace(ServerConstants.REPLACE_DATE_TO, endDayString);
		
//		String content = new HttpHelper().fetchGetContent(url);
//		Gson gson = new Gson();
//		List<Odd> odds = gson.fromJson(content, new TypeToken<List<Odd>>() {}.getType());
		
		Odd odd = new Odd();
		odd.setOdd1("1,5");
		odd.setOdd2("4");
		odd.setOddX("3,8");
		
		for (Competition competition : competitions2) {
			
			if (competition.getEvents()==null){
				continue;//TODO: why?
			}
			
			for (Event event : competition.getEvents()) {
//				for (Odd odd : odds){
//				
//				if (event.getMatchId().equals(odd.getMatchId())){
					event.setOdd(odd);
//				}
//					
//				}
			}
		}

	}
	
}
