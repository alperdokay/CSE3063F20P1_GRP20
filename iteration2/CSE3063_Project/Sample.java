package CSE3063_Project;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Sample {
	static ArrayList<Logger> logs = new ArrayList<Logger>();

	public static void main(String[] args) throws Exception {

		ArrayList<User> users = getUsers();
		ArrayList<DatasetReadModel> datasets = getDatasets(users);
		ArrayList<Dataset> allDatasets = new ArrayList<Dataset>();

		// importing old datasets
		for (DatasetReadModel model : datasets) {
			if (!model.getCurrentDatasetStatus()) {
				Dataset dataset = runDataset(model, allDatasets);
				allDatasets.add(dataset);
			}
		}
		for (DatasetReadModel model : datasets) {
			if (model.getCurrentDatasetStatus()) {
				Dataset dataset = runDataset(model, allDatasets);
				allDatasets.add(dataset);
			}
		}
		try {
			File myObj = new File("CSE3063_Project\\log_file.txt");
			if (myObj.createNewFile()) {
				System.out.println("Log file created: " + myObj.getName());
			}
			FileWriter myWriter = new FileWriter("CSE3063_Project\\log_file.txt");
			for (Logger log : logs) {
				myWriter.write(log.getLogMessage() + "\n");
			}
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	public static ArrayList<User> getUsers() throws Exception {
		ArrayList<User> users = new ArrayList<User>();

		JSONParser parser = new JSONParser();
		Object confObj = parser.parse(new FileReader("CSE3063_Project\\newConf.json", StandardCharsets.UTF_8));
		JSONObject confJsonObject = (JSONObject) confObj;

		JSONArray userArray = (JSONArray) confJsonObject.get("users");

		for (Object user : userArray) {
			JSONObject userObject = (JSONObject) user;

			Integer userId = Integer.parseInt(userObject.get("id").toString());
			String userName = userObject.get("userName").toString();
			String userType = userObject.get("userType").toString();
			double consistencyCheckProbability = Double
					.parseDouble(userObject.get("consistencyCheckProbability").toString());

			User newUser = new User(userId, userName, userType, consistencyCheckProbability);
			UserManager userManager = new UserManager("INFO", "created", newUser);
			logs.add(userManager);
			System.out.println(userManager.getLogMessage());
			users.add(newUser);
		}

		return users;
	}

	public static ArrayList<DatasetReadModel> getDatasets(ArrayList<User> users) throws Exception {
		ArrayList<DatasetReadModel> datasets = new ArrayList<DatasetReadModel>();

		JSONParser parser = new JSONParser();
		Object confObj = parser.parse(new FileReader("CSE3063_Project\\newConf.json", StandardCharsets.UTF_8));
		JSONObject confJsonObject = (JSONObject) confObj;
		Integer currentDatasetId = Integer.parseInt(confJsonObject.get("currentDatasetId").toString());

		JSONArray datasetArray = (JSONArray) confJsonObject.get("datasets");

		for (Object dataset : datasetArray) {
			JSONObject datasetObject = (JSONObject) dataset;

			Integer datasetId = Integer.parseInt(datasetObject.get("id").toString());
			String datasetName = datasetObject.get("name").toString();
			String filePath = datasetObject.get("filePath").toString();

			ArrayList<User> datasetUsers = new ArrayList<User>();
			JSONArray userIds = (JSONArray) datasetObject.get("userIds");

			ArrayList<Integer> integerUserIds = new ArrayList<Integer>();

			for (Object userId : userIds) {
				integerUserIds.add(Integer.parseInt(userId.toString()));
			}

			for (User user : users) {
				if (integerUserIds.contains(user.getId())) {
					datasetUsers.add(user);
				}
			}

			boolean isCurrentDataset = false;

			if (datasetId == currentDatasetId) {
				isCurrentDataset = true;
			}

			DatasetReadModel model = new DatasetReadModel(datasetId, datasetName, filePath, isCurrentDataset,
					datasetUsers);
			datasets.add(model);
		}

		return datasets;
	}

	public static Dataset runDataset(DatasetReadModel model, ArrayList<Dataset> allDatasets) throws Exception {
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
			System.out.println(datasetAuditor.getLogMessage());
		}

		for (Object label : labels) {
			JSONObject labelObject = (JSONObject) label;
			String text = labelObject.get("label text").toString();
			Label newLabel = new Label(Integer.parseInt(labelObject.get("label id").toString()), text);

			if (model.getCurrentDatasetStatus()) {
				LabelProvider labelProvider = new LabelProvider("INFO", "created", newLabel);
				logs.add(labelProvider);
				System.out.println(labelProvider.getLogMessage());
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

		if (model.getCurrentDatasetStatus()) {

			KeywordLabelingMechanism keywordMechanism = new KeywordLabelingMechanism();
			RandomLabelingMechanism randomMechanism = new RandomLabelingMechanism();

			keywordMechanism.addKeyword("güzel");
			keywordMechanism.addKeyword("etkili");
			keywordMechanism.addKeyword("harika");
			keywordMechanism.addKeyword("paket");
			keywordMechanism.addKeyword("numara");

			Random random = new Random();

			ArrayList<Instance> previouslyLabeledInstances = new ArrayList<Instance>();
			for (int i = 0; i < dataset.getUsers().size(); i++) {
				int assignmentCounter = 0;
				for (int j = 0; j < dataset.getInstances().size(); j++) {
					double randomDouble = random.nextDouble();
					User user = dataset.getUsers().get(i);
					Instance instance = dataset.getInstances().get(j);
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
					Assignment assignment = randomMechanism.label(instance, user, dataset.getLabels(),
							dataset.getInstanceLabellingLimit());
					if (!previouslyLabeledInstances.contains(instance)) {
						previouslyLabeledInstances.add(instance);
					}
					dataset.addAssignment(assignment);

					InstanceTagger tagger = new InstanceTagger("INFO", "created", assignment);
					logs.add(tagger);
					System.out.println(tagger.getLogMessage());
					exportInstanceMetrics(dataset);
					exportDatasetMetrics(dataset);
					dataset.exportOutput();
					if (assignmentCounter > 1) {
						allDatasets.add(dataset);
						exportUserMetrics(user, allDatasets);
						allDatasets.remove(dataset);
					}
					assignmentCounter++;
					//TimeUnit.SECONDS.sleep(1);
				}
			}
		} else {
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

				User user = new User(0, "", "", 0.1);
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

	public static void exportDatasetMetrics(Dataset dataset) {
		ArrayList<Integer> labelIds = new ArrayList<>();
		ArrayList<Integer> classDistributions = new ArrayList<>();
		ArrayList<Integer> userCompletenessPercentages = new ArrayList<>();
		int numberOfUsedLabels = dataset.getLabels().size(), frequency, totalNumberOfLabelAssignments = 0, counter = 0;

		for (Assignment assignment : dataset.getAssignments()) {
			for (Label label : assignment.getLabels()) {
				if (!labelIds.contains(label.getId())) {
					labelIds.add(label.getId());
					numberOfUsedLabels--;
				}
			}
		}

		double completenessPercentage = 100 - (numberOfUsedLabels / dataset.getLabels().size() * 100);

		for (int i = 0; i < dataset.getAssignments().size(); i++) {
			numberOfUsedLabels = dataset.getAssignments().get(i).getLabels().size();
			totalNumberOfLabelAssignments += dataset.getAssignments().get(i).getLabels().size();
			// System.out.println("--");
			for (int j = 0; j < dataset.getAssignments().get(i).getLabels().size(); j++) {
				// System.out.println("-> " + dataset.getAssignments().get(i).getLabels());
				// System.out.println("-> " + dataset.getAssignments().get(i).getUser());
				if (!classDistributions.contains(dataset.getAssignments().get(i).getLabels().get(j).getId())) {
					classDistributions.add(dataset.getAssignments().get(i).getLabels().get(j).getId());
					classDistributions.add(0);
				}
				for (int m = 0; m < classDistributions.size(); m += 2) {
					if (classDistributions.get(m) == dataset.getAssignments().get(i).getLabels().get(j).getId()) {
						frequency = classDistributions.get(m + 1);
						classDistributions.set(m + 1, ++frequency);
					}
				}
				if (!labelIds.contains(dataset.getAssignments().get(i).getLabels().get(j).getId())) {
					labelIds.add(dataset.getAssignments().get(i).getLabels().get(j).getId());
					numberOfUsedLabels--;
				}
			}
			// System.out.println("-> " + dataset.getAssignments().get(i).getLabels());
			// System.out.println("-> " + dataset.getAssignments().get(i).getUser());
			if (!userCompletenessPercentages.contains(dataset.getAssignments().get(i).getUser().getId())) {
				userCompletenessPercentages.add(dataset.getAssignments().get(i).getUser().getId());
				int userCompletenessPercentage = 100 - (numberOfUsedLabels * 100 / dataset.getLabels().size());
				userCompletenessPercentages.add(userCompletenessPercentage);
			}
		}
		// System.out.println("---> " + userCompletenessPercentages);
		// System.out.println("---> " + classDistributions);
		for (int k = 0; k < classDistributions.size(); k += 2) {
			frequency = classDistributions.get(k + 1);
			classDistributions.set(k + 1, frequency * 100 / totalNumberOfLabelAssignments);
			// System.out.println("-->> " + classDistributions.get(k+1) + "% " +
			// classDistributions.get(k));
		}

		for (int n = 0; n < userCompletenessPercentages.size(); n += 2) {
			// System.out.println("-->> " + "(" + userCompletenessPercentages.get(n) + ", "
			// + userCompletenessPercentages.get(n + 1) + "%)");
		}

		// System.out.println(completenessPercentage + " " + numberOfUsedLabels + " " +
		// totalNumberOfLabelAssignments);
		// System.out.println("-> " + labelIds);
		// System.out.println("--> " + classDistributions);
		int numberOfUsers = dataset.getUsers().size();
		// System.out.println(numberOfUsers);
	}	

	public static void exportInstanceMetrics(Dataset dataset) throws IOException {
		Map<String, Object> instanceMetricsMap = new LinkedHashMap<String, Object>();

		int frequency, max_frequency = 0;
		double entropy = 0.0;
		int total_number_of_label_assignments = 0, number_of_unique_label_assignments, number_of_unique_users,
				most_frequent_class_label = 0;
		ArrayList<Integer> unique_label_assignments = new ArrayList<Integer>();
		ArrayList<Integer> unique_users = new ArrayList<Integer>();
		ArrayList<Integer> class_labels_and_frequencies = new ArrayList<Integer>();

		for (int i = 0; i < dataset.getAssignments().size(); i++) {
			total_number_of_label_assignments += dataset.getAssignments().get(i).getLabels().size();
			for (int j = 0; j < dataset.getAssignments().get(i).getLabels().size(); j++) {
				if (!unique_label_assignments.contains(dataset.getAssignments().get(i).getLabels().get(j).getId())) {
					unique_label_assignments.add(dataset.getAssignments().get(i).getLabels().get(j).getId());
					class_labels_and_frequencies.add(dataset.getAssignments().get(i).getLabels().get(j).getId());
					class_labels_and_frequencies.add(0);
				}
				for (int m = 0; m < class_labels_and_frequencies.size(); m += 2) {
					if (class_labels_and_frequencies.get(m) == dataset.getAssignments().get(i).getLabels().get(j)
							.getId()) {
						frequency = class_labels_and_frequencies.get(m + 1);
						class_labels_and_frequencies.set(m + 1, ++frequency);
					}
				}
			}
			if (!unique_users.contains(dataset.getAssignments().get(i).getUser().getId())) {
				unique_users.add(dataset.getAssignments().get(i).getUser().getId());
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

		File targetFile = new File("CSE3063_Project\\instanceDataset" + dataset.getId() + ".json");
		targetFile.createNewFile();
		try (Writer writer = new FileWriter(targetFile.getAbsolutePath(), false)) {
			Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
			gson2.toJson(instanceMetricsMap, writer);

		}
	}

	public static void exportUserMetrics(User user, ArrayList<Dataset> datasets) throws Exception {
		Map<String, Object> userMetricsMap = new LinkedHashMap<String, Object>();
		int numberOfDatasets = 0;
		int totalNumberOfInstancesLabeled = 0;
		int totalNumberOfUniqueInstancesLabeled = 0;

		ArrayList<String> completenessPercentages = new ArrayList<String>();
		ArrayList<String> consistencyPercentages = new ArrayList<String>();
		ArrayList<ArrayList<LocalDateTime>> classLabelingDateTimes = new ArrayList<ArrayList<LocalDateTime>>();

		for (Dataset dataset : datasets) {
			ArrayList<Integer> tempInstanceIds = new ArrayList<Integer>();
			ArrayList<LocalDateTime> localDateTimes = new ArrayList<LocalDateTime>();
			double completenessPercentageCounter = 0;
			for (User datasetUser : dataset.getUsers()) {
				if (user.getId() == datasetUser.getId()) {
					numberOfDatasets++;
					break;
				}
			}
			for (Assignment assignment : dataset.getAssignments()) {
				if (user.getId() == assignment.getUser().getId()) {
					totalNumberOfInstancesLabeled++;
					if (!tempInstanceIds.contains(assignment.getInstance().getId())) {
						tempInstanceIds.add(assignment.getInstance().getId());
						totalNumberOfUniqueInstancesLabeled++;
						completenessPercentageCounter++;
					}
					localDateTimes.add(assignment.getDateTimeObject());
				}
			}
			classLabelingDateTimes.add(localDateTimes);
			double completenessPercentage = completenessPercentageCounter / dataset.getInstances().size() * 100;
			completenessPercentages.add("(" + dataset.getName() + ", " + completenessPercentage + "%)");
		}
		ArrayList<Double> labelingSeconds = new ArrayList<Double>();

		for (ArrayList<LocalDateTime> dateTimeList : classLabelingDateTimes) {
			for (int i = 1; i < dateTimeList.size(); i++) {
				double diff = Math.abs(dateTimeList.get(i - 1).getSecond() - dateTimeList.get(i).getSecond());
				labelingSeconds.add(diff);
			}
		}

		double total = 0;

		for (double number : labelingSeconds) {
			total += number;
		}
		double averageLabelingSeconds = total / labelingSeconds.size();
		double standardDeviation = calculateSD(labelingSeconds);

		userMetricsMap.put("number of datasets assigned", numberOfDatasets);
		userMetricsMap.put("total number of instances labeled", totalNumberOfInstancesLabeled);
		userMetricsMap.put("total number of unique instances labeled", totalNumberOfUniqueInstancesLabeled);
		userMetricsMap.put("average time spent in labeling an instance in seconds", averageLabelingSeconds);
		userMetricsMap.put("standard deviation of time in labeling an instance in seconds", standardDeviation);
		
		JSONArray consistencyPercentageArray = new JSONArray();

		for (String completenessPercentage : completenessPercentages) {
			Map<String, Object> consistencyPercentagesHashMap = new LinkedHashMap<String, Object>();
			consistencyPercentagesHashMap.put("consistency percentage", completenessPercentage);
			consistencyPercentageArray.add(consistencyPercentagesHashMap);
		}
		userMetricsMap.put("consistency percentages", consistencyPercentageArray);
		
		JSONArray completenessPercentageArray = new JSONArray();

		for (String completenessPercentage : completenessPercentages) {
			Map<String, Object> completenessPercentagesHashMap = new LinkedHashMap<String, Object>();
			completenessPercentagesHashMap.put("completeness percentage", completenessPercentage);
			completenessPercentageArray.add(completenessPercentagesHashMap);
		}
		userMetricsMap.put("completeness percentages", completenessPercentageArray);

		// Instantiate a new Gson instance.
		Gson gson = new Gson();

		// Convert the ordered map into an ordered string.
		String json = gson.toJson(userMetricsMap, Map.class);

		File targetFile = new File("CSE3063_Project\\user" + user.getId() + ".json");
		targetFile.createNewFile();
		try (Writer writer = new FileWriter(targetFile.getAbsolutePath(), false)) {
			Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
			gson2.toJson(userMetricsMap, writer);

		}
	}

	public static double calculateSD(ArrayList<Double> doubleArray) {
		double sum = 0.0, standardDeviation = 0.0;
		int size = doubleArray.size();

		for (double num : doubleArray) {
			sum += num;
		}

		double mean = sum / size;

		for (double num : doubleArray) {
			standardDeviation += Math.pow(num - mean, 2);
		}

		return Math.sqrt(standardDeviation / size);
	}
}
