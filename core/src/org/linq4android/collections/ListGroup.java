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
 * Provides default implementation of <IGrouping<TKey, TElement>>.
 *
 * @author ziaagikian
 * @param <TKey> the generic type
 * @param <TElement> the generic type
 */

class ListGroup<TKey, TElement> extends ArrayList<TElement> implements
		IGrouping<TKey, TElement> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3679138958250091510L;
	
	/** The m key. */
	private TKey mKey;

	/**
	 * Instantiates a new list group.
	 *
	 * @param key the key
	 */
	public ListGroup(TKey key) {
		mKey = key;
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
		return new LinqQuery<TElement>(this);
	}
}
