package com.pascalvaneck.mysql2json.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ListUtils {

    public static <T> List<List <T>> allPermutations(List<T> list) {
        if (list.size() == 1) {
            return Collections.singletonList(Collections.singletonList(list.get(0)));
        }
        LinkedList<List <T>> result = new LinkedList<>();
        for (T item : list) {
            for (List<T> onePermutation : allPermutations(listWithoutOneElement(list, item))) {
                LinkedList<T> subresult = new LinkedList<>(Collections.singletonList(item));
                subresult.addAll(onePermutation);
                result.add(subresult);
            }
        }
        return result;
    }

    static <T> List<T> listWithoutOneElement(List<T> list, T item) {
        return list.stream().filter(item2 -> item2 != item).collect(Collectors.toList());
    }

}
