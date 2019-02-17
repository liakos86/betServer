package gr.server.impl.service;

/**
 * 
 */
import gr.server.data.util.FileHelperUtils;
import gr.server.def.service.MyBetOddsService;
import gr.server.model.Person;
import gr.server.model.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;




@Path("/person")
@Consumes(MediaType.APPLICATION_XML)
@Produces(MediaType.APPLICATION_XML)
public class MyBetOddsServiceImpl implements MyBetOddsService {

	private static Map<Integer,Person> persons = new HashMap<Integer,Person>();
	
	@Override
	@POST
    @Path("/add")
	public Response addPerson(Person p) {
		Response response = new Response();
		if(persons.get(p.getId()) != null){
			response.setStatus(false);
			response.setMessage("Person Already Exists");
			return response;
		}
		persons.put(p.getId(), p);
		response.setStatus(true);
		response.setMessage("Person created successfully");
		return response;
	}

	@Override
	@GET
    @Path("/{id}/delete")
	public Response deletePerson(@PathParam("id") int id) {
		Response response = new Response();
		if(persons.get(id) == null){
			response.setStatus(false);
			response.setMessage("Person Doesn't Exists");
			return response;
		}
		persons.remove(id);
		response.setStatus(true);
		response.setMessage("Person deleted successfully");
		return response;
	}

	@Override
	@GET
	@Path("/{id}/get")
	public Person getPerson(@PathParam("id") int id) {
		return persons.get(id);
	}
	
	@GET
	@Path("/{id}/getDummy")
	public Person getDummyPerson(@PathParam("id") int id) {
		Person p = new Person(99, "Dummy");
		p.setId(id);
		return p;
	}

	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getUpcoming")
	public String getUpcoming() throws IOException {
		return FileHelperUtils.readFromFile("/mockResponses/jsonServerEPL.txt");
	}
	
	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getLeagues")
	public String getLeagues() throws IOException {
		return FileHelperUtils.readFromFile("/mockResponses/jsonServerEPL.txt");
	}

}