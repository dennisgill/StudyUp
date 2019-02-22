package edu.studyup.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import edu.studyup.entity.Event;

/***
 * 
 * This class is a temporary class to be used in place of a database. The static
 * variable eventList holds all the event data.
 * 
 * @author Shivani
 * 
 */
public class DataStorage {
	public final static Map<Integer, Event> eventData = Collections.unmodifiableMap(new HashMap<Integer, Event>());
}
