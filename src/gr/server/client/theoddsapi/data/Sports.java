package gr.server.client.theoddsapi.data;

import java.util.List;

public class Sports {
	
	Boolean success;
	
	List<Sport> data;

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public List<Sport> getData() {
		return data;
	}

	public void setData(List<Sport> data) {
		this.data = data;
	}
	
	

}
