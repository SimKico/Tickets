package beans;

public class Reservation {

	String title;
	String typeOfTicket;
	int price;
	
	public Reservation(String title, String typeOfTicket, int price) {
		super();
		this.title = title;
		this.typeOfTicket = typeOfTicket;
		this.price = price;
	}
	public Reservation() {
		super();
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTypeOfTicket() {
		return typeOfTicket;
	}
	public void setTypeOfTicket(String typeOfTicket) {
		this.typeOfTicket = typeOfTicket;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}

}
