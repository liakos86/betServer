package gr.server.client;

import gr.server.data.Server;
import gr.server.impl.client.ApiFootballClient;

import java.io.IOException;

import org.junit.Test;

public class TestApiFootballClient {

	@Test
	public void test() throws IOException {
		
		
		ApiFootballClient.getCompetitions();
		
		Server.LOG.debug("ffff");
		
	}
	
}
