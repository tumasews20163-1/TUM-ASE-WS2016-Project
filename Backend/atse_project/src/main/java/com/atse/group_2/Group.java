package com.atse.group_2;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Group {

	@Id
	String name; // String
	String tutor; // username of the tutor responsible for the group
	List<String> students; // usernames of the students in the group

	public Group() {
		if (this.students == null)
			students = new ArrayList<String>();
	}

	public Group(String name, String tutor) {
		this.name = name;
		this.tutor = tutor;
		students = new ArrayList<String>();
	}

	public List<Person> calculateBonuses() {

		List<Person> bonuses = new ArrayList();
		for (int i = 0; i < students.size(); i++) {
			Person person = ObjectifyService.ofy().load().type(Person.class).id(students.get(i)).now();

			int[] presence = person.presence;
			int countNotPresent = 0;
			for (int j = 0; j < presence.length; j++) {
				if (presence[j] == 0) {
					countNotPresent++;
				}
			}
			if (countNotPresent < 3 && person.presentation == true) {
				bonuses.add(person);
			}

		}

		return bonuses;
	}

}
