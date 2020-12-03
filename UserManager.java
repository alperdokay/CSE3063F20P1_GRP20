package CSE3063_Project;
import java.util.Date;
public class UserManager extends Logger {

    private User user;
	private Date date;
    public UserManager extends Logger( String type, String operation,Date date) {
        this.type = type;
        this.operation = operation;
		this.date = date;
    }

    @Override
     public String toString() {
        return date + "[UserManager] INFO userManager: created" + RandomLabelingMechanism.getRandomClass()
		+ "as" + User.getType();
    }

}