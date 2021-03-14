package database;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import com.google.gson.Gson;

import beans.BuyerType;
import beans.Comment;
import beans.Location;
import beans.Manifestation;
import beans.Ticket;
import beans.User;

public class Database {
	
	public static ArrayList<User> users = new ArrayList<User>();

	public static ArrayList<BuyerType> buyerTypes = new ArrayList<BuyerType>();
	
	public static ArrayList<Location> locations = new ArrayList<Location>();
	
	public static ArrayList<Manifestation> manifestations = new ArrayList<Manifestation>();
	
	public static ArrayList<Ticket> tickets = new ArrayList<Ticket>();
	
	public static ArrayList<Comment> comments = new ArrayList<Comment>();

	private static FileWriter file;
	
	public static void saveUsers() {
		
		String json = new Gson().toJson(users);
		
		try {
			System.out.println("save");
			file = new FileWriter(new File("src/database/users.json"));
			file.write(json);
			file.close();
	
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	public static boolean addUser(User newUser) {

			System.out.println("add");
			users.add(newUser);

			System.out.println("added");
			saveUsers();

			System.out.println("saved");
			
			return true;
			
		}
	
}
