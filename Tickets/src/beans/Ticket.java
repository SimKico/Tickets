package beans;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class Ticket{

	private String ticketID;
	private String manifestation;
	private Date manifestationDate;
	private int price;
	private String buyerFirstName;
	private String buyerLastName;
	private TicketStatus status;
	private TicketType type;
	
	
	public Ticket() {
		super();
	}

	public Ticket(String ticketID, String manifestation, Date manifestationDate, int price, String buyerFirstName,
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

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
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
	
	public static Comparator<Ticket> dateComparatorDSC = new Comparator<Ticket>() {

		public int compare(Ticket m1, Ticket m2) {
		   Date date1 = m1.getManifestationDate();
		   Date date2 = m2.getManifestationDate();
		   
		   if(date1.before(date2))
			   return -1;
		   else
			   return 0;

	    }};
	    
	public static Comparator<Ticket> dateComparatorASC = new Comparator<Ticket>() {

		public int compare(Ticket m1, Ticket m2) {
		   Date date1 = m1.getManifestationDate();
		   Date date2 = m2.getManifestationDate();	
		   
		   if(date2.before(date1))
			   return -1;
		   else
			   return 0;
		}};
	    
	public static Comparator<Ticket> titleComparatorASC = new Comparator<Ticket>() {

			public int compare(Ticket m1, Ticket m2) {
			   String title1 = m1.getManifestation().toLowerCase();
			   String title2 = m2.getManifestation().toLowerCase();
			   
			   //ascending order
			   return title1.compareTo(title2);
			}};
			
	public static Comparator<Ticket> titleComparatorDSC = new Comparator<Ticket>() {

			public int compare(Ticket m1, Ticket m2) {
		  String title1 = m1.getManifestation().toLowerCase();
		  String title2 = m2.getManifestation().toLowerCase();
				   
				   return title2.compareTo(title1);
				}};
			
	public static Comparator<Ticket> priceComparatorASC = new Comparator<Ticket>() {

			public int compare(Ticket m1, Ticket m2) {
				int price1 = m1.getPrice();		
				int price2 = m2.getPrice();
			
			/*For ascending order*/
			  return price1-price2;
		}};
		
	public static Comparator<Ticket> priceComparatorDSC = new Comparator<Ticket>() {

			public int compare(Ticket m1, Ticket m2) {
				int price1 = m1.getPrice();		
				int price2 = m2.getPrice();
			
			/*For ascending order*/
			  return price2-price1;
		}};
}
