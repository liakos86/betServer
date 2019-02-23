package gr.server.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public final class Server {

	public static final Logger LOG = Logger.getLogger(Server.class);
	
	public static Map<String, List<String>> AVAILABLE_LEAGUES;
	
	static{
		AVAILABLE_LEAGUES = new HashMap<String, List<String>>();
		ArrayList<String> greatBritain = new ArrayList<String>();
		greatBritain.add("premier league");
		AVAILABLE_LEAGUES.put("soccer", greatBritain);
	}
}
