
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	// ostali uporabniki
	public void getUsers() {
		try {
			String responseBody = Request.Get("http://chitchat.andrej.com/users")
						  .execute()
						  .returnContent().asString();
			
			String jsonStr = responseBody;
			JSONArray array = new JSONArray(jsonStr); 

			chat.addMessage("", "Trenutno so prisotni: ");
		    for(int i=0; i<array.length(); i++){
		    	JSONObject jsonObj  = array.getJSONObject(i);
			    String prisoten = jsonObj.getString("username");
			    chat.addMessage("", prisoten);
		    }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
			chat.addMessage("", "Pri�lo je do napake pri pridobivanju prisotnih uporabnikov.");
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
			chat.addMessage("", "Pozdravljeni " + user + "! Uspe�no ste se prijavili!");
        } catch (IOException e) {
        	prijava = false;
            e.printStackTrace();
            chat.addMessage("", "Pri�lo je do napake pri prijavi " + user + "!");
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
			chat.addMessage("", "Nasvidenje " + user + "! Uspe�no ste se odjavili!");
        } catch (IOException e) {
        	prijava = true;
            e.printStackTrace();
			chat.addMessage("", "Pri�lo je do napake pri odjavi " + user + "!");
        }
	}
	
	/**
	 * Activate the robot!
	 */
	public void activate() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(this, 5000, 1000);
	}
	
	// prejeto sporo�ilo
	public void receiveMessage(String user) throws URISyntaxException, JSONException{
		try {
			URI uri = new URIBuilder("http://chitchat.andrej.com/messages")
			          .addParameter("username", user)
			          .build();

			String responseBody = Request.Get(uri)
                            .execute()
                            .returnContent().asString();
			
			String jsonStr = responseBody;
			JSONArray array = new JSONArray(jsonStr); 

		    for(int i=0; i<array.length(); i++){
		    	JSONObject jsonObj  = array.getJSONObject(i);
			    String posiljatelj = jsonObj.getString("sender");
			    String sporocilo = jsonObj.getString("text");
			    chat.addMessage(posiljatelj, sporocilo);
			}
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	// poslano sporo�ilo
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
			// privat sporo�ilo
			else {
				message = "{ \"global\" : "+global+", \"recipient\" : \""+recipient+"\", \"text\" : \""+text+"\"  }";
			}
			
			String responseBody = Request.Post(uri)
					.bodyString(message, ContentType.APPLICATION_JSON)
					.execute()
					.returnContent().asString();
			
			String jsonStr = responseBody;
			JSONObject jsonObj  = new JSONObject(jsonStr);
		    String status = jsonObj.getString("status");
		    
			if (global == "false") {
				chat.addMessage("Za " + recipient, status);
			} else {
				chat.addMessage(user, status);
			}
        } catch (IOException | JSONException e) {
            e.printStackTrace();
			chat.addMessage("", "Pri�lo je do napake pri po�iljanju sporo�ila!");
        }
	}
}