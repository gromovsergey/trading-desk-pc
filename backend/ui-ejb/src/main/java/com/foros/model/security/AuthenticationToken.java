package com.foros.model.security;

import com.foros.model.EntityBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "AUTHENTICATIONTOKEN")
public class AuthenticationToken extends EntityBase {

    @Id
    @Column(name = "TOKEN", nullable = false, unique = true)
    private String token;

    @ManyToOne
    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID", updatable = false)
    private User user;

    @Column(name = "IP", updatable = false)
    private String ip;

    @Column(name = "LAST_UPDATE")
    private Long lastUpdate = System.currentTimeMillis();

    public AuthenticationToken() {
    }

    public AuthenticationToken(String token, String ip, User user) {
        setToken(token);
        setIp(ip);
        setUser(user);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        this.registerChange("token");
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.registerChange("user");
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
        this.registerChange("ip");
    }

    public Long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Long lastUpdate) {
        this.lastUpdate = lastUpdate;
        this.registerChange("lastUpdate");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthenticationToken that = (AuthenticationToken) o;

        if (!token.equals(that.token)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return token.hashCode();
    }
}
