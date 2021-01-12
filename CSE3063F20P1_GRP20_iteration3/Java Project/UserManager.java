package CSE3063_Project;


public class UserManager extends Logger {

    private User user;
	
    public UserManager(String type, String operation, User user) {
        super(type, operation);
		this.user = user;
    }
    
    public String getLogMessage() {
        return this.getDate() + " [UserManager] " + this.getType() + " userManager: "+ this.getOperation() + " " + user.getName()
		+ " as " + user.getType();
    }

}
