package gr.server.client.data;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;


//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.annotation.JsonInclude.Include;
//
//@JsonInclude(Include.NON_EMPTY)
@XmlRootElement
public class UpcomingEvent {

	public String getSport_key() {
		return sport_key;
	}

	public void setSport_key(String sport_key) {
		this.sport_key = sport_key;
	}

	public List<String> getTeams() {
		return teams;
	}

	public void setTeams(List<String> teams) {
		this.teams = teams;
	}

	public Long getCommence_time() {
		return commence_time;
	}

	public void setCommence_time(Long commence_time) {
		this.commence_time = commence_time;
	}

	public String getHome_team() {
		return home_team;
	}

	public void setHome_team(String home_team) {
		this.home_team = home_team;
	}

	public List<Site> getSites() {
		return sites;
	}

	public void setSites(List<Site> sites) {
		this.sites = sites;
	}

	String sport_key;
	
	List<String> teams;
	
	Long commence_time;
	
	String home_team;
	
	List<Site> sites;
}
