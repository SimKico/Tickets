package beans;

import java.util.Date;

public class Ticket {

	private String ticketID;
	private String manifestation;
	private Date manifestationDate;
	private double price;
	private String buyerFirstName;
	private String buyerLastName;
	private TicketStatus status;
	private TicketType type;
	
	public Ticket() {
		super();
	}

	public Ticket(String ticketID, String manifestation, Date manifestationDate, double price, String buyerFirstName,
			String buyerLastName, TicketStatus status, TicketType type) {
		super();
		this.ticketID = ticketID;
		this.manifestation = manifestation;
		this.manifestationDate = manifestationDate;
		this.price = price;
		this.buyerFirstName = buyerFirstName;
		this.buyerLastName = buyerLastName;
		this.status = status;
		this.type = type;
	}

	public String getTicketID() {
		return ticketID;
	}

	public void setTicketID(String ticketID) {
		this.ticketID = ticketID;
	}

	public String getManifestation() {
		return manifestation;
	}

	public void setManifestation(String manifestation) {
		this.manifestation = manifestation;
	}

	public Date getManifestationDate() {
		return manifestationDate;
	}

	public void setManifestationDate(Date manifestationDate) {
		this.manifestationDate = manifestationDate;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getBuyerFirstName() {
		return buyerFirstName;
	}

	public void setBuyerFirstName(String buyerFirstName) {
		this.buyerFirstName = buyerFirstName;
	}

	public String getBuyerLastName() {
		return buyerLastName;
	}

	public void setBuyerLastName(String buyerLastName) {
		this.buyerLastName = buyerLastName;
	}

	public TicketStatus getStatus() {
		return status;
	}

	public void setStatus(TicketStatus status) {
		this.status = status;
	}

	public TicketType getType() {
		return type;
	}

	public void setType(TicketType type) {
		this.type = type;
	}
	
}
