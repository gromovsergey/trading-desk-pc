package com.foros.rs.client.util;

import com.foros.rs.client.model.ConstraintViolation;
import com.foros.rs.client.model.entity.EntityBase;
import com.foros.rs.client.model.operation.Operation;
import com.foros.rs.client.model.operation.Operations;
import com.foros.rs.client.model.operation.OperationsResult;
import com.foros.rs.client.data.ByteArrayContentSource;
import com.foros.rs.client.result.RsConstraintViolationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class UploadStrategies {

    private static final UploadStrategy<? extends EntityBase> defaultStrategy = new DefaultStrategy<>();
    private static final UploadStrategy<? extends EntityBase> resendValidtrategy = new ResendValidStrategy<>();

    public static <T extends EntityBase> UploadStrategy<T> defaultStrategy() {
        return (UploadStrategy<T>) defaultStrategy;
    }

    public static <T extends EntityBase> UploadStrategy<T> resendValidStrategy() {
        return (UploadStrategy<T>) resendValidtrategy;
    }

    private UploadStrategies() {
    }

    private static final class DefaultStrategy<T extends EntityBase> implements UploadStrategy<T> {

        @Override
        public List<Long> upload(List<Operation<T>> operations, UploaderImplementer<T> uploaderImplementer, UploadOperationEvents<T> events) {
            OperationsResult operationsResult = uploaderImplementer.upload(wrapOperations(operations));

            return operationsResult.getIds();
        }
    }

    private static final class ResendValidStrategy<E extends EntityBase> implements UploadStrategy<E> {

        private static final Pattern PATTERN = Pattern.compile("operations\\[(\\d*)\\].*");

        @Override
        public List<Long> upload(List<Operation<E>> operationList, UploaderImplementer<E> uploaderImplementer, UploadOperationEvents<E> events) {
            Operations<E> operations = wrapOperations(operationList);

            try {
                return unsafeUpload(operations, uploaderImplementer);
            } catch (RsConstraintViolationException e) {
                events.onConstraintViolation(e);

                Operations<E> validOperations = fetchValidOperations(operations, e.getConstraintViolations());

                List<Long> ids = unsafeUpload(validOperations, uploaderImplementer);

                return reindex(operations, validOperations, ids);
            }
        }

        private List<Long> unsafeUpload(Operations<E> operations, UploaderImplementer<E> uploaderImplementer) {
            return uploaderImplementer.upload(operations).getIds();
        }

        private List<Long> reindex(Operations<E> allOperations, Operations<E> validOperations, List<Long> ids) {
            List<Long> newIndex = CollectionUtils.newLongListWithSize(allOperations.getOperations().size());

            for (int i = 0; i < ids.size(); i++) {
                Operation<E> operation = validOperations.getOperations().get(i);

                newIndex.set(allOperations.getOperations().indexOf(operation), ids.get(i));
            }

            return newIndex;
        }

        private Operations<E> fetchValidOperations(Operations<E> operations, List<ConstraintViolation> constraintViolations) {
            Set<Integer> invalidIndexes = new HashSet<>();

            for (ConstraintViolation violation : constraintViolations) {
                Matcher matcher = PATTERN.matcher(violation.getPath());
                if (matcher.matches()) {
                    invalidIndexes.add(Integer.parseInt(matcher.group(1)));
                }
            }

            return wrapOperations(CollectionUtils.copyExcludeIndexes(operations.getOperations(), invalidIndexes));
        }

    }

    private static <E extends EntityBase> Operations<E> wrapOperations(List<Operation<E>> operationsList) {
        Operations<E> operations = new Operations<>();
        operations.setOperations(operationsList);
        return operations;
    }

}
