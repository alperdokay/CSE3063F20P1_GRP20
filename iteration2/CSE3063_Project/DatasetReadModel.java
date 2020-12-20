package CSE3063_Project;

import java.util.ArrayList;

public class DatasetReadModel {
	private Integer id;

	private String name;

	private String filePath;

	private boolean isCurrentDataset;

	private ArrayList<User> users;

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

	public String toString() {
		return "Dataset{" + "Id=" + this.getId() + ", Name=" + this.getName() + ", filePath=" + this.getFilePath()
				+ ", isCurrentDataset=" + this.getCurrentDatasetStatus();
	}
}
