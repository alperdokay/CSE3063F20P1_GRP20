package CSE3063_Project;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Assignment {
    private Instance instance;

    private ArrayList<Label> labels;

    private User user;

    private LocalDateTime dateTime;

    public Assignment(Instance instance, ArrayList<Label> labels, User user) {
        this.instance = instance;
        this.labels = labels;
        this.user = user;
        this.dateTime = LocalDateTime.now();
    }

    public Assignment(Instance instance, User user) {
        this.instance = instance;
        this.user = user;
        this.dateTime = LocalDateTime.now();
    }

    public boolean addLabel(Label label) {
        if (this.labels.contains(label)){
            return false;

        } else{
            this.labels.add(label);
            return true;
        }
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public ArrayList<Label> getLabels() {
        return this.labels;
    }

    public void setLabels(ArrayList<Label> labels) {
        this.labels = labels;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDateTime() {
        return dateTime.toString();
    }
    
    public LocalDateTime getDateTimeObject() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "instanceId=" + instance.getId() +
                ", Labels=" + this.getLabels() +
                '}';
    }
}
