package CSE3063_Project;

public class LabelProvider extends Logger {

    private Label Label;

    public LabelProvider extends Logger( String type, String operation) {
        this.type = type;
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "Logger{" +
                "type='" + type + '\'' +
                ", operation='" + operation + '\'' +
                '}';
    }

}