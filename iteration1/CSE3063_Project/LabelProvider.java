package CSE3063_Project;

public class LabelProvider extends Logger {

    private Label label;
    
    public LabelProvider (String type, String operation, Label label) {
        super(type, operation);
        this.label = label; 
    }

    @Override
    public String getLogMessage() {
        return this.getDate() + " [LabelProvider] " + this.getType() + " " + this.getOperation() + " instance: " + this.label.getText();
    }

}
