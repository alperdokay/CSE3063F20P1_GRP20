package CSE3063_Project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class Dataset {
	private Integer id;

	private String name;

	private Integer instanceLabelingLimit;

	private ArrayList<Label> labels;

	private ArrayList<Instance> instances;

	private ArrayList<Assignment> assignments;

	private ArrayList<User> users;

	public Dataset(Integer id, String name, Integer instanceLabellingLimit) {
		this.id = id;
		this.name = name;
		this.instanceLabelingLimit = instanceLabellingLimit;
		this.labels = new ArrayList<Label>();
		this.instances = new ArrayList<Instance>();
		this.assignments = new ArrayList<Assignment>();
		this.users = new ArrayList<User>();
	}

	@Override
	public String toString() {
		return "Dataset{" + "Name='" + name + '\'' + '}';
	}

	public ArrayList<User> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getInstanceLabellingLimit() {
		return instanceLabelingLimit;
	}

	public void setInstanceLabellingLimit(Integer instanceLabellingLimit) {
		this.instanceLabelingLimit = instanceLabellingLimit;
	}

	public ArrayList<Label> getLabels() {
		return labels;
	}

	public void setLabels(ArrayList<Label> labels) {
		this.labels = labels;
	}

	public ArrayList<Instance> getInstances() {
		return instances;
	}

	public void setInstances(ArrayList<Instance> instances) {
		this.instances = instances;
	}

	public ArrayList<Assignment> getAssignments() {
		return assignments;
	}

	public void setAssignments(ArrayList<Assignment> assignments) {
		this.assignments = assignments;
	}

	public boolean addLabel(Label label) {

		Optional<Label> existedLabels = this.getLabels().stream().filter(data -> label.getId().equals(data.getId()))
				.findFirst();
		if (existedLabels.isEmpty()) {
			this.labels.add(label);
			return true;
		} else {
			return false;
		}
	}

	public boolean addInstance(Instance instance) {

		Optional<Instance> existedLabels = this.getInstances().stream()
				.filter(data -> instance.getId().equals(data.getId())).findFirst();
		if (existedLabels.isEmpty()) {
			this.instances.add(instance);
			return true;
		} else {
			return false;
		}
	}

	public boolean addAssignment(Assignment assignment) {
		this.assignments.add(assignment);
		return true;
	}

	public boolean addUser(User user) {

		Optional<User> existedLabels = this.getUsers().stream().filter(data -> user.getId().equals(data.getId()))
				.findFirst();
		if (existedLabels.isEmpty()) {
			this.users.add(user);
			return true;
		} else {
			return false;
		}
	}

	public boolean exportOutput() throws IOException {
		
		return true;
	}
}
