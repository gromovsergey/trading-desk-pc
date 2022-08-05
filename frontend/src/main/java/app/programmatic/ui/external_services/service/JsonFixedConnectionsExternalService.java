package app.programmatic.ui.external_services.service;

import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.*;
import akka.http.javadsl.unmarshalling.Unmarshaller;
import akka.stream.Materializer;
import app.programmatic.ui.external_services.exception.ExternalServiceException;
import app.programmatic.ui.external_services.exception.TooManyConnectionsException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonFixedConnectionsExternalService<P, T> {
    private static final Logger logger = Logger.getLogger(JsonFixedConnectionsExternalService.class.getName());

    private static final int DEFAULT_TIMEOUT_IN_MS = 60000;
    private static final int DEFAULT_MAX_CONNECTIONS_NUMBER = 100;
    private static final ObjectMapper mapper = new ObjectMapper();

    private static Semaphore semaphore;

    private static ActorSystem actorSystem;
    private static Materializer materializer;
    private static Http http;


    public int getMaxConnectionsNumber() {
        return DEFAULT_MAX_CONNECTIONS_NUMBER;
    }

    public int getTimeoutInMs() {
        return DEFAULT_TIMEOUT_IN_MS;
    }

    @PostConstruct
    public synchronized void init() {
        semaphore = new Semaphore(getMaxConnectionsNumber());

        actorSystem = ActorSystem.create(getClass().getName().replace('.', '-'));
        materializer = Materializer.createMaterializer(actorSystem);
        http = Http.get(actorSystem);

//    ConnectionPoolSettings poolSettings = ConnectionPoolSettings.create(actorSystem);
//    ClientConnectionSettings connectionSettings = ClientConnectionSettings.create(actorSystem);
//    poolSettings.withConnectionSettings(connectionSettings);

//    HttpsConnectionContext httpsConnectionContext = http.createDefaultClientHttpsContext();
//    http.singleRequest(HttpRequest.create("https://akka.io"), httpsConnectionContext, poolSettings, actorSystem.log());
    }


    public T doRequest(HttpRequest httpRequest, Class<T> returnValueType) throws ExternalServiceException {
        return doRequest(null, httpRequest, returnValueType);
    }

    public T doRequest(P model, HttpRequest httpRequest, Class<T> returnValueType) throws ExternalServiceException {
        boolean processingAllowed = semaphore.tryAcquire();
        if (!processingAllowed) {
            throw new TooManyConnectionsException();
        }

        try {
            return doRequestImpl(model, httpRequest, returnValueType);
        } catch (ExternalServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ExternalServiceException("Can't do request: " + e.getMessage(), e);
        } finally {
            semaphore.release();
        }
    }

    private T doRequestImpl(P model, HttpRequest httpRequest, Class<T> returnValueType) {
        HttpRequest request = httpRequest;
        if (model != null) {
            request = request.withEntity(ContentTypes.APPLICATION_JSON, toJson(model));
        }
        // ToDo: move to debug with aop
        logger.info("Request info: " + request);

        CompletionStage<HttpResponse> response = http.singleRequest(request);
        CompletableFuture<HttpResponse> responseFuture = response.toCompletableFuture();
        HttpResponse httpResponse;
        long curTimeoutInMs = getTimeoutInMs();
        TimeElapsed timeElapsed = new TimeElapsed();
        try {
            httpResponse = responseFuture.get(curTimeoutInMs, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new ExternalServiceException("Can't do request: " + e.getMessage(), e);
        } finally {
            long millisecondsPassed = timeElapsed.getInMs();
            if (logger.getLevel().intValue() <= Level.FINE.intValue()) {
                logger.fine("Read data time in ms: " + millisecondsPassed);
            }
            curTimeoutInMs -= millisecondsPassed;
        }

        logger.info("Response info: " + httpResponse);

        return convertToResponse(httpResponse, curTimeoutInMs, returnValueType);
    }

    private String toJson(P model) {
        try {
            return mapper.writeValueAsString(model);
        } catch (Exception e) {
            throw new ExternalServiceException(e);
        }
    }

    private T convertToResponse(HttpResponse response, long timeoutInMs, Class<T> valueType) {
        StatusCode statusCode = response.status();
        if (statusCode == null
                || HttpStatus.SC_OK != statusCode.intValue()) {
            throw new ExternalServiceException("Invalid HTTP status: " + statusCode.intValue());
        }
        if (response.entity() == null) {
            throw new ExternalServiceException("Empty body");
        }

        TimeElapsed timeElapsed = new TimeElapsed();
        try {
            CompletionStage<String> responseStage = Unmarshaller.entityToString().unmarshal(response.entity(), materializer);
            CompletableFuture<String> responseFuture = responseStage.toCompletableFuture();
            String resultAsString = responseFuture.get(timeoutInMs, TimeUnit.MILLISECONDS);
            logger.fine("Response body: " + resultAsString);

            T result = mapper.readValue(resultAsString, valueType);
            return result;
        } catch (Exception e) {
            throw new ExternalServiceException("Unmarshaling failed: " + e.getMessage(), e);
        } finally {
            if (logger.getLevel().intValue() <= Level.FINE.intValue()) {
                long millisecondsPassed = timeElapsed.getInMs();
                logger.fine("Read data time in ms: " + millisecondsPassed);
            }
        }
    }

    public void shutdown() {
        http.shutdownAllConnectionPools();
        materializer.shutdown();
        actorSystem.terminate();
    }

    private static class TimeElapsed {
        private static long nanosInSec = 100000000;
        private static long msInNano = 1000000;

        private final long nanosFromEpoch;

        public TimeElapsed() {
            LocalDateTime now = LocalDateTime.now();
            nanosFromEpoch = toNanosFromEpoch(now);
        }

        public long getInMs() {
            LocalDateTime now = LocalDateTime.now();
            return (toNanosFromEpoch(now) - nanosFromEpoch) / msInNano;
        }

        // ToDo: check and re-implement before 2262 year (whether long 64 is bit yet?) :)
        private long toNanosFromEpoch(LocalDateTime dateTime) {
            return dateTime.toEpochSecond(ZoneOffset.UTC) * nanosInSec + dateTime.getNano();
        }
    }
}
