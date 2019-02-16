package gr.server.application;

import gr.server.impl.service.MyBetOddsServiceImpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Application;


/**
 * 
 */
public class RestApplication  extends Application
{
//    public Set<Class<?>> getClasses()
//    {
//        Set<Class<?>> s = new HashSet<Class<?>>();
//        s.add(PersonServiceImpl.class);
//        return s;
//    }
    

    @Override
    public Set<Class<?>> getClasses() {
        
        Set<Class<?>> resources = new HashSet<Class<?>>();
        
        System.out.println("REST configuration starting: getClasses()");      
        
        resources.add(MyBetOddsServiceImpl.class);
        
        //features
        //this will register Jackson JSON providers
        ///resources.add(org.glassfish.jersey.jackson.JacksonFeature.class);
        //we could also use this:
        //resources.add(com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider.class);
        
        //instead let's do it manually:
        ///resources.add(MyJacksonProvider.class);
        //==> we could also choose packages, see below getProperties()
        
        System.out.println("REST configuration ended successfully.");
        
        return resources;
    }
    
    @Override
    public Set<Object> getSingletons() {
        return Collections.emptySet();
    }
    
   
}
    
    
    
