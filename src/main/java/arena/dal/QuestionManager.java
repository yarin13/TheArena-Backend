package arena.dal;

import java.sql.SQLException;
import java.util.ArrayList;

import org.json.simple.JSONObject;

public final class QuestionManager {

	public static String questions ;
	public static ArrayList<String> answers = new ArrayList<>();
	public static JSONObject json = new JSONObject();

	//------------------------------------------ArrayList<Questions>------------------------------------------
	
	public static void getQuestions() {
		/*
		 * this function get all the questions with the appropriate answers from the database
		 * and save them as an ArrayList of type Questions called questions
		 */
		
		json.clear();
		
		String query = "select questionId,question,answerNumberOne,answerNumberTwo,answerNumberThree,answerNumberFour from questions join possibleAnswer on possibleAnswer.questionId = questions.id;";

		DBManager.runSelect(query, (res) -> {
			try {
				answers.clear();
				//questions.clear();
				
				answers.add(res.getString("answerNumberOne"));
				answers.add(res.getString("answerNumberTwo"));
				answers.add(res.getString("answerNumberThree"));
				answers.add(res.getString("answerNumberFour"));
				questions = (res.getString("question"));
				json.put(questions, new ArrayList<>(answers));
				
			} catch (SQLException e) {
				answers.clear();
				json.clear();
				//questions.clear();
				e.printStackTrace();
			}
		});
	}
	
	
}