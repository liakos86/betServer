package gr.server.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public final class Server {

	public static final Logger LOG = Logger.getLogger(Server.class);
	
	public static final String soccer_chl = "soccer_uefa_champs_league";
	
	public static final String soccer_premier_league = "soccer_epl";
	
	public static List<String> AVAILABLE_LEAGUES;
	
	static{
		AVAILABLE_LEAGUES = new ArrayList<String>();
		AVAILABLE_LEAGUES.add(soccer_chl);
		AVAILABLE_LEAGUES.add(soccer_premier_league);
	}
}
