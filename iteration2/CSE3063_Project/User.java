package CSE3063_Project;

public class User {
	private Integer id;

    private String name;

    private String type;
    
    private double consistencyCheckProbability;

    public User(Integer id, String name, String type, double consistencyCheckProbability) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.consistencyCheckProbability = consistencyCheckProbability;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public double getConsistencyCheckProbability() {
        return this.consistencyCheckProbability;
    }

    public void setConsistencyCheckProbability(double consistencyCheckProbability) {
        this.consistencyCheckProbability = consistencyCheckProbability;
    }
    
    @Override
    public String toString() {
        return "user id: " + this.getId() + ", user name: " + this.getName() + ", user type: " + this.getType(); 
    }
}
