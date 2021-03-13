package beans;

public class Location {
	
	private double lng;
	private double lat;
	private String street;
	private int number;
	private String city;
	private int zipCode;
	
	public Location() {
		super();
	}

	public Location(double lng, double lat, String street, int number, String city, int zipCode) {
		super();
		this.lng = lng;
		this.lat = lat;
		this.street = street;
		this.number = number;
		this.city = city;
		this.zipCode = zipCode;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public int getZipCode() {
		return zipCode;
	}

	public void setZipCode(int zipCode) {
		this.zipCode = zipCode;
	}
	
}
