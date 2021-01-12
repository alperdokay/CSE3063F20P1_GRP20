package CSE3063_Project;

import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class DatasetReadModel {
	private Integer id;

	private String name;

	private String filePath;

	private boolean isCurrentDataset;

	private ArrayList<User> users;
	
	public DatasetReadModel() {
		this.id = 0;
		this.name = "System Model";
		this.isCurrentDataset = false;
	}

	public DatasetReadModel(Integer id, String name, String filePath, boolean isCurrentDataset, ArrayList<User> users) {
		this.id = id;
		this.name = name;
		this.filePath = filePath;
		this.isCurrentDataset = isCurrentDataset;
		this.users = users;
	}

	public DatasetReadModel(Integer id, String name, boolean isCurrentDataset, String filePath) {
		this.id = id;
		this.name = name;
		this.filePath = filePath;
		this.isCurrentDataset = isCurrentDataset;
		this.users = new ArrayList<User>();
	}

	public Integer getId() {
		return this.id;
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

	public String getFilePath() {
		return this.filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public boolean getCurrentDatasetStatus() {
		return this.isCurrentDataset;
	}

	public void setCurrentDatasetStatus(boolean isCurrentDataset) {
		this.isCurrentDataset = isCurrentDataset;
	}

	public ArrayList<User> getUsers() {
		return this.users;
	}

	public void addUser(User user) {
		this.users.add(user);
	}
	
	public ArrayList<DatasetReadModel> getDatasets(ArrayList<User> users) throws Exception {
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

	public String toString() {
		return "Dataset{" + "Id=" + this.getId() + ", Name=" + this.getName() + ", filePath=" + this.getFilePath()
				+ ", isCurrentDataset=" + this.getCurrentDatasetStatus();
	}
}
