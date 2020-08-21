package arena;

import java.util.ArrayList;

public class Questions  {
	
	//---------------------------properties------------------------------------------
	private int id;
	private String question;
	private ArrayList<String> answers;
	
	
	//---------------------------Getters & Setters------------------------------------------
	
	public ArrayList<String> getAnswers() {
		return answers;
	}

	public int getId() {
		return id;
	}
	public String getQuestion() {
		return question;
	}

	
	//---------------------------Contractors------------------------------------------
	public Questions(int id, String question,ArrayList<String> answers) {
		this.id = id;
		this.question = question;
		this.answers = answers;
	}
	
	public Questions( String question,ArrayList<String> answers) {
		this.question = question;
		this.answers = answers;
	}
	
	public Questions( String question) {
		this.question = question;
	}
	public Questions() {
	}
	
	
	@Override
	public String toString() {
		return this.question + this.answers;
	}

}
