package com.foros.security;

import com.foros.util.command.BatchPreparedStatementWork;
import com.foros.util.command.executor.HibernateWorkExecutorService;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;

@LocalBean
@Singleton
public class AccessStampService {

    private static final String UPDATE_SQL = "update authenticationtoken set last_update = ? where token = ?";

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private Map<String, Long> timestamp = new ConcurrentHashMap<>();

    @EJB
    private HibernateWorkExecutorService hibernateWorkExecutorService;

    public void touch(String authenticationToken) {
        Lock lock = readWriteLock.readLock();
        try {
            lock.lock();

            timestamp.put(authenticationToken, System.currentTimeMillis());
        } finally {
            lock.unlock();
        }
    }

    @Schedule(second= "*", minute = "*/1", hour = "*", persistent = false)
    public void update() {
        Map<String, Long> values = renewTimestamp();

        if (values.isEmpty()) {
            return;
        }

        hibernateWorkExecutorService.execute(new BatchPreparedStatementWork<Map.Entry<String, Long>>(UPDATE_SQL, values.entrySet()) {
            @Override
            protected void set(PreparedStatement statement, Map.Entry<String, Long> value) throws SQLException {
                statement.setLong(1, value.getValue());
                statement.setString(2, value.getKey());
            }
        });
    }

    private Map<String, Long> renewTimestamp() {
        Map<String, Long> values;

        Lock lock = readWriteLock.writeLock();
        try {
            lock.lock();

            values = timestamp;
            timestamp = new ConcurrentHashMap<>();
        } finally {
            lock.unlock();
        }

        return values;
    }

    public boolean check(String token, Long expirationTime) {
        Long value = timestamp.get(token);

        if (value == null) {
            return true;
        }

        return System.currentTimeMillis() - value < expirationTime;
    }
}
