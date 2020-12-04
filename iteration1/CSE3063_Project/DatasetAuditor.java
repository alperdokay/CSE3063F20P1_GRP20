package CSE3063_Project;

public class DatasetAuditor extends Logger {

    private Dataset dataset;
	
    public DatasetAuditor(String type, String operation, Dataset dataset) {
        super(type, operation);
        this.dataset = dataset;
    }

    @Override
    public String getLogMessage() {
        return this.getDate() + " [DatasetAuditor] " + this.getType() + " " + this.getOperation() + " name: " + this.dataset.getName();
    }

}