package rest;

import static spark.Spark.get;
import static spark.Spark.post;

import static spark.Spark.port;
import static spark.Spark.staticFiles;
import static spark.Spark.webSocket;

import java.io.File;

import com.google.gson.Gson;

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
		
	}
}
