package database;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import beans.BuyerType;
import beans.Gender;
import beans.Location;
import beans.Manifestation;
import beans.ManifestationType;
import beans.Role;
import beans.User;

public class Data {

	private static Gson g = new Gson();
	private static FileWriter file;
	
	public static void readingData() throws ParseException {

		File buyerTypes = new File("src/database/buyerTypes.json");
		File users = new File("src/database/users.json");
		File manifestations = new File("src/database/manifestations.json");
		File locations = new File("src/database/locations.json");
		
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
		
		users.add(new User("admin1", "123", "Petra", "Peric", Gender.FEMALE, date1, Role.ADMIN,null,null,0,null));

		users.add(new User("admin2", "123", "Nikola", "Nikolic", Gender.MALE, date2, Role.ADMIN,null,null,0,null));

		users.add(new User("seller1", "123", "Petar", "Petrovic", Gender.MALE, date3, Role.SELLER ,null,null,0,null));

		users.add(new User("buyer1", "123", "Natasa", "Kovacevic", Gender.FEMALE, date4, Role.BUYER,null,null,4500, gold));

		users.add(new User("buyer2", "123", "Snjezana", "Simic", Gender.FEMALE, date5, Role.BUYER, null, null, 3500, silver));
		
		users.add(new User("buyer3", "123", "Goran", "Stanic", Gender.MALE, date6, Role.BUYER, null, null, 2500, bronze));
		
		
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
		
		locations.add(new Location(20.46, 44.817, "Francuska", 3, "Beograd", 11000));
		locations.add(new Location(19.843, 45.255, "Pozorišni trg", 1, "Novi Sad", 21000));
		locations.add(new Location(19.845, 45.247, "Sutjeska", 2, "Novi Sad", 21000));
		locations.add(new Location(20.421, 44.814, "Bulevar Arsenija Čarnojevića", 58, "Beograd", 11000));
		locations.add(new Location(19.863, 45.252, "Beogradska", 0, "Petrovaradin", 21131));
		locations.add(new Location(19.845, 45.255, "Trg Slobode", 0, "Novi Sad", 21000));
		
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
		
		String sDate1="13.12.2020";
		String sDate2="18.04.2021";
		String sDate3="15.10.2021";
		
		Date date1 = new SimpleDateFormat("dd.MM.yyyy").parse(sDate1);  
		Date date2 = new SimpleDateFormat("dd.MM.yyyy").parse(sDate2);  
		Date date3 = new SimpleDateFormat("dd.MM.yyyy").parse(sDate3);  
		
		Location location1 = new Location(20.46, 44.817, "Francuska", 3, "Beograd", 11000);
		Location location2 = new Location(19.843, 45.255, "Pozorišni trg", 1, "Novi Sad", 21000);
		Location location3 = new Location(19.845, 45.247, "Sutjeska", 2, "Novi Sad", 21000);
		Location location4 = new Location(20.421, 44.814, "Bulevar Arsenija Čarnojevića", 58, "Beograd", 11000);
		Location location5 = new Location(19.863, 45.252, "Beogradska", 0, "Petrovaradin", 21131);
		Location location6 = new Location(19.845, 45.255, "Trg Slobode", 0, "Novi Sad", 21000);
		
		manifestations.add(new Manifestation("Koncert1", ManifestationType.CONCERTS, date1, 1500, true, location4, "static/source/cloud-desktop-background.jpg"));
		manifestations.add(new Manifestation("Festival1", ManifestationType.FESTIVALS, date2, 1200, true, location5, "static/source/cloud-desktop-background.jpg"));
		manifestations.add(new Manifestation("Pozoriste1", ManifestationType.THEATERS, date3, 789, true, location1, "static/source/cloud-desktop-background.jpg"));
		manifestations.add(new Manifestation("Koncert2", ManifestationType.CONCERTS, date3, 1500, false, location3, "static/source/cloud-desktop-background.jpg"));
		manifestations.add(new Manifestation("Festival2", ManifestationType.FESTIVALS, date2, 1200, false, location6, "static/source/cloud-desktop-background.jpg"));
		manifestations.add(new Manifestation("Pozoriste2", ManifestationType.THEATERS, date1, 789, true, location2, "static/source/cloud-desktop-background.jpg"));
		
		String json = new Gson().toJson(manifestations);
		
		try {
			file = new FileWriter(new File("src/database/manifestations.json"));
			file.write(json);
			file.close();
	
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
