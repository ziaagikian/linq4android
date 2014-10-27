/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.linq4android.collections;

import java.util.*;

/**
 * This class is used to provide operations on <b>Collections</b>.
 * 
 * @author engr-ziaa
 */

public class CollectionUtils {

	/**
	 * Searches the specified list for specified object by specified key
	 * selector and comparator. The list must be sorted into ascending order
	 * according to the specified key selector and comparator(as by the
	 * <code>Enumerable.orderby</code> method), prior to making this call. If it
	 * is not sorted, the results are undefined. If the list contains multiple
	 * elements their key equals to the specified key, there is no guarantee
	 * which one will be found.
	 *
	 * @param <T> the generic type
	 * @param <TKey> the generic type
	 * @param source            the list to be searched
	 * @param key            the key to be searched for
	 * @param selector            the selector for selecting key of element
	 * @param comparator            the comparator by which the list is ordered.
	 * @return the index of the search key, if it is contained in the list;
	 *         otherwise, (-(insertion point) - 1). The insertion point is
	 *         defined as the point at which the key would be inserted into the
	 *         list: the index of the first element greater than the key, or
	 *         list.size() if all elements in the list are less than the
	 *         specified key. Note that this guarantees that the return value
	 *         will be >= 0 if and only if the key is found.
	 */
	public static <T, TKey> int binarySearchBy(List<T> source, TKey key,
			Selector<T, TKey> selector, Comparator<TKey> comparator) {
		int low = 0;
		int high = source.size() - 1;
		while (low <= high) {
			int middle = low + ((high - low) >> 1);
			T item = source.get(middle);
			TKey value = selector.select(item);
			int cmp = comparator.compare(value, key);
			if (cmp == 0) {
				return middle;
			}
			if (cmp < 0) {
				low = middle + 1;
			} else {
				high = middle - 1;
			}
		}
		return ~low;

	}

	/**
	 * Searches the specified list for specified object by specified key
	 * selector. The list must be sorted into ascending order according to the
	 * specified key selector and comparator(as by the
	 * <code>Enumerable.orderby</code> method), prior to making this call. If it
	 * is not sorted, the results are undefined. If the list contains multiple
	 * elements their key equals to the specified key, there is no guarantee
	 * which one will be found.
	 *
	 * @param <T> the generic type
	 * @param <TKey> the generic type
	 * @param source            the list to be searched
	 * @param key            the key to be searched for
	 * @param selector            the selector for selecting key of element
	 * @return the index of the search key, if it is contained in the list;
	 *         otherwise, (-(insertion point) - 1). The insertion point is
	 *         defined as the point at which the key would be inserted into the
	 *         list: the index of the first element greater than the key, or
	 *         list.size() if all elements in the list are less than the
	 *         specified key. Note that this guarantees that the return value
	 *         will be >= 0 if and only if the key is found.
	 */
	public static <T, TKey> int binarySearchBy(List<T> source, TKey key,
			Selector<T, TKey> selector) {
		return binarySearchBy(source, key, selector,
				new NaturalComparator<TKey>());
	}
}
