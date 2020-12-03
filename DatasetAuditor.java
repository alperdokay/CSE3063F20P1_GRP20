package CSE3063_Project;
import java.util.Date;
public class DatasetAuditor extends Logger {

    private Dataset dataset;
	private Date date;
    public DatasetAuditor extends Logger( String type, String operation,Date date) {
        this.type = type;
        this.operation = operation;
		this.date = date;
    }

    @Override
    public String toString() {
        return date + "[DatasetAuditor]" + "INFO" + "user id:"+ User.getId() + 
		operation + "tagged instance id:" + Assignment.getUserId() +"with class label"+ 
		Label.getId()+":"+Label.getText() +"instance:"+instance.getInstance();
    }

}