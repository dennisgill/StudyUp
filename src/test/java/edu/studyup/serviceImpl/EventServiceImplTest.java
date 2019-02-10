package edu.studyup.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import edu.studyup.entity.Event;
import edu.studyup.entity.Location;
import edu.studyup.entity.Student;
import edu.studyup.util.DataStorage;
import edu.studyup.util.StudyUpException;

class EventServiceImplTest {

	EventServiceImpl eventServiceImpl;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		eventServiceImpl = new EventServiceImpl();
		//Create Student
		Student student = new Student();
		student.setFirstName("John");
		student.setLastName("Doe");
		student.setEmail("JohnDoe@email.com");
		student.setId(1);
		
		//Create Event1
		Event event = new Event();
		event.setEventID(1);
		event.setDate(new Date(1591288162));
		event.setName("Event 1");
		Location location = new Location(-122, 37);
		event.setLocation(location);
		List<Student> eventStudents = new ArrayList<>();
		eventStudents.add(student);
		event.setStudents(eventStudents);
		
		
		DataStorage.eventData.put(event.getEventID(), event);
	}

	@AfterEach
	void tearDown() throws Exception {
		DataStorage.eventData.clear();
	}

	@Test
	void testUpdateEventName_GoodCase() throws StudyUpException {
		int eventID = 1;
		eventServiceImpl.updateEventName(eventID, "Renamed Event 1");
		assertEquals("Renamed Event 1", DataStorage.eventData.get(eventID).getName());
	}
	
	@Test
	void testUpdateEventNameLength_badCase() throws StudyUpException {
		int eventID = 1;
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.updateEventName(eventID, "Renamed Event 1 Extra Letters");
		  });
	}
	
	@Test
	void testUpdateEventNameLengthandEventID_badCase() throws StudyUpException {
		int eventID = 3;
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.updateEventName(eventID, "Renamed Event 1 Extra Letters");
		  });
	}
	
	@Test
	void testUpdateEvent_WrongEventID_badCase() {
		int eventID = 3;
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.updateEventName(eventID, "Renamed Event 3");
		  });
	}
	
	@Test
	void testAddStudent_GoodCase() throws StudyUpException {
		int eventID = 1;
		Student student2 = new Student();
		student2.setFirstName("Bob");
		student2.setLastName("Dob");
		student2.setEmail("BobDob@email.com");
		student2.setId(2);
		assertEquals(eventServiceImpl.addStudentToEvent(student2, eventID), DataStorage.eventData.get(eventID));
	}
	
	@Test
	void testAddStudentTooMany_BadCase() throws StudyUpException {
		int eventID = 1;
		//Event event;
		Student student2 = new Student();
		student2.setFirstName("Bob");
		student2.setLastName("Dob");
		student2.setEmail("BobDob@email.com");
		student2.setId(2);
		Student student3 = new Student();
		student3.setFirstName("Joe");
		student3.setLastName("Doe");
		student3.setEmail("JoeDoe@email.com");
		student3.setId(3);
		eventServiceImpl.addStudentToEvent(student2, eventID);
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.addStudentToEvent(student3, eventID);
		  });
	}
	
	@Test
	void testActiveEvents_OneFutureEventCase() throws StudyUpException {
		int eventID = 1;
		
		List<Event> active = new ArrayList<>();
		active.add(DataStorage.eventData.get(eventID));

		assertEquals((eventServiceImpl.getActiveEvents()).size(), 1);
	}
	
	@Test
	void testActiveEvents_OneFutureOnePastEventCase() throws StudyUpException {
		int eventID = 1;
		int eventID2 = 2;
		
		//Create Event2 in the past
		Event event2 = new Event();
		event2.setEventID(2);
		event2.setDate(new Date(1455057762));
		event2.setName("Event 2");
		Location location = new Location(-122, 37);
		event2.setLocation(location);
		DataStorage.eventData.put(event2.getEventID(), event2);
		
		List<Event> active = new ArrayList<>();
		active.add(DataStorage.eventData.get(eventID));
		active.add(DataStorage.eventData.get(eventID2));
		
		// Only event 1 should be in the returned active events list
		assertEquals((eventServiceImpl.getActiveEvents()).size(), 1);
	}
}
