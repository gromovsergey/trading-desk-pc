package app.programmatic.ui.user.view;

import app.programmatic.ui.user.dao.model.UserRole;

public class UserRoleView {
    private Long id;
    private String name;
    private boolean advLevelAccessAvailable;

    public UserRoleView(UserRole userRole, boolean advLevelAccessAvailable) {
        this.id = userRole.getId();
        this.name = userRole.getName();
        this.advLevelAccessAvailable = advLevelAccessAvailable;
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

    public boolean isAdvLevelAccessAvailable() {
        return advLevelAccessAvailable;
    }

    public void setAdvLevelAccessAvailable(boolean advLevelAccessAvailable) {
        this.advLevelAccessAvailable = advLevelAccessAvailable;
    }
}
