package app.programmatic.ui.user.dao.model;

import app.programmatic.ui.common.model.VersionEntityBase;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity
@Table(name = "USERCREDENTIALS")
public class UserCredential extends VersionEntityBase<Long> {

    @SequenceGenerator(name = "UserCredentialGen", sequenceName = "usercredentials_user_credential_id_seq", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "UserCredentialGen")
    @Column(name = "user_credential_id", updatable = false, nullable = false)
    private Long id;

    @OneToOne(mappedBy = "userCredential")
    private User user;

    @NotNull
    @Size(min = 1, max = 400)
    @Column(name = "email", nullable = false)
    private String email;

    @Size(min = 1, max = 512)
    @Column(name = "password")
    private String password;

    @Size(min = 1, max = 64)
    @Column(name = "rs_auth_token")
    private String rsToken;

    @Column(name = "rs_signature_key")
    private byte[] rsKey;

    @Min(0)
    @Column(name = "wrong_attempts")
    private Integer wrongAttempts;

    @Column(name = "blocked_until")
    private LocalDateTime blockedUntil;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    @Size(min = 1, max = 23)
    @Column(name = "last_login_ip")
    private String lastLoginIP;


    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRsToken() {
        return rsToken;
    }

    public void setRsToken(String rsToken) {
        this.rsToken = rsToken;
    }

    public byte[] getRsKey() {
        return rsKey;
    }

    public void setRsKey(byte[] rsKey) {
        this.rsKey = rsKey;
    }

    public Integer getWrongAttempts() {
        return wrongAttempts;
    }

    public void setWrongAttempts(Integer wrongAttempts) {
        this.wrongAttempts = wrongAttempts;
    }

    public LocalDateTime getBlockedUntil() {
        return blockedUntil;
    }

    public void setBlockedUntil(LocalDateTime blockedUntil) {
        this.blockedUntil = blockedUntil;
    }

    public LocalDateTime getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(LocalDateTime lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getLastLoginIP() {
        return lastLoginIP;
    }

    public void setLastLoginIP(String lastLoginIP) {
        this.lastLoginIP = lastLoginIP;
    }
}
