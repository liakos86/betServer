package gr.server.client.footballApi.data;

import com.google.gson.annotations.SerializedName;

public class FootballApiAvailableMarkets {
	
	    @SerializedName("Win the match")
	    public FootballApiOdd winner;
	    
	    @SerializedName("Handicap [0:1]")
	    public FootballApiOdd handicap;

		public FootballApiOdd getHandicap() {
			return handicap;
		}

		public void setHandicap(FootballApiOdd handicap) {
			this.handicap = handicap;
		}

		public FootballApiOdd getWinner() {
			return winner;
		}

		public void setWinner(FootballApiOdd winner) {
			this.winner = winner;
		}
	    
}
