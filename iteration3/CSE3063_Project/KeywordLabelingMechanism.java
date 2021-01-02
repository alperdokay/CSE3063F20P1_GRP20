package CSE3063_Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
		if (!this.keywords.contains(keyword)) {
			this.keywords.add(keyword);
			return true;
		}
		return false;
	}

	public ArrayList<Label> getKeywords() {
		return this.keywords;
	}

	public Assignment label(Instance instance, User user, ArrayList<Label> labels, int instanceLabelingLimit) {
		Random random = new Random();
		Map<String, Label> mapping = new HashMap<String, Label>();

		int labelCount = random.nextInt(instanceLabelingLimit);
		labelCount = labelCount == 0 ? 1 : labelCount;

		RandomLabelingMechanism randomLabelingMechanism = new RandomLabelingMechanism(random);

		ArrayList<Integer> userPicks = new ArrayList<Integer>();
		ArrayList<String> wordList = new ArrayList<String>();
		String[] words = instance.getInstance().split(" ");

		for (Label label : this.keywords) {
			wordList.add(label.getText().trim());
			mapping.put(label.getText().trim(), label);
		}

		for (int i = 0; i < labelCount; i++) {

			for (int j = 0; j < words.length; j++) {
				for (String word : wordList) {
					if (word.equals(words[j])) {
						if (!userPicks.contains(j)) {
							userPicks.add(j);
							continue;
						}
						break;
					}
				}
			}
		}

		ArrayList<Label> userSelectedLabels = new ArrayList<Label>();

		for (Integer userPick : userPicks) {
			Label label = mapping.get(words[userPick]);
			userSelectedLabels.add(label);
		}

		Assignment assignment = new Assignment(instance, userSelectedLabels, user);

		return assignment;
	}

	@Override
	public String toString() {
		return "Keyword Labeling Mechanism";
	}
}
