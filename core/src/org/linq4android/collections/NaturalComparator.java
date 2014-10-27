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

import java.util.Comparator;

import org.linq4android.dependencies.java7.Objects;

/**
 * The Class NaturalComparator.
 *
 * @param <T> The type to compare. It must implement <Comparable>
 */
public class NaturalComparator<T> implements Comparator<T> {

	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public int compare(T arg0, T arg1) {
		
		if(arg0 == null && arg1 == null)
		{
			return 0;
		}
		else if(arg0 == null)
		{
			return -1;
		}
		else if(arg1 == null)
		{
			return 1;
		}
		else if(arg0 instanceof Comparable)
		{
			return ((Comparable)arg0).compareTo(arg1);
		}
		else if(Objects.equals(arg0, arg1))
		{
			return 0;
		}
		else
		{
			Integer h1 = arg0.hashCode();
			Integer h2 = arg1.hashCode();
			return h1.compareTo(h2);
		}
	}

}
