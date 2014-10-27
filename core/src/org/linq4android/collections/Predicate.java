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

/**
 * Provides a interface for testing value for a condition.
 * 
 * @author ziaagikian
 *
 * @param <T> Parameter type of predicate
 */
public interface Predicate<T> {

	/**
	 * Evaluate the predicate using specified parameter.
	 *
	 * @param obj the object to evaluate
	 * @return true if the object is satisfied with the condition; otherwise, false
	 * @throws Exception the exception
	 */
	public boolean evaluate(T obj) throws Exception;
}
