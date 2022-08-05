package app.programmatic.ui.user.view;

import app.programmatic.ui.common.model.MajorDisplayStatus;

import java.util.List;

public class UserView {
    private Long id;
    private Long accountId;
    private String firstName;
    private String lastName;
    private String email;
    private String roleName;
    private Long roleId;
    private List<Long> advertiserIds;
    private String advertisers;
    private MajorDisplayStatus displayStatus;
    private Long version;
    private Long version2;

    public UserView() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public List<Long> getAdvertiserIds() {
        return advertiserIds;
    }

    public void setAdvertiserIds(List<Long> advertiserIds) {
        this.advertiserIds = advertiserIds;
    }

    public String getAdvertisers() {
        return advertisers;
    }

    public void setAdvertisers(String advertisers) {
        this.advertisers = advertisers;
    }

    public MajorDisplayStatus getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(MajorDisplayStatus displayStatus) {
        this.displayStatus = displayStatus;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getVersion2() {
        return version2;
    }

    public void setVersion2(Long version2) {
        this.version2 = version2;
    }
}
