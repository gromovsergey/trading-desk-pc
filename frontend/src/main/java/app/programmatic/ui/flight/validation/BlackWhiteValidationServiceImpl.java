package app.programmatic.ui.flight.validation;

import com.foros.rs.client.model.ConstraintViolation;
import com.foros.rs.client.result.RsConstraintViolationException;
import app.programmatic.ui.common.aspect.forosApiViolation.ForosApiViolationProcessor;
import app.programmatic.ui.common.validation.ConstraintViolationBuilder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class BlackWhiteValidationServiceImpl implements ForosApiViolationProcessor {
    private static final Logger logger = Logger.getLogger(BlackWhiteValidationServiceImpl.class.getName());
    private static final String WHITE_LIST_ERROR_PATH = "whiteList";
    private static final String BLACK_LIST_ERROR_PATH = "blackList";

    @Override
    public ConstraintViolationBuilder process(RsConstraintViolationException e, Object[] methodArgs) {
        WhiteBlackInfoHolder whiteBlackInfoHolder = null;

        switch (methodArgs.length) {
            case 3:
                whiteBlackInfoHolder = inspectCreateArgs(methodArgs);
                break;
            case 4:
                whiteBlackInfoHolder = inspectUpdateArgs(methodArgs);
                break;
            default:
                throwUnexpectedMethod(methodArgs);
        }

        ConstraintViolationBuilder<Void> builder = new ConstraintViolationBuilder<>();
        for (ConstraintViolation violation : e.getConstraintViolations()) {
            process(violation, whiteBlackInfoHolder, builder);
        }
        return builder;
    }

    private void process(ConstraintViolation violation, WhiteBlackInfoHolder info, ConstraintViolationBuilder<Void> builder) {
        boolean whiteError = valueMatches(violation.getValue(), info.getWhiteId(), info.getWhiteList());
        boolean blackError = valueMatches(violation.getValue(), info.getBlackId(), info.getblackList());
        if (!whiteError && !blackError) {
            logger.log(Level.WARNING, String.format("Can't determine error path. Violation message: ", violation.getMessage()));
            whiteError = true;
            blackError = true;
        }

        if (whiteError) {
            builder.addViolationMessage(WHITE_LIST_ERROR_PATH, violation.getMessage());
        }

        if (blackError) {
            builder.addViolationMessage(BLACK_LIST_ERROR_PATH, violation.getMessage());
        }
    }

    private boolean valueMatches(String value, Long channelId, Set<String> urls) {
        if (channelId != null && String.valueOf(channelId).equals(value)) {
            return true;
        }

        return urls.contains(value);
    }

    private WhiteBlackInfoHolder inspectCreateArgs(Object[] args) {
        try {
            return new WhiteBlackInfoHolder(null, (List<String>)(args[1]), null, (List<String>)(args[2]));
        } catch (Exception e) {
            throwUnexpectedMethod(args);
        }
        return null;
    }

    private WhiteBlackInfoHolder inspectUpdateArgs(Object[] args) {
        try {
            return new WhiteBlackInfoHolder((Long)(args[1]), (List<String>)(args[0]), (Long)(args[3]), (List<String>)(args[2]));
        } catch (Exception e) {
            throwUnexpectedMethod(args);
        }
        return null;
    }

    private static void throwUnexpectedMethod(Object[] methodArgs) {
        throw new RuntimeException("Such method signature is unexpected: " +
                Arrays.asList(methodArgs).stream().map(o -> String.valueOf(o) ).collect(Collectors.joining(";;")));
    }

    private class WhiteBlackInfoHolder {
        private Long whiteId;
        private HashSet<String> whiteList;
        private Long blackId;
        private HashSet<String> blackList;

        public WhiteBlackInfoHolder(Long whiteId, List<String> whiteList, Long blackId, List<String> blackList) {
            this.whiteId = whiteId;
            this.whiteList = whiteList == null ? new HashSet<>(0) : new HashSet<>(whiteList);
            this.blackId = blackId;
            this.blackList = blackList == null ? new HashSet<>(0) : new HashSet<>(blackList);
        }

        public Long getWhiteId() {
            return whiteId;
        }

        public HashSet<String> getWhiteList() {
            return whiteList;
        }

        public Long getBlackId() {
            return blackId;
        }

        public HashSet<String> getblackList() {
            return blackList;
        }
    }
}