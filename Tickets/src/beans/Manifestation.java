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
	    
   
}
