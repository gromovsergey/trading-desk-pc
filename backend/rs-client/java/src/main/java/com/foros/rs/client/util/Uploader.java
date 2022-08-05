package com.foros.rs.client.util;

import com.foros.rs.client.model.entity.EntityBase;
import com.foros.rs.client.model.operation.Operation;
import com.foros.rs.client.model.operation.Operations;
import com.foros.rs.client.result.RsConstraintViolationException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Uploader<E extends EntityBase> {

    private static final int MAX_PAGE_SIZE = 500;

    private UploadOperationEvents<E> eventsHandler = new DoNothingUploadOperationEvents<>();

    private UploaderImplementer<E> uploaderImplementer;

    private UploadStrategy<E> uploadStrategy = UploadStrategies.defaultStrategy();

    private String name;

    private int pageSize = MAX_PAGE_SIZE;

    public Uploader(String name) {
        this.name = name;
    }

    public Uploader<E> withEventsHandler(UploadOperationEvents<E> eventsHandler) {
        this.eventsHandler = eventsHandler;
        return this;
    }

    public Uploader<E> withUploader(UploaderImplementer<E> uploaderImplementer) {
        this.uploaderImplementer = uploaderImplementer;
        return this;
    }

    public Uploader<E> withUploadStrategy(UploadStrategy<E> uploadStrategy) {
        this.uploadStrategy = uploadStrategy;
        return this;
    }

    public Uploader<E> withPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public List<Long> upload(Operations<E> operations) {
        return upload(operations.getOperations());
    }

    public List<Long> upload(List<Operation<E>> operations) {
        if (operations.isEmpty()) {
            return Collections.emptyList();
        }

        eventsHandler.onBefore(name, operations.size());

        int operationsCount = operations.size();

        List<Long> ids = new ArrayList<>(operationsCount);

        for (Index index : new Indexes(operationsCount, pageSize)) {

            eventsHandler.onBatch(index.getFrom(), index.getTo());

            List<Operation<E>> subOperations = operations.subList(index.getFrom(), index.getTo());

            try {
                ids.addAll(uploadStrategy.upload(subOperations, uploaderImplementer, eventsHandler));
            } finally {
                eventsHandler.onProcessedOperations(subOperations);
            }
        }

        eventsHandler.onAfter(name, operations.size());

        return ids;
    }

    private static class Indexes  implements Iterable<Index> {

        private int count;
        private int pageSize;
        private Index index = new Index();

        public Indexes(int count, int pageSize) {
            this.count = count;
            this.pageSize = pageSize;
        }

        @Override
        public Iterator<Index> iterator() {
            return new Iterator<Index>() {
                @Override
                public boolean hasNext() {
                    return count > index.to;
                }

                @Override
                public Index next() {
                    index.from = index.index * pageSize;

                    index.index++;

                    int last = index.index * pageSize;

                    index.to = last < count ? last : count;

                    return index;
                }

                @Override
                public void remove() {
                    throw new RuntimeException();
                }
            };
        }

    }

    private static class Index {

        private int index = 0;
        private int from = 0;
        private int to = 0;

        public int getFrom() {
            return from;
        }

        public int getTo() {
            return to;
        }
    }

    public static class DoNothingUploadOperationEvents<E extends EntityBase> implements UploadOperationEvents<E> {
        @Override
        public void onBefore(String name, int count) {
            // do nothing
        }

        @Override
        public void onBatch(int from, int to) {
            // do nothing
        }

        @Override
        public void onConstraintViolation(RsConstraintViolationException e) {
            // do nothing
        }

        @Override
        public void onProcessedOperations(List<Operation<E>> operation) {
            // do nothing
        }

        @Override
        public void onAfter(String name, int count) {
            // do nothing
        }
    }

    public static interface OperationEvent<E extends EntityBase> {

        void onOperation(Operation<E> operation);

    }

    public static abstract class AbstractUploadOperationEvent<E extends EntityBase> implements UploadOperationEvents<E> {

        private OperationEvent<E> operationEvent;

        public AbstractUploadOperationEvent(OperationEvent<E> operationEvent) {
            this.operationEvent = operationEvent;
        }

        @Override
        public void onProcessedOperations(List<Operation<E>> operations) {
            for (Operation<E> operation : operations) {
                operationEvent.onOperation(operation);
            }
        }
    }
}
