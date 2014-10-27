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

/**
 * The Class IterableGroup.
 *
 * @param <TKey> the generic type
 * @param <TElement> the generic type
 */
class IterableGroup<TKey, TElement> implements IGrouping<TKey, TElement> {

	/** The m source. */
	private Iterable<TElement> mSource;
	
	/** The m key. */
	private TKey mKey;

	/**
	 * Instantiates a new iterable group.
	 *
	 * @param key the key
	 * @param source the source
	 */
	public IterableGroup(TKey key, Iterable<TElement> source) {
		mKey = key;
		mSource = source;
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<TElement> iterator() {
		return mSource.iterator();
	}

	/* (non-Javadoc)
	 * @see org.linq4android.collections.IGrouping#getKey()
	 */
	@Override
	public TKey getKey() {
		return mKey;
	}

	/* (non-Javadoc)
	 * @see org.linq4android.collections.IGrouping#toQuery()
	 */
	@Override
	public LinqQuery<TElement> toQuery() {
		return new LinqQuery<TElement>(mSource);
	}

}
