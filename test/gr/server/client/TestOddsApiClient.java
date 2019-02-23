package gr.server.client;

import gr.server.client.theoddsapi.data.OddsApiConstants;
import gr.server.data.Server;
import gr.server.impl.client.OddsApiClient;

import java.io.IOException;

public class TestOddsApiClient {

	public static void main(String[] args) throws IOException {
		
		
		OddsApiClient.getLeaguesWithEvents();
		
//		Server.LOG.debug("ffff");
//		System.out.println(OddsApiClient.getLeaguesWithEvents().get(OddsApiConstants.PREMIER_LEAGUE).get(0).getTeams());
		
	}
	
}
