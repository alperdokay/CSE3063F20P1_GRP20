package CSE3063_Project;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class User {
	private Integer id;

	private String name;

	private String password;

	private String type;

	private double consistencyCheckProbability;
	
	public User() {
		this.id = 0;
		this.name = "System";
		this.type = "System User";
		this.consistencyCheckProbability = 0;
	}
	
	public User(Integer id, String name, String type, double consistencyCheckProbability) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.consistencyCheckProbability = consistencyCheckProbability;
	}
	
	public User(Integer id, String name, String type, double consistencyCheckProbability, String password) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.consistencyCheckProbability = consistencyCheckProbability;
		this.password = password;
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

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getConsistencyCheckProbability() {
		return this.consistencyCheckProbability;
	}

	public void setConsistencyCheckProbability(double consistencyCheckProbability) {
		this.consistencyCheckProbability = consistencyCheckProbability;
	}
	
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public ArrayList<User> getUsers(ArrayList<Logger> logs) throws Exception{
		ArrayList<User> users = new ArrayList<User>();

		JSONParser parser = new JSONParser();
		Object confObj = parser.parse(new FileReader("CSE3063_Project\\newConf.json", StandardCharsets.UTF_8));
		JSONObject confJsonObject = (JSONObject) confObj;
		
		JSONArray userPwdsArray = (JSONArray) confJsonObject.get("userPwds");

		JSONArray userArray = (JSONArray) confJsonObject.get("users");

		for (Object user : userArray) {
			JSONObject userObject = (JSONObject) user;

			Integer userId = Integer.parseInt(userObject.get("id").toString());
			String userName = userObject.get("userName").toString();
			String userType = userObject.get("userType").toString();
			double consistencyCheckProbability = Double
					.parseDouble(userObject.get("consistencyCheckProbability").toString());

			if (userType.equals("Human")) {
				for (Object userPwd : userPwdsArray) {
					JSONObject userPwdObject = (JSONObject) userPwd;

					Integer userPwdId = Integer.parseInt(userPwdObject.get("id").toString());
					String userPwdName = userPwdObject.get("userName").toString();
					String userPassword = userPwdObject.get("pwd").toString();
					if (userId == userPwdId) {
						User newUser = new User(userId, userName, userType, consistencyCheckProbability,
								userPassword);
						UserManager userManager = new UserManager("INFO", "created", newUser);
						logs.add(userManager);
						System.out.println(userManager.getLogMessage());
						users.add(newUser);
					}
				}
			}
			else {
				User newUser = new User(userId, userName, userType, consistencyCheckProbability);
				UserManager userManager = new UserManager("INFO", "created", newUser);
				logs.add(userManager);
				System.out.println(userManager.getLogMessage());
				users.add(newUser);
			}
		}

		return users;
	}

	public void exportUserMetrics(ArrayList<Dataset> datasets) throws IOException {
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
				if (this.getId() == datasetUser.getId()) {
					numberOfDatasets++;
					break;
				}
			}
			for (Assignment assignment : dataset.getAssignments()) {
				if (this.getId() == assignment.getUser().getId()) {
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
		double standardDeviation = this.calculateSD(labelingSeconds);

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

		File targetFile = new File("CSE3063_Project\\user" + this.getId() + ".json");
		targetFile.createNewFile();
		try (Writer writer = new FileWriter(targetFile.getAbsolutePath(), false)) {
			Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
			gson2.toJson(userMetricsMap, writer);

		}
	}
	
	public double calculateSD(ArrayList<Double> doubleArray) {
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
	
	@Override
	public String toString() {
		return "user id: " + this.getId() + ", user name: " + this.getName() + ", user type: " + this.getType()
				+ ", consistency check probability: " + this.getConsistencyCheckProbability();
	}
}
