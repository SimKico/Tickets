package beans;

public class Comment {
	
	private User buyer;
	private Manifestation manifestation;
	private String commentText;
	private double grade;
	private boolean isDeleted;
	
	public Comment() {
		super();
	}
	
	public Comment(User buyer, Manifestation manifestation, String commentText, double grade, boolean isDeleted) {
		super();
		this.buyer = buyer;
		this.manifestation = manifestation;
		this.commentText = commentText;
		this.grade = grade;
		this.isDeleted = isDeleted;
	}
	
	public User getBuyer() {
		return buyer;
	}
	public void setBuyer(User buyer) {
		this.buyer = buyer;
	}
	public Manifestation getManifestation() {
		return manifestation;
	}
	public void setManifestation(Manifestation manifestation) {
		this.manifestation = manifestation;
	}
	public String getCommentText() {
		return commentText;
	}
	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}
	public double getGrade() {
		return grade;
	}
	public void setGrade(double grade) {
		this.grade = grade;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	
}
