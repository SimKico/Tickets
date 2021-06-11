package rest;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.delete;
import static spark.Spark.staticFiles;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import beans.BuyerType;
import beans.Gender;
import beans.Location;
import beans.Manifestation;
import beans.ManifestationType;
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
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(job.get("birthDay").getAsString());
			
			Gender gender = null;
			if(job.get("gender").getAsString()!= null) {
				 gender = job.get("gender").getAsString() == ("MALE") ? Gender.MALE : Gender.FEMALE;
			}
		
//			BuyerType buyerType = new BuyerType("none",0,0);
			String passwordNew = oldUserData.getPassword();
			if(!job.get("password").getAsString().equals("") && !job.get("password").getAsString().equals(oldUserData.getPassword())) {
				System.out.println("nisu iste"+ job.get("password").getAsString());
				passwordNew = job.get("password").getAsString();
			}
//			User newUserData = new User(job.get("username").getAsString(), passwordOldNew, job.get("firstName").getAsString(), job.get("lastName").getAsString(), gender, date, Role.BUYER, null, null, 0, buyerType);
		
			Session ss = req.session(true);
					
			for(User oneUser : Database.users) {
				if(oneUser.getUsername().equals(oldUsername)) {
					oneUser.setUsername(job.get("username").getAsString());

					oneUser.setFirstName(job.get("firstName").getAsString());

					oneUser.setLastName(job.get("lastName").getAsString());

					oneUser.setGender(gender);

					oneUser.setPassword(passwordNew);
								
					Database.saveUsers();
					return true;
					}
			}					
			
			return false;	
		});
		
		get("/manifestations/all", (req, res) -> {
			
			res.type("application/json");
			List<Manifestation> list = Database.manifestations;
			list.removeIf(s -> !s.isActive());
			Collections.sort(list, Manifestation.dateComparator);
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
				
				ArrayList<User> users = new ArrayList<User>();
				
				for(User user : Database.users) {
					if(!user.isDeleted()) {
						users.add(user);
					}
				}
				if(users.isEmpty()) {
					return false;
				}else {
					return g.toJson(users);
				}
			});
//search
			get("/users/searchFirstName/:firstName", (req, res) -> {
				res.type("application/json");
				User k = req.session().attribute("user");
				
				String firstName = req.params(":firstName").toLowerCase();
				ArrayList<User> users = new ArrayList<User>();
				
				for(User user : Database.users) {
					if(!user.isDeleted() && user.getFirstName().toLowerCase().equals(firstName)) {
						users.add(user);
					}
				}
				if(users.isEmpty()) {
					return false;
				}else {
					return g.toJson(users);
				}
			
			});

			get("/users/searchLastName/:lastName", (req, res) -> {
				res.type("application/json");
				User k = req.session().attribute("user");
				
				String lastName = req.params(":lastName").toLowerCase();
				
				ArrayList<User> users = new ArrayList<User>();
				
				for(User user : Database.users) {
					if(!user.isDeleted() && user.getLastName().toLowerCase().equals(lastName)) {
						users.add(user);
					}
				}
				if(users.isEmpty()) {
					return false;
				}else {
					return g.toJson(users);
				}
			});

			get("/users/searchUsername/:username", (req, res) -> {
				res.type("application/json");
				User k = req.session().attribute("user");
				
				String username = req.params(":username").toLowerCase();
				System.out.println("lastName username" + username);
				ArrayList<User> users = new ArrayList<User>();
				
				for(User user : Database.users) {
					if(!user.isDeleted() && user.getUsername().toLowerCase().equals(username)) {
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
			get("/users/filterRole/:choiceRole", (req, res) -> {
				res.type("application/json");
				User k = req.session().attribute("user");
				
				String choiceRole = req.params(":choiceRole");
				System.out.println("choiceRole" + choiceRole);
				
				Role role;
				if(choiceRole.equalsIgnoreCase("admin")) {
					role = Role.ADMIN;
				}else if(choiceRole.equalsIgnoreCase( "buyer")) {
					role = Role.BUYER;
				}else {
					role = Role.SELLER;
				}
				
				ArrayList<User> users = new ArrayList<User>();
				
				for(User user : Database.users) {
					if(!user.isDeleted() && user.getRole().equals(role)) {
						users.add(user);
					}
				}
				if(users.isEmpty()) {
					return false;
				}else {
					return g.toJson(users);
				}
			});

			get("/users/filterType/:choiceType", (req, res) -> {
				res.type("application/json");
				User k = req.session().attribute("user");
				
				String choiceType = req.params(":choiceType");
				ArrayList<User> users = new ArrayList<User>();
				
				for(User user : Database.users) {
					if(!user.isDeleted() && user.getBuyerType()!=null && user.getBuyerType().getTypeName().equalsIgnoreCase(choiceType)) {
						users.add(user);
					}
				}
				if(users.isEmpty()) {
					return false;
				}else {
					return g.toJson(users);
				}
			});
			
			get("/users/sortAsc/:sortBy", (req, res) -> {

				res.type("application/json");
				String sortBy =  req.params(":sortBy");
				
				ArrayList<User> users = new ArrayList<User>();
				
				for(User user : Database.users) {
					if(!user.isDeleted()) {
						users.add(user);
					}
				}
				
				if(sortBy.equalsIgnoreCase("firstName")) {
					Collections.sort(users, User.firstNameComparatorASC);
				}else if(sortBy.equalsIgnoreCase("lastName")) {
					Collections.sort(users, User.lastNameComparatorASC);
				}else if(sortBy.equalsIgnoreCase("username")) {
					Collections.sort(users, User.usernameComparatorASC);
				}else {
					Collections.sort(users, User.scoreComparatorASC);
				}
				return g.toJson(users);
			});
			
			get("/users/sortDsc/:sortBy", (req, res) -> {

				res.type("application/json");
			
				String sortBy = req.params(":sortBy");
				
				ArrayList<User> users = new ArrayList<User>();
				
				for(User user :  Database.users) {
					if(!user.isDeleted()) {
						users.add(user);
					}
				}
				if(sortBy.equalsIgnoreCase("firstName")) {
					Collections.sort(users, User.firstNameComparatorDSC);
				}else if(sortBy.equalsIgnoreCase("lastName")) {
					Collections.sort(users, User.lastNameComparatorDSC);
				}else if(sortBy.equalsIgnoreCase("usrename")) {
					Collections.sort(users, User.usernameComparatorDSC);
				}else {
					Collections.sort(users, User.scoreComparatorDSC);
				}
				return g.toJson(users);
			});
//delete users
			
			delete("users/:username", (req, res) -> {
				
				String username = req.params(":username").toLowerCase();
				System.out.println("username delete" + username);
				ArrayList<User> users = new ArrayList<User>();
				
				for(User user : Database.users) {
					if(!user.isDeleted()) {
						users.add(user);
					}
				}
				for(User user : users) {
					if(user.getUsername().equals(username)) {
						if(user.isDeleted()) {
							res.status(404);
							return false;
							}else {
								System.out.println("User to be deleted"  + user.getFirstName());
								user.setDeleted(true);
								Database.saveUsers();
								res.status(200);
								return true;
							}
					}
				}
				return false;
			});
//***********************user reserve ticket*****************************//
			
			post("manifestations/bookTicket", (req, res)-> {
				
				res.type("application/json");
				User k = req.session().attribute("user");
				String username = k.getUsername();
				
				String data = req.body();
				JsonObject job = new JsonParser().parse(data).getAsJsonObject();
				
				String title = job.get("title").getAsString().toLowerCase();
				String typeOfTicket = job.get("typeOfTicket").getAsString().toLowerCase();
				
				for(Manifestation manifestation : Database.manifestations) {
					if((!manifestation.isDeleted() && manifestation.getTitle().equals(title))) {
						if(typeOfTicket == "REGULAR") {
							
						}else if(typeOfTicket == "FANPIT") {
							
						}else {
							
						}
					}
				}
				
				return true;
				
			});
			
// **********************manifestations**********************************//
			
			post("/manifestations/search", (req, res) -> {
				
				res.type("application/json");
				
				String data = req.body();
				System.out.print(data);
			
				JsonObject job = new JsonParser().parse(data).getAsJsonObject();
				System.out.print(data);
				String title = job.get("title").getAsString().toLowerCase();
				String location = job.get("location").getAsString().toLowerCase();
				String fromDate = job.get("fromDate").getAsString().toLowerCase();
				String toDate = job.get("toDate").getAsString().toLowerCase();
				int fromPrice = job.get("fromPrice").getAsInt();
				int toPrice = job.get("toPrice").getAsInt();
				System.out.print(data);
				boolean checkTitle = false;
				boolean checkLocation = false;
				boolean checkFromDate = false;
				boolean checkToDate = false;
				boolean checkFromPrice = false;
				boolean checkToPrice = false;
				System.out.print(data);
				
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
							&& (fromPrice >= 500 || checkFromPrice)
							&& (toPrice <= 3000 || checkToPrice)) {
						 System.out.println("Imaa");
						 manifestations.add(manifestation);
					}
					
				}
				
				if(manifestations.isEmpty()) {
					return null;
				}else {
					return g.toJson(manifestations);
				}
				
				
				
			});
			
			post("/manifestations/sortAsc", (req, res)->{
				
				res.type("application/json");
				String data = req.body();
				
				JsonObject job = new JsonParser().parse(data).getAsJsonObject();
				JsonArray manifestationsJson = job.get("manifestations").getAsJsonArray();
				String criteria = job.get("criteria").getAsString().toLowerCase();
				
				ArrayList<Manifestation> manifestations = new ArrayList<Manifestation>();
				
				for(int i=0; i<manifestationsJson.size(); i++) {
					
				   Location l = new Location();
				   l.setCity(manifestationsJson.get(i).getAsJsonObject().get("location").getAsJsonObject().get("city").getAsString());
				   l.setNumber(manifestationsJson.get(i).getAsJsonObject().get("location").getAsJsonObject().get("number").getAsString());
				   l.setStreet(manifestationsJson.get(i).getAsJsonObject().get("location").getAsJsonObject().get("street").getAsString());
				   l.setZipCode(manifestationsJson.get(i).getAsJsonObject().get("location").getAsJsonObject().get("zipCode").getAsInt());
				   l.setLat(manifestationsJson.get(i).getAsJsonObject().get("location").getAsJsonObject().get("lat").getAsDouble());
				   l.setLng(manifestationsJson.get(i).getAsJsonObject().get("location").getAsJsonObject().get("lng").getAsDouble());
				
			       Manifestation m = new Manifestation();
			       m.setLocation(l);
			       m.setActive(manifestationsJson.get(i).getAsJsonObject().get("isActive").getAsBoolean());
			       m.setPrice(manifestationsJson.get(i).getAsJsonObject().get("price").getAsInt());
			       m.setTitle(manifestationsJson.get(i).getAsJsonObject().get("title").getAsString());
			       m.setManifestationType(ManifestationType.valueOf(manifestationsJson.get(i).getAsJsonObject().get("manifestationType").getAsString()));
			       m.setRealisationDate(new Date(manifestationsJson.get(i).getAsJsonObject().get("realisationDate").getAsString()));
			       m.setPosterPath(manifestationsJson.get(i).getAsJsonObject().get("posterPath").getAsString());
			       m.setAvailableTickets(manifestationsJson.get(i).getAsJsonObject().get("availableTickets").getAsInt());
			       m.setAverageRating(manifestationsJson.get(i).getAsJsonObject().get("averageRating").getAsDouble());
			       manifestations.add(m);
			    }
			    
				if(criteria.equals("title")) {
					Collections.sort(manifestations, Manifestation.titleComparatorAsc);
				}else if(criteria.equals("city")) {
					Collections.sort(manifestations, Manifestation.cityComparatorAsc);
				}else if(criteria.equals("price")) {
					Collections.sort(manifestations, Manifestation.priceComparatorAsc);
				}else {
					Collections.sort(manifestations, Manifestation.dateComparator);
				}
				
				
				return g.toJson(manifestations);
				
			});
			
			post("/manifestations/sortDesc", (req, res)->{
				
				res.type("application/json");
				String data = req.body();
				
				JsonObject job = new JsonParser().parse(data).getAsJsonObject();
				JsonArray manifestationsJson = job.get("manifestations").getAsJsonArray();
				String criteria = job.get("criteria").getAsString().toLowerCase();
				
				ArrayList<Manifestation> manifestations = new ArrayList<Manifestation>();
				
				for(int i=0; i<manifestationsJson.size(); i++) {
					
				   Location l = new Location();
				   l.setCity(manifestationsJson.get(i).getAsJsonObject().get("location").getAsJsonObject().get("city").getAsString());
				   l.setNumber(manifestationsJson.get(i).getAsJsonObject().get("location").getAsJsonObject().get("number").getAsString());
				   l.setStreet(manifestationsJson.get(i).getAsJsonObject().get("location").getAsJsonObject().get("street").getAsString());
				   l.setZipCode(manifestationsJson.get(i).getAsJsonObject().get("location").getAsJsonObject().get("zipCode").getAsInt());
				   l.setLat(manifestationsJson.get(i).getAsJsonObject().get("location").getAsJsonObject().get("lat").getAsDouble());
				   l.setLng(manifestationsJson.get(i).getAsJsonObject().get("location").getAsJsonObject().get("lng").getAsDouble());
				
			       Manifestation m = new Manifestation();
			       m.setLocation(l);
			       m.setActive(manifestationsJson.get(i).getAsJsonObject().get("isActive").getAsBoolean());
			       m.setPrice(manifestationsJson.get(i).getAsJsonObject().get("price").getAsInt());
			       m.setTitle(manifestationsJson.get(i).getAsJsonObject().get("title").getAsString());
			       m.setManifestationType(ManifestationType.valueOf(manifestationsJson.get(i).getAsJsonObject().get("manifestationType").getAsString()));
			       m.setRealisationDate(new Date(manifestationsJson.get(i).getAsJsonObject().get("realisationDate").getAsString()));
			       m.setPosterPath(manifestationsJson.get(i).getAsJsonObject().get("posterPath").getAsString());
			       m.setAvailableTickets(manifestationsJson.get(i).getAsJsonObject().get("availableTickets").getAsInt());
			       m.setAverageRating(manifestationsJson.get(i).getAsJsonObject().get("averageRating").getAsDouble());
			       manifestations.add(m);
			    }
			    
				if(criteria.equals("title")) {
					Collections.sort(manifestations, Manifestation.titleComparatorDesc);
				}else if(criteria.equals("city")) {
					Collections.sort(manifestations, Manifestation.cityComparatorDesc);
				}else if(criteria.equals("price")) {
					Collections.sort(manifestations, Manifestation.priceComparatorDesc);
				}else {
					Collections.sort(manifestations, Manifestation.dateComparatorDesc);
				}
				
				
				return g.toJson(manifestations);
				
			});
			
			post("/manifestations/filter", (req, res)->{
				
				res.type("application/json");
				String data = req.body();
				
				JsonObject job = new JsonParser().parse(data).getAsJsonObject();
				JsonArray manifestationsJson = job.get("manifestations").getAsJsonArray();
				String type = job.get("type").getAsString();
				boolean available = job.get("available").getAsBoolean();
				
				ArrayList<Manifestation> manifestations = new ArrayList<Manifestation>();
				
				for(int i=0; i<manifestationsJson.size(); i++) {
					
				   Location l = new Location();
				   l.setCity(manifestationsJson.get(i).getAsJsonObject().get("location").getAsJsonObject().get("city").getAsString());
				   l.setNumber(manifestationsJson.get(i).getAsJsonObject().get("location").getAsJsonObject().get("number").getAsString());
				   l.setStreet(manifestationsJson.get(i).getAsJsonObject().get("location").getAsJsonObject().get("street").getAsString());
				   l.setZipCode(manifestationsJson.get(i).getAsJsonObject().get("location").getAsJsonObject().get("zipCode").getAsInt());
				   l.setLat(manifestationsJson.get(i).getAsJsonObject().get("location").getAsJsonObject().get("lat").getAsDouble());
				   l.setLng(manifestationsJson.get(i).getAsJsonObject().get("location").getAsJsonObject().get("lng").getAsDouble());
				
			       Manifestation m = new Manifestation();
			       m.setLocation(l);
			       m.setActive(manifestationsJson.get(i).getAsJsonObject().get("isActive").getAsBoolean());
			       m.setPrice(manifestationsJson.get(i).getAsJsonObject().get("price").getAsInt());
			       m.setTitle(manifestationsJson.get(i).getAsJsonObject().get("title").getAsString());
			       m.setManifestationType(ManifestationType.valueOf(manifestationsJson.get(i).getAsJsonObject().get("manifestationType").getAsString()));
			       m.setRealisationDate(new Date(manifestationsJson.get(i).getAsJsonObject().get("realisationDate").getAsString()));
			       m.setPosterPath(manifestationsJson.get(i).getAsJsonObject().get("posterPath").getAsString());
			       m.setAvailableTickets(manifestationsJson.get(i).getAsJsonObject().get("availableTickets").getAsInt());
			       m.setAverageRating(manifestationsJson.get(i).getAsJsonObject().get("averageRating").getAsDouble());
			       manifestations.add(m);
			    }
			    
				if(available && !type.equals("none")) {
					manifestations.removeIf(p -> p.getManifestationType() != ManifestationType.valueOf(type) || p.getAvailableTickets() == 0);
				}else if(!available && !type.equals("none")) {
					manifestations.removeIf(p -> p.getManifestationType() != ManifestationType.valueOf(type));
				}else if(available && type.equals("none")) {
					manifestations.removeIf(p -> p.getAvailableTickets() == 0);
				}
				
				
				return g.toJson(manifestations);
				
			});
			
			get("/manifestations/:username", (req, res) -> {
				
				res.type("application/json");
				String username = req.params(":username");
				User user = new User();
				for(User u : Database.users) {
					if(u.getUsername().equalsIgnoreCase(username)) {
						user = u;
						break;
					}
				}
				return g.toJson(user.getManifestations());
						
			});
			
			
	}
}
