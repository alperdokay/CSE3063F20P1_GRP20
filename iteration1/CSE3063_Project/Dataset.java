package CSE3063_Project;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
		
		File outputFile = new File(this.getName() + ".json");
		outputFile.createNewFile();
		
		Map<String,String> myLinkedHashMap = new LinkedHashMap<String, String>();

        // Add items, in-order, to the map.
        myLinkedHashMap.put("dataset id", this.getId().toString());
        myLinkedHashMap.put("dataset name", this.getName());
        myLinkedHashMap.put("maximum number of labels per instance", this.getInstanceLabellingLimit().toString());
        
        JSONArray labels = new JSONArray();
        
        for (Label label: this.getLabels()) {
        	Map<String,Object> labelHashMap = new LinkedHashMap<String, Object>();
        	labelHashMap.put("label id", label.getId());
        	labelHashMap.put("label text", label.getText());
        	
        	labels.add(labelHashMap);
        }
        myLinkedHashMap.put("class labels", labels.toString());
        
        JSONArray instances = new JSONArray();
        
        for (Instance instance: this.getInstances()) {
        	JSONObject instanceObject = new JSONObject();
        	instanceObject.put("id", instance.getId());
        	instanceObject.put("instance", instance.getInstance());
        	
        	instances.add(instanceObject);
        }
        myLinkedHashMap.put("instances", instances.toString());
        
        JSONArray assignments = new JSONArray();
        
        for (Assignment assignment: this.getAssignments()) {
        	JSONObject assignmentObject = new JSONObject();
        	assignmentObject.put("instance id", assignment.getInstanceId());
        	assignmentObject.put("class label ids", assignment.getLabelIds());
        	assignmentObject.put("user id", assignment.getUserId());
        	assignmentObject.put("datetime", assignment.getDateTime());
        	
        	assignments.add(assignmentObject);
        }
        myLinkedHashMap.put("class label assignments", assignments.toString());
        
        JSONArray users = new JSONArray();
        
        for (User user: this.getUsers()) {
        	JSONObject userObject = new JSONObject();
        	userObject.put("user id", user.getId());
        	userObject.put("user name", user.getName());
        	userObject.put("user type", user.getType());
        	
        	users.add(userObject);
        }
        myLinkedHashMap.put("users", users.toString());
        // Instantiate a new Gson instance.
        Gson gson = new Gson();

        // Convert the ordered map into an ordered string.
        String json = gson.toJson(myLinkedHashMap, Map.class);


		String jsonFormattedString = json.replaceAll("\\\\", "");
        File targetFile = new File(this.name+".json");
        targetFile.createNewFile();
		try (Writer writer = new FileWriter(targetFile.getAbsolutePath(),false)) {
            Gson gson2 = new GsonBuilder().create();
            gson2.toJson(myLinkedHashMap, writer);

        }
//        gson.toJson(myLinkedHashMap, new FileWriter("C:\\Users\\Alper\\eclipse-workspace\\cse1142\\CSE3063_Project\\output.json"));
       
        // Print ordered string.
        System.out.println(json);
		return true;
	}
}
