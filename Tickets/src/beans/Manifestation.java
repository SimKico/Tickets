package beans;

import java.util.Date;

public class Manifestation {
	
	private String title;
	private ManifestationType manifestationType;
	private Date realisationDate;
	private int price;
	private boolean isActive;
	private Location location;
	private String posterPath; //maybe bufferedImage instead String
	
	public Manifestation() {
		super();
	}

	public Manifestation(String title, ManifestationType manifestationType, Date realisationDate, int price,
			boolean isActive, Location location, String posterPath) {
		super();
		this.title = title;
		this.manifestationType = manifestationType;
		this.realisationDate = realisationDate;
		this.price = price;
		this.isActive = isActive;
		this.location = location;
		this.posterPath = posterPath;
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
	
}
