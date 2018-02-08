package com.foodme.util;

import one.util.streamex.StreamEx;

import java.util.Collection;

public class StreamUtils {
    /**
     * Null safe stream creator
     *
     * @param <T> the type of collection elements
     * @param collection collection to create the stream of
     * @return a sequential {@code StreamEx} over the elements in given
     *         collection
     */
    public static <T> StreamEx<T> streamOf(Collection<? extends T> collection) {
        return collection == null ? StreamEx.empty() : StreamEx.of(collection);
    }
}
