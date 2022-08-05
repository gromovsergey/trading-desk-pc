package app.programmatic.ui.invitation.dao.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ContractRequestData {

    @Size(min = 0, max = 200)
    private String name;

    @Size(min = 10, max = 50)
    private String tel;

    @Size(min = 5, max = 50)
    private String email;

    @NotNull
    @Size(min = 10, max = 1000)
    private String text;

    @Size(min = 0, max = 2000)
    private String reCaptcha;

    public ContractRequestData(String name, String tel, String email, String text, String reCaptcha) {
        this.name = name;
        this.tel = tel;
        this.email = email;
        this.text = text;
        this.reCaptcha = reCaptcha;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getReCaptcha() {
        return reCaptcha;
    }

    public void setReCaptcha(String reCaptcha) {
        this.reCaptcha = reCaptcha;
    }

    @Override
    public String toString() {
        return "ContractRequestData{" +
                "name='" + name + '\'' +
                ", tel='" + tel + '\'' +
                ", email='" + email + '\'' +
                ", text='" + text + '\'' +
                ", reCaptcha='" + reCaptcha + '\'' +
                '}';
    }
}
