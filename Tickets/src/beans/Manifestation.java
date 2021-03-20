package beans;

import java.util.Comparator;
import java.util.Date;

public class Manifestation {
	
	private String title;
	private ManifestationType manifestationType;
	private Date realisationDate;
	private int price;
	private boolean isActive;
	private Location location;
	private String posterPath; //maybe bufferedImage instead String
	private int availableTickets;
	private double averageRating;
	
	public Manifestation() {
		super();
	}

	public Manifestation(String title, ManifestationType manifestationType, Date realisationDate, int price,
			boolean isActive, Location location, String posterPath , int availableTickets, double averageRating) {
		super();
		this.title = title;
		this.manifestationType = manifestationType;
		this.realisationDate = realisationDate;
		this.price = price;
		this.isActive = isActive;
		this.location = location;
		this.posterPath = posterPath;
		this.availableTickets = availableTickets;
		this.averageRating = averageRating;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ManifestationType getManifestationType() {
		return manifestationType;
	}

	public void setManifestationType(ManifestationType manifestationType) {
		this.manifestationType = manifestationType;
	}

	public Date getRealisationDate() {
		return realisationDate;
	}

	public void setRealisationDate(Date realisationDate) {
		this.realisationDate = realisationDate;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getPosterPath() {
		return posterPath;
	}

	public void setPosterPath(String posterPath) {
		this.posterPath = posterPath;
	}
	
	public int getAvailableTickets() {
		return availableTickets;
	}

	public void setAvailableTickets(int availableTickets) {
		this.availableTickets = availableTickets;
	}

	public double getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(double averageRating) {
		this.averageRating = averageRating;
	}


	public static Comparator<Manifestation> dateComparator = new Comparator<Manifestation>() {

		public int compare(Manifestation m1, Manifestation m2) {
		   Date date1 = m1.getRealisationDate();
		   Date date2 = m2.getRealisationDate();
		   
		   if(date1.before(date2))
			   return -1;
		   else
			   return 0;

    }};
	    
    public static Comparator<Manifestation> dateComparatorDesc = new Comparator<Manifestation>() {

		public int compare(Manifestation m1, Manifestation m2) {
		   Date date1 = m1.getRealisationDate();
		   Date date2 = m2.getRealisationDate();
		   
		   if(date1.after(date2))
			   return -1;
		   else
			   return 0;

    }};
    
    public static Comparator<Manifestation> titleComparatorDesc = new Comparator<Manifestation>() {

		public int compare(Manifestation m1, Manifestation m2) {
		   String title1 = m1.getTitle().toLowerCase();
		   String title2 = m2.getTitle().toLowerCase();
		   
		   return title2.compareTo(title1);
    }};
    
    public static Comparator<Manifestation> titleComparatorAsc = new Comparator<Manifestation>() {

    	public int compare(Manifestation m1, Manifestation m2) {
 		   String title1 = m1.getTitle().toLowerCase();
 		   String title2 = m2.getTitle().toLowerCase();
 		   
 		   return title1.compareTo(title2);

    }};
    
    public static Comparator<Manifestation> cityComparatorDesc = new Comparator<Manifestation>() {

		public int compare(Manifestation m1, Manifestation m2) {
		   String city1 = m1.getLocation().getCity().toLowerCase();
		   String city2 = m2.getLocation().getCity().toLowerCase();
		   
		   return city2.compareTo(city1);
    }};
    
    public static Comparator<Manifestation> cityComparatorAsc = new Comparator<Manifestation>() {

    	public int compare(Manifestation m1, Manifestation m2) {
 		   String city1 = m1.getLocation().getCity().toLowerCase();
 		   String city2 = m2.getLocation().getCity().toLowerCase();
 		   
 		   return city1.compareTo(city2);

    }};
    
    public static Comparator<Manifestation> priceComparatorAsc = new Comparator<Manifestation>() {

		public int compare(Manifestation m1, Manifestation m2) {
		   int price1 = m1.getPrice();
		   int price2 = m2.getPrice();
		   
		   return price1-price2;
    }};
    
    public static Comparator<Manifestation> priceComparatorDesc = new Comparator<Manifestation>() {

    	public int compare(Manifestation m1, Manifestation m2) {
 		   int price1 = m1.getPrice();
 		   int price2 = m2.getPrice();
 		   
 		   return price2-price1;

    }};
	    
   
}
