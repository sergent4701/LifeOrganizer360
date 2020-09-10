package com.lifeorganizer360;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.*;
import java.util.ArrayList;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Transient;

@NodeEntity
public class RecurringTicket extends WorkTicket {

	@Transient
	public static final String DAILY = "Daily", EVERYOTHERDAY = "Every Other Day", WEEKLY = "Weekly";

	@Property
	private String type;

	@Property
	boolean[] days;

	@Property
	LocalDateTime stopDate;

	@Relationship(type = "CREATES", direction = Relationship.OUTGOING)
	private ArrayList<WorkTicket> tickets = new ArrayList<WorkTicket>();

	protected RecurringTicket() {
		super();
	}

	protected RecurringTicket(Task task, LocalDateTime s, LocalDateTime e, LocalDateTime stopDate, String type,
			boolean[] days, String info) {
		super(task, s, e, info);
		this.stopDate = stopDate;
		this.type = type;
		this.days = days;

		for (int i = 0; i <= s.until(stopDate, ChronoUnit.DAYS) + 1; i++) {
			switch (type) {
			case DAILY:
				addTicket(new WorkTicket(null, s.plusDays(i), e.plusDays(i), info));
				break;
			case EVERYOTHERDAY:
				if (i % 2 == 0)
					addTicket(new WorkTicket(null, s.plusDays(i), e.plusDays(i), info));
				break;
			case WEEKLY:
				if (days[s.plusDays(i).getDayOfWeek().getValue() - 1])
					addTicket(new WorkTicket(null, s.plusDays(i), e.plusDays(i), info));
				break;
			}
		}
	}

	protected RecurringTicket(Task task, LocalDateTime s, LocalDateTime e, LocalDateTime stopDate, String type,
			String info) {
		this(task, s, e, stopDate, type, null, info);
	}

	protected RecurringTicket(Task task, LocalDateTime a, LocalDateTime stopDate, String type, boolean[] days,
			String info) {
		super(task, a, info);
		this.stopDate = stopDate;
		this.type = type;
		this.days = days;

		for (int i = 0; i <= a.until(stopDate, ChronoUnit.DAYS) + 1; i++) {
			switch (type) {
			case DAILY:
				addTicket(new WorkTicket(null, a.plusDays(i), info));
				break;
			case EVERYOTHERDAY:
				if (i % 2 == 0)
					addTicket(new WorkTicket(null, a.plusDays(i), info));
				break;
			case WEEKLY:
				if (days[a.plusDays(i).getDayOfWeek().getValue() - 1])
					addTicket(new WorkTicket(null, a.plusDays(i), info));
				break;
			}
		}

	}

	protected RecurringTicket(Task task, LocalDateTime a, LocalDateTime stopDate, String type, String info) {
		this(task, a, stopDate, type, null, info);
	}

	private void addTicket(WorkTicket t) {
		tickets.add(t);
		save();
	}

	public void delete() {
		super.delete();
		for (WorkTicket t : tickets) {
			t.delete();
		}
	}
	public LocalDateTime getStopDate() {
		return stopDate;
	}

	public ArrayList<WorkTicket> getTickets() {
		return tickets;
	}

}
