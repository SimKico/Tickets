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
import beans.Role;
import beans.User;

public class Data {

	private static Gson g = new Gson();
	private static FileWriter file;
	
	public static void readingData() throws ParseException {

		File buyerTypes = new File("src/database/buyerTypes.json");
		File users = new File("src/database/users.json");
		
		if(buyerTypes.length()==0) {
			System.out.println("[System]: 'buyerTypes.json' is filled.");
			fillBuyerType();
		}
	
		if(users.length()==0) {
			System.out.println("[System]: 'users.json' is filled.");
			fillUsers();
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
}
