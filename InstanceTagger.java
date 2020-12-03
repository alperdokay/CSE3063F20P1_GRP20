package CSE3063_Project;

public class InstanceTagger extends Logger {

    private Assignment assignment;

    public InstanceTagger extends Logger( String type, String operation) {
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