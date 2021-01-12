package CSE3063_Project;

import java.util.ArrayList;

public abstract class LabelingMechanism {
	  
	  public abstract Assignment label(Instance instance,User user, ArrayList<Label> labels, int instanceLabelingLimit);
	  
}
