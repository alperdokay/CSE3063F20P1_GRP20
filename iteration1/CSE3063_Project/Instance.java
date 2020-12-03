package CSE3063_Project;

public class Instance {

    private Integer id;

    private String instance;

    public Instance(Integer id, String instance) {
        this.id = id;
        this.instance = instance;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    @Override
    public String toString() {
        return "Instance{" +
                "id=" + id +
                ", instance='" + instance + '\'' +
                '}';
    }


}
