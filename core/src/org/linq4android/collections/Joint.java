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
 * The Interface Joint.
 *
 * @param <T1> the generic type
 * @param <T2> the generic type
 * @param <TResult> the generic type
 */
public interface Joint<T1, T2, TResult> {
   
   /**
    * Join.
    *
    * @param arg1 the arg1
    * @param arg2 the arg2
    * @return the t result
    * @throws Exception the exception
    */
   TResult join(T1 arg1, T2 arg2) throws Exception;
}
