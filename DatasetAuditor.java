package CSE3063_Project;

public class DatasetAuditor extends Logger {

    private Dataset dataset;

    public DatasetAuditor extends Logger( String type, String operation) {
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