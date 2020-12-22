package CSE3063_Project;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
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

		for (DatasetReadModel model : datasets) {
			runDataset(model);
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
			
			for (Object userId: userIds) {
				integerUserIds.add(Integer.parseInt(userId.toString()));
			}
			
			for(User user : users) {
				if (integerUserIds.contains(user.getId())) {
					datasetUsers.add(user);
				}
			}
			
			boolean isCurrentDataset = false;
			
			if (datasetId == currentDatasetId) {
				isCurrentDataset = true;
			}
			
			DatasetReadModel model = new DatasetReadModel(datasetId, datasetName, filePath, isCurrentDataset, datasetUsers);
			datasets.add(model);
		}
		
		return datasets;
	}
	public static Dataset runDataset(DatasetReadModel model) throws Exception {
		JSONParser parser = new JSONParser();

		Object inputObj = parser.parse(new FileReader("CSE3063_Project\\" + model.getFilePath()));
		JSONObject inputJsonObject = (JSONObject) inputObj;
		long maxlabels = (long) inputJsonObject.get("maximum number of labels per instance");
		JSONArray labels = (JSONArray) inputJsonObject.get("class labels");
		JSONArray instances = (JSONArray) inputJsonObject.get("instances");

		Dataset dataset = new Dataset(model.getId(), model.getName(), (int) maxlabels);
		DatasetAuditor datasetAuditor = new DatasetAuditor("INFO", "created", dataset);
		logs.add(datasetAuditor);
		System.out.println(datasetAuditor.getLogMessage());

		for (Object label : labels) {
			JSONObject labelObject = (JSONObject) label;
			String text = labelObject.get("label text").toString();
			Label newLabel = new Label(Integer.parseInt(labelObject.get("label id").toString()), text);

			LabelProvider labelProvider = new LabelProvider("INFO", "created", newLabel);
			logs.add(labelProvider);
			System.out.println(labelProvider.getLogMessage());

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
			
			for (int i = 0; i < dataset.getUsers().size(); i++) {
				for (int j = 0; j < dataset.getInstances().size(); j++) {
					User user = dataset.getUsers().get(i);
					Instance instance = dataset.getInstances().get(j);
					Assignment assignment = randomMechanism.label(instance, user, dataset.getLabels(),
							dataset.getInstanceLabellingLimit());
					dataset.addAssignment(assignment);
					InstanceTagger tagger = new InstanceTagger("INFO", "created", assignment);
					logs.add(tagger);
					System.out.println(tagger.getLogMessage());
					exportInstanceMetrics(dataset);
					exportDatasetMetrics(dataset);
				}
			}
		}
		
		return dataset;
	}

	public static void exportDatasetMetrics(Dataset dataset) throws IOException {
		Map<String, Object> datasetMetricsMap = new LinkedHashMap<String, Object>();
		
		ArrayList<Integer> labelIds = new ArrayList<>();
		ArrayList<Integer> classDistributions = new ArrayList<>();
		ArrayList<Integer> userCompletenessPercentages = new ArrayList<>();
		ArrayList<Integer> userConsistencyPercentages = new ArrayList<>();
		ArrayList<Integer> userLabelIds = new ArrayList<>();
		ArrayList<Integer> uniqueInstances = new ArrayList<>();
		int numberOfUsedLabels = dataset.getLabels().size(), frequency, totalNumberOfLabelAssignments = 0, counterUser=1;

		for (Assignment assignment : dataset.getAssignments()) {
			for (Label label : assignment.getLabels()) {
				if (!labelIds.contains(label.getId())) {
					labelIds.add(label.getId());
					numberOfUsedLabels--;
				}
			}
		}
		
		double completenessPercentage = 100 - (numberOfUsedLabels / dataset.getLabels().size() * 100);
		datasetMetricsMap.put("completeness percentage", completenessPercentage);
		
		for (int a = 0; a < dataset.getLabels().size(); a++) {
			userLabelIds.add(dataset.getLabels().get(a).getId());
		}
		//System.out.println("--> " + userLabelIds);
		//System.out.println("--> " + labelIds);
		labelIds.removeAll(labelIds);
		
		for (int i = 0; i < dataset.getAssignments().size(); i++) {
			numberOfUsedLabels = dataset.getAssignments().get(i).getLabels().size();
			totalNumberOfLabelAssignments += dataset.getAssignments().get(i).getLabels().size();
			for (int j = 0; j < dataset.getAssignments().get(i).getLabels().size(); j++) {
				if (!classDistributions.contains(dataset.getAssignments().get(i).getLabels().get(j).getId())) {
					classDistributions.add(dataset.getAssignments().get(i).getLabels().get(j).getId());
					uniqueInstances.add(dataset.getAssignments().get(i).getLabels().get(j).getId());
					classDistributions.add(0);
					uniqueInstances.add(0);
				}
				for (int m = 0; m < classDistributions.size(); m += 2) {
					if (classDistributions.get(m) == dataset.getAssignments().get(i).getLabels().get(j)
							.getId()) {
						frequency = classDistributions.get(m + 1);
						classDistributions.set(m + 1, ++frequency);
					}
					if(uniqueInstances.get(m) == dataset.getAssignments().get(i).getLabels().get(j)
							.getId()) {
						if(!uniqueInstances.contains(dataset.getAssignments().get(i).getInstance().getId())) {
							frequency = uniqueInstances.get(m + 1);
							uniqueInstances.set(m + 1, ++frequency);
						}
					}
				}
				if (!labelIds.contains(dataset.getAssignments().get(i).getLabels().get(j).getId())) {
					labelIds.add(dataset.getAssignments().get(i).getLabels().get(j).getId());
					numberOfUsedLabels--;
				}
			}
			//System.out.println("-> " + dataset.getAssignments().get(i).getLabels());
			//System.out.println("-> " + dataset.getAssignments().get(i).getUser());
			if (!userCompletenessPercentages.contains(dataset.getAssignments().get(i).getUser().getId())) {
				userCompletenessPercentages.add(dataset.getAssignments().get(i).getUser().getId());
				int userCompletenessPercentage = 100 - (numberOfUsedLabels * 100 / dataset.getLabels().size());
				userCompletenessPercentages.add(userCompletenessPercentage);
				//System.out.println("* " + labelIds);
			}
			if(counterUser == dataset.getAssignments().get(i).getUser().getId()) {
				for (int a = 0; a < dataset.getAssignments().get(i).getLabels().size(); a++) {
					userLabelIds.add(dataset.getAssignments().get(i).getLabels().get(a).getId());
				}
				
			}
			else {
				userLabelIds.removeAll(userLabelIds);
				counterUser++;
			}
			//System.out.println("-->> " + userLabelIds);
		}
		//System.out.println("---> " + userCompletenessPercentages);
		//System.out.println("---> " + classDistributions);
		//System.out.println("---> " + uniqueInstances);
		
		JSONArray classDistributionsBased = new JSONArray();
		JSONArray uniqueInstancesClassLabel = new JSONArray();
		
		for (int k = 0; k < classDistributions.size(); k += 2) {
			frequency = classDistributions.get(k + 1);
			classDistributions.set(k + 1, frequency * 100 / totalNumberOfLabelAssignments);
			Map<String, Object> classDistributionsHashMap = new LinkedHashMap<String, Object>();
			Map<String, Object> uniqueInstancesHashMap = new LinkedHashMap<String, Object>();
			classDistributionsHashMap.put("class distribution based on final instance label",
					classDistributions.get(k+1) + "% " + classDistributions.get(k));
			uniqueInstancesHashMap.put("class label and number of unique instances",
					uniqueInstances.get(k) + " and " + uniqueInstances.get(k+1));
			classDistributionsBased.add(classDistributionsHashMap);
			uniqueInstancesClassLabel.add(uniqueInstancesHashMap);
		}
		datasetMetricsMap.put("class distributions based on final instance labels",
				classDistributionsBased);
		datasetMetricsMap.put("number of unique instances for each class label",
				uniqueInstancesClassLabel);
		
		int numberOfUsers = dataset.getUsers().size();
		datasetMetricsMap.put("number of users", numberOfUsers);
		
		JSONArray usersAndCompletenessPercentages = new JSONArray();
		
		for(int n = 0; n < userCompletenessPercentages.size(); n += 2) {
			Map<String, Object> usersAndCompletenessPercentagesHashMap = new LinkedHashMap<String, Object>();
			usersAndCompletenessPercentagesHashMap.put("user and completeness percentage",
					"(" + userCompletenessPercentages.get(n) + ", " + userCompletenessPercentages.get(n + 1) + "%)");
			usersAndCompletenessPercentages.add(usersAndCompletenessPercentagesHashMap);
		}
		datasetMetricsMap.put("users assigned and their completeness percentage",
				usersAndCompletenessPercentages);
		
		//System.out.println("-> " + labelIds);
		//System.out.println("--> " + classDistributions);
		
		// Instantiate a new Gson instance.
		Gson gson = new Gson();

		// Convert the ordered map into an ordered string.
		String json = gson.toJson(datasetMetricsMap, Map.class);

		File targetFile = new File("CSE3063_Project\\DatasetMetricsfordataset" + dataset.getId() + ".json");
		targetFile.createNewFile();
		try (Writer writer = new FileWriter(targetFile.getAbsolutePath(), false)) {
			Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
			gson2.toJson(datasetMetricsMap, writer);

		}
		
	}

	public static void exportInstanceMetrics(Dataset dataset) throws IOException {
		Map<String, Object> instanceMetricsMap = new LinkedHashMap<String, Object>();
		/*
		System.out.println("-> " + dataset.getAssignments());
		System.out.println("-> " + dataset.getAssignments().get(0).getInstance());
		System.out.println("-> " + dataset.getAssignments().get(0).getLabels());
		System.out.println("-->> " + dataset.getInstances());
		System.out.println("--->>> " + dataset.getLabels());
		System.out.println("---->>>> " + dataset.getUsers());
		*/
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

		File targetFile = new File("CSE3063_Project\\dataset" + dataset.getId() + ".json");
		targetFile.createNewFile();
		try (Writer writer = new FileWriter(targetFile.getAbsolutePath(), false)) {
			Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
			gson2.toJson(instanceMetricsMap, writer);

		}

	}
}
