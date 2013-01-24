package ng.jessica.hw5;

import android.text.format.Time;

public class Contact {

	private long id;
	private String displayName;
	private String firstName;
	private String lastName;
	private Time birthday;
	private Time contactStartTime;
	private Time contactEndTime;
	private String homePhone;
	private String workPhone;
	private String mobilePhone;
	private String emailAddress;
	private String postalAddress;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

//	public Calendar getBirthday() {
//		return birthday;
//	}
//
//	public void setBirthday(Calendar birthday) {
//		this.birthday = birthday;
//	}

	public Time getBirthday() {
		return birthday;
	}
	public void setBirthday(Time birthday) {
		this.birthday = birthday;
	}
	
/*	public Calendar getContactStartTime() {
		return contactStartTime;
	}

	public void setContactStartTime(Calendar contactStartTime) {
		this.contactStartTime = contactStartTime;
	}

	public Calendar getContactEndTime() {
		return contactEndTime;
	}

	public void setContactEndTime(Calendar contactEndTime) {
		this.contactEndTime = contactEndTime;
	}*/

	public Time getContactStartTime() {
		return contactStartTime;
	}

	public void setContactStartTime(Time contactStartTime) {
		this.contactStartTime = contactStartTime;
	}

	public Time getContactEndTime() {
		return contactEndTime;
	}

	public void setContactEndTime(Time contactEndTime) {
		this.contactEndTime = contactEndTime;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public String getWorkPhone() {
		return workPhone;
	}

	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPostalAddress() {
		return postalAddress;
	}

	public void setPostalAddress(String postalAddress) {
		this.postalAddress = postalAddress;
	}

	public Contact() {
	}


}
