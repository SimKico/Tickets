package database;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import beans.BuyerType;
import beans.Comment;
import beans.Gender;
import beans.Location;
import beans.Manifestation;
import beans.ManifestationType;
import beans.Role;
import beans.Ticket;
import beans.TicketStatus;
import beans.TicketType;
import beans.User;

public class Data {

	private static Gson g = new Gson();
	private static FileWriter file;
	
	public static void readingData() throws ParseException {

		File buyerTypes = new File("src/database/buyerTypes.json");
		File users = new File("src/database/users.json");
		File manifestations = new File("src/database/manifestations.json");
		File locations = new File("src/database/locations.json");
		File tickets = new File("src/database/tickets.json");
		File comments = new File("src/database/comments.json");
		
		if(buyerTypes.length()==0) {
			System.out.println("[System]: 'buyerTypes.json' is filled.");
			fillBuyerType();
		}
	
		if(users.length()==0) {
			System.out.println("[System]: 'users.json' is filled.");
			fillUsers();
		}
		
		if(manifestations.length()==0) {
			System.out.println("[System]: 'manifestations.json' is filled.");
			fillManifestations();
		}
		
		if(locations.length()==0) {
			System.out.println("[System]: 'locations.json' is filled.");
			fillLocations();
		}
		
		if(tickets.length()==0) {
			System.out.println("[System]: 'tickets.json' is filled.");
			fillTickets();
		}
		
		if(comments.length()==0) {
			System.out.println("[System]: 'comments.json' is filled.");
			fillComments();
		}
		
		
		try {
			FileReader reader = new FileReader(new File("src/database/users.json"));
			JsonParser jp = new JsonParser();
			JsonElement obj = jp.parse(reader);
			JsonArray j = obj.getAsJsonArray();
			for (JsonElement jsonElement : j) {
				User user = g.fromJson(jsonElement, User.class);
				Database.users.add(user);
			}
			System.out.println("[System]: 'Users' is read. ["+j.size()+"] elements.");
			
			reader = new FileReader(new File("src/database/buyerTypes.json"));
			j = jp.parse(reader).getAsJsonArray();
			
			for (JsonElement jsonElement : j) {
				BuyerType buyerType = g.fromJson(jsonElement, BuyerType.class);
				Database.buyerTypes.add(buyerType);
			}
			System.out.println("[System]: 'BuyerTypes' is read. ["+j.size()+"] elements.");
			
			reader = new FileReader(new File("src/database/manifestations.json"));
			j = jp.parse(reader).getAsJsonArray();
			
			for (JsonElement jsonElement : j) {
				Manifestation manifestation = g.fromJson(jsonElement, Manifestation.class);
				Database.manifestations.add(manifestation);
			}
			System.out.println("[System]: 'Manifestations' is read. ["+j.size()+"] elements.");
			
			reader = new FileReader(new File("src/database/locations.json"));
			j = jp.parse(reader).getAsJsonArray();
			
			for (JsonElement jsonElement : j) {
				Location location = g.fromJson(jsonElement, Location.class);
				Database.locations.add(location);
			}
			System.out.println("[System]: 'Location' is read. ["+j.size()+"] elements.");
			
			reader = new FileReader(new File("src/database/tickets.json"));
			j = jp.parse(reader).getAsJsonArray();
			
			for (JsonElement jsonElement : j) {
				Ticket ticket = g.fromJson(jsonElement, Ticket.class);
				Database.tickets.add(ticket);
			}
			System.out.println("[System]: 'Tickets' is read. ["+j.size()+"] elements.");
			
			reader = new FileReader(new File("src/database/comments.json"));
			j = jp.parse(reader).getAsJsonArray();
			
			for (JsonElement jsonElement : j) {
				Comment comment = g.fromJson(jsonElement, Comment.class);
				Database.comments.add(comment);
			}
			System.out.println("[System]: 'Comments' is read. ["+j.size()+"] elements.");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	public static void fillBuyerType() {
		
		ArrayList<BuyerType> buyerType = new ArrayList<BuyerType>();
		
		BuyerType gold = new BuyerType("gold", 30, 4000); 

		BuyerType silver = new BuyerType("silver", 20, 3000); 
 
		BuyerType bronze = new BuyerType("bronze", 10, 2000); 
		
		buyerType.add(gold);
		buyerType.add(silver);
		buyerType.add(bronze);
		
		String json = new Gson().toJson(buyerType);
		
		try {
			file = new FileWriter(new File("src/database/buyerTypes.json"));
			file.write(json);
			file.close();
	
		} catch (Exception e) {
			// TODO: handle exception
		}		
	}
	
	public static void fillUsers() throws ParseException {

		ArrayList<User> users = new ArrayList<User>();
		
		String sDate1="31.12.1998";
		String sDate2="06.12.1965";
		String sDate3="05.10.1988";
		String sDate4="01.02.1995";
		String sDate5="31.10.1997";
		String sDate6="15.02.1991";
		
		
		Date date1 = new SimpleDateFormat("dd.MM.yyyy").parse(sDate1);  
		Date date2 = new SimpleDateFormat("dd.MM.yyyy").parse(sDate2);  
		Date date3 = new SimpleDateFormat("dd.MM.yyyy").parse(sDate3);  
		Date date4 = new SimpleDateFormat("dd.MM.yyyy").parse(sDate4);  
		Date date5 = new SimpleDateFormat("dd.MM.yyyy").parse(sDate5);  
		Date date6 = new SimpleDateFormat("dd.MM.yyyy").parse(sDate6); //exception maybe
		
		BuyerType gold = new BuyerType("gold", 30, 4000); 

		BuyerType silver = new BuyerType("silver", 20, 3000); 
 
		BuyerType bronze = new BuyerType("bronze", 10, 2000); 
		
		String ssDate1="13.12.2020 20:00";
		String ssDate2="18.04.2021 19:00";
		String ssDate3="15.10.2021 21:00";
		
		Date sdate1 = new SimpleDateFormat("dd.MM.yyyy HH:mm").parse(ssDate1);  
		Date sdate2 = new SimpleDateFormat("dd.MM.yyyy HH:mm").parse(ssDate2);  
		Date sdate3 = new SimpleDateFormat("dd.MM.yyyy HH:mm").parse(ssDate3);  
		
		Location location1 = new Location(20.46, 44.817, "Francuska", "3", "Beograd", 11000);
		Location location2 = new Location(19.843, 45.255, "Pozorišni trg", "1", "Novi Sad", 21000);
		Location location3 = new Location(19.845, 45.247, "Sutjeska", "2", "Novi Sad", 21000);
		Location location4 = new Location(20.421, 44.814, "Bulevar Arsenija Čarnojevića", "58", "Beograd", 11000);
		Location location5 = new Location(19.863, 45.252, "Beogradska", "bb", "Petrovaradin", 21131);
		Location location6 = new Location(19.845, 45.255, "Trg Slobode", "bb", "Novi Sad", 21000);
		
		Manifestation manifestation1 = new Manifestation("Koncert1", ManifestationType.CONCERTS, date1, 1500, true, location4, "source/cloud-desktop-background.jpg", 10, 7, 2, 1, 4.5, false);
		Manifestation manifestation2 = new Manifestation("Festival1", ManifestationType.FESTIVALS, date2, 1200, true, location5, "source/cloud-desktop-background.jpg", 20, 12, 5, 3, 0, false);
		Manifestation manifestation3 = new Manifestation("Pozoriste1", ManifestationType.THEATERS, date3, 789, true, location1, "source/cloud-desktop-background.jpg", 20, 12, 5, 3, 0, false);
		Manifestation manifestation4 = new Manifestation("Koncert2", ManifestationType.CONCERTS, date3, 1500, false, location3, "source/cloud-desktop-background.jpg", 20, 12, 5, 3, 0, false);
		Manifestation manifestation5 = new Manifestation("Festival2", ManifestationType.FESTIVALS, date2, 1200, false, location6, "source/cloud-desktop-background.jpg", 20, 12, 5, 3, 0, false);
		Manifestation manifestation6 = new Manifestation("Pozoriste2", ManifestationType.THEATERS, date1, 789, true, location2, "source/cloud-desktop-background.jpg", 20,  12, 5, 3,4.3,false);
		
		ArrayList<Manifestation> manifestations = new ArrayList<Manifestation>();
		manifestations.add(manifestation1);
		manifestations.add(manifestation2);
		manifestations.add(manifestation3);
		manifestations.add(manifestation4);
		
		users.add(new User("admin1", "123", "Petra", "Peric", Gender.FEMALE, date1, Role.ADMIN,null,null,0,null, false, false));

		users.add(new User("admin2", "123", "Nikola", "Nikolic", Gender.MALE, date2, Role.ADMIN,null,null,0,null, false, false));

		users.add(new User("seller1", "123", "Petar", "Petrovic", Gender.MALE, date3, Role.SELLER ,null, manifestations, 0, null, false, false));

		users.add(new User("buyer1", "123", "Natasa", "Kovacevic", Gender.FEMALE, date4, Role.BUYER,null,null,4500, gold, false, false));

		users.add(new User("buyer2", "123", "Snjezana", "Simic", Gender.FEMALE, date5, Role.BUYER, null, null, 3500, silver, false, false));
		
		users.add(new User("buyer3", "123", "Goran", "Stanic", Gender.MALE, date6, Role.BUYER, null, null, 2500, bronze, false, false));
		
		
		String json = new Gson().toJson(users);
		
		try {
			file = new FileWriter(new File("src/database/users.json"));
			file.write(json);
			file.close();
	
		} catch (Exception e) {
			// TODO: handle exception
		}			
	}
	
	public static void fillLocations() {
		
		ArrayList<Location> locations = new ArrayList<Location>();
		
		locations.add(new Location(20.46, 44.817, "Francuska", "3", "Beograd", 11000));
		locations.add(new Location(19.843, 45.255, "Pozorišni trg", "1", "Novi Sad", 21000));
		locations.add(new Location(19.845, 45.247, "Sutjeska", "2", "Novi Sad", 21000));
		locations.add(new Location(20.421, 44.814, "Bulevar Arsenija Čarnojevića", "58", "Beograd", 11000));
		locations.add(new Location(19.863, 45.252, "Beogradska", "bb", "Petrovaradin", 21131));
		locations.add(new Location(19.845, 45.255, "Trg Slobode", "bb", "Novi Sad", 21000));
		
		String json = new Gson().toJson(locations);
		
		try {
			file = new FileWriter(new File("src/database/locations.json"));
			file.write(json);
			file.close();
	
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public static void fillManifestations() throws ParseException {
		
		ArrayList<Manifestation> manifestations = new ArrayList<Manifestation>();
		
		String sDate1="13.12.2020 20:00";
		String sDate2="18.04.2021 19:00";
		String sDate3="15.10.2021 21:00";
		
		Date date1 = new SimpleDateFormat("dd.MM.yyyy HH:mm").parse(sDate1);  
		Date date2 = new SimpleDateFormat("dd.MM.yyyy HH:mm").parse(sDate2);  
		Date date3 = new SimpleDateFormat("dd.MM.yyyy HH:mm").parse(sDate3);  
		
		Location location1 = new Location(20.46, 44.817, "Francuska", "3", "Beograd", 11000);
		Location location2 = new Location(19.843, 45.255, "Pozorišni trg", "1", "Novi Sad", 21000);
		Location location3 = new Location(19.845, 45.247, "Sutjeska", "2", "Novi Sad", 21000);
		Location location4 = new Location(20.421, 44.814, "Bulevar Arsenija Čarnojevića", "58", "Beograd", 11000);
		Location location5 = new Location(19.863, 45.252, "Beogradska", "bb", "Petrovaradin", 21131);
		Location location6 = new Location(19.845, 45.255, "Trg Slobode", "bb", "Novi Sad", 21000);
		
		manifestations.add(new Manifestation("Koncert1", ManifestationType.CONCERTS, date1, 1500, true, location4, "source/cloud-desktop-background.jpg", 10, 5, 3, 2, 4.5, false));
		manifestations.add(new Manifestation("Festival1", ManifestationType.FESTIVALS, date2, 1200, true, location5, "source/cloud-desktop-background.jpg", 20, 12, 5, 3, 0, false));
		manifestations.add(new Manifestation("Pozoriste1", ManifestationType.THEATERS, date3, 789, true, location1, "source/cloud-desktop-background.jpg", 20, 12, 5, 3, 0, false));
		manifestations.add(new Manifestation("Koncert2", ManifestationType.CONCERTS, date3, 1500, false, location3, "source/cloud-desktop-background.jpg", 20, 12, 5, 3, 0, false));
		manifestations.add(new Manifestation("Festival2", ManifestationType.FESTIVALS, date2, 1200, false, location6, "source/cloud-desktop-background.jpg", 20, 12, 5, 3, 0, false));
		manifestations.add(new Manifestation("Pozoriste2", ManifestationType.THEATERS, date1, 789, true, location2, "source/cloud-desktop-background.jpg", 20, 12, 5, 3, 4.3, false));
		System.out.println(manifestations.get(0).getLocation().getStreet());
		String json = new Gson().toJson(manifestations);
		
		try {
			file = new FileWriter(new File("src/database/manifestations.json"));
			file.write(json);
			file.close();
	
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private static void fillTickets() throws ParseException {
		
		ArrayList<Ticket> tickets = new ArrayList<Ticket>();
		
		String sDate1="13.12.2020";
		String sDate2="18.04.2021";
		String sDate3="15.10.2021";
		
		Date date1 = new SimpleDateFormat("dd.MM.yyyy").parse(sDate1);  
		Date date2 = new SimpleDateFormat("dd.MM.yyyy").parse(sDate2);  
		Date date3 = new SimpleDateFormat("dd.MM.yyyy").parse(sDate3);  
		
		int priceKoncert1Regular = 1500;

		int priceFestival1Regular = 1200;

		int pricePozoriste1Regular = 789;
		
		int priceKoncert2Regural = 1500;
		
		int priceFestival2Regular = 1200;

		int pricePozoriste3Regular = 789;
		
		Ticket ticket1 = new Ticket("t1", "Koncert1", date1, priceKoncert1Regular, "Natasa", "Kovacevic", TicketStatus.RESERVED, TicketType.REGULAR, false);
		Ticket ticket2 = new Ticket("t2", "Koncert1", date1, priceKoncert1Regular * 2, "Snjezana", "Simic", TicketStatus.RESERVED, TicketType.FANPIT, false);
		Ticket ticket3 = new Ticket("t3", "Koncert1", date1, priceKoncert1Regular * 4, "Goran", "Stanic", TicketStatus.RESERVED, TicketType.VIP, false);
		Ticket ticket4 = new Ticket("t4", "Festival1", date2, priceFestival1Regular, "Natasa", "Kovacevic", TicketStatus.RESERVED, TicketType.REGULAR, false);
		Ticket ticket5 = new Ticket("t5", "Festival1", date2, priceFestival1Regular * 2,  "Snjezana", "Simic", TicketStatus.RESERVED, TicketType.FANPIT, false);
		Ticket ticket6 = new Ticket("t6", "Festival1", date2, priceFestival1Regular * 4,  "Goran", "Stanic", TicketStatus.CANCELD, TicketType.VIP, false);
		Ticket ticket7 = new Ticket("t7", "Pozoriste1", date3, pricePozoriste1Regular, "Natasa", "Kovacevic", TicketStatus.RESERVED, TicketType.REGULAR, false);
		Ticket ticket8 = new Ticket("t8", "Koncert2", date3, priceKoncert2Regural, "Snjezana", "Simic", TicketStatus.RESERVED, TicketType.REGULAR, false);
		Ticket ticket9 = new Ticket("t9", "Festival2", date2, priceFestival2Regular, "Goran", "Stanic", TicketStatus.RESERVED, TicketType.REGULAR, false);
		Ticket ticket10 = new Ticket("t10", "Pozoriste2", date1, pricePozoriste3Regular, "Natasa", "Kovacevic", TicketStatus.CANCELD, TicketType.REGULAR, false);
		
		tickets.add(ticket1);
		tickets.add(ticket2);
		tickets.add(ticket3);
		tickets.add(ticket4);
		tickets.add(ticket5);
		tickets.add(ticket6);
		tickets.add(ticket7);
		tickets.add(ticket8);
		tickets.add(ticket9);
		tickets.add(ticket10);
		
		String json = new Gson().toJson(tickets);
		
		try {
			file = new FileWriter(new File("src/database/tickets.json"));
			file.write(json);
			file.close();
	
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	private static void fillComments() throws ParseException {
		
		ArrayList<Comment> comments = new ArrayList<Comment>();
		
		String sDate4="01.02.1995";
		
		Date date4 = new SimpleDateFormat("dd.MM.yyyy").parse(sDate4);  
		
		BuyerType gold = new BuyerType("gold", 30, 4000); 
		
		User user1 = new User("buyer1", "123", "Natasa", "Kovacevic", Gender.FEMALE, date4, Role.BUYER,null,null,4500, gold, false, false);
		
		String sDate1="13.12.2020";
		
		Date date1 = new SimpleDateFormat("dd.MM.yyyy").parse(sDate1);  
		
		Location location4 = new Location(20.421, 44.814, "Bulevar Arsenija Čarnojevića", "58", "Beograd", 11000);
		
		Manifestation manifestation1 = new Manifestation("Koncert1", ManifestationType.CONCERTS, date1, 1500, true, location4, "static/source/cloud-desktop-background.jpg", 10, 5, 3, 2, 4.5, false);
		
		Comment comment1 = new Comment(user1, manifestation1, "Sve pohvale.", 5, false);
		
		comments.add(comment1);
		
		String json = new Gson().toJson(comments);
		
		try {
			file = new FileWriter(new File("src/database/comments.json"));
			file.write(json);
			file.close();
	
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}

