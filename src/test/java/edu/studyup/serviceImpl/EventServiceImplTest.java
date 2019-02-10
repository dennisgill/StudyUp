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
		long futureTime = 1649837547189L;
		event.setDate(new Date(futureTime));
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
	
	// Event name equals 20 characters -- Test fails
	@Test 
	void testEventNameLength_20_Chars() throws StudyUpException {
		int eventID = 1;
		eventServiceImpl.updateEventName(eventID, "12345678912345678912");
		assertEquals("12345678912345678912", DataStorage.eventData.get(eventID).getName());
	}
	
	// Event name less than 20 characters 
	@Test 
	void testEventNameLength_LessThan20Chars() throws StudyUpException {
		int eventID = 1;
		eventServiceImpl.updateEventName(eventID, "123456789123456789");
		assertEquals("123456789123456789", DataStorage.eventData.get(eventID).getName());
	}
		
	// Event name greater than 20 characters 
	@Test 
	void testEventNameLength_GreaterThan20Chars() throws StudyUpException {
		int eventID = 1;
		Assertions.assertThrows(StudyUpException.class, () -> {eventServiceImpl.updateEventName(eventID, "123456789123456789123456789");});
	}
	
	// Update name of event that doesn't exist
	@Test
	void testUpdateEvent_WrongEventID() {
		int eventID = 3;
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.updateEventName(eventID, "Renamed Event 3");
		});
	}
	
	// Added a second student to one event
	@Test
	void testAddStudent_TwoStudents() throws StudyUpException {
		int eventID = 1;
		Student student2 = new Student();
		student2.setFirstName("Bob");
		student2.setLastName("Dob");
		student2.setEmail("BobDob@email.com");
		student2.setId(2);
		assertEquals(eventServiceImpl.addStudentToEvent(student2, eventID), DataStorage.eventData.get(eventID));
	}
	
	// Added a second and third student to one event
	@Test
	void testAddStudent_TooMany() throws StudyUpException {
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
	
	// Add student to an event that did not have students in it
	@Test
	void testAddStudent_EventWithNoStudents() throws StudyUpException {
		int eventID2 = 2;
		
		Student student2 = new Student();
		student2.setFirstName("Bob");
		student2.setLastName("Dob");
		student2.setEmail("BobDob@email.com");
		student2.setId(2);
		Event event2 = new Event();
		event2.setEventID(2);
		long futureTime = 1649837547189L;
		event2.setDate(new Date(futureTime));
		event2.setName("Event 2");
		Location location = new Location(-122, 37);
		event2.setLocation(location);
		DataStorage.eventData.put(event2.getEventID(), event2);
		
		List<Student> students = new ArrayList<>();
		students.add(student2);
		
		// Only event 1 should be in the returned active events list
		assertEquals(eventServiceImpl.addStudentToEvent(student2, eventID2), DataStorage.eventData.get(eventID2));
	}
	
	// Add student to an event that does not exist
	@Test
	void testAddStudent_EventDoesntExist() throws StudyUpException {
		int eventID = 3;
		
		Student student2 = new Student();
		student2.setFirstName("Bob");
		student2.setLastName("Dob");
		student2.setEmail("BobDob@email.com");
		student2.setId(2);

		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.addStudentToEvent(student2, eventID);
		});
	}
	
	// One event in the future is in the database
	@Test
	void testActiveEvents_OneFutureEvent() throws StudyUpException {
		int eventID = 1;
		
		List<Event> active = new ArrayList<>();
		active.add(DataStorage.eventData.get(eventID));

		assertEquals((eventServiceImpl.getActiveEvents()).size(), 1);
	}
	
	// One event in the future and one event in the past are in the database
	@Test
	void testActiveEvents_OneFutureOnePastEvent() throws StudyUpException {
		int eventID = 1;
		int eventID2 = 2;
		
		//Create Event2 in the past
		Event event2 = new Event();
		event2.setEventID(2);
		long pastTime = 1449837547189L;
		event2.setDate(new Date(pastTime));
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
	
	// One event in the future and one event in the past are in the database
	@Test
	void testPastEvents_OneFutureOnePastEvent() throws StudyUpException {
		//Create Event2 in the past
		Event event2 = new Event();
		event2.setEventID(2);
		long pastTime = 1449837547189L;
		event2.setDate(new Date(pastTime));
		event2.setName("Event 2");
		Location location = new Location(-122, 37);
		event2.setLocation(location);
		DataStorage.eventData.put(event2.getEventID(), event2);
		
		// Only event 2 should be in the returned past events list
		assertEquals((eventServiceImpl.getPastEvents()).size(), 1);
	}
	
	// One event in the future and one event in the past are in the database
		@Test
		void testDeleteEvent_GoodCase() throws StudyUpException {
			int eventID = 1;
			Event tempEvent = DataStorage.eventData.get(eventID);
			assertEquals(eventServiceImpl.deleteEvent(eventID), tempEvent);
		}
}
