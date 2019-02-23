package gr.server.impl.service;

import gr.server.application.exception.UserExistsException;
import gr.server.data.user.model.User;
import gr.server.data.user.model.UserPrediction;
import gr.server.data.util.FileHelperUtils;
import gr.server.def.service.MyBetOddsService;
import gr.server.impl.client.MongoClientHelperImpl;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.bson.Document;




@Path("/betServer")
@Consumes(MediaType.APPLICATION_XML)
@Produces(MediaType.APPLICATION_XML)
public class MyBetOddsServiceImpl 
implements MyBetOddsService {

	
//	@Override
//	@GET
//    @Path("/{id}/delete")
//	public Response deletePerson(@PathParam("id") int id) {
	
	@Override
	@POST
    @Path("/placeBet")
	public Document placeBet(UserPrediction userPrediction) {
		return new MongoClientHelperImpl().placePrediction(userPrediction);
		
	}
	
	@Override
	@POST
    @Path("/createUser")
	public Document createUser(User user) throws UserExistsException {
		return new MongoClientHelperImpl().createUser(user);
	}
	
	

//	@Override
//	@GET
//    @Path("/{id}/delete")
//	public Response deletePerson(@PathParam("id") int id) {
//		Response response = new Response();
//		if(persons.get(id) == null){
//			response.setStatus(false);
//			response.setMessage("Person Doesn't Exists");
//			return response;
//		}
//		persons.remove(id);
//		response.setStatus(true);
//		response.setMessage("Person deleted successfully");
//		return response;
//	}
//
//	@Override
//	@GET
//	@Path("/{id}/get")
//	public Person getPerson(@PathParam("id") int id) {
//		return persons.get(id);
//	}
//	
//	@GET
//	@Path("/{id}/getDummy")
//	public Person getDummyPerson(@PathParam("id") int id) {
//		Person p = new Person(99, "Dummy");
//		p.setId(id);
//		return p;
//	}

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
