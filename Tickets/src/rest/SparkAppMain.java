package rest;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.staticFiles;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import beans.BuyerType;
import beans.Gender;
import beans.Manifestation;
import beans.Role;
import beans.Ticket;
import beans.TicketStatus;
import beans.TicketType;
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
					
			res.redirect("homepage.html"); 
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
			
			Boolean existAlready = false;
			for(User kk: Database.users) {
				if(kk.getUsername().equals(newUser.getUsername())) {
						existAlready = true;
						return false;
					}
				}			
			
			if(!existAlready) {
					Database.addUser(newUser);
					return true;
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
		
		get("/manifestations/all", (req, res) -> {
			
			res.type("application/json");
			Collections.sort(Database.manifestations, Manifestation.dateComparator);
			return g.toJson(Database.manifestations);
					
		});
		
		get("/userTickets", (req, res) -> {
			
			res.type("application/json");
			User k = req.session().attribute("user");
			
			System.out.println(k);
			System.out.println(k.getLastName());
			
			ArrayList<Ticket> userTickets = new ArrayList<Ticket>();
			if(k.getRole().equals(Role.BUYER)) {
				for(Ticket ticket : Database.tickets) {
					if(ticket.getBuyerFirstName().equals(k.getFirstName()) && ticket.getBuyerLastName().equals(k.getLastName())) {
						System.out.println(ticket.getBuyerFirstName()+ " " + ticket.getBuyerLastName());
						userTickets.add(ticket);
					}
				}
			}else if(k.getRole().equals(Role.ADMIN)) {
				for(Ticket ticket : Database.tickets) {
					userTickets.add(ticket);
				}
			}else {
				if(k.getRole().equals(Role.SELLER)) {
					for(Ticket ticket : Database.tickets) {
						if(ticket.getStatus().equals(TicketStatus.RESERVED) && Database.isSellersManifestation(k, ticket.getManifestation())) {
							userTickets.add(ticket);
						}
					}
				}
			}
			return g.toJson(userTickets);
		});

			post("/tickets/searchManifestation", (req, res) -> {
				
				res.type("application/json");
				User k = req.session().attribute("user");
				
				String data = req.body();
			
				JsonObject job = new JsonParser().parse(data).getAsJsonObject();
				
				String manifestation = job.get("manifestation").getAsString().toLowerCase();

				ArrayList<Ticket> satisfiesManifestation = new ArrayList<Ticket>();
			
				if(k.getRole().equals(Role.ADMIN)) {
					if(manifestation != null) {
						for(Ticket ticket : Database.tickets) {
							if(ticket.getManifestation().toLowerCase().equals(manifestation)) {
								satisfiesManifestation.add(ticket);
							}
						}
					}
				}else if(k.getRole().equals(Role.SELLER)) {
					if(manifestation != null && Database.isSellersManifestation(k, manifestation)) {
						for(Ticket ticket : Database.tickets) {
							if(ticket.getManifestation().toLowerCase().equals(manifestation)) {
								satisfiesManifestation.add(ticket);
							}
						}
					}
				}else {
					for(Ticket ticket : Database.tickets) {
						if(ticket.getManifestation().toLowerCase().equals(manifestation) && ticket.getBuyerFirstName().equals(k.getFirstName()) && ticket.getBuyerLastName().equals(k.getLastName())) {
							satisfiesManifestation.add(ticket);
						}
					}
				}
				if(satisfiesManifestation.isEmpty()) {
					return false;
				}else {
					return g.toJson(satisfiesManifestation);
				}
			});
			
			post("/tickets/searchPrice", (req, res) -> {
				
				res.type("application/json");
				User k = req.session().attribute("user");
				
				String data = req.body();
			
				JsonObject job = new JsonParser().parse(data).getAsJsonObject();
				
				int minPrice = job.get("minPrice").getAsInt();

				int maxPrice = job.get("maxPrice").getAsInt();

				ArrayList<Ticket> satisfiesManifestation = new ArrayList<Ticket>();
			
				if(k.getRole().equals(Role.ADMIN)) {
						for(Ticket ticket : Database.tickets) {
							if(ticket.getPrice() >= minPrice && ticket.getPrice() <= maxPrice ) {
								satisfiesManifestation.add(ticket);
							}
					}
				}else if(k.getRole().equals(Role.SELLER)) {
						for(Ticket ticket : Database.tickets) {
							if(Database.isSellersManifestation(k, ticket.getManifestation())) {
								if(ticket.getPrice() >= minPrice && ticket.getPrice() <= maxPrice ) {
									satisfiesManifestation.add(ticket);
								}
							}
							
						
					}
				}else {
					for(Ticket ticket : Database.tickets) {
						if(ticket.getBuyerFirstName().equals(k.getFirstName()) && ticket.getBuyerLastName().equals(k.getLastName()) && ticket.getPrice() >= minPrice && ticket.getPrice() <= maxPrice ) {
							satisfiesManifestation.add(ticket);
						}
					}
				}
				if(satisfiesManifestation.isEmpty()) {
					return false;
				}else {
					return g.toJson(satisfiesManifestation);
				}
			});
			
			post("/tickets/searchDate", (req, res) -> {
				
				res.type("application/json");
				User k = req.session().attribute("user");
		
				String data = req.body();
				JsonObject job = new JsonParser().parse(data).getAsJsonObject();
			
				Date fromDate = new SimpleDateFormat("yyyy-MM-dd").parse(job.get("fromDate").getAsString());
				Date toDate = new SimpleDateFormat("yyyy-MM-dd").parse(job.get("toDate").getAsString());
			
				ArrayList<Ticket> satisfiesManifestation = new ArrayList<Ticket>();
		
				if(k.getRole().equals(Role.ADMIN)) {
						for(Ticket ticket : Database.tickets) {
							if(ticket.getManifestationDate().after(fromDate) && ticket.getManifestationDate().before(toDate)) {
								satisfiesManifestation.add(ticket);
							}
					}
				}else if(k.getRole().equals(Role.SELLER)) {
						for(Ticket ticket : Database.tickets) {
							if(Database.isSellersManifestation(k, ticket.getManifestation())) {
								if(ticket.getManifestationDate().after(fromDate) && ticket.getManifestationDate().before(toDate)) {
									satisfiesManifestation.add(ticket);
								}
							}
							
						
					}
				}else {
					for(Ticket ticket : Database.tickets) {
						if(ticket.getBuyerFirstName().equals(k.getFirstName()) && ticket.getBuyerLastName().equals(k.getLastName()) && ticket.getManifestationDate().after(fromDate) && ticket.getManifestationDate().before(toDate) ) {
							satisfiesManifestation.add(ticket);
						}
					}
				}
				if(satisfiesManifestation.isEmpty()) {
					return false;
				}else {
					return g.toJson(satisfiesManifestation);
				}
			});
			
			post("/tickets/filterTicketType", (req, res) -> {

				System.out.println("br");
				res.type("application/json");
				User k = req.session().attribute("user");
				
				String data = req.body();
			
				JsonObject job = new JsonParser().parse(data).getAsJsonObject();
				
				String type = job.get("type").getAsString().toUpperCase();
				System.out.println("da vidimo sta ne valja" + type);
				
				TicketType ticketType;
				if(type.equals( "REGULAR")) {
					ticketType = TicketType.REGULAR;
				}else if(type.equals( "FANPIT")) {
					ticketType = TicketType.FANPIT;
				}else {
					ticketType = TicketType.VIP;
				}
				
				ArrayList<Ticket> satisfiesManifestation = new ArrayList<Ticket>();
			
				if(k.getRole().equals(Role.ADMIN)) {
						for(Ticket ticket : Database.tickets) {
							if(ticket.getType().equals(ticketType)) {
								satisfiesManifestation.add(ticket);
							}
					}
				}else if(k.getRole().equals(Role.SELLER)) {
					for(Ticket ticket : Database.tickets) {
						if(Database.isSellersManifestation(k, ticket.getManifestation())) {
							if(ticket.getType().equals(ticketType)) {
								satisfiesManifestation.add(ticket);
							}
						}
					}
				}else {
					for(Ticket ticket : Database.tickets) {
						System.out.println(ticketType);
						if(ticket.getBuyerFirstName().equals(k.getFirstName()) && ticket.getBuyerLastName().equals(k.getLastName()) && ticket.getType().equals(ticketType)) {
							System.out.println("hehe");
							satisfiesManifestation.add(ticket);
						}
					}
				}
				if(satisfiesManifestation.isEmpty()) {
					return false;
				}else {
					return g.toJson(satisfiesManifestation);
				}
			});
			
			post("/tickets/filterTicketStatus", (req, res) -> {

				res.type("application/json");
				User k = req.session().attribute("user");
				
				String data = req.body();
			
				JsonObject job = new JsonParser().parse(data).getAsJsonObject();
				
				String type = job.get("status").getAsString().toUpperCase();
				
				TicketStatus ticketStatus;
				if(type.equals("RESERVED")) {
					ticketStatus = TicketStatus.RESERVED;
				}else {
					ticketStatus = TicketStatus.CANCELD;
				}
				
				ArrayList<Ticket> satisfiesManifestation = new ArrayList<Ticket>();
			
				if(k.getRole().equals(Role.ADMIN)) {
						for(Ticket ticket : Database.tickets) {
							if(ticket.getStatus().equals(ticketStatus)) {
								satisfiesManifestation.add(ticket);
							}
					}
				}else if(k.getRole().equals(Role.SELLER)) {
					for(Ticket ticket : Database.tickets) {
						if(Database.isSellersManifestation(k, ticket.getManifestation())) {
							if(ticket.getStatus().equals(ticketStatus)) {
								satisfiesManifestation.add(ticket);
							}
						}
					}
				}else {
					for(Ticket ticket : Database.tickets) {
						if(ticket.getBuyerFirstName().equals(k.getFirstName()) && ticket.getBuyerLastName().equals(k.getLastName()) && ticket.getStatus().equals(ticketStatus)) {
							satisfiesManifestation.add(ticket);
						}
					}
				}
				if(satisfiesManifestation.isEmpty()) {
					return false;
				}else {
					return g.toJson(satisfiesManifestation);
				}
			});
			

			post("/tickets/sortAsc", (req, res) -> {
				
				res.type("application/json");
				User k = req.session().attribute("user");
				
				ArrayList<Ticket> userTickets = new ArrayList<Ticket>();
				
				String data = req.body();
				
				JsonObject job = new JsonParser().parse(data).getAsJsonObject();
				
				String sortBy = job.get("sortBy").getAsString();
				
				if(k.getRole().equals(Role.BUYER)) {
					for(Ticket ticket : Database.tickets) {
						if(ticket.getBuyerFirstName().equals(k.getFirstName()) && ticket.getBuyerLastName().equals(k.getLastName())) {
							System.out.println(ticket.getBuyerFirstName()+ " " + ticket.getBuyerLastName());
							userTickets.add(ticket);
						}
					}
				}else if(k.getRole().equals(Role.ADMIN)) {
					for(Ticket ticket : Database.tickets) {
						userTickets.add(ticket);
					}
				}else {
					if(k.getRole().equals(Role.SELLER)) {
						for(Ticket ticket : Database.tickets) {
							if(ticket.getStatus().equals(TicketStatus.RESERVED) && Database.isSellersManifestation(k, ticket.getManifestation())) {
								userTickets.add(ticket);
							}
						}
					}
				}
				if(sortBy.equals("title")) {
					Collections.sort(userTickets, Ticket.titleComparatorASC);
				}else if(sortBy.equals("price")) {
					Collections.sort(userTickets, Ticket.priceComparatorASC);
				}else {
					Collections.sort(userTickets, Ticket.dateComparatorASC);
				}
				return g.toJson(userTickets);
			});
			
			post("/tickets/sortDsc", (req, res) -> {
				
				res.type("application/json");
				User k = req.session().attribute("user");
				
				ArrayList<Ticket> userTickets = new ArrayList<Ticket>();
				
				String data = req.body();
				
				JsonObject job = new JsonParser().parse(data).getAsJsonObject();
				
				String sortBy = job.get("sortBy").getAsString();
				
				if(k.getRole().equals(Role.BUYER)) {
					for(Ticket ticket : Database.tickets) {
						if(ticket.getBuyerFirstName().equals(k.getFirstName()) && ticket.getBuyerLastName().equals(k.getLastName())) {
							System.out.println(ticket.getBuyerFirstName()+ " " + ticket.getBuyerLastName());
							userTickets.add(ticket);
						}
					}
				}else if(k.getRole().equals(Role.ADMIN)) {
					for(Ticket ticket : Database.tickets) {
						userTickets.add(ticket);
					}
				}else {
					if(k.getRole().equals(Role.SELLER)) {
						for(Ticket ticket : Database.tickets) {
							if(ticket.getStatus().equals(TicketStatus.RESERVED) && Database.isSellersManifestation(k, ticket.getManifestation())) {
								userTickets.add(ticket);
							}
						}
					}
				}
				if(sortBy.equals("title")) {
					Collections.sort(userTickets, Ticket.titleComparatorDSC);
				}else if(sortBy.equals("price")) {
					Collections.sort(userTickets, Ticket.priceComparatorDSC);
				}else {
					Collections.sort(userTickets, Ticket.dateComparatorDSC);
				}
				return g.toJson(userTickets);
			});
			
//			users ***************************************
			get("/users", (req, res) -> {
				
				res.type("application/json");
				return g.toJson(Database.users);
				
			});
//search
			post("/users/searchFirstName", (req, res) -> {
				res.type("application/json");
				User k = req.session().attribute("user");
				
				String data = req.body();
			
				JsonObject job = new JsonParser().parse(data).getAsJsonObject();
				
				String firstName = job.get("firstName").getAsString().toLowerCase();
				ArrayList<User> users = new ArrayList<User>();
				
				for(User user : Database.users) {
					if(user.getFirstName().toLowerCase().equals(firstName)) {
						users.add(user);
					}
				}
				if(users.isEmpty()) {
					return false;
				}else {
					return g.toJson(users);
				}
			
			});

			post("/users/searchLastName", (req, res) -> {
				res.type("application/json");
				User k = req.session().attribute("user");
				
				String data = req.body();
			
				JsonObject job = new JsonParser().parse(data).getAsJsonObject();
				
				String lastName = job.get("lastName").getAsString().toLowerCase();
				System.out.println(lastName);
				ArrayList<User> users = new ArrayList<User>();
				
				for(User user : Database.users) {
					if(user.getLastName().toLowerCase().equals(lastName)) {
						users.add(user);
					}
				}
				if(users.isEmpty()) {
					return false;
				}else {
					return g.toJson(users);
				}
			});

			post("/users/searchUsername", (req, res) -> {
				res.type("application/json");
				User k = req.session().attribute("user");
				
				String data = req.body();
			
				JsonObject job = new JsonParser().parse(data).getAsJsonObject();
				
				String username = job.get("username").getAsString().toLowerCase();
				ArrayList<User> users = new ArrayList<User>();
				
				for(User user : Database.users) {
					if(user.getUsername().toLowerCase().equals(username)) {
						users.add(user);
					}
				}
				if(users.isEmpty()) {
					return false;
				}else {
					return g.toJson(users);
				}
			});
//filter
			post("/users/filterRole", (req, res) -> {
				res.type("application/json");
				User k = req.session().attribute("user");
				
				String data = req.body();
			
				JsonObject job = new JsonParser().parse(data).getAsJsonObject();
				
				String choiceRole = job.get("choiceRole").getAsString().toUpperCase();
				

				Role role;
				if(choiceRole.equals( "ADMIN")) {
					role = Role.ADMIN;
				}else if(choiceRole.equals( "BUYER")) {
					role = Role.BUYER;
				}else {
					role = Role.SELLER;
				}
				
				ArrayList<User> users = new ArrayList<User>();
				
				for(User user : Database.users) {
					if(user.getRole().equals(role)) {
						users.add(user);
					}
				}
				if(users.isEmpty()) {
					return false;
				}else {
					return g.toJson(users);
				}
			});

			post("/users/filterType", (req, res) -> {
				res.type("application/json");
				User k = req.session().attribute("user");
				
				String data = req.body();
			
				JsonObject job = new JsonParser().parse(data).getAsJsonObject();
				
				String choiceType = job.get("choiceType").getAsString();
				System.out.println("choice type" +choiceType );
				ArrayList<User> users = new ArrayList<User>();
				
				for(User user : Database.users) {
					if(user.getBuyerType()!=null && user.getBuyerType().getTypeName().equals(choiceType)) {
						users.add(user);
					}
				}
				if(users.isEmpty()) {
					return false;
				}else {
					return g.toJson(users);
				}
			});
			
			post("/users/sortAsc", (req, res) -> {

				res.type("application/json");
				String data = req.body();
				
				JsonObject job = new JsonParser().parse(data).getAsJsonObject();
				String sortBy = job.get("sortBy").getAsString();
				
				ArrayList<User> users = Database.users;
				
				if(sortBy.equals("firstName")) {
					Collections.sort(users, User.firstNameComparatorASC);
				}else if(sortBy.equals("lastName")) {
					Collections.sort(users, User.lastNameComparatorASC);
				}else if(sortBy.equals("username")) {
					Collections.sort(users, User.usernameComparatorASC);
				}else {
					Collections.sort(users, User.scoreComparatorASC);
				}
				return g.toJson(users);
			});
			
			post("/users/sortDsc", (req, res) -> {

				res.type("application/json");
				String data = req.body();
				
				JsonObject job = new JsonParser().parse(data).getAsJsonObject();
				String sortBy = job.get("sortBy").getAsString();
				
				ArrayList<User> users = Database.users;
				
				if(sortBy.equals("firstName")) {
					Collections.sort(users, User.firstNameComparatorDSC);
				}else if(sortBy.equals("lastName")) {
					Collections.sort(users, User.lastNameComparatorDSC);
				}else if(sortBy.equals("usrename")) {
					Collections.sort(users, User.usernameComparatorDSC);
				}else {
					Collections.sort(users, User.scoreComparatorDSC);
				}
				return g.toJson(users);
			});
			
			post("/manifestations/search", (req, res) -> {
				
				res.type("application/json");
				
				String data = req.body();
				System.out.print(data);
			
				JsonObject job = new JsonParser().parse(data).getAsJsonObject();
				
				String title = job.get("title").getAsString().toLowerCase();
				String location = job.get("location").getAsString().toLowerCase();
				String fromDate = job.get("fromDate").getAsString().toLowerCase();
				String toDate = job.get("toDate").getAsString().toLowerCase();
				int fromPrice = job.get("fromPrice").getAsInt();
				int toPrice = job.get("toPrice").getAsInt();
				
				boolean checkTitle = false;
				boolean checkLocation = false;
				boolean checkFromDate = false;
				boolean checkToDate = false;
				boolean checkFromPrice = false;
				boolean checkToPrice = false;
				
				
				ArrayList<Manifestation> manifestations = new ArrayList<Manifestation>();
				
				for(Manifestation manifestation : Database.manifestations) {
					if((!title.isEmpty() && manifestation.getTitle().toLowerCase().contains(title))) {
						checkTitle = true;
					}else {
						checkTitle = false;
					}
					
					if((!location.isEmpty() && manifestation.getLocation().getCity().toLowerCase().equals(location))) {
						checkLocation = true;
					}else {
						checkLocation = false;
					}
					
					if((!fromDate.isEmpty())){
						Date from = new Date(fromDate);
						if(manifestation.getRealisationDate().after(from)) {
							checkFromDate = true;
						}
						else {
								checkFromDate = false;
						}
					}else {
						checkFromDate = false;
					}
					
					if((!toDate.isEmpty())){
						Date to = new Date(toDate);
						if(manifestation.getRealisationDate().before(to)) {
							checkToDate = true;
						}
						else {
							checkToDate = false;
						}
					}else {
						checkToDate = false;
					}
					
					if((manifestation.getPrice() >= fromPrice)){
						checkFromPrice = true;
					}else {
						checkFromPrice = false;
					}
					
					if((manifestation.getPrice() <= toPrice)){
						checkToPrice = true;
					}else {
						checkToPrice = false;
					}
					
					if((title.equals("") || checkTitle) 
							&& (location.equals("") || checkLocation) 
							&& (fromDate.equals("") || checkFromDate)
							&& (toDate.equals("") || checkToDate)
							&& (fromPrice == 500 || checkFromPrice)
							&& (toPrice == 3000 || checkToPrice)) {
						 System.out.println("Imaa");
						 manifestations.add(manifestation);
					}
					
					System.out.println("\n Baza " +  manifestation.getLocation().getCity() + " uneseno " + location);
					System.out.println("\nLokacija, poredjenje " + manifestation.getLocation().getCity().equals(location) + " ocekivano true");
					System.out.println("\nProvjera praznog polja  " + location.equals("") + " Treba biti false");
					System.out.println("\nLocation check " + checkLocation + " ocekivan true");
					System.out.println("\n Ilii " + (location.equals("") || checkLocation));
				}
				
				if(manifestations.isEmpty()) {
					return false;
				}else {
					return g.toJson(manifestations);
				}
				
				
				
			});
			
	}
}
