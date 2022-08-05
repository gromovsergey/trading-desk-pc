package app.programmatic.ui.external_services.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RecaptchaResponse {

    // whether this request was a valid reCAPTCHA token for your site
    private Boolean success;

    // the score for this request (0.0 - 1.0)
    private Float score;

    // the action name for this request (important to verify)
    private String action;

    // timestamp of the challenge load (ISO format yyyy-MM-dd'T'HH:mm:ssZZ)
    private String challengeTs;

    // the hostname of the site where the reCAPTCHA was solved
    private String hostname;

    /*
        https://developers.google.com/recaptcha/docs/verify
        missing-input-secret	The secret parameter is missing.
        invalid-input-secret	The secret parameter is invalid or malformed.
        missing-input-response	The response parameter is missing.
        invalid-input-response	The response parameter is invalid or malformed.
        bad-request	The request is invalid or malformed.
        timeout-or-duplicate	The response is no longer valid: either is too old or has been used previously.

     */
    private List<String> errorCodes;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getChallengeTs() {
        return challengeTs;
    }

    @JsonProperty("challenge_ts")
    public void setChallengeTs(String challengeTs) {
        this.challengeTs = challengeTs;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public List<String> getErrorCodes() {
        return errorCodes;
    }

    @JsonProperty("error-codes")
    public void setErrorCodes(List<String> errorCodes) {
        this.errorCodes = errorCodes;
    }

    @Override
    public String toString() {
        return "RecaptchaResponse{" +
                "success=" + success +
                ", score=" + score +
                ", action='" + action + '\'' +
                ", challengeTs=" + challengeTs +
                ", hostname='" + hostname + '\'' +
                ", errorCodes=" + errorCodes +
                '}';
    }
}
