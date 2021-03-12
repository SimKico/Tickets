package beans;

import java.util.ArrayList;
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
	
}
