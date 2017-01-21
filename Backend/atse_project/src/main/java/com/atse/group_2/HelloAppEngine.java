/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atse.group_2;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import com.example.guestbook.Greeting;
//import com.example.guestbook.Guestbook;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

public class HelloAppEngine extends HttpServlet {

	static {
		ObjectifyService.register(Group.class);
		ObjectifyService.register(Person.class);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setContentType("text/plain");
		response.getWriter().println("Hello App Engine!");

		try {
			int countPeople = ObjectifyService.ofy().load().type(Person.class).count();
			int countGroups = ObjectifyService.ofy().load().type(Group.class).count();
			response.getWriter().println("Number of people in Objectify database:" + countPeople);
			response.getWriter().println("Number of groups in Objectify database:" + countGroups);

			if (countGroups == 0) {
				ObjectifyService.ofy().save().entity(new Group("1", "tutor1")).now();
				ObjectifyService.ofy().save().entity(new Group("2", "tutor2")).now();
				ObjectifyService.ofy().save().entity(new Group("3", "tutor3")).now();
			}
			
			if (countPeople == 0) {
				ObjectifyService.ofy().save().entity(new Person("tutor1", "1111", 1,"1")).now();
				ObjectifyService.ofy().save().entity(new Person("tutor2", "1111", 1,"2")).now();
				ObjectifyService.ofy().save().entity(new Person("tutor3", "1111", 1,"3")).now();

				// Set the last param to null to clear group associations
				ObjectifyService.ofy().save().entity(new Person("student1", "1111", "Student1", "LastName1", 0, "1")).now();
				ObjectifyService.ofy().save().entity(new Person("student2", "1111", "Student2", "LastName2",0, "1")).now();
				ObjectifyService.ofy().save().entity(new Person("student3", "1111", "Student3", "LastName3",0, "2")).now();
				ObjectifyService.ofy().save().entity(new Person("MyTestUsername", "MyTestPassword123", 0, "3")).now();
			}



			List<Person> people = ObjectifyService.ofy().load().type(Person.class).list();
			Iterator<Person> it1 = people.iterator();

			while (it1.hasNext()) {
				Person person_temp = it1.next();
				response.getWriter()
						.println(person_temp.username + " : " + person_temp.password + " : " + person_temp.role);
			}

			List<Group> groups = ObjectifyService.ofy().load().type(Group.class).list();
			Iterator<Group> it2 = groups.iterator();

			while (it2.hasNext()) {
				Group group_temp = it2.next();
				response.getWriter().println(group_temp.name + " : " + group_temp.tutor);
			}

		} catch (Exception e) {
			response.getWriter().println(e);
		}

	}
}
