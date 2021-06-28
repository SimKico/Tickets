package beans;

public class Comment {
	
	private User buyer;
	private String manifestation;
	private String commentText;
	private double grade;
	private boolean isDeleted;
	private boolean isApproved;
	private boolean isRefused;
	
	public Comment() {
		super();
	}
	
	public Comment(User buyer, String manifestation, String commentText, double grade, boolean isDeleted, boolean isApproved) {
		super();
		this.buyer = buyer;
		this.manifestation = manifestation;
		this.commentText = commentText;
		this.grade = grade;
		this.isDeleted = isDeleted;
		this.isApproved = isApproved;
		this.isRefused = false;
	}
	
	public User getBuyer() {
		return buyer;
	}
	public void setBuyer(User buyer) {
		this.buyer = buyer;
	}
	public String getManifestation() {
		return manifestation;
	}
	public void setManifestation(String manifestation) {
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

	public boolean isApproved() {
		return isApproved;
	}

	public void setApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}

	public boolean isRefused() {
		return isRefused;
	}

	public void setRefused(boolean isRefused) {
		this.isRefused = isRefused;
	}
	
	
	
}
