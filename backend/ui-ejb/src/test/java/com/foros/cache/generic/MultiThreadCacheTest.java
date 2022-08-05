package com.foros.cache.generic;

import com.foros.cache.generic.implementor.CacheImplementorFactory;
import com.foros.cache.generic.model.SomeEntity;
import com.foros.cache.generic.model.SomeKey;

import group.Unit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.joda.time.Duration;
import org.joda.time.ReadableDuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(Unit.class)
public class MultiThreadCacheTest extends Assert {

    private Cache cache;

    public static class Counter {

        private long value;

        public Counter(long value) {
            this.value = value;
        }

        public synchronized long next() {
            return ++value;
        }
    }

    private static ExecutorService executorService = Executors.newCachedThreadPool();

    /**
      Uncomment if you need to develop Memcached-related functionality
      You should replace localhost below with a real oix-dev host name
      but you should get that host from the Hosts' pool first
      
      Comment again after the development is done
      
      No need to have this test always running because of two points:
      1) We don't have a spare host with Memcached running (yep, XNB is gone)
      2) This test looks like a development helper, not a JUnit test
    **/
    // @Before
    public void init() {
        CacheFactory cacheFactory = new CacheFactory(CacheImplementorFactory.memcached(
                Arrays.asList("localhost:11211"))
        );

        cacheFactory.addRegionConfig(new CacheRegionConfig("test", new ExpirationTimeCalculator() {
            @Override
            public ReadableDuration getExpirationTime() {
                return Duration.standardMinutes(5);
            }
        }));
        cache = cacheFactory.create();
    }

    // @Test
    public void testMultiThread() throws InterruptedException, IOException {
        multiThreadTestImpl(10, 100);
    }

    private void multiThreadTestImpl(int threadCount, int operationCount) throws InterruptedException {
        List<Callable<Void>> tests = new ArrayList<Callable<Void>>();

        Counter counter = new Counter(1000);

        for (int i = 0; i < threadCount; i++) {
            tests.add(new TestThread(cache, counter.next(), operationCount));
        }

        executorService.invokeAll(tests);
    }

    public static class TestThread implements Callable<Void> {

        private Cache cache;
        private long index;
        private int operationCount;

        public TestThread(Cache cache, long index, int operationCount) {
            this.cache = cache;
            this.index = index;
            this.operationCount = operationCount;
        }

        @Override
        public Void call() {
            try {
                cacheCase(cache, index, operationCount);
            } catch (RuntimeException e) {
                e.printStackTrace();
                throw e;
            } catch (Throwable t) {
                t.printStackTrace();
            }

            return null;
        }
    }

    public static void cacheCase(Cache cache, long index, int operationCount) {
        cacheTestWithSize(cache, index, operationCount);
    }

    private static  void cacheTestWithSize(Cache cache, long index, int size) {
        Random random = new Random(index);
        Long parentId = random.nextLong();

        List<SomeKey> keys = new ArrayList<SomeKey>();

        for (int i = 0; i < size; i++) {
            keys.add(setAndGetBean(cache, index, parentId, random));
        }

        SomeKey matchKey = new SomeKey();
        matchKey.setParentId(parentId);

        cache.removeByTags(Arrays.<Object>asList(EntityIdTag.create("ParentEntity", parentId)));

        for (SomeKey key : keys) {
            Assert.assertNull(cache.getRegion("test").get(key));
        }
    }

    private static SomeKey setAndGetBean(Cache cache, Long index, Long parentId, Random random) {
        List<String> stringField = Arrays.asList("test value " + String.valueOf(random.nextGaussian()));

        SomeEntity someEntity = createEntity(index.intValue(), stringField);
        SomeKey someKey = createKey(stringField, parentId);

        CacheRegion region = cache.getRegion("test");
        region.set(someKey, someEntity, Arrays.<Object>asList(EntityIdTag.create("ParentEntity", parentId)));

        SomeEntity entity = region.get(someKey);

        Assert.assertNotNull(entity);
        Assert.assertEquals(index.intValue(), entity.getIntField().intValue());
        Assert.assertEquals(stringField, entity.getStringsField());

        return someKey;
    }

    private static SomeKey createKey(List<String> stringField, Long parentId) {
        SomeKey someKey = new SomeKey();
        someKey.setParentId(parentId);
        someKey.setStringField(stringField);
        return someKey;
    }

    private static SomeEntity createEntity(int intField, List<String> stringField) {
        SomeEntity someEntity = new SomeEntity();
        someEntity.setIntField(intField);
        someEntity.setStringsField(stringField);
        return someEntity;
    }

}
