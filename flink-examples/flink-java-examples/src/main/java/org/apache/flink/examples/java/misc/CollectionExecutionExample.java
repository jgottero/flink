/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.examples.java.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.io.LocalCollectionOutputFormat;
import org.apache.flink.api.java.tuple.Tuple2;

/** 
 * This example shows how to use the collection based execution of Flink.
 * 
 * The collection based execution is a local mode that is not using the full Flink runtime.
 * DataSet transformations are executed on Java collections.
 * 
 * See the "Local Execution" section in the documentation for more details: 
 * 	http://flink.apache.org/docs/latest/local_execution.html
 * 
 */
public class CollectionExecutionExample {
	
	/**
	 * POJO class representing a user
	 */
	public static class User {
		public int userIdentifier;
		public String name;
		public User() {}
		public User(int userIdentifier, String name) {
			this.userIdentifier = userIdentifier; this.name = name;
		}
		public String toString() {
			return "User{userIdentifier="+userIdentifier+" name="+name+"}";
		}
	}
	
	/**
	 * POJO for an EMail.
	 */
	public static class EMail {
		public int userId;
		public String subject;
		public String body;
		public EMail() {}
		public EMail(int userId, String subject, String body) {
			this.userId = userId; this.subject = subject; this.body = body;
		}
		public String toString() {
			return "eMail{userId="+userId+" subject="+subject+" body="+body+"}";
		}
		
	}
	public static void main(String[] args) throws Exception {
		// initialize a new Collection-based execution environment
		final ExecutionEnvironment env = ExecutionEnvironment.createCollectionsEnvironment();
		
		// create objects for users and emails
		User[] usersArray = { new User(1, "Peter"), new User(2, "John"), new User(3, "Bill") };
		EMail[] emailsArray = {new EMail(1, "Re: Meeting", "How about 1pm?"),
							new EMail(1, "Re: Meeting", "Sorry, I'm not availble"),
							new EMail(3, "Re: Re: Project proposal", "Give me a few more days to think about it.")};
		
		// convert objects into a DataSet
		DataSet<User> users = env.fromCollection(Arrays.asList(usersArray));
		DataSet<EMail> emails = env.fromCollection(Arrays.asList(emailsArray));
		
		// join the two DataSets
		DataSet<Tuple2<User,EMail>> joined = users.join(emails).where("userIdentifier").equalTo("userId");
		
		// retrieve the resulting Tuple2 elements into a ArrayList.
		Collection<Tuple2<User,EMail>> result = new ArrayList<Tuple2<User,EMail>>(3);
		joined.output(new LocalCollectionOutputFormat<Tuple2<User,EMail>>(result));
		
		// kick off execution.
		env.execute();
		
		// Do some work with the resulting ArrayList (=Collection).
		for(Tuple2<User, EMail> t : result) {
			System.err.println("Result = "+t);
		}
	}
}
