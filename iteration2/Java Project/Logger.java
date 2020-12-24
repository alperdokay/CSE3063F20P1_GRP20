package CSE3063_Project;

import java.time.LocalDateTime;

public class Logger {

    private LocalDateTime date;
    private String type;
    private String operation;

    public Logger(String type, String operation) {
        this.date = LocalDateTime.now();
        this.type = type;
        this.operation = operation;
    }
    
    public String getType() {
    	return this.type;
    }
    
    public void setType(String type) {
    	this.type = type;
    }
    
    public String getDate() {
    	return this.date.toString();
    }
    
    public String getOperation() {
    	return this.operation;
    }
    
    public void setOperation(String operation) {
    	this.operation = operation;
    }

    public String getLogMessage() {
		return "Example log " + date.toString();
}
}
