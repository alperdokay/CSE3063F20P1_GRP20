package CSE3063_Project;

import java.util.ArrayList;
import java.util.Random;

public class RandomLabelingMechanism extends LabelingMechanism {
	
	private Random random;
	
	public RandomLabelingMechanism() {
		this.random = new Random();
	}
	
	public RandomLabelingMechanism(Random random) {
		this.random = random;
	}
	
	public Random getRandomClass() {
		return this.random;
	}
	
	public void setRandomClass(Random random) {
		this.random = random;
	}
	
	public Assignment label(Instance instance, User user, ArrayList<Label> labels, int instanceLabelingLimit) {
		int labelCount = this.random.nextInt(instanceLabelingLimit);
		labelCount = labelCount == 0 ? 1 : labelCount;
		
		ArrayList<Integer> userPicks = new ArrayList<Integer>();
		for (int i = 0; i < labelCount; i++) {
			int randomIndex = this.random.nextInt(labels.size());
			randomIndex = randomIndex == 0 ? 1 : randomIndex;
			if (!userPicks.contains(randomIndex)) {
				userPicks.add(randomIndex);
			}
		}
		Assignment assignment = new Assignment(instance.getId(), userPicks, user.getId());
		
		return assignment;
	}
	
	@Override
	public String toString() {
		return "Random Labeling Mechanism";
	}
}
