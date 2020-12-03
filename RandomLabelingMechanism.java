/**
 * Auto Generated Java Class.
 */

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

public class RandomLabelingMechanism extends LabelingMechanism{
  
  private int random = 1;
  
  public RandomLabelingMechanism(){
    JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader("/CES3063F20_LabelingProject_Input-1.json")) {

            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            System.out.println(jsonObject);

            String name = (String) jsonObject.get("name");
            System.out.println(name);

            long age = (Long) jsonObject.get("age");
            System.out.println(age);

            // loop array
            JSONArray msg = (JSONArray) jsonObject.get("messages");
            Iterator<String> iterator = msg.iterator();
            while (iterator.hasNext()) {
                System.out.println(iterator.next());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

  
  
  }/*
  public RandomLabelingMechanism(Random random){
  
  
  }
  public Assignment label(Instance instance,User user, Label[] labels){
  
  
  }
  public Random getRandomClass(){
    
    
  }
  public void setRandomClass(Random random){
    
    
  }
  public String toString(){
  
    
  }*/
  
//}
