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
		
		get("/allManifestations", (req, res) -> {
			
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
				System.out.println("nesto");
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
					System.out.println("sort by title");
					Collections.sort(userTickets, Ticket.titleComparatorASC);
				}else if(sortBy.equals("price")) {
					System.out.println("sort by price");
					Collections.sort(userTickets, Ticket.priceComparatorASC);
				}else {
					System.out.println("sort by date");
					Collections.sort(userTickets, Ticket.dateComparatorASC);
				}
				return g.toJson(userTickets);
			});
			
			post("/tickets/sortDsc", (req, res) -> {
				System.out.println("nesto");
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
					System.out.println("sort by title");
					Collections.sort(userTickets, Ticket.titleComparatorDSC);
				}else if(sortBy.equals("price")) {
					System.out.println("sort by price");
					Collections.sort(userTickets, Ticket.priceComparatorDSC);
				}else {
					System.out.println("sort by date");
					Collections.sort(userTickets, Ticket.dateComparatorDSC);
				}
				return g.toJson(userTickets);
			});
	}
}
