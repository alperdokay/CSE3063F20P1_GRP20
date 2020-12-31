package CSE3063_Project;

import java.util.ArrayList;
import java.util.Random;

public class KeywordLabelingMechanism extends LabelingMechanism {
	
	private ArrayList<Label> keywords;
	
	public KeywordLabelingMechanism() {
		this.keywords = new ArrayList<Label>();
	}
	
	public KeywordLabelingMechanism(ArrayList<Label> keywords) {
		this.keywords = keywords;
	}
	
	public boolean addKeyword(Label keyword) {
		if (!this.keywords.contains(keyword)){
			this.keywords.add(keyword);
			return true;
		}
		return false;
	}
	
	public ArrayList<Label> getKeywords(){
		return this.keywords;
	}
	
	public Assignment label(Instance instance, User user, ArrayList<Label> labels, int instanceLabelingLimit){
		Random random = new Random();
		
		int labelCount = random.nextInt(instanceLabelingLimit);
		labelCount = labelCount == 0 ? 1 : labelCount;
		
		RandomLabelingMechanism randomLabelingMechanism = new RandomLabelingMechanism(random);
		
		ArrayList<Integer> userPicks = new ArrayList<Integer>();
		ArrayList<String> wordList = new ArrayList<String>();
		String[] words = instance.getInstance().split("");
		
		for (Label label: this.keywords) {
			wordList.add(label.getText().trim());
		}
		
		boolean isFound = false;
		
		for (int i = 0; i < labelCount; i++) {
			
			for (int j = 0; j < words.length; j++) {
				if (wordList.contains(words[j])) {
					System.out.println(words[j]);
					userPicks.add(j);
					break;
				}
			}
		}
		
		ArrayList<Label> userSelectedLabels = new ArrayList<Label>();
		
		for (Integer userPick: userPicks) {
			for (Label label: this.keywords) {
				if (words[userPick] == label.getText().trim()) {
					userSelectedLabels.add(label);
				}
			}
		}
		
//		if (!isFound) {
//			System.out.println("Test");
//			return randomLabelingMechanism.label(instance, user, labels, instanceLabelingLimit);
//		}
		
		Assignment assignment = new Assignment(instance, userSelectedLabels, user);
		
		return assignment;
	}
	
	@Override
	public String toString() {
		return "Keyword Labeling Mechanism";
	}
}
