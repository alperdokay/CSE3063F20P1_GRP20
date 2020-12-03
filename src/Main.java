import java.io.*;
import java.text.Normalizer;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import java.util.ArrayList;
import java.lang.*;

import java.io.FileWriter;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("/Users/nurhandeakyuz/Downloads/CES3063F20_LabelingProject_Input-1.json"));
            JSONObject jsonObject = (JSONObject)obj;
            long datasetid = (long) jsonObject.get("dataset id");
            String datasetname = (String)jsonObject.get("dataset name");
            long maxlabels = (long)jsonObject.get("maximum number of labels per instance");
            JSONArray labels = (JSONArray)jsonObject.get("class labels");
            JSONArray instances = (JSONArray)jsonObject.get("instances");

            ArrayList<Label> all_labels = new ArrayList<Label>();
            ArrayList<Instance> all_instances = new ArrayList<Instance>(); //turkish alphabets
            ArrayList<Instance> allinstances = new ArrayList<Instance>(); //normalizing turkish alphabets
            ArrayList<String> keywordsPositive = new ArrayList<String>();
            ArrayList<String> keywordsNegative = new ArrayList<String>();
            ArrayList<User> all_users = new ArrayList<User>();

            Dataset dataset = new Dataset((int)datasetid,datasetname,(int)maxlabels);
            dataset.setId((int) datasetid);
            dataset.setName(datasetname);
            dataset.setInstanceLabelingLimit((int)maxlabels);
            dataset.addLabel(labels,all_labels);
            dataset.addInstance(instances,all_instances);

            User user1 = new User(1,"RandomLabelingMechanism", null);
            dataset.addUser(user1,all_users);

            for(int i = 0 ;i < all_instances.size(); i++){
                String text = all_instances.get(i).getInstance();
                text = Normalizer.normalize(text, Normalizer.Form.NFD);
                text = text.replaceAll("[^\\p{ASCII}]", "");
                text = text.replaceAll("\\p{M}", "");
                text = text.toLowerCase();
                Instance in = new Instance(i,text);
                allinstances.add(in);
            }

            //Adding key words
            keywordsPositive.add("gzel");
            keywordsPositive.add("memnun");
            keywordsPositive.add("iyi");
            keywordsPositive.add("zverili");
            keywordsPositive.add("arts");
            keywordsPositive.add("basarili");
            keywordsNegative.add("yoktu");
            keywordsNegative.add("hasarlyd");
            keywordsNegative.add("almyor");
            keywordsNegative.add("bitiriyor");

            ArrayList<Integer> classLabelIDS = new ArrayList<>();
            for(int i = 0; i<all_labels.size();i++){
                int t = all_labels.get(i).getId();
                classLabelIDS.add(t);
            }
            ArrayList<HashMap> maps = new ArrayList<HashMap>();
            for (int i = 0; i < allinstances.size(); i++) {
                Assignment assignment = new Assignment(allinstances.get(i).getId(), user1.getId(), classLabelIDS);
                int result = assignment.addLabel(allinstances.get(i).getInstance(), keywordsPositive, keywordsNegative);
                dataset.addAssignment(assignment);

                maps = dataset.exportOutput(allinstances,result,user1);
            }

            JSONObject jsonObject1 = new JSONObject();

            jsonObject1.put("dataset id ", dataset.getId());
            jsonObject1.put("dataset name ", dataset.getName());
            jsonObject1.put("maximum number of labels per instance ", dataset.getInstanceLabelingLimit());

            jsonObject1.put("class labels ", dataset.getLabels());
            jsonObject1.put("class label assignment", maps);

            jsonObject1.put("instances ", dataset.getInstances());
            jsonObject1.put("users ", dataset.getUsers());





            FileWriter fileWriter = new FileWriter("/Users/nurhandeakyuz/Downloads/output.json");
            fileWriter.write(jsonObject1.toJSONString());   //writing JSON object to a file
            fileWriter.flush();


        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}



