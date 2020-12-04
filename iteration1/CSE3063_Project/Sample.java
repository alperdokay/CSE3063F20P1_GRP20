package CSE3063_Project;

import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
				Assignment assignment = randomMechanism.label(instance, user, dataset1.getLabels(), dataset1.getInstanceLabellingLimit());
				dataset1.addAssignment(assignment);
				System.out.println(assignment.getDateTime());
				InstanceTagger tagger = new InstanceTagger("INFO", "created", assignment);
				logs.add(tagger);
				System.out.println(tagger.getLogMessage());
			}
		}
		for (int i = 0; i < dataset2.getUsers().size(); i++) {
			for (int j = 0; j < dataset2.getInstances().size(); j++) {
				User user = dataset2.getUsers().get(i);
				Instance instance = dataset2.getInstances().get(j);
				Assignment assignment = randomMechanism.label(instance, user, dataset2.getLabels(), dataset2.getInstanceLabellingLimit());
				dataset2.addAssignment(assignment);
				InstanceTagger tagger = new InstanceTagger("INFO", "created", assignment);
				logs.add(tagger);
				System.out.println(tagger.getLogMessage());
			}
		}
		dataset1.exportOutput();
		dataset2.exportOutput();
	}

	public static Dataset createDataset(String fileName, int numberOfUsers) throws Exception {
		JSONParser parser = new JSONParser();
		
		Object confObj = parser.parse(new FileReader("CSE3063_Project\\conf.json", StandardCharsets.UTF_8));
		JSONObject confJsonObject = (JSONObject) confObj;
		JSONArray userNames = (JSONArray) confJsonObject.get("userNames");
		JSONArray userTypes = (JSONArray) confJsonObject.get("userTypes");
		
		Object inputObj = parser.parse(new FileReader(
				"CSE3063_Project\\" + fileName));
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
			Label newLabel = new Label(Integer.parseInt(labelObject.get("label id").toString()),
					text);
			
			LabelProvider labelProvider = new LabelProvider("INFO", "created", newLabel);
			logs.add(labelProvider);
			System.out.println(labelProvider.getLogMessage());
			
			dataset.addLabel(newLabel);
		}

		for (Object instance : instances) {
			JSONObject instanceObject = (JSONObject) instance;
			String text = instanceObject.get("instance").toString();
			
			Instance newInstance = new Instance(Integer.parseInt(instanceObject.get("id").toString()),
					text);

			dataset.addInstance(newInstance);
		}
		
		Random random = new Random();
		for (int i = 0; i < numberOfUsers; i++) {
			int nameIndex = random.nextInt(userNames.size());
			int typeIndex = random.nextInt(userTypes.size());
			User user = new User(i+1, userNames.get(nameIndex).toString() + (i+1), userTypes.get(typeIndex).toString());
			UserManager userManager = new UserManager("INFO", "created", user);
			logs.add(userManager);
			System.out.println(userManager.getLogMessage());
			dataset.addUser(user);
		}
		return dataset;
	}

}
