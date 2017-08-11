package com.pascalvaneck.mysql2json.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ListUtilsTest {

    @Test
    public void testRemoveFirstElement() {
        List<String> myList = new ArrayList<>(Arrays.asList("a", "b"));
        List<String> result = ListUtils.listWithoutOneElement(myList, "a");
        assertEquals("Result has one element", 1, result.size());
        assertEquals("Only element is b", "b", result.get(0));
    }

    @Test
    public void testRemoveSecondElement() {
        List<String> myList = new ArrayList<>(Arrays.asList("a", "b"));
        List<String> result = ListUtils.listWithoutOneElement(myList, "b");
        assertEquals("Result has one element", 1, result.size());
        assertEquals("Only element is a", "a", result.get(0));
    }

    @Test
    public void testRemoveOnlyElement() {
        List<String> myList = new ArrayList<>(Collections.singletonList("a"));
        List<String> result = ListUtils.listWithoutOneElement(myList, "a");
        assertEquals("Result has no elements", 0, result.size());
    }

    @Test
    public void testRemoveNoElement() {
        List<String> myList = new ArrayList<>(Arrays.asList("a", "b"));
        List<String> result = ListUtils.listWithoutOneElement(myList, "c");
        assertEquals("Result has two elements", 2, result.size());
        assertEquals("First element is a", "a", result.get(0));
        assertEquals("Second element is b", "b", result.get(1));
    }

    @Test
    public void testRemoveFromEmptyList() {
        List<String> myList = Collections.emptyList();
        List<String> result = ListUtils.listWithoutOneElement(myList, "a");
        assertEquals("Result has no elements", 0, result.size());
    }

    @Test
    public void testRemoveMiddleElement() {
        List<String> myList = new ArrayList<>(Arrays.asList("a", "b", "c"));
        List<String> result = ListUtils.listWithoutOneElement(myList, "b");
        assertEquals("Result has two elements", 2, result.size());
        assertEquals("First element is a", "a", result.get(0));
        assertEquals("Second element is c", "c", result.get(1));
    }

    @Test
    public void allPermutationsEmptyList() {
        List<String> myList = Collections.emptyList();
        assertEquals("allPermutations on empty list returns list of empty list", 0,
            ListUtils.allPermutations(myList).size());
    }

    @Test
    public void allPermutationsSingleton() {
        List<String> myList = new ArrayList<>(Collections.singletonList("a"));
        assertEquals("allPermutations on singleton returns list with that singleton", 1,
            ListUtils.allPermutations(myList).size());
        assertEquals( "Only permutation of singleton is that singleton (size)", 1,
            ListUtils.allPermutations(myList).get(0).size());
        assertEquals( "Only permutation of singleton is that singleton (element)", "a",
            ListUtils.allPermutations(myList).get(0).get(0));
    }

    @Test
    public void allPermutationsTwoItemList() {
        List<String> myList = new ArrayList<>(Arrays.asList("a", "b"));
        assertEquals("allPermutations on two item list returns four items", 2,
            ListUtils.allPermutations(myList).size());
    }

    @Test
    public void allPermutationsThreeItemList() {
        List<String> myList = new ArrayList<>(Arrays.asList("a", "b", "c"));
        assertEquals("allPermutations on two item list returns four items", 6,
            ListUtils.allPermutations(myList).size());
    }

    @Test
    public void allPermutationsFourItemList() {
        List<String> myList = new ArrayList<>(Arrays.asList("a", "b", "c", "d"));
        assertEquals("allPermutations on two item list returns four items", 24,
            ListUtils.allPermutations(myList).size());
    }

}
