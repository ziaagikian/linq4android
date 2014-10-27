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
 * Provides convenient static methods for creating a {@code Linq}.
 *
 * @author ziaagikian
 */
public final class Queries {


	/**
	 * Recursive searches specified object's ancestor by specified parent
	 * selector.
	 *
	 * @param <T> the generic type
	 * @param child            the object to search
	 * @param selector            selector for getting specified child's parent
	 * @return An {@code Query<T>} contains specified object and it's ancestors
	 */
	public static <T> LinqQuery<T> flatternAncestors(T child,
			Selector<T, T> selector) {
		return new LinqQuery<T>(new AncestorIterable<T>(child, selector));
	}

	/**
	 * The Class AncestorIterable.
	 *
	 * @param <T> the generic type
	 */
	private static class AncestorIterable<T> implements Iterable<T> {
		
		/** The m child. */
		private T mChild;
		
		/** The m selector. */
		private Selector<T, T> mSelector;

		/**
		 * Instantiates a new ancestor iterable.
		 *
		 * @param child the child
		 * @param selector the selector
		 */
		public AncestorIterable(T child, Selector<T, T> selector) {
			this.mChild = child;
			this.mSelector = selector;
		}

		/* (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		@Override
		public Iterator<T> iterator() {
			return new AncestorIterator<T>(this.mChild, this.mSelector);
		}
	}

	/**
	 * The Class AncestorIterator.
	 *
	 * @param <T> the generic type
	 */
	private static class AncestorIterator<T> implements Iterator<T> {
		
		/** The m next instance. */
		private T mNextInstance;
		
		/** The m selector. */
		private Selector<T, T> mSelector;

		/**
		 * Instantiates a new ancestor iterator.
		 *
		 * @param child the child
		 * @param selector the selector
		 */
		public AncestorIterator(T child, Selector<T, T> selector) {
			this.mNextInstance = child;
			this.mSelector = selector;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return mNextInstance != null;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public T next() {
			T rs = mNextInstance;
			mNextInstance = mSelector.select(mNextInstance);
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
	 * Recursive searches specified object's children by specified child
	 * selector.
	 *
	 * @param <T> the generic type
	 * @param ancestor            the object to search
	 * @param selector            selector for getting specified object's children
	 * @return An {@code Query<T>} contains specified object and all children
	 */

	public static <T> LinqQuery<T> flatternChildren(T ancestor,
			Selector<T, Iterable<T>> selector) {
		Iterable<T> rs = new FlatternerIterable<T>(ancestor, selector);
		return new LinqQuery<T>(rs);
	}

	/**
	 * The Class FlatternerIterable.
	 *
	 * @param <T> the generic type
	 */
	private static class FlatternerIterable<T> implements Iterable<T> {
		
		/** The m parent. */
		private T mParent;
		
		/** The m selector. */
		private Selector<T, Iterable<T>> mSelector;
		
		/**
		 * Instantiates a new flatterner iterable.
		 *
		 * @param parent the parent
		 * @param selector the selector
		 */
		public FlatternerIterable(final T parent,
				final Selector<T, Iterable<T>> selector) {
			this.mParent = parent;
			this.mSelector = selector;
		}

		/* (non-Javadoc)
		 * @see java.lang.Iterable#iterator()
		 */
		@Override
		public Iterator<T> iterator() {
			return new FlatternerIterator<T>(mParent, mSelector);
		}
	}

	/**
	 * The Class FlatternerIterator.
	 *
	 * @param <T> the generic type
	 */
	private static class FlatternerIterator<T> implements Iterator<T> {
		private T mParent;
		private Selector<T, Iterable<T>> mSelector;
		private Iterator<T> mChildren = null;;
		private FlatternerIterator<T> mChildItr = null;
		// 0: init; //1: visit children 2://end;
		private int mState = 0;
		
		/**
		 * Instantiates a new flatterner iterator.
		 *
		 * @param parent the parent
		 * @param selector the selector
		 */
		public FlatternerIterator(final T parent,
				final Selector<T, Iterable<T>> selector) {
			this.mParent = parent;
			this.mSelector = selector;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return mState < 2;
		}

		/**
		 * Load chidren.
		 */
		private void loadChidren() {
			Iterable<T> iterable = mSelector.select(mParent);
			if (iterable != null) {
				mChildren = iterable.iterator();

				if (mChildren.hasNext()) {
					mChildItr = new FlatternerIterator<T>(
							mChildren.next(), this.mSelector);
					mState = 1;
				} else {
					mState = 2;
				}

			} else {
				mState = 2;
			}
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public T next() {
			if (mState == 0) {
				loadChidren();
				return this.mParent;
			} else if (mState == 1) {
				T rs = mChildItr.next();

				if (!mChildItr.hasNext()) {
					if (mChildren.hasNext()) {
						mChildItr = new FlatternerIterator<T>(
								mChildren.next(), this.mSelector);
					} else {
						mState = 2;
					}
				}
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
	 * Query.
	 *
	 * @param <T> the generic type
	 * @param source the source
	 * @return the linq
	 */
	public static <T> LinqQuery<T> query(Iterable<T> source) {
		return new LinqQuery<T>(source);
	}

	/**
	 * Query.
	 *
	 * @param <T> the generic type
	 * @param source the source
	 * @return the linq
	 */
	public static <T> LinqQuery<T> query(T[] source) {
		return new LinqQuery<T>(source);
	}

	/**
	 * Query.
	 *
	 * @param source the source
	 * @return the linq
	 */
	public static LinqQuery<Boolean> query(boolean[] source) {
		ArrayList<Boolean> rs = new ArrayList<Boolean>(source.length);
		for (Boolean b : source) {
			rs.add(b);
		}

		return new LinqQuery<Boolean>(rs);
	}

	/**
	 * Query.
	 *
	 * @param source the source
	 * @return the linq
	 */
	public static LinqQuery<Short> query(short[] source) {
		ArrayList<Short> rs = new ArrayList<Short>(source.length);
		for (Short b : source) {
			rs.add(b);
		}
		return new LinqQuery<Short>(rs);
	}

	/**
	 * Query.
	 *
	 * @param source the source
	 * @return the linq
	 */
	public static LinqQuery<Integer> query(int[] source) {
		ArrayList<Integer> rs = new ArrayList<Integer>(source.length);
		for (Integer b : source) {
			rs.add(b);
		}

		return new LinqQuery<Integer>(rs);
	}

	/**
	 * Query.
	 *
	 * @param source the source
	 * @return the linq
	 */
	public static LinqQuery<Long> query(long[] source) {
		ArrayList<Long> rs = new ArrayList<Long>(source.length);
		for (Long b : source) {
			rs.add(b);
		}
		return new LinqQuery<Long>(rs);
	}

	/**
	 * Query.
	 *
	 * @param source the source
	 * @return the linq
	 */
	public static LinqQuery<Float> query(float[] source) {
		ArrayList<Float> rs = new ArrayList<Float>(source.length);
		for (Float b : source) {
			rs.add(b);
		}
		return new LinqQuery<Float>(rs);
	}

	/**
	 * Query.
	 *
	 * @param source the source
	 * @return the linq
	 */
	public static LinqQuery<Double> query(double[] source) {
		ArrayList<Double> rs = new ArrayList<Double>(source.length);
		for (Double b : source) {
			rs.add(b);
		}
		return new LinqQuery<Double>(rs);
	}

	/**
	 * Query.
	 *
	 * @param source the source
	 * @return the linq
	 */
	public static LinqQuery<Character> query(char[] source) {
		ArrayList<Character> rs = new ArrayList<Character>(source.length);
		for (Character b : source) {
			rs.add(b);
		}
		return new LinqQuery<Character>(rs);
	}
}
