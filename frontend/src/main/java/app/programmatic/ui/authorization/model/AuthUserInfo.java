package app.programmatic.ui.authorization.model;

public class AuthUserInfo {
    private Long id;
    private String ip;

    public AuthUserInfo(Long id, String ip) {
        this.id = id;
        this.ip = ip;
    }

    public Long getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }
}
