package gr.server.data.user.enums;

public enum SupportedLeagues {
	
	CHL (163, "Champions League"),
	EUROPA_LEAGUE(164, "Europa League"),
	 UEFA (165, "UEFA"),
	 ENGLAND(169,"England"),
	 ITALY(170,"Italy"),
	 SPAIN(171,"Spain");
	 
	 
	 SupportedLeagues(Integer countryId, String countryName){
		 this.countryId = countryId;
		 this.countryName = countryName;
	 }
	 
	 
	 Integer countryId;
	 
	 String countryName;

	public Integer getCountryId() {
		return countryId;
	}

	public String getCountryName() {
		return countryName;
	}
	 
	 }
