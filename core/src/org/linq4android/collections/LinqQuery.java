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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.TreeMap;
import java.util.TreeSet;

import org.linq4android.dependencies.java7.Objects;
import org.linq4android.exceptions.DuplicateKeyException;
/**
 * Provides capabilities to make query on {@code java.util.Iterable<T>} or an array
 * with chainable methods
 * 
 * @author ziaagikian
 * 
 * @param <T>
 *            The element type of collection
 */
public class LinqQuery<T> implements Iterable<T> {

	/** The m source itr. */
	private Iterable<T> mSourceItr;

	/**
	 * Create a new Query by specified <Iterable<T>>.
	 *
	 * @param sourceItr            The source collection
	 */
	LinqQuery(Iterable<T> sourceItr) {
		if (sourceItr != null) {
			mSourceItr = sourceItr;
		} else {
			throw new IllegalArgumentException("source iterator is null");
		}
	}

	/**
	 * Create a new Query by specified array.
	 *
	 * @param sourceItr the source itr
	 */
	LinqQuery(T[] sourceItr) {
		if (sourceItr == null) {
			throw new IllegalArgumentException("source iterator is null");
		}
		this.mSourceItr = new IterableArray<T>(sourceItr);
	}

	/**
	 * The Interface IRandomAccessor2.
	 *
	 * @param <T> the generic type
	 */
	private interface IRandomAccessor2<T> {
		
		/**
		 * Gets the size.
		 *
		 * @return the size
		 */
		int getSize();

		/**
		 * Gets the.
		 *
		 * @param index the index
		 * @return the t
		 */
		T get(int index);
	}

	/**
	 * The Class ListRandomAccessor.
	 */
	private class ListRandomAccessor implements IRandomAccessor2<T> {
		
		/** The m source list. */
		private List<T> mSourceList;

		/**
		 * Instantiates a new list random accessor.
		 *
		 * @param source the source
		 */
		public ListRandomAccessor(List<T> source) {
			this.mSourceList = source;
		}

		/* (non-Javadoc)
		 * @see org.linq4android.collections.Linq.IRandomAccessor2#getSize()
		 */
		@Override
		public int getSize() {
			return this.mSourceList.size();
		}

		/* (non-Javadoc)
		 * @see org.linq4android.collections.Linq.IRandomAccessor2#get(int)
		 */
		@Override
		public T get(int index) {
			return this.mSourceList.get(index);
		}

	}

	/**
	 * The Class ArrayRandomAccessor.
	 */
	private class ArrayRandomAccessor implements IRandomAccessor2<T> {
		
		/** The m source. */
		private T[] mSource;

		/**
		 * Instantiates a new array random accessor.
		 *
		 * @param source the source
		 */
		public ArrayRandomAccessor(T[] source) {
			this.mSource = source;
		}

		/* (non-Javadoc)
		 * @see org.linq4android.collections.Linq.IRandomAccessor2#getSize()
		 */
		@Override
		public int getSize() {
			return this.mSource.length;
		}

		/* (non-Javadoc)
		 * @see org.linq4android.collections.Linq.IRandomAccessor2#get(int)
		 */
		@Override
		public T get(int index) {
			return this.mSource[index];
		}

	}

	/**
	 * Checks if is random accessable.
	 *
	 * @param obj the obj
	 * @return true, if is random accessable
	 */
	private boolean isRandomAccessable(Object obj) {
		return obj instanceof List<?> || obj.getClass().isArray()
				|| obj instanceof IterableArray<?>;
	}

	/**
	 * Creates the random accessor.
	 *
	 * @param source the source
	 * @return the i random accessor2
	 */
	@SuppressWarnings("unchecked")
	private IRandomAccessor2<T> createRandomAccessor(Object source) {
		if (source instanceof List<?>) {
			return new ListRandomAccessor((List<T>) source);
		} else if (source.getClass().isArray()) {
			return new ArrayRandomAccessor((T[]) source);
		} else if (source instanceof IterableArray<?>) {
			return new ArrayRandomAccessor(
					((IterableArray<T>) source).getSource());
		}

		throw new UnsupportedOperationException();
	}

	/**
	 * Projects each element of a sequence into a new form.
	 *
	 * @param <TResult> the generic type
	 * @param selector            A transform {@code Selector<T, TResult>} to apply to each
	 *            element.
	 * @return A {@code Query<T>} whose elements are the result of invoking the
	 *         transform function on each element of source.
	 */
	public <TResult> LinqQuery<TResult> select(Selector<T, TResult> selector) {
		Iterable<TResult> rs = new SelectIterable<TResult>(this.mSourceItr,
				selector);
		return new LinqQuery<TResult>(rs);

	}

	/**
	 * The Class SelectIterable.
	 *
	 * @param <TResult> the generic type
	 */
	private class SelectIterable<TResult> implements Iterable<TResult> {

		/** The m source itr. */
		private Iterable<T> mSourceItr;
		
		/** The m selector. */
		private Selector<T, TResult> mSelector;

		/**
		 * Instantiates a new select iterable.
		 *
		 * @param source the source
		 * @param selector the selector
		 */
		public SelectIterable(Iterable<T> source, Selector<T, TResult> selector) {
			this.mSourceItr = source;
			this.mSelector = selector;
		}

		/* (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		@Override
		public Iterator<TResult> iterator() {
			return new SelectIterator<TResult>(this.mSourceItr, this.mSelector);
		}
	}

	/**
	 * The Class SelectIterator.
	 *
	 * @param <TResult> the generic type
	 */
	private class SelectIterator<TResult> implements Iterator<TResult> {

		/** The m source itr. */
		private Iterator<T> mSourceItr;
		
		/** The m selector. */
		private Selector<T, TResult> mSelector;

		/**
		 * Instantiates a new select iterator.
		 *
		 * @param source the source
		 * @param selector the selector
		 */
		public SelectIterator(Iterable<T> source, Selector<T, TResult> selector) {
			this.mSourceItr = source.iterator();
			this.mSelector = selector;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return mSourceItr.hasNext();
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public TResult next() {
			T item = this.mSourceItr.next();
			TResult rs = this.mSelector.select(item);
			return rs;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();

		}

	}

	/**
	 * Returns distinct elements from a sequence by using hash code to compare
	 * values.
	 * 
	 * @return A {@code Query<T>} that contains distinct elements from the
	 *         source sequence.
	 */
	public LinqQuery<T> distinct() {
		HashSet<T> rs = new HashSet<T>();
		for (T item : this.mSourceItr) {
			rs.add(item);
		}
		return new LinqQuery<T>(rs);
	}

	/**
	 * Returns distinct elements from a sequence by using a specified
	 * {@code Comparator<T>} to compare values.
	 * 
	 * @param comparator
	 *            A {@code Comparator<T>} to compare values
	 * @return A {@code Query<T>}that contains distinct elements from the
	 *         sequence.
	 */
	public LinqQuery<T> distinct(Comparator<T> comparator) {
		TreeSet<T> rs = new TreeSet<T>(comparator);
		for (T item : this.mSourceItr) {
			rs.add(item);
		}
		return new LinqQuery<T>(rs);

	}

	/**
	 * Filters a sequence of values based on a predicate.
	 *
	 * @param predicate            A {@code Predicate<T>} to test each element for a condition.
	 * @return An {@code Query<T>} that contains elements from the input
	 *         sequence that satisfy the condition.
	 * @throws Exception the exception
	 */

	public LinqQuery<T> where(Predicate<T> predicate) throws Exception {
		WhereIterable rs = new WhereIterable(this.mSourceItr, predicate);
		return new LinqQuery<T>(rs);
	}

	/**
	 * The Class WhereIterable.
	 */
	private class WhereIterable implements Iterable<T> {

		/** The m source. */
		private Iterable<T> mSource;
		
		/** The m predicate. */
		private Predicate<T> mPredicate;

		/**
		 * Instantiates a new where iterable.
		 *
		 * @param source the source
		 * @param predicate the predicate
		 */
		public WhereIterable(Iterable<T> source, Predicate<T> predicate) {
			this.mPredicate = predicate;
			this.mSource = source;
		}

		/* (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		@Override
		public Iterator<T> iterator() {
			return new WhereIterator(this.mSource, this.mPredicate);
		}

	}

	/**
	 * The Class WhereIterator.
	 */
	private class WhereIterator implements Iterator<T> {

		/** The m sourceitr. */
		private Iterator<T> mSourceitr;
		
		/** The m predicate. */
		private Predicate<T> mPredicate;
		
		/** The m current. */
		private T mCurrent;

		// 0: need To Find Next, 1: return current, 2 : end
		/** The m state. */
		private int mState = 0;

		/**
		 * Instantiates a new where iterator.
		 *
		 * @param source the source
		 * @param predicate the predicate
		 */
		public WhereIterator(Iterable<T> source, Predicate<T> predicate) {
			this.mPredicate = predicate;
			this.mSourceitr = source.iterator();
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			if (mState == 0) {
				this.findNext();
			}

			return mState != 2;
		}

		/**
		 * Find next.
		 *
		 * @throws UnsupportedOperationException the unsupported operation exception
		 */
		private void findNext() throws UnsupportedOperationException {
			while (this.mState == 0) {
				if (this.mSourceitr.hasNext()) {
					T item = this.mSourceitr.next();
					try {
						if (this.mPredicate.evaluate(item)) {
							this.mCurrent = item;
							this.mState = 1;
						}
					} catch (Exception e) {
						throw new UnsupportedOperationException(e);
					}

				} else {
					this.mState = 2;
				}
			}

		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public T next() {
			if (mState == 0) {
				this.findNext();
			}
			if (mState == 2) {
				throw new NoSuchElementException();
			}
			T rs = this.mCurrent;
			this.mState = 0;
			return rs;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();

		}
	}

	/**
	 * Returns the first element of a sequence, or a default value if the
	 * sequence contains no elements.
	 *
	 * @return The first element of this sequence or null if the collection is
	 *         empty.
	 * @throws Exception the exception
	 */
	public T firstOrDefault() throws Exception {
		return this.firstOrDefault(new TruePredicate<T>());
	}

	/**
	 * Returns the first element of the sequence that satisfies a condition or
	 * <code>null</code> value if no such element is found.
	 *
	 * @param predicate            A {@code Predicate<T>} to test each element for a condition.
	 * @return null if source is empty or if no element passes the test
	 *         specified by predicate; otherwise, the first element in source
	 *         that passes the test specified by predicate.
	 * @throws Exception the exception
	 */
	public T firstOrDefault(Predicate<T> predicate) throws Exception {
		for (T item : this.mSourceItr) {
			if (predicate.evaluate(item)) {
				return item;
			}

		}
		return null;
	}

	/**
	 * The Class TruePredicate.
	 *
	 * @param <T2> the generic type
	 */
	static class TruePredicate<T2> implements Predicate<T2> {
		
		/* (non-Javadoc)
		 * @see org.linq4android.collections.Predicate#evaluate(java.lang.Object)
		 */
		@Override
		public boolean evaluate(T2 obj) throws Exception {
			return true;
		}
	}

	/**
	 * Returns the last element of the sequence.
	 *
	 * @return The value at the last position in the sequence.
	 * @throws Exception the exception
	 */
	public T last() throws Exception {
		return this.last(new TruePredicate<T>());
	}

	/**
	 * Returns the last element of a sequence that satisfies a specified
	 * condition.
	 *
	 * @param predicate            A {@code Predicate<T>} to test each element for a condition.
	 * @return The last element in the sequence that passes the test in the
	 *         specified {@code Predicate<T>}.
	 * @throws Exception the exception
	 */
	public T last(Predicate<T> predicate) throws Exception {
		T rs = lastOrDefault(predicate);
		if (rs == null) {
			throw new IllegalStateException("No such element in collection");
		}
		return rs;
	}

	/**
	 * Returns the last element of the sequence, or a <code>null</code> if the
	 * sequence contains no elements.
	 *
	 * @return <code>null</code> if the sequence is empty; otherwise, the last
	 *         element in the {@code Query<T>}.
	 * @throws Exception the exception
	 */
	public T lastOrDefault() throws Exception {
		return this.lastOrDefault(new TruePredicate<T>());
	}

	/**
	 * Returns the last element of the sequence that satisfies a condition or
	 * <code>null</code> if no such element is found.
	 *
	 * @param predicate            A {@code Predicate<T>} to test each element for a condition.
	 * @return <code>null</code> if the sequence is empty or if no elements pass
	 *         the test in the {@code Predicate<T>}; otherwise, the last element
	 *         that passes the test in the {@code Predicate<T>}.
	 * @throws Exception the exception
	 */

	public T lastOrDefault(Predicate<T> predicate) throws Exception {
		for (T item : this.reverse()) {
			if (predicate.evaluate(item)) {
				return item;
			}
		}

		return null;
	}

	/**
	 * Returns the first element of the sequence.
	 *
	 * @return The first element in the sequence.
	 * @throws Exception the exception
	 */
	public T first() throws Exception {
		return this.first(new TruePredicate<T>());
	}

	/**
	 * Returns the first element in the sequence that satisfies a specified
	 * condition.
	 *
	 * @param predicate            A {@code Predicate<T>} to test each element for a condition.
	 * @return The first element in the sequence that passes the test in the
	 *         specified {@code Predicate<T>}.
	 * @throws Exception the exception
	 */
	public T first(Predicate<T> predicate) throws Exception {
		T rs = firstOrDefault(predicate);
		if (rs == null) {
			throw new IllegalStateException("No such element in collection");
		}
		return rs;

	}

	/**
	 * Filters the elements of an Enumerable based on a specified type.
	 *
	 * @param <T2> the generic type
	 * @param cls            The type to filter the elements of the sequence on.
	 * @return A {@code Query<T>}that contains elements from the sequence of
	 *         type TResult.
	 * @throws Exception the exception
	 */
	public <T2> LinqQuery<T2> ofType(final Class<T2> cls) throws Exception {

		return this.where(new Predicate<T>() {

			@Override
			public boolean evaluate(T obj) throws Exception {

				return cls.isInstance(obj);
			}
		}).cast(cls);

	}

	/**
	 * Converts the elements of an IEnumerable to the specified type.
	 *
	 * @param <T2> the generic type
	 * @param t2            The type to convert the elements of source to
	 * @return A {@code Query<T>} that contains each element of the source
	 *         sequence converted to the specified type.
	 */

	public <T2> LinqQuery<T2> cast(final Class<T2> t2) {

		return this.select(new Selector<T, T2>() {

			@Override
			public T2 select(T item) {
				return t2.cast(item);
			}
		});

	}

	/**
	 * Returns the only element of the sequence, and throws an exception if
	 * there is not exactly one element in the sequence.
	 *
	 * @return The single element of the input sequence.
	 * @throws Exception the exception
	 */
	public T single() throws Exception {
		return this.single(new TruePredicate<T>());
	}

	/**
	 * Returns the only element of the sequence that satisfies a specified
	 * condition, and throws an exception if more than one such element exists.
	 *
	 * @param predicate            a {@code Predicate<T>} to test an element for a condition.
	 * @return The single element of the sequence that satisfies a condition.
	 * @throws Exception the exception
	 */
	public T single(Predicate<T> predicate) throws Exception {
		T rs = singleOrDefault(predicate);

		if (rs == null) {
			throw new IllegalStateException(
					"No elements match the predicate in collection");
		}

		return rs;
	}

	/**
	 * Returns the only element of a sequence, or <code>null</code> if the
	 * sequence is empty; this method throws an exception if there is more than
	 * one element in the sequence.
	 *
	 * @return The single element of the sequence, or <code>null</code> if the
	 *         sequence contains no elements.
	 * @throws Exception the exception
	 */
	public T singleOrDefault() throws Exception {
		return this.singleOrDefault(new TruePredicate<T>());
	}

	/**
	 * Returns the only element of the sequence that satisfies a specified
	 * condition or <code>null</code> if no such element exists; this method
	 * throws an exception if more than one element satisfies the condition.
	 *
	 * @param predicate            A {@code Predicate<T>} to test an element for a condition.
	 * @return The single element of the input sequence that satisfies the
	 *         condition, or <code>null</code> if no such element is found.
	 * @throws Exception the exception
	 */
	public T singleOrDefault(Predicate<T> predicate) throws Exception {
		T rs = null;
		for (T item : this.mSourceItr) {
			if (predicate.evaluate(item)) {
				if (rs == null) {
					rs = item;
				} else {
					throw new IllegalStateException(
							"More than one elements match the predicate in collection");
				}
			}
		}
		return rs;
	}

	/**
	 * Creates an {@code ArrayList <T>} from A {@code Query<T>}.
	 * 
	 * @return An {@code ArrayList <T>} that contains elements from the
	 *         sequence.
	 */
	public ArrayList<T> toArrayList() {
		ArrayList<T> rs = new ArrayList<T>();
		for (T item : this.mSourceItr) {
			rs.add(item);
		}
		return rs;
	}

	/**
	 * Creates an array from A {@code Query<T>}.
	 *
	 * @param <T2> the generic type
	 * @param a            the array into which the elements of the list are to be
	 *            stored, if it is big enough; otherwise, a new array of the
	 *            same runtime type is allocated for this purpose.
	 * @return An array that contains elements from the sequence.
	 */
	public <T2> T2[] toArray(T2[] a) {
		return this.toArrayList().toArray(a);
	}

	/**
	 * Sorts the elements of the sequence in descending order according to a
	 * key.
	 *
	 * @param <TKey> the generic type
	 * @param keySelector            A {@code Selector<T, TResult>} to extract a key from an
	 *            element.
	 * @return A {@code Query<T>} whose elements are sorted in descending order
	 *         according to a key.
	 */
	public <TKey> LinqQuery<T> orderByDescending(Selector<T, TKey> keySelector) {
		return this.orderByDescending(keySelector, null);
	}

	/**
	 * Sorts the elements of the sequence in descending order by using a
	 * specified {@code Comparator<T>}.
	 *
	 * @param <TKey> the generic type
	 * @param keySelector            A {@code Selector<T, TResult>} to extract a key from an
	 *            element.
	 * @param comparator            A {@code Comparator<T>}to compare keys.
	 * @return A {@code Query<T>} whose elements are sorted in descending order
	 *         according to a key.
	 */
	public <TKey> LinqQuery<T> orderByDescending(Selector<T, TKey> keySelector,
			Comparator<TKey> comparator) {
		KeyComparator<TKey> kc = new KeyComparator<TKey>();
		kc.keySelector = keySelector;
		kc.innerComparator = comparator != null ? comparator
				: new NaturalComparator<TKey>();

		OrderByIterable rs = new OrderByIterable(this.mSourceItr,
				new ReverseComparator<T>(kc));
		return new LinqQuery<T>(rs);
	}

	/**
	 * Sorts the elements of the sequence in ascending order according to a key.
	 *
	 * @param <TKey> the generic type
	 * @param keySelector            A {@code Selector<T, TResult>} to extract a key from an
	 *            element.
	 * @return A {@code Query<T>}whose elements are sorted according to a key.
	 */

	public <TKey> LinqQuery<T> orderBy(Selector<T, TKey> keySelector) {
		return this.orderBy(keySelector, null);
	}

	/**
	 * Sorts the elements of a sequence in ascending order by using a specified
	 * {@code Comparator<T>}.
	 *
	 * @param <TKey> the generic type
	 * @param keySelector            A {@code Selector<T, TResult>} to extract a key from an
	 *            element.
	 * @param comparator            A {@code Comparator<T>}to compare keys.
	 * @return A {@code Query<T>} whose elements are sorted according to a key.
	 */
	public <TKey> LinqQuery<T> orderBy(Selector<T, TKey> keySelector,
			Comparator<TKey> comparator) {

		KeyComparator<TKey> kc = new KeyComparator<TKey>();
		kc.keySelector = keySelector;
		kc.innerComparator = comparator != null ? comparator
				: new NaturalComparator<TKey>();
		OrderByIterable rs = new OrderByIterable(this.mSourceItr, kc);

		return new LinqQuery<T>(rs);
	}

	/**
	 * The Class OrderByIterable.
	 */
	private class OrderByIterable implements Iterable<T> {

		/** The m source. */
		private Iterable<T> mSource;
		
		/** The m comparer. */
		private Comparator<T> mComparer;

		/**
		 * Instantiates a new order by iterable.
		 *
		 * @param source the source
		 * @param comparator the comparator
		 */
		public OrderByIterable(Iterable<T> source, Comparator<T> comparator) {
			this.mSource = source;
			this.mComparer = comparator;
		}

		/* (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		@Override
		public Iterator<T> iterator() {
			return new OrderByIterator(this.mSource, this.mComparer);
		}
	}

	/**
	 * The Class OrderByIterator.
	 */
	private class OrderByIterator implements Iterator<T> {

		/** The m list. */
		private ArrayList<T> mList;

		/** The m index. */
		private int mIndex = 0;
		
		/** The m comparer. */
		private Comparator<T> mComparer;

		/**
		 * Instantiates a new order by iterator.
		 *
		 * @param source the source
		 * @param comparator the comparator
		 */
		public OrderByIterator(Iterable<T> source, Comparator<T> comparator) {
			mList = new ArrayList<T>();
			for (T item : source) {
				mList.add(item);
			}
			this.mComparer = comparator;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return mIndex < mList.size();
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public T next() {
			if (mIndex >= mList.size()) {
				throw new NoSuchElementException();
			}

			T rs;
			for (int i = this.mList.size() - 1; i > this.mIndex; i--) {
				T x = this.mList.get(i);
				T y = this.mList.get(i - 1);
				if (this.mComparer.compare(x, y) < 0) {
					this.mList.set(i, y);
					this.mList.set(i - 1, x);
				}
			}

			rs = this.mList.get(this.mIndex);
			this.mIndex++;
			return rs;

		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();

		}

	}

	/**
	 * The Class KeyComparator.
	 *
	 * @param <TKey> the generic type
	 */
	private class KeyComparator<TKey> implements Comparator<T> {
		
		/** The key selector. */
		public Selector<T, TKey> keySelector;
		
		/** The inner comparator. */
		public Comparator<TKey> innerComparator;

		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(T o1, T o2) {
			TKey k1 = keySelector.select(o1);
			TKey k2 = keySelector.select(o2);
			return innerComparator.compare(k1, k2);
		}
	}

	/**
	 * Projects each element of the sequence to A {@code Query<T>}and flattens
	 * the resulting sequences into one sequence.
	 *
	 * @param <T2> the generic type
	 * @param selector            A transform {@code Selector<T, TResult>} to apply to each
	 *            element.
	 * @return A {@code Query<T>} whose elements are the result of invoking the
	 *         one-to-many transform function on each element of the input
	 *         sequence.
	 */

	public <T2> LinqQuery<T2> selectMany(Selector<T, Iterable<T2>> selector) {
		Iterable<T2> rs = new FromIterable<T2>(this.mSourceItr, selector);
		return new LinqQuery<T2>(rs);
	}

	/**
	 * The Class FromIterable.
	 *
	 * @param <T2> the generic type
	 */
	private class FromIterable<T2> implements Iterable<T2> {

		/** The m source itr. */
		private Iterable<T> mSourceItr;
		
		/** The m selector. */
		private Selector<T, Iterable<T2>> mSelector;

		/**
		 * Instantiates a new from iterable.
		 *
		 * @param source the source
		 * @param selector the selector
		 */
		public FromIterable(Iterable<T> source,
				Selector<T, Iterable<T2>> selector) {
			this.mSourceItr = source;
			this.mSelector = selector;
		}

		/* (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		@Override
		public Iterator<T2> iterator() {
			return new FromIterator<T2>(this.mSourceItr, this.mSelector);
		}
	}

	/**
	 * The Class FromIterator.
	 *
	 * @param <T2> the generic type
	 */
	private class FromIterator<T2> implements Iterator<T2> {

		/** The m source. */
		private Iterator<T> mSource;
		
		/** The m current iterator. */
		private Iterator<T2> mCurrentIterator;
		
		/** The m selector. */
		private Selector<T, Iterable<T2>> mSelector;
		// 0: need to call find next; 1: has item; 2 : end
		/** The m has next. */
		private boolean mHasNext = true;
		
		/** The m current. */
		private T2 mCurrent;

		/**
		 * Instantiates a new from iterator.
		 *
		 * @param source the source
		 * @param selector the selector
		 */
		public FromIterator(Iterable<T> source,
				Selector<T, Iterable<T2>> selector) {
			this.mSource = source.iterator();
			this.mSelector = selector;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {

			if (this.mHasNext) {
				this.tryFindNext();
			}

			return this.mHasNext;
		}

		/**
		 * Try find next.
		 */
		private void tryFindNext() {

			while (this.mHasNext && this.mCurrent == null) {

				if (this.mCurrentIterator == null) {
					if (this.mSource.hasNext()) {
						T s = this.mSource.next();
						this.mCurrentIterator = this.mSelector.select(s)
								.iterator();
					} else {
						this.mHasNext = false;
					}
				} else if (!this.mCurrentIterator.hasNext()) {
					this.mCurrentIterator = null;
				} else {
					this.mCurrent = this.mCurrentIterator.next();
				}
			}

		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public T2 next() {
			if (this.hasNext()) {
				T2 rs = this.mCurrent;
				this.mCurrent = null;
				return rs;
			} else {
				throw new NoSuchElementException();
			}

		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();

		}

	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<T> iterator() {
		return this.mSourceItr.iterator();
	}

	/**
	 * Groups the elements of a sequence according to a specified key
	 * {@code Selector<T, TResult>}.
	 *
	 * @param <TKey> the generic type
	 * @param keySelector            A {@code Selector<T, TResult>} to extract the key for each
	 *            element.
	 * @return A {@code Query<T>} in where each {@code IGrouping } object
	 *         contains a sequence of objects and a key.
	 */
	public <TKey> LinqQuery<IGrouping<TKey, T>> groupBy(
			Selector<T, TKey> keySelector) {
		return groupBy(keySelector, null);
	}

	/**
	 * Groups the elements of a sequence according to a specified key selector
	 * function and compares the keys by using a specified comparer.
	 *
	 * @param <TKey> the generic type
	 * @param keySelector            A {@code Selector<T, TResult>} to extract the key for each
	 *            element.
	 * @param comparator            An {@code Comparator<T>} to compare keys.
	 * @return A {@code Query<T>} in where each {@code IGrouping} object
	 *         contains a sequence of objects and a key.
	 */
	public <TKey> LinqQuery<IGrouping<TKey, T>> groupBy(
			Selector<T, TKey> keySelector, Comparator<TKey> comparator) {
		if (comparator == null) {
			comparator = new NaturalComparator<TKey>();
		}

		TreeMap<TKey, ListGroup<TKey, T>> rs = new TreeMap<TKey, ListGroup<TKey, T>>(
				comparator);

		for (T element : this.mSourceItr) {
			TKey key = keySelector.select(element);
			ListGroup<TKey, T> group = rs.get(key);
			if (group == null) {
				group = new ListGroup<TKey, T>(key);
				rs.put(key, group);
			}
			group.add(element);
		}

		return new LinqQuery<ListGroup<TKey, T>>(rs.values())
				.select(new Selector<ListGroup<TKey, T>, IGrouping<TKey, T>>() {

					@Override
					public IGrouping<TKey, T> select(ListGroup<TKey, T> item) {
						return item;
					}
				});
	}

	/**
	 * Returns the number of elements in the sequence.
	 * 
	 * @return The number of elements in the sequence.
	 */
	public int count() {
		if (this.mSourceItr instanceof Collection<?>) {
			return ((Collection<?>) this.mSourceItr).size();
		} else if (this.mSourceItr instanceof ICountable) {
			return ((ICountable) this.mSourceItr).count();
		} else {
			int rs = 0;
			Iterator<T> iterator = this.iterator();
			while (iterator.hasNext()) {
				iterator.next();
				rs++;
			}
			return rs;
		}
	}

	/**
	 * Returns a number that represents how many elements in the sequence
	 * satisfy a condition.
	 *
	 * @param predicate            A {@code Predicate<T>} to test each element for a condition.
	 * @return A number that represents how many elements in the sequence
	 *         satisfy the condition in the predicate.
	 * @throws Exception the exception
	 */
	public int count(Predicate<T> predicate) throws Exception {
		int rs = 0;
		Iterator<T> iterator = this.iterator();
		while (iterator.hasNext()) {
			T item = iterator.next();
			if (predicate.evaluate(item)) {
				rs++;
			}
		}
		return rs;
	}

	/**
	 * Inverts the order of the elements in a sequence.
	 * 
	 * @return A sequence whose elements correspond to those of the sequence in
	 *         reverse order.
	 */
	public LinqQuery<T> reverse() {
		if (this.mSourceItr instanceof IterableArray<?>) {
			ArrayReverseIterable rs = new ArrayReverseIterable(
					((IterableArray<T>) this.mSourceItr).getSource());
			return new LinqQuery<T>(rs);
		} else if ((this.mSourceItr instanceof List<?>)
				&& (this.mSourceItr instanceof RandomAccess)) {
			return new LinqQuery<T>(new ReverseIterable((List<T>) this.mSourceItr));
		} else {
			ArrayList<T> rs = new ArrayList<T>();
			for (T element : this.mSourceItr) {
				rs.add(element);
			}
			return new LinqQuery<T>(rs);
		}
	}

	/**
	 * The Class ArrayReverseIterable.
	 */
	private class ArrayReverseIterable implements Iterable<T> {

		/** The m source. */
		private T[] mSource;

		/**
		 * Instantiates a new array reverse iterable.
		 *
		 * @param source the source
		 */
		public ArrayReverseIterable(T[] source) {
			this.mSource = source;
		}

		/* (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		@Override
		public Iterator<T> iterator() {
			return new ArrayReverseIterator(this.mSource);
		}
	}

	/**
	 * The Class ArrayReverseIterator.
	 */
	private class ArrayReverseIterator implements Iterator<T> {

		/** The m source. */
		private T[] mSource;
		
		/** The m position. */
		private int mPosition;

		/**
		 * Instantiates a new array reverse iterator.
		 *
		 * @param source the source
		 */
		public ArrayReverseIterator(T[] source) {
			this.mSource = source;
			this.mPosition = this.mSource.length - 1;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return mPosition >= 0;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public T next() {
			T rs = this.mSource[this.mPosition];
			this.mPosition--;
			return rs;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();

		}
	}

	/**
	 * The Class ReverseIterable.
	 */
	private class ReverseIterable implements Iterable<T> {

		/** The m source list. */
		private List<T> mSourceList;

		/**
		 * Instantiates a new reverse iterable.
		 *
		 * @param source the source
		 */
		public ReverseIterable(List<T> source) {
			this.mSourceList = source;
		}

		/* (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		@Override
		public Iterator<T> iterator() {
			return new ReverseIterator(this.mSourceList);
		}
	}

	/**
	 * The Class ReverseIterator.
	 */
	private class ReverseIterator implements Iterator<T> {

		/** The m source. */
		private List<T> mSource;
		
		/** The m position. */
		private int mPosition;

		/**
		 * Instantiates a new reverse iterator.
		 *
		 * @param source the source
		 */
		public ReverseIterator(List<T> source) {
			this.mSource = source;
			this.mPosition = this.mSource.size() - 1;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return mPosition >= 0;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public T next() {
			T rs = this.mSource.get(this.mPosition);
			this.mPosition--;
			return rs;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();

		}
	}

	/**
	 * Returns a specified number of contiguous elements from the start of a
	 * sequence.
	 * 
	 * @param count
	 *            The number of elements to return.
	 * @return A {@code Query<T>} that contains the specified number of elements
	 *         from the start of the input sequence.
	 */
	public LinqQuery<T> take(int count) {
		return new LinqQuery<T>(new TakeIterable(this.mSourceItr, count));
	}

	/**
	 * The Class TakeIterable.
	 */
	private class TakeIterable implements Iterable<T> {

		/** The m source itr. */
		private Iterable<T> mSourceItr;
		
		/** The m capicity. */
		private int mCapicity;

		/**
		 * Instantiates a new take iterable.
		 *
		 * @param source the source
		 * @param take the take
		 */
		public TakeIterable(Iterable<T> source, int take) {
			this.mSourceItr = source;
			this.mCapicity = take;
		}

		/* (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		@Override
		public Iterator<T> iterator() {
			return new TakeIterator(this.mSourceItr, this.mCapicity);
		}

	}

	/**
	 * The Class TakeIterator.
	 */
	private class TakeIterator implements Iterator<T> {

		/** The m source itr. */
		private Iterator<T> mSourceItr;
		
		/** The m take. */
		int mTake;
		
		/** The m position. */
		int mPosition = 0;

		/**
		 * Instantiates a new take iterator.
		 *
		 * @param source the source
		 * @param take the take
		 */
		public TakeIterator(Iterable<T> source, int take) {
			this.mSourceItr = source.iterator();
			this.mTake = take;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return mPosition < this.mTake && mSourceItr.hasNext();
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public T next() {
			if (this.mPosition < this.mTake) {
				T rs = mSourceItr.next();
				this.mPosition++;
				return rs;
			} else {
				throw new NoSuchElementException();

			}

		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();

		}

	}

	/**
	 * Determines whether a sequence contains any elements.
	 * 
	 * @return true if source sequence contains any elements; otherwise, false.
	 */
	public boolean any() {
		return this.mSourceItr.iterator().hasNext();
	}

	/**
	 * Determines whether any element of a sequence satisfies a condition.
	 * 
	 * @param predicate
	 *            A {@code Predicate<T>} to test each element for a condition.
	 * @return true if any elements in the source sequence pass the test in the
	 *         specified predicate; otherwise, false.
	 */
	public boolean any(Predicate<T> predicate) {
		return new WhereIterable(this.mSourceItr, predicate).iterator()
				.hasNext();
	}

	/**
	 * Produces the set union of two sequences by using a specified
	 * {@code Comparator<T>}.
	 * 
	 * @param other
	 *            An array whose distinct elements form the second set for union
	 * @param comparator
	 *            The {@code Comparator<T>} to compare values
	 * @return A {@code Query<T>}that contains the elements from both input
	 *         sequences, excluding duplicates.
	 */
	public LinqQuery<T> union(T[] other, Comparator<T> comparator) {
		return this.union(new IterableArray<T>(other), comparator);
	}

	/**
	 * Produces the set union of two sequences by using a specified
	 * {@code Comparator<T>}.
	 * 
	 * @param other
	 *            A sequence whose distinct elements form the second set for
	 *            union
	 * @param comparator
	 *            The {@code Comparator<T>} to compare values
	 * @return A {@code Query<T>}that contains the elements from both input
	 *         sequences, excluding duplicates.
	 */
	public LinqQuery<T> union(Iterable<T> other, Comparator<T> comparator) {
		final TreeSet<T> set = new TreeSet<T>(comparator);

		LinqQuery<T> rs = null;
		try {
			rs = this.contact(other).where(new Predicate<T>() {

				@Override
				public boolean evaluate(T obj) throws Exception {
					if (set.contains(obj)) {
						return false;
					} else {
						set.add(obj);
						return true;
					}
				}
			});
		} catch (Exception e) {

			e.printStackTrace();
		}

		return rs;
	}

	/**
	 * Produces the set union of two sequences.
	 * 
	 * @param other
	 *            A sequence whose distinct elements form the second set for
	 *            union
	 * @return A {@code Query<T>}that contains the elements from both input
	 *         sequences, excluding duplicates.
	 */
	public LinqQuery<T> union(Iterable<T> other) {
		final HashSet<T> set = new HashSet<T>();

		LinqQuery<T> rs = null;
		try {
			rs = this.contact(other).where(new Predicate<T>() {

				@Override
				public boolean evaluate(T obj) throws Exception {
					if (set.contains(obj)) {
						return false;
					} else {
						set.add(obj);
						return true;
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rs;

	}

	/**
	 * Produces the set union of two sequences.
	 * 
	 * @param array
	 *            An array whose distinct elements form the second set for union
	 * @return A {@code Query<T>}that contains the elements from both input
	 *         sequences, excluding duplicates.
	 */
	public LinqQuery<T> union(T[] array) {
		return this.union(new IterableArray<T>(array));
	}

	/**
	 * The Class ContactIterable.
	 */
	private class ContactIterable implements Iterable<T> {

		/** The m itr1. */
		private Iterable<T> mItr1;
		
		/** The m itr2. */
		private Iterable<T> mItr2;

		/**
		 * Instantiates a new contact iterable.
		 *
		 * @param s1 the s1
		 * @param s2 the s2
		 */
		public ContactIterable(Iterable<T> s1, Iterable<T> s2) {
			mItr1 = s1;
			mItr2 = s2;
		}

		/* (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		@Override
		public Iterator<T> iterator() {
			return new ContactIterator(this.mItr1.iterator(),
					this.mItr2.iterator());
		}
	}

	/**
	 * The Class ContactIterator.
	 */
	private class ContactIterator implements Iterator<T> {

		/** The m itr1. */
		private Iterator<T> mItr1;
		
		/** The m itr2. */
		private Iterator<T> mItr2;
		
		/** The m state. */
		private int mState = 0;

		/**
		 * Instantiates a new contact iterator.
		 *
		 * @param s1 the s1
		 * @param s2 the s2
		 */
		public ContactIterator(Iterator<T> s1, Iterator<T> s2) {
			this.mItr1 = s1;
			this.mItr2 = s2;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {

			if (mState == 0) {
				boolean rs = mItr1.hasNext();
				if (rs == false) {
					mState = 1;
					rs = mItr2.hasNext();
				}
				return rs;
			} else {
				return mItr2.hasNext();
			}

		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public T next() {

			T rs = null;

			if (mState == 0) {
				if (mItr1.hasNext()) {
					rs = mItr1.next();
				} else {
					mState = 1;
				}
			}
			if (mState == 1) {
				rs = mItr2.next();
			}

			return rs;

		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();

		}
	}

	/**
	 * Concatenates two sequences.
	 * 
	 * @param other
	 *            The sequence to concatenate to current {@code Query<T>}.
	 * @return A {@code Query<T>} that contains the concatenated elements of the
	 *         two input sequences.
	 */
	public LinqQuery<T> contact(Iterable<T> other) {
		return new LinqQuery<T>(new ContactIterable(this.mSourceItr, other));
	}

	/**
	 * Concatenates two sequences.
	 * 
	 * @param other
	 *            The sequence to concatenate to current {@code Query<T>}.
	 * @return A {@code Query<T>} that contains the concatenated elements of the
	 *         two input sequences.
	 */
	public LinqQuery<T> contact(T[] other) {
		return this.contact(new IterableArray<T>(other));
	}

	/**
	 * Determines whether the sequence contains a specified element by using the
	 * default equality comparer.
	 * 
	 * @param obj
	 *            The value to locate in the sequence
	 * @return true if the sequence contains the specified element; otherwise,
	 *         false
	 */
	public boolean contains(T obj) {
		for (T item : this.mSourceItr) {
			if (Objects.equals(item, obj)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines whether the sequence contains a specified element by using the
	 * specified equality comparer.
	 * 
	 * @param obj
	 *            The value to locate in the sequence
	 * @param comparator
	 *            The {@code Comparator<T>} to compare values
	 * @return true if the sequence contains the specified element; otherwise,
	 *         false
	 */
	public boolean contains(T obj, Comparator<T> comparator) {
		for (T item : this.mSourceItr) {
			if (comparator.compare(obj, item) == 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the element at a specified index in a sequence.
	 *
	 * @param index            The zero-based index of the element to retrieve.
	 * @return The element at the specified position in the source sequence.
	 * @throws Exception the exception
	 */
	public T elementAt(int index) throws Exception {
		if (this.mSourceItr instanceof List<?>) {
			return ((List<T>) this.mSourceItr).get(index);
		} else if (this.mSourceItr instanceof IterableArray<?>) {
			return ((IterableArray<T>) this.mSourceItr).getSource()[index];
		} else {
			int i = 0;
			for (T item : this.mSourceItr) {
				if (i == index) {
					return item;
				}
				i++;
			}
			throw new IndexOutOfBoundsException("index out of bond");
		}
	}

	/**
	 * Returns the element at a specified index in a sequence or
	 * <code>null</code> if the index is out of range.
	 * 
	 * @param index
	 *            The zero-based index of the element to retrieve.
	 * @return <code>null</code> if the index is outside the bounds of the
	 *         source sequence; otherwise, the element at the specified position
	 *         in the source sequence.
	 */
	public T elementAtOrDefault(int index) {
		if (this.mSourceItr instanceof List<?>) {
			List<T> l = ((List<T>) this.mSourceItr);
			return (l.size() > index && index >= 0) ? l.get(index) : null;
		} else if (this.mSourceItr instanceof IterableArray<?>) {
			T[] a = ((IterableArray<T>) this.mSourceItr).getSource();
			return (a.length > index && index >= 0) ? a[index] : null;
		} else {
			int i = 0;
			for (T item : this.mSourceItr) {
				if (i == index) {
					return item;
				}
				i++;
			}
			return null;
		}
	}

	/**
	 * Produces the set difference of two sequences by using the default
	 * equality comparer to compare values.
	 *
	 * @param other            An {@code java.util.Iterable<T>} whose elements that also
	 *            occur in the source sequence will cause those elements to be
	 *            removed from the returned sequence.
	 * @return A sequence that contains the set difference of the elements of
	 *         two sequences.
	 * @throws Exception the exception
	 */
	public LinqQuery<T> except(Iterable<T> other) throws Exception {
		final HashSet<T> excludes = new HashSet<T>();
		for (T ele : other) {
			excludes.add(ele);
		}

		return this.where(new Predicate<T>() {

			@Override
			public boolean evaluate(T obj) throws Exception {
				return !excludes.contains(obj);
			}
		});
	}

	/**
	 * Produces the set difference of two sequences by using the default
	 * equality comparer to compare values.
	 *
	 * @param other            An array whose elements that also occur in the source sequence
	 *            will cause those elements to be removed from the returned
	 *            sequence.
	 * @return A sequence that contains the set difference of the elements of
	 *         two sequences.
	 * @throws Exception the exception
	 */
	public LinqQuery<T> except(T[] other) throws Exception {
		return this.except(new IterableArray<T>(other));
	}

	/**
	 * Produces the set difference of two sequences by using the specified
	 * comparator to compare values.
	 *
	 * @param other            An <Iterable> whose elements that also occur in the source
	 *            sequence will cause those elements to be removed from the
	 *            returned sequence.
	 * @param comparator            The {@code Comparator<T>} to compare values.
	 * @return A sequence that contains the set difference of the elements of
	 *         two sequences.
	 * @throws Exception the exception
	 */
	public LinqQuery<T> except(Iterable<T> other, Comparator<T> comparator)
			throws Exception {
		final TreeSet<T> excludes = new TreeSet<T>(comparator);
		for (T ele : other) {
			excludes.add(ele);
		}

		return this.where(new Predicate<T>() {
			@Override
			public boolean evaluate(T obj) throws Exception {
				return !excludes.contains(obj);
			}
		});
	}

	/**
	 * Produces the set difference of two sequences by using the specified
	 * comparator to compare values.
	 *
	 * @param other            An <Array> whose elements that also occur in the source
	 *            sequence will cause those elements to be removed from the
	 *            returned sequence.
	 * @param comparator            The {@code Comparator<T>} to compare values.
	 * @return A sequence that contains the set difference of the elements of
	 *         two sequences.
	 * @throws Exception the exception
	 */
	public LinqQuery<T> except(T[] other, Comparator<T> comparator)
			throws Exception {
		return this.except(new IterableArray<T>(other), comparator);
	}

	/**
	 * Produces the set intersection of two sequences by using the default
	 * equality comparer to compare values.
	 *
	 * @param other            An {@code java.util.Iterable<T>} whose distinct elements that
	 *            also appear in the first sequence will be returned.
	 * @return A sequence that contains the elements that form the set
	 *         intersection of two sequences.
	 * @throws Exception the exception
	 */
	public LinqQuery<T> intersect(Iterable<T> other) throws Exception {
		final HashSet<T> includes = new HashSet<T>();
		for (T ele : other) {
			includes.add(ele);
		}

		return this.where(new Predicate<T>() {

			@Override
			public boolean evaluate(T obj) throws Exception {
				return includes.contains(obj);
			}
		});
	}

	/**
	 * Produces the set intersection of two sequences by using the default
	 * equality comparer to compare values.
	 *
	 * @param other            An array whose distinct elements that also appear in the first
	 *            sequence will be returned.
	 * @return A sequence that contains the elements that form the set
	 *         intersection of two sequences.
	 * @throws Exception the exception
	 */
	public LinqQuery<T> intersect(T[] other) throws Exception {
		return this.intersect(new IterableArray<T>(other));
	}

	/**
	 * Produces the set intersection of two sequences by using the specified
	 * comparator to compare values.
	 *
	 * @param other            An {@code java.util.Iterable<T>} whose distinct elements that
	 *            also appear in the first sequence will be returned.
	 * @param comparator            The {@code Comparator<T>} to compare values.
	 * @return A sequence that contains the elements that form the set
	 *         intersection of two sequences.
	 * @throws Exception the exception
	 */
	public LinqQuery<T> intersect(Iterable<T> other, Comparator<T> comparator)
			throws Exception {
		final TreeSet<T> includes = new TreeSet<T>(comparator);
		for (T ele : other) {
			includes.add(ele);
		}

		return this.where(new Predicate<T>() {

			@Override
			public boolean evaluate(T obj) throws Exception {
				return includes.contains(obj);
			}
		});
	}

	/**
	 * Produces the set intersection of two sequences by using the specified
	 * comparator to compare values.
	 *
	 * @param other            An array whose distinct elements that also appear in the first
	 *            sequence will be returned.
	 * @param comparator            The {@code Comparator<T>} to compare values.
	 * @return A sequence that contains the elements that form the set
	 *         intersection of two sequences.
	 * @throws Exception the exception
	 */
	public LinqQuery<T> intersect(T[] other, Comparator<T> comparator)
			throws Exception {
		return this.intersect(new IterableArray<T>(other), comparator);
	}

	/**
	 * Correlates the elements of two sequences based on matching keys by using
	 * the default equality comparer.
	 *
	 * @param <TInner> the generic type
	 * @param <TKey> the generic type
	 * @param <TResult> the generic type
	 * @param inner            The sequence to join to the source sequence.
	 * @param outerKeySelector            A {@code Selector<T, TResult>} to extract the join key from
	 *            each element of the source sequence.
	 * @param innerKeySelector            A {@code Selector<T, TResult>} to extract the join key from
	 *            each element of the source sequence.
	 * @param joint            A {@code Joint<T1, T2, TResult>} to create result element from
	 *            tow matching elements
	 * @return A {@code Query<T>} that has elements of type TResult that are
	 *         obtained by performing an inner join on two sequences.
	 */
	public <TInner, TKey, TResult> LinqQuery<TResult> join(TInner[] inner,
			Selector<T, TKey> outerKeySelector,
			Selector<TInner, TKey> innerKeySelector,
			Joint<T, TInner, TResult> joint) {
		return this.join(new IterableArray<TInner>(inner), outerKeySelector,
				innerKeySelector, joint, new NaturalComparator<TKey>());
	}

	/**
	 * Correlates the elements of two sequences based on matching keys by using
	 * the default equality comparer.
	 *
	 * @param <TInner> the generic type
	 * @param <TKey> the generic type
	 * @param <TResult> the generic type
	 * @param inner            The sequence to join to the source sequence.
	 * @param outerKeySelector            A {@code Selector<T, TResult>} to extract the join key from
	 *            each element of the source sequence.
	 * @param innerKeySelector            A {@code Selector<T, TResult>} to extract the join key from
	 *            each element of the source sequence.
	 * @param joint            A {@code Joint<T1, T2, TResult>} to create result element from
	 *            tow matching elements
	 * @return A {@code Query<T>} that has elements of type TResult that are
	 *         obtained by performing an inner join on two sequences.
	 */
	public <TInner, TKey, TResult> LinqQuery<TResult> join(Iterable<TInner> inner,
			Selector<T, TKey> outerKeySelector,
			Selector<TInner, TKey> innerKeySelector,
			Joint<T, TInner, TResult> joint) {
		return this.join(inner, outerKeySelector, innerKeySelector, joint,
				new NaturalComparator<TKey>());
	}

	/**
	 * Correlates the elements of two sequences based on matching keys by using
	 * the specified comparer.
	 *
	 * @param <TInner> the generic type
	 * @param <TKey> the generic type
	 * @param <TResult> the generic type
	 * @param inner            The sequence to join to the source sequence.
	 * @param outerKeySelector            A {@code Selector<T, TResult>} to extract the join key from
	 *            each element of the source sequence.
	 * @param innerKeySelector            A {@code Selector<T, TResult>} to extract the join key from
	 *            each element of the source sequence.
	 * @param joint            A {@code Joint<T1, T2, TResult>} to create result element from
	 *            tow matching elements
	 * @param comparator            The {@code Comparator<T>} to compare values.
	 * @return A {@code Query<T>} that has elements of type TResult that are
	 *         obtained by performing an inner join on two sequences.
	 */
	public <TInner, TKey, TResult> LinqQuery<TResult> join(TInner[] inner,
			Selector<T, TKey> outerKeySelector,
			Selector<TInner, TKey> innerKeySelector,
			Joint<T, TInner, TResult> joint, Comparator<TKey> comparator) {
		return new LinqQuery<TResult>(new JoinIterable<TInner, TKey, TResult>(
				this.mSourceItr, new IterableArray<TInner>(inner),
				outerKeySelector, innerKeySelector, joint, comparator));
	}

	/**
	 * Correlates the elements of two sequences based on matching keys by using
	 * the specified comparer.
	 *
	 * @param <TInner> the generic type
	 * @param <TKey> the generic type
	 * @param <TResult> the generic type
	 * @param inner            The sequence to join to the source sequence.
	 * @param outerKeySelector            A {@code Selector<T, TResult>} to extract the join key from
	 *            each element of the source sequence.
	 * @param innerKeySelector            A {@code Selector<T, TResult>} to extract the join key from
	 *            each element of the source sequence.
	 * @param joint            A {@code Joint<T1, T2, TResult>} to create result element from
	 *            tow matching elements
	 * @param comparator            The {@code Comparator<T>} to compare values.
	 * @return A {@code Query<T>} that has elements of type TResult that are
	 *         obtained by performing an inner join on two sequences.
	 */
	public <TInner, TKey, TResult> LinqQuery<TResult> join(Iterable<TInner> inner,
			Selector<T, TKey> outerKeySelector,
			Selector<TInner, TKey> innerKeySelector,
			Joint<T, TInner, TResult> joint, Comparator<TKey> comparator) {
		return new LinqQuery<TResult>(new JoinIterable<TInner, TKey, TResult>(
				this.mSourceItr, inner, outerKeySelector, innerKeySelector,
				joint, comparator));
	}

	/**
	 * The Class JoinIterable.
	 *
	 * @param <TInner> the generic type
	 * @param <TKey> the generic type
	 * @param <TResult> the generic type
	 */
	private class JoinIterable<TInner, TKey, TResult> implements
			Iterable<TResult> {
		
		/** The m outer itr. */
		private Iterable<T> mOuterItr;
		
		/** The m inner itr. */
		private Iterable<TInner> mInnerItr;
		
		/** The m outer key selector. */
		private Selector<T, TKey> mOuterKeySelector;
		
		/** The m inner key selector. */
		private Selector<TInner, TKey> mInnerKeySelector;
		
		/** The m joint. */
		private Joint<T, TInner, TResult> mJoint;
		
		/** The m comparator. */
		private Comparator<TKey> mComparator;
		
		/**
		 * Instantiates a new join iterable.
		 *
		 * @param outer the outer
		 * @param inner the inner
		 * @param outerKeySelector the outer key selector
		 * @param innerKeySelector the inner key selector
		 * @param joint the joint
		 * @param comparator the comparator
		 */
		public JoinIterable(Iterable<T> outer, Iterable<TInner> inner,
				Selector<T, TKey> outerKeySelector,
				Selector<TInner, TKey> innerKeySelector,
				Joint<T, TInner, TResult> joint, Comparator<TKey> comparator) {
			this.mOuterItr = outer;
			this.mInnerItr = inner;
			this.mInnerKeySelector = innerKeySelector;
			this.mOuterKeySelector = outerKeySelector;
			this.mJoint = joint;
			this.mComparator = comparator;
		}

		/* (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		@Override
		public Iterator<TResult> iterator() {
			return new JoinIterator<TInner, TKey, TResult>(this.mOuterItr,
					this.mInnerItr, this.mOuterKeySelector,
					this.mInnerKeySelector, this.mJoint, this.mComparator);
		}

	}

	/**
	 * The Class JoinIterator.
	 *
	 * @param <TInner> the generic type
	 * @param <TKey> the generic type
	 * @param <TResult> the generic type
	 */
	private class JoinIterator<TInner, TKey, TResult> implements
			Iterator<TResult> {
		
		/** The m outer itr. */
		private Iterator<T> mOuterItr;
		
		/** The m inner itr. */
		private Iterable<TInner> mInnerItr;
		
		/** The m outer key selector. */
		private Selector<T, TKey> mOuterKeySelector;
		
		/** The m inner key selector. */
		private Selector<TInner, TKey> mInnerKeySelector;
		
		/** The m joint. */
		private Joint<T, TInner, TResult> mJoint;
		
		/** The m comparator. */
		private Comparator<TKey> mComparator;

		/** The m inner maps. */
		private TreeMap<TKey, IGrouping<TKey, TInner>> mInnerMaps;
		
		/** The m current iner itr. */
		private Iterator<TInner> mCurrentInerItr = null;
		
		/** The m current out. */
		private T mCurrentOut = null;

		/** The m current result. */
		private TResult mCurrentResult;
		
		/** The m state. */
		private int mState = 0;
		
		/**
		 * Instantiates a new join iterator.
		 *
		 * @param outer the outer
		 * @param inner the inner
		 * @param outerKeySelector the outer key selector
		 * @param innerKeySelector the inner key selector
		 * @param joint the joint
		 * @param comparator the comparator
		 */
		public JoinIterator(Iterable<T> outer, Iterable<TInner> inner,
				Selector<T, TKey> outerKeySelector,
				Selector<TInner, TKey> innerKeySelector,
				Joint<T, TInner, TResult> joint, Comparator<TKey> comparator) {
			this.mOuterItr = outer.iterator();
			this.mInnerItr = inner;
			this.mInnerKeySelector = innerKeySelector;
			this.mOuterKeySelector = outerKeySelector;
			this.mJoint = joint;
			this.mComparator = comparator;
		}

		/**
		 * Try find next.
		 */
		private void tryFindNext() {
			if (mState == 0) {
				this.mInnerMaps = new TreeMap<TKey, IGrouping<TKey, TInner>>(
						this.mComparator);
				for (IGrouping<TKey, TInner> group : new LinqQuery<TInner>(
						this.mInnerItr).groupBy(this.mInnerKeySelector,
						this.mComparator)) {
					this.mInnerMaps.put(group.getKey(), group);
				}
				this.mState = 1;
			}

			while (mCurrentResult == null && this.mState == 1) {
				if (this.mCurrentOut == null) {
					if (this.mOuterItr.hasNext()) {
						this.mCurrentOut = this.mOuterItr.next();
						this.mCurrentInerItr = null;
					} else {
						this.mState = 2;
					}

				} else if (this.mCurrentInerItr == null) {
					IGrouping<TKey, TInner> g = this.mInnerMaps
							.get(this.mOuterKeySelector
									.select(this.mCurrentOut));
					if (g != null) {
						this.mCurrentInerItr = g.iterator();
					} else {
						this.mCurrentOut = null;
						this.mCurrentInerItr = null;
					}
				} else if (!this.mCurrentInerItr.hasNext()) {
					this.mCurrentOut = null;
					this.mCurrentInerItr = null;
				} else {
					TInner inner = this.mCurrentInerItr.next();
					try {
						this.mCurrentResult = this.mJoint.join(this.mCurrentOut,
								inner);
					} catch (Exception e) {
						throw new IllegalStateException(e);
					}
				}
			}

		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			this.tryFindNext();
			return this.mState < 2;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public TResult next() {
			if (this.hasNext()) {
				TResult rs = this.mCurrentResult;
				this.mCurrentResult = null;

				return rs;
			} else {
				throw new NoSuchElementException();
			}
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();

		}

	}

	/**
	 * Correlates the elements of two sequences based on equality of keys and
	 * groups the results. The specified comparator is used to compare keys.
	 *
	 * @param <TInner> the generic type
	 * @param <TKey> the generic type
	 * @param <TResult> the generic type
	 * @param inner            The sequence to join to the source sequence.
	 * @param outerKeySelector            A {@code Selector<T, TResult>} to extract the join key from
	 *            each element of the source sequence.
	 * @param innerKeySelector            A {@code Selector<T, TResult>} to extract the join key from
	 *            each element of the second sequence.
	 * @param joint            A {@code Joint<T1, T2, TResult>} to create a result element
	 *            from an element from the first sequence and a collection of
	 *            matching elements from the second sequence.
	 * @param comparator            An {@code Comparator<T>}to hash and compare keys.
	 * @return A {@code Query<T>}that contains elements of type TResult that are
	 *         obtained by performing a grouped join on two sequences.
	 */
	public <TInner, TKey, TResult> LinqQuery<IGrouping<TKey, TResult>> groupJoin(
			Iterable<TInner> inner, Selector<T, TKey> outerKeySelector,
			Selector<TInner, TKey> innerKeySelector,
			Joint<T, TInner, TResult> joint, Comparator<TKey> comparator) {

		return new LinqQuery<IGrouping<TKey, TResult>>(
				new GroupJoinIterable<TInner, TKey, TResult>(this.mSourceItr,
						inner, outerKeySelector, innerKeySelector, joint,
						comparator));
	}

	/**
	 * Correlates the elements of two sequences based on equality of keys and
	 * groups the results. The specified comparator is used to compare keys.
	 *
	 * @param <TInner> the generic type
	 * @param <TKey> the generic type
	 * @param <TResult> the generic type
	 * @param inner            The sequence to join to the source sequence.
	 * @param outerKeySelector            A {@code Selector<T, TResult>} to extract the join key from
	 *            each element of the source sequence.
	 * @param innerKeySelector            A {@code Selector<T, TResult>} to extract the join key from
	 *            each element of the second sequence.
	 * @param joint            A {@code Joint<T1, T2, TResult>} to create a result element
	 *            from an element from the first sequence and a collection of
	 *            matching elements from the second sequence.
	 * @param comparator            An {@code Comparator<T>}to hash and compare keys.
	 * @return A {@code Query<T>}that contains elements of type TResult that are
	 *         obtained by performing a grouped join on two sequences.
	 */
	public <TInner, TKey, TResult> LinqQuery<IGrouping<TKey, TResult>> groupJoin(
			TInner[] inner, Selector<T, TKey> outerKeySelector,
			Selector<TInner, TKey> innerKeySelector,
			Joint<T, TInner, TResult> joint, Comparator<TKey> comparator) {
		return new LinqQuery<IGrouping<TKey, TResult>>(
				new GroupJoinIterable<TInner, TKey, TResult>(this.mSourceItr,
						new IterableArray<TInner>(inner), outerKeySelector,
						innerKeySelector, joint, comparator));
	}

	/**
	 * Correlates the elements of two sequences based on equality of keys and
	 * groups the results. The default comparator is used to compare keys.
	 *
	 * @param <TInner> the generic type
	 * @param <TKey> the generic type
	 * @param <TResult> the generic type
	 * @param inner            The sequence to join to the source sequence.
	 * @param outerKeySelector            A {@code Selector<T, TResult>} to extract the join key from
	 *            each element of the source sequence.
	 * @param innerKeySelector            A {@code Selector<T, TResult>} to extract the join key from
	 *            each element of the second sequence.
	 * @param joint            A {@code Joint<T1, T2, TResult>} to create a result element
	 *            from an element from the first sequence and a collection of
	 *            matching elements from the second sequence.
	 * @return A {@code Query<T>}that contains elements of type TResult that are
	 *         obtained by performing a grouped join on two sequences.
	 */
	public <TInner, TKey, TResult> LinqQuery<IGrouping<TKey, TResult>> groupJoin(
			Iterable<TInner> inner, Selector<T, TKey> outerKeySelector,
			Selector<TInner, TKey> innerKeySelector,
			Joint<T, TInner, TResult> joint) {

		return new LinqQuery<IGrouping<TKey, TResult>>(
				new GroupJoinIterable<TInner, TKey, TResult>(this.mSourceItr,
						inner, outerKeySelector, innerKeySelector, joint,
						new NaturalComparator<TKey>()));
	}

	/**
	 * Correlates the elements of two sequences based on equality of keys and
	 * groups the results. The default comparator is used to compare keys.
	 *
	 * @param <TInner> the generic type
	 * @param <TKey> the generic type
	 * @param <TResult> the generic type
	 * @param inner            The sequence to join to the source sequence.
	 * @param outerKeySelector            A {@code Selector<T, TResult>} to extract the join key from
	 *            each element of the source sequence.
	 * @param innerKeySelector            A {@code Selector<T, TResult>} to extract the join key from
	 *            each element of the second sequence.
	 * @param joint            A {@code Joint<T1, T2, TResult>} to create a result element
	 *            from an element from the first sequence and a collection of
	 *            matching elements from the second sequence.
	 * @return A {@code Query<T>}that contains elements of type TResult that are
	 *         obtained by performing a grouped join on two sequences.
	 */
	public <TInner, TKey, TResult> LinqQuery<IGrouping<TKey, TResult>> groupJoin(
			TInner[] inner, Selector<T, TKey> outerKeySelector,
			Selector<TInner, TKey> innerKeySelector,
			Joint<T, TInner, TResult> joint) {
		return new LinqQuery<IGrouping<TKey, TResult>>(
				new GroupJoinIterable<TInner, TKey, TResult>(this.mSourceItr,
						new IterableArray<TInner>(inner), outerKeySelector,
						innerKeySelector, joint, new NaturalComparator<TKey>()));
	}

	/**
	 * The Class GroupJoinIterable.
	 *
	 * @param <TInner> the generic type
	 * @param <TKey> the generic type
	 * @param <TResult> the generic type
	 */
	private class GroupJoinIterable<TInner, TKey, TResult> implements
			Iterable<IGrouping<TKey, TResult>> {
		
		/** The m outer itr. */
		private Iterable<T> mOuterItr;
		
		/** The m inner itr. */
		private Iterable<TInner> mInnerItr;
		
		/** The m outer key selector. */
		private Selector<T, TKey> mOuterKeySelector;
		
		/** The m inner key selector. */
		private Selector<TInner, TKey> mInnerKeySelector;
		
		/** The m joint. */
		private Joint<T, TInner, TResult> mJoint;
		
		/** The m comparator. */
		private Comparator<TKey> mComparator;
		
		/**
		 * Instantiates a new group join iterable.
		 *
		 * @param outer the outer
		 * @param inner the inner
		 * @param outerKeySelector the outer key selector
		 * @param innerKeySelector the inner key selector
		 * @param joint the joint
		 * @param comparator the comparator
		 */
		public GroupJoinIterable(Iterable<T> outer, Iterable<TInner> inner,
				Selector<T, TKey> outerKeySelector,
				Selector<TInner, TKey> innerKeySelector,
				Joint<T, TInner, TResult> joint, Comparator<TKey> comparator) {
			this.mOuterItr = outer;
			this.mInnerItr = inner;
			this.mInnerKeySelector = innerKeySelector;
			this.mOuterKeySelector = outerKeySelector;
			this.mJoint = joint;
			this.mComparator = comparator;
		}

		/* (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		@Override
		public Iterator<IGrouping<TKey, TResult>> iterator() {
			return new GroupJoinIterator<TInner, TKey, TResult>(this.mOuterItr,
					this.mInnerItr, this.mOuterKeySelector,
					this.mInnerKeySelector, this.mJoint, this.mComparator);
		}

	}

	/**
	 * The Class GroupJoinIterator.
	 *
	 * @param <TInner> the generic type
	 * @param <TKey> the generic type
	 * @param <TResult> the generic type
	 */
	private class GroupJoinIterator<TInner, TKey, TResult> implements
			Iterator<IGrouping<TKey, TResult>> {
		
		/** The m outer itr. */
		private Iterable<T> mOuterItr;
		
		/** The m inner itr. */
		private Iterable<TInner> mInnerItr;
		
		/** The m outer key selector. */
		private Selector<T, TKey> mOuterKeySelector;
		
		/** The m inner key selector. */
		private Selector<TInner, TKey> mInnerKeySelector;
		
		/** The m joint. */
		private Joint<T, TInner, TResult> mJoint;
		
		/** The m comparator. */
		private Comparator<TKey> mComparator;

		/** The m out groups. */
		private Iterator<IGrouping<TKey, T>> mOutGroups;
		
		/** The m inner groups. */
		private TreeMap<TKey, IGrouping<TKey, TInner>> mInnerGroups;
		
		/** The m current. */
		private IGrouping<TKey, TResult> mCurrent = null;

		/** The m state. */
		private int mState = 0;
		
		/**
		 * Instantiates a new group join iterator.
		 *
		 * @param outer the outer
		 * @param inner the inner
		 * @param outerKeySelector the outer key selector
		 * @param innerKeySelector the inner key selector
		 * @param joint the joint
		 * @param comparator the comparator
		 */
		public GroupJoinIterator(Iterable<T> outer, Iterable<TInner> inner,
				Selector<T, TKey> outerKeySelector,
				Selector<TInner, TKey> innerKeySelector,
				Joint<T, TInner, TResult> joint, Comparator<TKey> comparator) {
			this.mOuterItr = outer;
			this.mInnerItr = inner;
			this.mInnerKeySelector = innerKeySelector;
			this.mOuterKeySelector = outerKeySelector;
			this.mJoint = joint;
			this.mComparator = comparator;
		}

		/**
		 * Try find next.
		 */
		private void tryFindNext() {
			if (mState == 0) {
				this.mOutGroups = new LinqQuery<T>(this.mOuterItr).groupBy(
						this.mOuterKeySelector, this.mComparator).iterator();

				this.mInnerGroups = new TreeMap<TKey, IGrouping<TKey, TInner>>(
						this.mComparator);
				for (IGrouping<TKey, TInner> group : new LinqQuery<TInner>(
						this.mInnerItr).groupBy(this.mInnerKeySelector,
						this.mComparator)) {
					this.mInnerGroups.put(group.getKey(), group);
				}
				this.mState = 1;
			}

			while (this.mState == 1 && mCurrent == null) {
				if (this.mOutGroups.hasNext()) {
					IGrouping<TKey, T> g1 = this.mOutGroups.next();
					IGrouping<TKey, TInner> g2 = this.mInnerGroups.get(g1
							.getKey());
					if (g2 != null) {
						this.mCurrent = new IterableGroup<TKey, TResult>(
								g1.getKey(),
								new CrossJoinIterable<TInner, TResult>(g1, g2,
										this.mJoint));
					}
				} else {
					this.mState = 2;
				}

			}
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			this.tryFindNext();
			return this.mState < 2;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public IGrouping<TKey, TResult> next() {
			if (this.hasNext()) {
				IGrouping<TKey, TResult> rs = this.mCurrent;
				this.mCurrent = null;

				return rs;
			} else {
				throw new NoSuchElementException();
			}
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Cross join the elements of two sequences.
	 *
	 * @param <TInner> the generic type
	 * @param <TResult> the generic type
	 * @param inner            The sequence to join to
	 * @param joint            A {@code Joint<T1, T2, TResult>} to create result element from
	 *            tow matching elements
	 * @return A {@code Query<T>} that has elements of type TResult that are
	 *         obtained by performing a cross join on two sequences.
	 */
	public <TInner, TResult> LinqQuery<TResult> crossJoin(TInner[] inner,
			Joint<T, TInner, TResult> joint) {
		return new LinqQuery<TResult>(new CrossJoinIterable<TInner, TResult>(
				this.mSourceItr, new IterableArray<TInner>(inner), joint));
	}

	/**
	 * Cross join the elements of two sequences.
	 *
	 * @param <TInner> the generic type
	 * @param <TResult> the generic type
	 * @param inner            The sequence to join to
	 * @param joint            A {@code Joint<T1, T2, TResult>} to create result element from
	 *            tow matching elements
	 * @return A {@code Query<T>} that has elements of type TResult that are
	 *         obtained by performing a cross join on two sequences.
	 */
	public <TInner, TResult> LinqQuery<TResult> crossJoin(Iterable<TInner> inner,
			Joint<T, TInner, TResult> joint) {
		return new LinqQuery<TResult>(new CrossJoinIterable<TInner, TResult>(
				this.mSourceItr, inner, joint));
	}

	/**
	 * The Class CrossJoinIterable.
	 *
	 * @param <TInner> the generic type
	 * @param <TResult> the generic type
	 */
	private class CrossJoinIterable<TInner, TResult> implements
			Iterable<TResult> {
		
		/** The m first itr. */
		private Iterable<T> mFirstItr;
		
		/** The m second itr. */
		private Iterable<TInner> mSecondItr;
		
		/** The m joint. */
		private Joint<T, TInner, TResult> mJoint;
		
		/**
		 * Instantiates a new cross join iterable.
		 *
		 * @param first the first
		 * @param second the second
		 * @param joint the joint
		 */
		public CrossJoinIterable(Iterable<T> first, Iterable<TInner> second,
				Joint<T, TInner, TResult> joint) {
			this.mFirstItr = first;
			this.mSecondItr = second;
			this.mJoint = joint;
		}

		/* (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		@Override
		public Iterator<TResult> iterator() {
			return new CrossJoinIterator<TInner, TResult>(this.mFirstItr,
					this.mSecondItr, this.mJoint);
		}
	}

	/**
	 * The Class CrossJoinIterator.
	 *
	 * @param <TInner> the generic type
	 * @param <TResult> the generic type
	 */
	private class CrossJoinIterator<TInner, TResult> implements
			Iterator<TResult> {
		
		/** The m first. */
		private Iterator<T> mFirst;
		
		/** The m second. */
		private Iterable<TInner> mSecond;
		
		/** The m second iterator. */
		private Iterator<TInner> mSecondIterator;
		
		/** The m joint. */
		private Joint<T, TInner, TResult> mJoint;
		
		/** The m currentinstance. */
		private T mCurrentinstance;
		
		/** The m current inner. */
		private TInner mCurrentInner;
		
		/** The m state. */
		private int mState = 0;
		
		/**
		 * Instantiates a new cross join iterator.
		 *
		 * @param first the first
		 * @param second the second
		 * @param joint the joint
		 */
		public CrossJoinIterator(Iterable<T> first, Iterable<TInner> second,
				Joint<T, TInner, TResult> joint) {
			this.mFirst = first.iterator();
			this.mSecond = second;
			this.mJoint = joint;
		}

		/**
		 * Try find next.
		 */
		private void tryFindNext() {
			if (this.mState == 0) {
				this.mSecondIterator = this.mSecond.iterator();
				this.mState = 1;
			}

			while ((this.mCurrentinstance == null || this.mCurrentInner == null)
					&& mState == 1) {
				if (this.mCurrentinstance == null) {
					if (this.mFirst.hasNext()) {
						this.mCurrentinstance = this.mFirst.next();
					} else {
						this.mState = 2;
					}
				} else if (this.mCurrentInner == null) {
					if (this.mSecondIterator.hasNext()) {
						this.mCurrentInner = this.mSecondIterator.next();
					} else {
						this.mCurrentinstance = null;
						this.mSecondIterator = this.mSecond.iterator();
					}
				}
			}
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			this.tryFindNext();
			return this.mState < 2;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public TResult next() {
			if (this.hasNext()) {
				TResult rs = null;
				try {
					rs = this.mJoint.join(this.mCurrentinstance,
							this.mCurrentInner);

				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
				this.mCurrentInner = null;
				return rs;
			} else {
				throw new NoSuchElementException();
			}

		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();

		}

	}

	/**
	 * Bypasses a specified number of elements in a sequence and then returns
	 * the remaining elements.
	 * 
	 * @param count
	 *            The number of elements to skip before returning the remaining
	 *            elements.
	 * @return A {@code Query<T>}that contains the elements that occur after the
	 *         specified index in the input sequence.
	 */
	public LinqQuery<T> skip(int count) {
		Iterable<T> rs;
		if (this.isRandomAccessable(this.mSourceItr)) {
			rs = new RandomSkipIterable(
					this.createRandomAccessor(this.mSourceItr), count);
		} else {
			rs = new SkipIterable(this.mSourceItr, count);
		}

		return new LinqQuery<T>(rs);
	}

	/**
	 * The Class SkipIterable.
	 */
	private class SkipIterable implements Iterable<T> {

		/** The m accessor. */
		private Iterable<T> mAccessor;
		
		/** The m count. */
		private int mCount;
		
		/**
		 * Instantiates a new skip iterable.
		 *
		 * @param accessor the accessor
		 * @param count the count
		 */
		public SkipIterable(Iterable<T> accessor, int count) {
			this.mAccessor = accessor;
			this.mCount = count;
		}

		/* (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		@Override
		public Iterator<T> iterator() {
			return new SkipIterator(this.mAccessor, this.mCount);
		}

	}

	/**
	 * The Class SkipIterator.
	 */
	private class SkipIterator implements Iterator<T> {

		/** The m is skipped. */
		private boolean mIsSkipped = false;
		
		/** The m iterator. */
		private Iterator<T> mIterator;
		
		/** The m count. */
		private int mCount;
		
		/**
		 * Instantiates a new skip iterator.
		 *
		 * @param source the source
		 * @param count the count
		 */
		public SkipIterator(Iterable<T> source, int count) {
			this.mIterator = source.iterator();
			this.mCount = count;
		}

		/**
		 * Do skip.
		 */
		private void doSkip() {
			if (!this.mIsSkipped) {
				this.mIsSkipped = true;
				for (int i = 0; i < mCount && this.mIterator.hasNext(); i++) {
					this.mIterator.hasNext();
				}

			}
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			doSkip();
			return this.mIterator.hasNext();

		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public T next() {
			doSkip();
			return this.mIterator.next();
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();

		}

	}

	/**
	 * The Class RandomSkipIterable.
	 */
	private class RandomSkipIterable implements Iterable<T> {

		/** The _accessor. */
		private IRandomAccessor2<T> _accessor;
		
		/** The _count. */
		private int _count;
		
		/**
		 * Instantiates a new random skip iterable.
		 *
		 * @param accessor the accessor
		 * @param count the count
		 */
		public RandomSkipIterable(IRandomAccessor2<T> accessor, int count) {
			this._accessor = accessor;
			this._count = count;
		}

		/* (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		@Override
		public Iterator<T> iterator() {
			return new RandomSkipIterator(this._accessor, this._count);
		}

	}

	/**
	 * The Class RandomSkipIterator.
	 */
	private class RandomSkipIterator implements Iterator<T> {
		
		/**
		 * Instantiates a new random skip iterator.
		 *
		 * @param accessor the accessor
		 * @param count the count
		 */
		public RandomSkipIterator(IRandomAccessor2<T> accessor, int count) {
			this._accessor = accessor;
			this._pos = count;
		}

		/** The _accessor. */
		private IRandomAccessor2<T> _accessor;

		/** The _pos. */
		private int _pos;

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return _pos < this._accessor.getSize();
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public T next() {

			if (hasNext()) {
				T rs = this._accessor.get(this._pos);
				this._pos++;
				return rs;
			} else {
				throw new NoSuchElementException();
			}
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();

		}

	}

	/**
	 * Bypasses elements in a sequence as long as a specified condition is true
	 * and then returns the remaining elements.
	 * 
	 * @param predicate
	 *            The {@code Predicate<T>} to test each element for a condition
	 * @return A {@code Query<T>}that contains the elements from the source
	 *         sequence starting at the first element in the linear series that
	 *         does not pass the test specified by predicate.
	 */
	public LinqQuery<T> skipWhile(Predicate<T> predicate) {
		return new LinqQuery<T>(new SkipWhileIterable(this.mSourceItr, predicate));
	}

	/**
	 * The Class SkipWhileIterable.
	 */
	private class SkipWhileIterable implements Iterable<T> {
		
		/**
		 * Instantiates a new skip while iterable.
		 *
		 * @param source the source
		 * @param predicate the predicate
		 */
		public SkipWhileIterable(Iterable<T> source, Predicate<T> predicate) {
			this._source = source;
			this._predicate = predicate;
		}

		/** The _source. */
		private Iterable<T> _source;
		
		/** The _predicate. */
		private Predicate<T> _predicate;

		/* (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		@Override
		public Iterator<T> iterator() {
			return new SkipWhileIterator(this._source, this._predicate);
		}

	}

	/**
	 * The Class SkipWhileIterator.
	 */
	private class SkipWhileIterator implements Iterator<T> {

		/**
		 * Instantiates a new skip while iterator.
		 *
		 * @param source the source
		 * @param predicate the predicate
		 */
		public SkipWhileIterator(Iterable<T> source, Predicate<T> predicate) {
			this._source = source.iterator();
			this._predicate = predicate;
		}

		/** The _source. */
		private Iterator<T> _source;
		
		/** The _predicate. */
		private Predicate<T> _predicate;

		/** The _state. */
		private int _state = 0; // 0 : unskipped; 1 : first : 2 : remains;
		
		/** The _first. */
		private T _first;

		/**
		 * Do skip.
		 *
		 * @throws Exception the exception
		 */
		private void doSkip() throws Exception {
			if (_state == 0) {
				while (_source.hasNext()) {
					T item = _source.next();
					if (this._predicate.evaluate(item)) {
						this._state = 1;
						this._first = item;

						return;
					}
				}
				this._state = 2;
			}
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			try {
				doSkip();
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
			return this._state == 1 || this._source.hasNext();
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public T next() {
			try {
				doSkip();
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}

			if (this._state == 1) {
				this._state = 2;
				return this._first;

			} else {
				return this._source.next();
			}

		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();

		}

	}

	/**
	 * Returns elements from a sequence as long as a specified condition is
	 * true.
	 * 
	 * @param predicate
	 *            A {@code Predicate<T>} to test each element for a condition.
	 * @return A {@code Query<T>}that contains the elements from the input
	 *         sequence that occur before the element at which the test no
	 *         longer passes.
	 */
	public LinqQuery<T> takeWhile(Predicate<T> predicate) {
		return new LinqQuery<T>(new TakeWhileIterable(this.mSourceItr, predicate));
	}

	/**
	 * The Class TakeWhileIterable.
	 */
	private class TakeWhileIterable implements Iterable<T> {

		/**
		 * Instantiates a new take while iterable.
		 *
		 * @param source the source
		 * @param predicate the predicate
		 */
		public TakeWhileIterable(Iterable<T> source, Predicate<T> predicate) {
			this._source = source;
			this._predicate = predicate;
		}

		/** The _source. */
		private Iterable<T> _source;
		
		/** The _predicate. */
		private Predicate<T> _predicate;

		/* (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		@Override
		public Iterator<T> iterator() {
			return new TakeWhileIterator(this._source, this._predicate);
		}

	}

	/**
	 * The Class TakeWhileIterator.
	 */
	private class TakeWhileIterator implements Iterator<T> {
		
		/**
		 * Instantiates a new take while iterator.
		 *
		 * @param source the source
		 * @param predicate the predicate
		 */
		public TakeWhileIterator(Iterable<T> source, Predicate<T> predicate) {
			this.mSourceItr = source.iterator();
			this.mPredicate = predicate;
		}

		/** The m source itr. */
		private Iterator<T> mSourceItr;
		
		/** The m predicate. */
		private Predicate<T> mPredicate;
		
		/** The m current. */
		private T mCurrent = null;
		
		/** The m has next. */
		private boolean mHasNext = true;

		/**
		 * Try get next.
		 */
		private void tryGetNext() {
			if (mHasNext) {

				if (mCurrent == null) {
					if (this.mSourceItr.hasNext()) {
						T item = this.mSourceItr.next();
						try {
							if (this.mPredicate.evaluate(item)) {
								this.mCurrent = item;
							} else {
								this.mHasNext = false;
							}
						} catch (Exception e) {
							throw new IllegalStateException(e);
						}
					} else {
						this.mHasNext = false;
					}

				}
			}

		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			tryGetNext();
			return this.mHasNext;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public T next() {
			tryGetNext();
			T rs = this.mCurrent;
			this.mCurrent = null;
			return rs;

		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();

		}
	}

	/**
	 * Merges two sequences by using the specified
	 * {@code Joint<T1, T2, TResult>}.
	 *
	 * @param <TOther> the generic type
	 * @param <TResult> the generic type
	 * @param other            The second sequence to merge.
	 * @param joint            The {@code Joint<T1, T2, TResult>} that specifies how to merge
	 *            the elements from the two sequences.
	 * @return A {@code Query<T>}that contains merged elements of two input
	 *         sequences.
	 */
	public <TOther, TResult> LinqQuery<TResult> zip(TOther[] other,
			Joint<T, TOther, TResult> joint) {
		return new LinqQuery<TResult>(new ZipIterable<TOther, TResult>(
				this.mSourceItr, new IterableArray<TOther>(other), joint));
	}

	/**
	 * Merges two sequences by using the specified
	 * {@code Joint<T1, T2, TResult>}.
	 *
	 * @param <TOther> the generic type
	 * @param <TResult> the generic type
	 * @param other            The second sequence to merge.
	 * @param joint            The {@code Joint<T1, T2, TResult>} that specifies how to merge
	 *            the elements from the two sequences.
	 * @return A {@code Query<T>}that contains merged elements of two input
	 *         sequences.
	 */
	public <TOther, TResult> LinqQuery<TResult> zip(Iterable<TOther> other,
			Joint<T, TOther, TResult> joint) {
		return new LinqQuery<TResult>(new ZipIterable<TOther, TResult>(
				this.mSourceItr, other, joint));
	}

	/**
	 * The Class ZipIterable.
	 *
	 * @param <TOther> the generic type
	 * @param <TResult> the generic type
	 */
	private class ZipIterable<TOther, TResult> implements Iterable<TResult> {
		
		/** The m first itr. */
		private Iterable<T> mFirstItr;
		
		/** The m other ith. */
		private Iterable<TOther> mOtherIth;
		
		/** The m joint. */
		private Joint<T, TOther, TResult> mJoint;
		
		/**
		 * Instantiates a new zip iterable.
		 *
		 * @param first the first
		 * @param second the second
		 * @param joint the joint
		 */
		public ZipIterable(Iterable<T> first, Iterable<TOther> second,
				Joint<T, TOther, TResult> joint) {
		
			this.mFirstItr = first;
			this.mOtherIth = second;
			this.mJoint = joint;
		}

		/* (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		@Override
		public Iterator<TResult> iterator() {
			return new ZipIterator<TOther, TResult>(this.mFirstItr, this.mOtherIth,
					this.mJoint);
		}

	}

	/**
	 * The Class ZipIterator.
	 *
	 * @param <TOther> the generic type
	 * @param <TResult> the generic type
	 */
	private class ZipIterator<TOther, TResult> implements Iterator<TResult> {
		
		/** The m first. */
		private Iterator<T> mFirst;
		
		/** The m other itr. */
		private Iterator<TOther> mOtherItr;
		
		/** The m joint. */
		private Joint<T, TOther, TResult> mJoint;
		
		/**
		 * Instantiates a new zip iterator.
		 *
		 * @param first the first
		 * @param second the second
		 * @param joint the joint
		 */
		public ZipIterator(Iterable<T> first, Iterable<TOther> second,
				Joint<T, TOther, TResult> joint) {
			this.mFirst = first.iterator();
			this.mOtherItr = second.iterator();
			this.mJoint = joint;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return mFirst.hasNext() && this.mOtherItr.hasNext();
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public TResult next() {
			T f = this.mFirst.next();
			TOther s = this.mOtherItr.next();
			TResult rs;
			try {
				rs = this.mJoint.join(f, s);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
			return rs;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();

		}

	}

	/**
	 * Creates a {@code Map<TKey, TValue>}from an {@code Query<T>}according to a
	 * specified key {@code Selector}.
	 *
	 * @param <TKey> the generic type
	 * @param selector            The {@code Selector} to extract key
	 * @return A {@code Map<TKey, TValue>} that contains keys and values.
	 * @throws Exception the exception
	 */
	public <TKey> Map<TKey, T> toMap(Selector<T, TKey> selector)
			throws Exception {
		HashMap<TKey, T> rs = new HashMap<TKey, T>();
		for (T item : this.mSourceItr) {
			TKey key = selector.select(item);
			if (!rs.containsKey(key)) {
				rs.put(key, item);
			} else {
				throw new DuplicateKeyException();
			}
		}

		return rs;
	}

	/**
	 * Creates a {@code Map<TKey, TValue>}from an {@code Query<T>}according to a
	 * specified key {@code Selector} and key {@code Comparator<T>}.
	 *
	 * @param <TKey> the generic type
	 * @param selector            The {@code Selector} to extract key.
	 * @param comparator            The {@code Comparator<T>} to compare keys.
	 * @return A {@code Map<TKey, TValue>} that contains keys and values.
	 * @throws Exception the exception
	 */
	public <TKey> Map<TKey, T> toMap(Selector<T, TKey> selector,
			Comparator<TKey> comparator) throws Exception {
		TreeMap<TKey, T> rhsMap = new TreeMap<TKey, T>(comparator);
		for (T item : this.mSourceItr) {
			TKey key = selector.select(item);
			if (!rhsMap.containsKey(key)) {
				rhsMap.put(key, item);
			} else {
				throw new DuplicateKeyException();
			}
		}
		return rhsMap;
	}
}
