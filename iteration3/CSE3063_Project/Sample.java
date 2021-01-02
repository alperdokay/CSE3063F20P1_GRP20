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

		// importing old data sets
		for (DatasetReadModel model : datasets) {
			if (!model.getCurrentDatasetStatus()) {
				Dataset dataset = new Dataset().runDataset(model, allDatasets, logs);
				allDatasets.add(dataset);
			}
		}
		for (DatasetReadModel model : datasets) {
			if (model.getCurrentDatasetStatus()) {
				Dataset dataset = new Dataset().runDataset(model, allDatasets, logs);
				allDatasets.add(dataset);
			}
		}
		
		Scanner input = new Scanner(System.in);
		while (true) {
			Integer check = 0;
			System.out.print("Enter your username: ");
			String userName = input.nextLine();
			for (User user : users) {
				if (user.getType().equals("Human")) {
					if (userName.equals(user.getName())) {
						System.out.print("Enter your password: ");
						String userPassword = input.nextLine();
						if (userPassword.equals(user.getPassword())) {
							System.out.println("You did it :)"); //Bunu sonra silebiliriz
							check = 1;
						}
						else {
							System.out.println("Your password is wrong. Please, try again!!!");
							check = 2;
						}
					}
				}
			}
			if (check == 1)
				break;
			else if (check == 0)
				System.out.println("Your name is wrong. Please, try again!!!");
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
}
