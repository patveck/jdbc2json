package com.pascalvaneck.jdbc2json.util;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public final class ListUtils {

    private ListUtils() {}

    @Nonnull
    public static <T> List<List<T>> allPermutations(@Nonnull List<T> list) {
        if (list.size() == 1) {
            return Collections.singletonList(Collections.singletonList(list.get(0)));
        }
        final LinkedList<List<T>> result = new LinkedList<>();
        for (T item : list) {
            for (List<T> onePermutation : allPermutations(listWithoutOneElement(list, item))) {
                final LinkedList<T> subresult = new LinkedList<>(Collections.singletonList(item));
                subresult.addAll(onePermutation);
                result.add(subresult);
            }
        }
        return result;
    }

    @Nonnull
    public static <T> List<T> listWithoutOneElement(@Nonnull final List<T> list, final T item) {
        return list.stream().filter(item2 -> item2 != item).collect(Collectors.toList());
    }

    @Nonnull
    public static <T> List<T> listWithoutList(@Nonnull final List<T> list1, @Nonnull final List<T> list2) {
        return list1.stream().filter(item -> !list2.contains(item)).collect(Collectors.toList());
    }

    @Nonnull
    public static List<String> parseString(@Nonnull final String s, @Nonnull final String regex) {
        if ("".equals(s)) {
            return Collections.emptyList();
        }
        if ("".equals(regex)) {
            return Collections.singletonList(s);
        }
        final List<String> result = new ArrayList<>();
        final String[] names = s.split(regex);
        Collections.addAll(result, names);
        return result;
    }
}
