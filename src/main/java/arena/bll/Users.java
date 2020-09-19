package arena.bll;

public class Users {

	//---------------------------properties------------------------------------------
	private int id;
	private String firstName;
	private String lastName;
	private String email;
	private String phoneNumber;
	private int age;
	private String gender;
	private String interestedIn;
	private int score;
	
	//------------------------------Getters & Setters----------------------------------
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getInterestedIn() {
		return interestedIn;
	}
	public void setInterestedIn(String intrestedIn) {
		this.interestedIn = intrestedIn;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	//----------------------------Constructors------------------------------------
	
	//-------------Without id-------------
	public Users(String email,String firstName, String lastName, String phoneNumber, int age, String gender,String intrestedIn, int score) {
		setEmail(email);
		setFirstName(firstName);
		setLastName(lastName);
		setPhoneNumber(phoneNumber);
		setAge(age);
		setGender(gender);
		setInterestedIn(intrestedIn);
		setScore(score);
	}
	public Users(String email,String firstName) {
		setEmail(email);
		setFirstName(firstName);
	}
	//-------------All properties are included-------------
	public Users(int id, String email,String firstName, String lastName, String phoneNumber, int age, String gender,String intrestedIn, int score) {
		setId(id);
		setEmail(email);
		setFirstName(firstName);
		setLastName(lastName);
		setPhoneNumber(phoneNumber);
		setAge(age);
		setGender(gender);
		setInterestedIn(intrestedIn);
		setScore(score);
	}
	
}
