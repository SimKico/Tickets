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
	
	public static void saveTickets() {
		
		String json = new Gson().toJson(tickets);
		
		try {
			System.out.println("save");
			file = new FileWriter(new File("src/database/tickets.json"));
			file.write(json);
			file.close();
	
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public static void saveManifestations() {
		
		String json = new Gson().toJson(manifestations);
		
		try {
			System.out.println("save");
			file = new FileWriter(new File("src/database/manifestations.json"));
			file.write(json);
			file.close();
	
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public static boolean addUser(User newUser) {

			users.add(newUser);
			saveUsers();

			return true;
		}
	
	public static boolean addTicket(Ticket newTicket) {

		tickets.add(newTicket);
		saveTickets();

		return true;
	}
	
	public static boolean addManifestation(Manifestation newManifestation) {

		manifestations.add(newManifestation);
		saveTickets();

		return true;
	}
	
	public static boolean  isSellersManifestation(User seller, String searchManifestition) {
		for(User user : Database.users) {
			if(user.getUsername().equals(seller.getUsername())) {
				if(user.getManifestations() != null) {
					for(Manifestation manifestation : user.getManifestations()) {
						if(manifestation.getTitle().equals(searchManifestition)) {			
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
}
