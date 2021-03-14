package database;

import java.util.ArrayList;

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
	
}
