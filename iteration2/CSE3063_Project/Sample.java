package CSE3063_Project;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

import java.text.DecimalFormat;

public class Sample {
	static ArrayList<Logger> logs = new ArrayList<Logger>();

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		int numberOfUsers = 2;
		KeywordLabelingMechanism keywordMechanism = new KeywordLabelingMechanism();
		RandomLabelingMechanism randomMechanism = new RandomLabelingMechanism();

		keywordMechanism.addKeyword("güzel");
		keywordMechanism.addKeyword("etkili");
		keywordMechanism.addKeyword("harika");
		keywordMechanism.addKeyword("paket");
		keywordMechanism.addKeyword("numara");

		Dataset dataset1 = createDataset("CES3063F20_LabelingProject_Input-1.json", numberOfUsers);
		Dataset dataset2 = createDataset("CES3063F20_LabelingProject_Input-2.json", numberOfUsers);

		for (int i = 0; i < dataset1.getUsers().size(); i++) {
			for (int j = 0; j < dataset1.getInstances().size(); j++) {
				User user = dataset1.getUsers().get(i);
				Instance instance = dataset1.getInstances().get(j);
				Assignment assignment = randomMechanism.label(instance, user, dataset1.getLabels(),
						dataset1.getInstanceLabellingLimit());
				dataset1.addAssignment(assignment);
				InstanceTagger tagger = new InstanceTagger("INFO", "created", assignment);
				logs.add(tagger);
				System.out.println(tagger.getLogMessage());
			}
		}
		for (int i = 0; i < dataset2.getUsers().size(); i++) {
			for (int j = 0; j < dataset2.getInstances().size(); j++) {
				User user = dataset2.getUsers().get(i);
				Instance instance = dataset2.getInstances().get(j);
				Assignment assignment = randomMechanism.label(instance, user, dataset2.getLabels(),
						dataset2.getInstanceLabellingLimit());
				dataset2.addAssignment(assignment);
				InstanceTagger tagger = new InstanceTagger("INFO", "created", assignment);
				logs.add(tagger);
				System.out.println(tagger.getLogMessage());
			}
		}
		dataset1.exportOutput();
		dataset2.exportOutput();
		exportInstanceMetrics(dataset1);
		exportInstanceMetrics(dataset2);
		exportDatasetMetrics(dataset1);
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

	public static Dataset createDataset(String fileName, int numberOfUsers) throws Exception {
		JSONParser parser = new JSONParser();

		Object confObj = parser.parse(new FileReader("CSE3063_Project\\conf.json", StandardCharsets.UTF_8));
		JSONObject confJsonObject = (JSONObject) confObj;
		JSONArray userNames = (JSONArray) confJsonObject.get("userNames");
		JSONArray userTypes = (JSONArray) confJsonObject.get("userTypes");

		Object inputObj = parser.parse(new FileReader("CSE3063_Project\\" + fileName));
		JSONObject inputJsonObject = (JSONObject) inputObj;
		long datasetId = (long) inputJsonObject.get("dataset id");
		String datasetName = (String) inputJsonObject.get("dataset name");
		long maxlabels = (long) inputJsonObject.get("maximum number of labels per instance");
		JSONArray labels = (JSONArray) inputJsonObject.get("class labels");
		JSONArray instances = (JSONArray) inputJsonObject.get("instances");

		Dataset dataset = new Dataset((int) datasetId, datasetName, (int) maxlabels);
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

		Random random = new Random();
		for (int i = 0; i < numberOfUsers; i++) {
			int nameIndex = random.nextInt(userNames.size());
			int typeIndex = random.nextInt(userTypes.size());
			User user = new User(i + 1, userNames.get(nameIndex).toString() + (i + 1),
					userTypes.get(typeIndex).toString(), 0.1);
			UserManager userManager = new UserManager("INFO", "created", user);
			logs.add(userManager);
			System.out.println(userManager.getLogMessage());
			dataset.addUser(user);
		}
		return dataset;
	}
	
	public static void exportDatasetMetrics(Dataset dataset) {
		ArrayList<Integer> labelIds = new ArrayList<>();
		int numberOfUsedLabels = dataset.getLabels().size();
		
		for (Assignment assignment: dataset.getAssignments()) {
			for (Label label: assignment.getLabels()) {
				if (!labelIds.contains(label.getId())) {
					labelIds.add(label.getId());
					numberOfUsedLabels--;
				}
			}
		}
		double completenessPercentage = 100 - (numberOfUsedLabels / dataset.getLabels().size() * 100);
		System.out.println(completenessPercentage);
		int numberOfUsers = dataset.getUsers().size();
		System.out.println(numberOfUsers);
	}
	
	public static void exportInstanceMetrics(Dataset dataset) throws IOException {
		Map<String, Object> instanceMetricsMap = new LinkedHashMap<String, Object>();
		
		int frequency, max_frequency=0;
		double entropy=0.0;
		int total_number_of_label_assignments=0, number_of_unique_label_assignments, number_of_unique_users,
				most_frequent_class_label=0;
		ArrayList<Integer> unique_label_assignments = new ArrayList<Integer>();
		ArrayList<Integer> unique_users = new ArrayList<Integer>();
		ArrayList<Integer> class_labels_and_frequencies = new ArrayList<Integer>();
		
		for(int i = 0; i < dataset.getAssignments().size(); i++) {
			total_number_of_label_assignments += dataset.getAssignments().get(i).getLabels().size();
			for(int j = 0; j < dataset.getAssignments().get(i).getLabels().size(); j++) {
				if(!unique_label_assignments.contains(dataset.getAssignments().get(i).getLabels().get(j).getId())) {
					unique_label_assignments.add(dataset.getAssignments().get(i).getLabels().get(j).getId());
					class_labels_and_frequencies.add(dataset.getAssignments().get(i).getLabels().get(j).getId());
					class_labels_and_frequencies.add(0);
				}
				for(int m = 0; m < class_labels_and_frequencies.size(); m+=2) {
					if(class_labels_and_frequencies.get(m) == dataset.getAssignments().get(i).getLabels().get(j).getId()) {
						frequency = class_labels_and_frequencies.get(m + 1);
						class_labels_and_frequencies.set(m + 1, ++frequency);
					}
				}
			}
			if(!unique_users.contains(dataset.getAssignments().get(i).getUser().getId())) {
				unique_users.add(dataset.getAssignments().get(i).getUser().getId());
			}
		}
		
		for(int k = 0; k < class_labels_and_frequencies.size(); k+=2) {
			frequency = class_labels_and_frequencies.get(k + 1);
			class_labels_and_frequencies.set(k + 1, frequency * 100 / total_number_of_label_assignments);
			frequency = class_labels_and_frequencies.get(k + 1);
			if(max_frequency < frequency) {
				most_frequent_class_label = class_labels_and_frequencies.get(k);
				max_frequency = frequency;
			}
		}
		
		number_of_unique_label_assignments = unique_label_assignments.size();
		number_of_unique_users = unique_users.size();
		
		instanceMetricsMap.put("total number of label assignments", total_number_of_label_assignments);
		instanceMetricsMap.put("number of unique label assignments", number_of_unique_label_assignments);
		instanceMetricsMap.put("number of unique users", number_of_unique_users);
		instanceMetricsMap.put("most frequent class label and percentage", "(" + most_frequent_class_label + ", " + max_frequency
				+ "%)");
		
		JSONArray class_labels_and_percentages = new JSONArray();
		
		for(int n = 0; n < class_labels_and_frequencies.size(); n+=2) {
			frequency = class_labels_and_frequencies.get(n + 1);
			Map<String, Object> classLabelsAndPercentagesHashMap = new LinkedHashMap<String, Object>();
			classLabelsAndPercentagesHashMap.put("class label and percentage", "(" + class_labels_and_frequencies.get(n) + ", "
					+ frequency + "%)");
			class_labels_and_percentages.add(classLabelsAndPercentagesHashMap);
			entropy += (-1) * (frequency / 100.0) * (Math.log10(frequency / 100.0) /
					Math.log10(number_of_unique_label_assignments));
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
