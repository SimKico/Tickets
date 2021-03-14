package rest;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.staticFiles;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import beans.BuyerType;
import beans.Gender;
import beans.Role;
import beans.User;
import database.Data;
import database.Database;
import spark.Session;

public class SparkAppMain {

	private static Gson g = new Gson();

	public static void main(String[] args) throws Exception {
		
		Data.readingData();
		
		port(8080);

		staticFiles.externalLocation(new File("./static").getCanonicalPath());
		
		get("/", (req, res) ->{
					
					res.redirect("login.html"); 
					return null;
				});
		

		post("/login", (req, res) -> {
			
			res.type("application/json");
					
			String data = req.body();
			System.out.println(data);
			
			User k = g.fromJson(data, User.class);
			Session ss = req.session(true);
					
			User user = ss.attribute("user");
			
			for(User kk: Database.users) {
				if(kk.getUsername().equals(k.getUsername()) && kk.getPassword().equals(k.getPassword())  ) {
					if(user == null) {
						user = kk;
						ss.attribute("user", user);	
					}
					return user.getRole();
				}						
			}
			return false;			
		});
		
		get("/logout", (req, res) ->{
			
			Session ss = req.session(true);
			User user = ss.attribute("user");
			
			if (user != null) {
				ss.invalidate();
			}
			return true;
		});
		
		post("/registration", (req, res) -> {
			
			res.type("application/json");
					
			String data = req.body();
			JsonObject job = new JsonParser().parse(data).getAsJsonObject();
		
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(job.get("birthDay").getAsString());
			
			Gender gender = job.get("gender").getAsString() == ("MALE") ? Gender.MALE : Gender.FEMALE;
		
			BuyerType buyerType = new BuyerType("none",0,0);
			User newUser = new User(job.get("username").getAsString(), job.get("password").getAsString(), job.get("firstName").getAsString(), job.get("lastName").getAsString(), gender, date, Role.BUYER, null, null, 0, buyerType);
		
			Session ss = req.session(true);
					
			User user = ss.attribute("user");
					
			for(User kk: Database.users) {
				if(kk.getUsername().equals(newUser.getUsername())) {
						return false;
					}else {
						Database.addUser(newUser);
						return false;
					}
				}						
			
			return false;
		});
		
		
		get("/profile", (req, res) -> {
			
			res.type("application/json");
			User k = req.session().attribute("user");
			System.out.println(req.session());
			return g.toJson(k);
					
			
		});
		
		put("/profileUpdate", (req, res) -> {
			
			res.type("application/json");
			String data = req.body();
			JsonObject job = new JsonParser().parse(data).getAsJsonObject();
			
			User oldUserData = req.session().attribute("user");
			String oldUsername = oldUserData.getUsername();
			
			System.out.println(oldUserData.getUsername());
			
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(job.get("birthDay").getAsString());
			
			Gender gender = job.get("gender").getAsString() == ("MALE") ? Gender.MALE : Gender.FEMALE;
		
			BuyerType buyerType = new BuyerType("none",0,0);
			
			
			User newUserData = new User(job.get("username").getAsString(), job.get("password").getAsString(), job.get("firstName").getAsString(), job.get("lastName").getAsString(), gender, date, Role.BUYER, null, null, 0, buyerType);
		
			Session ss = req.session(true);
					
			for(User kk: Database.users) {
				if(kk.getUsername().equals(newUserData.getUsername())) {
						return false;
					}else {
						for(User oneUser : Database.users) {
							if(oneUser.getUsername().equals(oldUsername)) {
								oneUser.setUsername(newUserData.getUsername());

								oneUser.setFirstName(newUserData.getFirstName());

								oneUser.setLastName(newUserData.getLastName());

								oneUser.setGender(newUserData.getGender());

								oneUser.setPassword(newUserData.getPassword());
								
								Database.saveUsers();
								return true;
							}
						}
					}
				}						
			
			return false;	
		});
		
	}
}
