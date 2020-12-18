package CSE3063_Project;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Assignment {
    private Integer instanceId;

    private ArrayList<Integer> labelIds;

    private Integer userId;

    private LocalDateTime dateTime;

    public Assignment(Integer instanceId, ArrayList<Integer> labelIds, Integer userId) {
        this.instanceId = instanceId;
        this.labelIds = labelIds;
        this.userId = userId;
        this.dateTime = LocalDateTime.now();
    }

    public Assignment(Integer instanceId, Integer userId) {
        this.instanceId = instanceId;
        this.userId = userId;
        this.dateTime = LocalDateTime.now();
    }

    public boolean addLabel(Integer labelId) {
        if (this.labelIds.contains(labelId)){
            return false;

        } else{
            this.labelIds.add(labelId);
            return true;
        }
    }

    public Integer getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Integer instanceId) {
        this.instanceId = instanceId;
    }

    public ArrayList<Integer> getLabelIds() {
        return this.labelIds;
    }

    public void setLabelIds(ArrayList<Integer> labelIds) {
        this.labelIds = labelIds;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getDateTime() {
        return dateTime.toString();
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "instanceId=" + instanceId +
                ", LabelIds=" + this.getLabelIds() +
                '}';
    }
}
