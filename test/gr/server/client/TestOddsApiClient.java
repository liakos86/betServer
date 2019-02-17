package gr.server.client;

import gr.server.client.theoddsapi.data.OddsApiConstants;
import gr.server.data.Server;

import java.io.IOException;

public class TestOddsApiClient {

	public static void main(String[] args) throws IOException {
		
		
		Server.LOG.debug("ffff");
		System.out.println(OddsApiClient.getLeagues().get(OddsApiConstants.PREMIER_LEAGUE).get(0).getTeams());
		
	}
	
}
