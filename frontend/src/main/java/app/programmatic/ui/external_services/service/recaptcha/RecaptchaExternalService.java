package app.programmatic.ui.external_services.service.recaptcha;

import akka.http.javadsl.model.HttpRequest;
import app.programmatic.ui.external_services.exception.ExternalServiceException;
import app.programmatic.ui.external_services.model.RecaptchaResponse;
import app.programmatic.ui.external_services.service.JsonFixedConnectionsExternalService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RecaptchaExternalService extends JsonFixedConnectionsExternalService<Void, RecaptchaResponse> {

    @Value("${recaptcha.maxConnectionsNumber:10}")
    private int MAX_CONNECTIONS_NUMBER;

    @Value("${recaptcha.timeoutInMs:1000}")
    private int TIMEOUT_IN_MS;

    @Value("${recaptcha.baseUrl:https://www.google.com/recaptcha/api/siteverify}")
    private String BASE_URL;

    @Value("${recaptcha.secretKey}")
    private String SECRET_KEY;

    public int getMaxConnectionsNumber() {
        return MAX_CONNECTIONS_NUMBER;
    }

    public int getTimeoutInMs() {
        return TIMEOUT_IN_MS;
    }

    public RecaptchaResponse doRequest(String recaptchaUserResponse)
            throws ExternalServiceException {
        StringBuilder url = new StringBuilder(BASE_URL);
        url.append("?secret=");
        url.append(SECRET_KEY);
        url.append("&response=");
        url.append(recaptchaUserResponse);

        HttpRequest httpRequest = HttpRequest.POST(url.toString());
        return resultOrFailed(doRequest(httpRequest, RecaptchaResponse.class));
    }

    private RecaptchaResponse resultOrFailed(RecaptchaResponse source) {
        RecaptchaResponse result = source != null ? source : new RecaptchaResponse();
        if (result.getSuccess() == null) {
            result.setSuccess(Boolean.FALSE);
        }

        return result;
    }
}
