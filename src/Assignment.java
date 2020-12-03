package com.company;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Assignment {
    private int instanceId;
    private ArrayList<Integer> classLabelIds;
    private int userId;
    private LocalDateTime datetime;

    public Assignment(int instanceId, int userId, ArrayList<Integer> labelIds){
        this.instanceId = instanceId;
        this.userId = userId;
        this.classLabelIds = labelIds;
    }
    public Assignment(int instanceId, int userId){}
    public int addLabel(String sentence, ArrayList<String> keywordsPositive, ArrayList<String> keywordsNegative){
        //Labeling --> each instance has an assignment
        String[] words = sentence.split("\\s"); //splitting the string based on whitespace
        int label_no = classLabelIds.get(2);
        for(String word: words){
            if(keywordsPositive.contains(word)){
                label_no = classLabelIds.get(0);
            }
            if(keywordsNegative.contains(word)){
                label_no = classLabelIds.get(1);
            }
        }
        return label_no;
    }
    public int getInstanceId(){
        return instanceId;
    }
    public ArrayList<Integer> getLabels(){
        return classLabelIds;
    }
    public int getUserId(){
        return userId;
    }
    public void setInstanceId(int id){
        this.instanceId = id;
    }
    public void setUserId(int id){
        this.userId = id;
    }
    public String toString(){
        return "instance id : " + instanceId + " , " + "class label ids : " + classLabelIds + " , " + "user id : "
                + userId + " , " + "datetime : " + datetime;
    }
}