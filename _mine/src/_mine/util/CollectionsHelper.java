package _mine.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.TreeSet;

public class CollectionsHelper
{
	public static <K, V> HashMap<K, V> newHashMap() {
		return new HashMap<K, V>();
	}
	
	public static <K, V> TreeMap<K, V> newTreeMap() {
		return new TreeMap<K, V>();
	}
	
	public static <V> HashSet< V> newHashSet() {
		return new HashSet<V>();
	}
	
	public static <V> TreeSet<V> newTreeSet() {
		return new TreeSet<V>();
	}
	
	public static <V> ArrayList<V> newArrayList() {
		return new ArrayList<V>();
	}
	
	public static <V> ArrayList<V> newArrayList(int size) {
		return new ArrayList<V>(size);
	}
	
	public static <V> LinkedList<V> newLinkedList() {
		return new LinkedList<V>();
	}
	
	private CollectionsHelper() {
	}
}
