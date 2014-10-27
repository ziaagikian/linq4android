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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The Class IterableArray.
 *
 * @param <T> the generic type
 */
class IterableArray<T> implements Iterable<T>, ICountable
{
	
	/** The m source. */
	private T[] mSource;
	
	/**
	 * Instantiates a new iterable array.
	 * @param source the source
	 */
	public IterableArray(T[] source)
	{
		this.mSource = source;
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<T> iterator() {

		return new ArrayIterator(this.mSource);
	}

	

	/**
	 * Gets the source.
	 *
	 * @return the source
	 */
	public T[] getSource()
	{
		return mSource;
	}

	/* (non-Javadoc)
	 * @see org.linq4android.collections.ICountable#count()
	 */
	@Override
	public int count() {

		return mSource.length;
	}
	
	/**
	 * The Class ArrayIterator.
	 */
	private class ArrayIterator implements Iterator<T>
	{
		
		/** The m index. */
		private int mIndex = 0;

		/** The m source. */
		private T[] mSource;
		
		/**
		 * Instantiates a new array iterator.
		 *
		 * @param source the source
		 */
		public ArrayIterator(T[] source)
		{
			this.mSource = source;
		}

		

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return mIndex < this.mSource.length;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public T next() {
			if(mIndex < this.mSource.length)
			{
				T rs = this.mSource[this.mIndex];
				this.mIndex ++;
				return rs;
			}
			else
			{
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

}