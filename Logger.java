package CSE3063_Project;

import java.util.Date;

    public class Logger {

        private Date date;
        private String type;
        private String operation;

        public Logger(Date date,String type, String operation) {
            this.date = date;
            this.type = type;
            this.operation = operation;
        }

        public String toString() {
			return date + "[" + type + "]" + "INFO" + "user id:"+ User.getId() + 
			operation + "tagged instance id:" + Assignment.getUserId() +"with class label"+ 
			Label.getId()+":"+ Label.getText() +"instance:"+ Instance.getInstance();
    }
}