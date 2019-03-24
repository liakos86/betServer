package gr.client.android.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AndroidUpcomingEvent {
	
	String league;
	
	String sport;

	List<String> teams;
	
//	String team1;
//	
//	String team2;

	Integer commenceTime2;

	String homeTeam;

	//Odds odds;
	
	List<Double>odds;
	
	//H2h h2h;
	
	

	public String getSport() {
		return sport;
	}

	public void setSport(String sport) {
		this.sport = sport;
	}

//	public H2h getH2h() {
//		return h2h;
//	}
//
//	public void setH2h(H2h h2h) {
//		this.h2h = h2h;
//	}
//
//
//	public String getTeam1() {
//		return team1;
//	}
//
//	public void setTeam1(String team1) {
//		this.team1 = team1;
//	}
//
//	public String getTeam2() {
//		return team2;
//	}
//
//	public void setTeam2(String team2) {
//		this.team2 = team2;
//	}

	public String getLeague() {
		return league;
	}

	public void setLeague(String league) {
		this.league = league;
	}

	public Integer getCommenceTime() {
		return commenceTime2;
	}

	public void setCommenceTime(Integer commenceTime) {
		this.commenceTime2 = commenceTime;
	}

	public String getHomeTeam() {
		return homeTeam;
	}

	public void setHomeTeam(String homeTeam) {
		this.homeTeam = homeTeam;
	}

	public List<String> getTeams() {
		return teams;
	}

	public void setTeams(List<String> teams) {
		this.teams = teams;
	}

	public Integer getCommenceTime2() {
		return commenceTime2;
	}

	public void setCommenceTime2(Integer commenceTime2) {
		this.commenceTime2 = commenceTime2;
	}

	public List<Double> getH2h() {
		return odds;
	}

	public void setH2h(List<Double> h2h) {
		this.odds = h2h;
	}
	
	

//	public Odds getOdds() {
//		return odds;
//	}
//
//	public void setOdds(Odds odds) {
//		this.odds = odds;
//	}

	
}
