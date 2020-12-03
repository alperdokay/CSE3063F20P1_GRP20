package CSE3063_Project;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class Dataset {

    private Integer id;

    private String Name;

    private Integer instanceLabellingLimit;

    private ArrayList<Label> labels;

    private ArrayList<Instance> instances;

    private ArrayList<Assignment> assignments;

    private ArrayList<User> users;

    public Dataset(Integer id, String name, Integer instanceLabellingLimit) {
        this.id = id;
        Name = name;
        this.instanceLabellingLimit = instanceLabellingLimit;
    }

    @Override
    public String toString() {
        return "Dataset{" +
                "Name='" + Name + '\'' +
                '}';
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
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Integer getInstanceLabellingLimit() {
        return instanceLabellingLimit;
    }

    public void setInstanceLabellingLimit(Integer instanceLabellingLimit) {
        this.instanceLabellingLimit = instanceLabellingLimit;
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

    public boolean addLabel(Label label){

        Optional<Label> existedLabels = this.getLabels().stream().filter(data -> label.getId().equals(data.getId())).findFirst();
        if (existedLabels.isEmpty()){
            this.getLabels().add(label);
            return true;
        } else {
            return false;
        }
    }
    public boolean addInstance(Instance instance){

        Optional<Instance> existedLabels = this.getInstances().stream().filter(data -> instance.getId().equals(data.getId())).findFirst();
        if (existedLabels.isEmpty()){
            this.getInstances().add(instance);
            return true;
        } else {
            return false;
        }
    }


    public boolean addAssignment(Assignment assignment){

        Optional<Assignment> existedLabels = this.getAssignments().stream().filter(data -> assignment.getId().equals(data.getId())).findFirst();
        if (existedLabels.isEmpty()){
            this.getAssignments().add(assignment);
            return true;
        } else {
            return false;
        }
    }

    public boolean addUser(User user){

        Optional<User> existedLabels = this.getUsers().stream().filter(data -> user.getId().equals(data.getId())).findFirst();
        if (existedLabels.isEmpty()){
            this.getUsers().add(user);
            return true;
        } else {
            return false;
        }
    }

    private boolean exportOutput() throws IOException {
        try{
        ObjectMapper Obj = new ObjectMapper();
        Obj.writeValue(new File("dataset-output.json"), this);
        Obj.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        return true;
        } catch (Exception e){
            return false;

        }
    }

}
