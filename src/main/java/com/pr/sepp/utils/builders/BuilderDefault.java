package com.pr.sepp.utils.builders;

import com.google.common.base.Optional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

/**
 * @author : lchen
 * @date : 2019/6/19
 */
public final class BuilderDefault {
    private BuilderDefault() {
    }

    public static <T> T defaultIfAbsent(T newValue, T defaultValue) {
        return Optional.fromNullable(newValue)
                .or(Optional.fromNullable(defaultValue))
                .orNull();
    }

    public static <T> List<T> nullToEmptyList(Collection<T> newValue) {
        if (newValue == null) {
            return newArrayList();
        }
        return newArrayList(newValue);
    }

    public static <K, V> Map<K, V> nullToEmptyMap(Map<K, V> newValue) {
        if (newValue == null) {
            return newHashMap();
        }
        return newValue;
    }

    public static <T> Stream<T> from(Collection<T> source) {
        return source.stream();
    }

    public static <K, V> MapBuilder<K, V> asMapBuilder(Map<K, V> map) {
        return new MapBuilder<>(map);
    }

    public static class MapBuilder<K, V> {

        private Map<K, V> map;

        MapBuilder(Map<K, V> map) {
            this.map = map;
        }

        public MapBuilder<K, V> put(K k, V v) {
            map.put(k, v);
            return this;
        }

        public Map<K, V> build() {
            return map;
        }

    }
}
