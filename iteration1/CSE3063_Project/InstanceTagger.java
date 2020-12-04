package CSE3063_Project;

public class InstanceTagger extends Logger {

    private Assignment assignment;

    public InstanceTagger(String type, String operation,Assignment assignment) {
        super(type, operation);
        this.assignment = assignment;
	}

    @Override
    public String getLogMessage() {
        return this.getDate() + " [InstanceTagger] " + this.getType() + " user id: "+ this.assignment.getUserId() + " " +
		this.getOperation() + " instance id: " + this.assignment.getInstanceId();
    }

}
