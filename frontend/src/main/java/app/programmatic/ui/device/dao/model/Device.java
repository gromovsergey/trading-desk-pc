package app.programmatic.ui.device.dao.model;

public class Device {
    private Long id;
    private String name;

    public Device() {
    }

    public Device(Long id) {
        this.id = id;
    }

    public Device(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Device)) {
            return false;
        }

        Device other = (Device)obj;
        return id != null ? id.equals(other.getId()) : other.getId() == null;
    }
}
