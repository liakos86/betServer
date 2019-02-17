package gr.server.client.footballApi.data;


public class Api {
	
	String results;
	
	FootballApiAvailableMarkets odds;
	
	public String getResults() {
		return results;
	}

	public void setResults(String results) {
		this.results = results;
	}

	public FootballApiAvailableMarkets getOdds() {
		return odds;
	}

	public void setOdds(FootballApiAvailableMarkets odds) {
		this.odds = odds;
	}

}
