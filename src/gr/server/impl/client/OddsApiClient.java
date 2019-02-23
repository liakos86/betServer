package gr.server.impl.client;

import gr.server.client.theoddsapi.data.Sport;
import gr.server.client.theoddsapi.data.Sports;
import gr.server.client.theoddsapi.data.UpcomingEvent;
import gr.server.client.theoddsapi.data.UpcomingEvents;
import gr.server.data.ServerConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class OddsApiClient {
	
	/**
	 * Gets a list of the available sports. This is a list of all leagues for 
	 * alla supported sports
	 * 
	 * @throws IOException
	 */
	static List<Sport> getSports() throws IOException {
		String url = ServerConstants.BASE_API_URL + ServerConstants.GET_SPORTS_URL + ServerConstants.ODDS_API_KEY;//  odds/?sport=upcoming&region=uk&mkt=h2h&apiKey="+ServerConstants.ODDS_API_KEY;
		url = "http://localhost:8080/betServer/ws/betServer/getSports";
		String content = fetchContent(url);
		Gson gson = new Gson();
		Sports sports = gson.fromJson(content,
				new TypeToken<Sports>() {
				}.getType());

		return sports.getData();
	}
	
	/**
	 * Returns the 8 upcoming events, all sports combined.
	 * Not so useful.
	 * 
	 * @throws IOException
	 */
	static List<UpcomingEvent> getUpcoming() throws IOException {
		String url = ServerConstants.BASE_API_URL + ServerConstants.GET_UPCOMING_URL + ServerConstants.ODDS_API_KEY;
		url = "http://localhost:8080/betServer/ws/betServer/getUpcoming";
		String content = fetchContent(url);
		Gson gson = new Gson();
		UpcomingEvents events = gson.fromJson(content,
				new TypeToken<UpcomingEvents>() {
				}.getType());
		return events.getData();
	}
	
	/**
	 * The total list of leagues that will be decided will be called
	 * in sequence here. They will afterwards be sent to the mongoDb.
	 * 
	 * @throws IOException
	 */
	public static Map<String, List<UpcomingEvent>> getLeagues() throws IOException {
		String url = ServerConstants.BASE_API_URL + ServerConstants.GET_PREMIER_LEAGUE_URL + ServerConstants.ODDS_API_KEY;//  odds/?sport=upcoming&region=uk&mkt=h2h&apiKey="+ServerConstants.ODDS_API_KEY;
		url = "http://localhost:8080/betServer/ws/betServer/getLeagues";
		String content = fetchContent(url);
		UpcomingEvents events = new Gson().fromJson(content, new TypeToken<UpcomingEvents>() {}.getType());
		Map<String, List<UpcomingEvent>> leaguesToSports = new HashMap<String, List<UpcomingEvent>>();
		leaguesToSports.put("premierLeague", events.getData());
		return leaguesToSports;
		
	}
	
	private static String fetchContent(String uri) throws IOException {
		URL url = new URL(uri);
		final int OK = 200;
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		int responseCode = connection.getResponseCode();
		if(responseCode == OK){
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response2 = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response2.append(inputLine);
			}
			in.close();
			return response2.toString();
		}
		return null;
	}

}
