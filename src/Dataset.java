package com.company;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Dataset {
    private int id;
    private String name;
    private int instanceLabelingLimit;
    private ArrayList<Label> labels;
    private ArrayList<Instance> instances;
    private ArrayList<User> users;
    private ArrayList<Assignment> assignments;

    public Dataset(int id, String name, int quantity){
        this.id = id;
        this.name = name;
    }
    public ArrayList<HashMap> exportOutput(ArrayList<Instance>allinstances, int result, User user){
        ArrayList<HashMap> map = new ArrayList<HashMap>();

        for(int i = 0; i< allinstances.size(); i++){
            HashMap hash = new HashMap();
            hash.put("instance id", (allinstances.get(i).getId()+1));
            hash.put("class label ids", result);
            hash.put("user id", user.getId());
            hash.put("datetime", LocalDateTime.now());
            map.add(hash);
        }
        return map;
    }
    public ArrayList<Label> addLabel(JSONArray labelS, ArrayList<Label> allLabels){
        for (Object label : labelS){
            JSONObject jsonObject = (JSONObject) label;
            Map<String,String> mapLabel = new HashMap <>(jsonObject.size());
            for (Object jsonEntry : jsonObject.entrySet()){
                Map.Entry <?,?> entry = (Map.Entry <?,?>) jsonEntry;
                mapLabel.put(entry.getKey().toString(), entry.getValue().toString());
            }

            Label label1 = new Label(Integer.parseInt((String) mapLabel.values().toArray()[0]), (String) mapLabel.values().toArray()[1]);
            assert false;
            allLabels.add(label1);
        }
        this.labels = allLabels;
        return allLabels;
    }
    public void setInstanceLabelingLimit(int limit){

        this.instanceLabelingLimit = limit;
    }
    public ArrayList<Instance> addInstance(JSONArray instanceS, ArrayList<Instance> allInstances){
        for (Object instance : instanceS){
            JSONObject jsonObject = (JSONObject) instance;
            Map<String,String> mapInstance = new HashMap <>(jsonObject.size());
            for (Object jsonEntry : jsonObject.entrySet()){
                Map.Entry <?,?> entry = (Map.Entry <?,?>) jsonEntry;
                mapInstance.put(entry.getKey().toString(), entry.getValue().toString());
            }

            Instance instance1 = new Instance((Integer.parseInt((String) mapInstance.values().toArray()[1])), mapInstance.values().toArray()[0].toString());
            assert false;
            allInstances.add(instance1);
        }
        this.instances = allInstances;
        return allInstances;
    }
    public ArrayList<Assignment> addAssignment(Assignment assignment){
        assignments = new ArrayList<>();
        assignments.add(assignment);
        return assignments;
    }
    public ArrayList<User> addUser(User user, ArrayList<User> allUsers){
        allUsers.add(user);
        this.users = allUsers;
        return allUsers;
    }
    public int getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public int getInstanceLabelingLimit(){
        return instanceLabelingLimit;
    }
    public ArrayList<HashMap> getLabels(){
        ArrayList<HashMap> hashmapLabel = new ArrayList<HashMap>();
        for (int i = 0; i < labels.size(); i++){
            HashMap hashLabel = new HashMap();
            hashLabel.put("label id", labels.get(i).getId());
            hashLabel.put("label text", labels.get(i).getText());
            hashmapLabel.add(hashLabel);
        }
        return hashmapLabel;
    }
    public ArrayList<HashMap> getInstances(){
        ArrayList<HashMap> hashmapInstance = new ArrayList<HashMap>();
        for (int i = 0; i < instances.size(); i++){
            HashMap hashInstance = new HashMap();
            hashInstance.put("instance id", instances.get(i).getId());
            hashInstance.put("instance", instances.get(i).getInstance());
            hashmapInstance.add(hashInstance);
        }
        return hashmapInstance;
    }
    public ArrayList<HashMap> getUsers(){
        ArrayList<HashMap> hashmapUser = new ArrayList<HashMap>();
        for (int i = 0; i < users.size(); i++){
            HashMap hashUser = new HashMap();
            hashUser.put("user id", users.get(i).getId());
            hashUser.put("user name", users.get(i).getName());
            hashmapUser.add(hashUser);
        }
        return hashmapUser;
    }
    public ArrayList<Assignment> getAssignments(){
        return assignments;
    }
    public void setId(int id){
        this.id = id;
    }
    public void setName(String name){
        this.name = name;
    }
    public String toString(){
        return "dataset id : " + id + "\n" + "dataset name : " + name + "\n" + "class labels : " + labels + "\n" +
                "instances : " + instances + "\n" + "class label assignments : " + assignments + "users : " + users;
    }

}

