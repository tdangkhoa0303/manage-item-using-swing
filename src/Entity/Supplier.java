package Entity;

public class Supplier {
    private String code, name, address;
    private boolean collaborating;

    public Supplier() {
    }


    public Supplier(String code, String name, String address, boolean collaborating) {
        this.code = code;
        this.name = name;
        this.address = address;
        this.collaborating = collaborating;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isCollaborating() {
        return collaborating;
    }

    public void setCollaborating(boolean collaborating) {
        this.collaborating = collaborating;
    }
}
