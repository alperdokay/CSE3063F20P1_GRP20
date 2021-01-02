package CSE3063_Project;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Dataset {
	private Integer id;

	private String name;

	private Integer instanceLabelingLimit;

	private ArrayList<Label> labels;

	private ArrayList<Instance> instances;

	private ArrayList<Assignment> assignments;

	private ArrayList<User> users;

	public Dataset() {
		this.id = 0;
		this.name = "System Dataset";
		this.instanceLabelingLimit = 0;
	}

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

	public Dataset runDataset(DatasetReadModel model, ArrayList<Dataset> allDatasets, ArrayList<Logger> logs)
			throws Exception {
		JSONParser parser = new JSONParser();

		Object inputObj = parser.parse(new FileReader("CSE3063_Project\\" + model.getFilePath()));
		JSONObject inputJsonObject = (JSONObject) inputObj;
		long maxlabels = (long) inputJsonObject.get("maximum number of labels per instance");
		JSONArray labels = (JSONArray) inputJsonObject.get("class labels");
		JSONArray instances = (JSONArray) inputJsonObject.get("instances");

		Dataset dataset = new Dataset(model.getId(), model.getName(), (int) maxlabels);
		if (model.getCurrentDatasetStatus()) {
			DatasetAuditor datasetAuditor = new DatasetAuditor("INFO", "created", dataset);
			logs.add(datasetAuditor);
		}

		for (Object label : labels) {
			JSONObject labelObject = (JSONObject) label;
			String text = labelObject.get("label text").toString();
			Label newLabel = new Label(Integer.parseInt(labelObject.get("label id").toString()), text);

			if (model.getCurrentDatasetStatus()) {
				LabelProvider labelProvider = new LabelProvider("INFO", "created", newLabel);
				logs.add(labelProvider);
			}

			dataset.addLabel(newLabel);
		}

		for (Object instance : instances) {
			JSONObject instanceObject = (JSONObject) instance;
			String text = instanceObject.get("instance").toString();

			Instance newInstance = new Instance(Integer.parseInt(instanceObject.get("id").toString()), text);

			dataset.addInstance(newInstance);
		}

		dataset.setUsers(model.getUsers());
		File f = new File("CSE3063_Project\\" + model.getName() + ".json");
		if (f.exists() && !f.isDirectory()) {
			Object existingDatasetObj = parser.parse(new FileReader("CSE3063_Project\\" + model.getName() + ".json"));
			JSONObject datasetJsonObject = (JSONObject) existingDatasetObj;
			JSONArray assignments = (JSONArray) datasetJsonObject.get("class label assignments");

			for (Object assignment : assignments) {
				JSONObject assignmentObject = (JSONObject) assignment;
				Integer userId = Integer.parseInt(assignmentObject.get("user id").toString());
				Integer instanceId = Integer.parseInt(assignmentObject.get("instance id").toString());
				JSONArray classLabelIds = (JSONArray) assignmentObject.get("class label ids");

				ArrayList<Label> assignmentLabels = new ArrayList<Label>();

				for (Object classLabelId : classLabelIds) {
					Integer classLabelIdInteger = Integer.parseInt(classLabelId.toString());
					for (Label label : dataset.getLabels()) {
						if (label.getId() == classLabelIdInteger) {
							assignmentLabels.add(label);
							break;
						}
					}
				}

				Instance instance = new Instance(0, "");

				for (Instance instanceItem : dataset.getInstances()) {
					if (instanceItem.getId() == instanceId) {
						instance = instanceItem;
						break;
					}
				}

				User user = new User();
				for (User userItem : dataset.getUsers()) {
					if (userItem.getId() == userId) {
						user = userItem;
						break;
					}
				}
				Assignment oldAssignment = new Assignment(instance, assignmentLabels, user);
				dataset.addAssignment(oldAssignment);
			}
		}

		return dataset;
	}

	public void labelDataset(ArrayList<Logger> logs, User humanUser) throws Exception {
		ArrayList<Assignment> oldAssignments = new ArrayList<Assignment>();
		
		for (Assignment assignment : this.getAssignments()) {
			if (assignment.getUser().getId() == humanUser.getId()) {
				oldAssignments.add(assignment);
			}
		}
		
		this.setAssignments(oldAssignments);
		Random random = new Random();

		KeywordLabelingMechanism keywordMechanism = new KeywordLabelingMechanism(this.getLabels());

		RandomLabelingMechanism randomMechanism = new RandomLabelingMechanism(random);
		ArrayList<Instance> previouslyLabeledInstances = new ArrayList<Instance>();
		if (humanUser.getId() != 0) {
			ArrayList<Integer> assignedInstanceIds = new ArrayList<Integer>();

			for (Assignment assignment : this.getAssignments()) {
				if (assignment.getUser().getId() == humanUser.getId()) {
					if (!assignedInstanceIds.contains(assignment.getInstance().getId())) {
						assignedInstanceIds.add(assignment.getInstance().getId());
					}
				}
			}
			System.out.println("Welcome to the labeling system of Group 20!");
			System.out.println("You can use any of the following labels to label the upcoming instances!");
			ArrayList<String> existingLabelIds = new ArrayList<String>();

			for (Label label : this.getLabels()) {
				System.out.println(label.getId() + " - " + label.getText());
				existingLabelIds.add(label.getId().toString());
			}

			System.out.println("You can label an instance at most " + this.getInstanceLabellingLimit().toString()
					+ " times! Use comma to separate them");

			int labelCounter = 0;
			for (Instance instance : this.getInstances()) {
				int assignmentCounter = 0;
				double randomDouble = random.nextDouble();
				if (assignmentCounter > 0) {
					if (randomDouble < humanUser.getConsistencyCheckProbability()) {
						int labeledInstancesSize = previouslyLabeledInstances.size();
						if (labeledInstancesSize < 0) {
							labeledInstancesSize = 0;
						}
						int randomIndex = random.nextInt(labeledInstancesSize);
						instance = previouslyLabeledInstances.get(randomIndex);
					}
				}
				if (!assignedInstanceIds.contains(instance.getId())) {
					labelCounter++;
					System.out.println("Instance: " + instance.getInstance());
					String[] labelIds = {};
					Scanner input = new Scanner(System.in);
					while (true) {
						boolean isValid = true;
						System.out.print("Enter label id(s): ");
						String labelId = input.nextLine();

						labelIds = labelId.split(",");

						if (labelIds.length > 10) {
							System.out.println("You cannot label an instance with more than 10 labels");
							isValid = false;
							continue;
						}
						for (String labelIdString : labelIds) {
							if (!existingLabelIds.contains(labelIdString)) {
								System.out.println("You entered an id (" + labelIdString
										+ ") which does not exist in our system. Try again!");
								isValid = false;
								break;
							}
						}

						if (isValid) {
							break;
						}

					}

					ArrayList<Label> assignedLabels = new ArrayList<Label>();

					for (String labelId : labelIds) {
						for (Label label : this.getLabels()) {
							if (label.getId() == Integer.parseInt(labelId)) {
								assignedLabels.add(label);
								break;
							}
						}
					}

					Assignment assignment = new Assignment(instance, assignedLabels, humanUser);
					this.addAssignment(assignment);
					System.out.println("Labeling successfully completed!");
					this.exportInstanceMetrics();
					this.exportDatasetMetrics();
					this.exportOutput();
				}
			}
			if (labelCounter == 0) {
				System.out.println("You already labeled all the instances!");
			}
		}

		previouslyLabeledInstances = new ArrayList<Instance>();
		for (int i = 0; i < this.getUsers().size(); i++) {
			boolean isRandomMechanism = false;
			User user = this.getUsers().get(i);

			if (humanUser != null && user.getId() == humanUser.getId()) {
				continue;
			}

			if (user.getType().equals("Random") || user.getType().equals("Human")) {
				isRandomMechanism = true;
			}
			int assignmentCounter = 0;
			for (int j = 0; j < this.getInstances().size(); j++) {
				double randomDouble = random.nextDouble();
				Instance instance = this.getInstances().get(j);
				if (assignmentCounter > 0) {
					if (randomDouble < user.getConsistencyCheckProbability()) {
						int labeledInstancesSize = previouslyLabeledInstances.size();
						if (labeledInstancesSize < 0) {
							labeledInstancesSize = 0;
						}
						int randomIndex = random.nextInt(labeledInstancesSize);
						instance = previouslyLabeledInstances.get(randomIndex);
					}
				}
				Assignment assignment = isRandomMechanism
						? randomMechanism.label(instance, user, this.getLabels(), this.getInstanceLabellingLimit())
						: keywordMechanism.label(instance, user, this.getLabels(), this.getInstanceLabellingLimit());
				
				
				if (!previouslyLabeledInstances.contains(instance)) {
					previouslyLabeledInstances.add(instance);
				}
				this.addAssignment(assignment);

				InstanceTagger tagger = new InstanceTagger("INFO", "created", assignment);
				logs.add(tagger);
				this.exportInstanceMetrics();
				this.exportDatasetMetrics();
				this.exportOutput();
				assignmentCounter++;
			}
		}
	}

	public void exportDatasetMetrics() throws IOException {
		Map<String, Object> datasetMetricsMap = new LinkedHashMap<String, Object>();

		ArrayList<Integer> labelIds = new ArrayList<>();
		ArrayList<Integer> classDistributions = new ArrayList<>();
		ArrayList<Integer> userCompletenessPercentages = new ArrayList<>();
		ArrayList<Integer> userConsistencyPercentages = new ArrayList<>();
		ArrayList<Integer> uniqueInstances = new ArrayList<>();
		ArrayList<Integer> consistentLabels = new ArrayList<>();
		int numberOfUsedLabels = this.getLabels().size(), frequency, totalNumberOfLabelAssignments = 0, counterUser = 1,
				numberOfConsistency = 0, totalConsistency = 0, tempj = 0;

		for (Assignment assignment : this.getAssignments()) {
			for (Label label : assignment.getLabels()) {
				if (!labelIds.contains(label.getId())) {
					labelIds.add(label.getId());
					numberOfUsedLabels--;
				}
			}
		}

		double completenessPercentage = 100 - (numberOfUsedLabels / this.getLabels().size() * 100);
		datasetMetricsMap.put("completeness percentage", completenessPercentage);

		labelIds.removeAll(labelIds);

		for (int i = 0; i < this.getAssignments().size(); i++) {
			numberOfUsedLabels = this.getAssignments().get(i).getLabels().size();
			totalNumberOfLabelAssignments += this.getAssignments().get(i).getLabels().size();
			for (int j = 0; j < this.getAssignments().get(i).getLabels().size(); j++) {
				tempj = j;
				if (!classDistributions.contains(this.getAssignments().get(i).getLabels().get(j).getId())) {
					classDistributions.add(this.getAssignments().get(i).getLabels().get(j).getId());
					uniqueInstances.add(this.getAssignments().get(i).getLabels().get(j).getId());
					consistentLabels.add(this.getAssignments().get(i).getLabels().get(j).getId());
					classDistributions.add(0);
					uniqueInstances.add(0);
					consistentLabels.add(0);
				}
				for (int m = 0; m < classDistributions.size(); m += 2) {
					if (classDistributions.get(m) == this.getAssignments().get(i).getLabels().get(j).getId()) {
						frequency = classDistributions.get(m + 1);
						classDistributions.set(m + 1, ++frequency);
					}
					if (uniqueInstances.get(m) == this.getAssignments().get(i).getLabels().get(j).getId()) {
						if (!uniqueInstances.contains(this.getAssignments().get(i).getInstance().getId())) {
							frequency = uniqueInstances.get(m + 1);
							uniqueInstances.set(m + 1, ++frequency);
						}
					}
				}
				for (int b = 0; b < consistentLabels.size(); b += 2) {
					if (consistentLabels.get(b) == this.getAssignments().get(i).getLabels().get(j).getId()) {
						frequency = consistentLabels.get(b + 1);
						consistentLabels.set(b + 1, ++frequency);
					}
				}
				if (!labelIds.contains(this.getAssignments().get(i).getLabels().get(j).getId())) {
					labelIds.add(this.getAssignments().get(i).getLabels().get(j).getId());
					numberOfUsedLabels--;
				}
			}
			if (!userCompletenessPercentages.contains(this.getAssignments().get(i).getUser().getId())) {
				userCompletenessPercentages.add(this.getAssignments().get(i).getUser().getId());
				int userCompletenessPercentage = 100 - (numberOfUsedLabels * 100 / this.getLabels().size());
				userCompletenessPercentages.add(userCompletenessPercentage);
			}
			if (counterUser != this.getAssignments().get(i).getUser().getId()) {
				for (int a = 0; a < consistentLabels.size(); a += 2) {
					if (consistentLabels.get(a + 1) > 1) {
						numberOfConsistency += consistentLabels.get(a + 1);
						totalConsistency += consistentLabels.get(a) * consistentLabels.get(a + 1);
					}
					totalConsistency += consistentLabels.get(a) * consistentLabels.get(a + 1);
				}
				if (totalConsistency == 0) {
					totalConsistency = this.getLabels().size();
				}
				int userConsistencyPercentage = 100 - (numberOfConsistency * 100 / totalConsistency);
				userConsistencyPercentages.add(counterUser);
				userConsistencyPercentages.add(userConsistencyPercentage);
				consistentLabels.removeAll(consistentLabels);
				counterUser++;
				totalConsistency = 0;
				numberOfConsistency = 0;
			} else if (i == this.getAssignments().size() - 1) {
				if (tempj == this.getAssignments().get(i).getLabels().size() - 1) {
					for (int a = 0; a < consistentLabels.size(); a += 2) {
						if (consistentLabels.get(a + 1) > 1) {
							numberOfConsistency += consistentLabels.get(a + 1);
							totalConsistency += consistentLabels.get(a) * consistentLabels.get(a + 1);
						}
						totalConsistency += consistentLabels.get(a) * consistentLabels.get(a + 1);
					}
					if (totalConsistency == 0) {
						totalConsistency = this.getLabels().size();
					}
					int userConsistencyPercentage = 100 - (numberOfConsistency * 100 / totalConsistency);
					userConsistencyPercentages.add(counterUser);
					userConsistencyPercentages.add(userConsistencyPercentage);
					consistentLabels.removeAll(consistentLabels);
					counterUser++;
					totalConsistency = 0;
					numberOfConsistency = 0;
				}
			}
		}

		JSONArray classDistributionsBased = new JSONArray();
		JSONArray uniqueInstancesClassLabel = new JSONArray();

		for (int k = 0; k < classDistributions.size(); k += 2) {
			frequency = classDistributions.get(k + 1);
			classDistributions.set(k + 1, frequency * 100 / totalNumberOfLabelAssignments);
			Map<String, Object> classDistributionsHashMap = new LinkedHashMap<String, Object>();
			Map<String, Object> uniqueInstancesHashMap = new LinkedHashMap<String, Object>();
			classDistributionsHashMap.put("class distribution based on final instance label",
					classDistributions.get(k + 1) + "% " + classDistributions.get(k));
			uniqueInstancesHashMap.put("class label and number of unique instances",
					uniqueInstances.get(k) + " and " + uniqueInstances.get(k + 1));
			classDistributionsBased.add(classDistributionsHashMap);
			uniqueInstancesClassLabel.add(uniqueInstancesHashMap);
		}
		datasetMetricsMap.put("class distributions based on final instance labels", classDistributionsBased);
		datasetMetricsMap.put("number of unique instances for each class label", uniqueInstancesClassLabel);

		int numberOfUsers = this.getUsers().size();
		datasetMetricsMap.put("number of users", numberOfUsers);

		JSONArray usersAndCompletenessPercentages = new JSONArray();

		for (int n = 0; n < userCompletenessPercentages.size(); n += 2) {
			Map<String, Object> usersAndCompletenessPercentagesHashMap = new LinkedHashMap<String, Object>();
			usersAndCompletenessPercentagesHashMap.put("user and completeness percentage",
					"(" + userCompletenessPercentages.get(n) + ", " + userCompletenessPercentages.get(n + 1) + "%)");
			usersAndCompletenessPercentages.add(usersAndCompletenessPercentagesHashMap);
		}
		datasetMetricsMap.put("users assigned and their completeness percentage", usersAndCompletenessPercentages);

		JSONArray usersAndConsistencyPercentages = new JSONArray();

		for (int c = 0; c < userConsistencyPercentages.size(); c += 2) {
			Map<String, Object> usersAndConsistencyPercentagesHashMap = new LinkedHashMap<String, Object>();
			usersAndConsistencyPercentagesHashMap.put("user and consistency percentage",
					"(" + userConsistencyPercentages.get(c) + ", " + userConsistencyPercentages.get(c + 1) + "%)");
			usersAndConsistencyPercentages.add(usersAndConsistencyPercentagesHashMap);
		}
		datasetMetricsMap.put("users assigned and their consistency percentage", usersAndConsistencyPercentages);

		// Instantiate a new Gson instance.
		Gson gson = new Gson();

		// Convert the ordered map into an ordered string.
		String json = gson.toJson(datasetMetricsMap, Map.class);

		File targetFile = new File("CSE3063_Project\\dataset" + this.getId() + ".json");
		targetFile.createNewFile();
		try (Writer writer = new FileWriter(targetFile.getAbsolutePath(), false)) {
			Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
			gson2.toJson(datasetMetricsMap, writer);

		}
	}

	public void exportInstanceMetrics() throws IOException {
		Map<String, Object> instanceMetricsMap = new LinkedHashMap<String, Object>();

		int frequency, max_frequency = 0;
		double entropy = 0.0;
		int total_number_of_label_assignments = 0, number_of_unique_label_assignments, number_of_unique_users,
				most_frequent_class_label = 0;
		ArrayList<Integer> unique_label_assignments = new ArrayList<Integer>();
		ArrayList<Integer> unique_users = new ArrayList<Integer>();
		ArrayList<Integer> class_labels_and_frequencies = new ArrayList<Integer>();

		for (int i = 0; i < this.getAssignments().size(); i++) {
			total_number_of_label_assignments += this.getAssignments().get(i).getLabels().size();
			for (int j = 0; j < this.getAssignments().get(i).getLabels().size(); j++) {
				if (!unique_label_assignments.contains(this.getAssignments().get(i).getLabels().get(j).getId())) {
					unique_label_assignments.add(this.getAssignments().get(i).getLabels().get(j).getId());
					class_labels_and_frequencies.add(this.getAssignments().get(i).getLabels().get(j).getId());
					class_labels_and_frequencies.add(0);
				}
				for (int m = 0; m < class_labels_and_frequencies.size(); m += 2) {
					if (class_labels_and_frequencies.get(m) == this.getAssignments().get(i).getLabels().get(j)
							.getId()) {
						frequency = class_labels_and_frequencies.get(m + 1);
						class_labels_and_frequencies.set(m + 1, ++frequency);
					}
				}
			}
			if (!unique_users.contains(this.getAssignments().get(i).getUser().getId())) {
				unique_users.add(this.getAssignments().get(i).getUser().getId());
			}
		}

		for (int k = 0; k < class_labels_and_frequencies.size(); k += 2) {
			frequency = class_labels_and_frequencies.get(k + 1);
			class_labels_and_frequencies.set(k + 1, frequency * 100 / total_number_of_label_assignments);
			frequency = class_labels_and_frequencies.get(k + 1);
			if (max_frequency < frequency) {
				most_frequent_class_label = class_labels_and_frequencies.get(k);
				max_frequency = frequency;
			}
		}

		number_of_unique_label_assignments = unique_label_assignments.size();
		number_of_unique_users = unique_users.size();

		instanceMetricsMap.put("total number of label assignments", total_number_of_label_assignments);
		instanceMetricsMap.put("number of unique label assignments", number_of_unique_label_assignments);
		instanceMetricsMap.put("number of unique users", number_of_unique_users);
		instanceMetricsMap.put("most frequent class label and percentage",
				"(" + most_frequent_class_label + ", " + max_frequency + "%)");

		JSONArray class_labels_and_percentages = new JSONArray();

		for (int n = 0; n < class_labels_and_frequencies.size(); n += 2) {
			frequency = class_labels_and_frequencies.get(n + 1);
			Map<String, Object> classLabelsAndPercentagesHashMap = new LinkedHashMap<String, Object>();
			classLabelsAndPercentagesHashMap.put("class label and percentage",
					"(" + class_labels_and_frequencies.get(n) + ", " + frequency + "%)");
			class_labels_and_percentages.add(classLabelsAndPercentagesHashMap);
			entropy += (-1) * (frequency / 100.0)
					* (Math.log10(frequency / 100.0) / Math.log10(number_of_unique_label_assignments));
		}

		instanceMetricsMap.put("class labels and percentages", class_labels_and_percentages);
		DecimalFormat df = new DecimalFormat("#.###");
		instanceMetricsMap.put("entropy", df.format(entropy));

		// Instantiate a new Gson instance.
		Gson gson = new Gson();

		// Convert the ordered map into an ordered string.
		String json = gson.toJson(instanceMetricsMap, Map.class);

		File targetFile = new File("CSE3063_Project\\instanceDataset" + this.getId() + ".json");
		targetFile.createNewFile();
		try (Writer writer = new FileWriter(targetFile.getAbsolutePath(), false)) {
			Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
			gson2.toJson(instanceMetricsMap, writer);

		}
	}

	public boolean exportOutput() throws IOException {
		Map<String, Object> myLinkedHashMap = new LinkedHashMap<String, Object>();

		// Add items, in-order, to the map.
		myLinkedHashMap.put("dataset id", this.getId());
		myLinkedHashMap.put("dataset name", this.getName());
		myLinkedHashMap.put("maximum number of labels per instance", this.getInstanceLabellingLimit());

		JSONArray labels = new JSONArray();

		for (Label label : this.getLabels()) {
			Map<String, Object> labelHashMap = new LinkedHashMap<String, Object>();
			labelHashMap.put("label id", label.getId());
			labelHashMap.put("label text", label.getText());

			labels.add(labelHashMap);
		}
		myLinkedHashMap.put("class labels", labels);

		JSONArray instances = new JSONArray();

		for (Instance instance : this.getInstances()) {
			JSONObject instanceObject = new JSONObject();
			instanceObject.put("id", instance.getId());
			instanceObject.put("instance", instance.getInstance());

			instances.add(instanceObject);
		}
		myLinkedHashMap.put("instances", instances);

		JSONArray assignments = new JSONArray();

		for (Assignment assignment : this.getAssignments()) {
			JSONObject assignmentObject = new JSONObject();
			ArrayList<Integer> labelIds = new ArrayList<Integer>();

			for (Label label : assignment.getLabels()) {
				labelIds.add(label.getId());
			}

			assignmentObject.put("instance id", assignment.getInstance().getId());
			assignmentObject.put("class label ids", labelIds);
			assignmentObject.put("user id", assignment.getUser().getId());
			assignmentObject.put("datetime", assignment.getDateTime());

			assignments.add(assignmentObject);
		}
		myLinkedHashMap.put("class label assignments", assignments);

		JSONArray users = new JSONArray();

		for (User user : this.getUsers()) {
			JSONObject userObject = new JSONObject();
			userObject.put("user id", user.getId());
			userObject.put("user name", user.getName());
			userObject.put("user type", user.getType());

			users.add(userObject);
		}
		myLinkedHashMap.put("users", users);
		// Instantiate a new Gson instance.
		Gson gson = new Gson();

		// Convert the ordered map into an ordered string.
		String json = gson.toJson(myLinkedHashMap, Map.class);

		File targetFile = new File("CSE3063_Project\\" + this.name + ".json");
		targetFile.createNewFile();
		try (Writer writer = new FileWriter(targetFile.getAbsolutePath(), false)) {
			Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
			gson2.toJson(myLinkedHashMap, writer);

		}
		// Print ordered string.
		return true;
	}
}
