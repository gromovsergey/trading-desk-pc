package com.foros.birt.services.birt;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.stereotype.Service;

@Service
public class SynchronizationReportSessionService {

    private Map<Long, ReentrantLock> locks = new HashMap<Long, ReentrantLock>();

    public void lock(Long sessionId) {
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        locks.put(sessionId, lock);
    }

    public void unlock(Long sessionId) {
        locks.remove(sessionId).unlock();
    }

    public void waitFor(Long sessionId) {
        ReentrantLock lock = locks.get(sessionId);
        if (lock != null) {
            try {
                lock.lock();
            } finally {
                lock.unlock();
            }
        }
    }

    public boolean isLocked(Long sessionId) {
        ReentrantLock lock = locks.get(sessionId);
        return lock != null && lock.isLocked();
    }

}
