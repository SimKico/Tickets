package beans;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class User {
	
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private Gender gender;
	private Date birthDay;
	private Role role;
	private ArrayList<Ticket> tickets;
	private ArrayList<Manifestation> manifestations;
	private int points;
	private BuyerType buyerType;
	
	public User() {
		super();
	}

	public User(String username, String password, String firstName, String lastName, Gender gender, Date birthDay,
			Role role, ArrayList<Ticket> tickets, ArrayList<Manifestation> manifestations, int points,
			BuyerType buyerType) {
		super();
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.birthDay = birthDay;
		this.role = role;
		this.tickets = tickets;
		this.manifestations = manifestations;
		this.points = points;
		this.buyerType = buyerType;
		System.out.println(this.birthDay);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Date getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(Date birthDay) {
		this.birthDay = birthDay;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public ArrayList<Ticket> getTickets() {
		return tickets;
	}

	public void setTickets(ArrayList<Ticket> tickets) {
		this.tickets = tickets;
	}

	public ArrayList<Manifestation> getManifestations() {
		return manifestations;
	}

	public void setManifestations(ArrayList<Manifestation> manifestations) {
		this.manifestations = manifestations;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public BuyerType getBuyerType() {
		return buyerType;
	}

	public void setBuyerType(BuyerType buyerType) {
		this.buyerType = buyerType;
	}
	
	public static Comparator<User> firstNameComparatorASC = new Comparator<User>() {

		public int compare(User m1, User m2) {
		  String firstName1 = m1.getFirstName().toLowerCase();
		  String firstName2 = m2.getFirstName().toLowerCase();
				   
		  return firstName1.compareTo(firstName2);
		}};

			
	public static Comparator<User> firstNameComparatorDSC = new Comparator<User>() {

		public int compare(User m1, User m2) {
		  String firstName1 = m1.getFirstName().toLowerCase();
		  String firstName2 = m2.getFirstName().toLowerCase();
				   
			   return firstName2.compareTo(firstName1);
	}};

	public static Comparator<User> lastNameComparatorASC = new Comparator<User>() {

		public int compare(User m1, User m2) {
		  String lastName1 = m1.getLastName().toLowerCase();
		  String lastName2 = m2.getLastName().toLowerCase();
				   
		  return lastName1.compareTo(lastName2);
		}};

			
	public static Comparator<User> lastNameComparatorDSC = new Comparator<User>() {

		public int compare(User m1, User m2) {
		  String lastName1 = m1.getFirstName().toLowerCase();
		  String lastName2 = m2.getFirstName().toLowerCase();
				   
			   return lastName2.compareTo(lastName1);
	}};

	public static Comparator<User> usernameComparatorASC = new Comparator<User>() {

		public int compare(User m1, User m2) {
		  String username1 = m1.getUsername().toLowerCase();
		  String username2 = m2.getUsername().toLowerCase();
				   
		  return username1.compareTo(username2);
		}};

			
	public static Comparator<User> usernameComparatorDSC = new Comparator<User>() {

		public int compare(User m1, User m2) {
		  String username1 = m1.getUsername().toLowerCase();
		  String username2 = m2.getUsername().toLowerCase();
				   
			   return username2.compareTo(username1);
	}};

	public static Comparator<User> scoreComparatorASC = new Comparator<User>() {
		
		public int compare(User m1, User m2) {
			int score1 = m1.getPoints();		
			int score2 = m2.getPoints();
			
		/*For ascending order*/
		  return score1-score2;
		}};

	public static Comparator<User> scoreComparatorDSC = new Comparator<User>() {
			
		public int compare(User m1, User m2) {
			int score1 = m1.getPoints();		
			int score2 = m2.getPoints();
			
		/*For ascending order*/
		  return score2-score1;
		}};

}
