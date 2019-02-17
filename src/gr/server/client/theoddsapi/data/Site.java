package gr.server.client.theoddsapi.data;

import java.util.List;

public class Site {
	
	String site_nice;
	
	Long last_update;
	
	Odds odds;

	public String getSite_nice() {
		return site_nice;
	}

	public void setSite_nice(String site_nice) {
		this.site_nice = site_nice;
	}

	public Long getLast_update() {
		return last_update;
	}

	public void setLast_update(Long last_update) {
		this.last_update = last_update;
	}

	public Odds getOdds() {
		return odds;
	}

	public void setOdds(Odds odds) {
		this.odds = odds;
	}

}
