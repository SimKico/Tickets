package beans;

public class BuyerType {
	private String typeName; // e.g. GOLD, SILVER, BRONZE
	private int discount;
	private int nextLevelPoints;
	
	public BuyerType() {
		super();
	}
	
	public BuyerType(String typeName, int discount, int nextLevelPoints) {
		super();
		this.typeName = typeName;
		this.discount = discount;
		this.nextLevelPoints = nextLevelPoints;
	}
	
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public int getDiscount() {
		return discount;
	}
	public void setDiscount(int discount) {
		this.discount = discount;
	}
	public int getNextLevelPoints() {
		return nextLevelPoints;
	}
	public void setNextLevelPoints(int nextLevelPoints) {
		this.nextLevelPoints = nextLevelPoints;
	} 
	
}
