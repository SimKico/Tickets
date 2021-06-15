package rest;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.staticFiles;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
					return g.toJson(user);
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
		
		get("/manifestations", (req, res) -> {
			
			res.type("application/json");
			String active = req.queryParams("active"); 
			System.out.println(active);
			
			List<Manifestation> list = new ArrayList<Manifestation>();
			list.addAll(Database.manifestations);
			System.out.println(list);
			
			if(active.equals("true")) {
				
				list.removeIf(s -> s.isDeleted());
				System.out.println(list);
			}else if(active.equals("false")) {
				list.removeIf(s -> s.isActive() || s.isDeleted());
				System.out.println(list);
			}
			
			
			Collections.sort(list, Manifestation.dateComparator);
			return g.toJson(list);
					
		});
		
		get("/userTickets", (req, res) -> {
			
			res.type("application/json");
			User k = req.session().attribute("user");
			
			System.out.println(k);
			System.out.println(k.getLastName());
			
			ArrayList<Ticket> userTickets = new ArrayList<Ticket>();
			if(k.getRole().equals(Role.BUYER)) {
				for(Ticket ticket : Database.tickets) {
					if(ticket.getStatus().equals(TicketStatus.RESERVED) && ticket.getBuyerFirstName().equals(k.getFirstName()) && ticket.getBuyerLastName().equals(k.getLastName())) {
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
						if(ticket.getStatus().equals(TicketStatus.RESERVED) && ticket.getManifestation().toLowerCase().equals(manifestation) && ticket.getBuyerFirstName().equals(k.getFirstName()) && ticket.getBuyerLastName().equals(k.getLastName())) {
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
						if(ticket.getStatus().equals(TicketStatus.RESERVED) && ticket.getBuyerFirstName().equals(k.getFirstName()) && ticket.getBuyerLastName().equals(k.getLastName()) && ticket.getPrice() >= minPrice && ticket.getPrice() <= maxPrice ) {
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
						if(ticket.getStatus().equals(TicketStatus.RESERVED) &&  ticket.getBuyerFirstName().equals(k.getFirstName()) && ticket.getBuyerLastName().equals(k.getLastName()) && ticket.getManifestationDate().after(fromDate) && ticket.getManifestationDate().before(toDate) ) {
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
						if(ticket.getStatus().equals(TicketStatus.RESERVED) && ticket.getBuyerFirstName().equals(k.getFirstName()) && ticket.getBuyerLastName().equals(k.getLastName()) && ticket.getType().equals(ticketType)) {
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
					ticketStatus = TicketStatus.CANCELED;
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
						if(ticket.getStatus().equals(TicketStatus.RESERVED) && ticket.getBuyerFirstName().equals(k.getFirstName()) && ticket.getBuyerLastName().equals(k.getLastName())) {
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
						if(ticket.getStatus().equals(TicketStatus.RESERVED) && ticket.getBuyerFirstName().equals(k.getFirstName()) && ticket.getBuyerLastName().equals(k.getLastName())) {
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
			


			put("/tickets/cancel", (req, res) ->{
						res.type("application/json");
						User k = req.session().attribute("user");
						String data = req.body();
						JsonObject job = new JsonParser().parse(data).getAsJsonObject();
						String ticketID = job.get("ticketID").getAsString();

						System.out.println(ticketID);
						for(Ticket t : Database.tickets) {
							if(t.getTicketID().equalsIgnoreCase(ticketID)) {

								System.out.println(t.getManifestation());
								 Calendar cal = Calendar.getInstance();
							      cal.setTime(new Date());
							      cal.add(Calendar.DATE, 7); //minus number would decrement the days
							      Date date = cal.getTime();

							      if(t.getManifestationDate().after(date) && t.getManifestationDate().after(new Date())) {
										t.setStatus(TicketStatus.CANCELED);
										Database.saveTickets();
										
										k.setPoints(k.getPoints() - ((t.getPrice()/1000)*532));
										Database.saveUsers();
										
										if(4000 <= k.getPoints()) {
											k.setBuyerType(new BuyerType("GOLDEN", 30, 0));
										}else if(3000 <= k.getPoints()) {
											k.setBuyerType(new BuyerType("SILVER", 20, 4000 - k.getPoints()));
										}else {
											k.setBuyerType(new BuyerType("BRONZE", 10, 3000 - k.getPoints()));
										}
										Database.saveUsers();
										
										return true;
							      }else {
							    	  	return false;
							      }
							}
						}
						return true;
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
				User user = req.session().attribute("user");

				String data = req.body();
			
				JsonArray job = new JsonParser().parse(data).getAsJsonArray();
				
				for(JsonElement r : job) {
					JsonObject dd = r.getAsJsonObject();
					String title = dd.get("title").getAsString().toLowerCase();
					String typeOfTicket = dd.get("typeOfTicket").getAsString().toLowerCase();
					String priceString = dd.get("price").getAsString().toLowerCase();
					Integer price = Integer.valueOf(priceString);
					for(Manifestation manifestation : Database.manifestations) {
					
						
					if((!manifestation.isDeleted() && manifestation.getTitle().equalsIgnoreCase(title))) {
						
						if(typeOfTicket.equalsIgnoreCase("REGULAR")) {
							
							if(manifestation.getAvailableRegularTickets() !=0) {
							
								String id =String.valueOf("t" + Database.tickets.size() + 1);
								Ticket newTicket = new Ticket(id, title , manifestation.getRealisationDate(), manifestation.getPrice(),user.getUsername(), user.getFirstName(), user.getLastName(), TicketStatus.RESERVED, TicketType.REGULAR, false);
								//add ticket to tickets
								
//								ArrayList<Ticket> tickets;
//								if(user.getTickets().isEmpty()) {
//									tickets = new ArrayList<Ticket>();
//								}else {
//									tickets = user.getTickets();
//								}
//								tickets.add(newTicket);
//								user.setTickets(tickets);
								user.setPoints(user.getPoints() + (price/1000)*3);
								if(4000 <= user.getPoints() + (price/1000)*3) {
									user.setBuyerType(new BuyerType("GOLDEN", 30, 0));
								}else if(3000 <= user.getPoints() + (price/1000)*3) {
									user.setBuyerType(new BuyerType("SILVER", 20, 4000 - user.getPoints() + (price/1000)*3));
								}else {
									user.setBuyerType(new BuyerType("BRONZE", 10, 3000 - user.getPoints() + (price/1000)*3));
								}
								Database.saveUsers();
								
								Database.addTicket(newTicket);
								Database.saveTickets();
								
								//reduce number of available tickets
								manifestation.setAvailableRegularTickets(manifestation.getAvailableRegularTickets()-1);
								Database.saveManifestations();
								System.out.println("SAVED" + title);
							}else {
								return false;
							}
						}else if(typeOfTicket.equalsIgnoreCase("FANPIT")) {
							if(manifestation.getAvailableFanpitTickets() !=0) {
								System.out.println(manifestation.getAvailableFanpitTickets());
								String id =String.valueOf("t" + Database.tickets.size() + 1);
								Ticket newTicket = new Ticket(id, title , manifestation.getRealisationDate(), manifestation.getPrice()*2,user.getUsername(), user.getFirstName(), user.getLastName(), TicketStatus.RESERVED, TicketType.FANPIT, false);
								//add ticket to tickets
								Database.addTicket(newTicket);
								Database.saveTickets();
								
								//reduce number of available tickets
								manifestation.setAvailableFanpitTickets(manifestation.getAvailableFanpitTickets()-1);
								Database.saveManifestations();

								System.out.println("SAVED" + title);
							}else {
								return false;
							}
						}else {
							if(manifestation.getAvailableVipTickets() !=0) {
								
								String id =String.valueOf("t" + Database.tickets.size() + 1);
								Ticket newTicket = new Ticket(id, title , manifestation.getRealisationDate(), manifestation.getPrice() * 4,user.getUsername(), user.getFirstName(), user.getLastName(), TicketStatus.RESERVED, TicketType.VIP, false);
								//add ticket to tickets
								Database.addTicket(newTicket);
								Database.saveTickets();
								
								//reduce number of available tickets
								manifestation.setAvailableVipTickets(manifestation.getAvailableVipTickets()-1);
								manifestation.setAvailableTickets(manifestation.getAvailableTickets()-1);
								Database.saveManifestations();

								System.out.println("SAVED" + title);
							}else {
								return false;
							}
						}
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
				ArrayList<Manifestation> nonDeletedManifestations = Database.manifestations;
				nonDeletedManifestations.removeIf(m -> m.isDeleted());
				for(Manifestation manifestation : nonDeletedManifestations) {
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
			
			get("/manifestations/user/:username", (req, res) -> {
				
				res.type("application/json");
				String username = req.params(":username");
				User user = new User();
				List<Manifestation> usersMan = new ArrayList<Manifestation>();
				for(User u : Database.users) {
					if(u.getUsername().equalsIgnoreCase(username)) {
						usersMan.addAll(u.getManifestations());
						break;
					}
				}
				usersMan.removeIf(s -> s.isDeleted());
				return g.toJson(usersMan);
						
			});
			
			post("/manifestations", (req, res) -> {
				
				res.type("application/json");
				String data = req.body();
				
				JsonObject job = new JsonParser().parse(data).getAsJsonObject();		
			
			    Location l = new Location();
			    l.setCity(job.get("city").getAsString());
			    l.setNumber(job.get("number").getAsString());
			    l.setStreet(job.get("street").getAsString());
			    l.setZipCode(job.get("zipCode").getAsInt());
			    l.setLat(job.get("lat").getAsDouble());
			    l.setLng(job.get("lon").getAsDouble());
			
		        Manifestation m = new Manifestation();
		        m.setLocation(l);
		        m.setActive(false);
		        m.setDeleted(false);
		        m.setPrice(job.get("price").getAsInt());
		        m.setTitle(job.get("title").getAsString());
		       
		        m.setManifestationType(ManifestationType.valueOf(job.get("type").getAsString()));
		        Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm").parse(job.get("date").getAsString() + " " + job.get("time").getAsString());  
		        m.setRealisationDate(date);
		       
		        m.setAvailableTickets(job.get("seats").getAsInt());
		        double seats =  m.getAvailableTickets()*0.7;
		        m.setAvailableRegularTickets((int)seats);
		        seats =  m.getAvailableTickets()*0.2;
		        m.setAvailableFanpitTickets((int)seats);
		        seats =  m.getAvailableTickets()*0.1;
			    m.setAvailableVipTickets((int)seats); 
			   
			    System.out.println(job.get("poster"));
			   
			    String imageData = job.get("poster").getAsString();
			    String base64Data = imageData.split(",")[1];

			    byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
			    ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes);
			    BufferedImage image = ImageIO.read(bis);

			    File outputFile = new File("static/source",m.getTitle()+".jpg").getAbsoluteFile();
			    ImageIO.write(image, "jpg", outputFile); 
			    m.setPosterPath("source/"+m.getTitle()+".jpg");
			    
			    if(m.getRealisationDate().before(new Date())) {
		    		res.status(400);
		    		return new Gson().toJson(Collections.singletonMap("message", "Your request could not be processed. Date chosen already passed!")); 

		    	}
			    
			    for(Manifestation existingMan : Database.manifestations) {
			    	if((existingMan.getLocation().getLat() == m.getLocation().getLat() && existingMan.getLocation().getLng()== m.getLocation().getLng()) && existingMan.getRealisationDate().getTime() == m.getRealisationDate().getTime()) {
			    		res.status(400);
			    		return new Gson().toJson(Collections.singletonMap("message", "Your request could not be processed. A manifestation on that date and time on given location already exists."));
			    	}else if(existingMan.getTitle().equals(m.getTitle())) {
			    		res.status(400);
			    		return new Gson().toJson(Collections.singletonMap("message", "Your request could not be processed. A manifestation with that name already exists!")); 
	
			    	}
			    }
			   
			    
			    Database.addManifestation(m);
				
			    User k = req.session().attribute("user");
			    System.out.println("username " + k.getUsername());
			    Database.users.get(Database.users.indexOf(k)).getManifestations().add(m);
			    Database.saveUsers();
			    
				return g.toJson(Database.manifestations.get(Database.manifestations.indexOf(m)));
						
			});
			
			delete("/manifestations/:title", (req, res) -> {
				
				res.type("application/json");
				String title = req.params(":title");
				
				
				for(Manifestation m : Database.manifestations) {
					if(m.getTitle().equalsIgnoreCase(title)) {
						Database.manifestations.get(Database.manifestations.indexOf(m)).setDeleted(true);
						break;
					}
				}
				
				for(Ticket t : Database.tickets) {
					if(t.getManifestation().equalsIgnoreCase(title)) {
						Database.tickets.get(Database.tickets.indexOf(t)).setDeleted(true);
					}
				}
				
				for(User u : Database.users) {
					if(u.getManifestations() != null) {
						for(Manifestation m : u.getManifestations()) {
							if(m.getTitle().equalsIgnoreCase(title)) {
								u.getManifestations().get(u.getManifestations().indexOf(m)).setDeleted(true);
								break;
							}
						}
					}
				}
				
				Database.saveManifestations();
				Database.saveTickets();
			    Database.saveUsers();
			    
				res.status(200);
				return true;
						
			});
			
			put("/manifestations/approve/:title", (req, res) -> {
				
				res.type("application/json");
				String title = req.params(":title");
				
				for(User u : Database.users) {
					if(u.getManifestations() != null) {
						for(Manifestation m : u.getManifestations()) {
							if(m.getTitle().equalsIgnoreCase(title)) {
								u.getManifestations().get(u.getManifestations().indexOf(m)).setActive(true);
								Database.saveUsers();
								break;
							}
						}
					}
				}
				
				for(Manifestation m : Database.manifestations) {
					if(m.getTitle().equalsIgnoreCase(title)) {
						System.out.println("BRACOOO " + Database.manifestations.get(Database.manifestations.indexOf(m)).isActive());
						Database.manifestations.get(Database.manifestations.indexOf(m)).setActive(true);
						Database.saveManifestations();
						System.out.println("I SESTREEE " + Database.manifestations.get(Database.manifestations.indexOf(m)).isActive());
						res.status(200);
						return true;
					
					}
				}
								
				res.status(404);
				return false;
						
			});
			
			get("/manifestations/:title", (req, res) -> {
				System.out.println("WHAT THEEE FUUUCKKKADJNCFKAC");
				res.type("application/json");
				String title = req.params(":title");
				System.out.println("WHAT THEEE FUUUCKKKADJNCFKAC");
				Manifestation returnVal = new Manifestation();
				System.out.println("WHAT THEEE FUUUCKKKADJNCFKAC");
				for(Manifestation m : Database.manifestations) {
					if(m.getTitle().equalsIgnoreCase(title)) {
						
						returnVal = Database.manifestations.get(Database.manifestations.indexOf(m));
					
					}
				}
				System.out.println("WHAT THEEE FUUUCKKKADJNCFKAC " + returnVal.getTitle());
				
				return g.toJson(returnVal);
						
			});
			

			
	}
}
