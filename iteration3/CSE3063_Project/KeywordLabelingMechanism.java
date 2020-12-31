package CSE3063_Project;

import java.util.ArrayList;
import java.util.Random;

public class KeywordLabelingMechanism extends LabelingMechanism {
	
	private ArrayList<String> keywords;
	
	public KeywordLabelingMechanism() {
		this.keywords = new ArrayList<String>();
	}
	
	public KeywordLabelingMechanism(ArrayList<String> keywords) {
		this.keywords = keywords;
	}
	
	public boolean addKeyword(String keyword) {
		if (!this.keywords.contains(keyword)){
			this.keywords.add(keyword);
			return true;
		}
		return false;
	}
	
	public ArrayList<String> getKeywords(){
		return this.keywords;
	}
	
	public Assignment label(Instance instance, User user, ArrayList<Label> labels, int instanceLabelingLimit){
		Random random = new Random();
		
		int labelCount = random.nextInt(instanceLabelingLimit);
		labelCount = labelCount == 0 ? 1 : labelCount;
		
		ArrayList<Label> userPicks = new ArrayList<Label>();
		
		for (int i = 0; i < labelCount; i++) {
			for (int j = 0; j < this.keywords.size(); j++) {
				if (instance.getInstance().contains(this.keywords.get(j))) {
					userPicks.add(labels.get(j));
				}
			}
		}
		
		Assignment assignment = new Assignment(instance, userPicks, user);
		
		return assignment;
	}
	
	@Override
	public String toString() {
		return "Keyword Labeling Mechanism";
	}
}
