package com.me.job.march.java;

import java.util.*;

/**
 * 三者都提供了 Key->value（键值对）的映射和過历 key 的迭代器。这些类中最大的区别就
 * 是给予的时间保证和 key 的顺序。
 * 口 HashMap提供了O(1)的查找和插人。如果你要遍历key时，要清楚 key 其实是无序的。
 * 它是用节点为链表的数组实现的。
 * 口 TreeMap提供了O(logN)的查找和插人。但key是有序的，如果你想要按顺序遍历 key,
 * 那么它刚好满足。这也意味着key必须实现了Comparable接口。TreeMap是用红黑树
 * 实现的。
 * 口 LinkedHashMap 提供了O(1)的查找和插人。key是按照插人顺序排序的。它是用双向链
 * 表桶实现的。
 */
public class MapDemo {
    public static void main(String[] args) {
        TreeMap<Integer, String> treeMap = new TreeMap<>();
        HashMap<Integer, String> hashMap = new HashMap<>();
        LinkedHashMap<Integer, String> linkedHashMap = new LinkedHashMap<>();

        System.out.println("\nHashMap - Arbitrary Order:");
        insertAndPrint(hashMap);

        System.out.println("\nLinkedHashMap - Insertion Order:");
        insertAndPrint(linkedHashMap);

        System.out.println("\nTreeMap - Natural Order:");
        insertAndPrint(treeMap);
    }

    public static void insertAndPrint(AbstractMap<Integer, String> map)  {
        int[] array = {1, -1, 0};
        Arrays.stream(array).forEachOrdered(i -> map.put(i, Integer.toString(i)));
        map.keySet().forEach(System.out::println);
    }
}
