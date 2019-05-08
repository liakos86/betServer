package gr.server.data.api.model;

import gr.server.data.api.model.events.Event;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CountryWithCompetitions {

	@SerializedName("country_id")
	@Expose
	String countryId;
	
	@SerializedName("country_name")
	@Expose
	String countryName;
	
	List<Competition> competitions;
	
	Map<String, Event> allEventsMap;
	
	public Map<String, Event> getAllEventsMap() {
		return allEventsMap;
	}

	public void setAllEventsMap(Map<String, Event> allEventsMap) {
		this.allEventsMap = allEventsMap;
	}

	public String getCountryId() {
		return countryId;
	}

	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public List<Competition> getCompetitions() {
		return competitions;
	}

	public void setCompetitions(List<Competition> competitions) {
		this.competitions = competitions;
	}

}
