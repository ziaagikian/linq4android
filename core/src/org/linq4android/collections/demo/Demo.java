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
package org.linq4android.collections.demo;

import org.linq4android.collections.Predicate;
import org.linq4android.collections.Queries;
import org.linq4android.collections.LinqQuery;
import org.linq4android.collections.Selector;
/**
 * The Class Demo.
 */
public class Demo {

	/**
	 * The main method.
	 *
	 * @author ziaagikian
	 * @param args the arguments
	 */
	public static void main(String[] args)  {
		try {
			testWhereQuery();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test where query.
	 *
	 * @throws Exception the exception
	 */
	private static void testWhereQuery() throws Exception{
		int size = 100;
		 int[] values = new int[size] ;
		 for(int i = 0; i < size; i++){
			 values[i] = i;
		 }
		   LinqQuery<Integer> evenValues = Queries.query(values)
				   .where(new Predicate<Integer>(){

			@Override
			public boolean evaluate(Integer value) throws Exception {
				return value % 2 == 0;
			}})
			.orderByDescending(new Selector<Integer, Integer>(){

				@Override
				public Integer select(Integer item) {
					return item;
				}});
		   
//		   for(int x : evenValues)
//		   {
//			   System.out.println(x);
//		   }
	}

}
