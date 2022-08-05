package com.foros.util;

import com.foros.util.bean.Filter;
import com.foros.util.mapper.Converter;
import com.foros.util.mapper.Mapper;
import com.foros.util.mapper.NoChangeConverter;
import com.foros.util.mapper.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.map.LazyMap;

public class CollectionUtils {
    /**
     * Basicaly this is a typed version of {@link org.apache.commons.collections.CollectionUtils} class.
     * <br/>
     * Filters the collection by applying a Filter to each element. If the
     * filter returns false, remove the element.
     * <p>
     * If the input collection or filter is null, there is no change made.
     *  @param collection  the collection to get the input from, may be null
     * @param filter  the filter to use as a filter, may be null
     */
    public static <C extends Collection<T>, T> C filter(C collection, Filter<T> filter) {
        if (collection != null && filter != null) {
            for (Iterator<T> it = collection.iterator(); it.hasNext();) {
                if (!filter.accept(it.next())) {
                    it.remove();
                }
            }
        }
        return collection;
    }

    public static <T> T find(Collection<T> collection, Filter<T> filter) {
        for (T t : collection) {
            if (filter.accept(t)) {
                return t;
            }
        }
        return null;
    }
    
    public static <T> Collection<T> subtract(final Collection<T> a, final Collection<T> b) {
        Collection<T> list = new ArrayList<T>( a );
        for (T aB : b) {
            list.remove(aB);
        }
        return list;
    }    

    /**
     * Returns true if collection is null or empty
     * TODO it needs to be removed after commons-collection 3.2 (using method CollectionUtils.isEmpty())
     * 
     * @param collection the collection to test
     * @return return true if collection is null or empty, false otherwise
     */
    public static boolean isNullOrEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static String toString(Collection collection) {
        StringBuilder strBuf = new StringBuilder();

        boolean isFirst = true;
        for (Object o : collection) {
            if (isFirst) {
                isFirst = false;
            } else {
                strBuf.append(", ");
            }
            strBuf.append(o.toString());
        }

        return strBuf.toString();
    }

    public static <T, K, V> Map<K, V> map(Mapper<T, K, V> mapper, Iterable<T> values) {
        return CollectionUtils.<K, V>map().items(mapper, values).build();
    }

    public static <T, K, V> Map<K, V> map(Mapper<T, K, V> mapper, T...values) {
        return map(mapper, Arrays.asList(values));
    }

    /** @noinspection unchecked*/
    public static <K, V> Map<K, V> lazyMap(final Converter<K, V> converter) {
        return LazyMap.decorate(new HashMap<K, V>(), new Transformer() {
            @Override
            public Object transform(Object input) {
                return converter.item((K) input);
            }
        });
    }

    public static interface MapBuilder<K, V> {
        public MapBuilder<K, V> map(K key, V value);
        public <T> MapBuilder<K, V> items(Mapper<T, K, V> mapper, Iterable<T> items);
        public Map<K, V> build();
    }

    private static abstract class AbstractMapBuilder<K, V> implements MapBuilder<K, V> {
        protected Map<K, V> map = new LinkedHashMap<K, V>();

        public MapBuilder<K, V> map(K key, V value) {
            put(key, value);
            return this;
        }

        @Override
        public <T> MapBuilder<K, V> items(Mapper<T, K, V> mapper, Iterable<T> items) {
            for (T item : items) {
                Pair<K, V> pair = mapper.item(item);
                put(pair.getLeftValue(), pair.getRightValue());
            }

            return this;
        }

        public Map<K, V> build() {
            return map;
        }

        protected abstract void put(K key, V value);
    }

    private static final class LocalizeMapBuilder<K> extends AbstractMapBuilder<K, String> {
        @Override
        protected void put(K key, String value) {
            map.put(key, StringUtil.getLocalizedString(value));
        }
    }

    private static final class MapBuilderImpl<K, V> extends AbstractMapBuilder<K, V> {
        @Override
        protected void put(K key, V value) {
            map.put(key, value);
        }
    }

    public static <K, V> MapBuilder<K, V> map(K key, V value) {
        return new MapBuilderImpl<K, V>().map(key, value);
    }

    public static <K> MapBuilder<K, String> localizeMap(K key, String value) {
        return new LocalizeMapBuilder<K>().map(key, value);
    }

    public static <K, V> MapBuilder<K, V> map() {
        return new MapBuilderImpl<K, V>();
    }

    public static <K> MapBuilder<K, String> localizeMap() {
        return new LocalizeMapBuilder<K>();
    }

    public static <V, R> List<R> convert(Iterable<V> values, com.foros.util.mapper.Converter<V, R> converter) {
        return convert(converter, values);
    }

    public static <V, R> List<R> convert(com.foros.util.mapper.Converter<V, R> converter, V...values) {
        return convert(converter, Arrays.asList(values));
    }

    public static <V, R> List<R> convert(com.foros.util.mapper.Converter<V, R> converter, Iterable<V> values) {
        List<R> result;
        if (values instanceof Collection<?>) {
            result = new ArrayList<R>(((Collection<?>) values).size());
        } else {
            result = new ArrayList<R>();
        }

        for (V value : values) {
            R r = converter.item(value);
            if (r != null) {
                result.add(r);
            }
        }

        return result;
    }

    public static <V> String toString(boolean quotesNeeded, Iterable<V> values) {
        return toString(new NoChangeConverter<V>(), quotesNeeded, values);
    }

    public static <V, R> String toString(com.foros.util.mapper.Converter<V, R> converter, boolean quotesNeeded, Iterable<V> values) {
        StringBuilder builder = new StringBuilder();

        boolean isFirst = true;
        for (V value : values) {
            R result = converter.item(value);

            if (isFirst) {
                isFirst = false;
            } else {
                builder.append(", ");
            }

            if (!quotesNeeded) {
                builder.append(result);
            } else {
                builder.append("\"").append(result).append("\"");
            }
        }

        return builder.toString();
    }

    public static <T> boolean containsAny(Collection<T> where, Iterable<T> what) {
        for (T value : what) {
            if (where.contains(value)) {
                return true;
            }
        }

        return false;
    }

    public static <T> String join(Iterable<T> iterable, String separator) {
        return join(iterable, separator, new com.foros.util.mapper.Converter<T, String>() {
            @Override
            public String item(T value) {
                return value == null ? null : value.toString();
            }
        });
    }

    public static <T> String join(Iterable<T> iterable, String separator, com.foros.util.mapper.Converter<T, String> converter) {
        if (iterable == null) {
            return null;
        }

        StringBuilder res = new StringBuilder();
        boolean first = true;
        for (T t : iterable) {
            if (!first) {
                res.append(separator);
            }
            res.append(converter.item(t));
            first = false;
        }
        return res.toString();
    }

    public static <T> List<T> resize(List<T> list, int index) {
        if (index >= list.size()) {
            for (int i = list.size(); i <= index; i++) {
                list.add(null);
            }
        }
        return list;
    }

    public static <T> Iterable<T> asIterable(final Iterator<T> iterator) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return iterator;
            }
        };
    }

    /**
     * Returns empty collection if argument is null or argument itself otherwise
     */
    public static <T> Collection<T> safe(final Collection<T> collection) {
        return collection == null ? Collections.<T>emptyList() : collection;
    }

    public static class MultiMapBuilder<K, V> {
        private Map<K, List<V>> sourceMap;
        private Map<K, List<V>> decoratedMap;

        public MultiMapBuilder() {
            sourceMap = new HashMap<>();
            decorate();
        }

        public MultiMapBuilder(int initialCapacity) {
            sourceMap = new HashMap<>(initialCapacity);
            decorate();
        }

        public void put(final K key, final V value) {
            decoratedMap.get(key).add(value);
        }

        public Map<K, List<V>> build() {
            return sourceMap;
        }

        private void decorate() {
            //noinspection unchecked
            decoratedMap = LazyMap.decorate(sourceMap, new Transformer() {
                @Override
                public Object transform(Object input) {
                    return new LinkedList<V>();
                }
            });
        }
    }
}
