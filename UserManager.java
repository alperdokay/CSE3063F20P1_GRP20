package CSE3063_Project;

public class UserManager extends Logger {

    private User user;

    public UserManager extends Logger( String type, String operation) {
        this.type = type;
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "UserManager: {" +
                "type='" + type + '\'' +
                ", operation='" + operation + '\'' +
                '}';
    }

}