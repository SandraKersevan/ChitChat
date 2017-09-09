
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class USER extends TimerTask{
	private ChatFrame chat;
	public boolean prijava = false;
	public String user;
	
	public USER(ChatFrame chat, String user, boolean prijava) {
		this.chat = chat;
		this.prijava = prijava;
		this.user = user;
	}

	// robot
	@Override
	public void run(){
		if (prijava) { 
			try {
				receiveMessage(this.user);
			} catch (URISyntaxException e) {
				e.printStackTrace();
				System.out.println("Prišlo je do napake pri robotu.");
			}
		}
	}

	// ostali uporabniki
	public void getUsers(){
		try {
			String hello = Request.Get("http://chitchat.andrej.com/users")
						  .execute()
						  .returnContent().asString();

			chat.addMessage("","Trenutni so prisotni: " + hello);
        } catch (IOException e) {
            e.printStackTrace();
			System.out.println("Prišlo je do napake pri pridobivanju prisotnih uporabnikov.");
        }
	}

	// prijava
	public void logIn(String user) throws URISyntaxException{
		try {
			String time = Long.toString(new Date().getTime());

			URI uri = new URIBuilder("http://chitchat.andrej.com/users?username="+user)
					.addParameter("stop_cache", time)
					.build();

			Request.Post(uri)
					.execute()
					.returnContent().asString();
			
			prijava = true;
			chat.addMessage("", "Pozdravljeni " + user + "! Uspešno ste se prijavili!");
        } catch (IOException e) {
        	prijava = false;
            e.printStackTrace();
            chat.addMessage("", "Prišlo je do napake pri prijavi " + user + "!");
            System.out.println("Prišlo je do napake pri prijavi.");
        }
	}	
	
	// odjava
	public void logOut(String user) throws URISyntaxException{
		try {
			String time = Long.toString(new Date().getTime());

			URI uri = new URIBuilder("http://chitchat.andrej.com/users?username="+user)
					.addParameter("stop_cache", time)
					.build();

			Request.Delete(uri)
					.execute()
					.returnContent().asString();

			prijava = false;
			chat.addMessage("", "Nasvidenje " + user + "! Uspešno ste se odjavili!");
        } catch (IOException e) {
        	prijava = true;
            e.printStackTrace();
			System.out.println("Prišlo je do napake pri odjavi.");
			chat.addMessage("", "Prišlo je do napake pri odjavi " + user + "!");
        }
	}
	
	/**
	 * Activate the robot!
	 */
	public void activate() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(this, 5000, 1000);
	}
	
	// prejeto sporoèilo
	public void receiveMessage(String user) throws URISyntaxException{
		try {
			URI uri = new URIBuilder("http://chitchat.andrej.com/messages")
			          .addParameter("username", user)
			          .build();

			String responseBody = Request.Get(uri)
                            .execute()
                            .returnContent().asString();	
			
			chat.addMessage(user, responseBody);		
        } catch (IOException e) {
            e.printStackTrace();
			System.out.println("Prišlo je do napake pri prejemanju sporoèil.");
        }
	}
	
	// poslano sporoèilo
	public void sendMessage(String user, String global, String text, String recipient) throws URISyntaxException{
		try {
			String time = Long.toString(new Date().getTime());

			URI uri = new URIBuilder("http://chitchat.andrej.com/messages?username="+user)
					.addParameter("stop_cache", time)
					.build();
			
			String message;
			
			//javno sporocilo
			if (global == "true") {
				message = "{ \"global\" : "+global+", \"text\" : \""+ text +"\"  }";
			}
			// privat sporoèilo
			else {
				message = "{ \"global\" : "+global+", \"recipient\" : \""+recipient+"\", \"text\" : \""+text+"\"  }";
			}
			
			String responseBody = Request.Post(uri)
					.bodyString(message, ContentType.APPLICATION_JSON)
					.execute()
					.returnContent().asString();

			if (global == "false") {
				chat.addMessage("Za " + recipient, responseBody);
			} else {
				chat.addMessage(user, responseBody);
			}
        } catch (IOException e) {
            e.printStackTrace();
			System.out.println("Prišlo je do napake pri pošiljanju sporoèila.");
			chat.addMessage("", "Prišlo je do napake pri pošiljanju sporoèila!");
        }
	}
}