package CSE3063_Project;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Assignment {

    private Integer id;

    private Integer instanceId;

    private ArrayList<Integer> classLabelIds;

    private Integer userId;

    private LocalDateTime labelIds;

    public Assignment(Integer instanceId, ArrayList<Integer> classLabelIds, Integer userId) {
        this.instanceId = instanceId;
        this.classLabelIds = classLabelIds;
        this.userId = userId;
    }

    public Assignment(Integer instanceId, Integer userId) {
        this.instanceId = instanceId;
        this.userId = userId;
    }

    public boolean addLabel(Integer labelId) {
        if (this.classLabelIds.contains(labelId)){
            return false;

        } else{
            this.classLabelIds.add(labelId);
            return true;
        }
    }

    public Integer getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Integer instanceId) {
        this.instanceId = instanceId;
    }

    public ArrayList<Integer> getClassLabelIds() {
        return classLabelIds;
    }

    public void setClassLabelIds(ArrayList<Integer> classLabelIds) {
        this.classLabelIds = classLabelIds;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDateTime getLabelIds() {
        return labelIds;
    }

    public void setLabelIds(LocalDateTime labelIds) {
        this.labelIds = labelIds;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "instanceId=" + instanceId +
                ", labelIds=" + labelIds +
                '}';
    }
}
