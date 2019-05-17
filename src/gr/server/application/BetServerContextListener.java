package gr.server.application;

import gr.server.data.user.model.User;
import gr.server.impl.client.MongoClientHelperImpl;
import gr.server.mongo.util.MongoCollectionUtils;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class BetServerContextListener
implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("SHUTTING DOWN");
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
			TimerTask task = new TimerTask() {
		        public void run() {
		            System.out.println("Task performed on: " + new Date() + "n" +
		              "Thread's name: " + Thread.currentThread().getName());
		            
		            User u = new User("rr");
		            u.setBalance(44d);
		            new MongoClientHelperImpl().settleBets();
		        }
		    };
		    Timer timer = new Timer("Timer");
		     
		    long delay = 10L;
		    timer.schedule(task, delay);
	}

}
