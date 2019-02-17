package gr.server.def.service;

import gr.server.model.Person;
import gr.server.model.Response;

import java.io.IOException;

/**
 * 
 */
public interface MyBetOddsService {

	public Response addPerson(Person p);
	
	public Response deletePerson(int id);
	
	public Person getPerson(int id);
	
	public String getUpcoming() throws IOException;

	public String getLeagues() throws IOException;

}
