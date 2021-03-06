package xivvic.roost.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import xivvic.event.Nullable;

/** 
 * Immutable Event value object
 * 
 * @author reid.dev
 *
 */
@AutoValue
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Event 
	implements DomainEntity
{
	public static final List<Event> EMPTY_LIST = Collections.<Event>emptyList();
	public static final String    PROP_ID = "event_id";
	public static final String  PROP_TYPE = "event_type";
	public static final String  PROP_TEXT = "event_text";
	public static final String  PROP_DATE = "event_date";
	public static final String  PROP_TIME = "event_time";

	/**
	 * Unique id for this event
	 * 
	 * @return
	 */
	@JsonProperty("id")
	public abstract String id();
	
	/**
	 * The date that the Event occurs. 
	 * 
	 * Note that this has no time concept, so if @{link time()} returns null for this Event,
	 * it is related to the date, but there is no specific time.
	 * 
	 * @return the event's date
	 */
	@Nullable
	@JsonProperty("date")
	public abstract LocalDate date();
	
	/**
	 * The time that the Event occurs
	 * 
	 * Note, that this has no date concept, so if {@link date()} returns null for this object,
	 * the time occurs every day.
	 * 
	 * @return the event's time
	 */
	@Nullable
	@JsonProperty("time")
	public abstract LocalTime time();

	/**
	 * What type of event this represents
	 * 
	 * @return the event's type
	 */
	@JsonProperty("type")
	public abstract EventType type();

	/**
	 * A description of or name for the event
	 * 
	 * @return the event's description or name
	 */
	@JsonProperty("text")
	public abstract String text();
	
	@JsonCreator
	public static Event create(String id, LocalDate date, LocalTime time, EventType type, String text)
	{
		return new AutoValue_Event(id, date, time, type, text);
	}

	public static Event createForTodayWithoutTimeComponent(String id, EventType type, String text)
	{
		LocalDate today = LocalDate.now();
		
		return new AutoValue_Event(id, today, null, type, text);
	}
}
