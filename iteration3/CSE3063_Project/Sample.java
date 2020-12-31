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
