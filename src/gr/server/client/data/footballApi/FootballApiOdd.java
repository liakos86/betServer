package gr.server.client.data.footballApi;

import com.google.gson.annotations.SerializedName;

public class FootballApiOdd {
	
	@SerializedName("1")
	Result home;
	
	@SerializedName("N")
	Result draw;
	
	@SerializedName("2")
	Result away;

	public Result getHome() {
		return home;
	}

	public void setHome(Result home) {
		this.home = home;
	}

	public Result getDraw() {
		return draw;
	}

	public void setDraw(Result draw) {
		this.draw = draw;
	}

	public Result getAway() {
		return away;
	}

	public void setAway(Result away) {
		this.away = away;
	}
	
	

}
