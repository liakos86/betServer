package gr.server.client.data;

import java.util.List;

public class UpcomingEvents {
	
	Boolean success;
	
	List<UpcomingEvent> data;

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public List<UpcomingEvent> getData() {
		return data;
	}

	public void setData(List<UpcomingEvent> data) {
		this.data = data;
	}
	
	

}
