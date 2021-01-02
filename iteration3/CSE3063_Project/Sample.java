package CSE3063_Project;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.*;

/*
import java.io.FileReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
*/
public class Sample {

	public static void main(String[] args) throws Exception {
		ArrayList<Logger> logs = new ArrayList<Logger>();
		ArrayList<User> users = new User().getUsers(logs);
		ArrayList<DatasetReadModel> datasets = new DatasetReadModel().getDatasets(users);
		ArrayList<Dataset> allDatasets = new ArrayList<Dataset>();
		Dataset currentDataset = new Dataset();
		// importing old data sets
		for (DatasetReadModel model : datasets) {
			Dataset dataset = new Dataset().runDataset(model, allDatasets, logs);
			allDatasets.add(dataset);
			if (model.getCurrentDatasetStatus()) {
				currentDataset = dataset;
			}
		}

		boolean isLogon = false;
		User humanUser = new User();
		Scanner input = new Scanner(System.in);
		while (!isLogon) {

			System.out.print("Enter your username: ");
			String userName = input.nextLine();
			System.out.print("Enter your password: ");
			String userPassword = input.nextLine();

			if (userName == "" && userPassword == "") {
				break;
			}

			for (User user : users) {
				if (user.getType().equals("Human")) {
					if (userName.equals(user.getName()) && userPassword.equals(user.getPassword())) {
						isLogon = true;
						humanUser = user;
						break;
					}
				}
			}
			System.out.println("The user could not found. Please, try again!!!");
		}

		currentDataset.labelDataset(logs, humanUser);
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
}
