package com.lifeorganizer360;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class WorkTicketStart extends Notification {
	@Relationship(type = "BELONGS_TO", direction = Relationship.OUTGOING)
	private WorkTicket ticket;

	@Relationship(type = "PARENT_TAST", direction = Relationship.OUTGOING)
	private Task task;

	protected WorkTicketStart() {

	}

	protected WorkTicketStart(WorkTicket t) {
		this(t, t.getTask());
	}

	protected WorkTicketStart(WorkTicket t, Task task) {
		super("Work Ticket: " + task.getTitle() + " has begun!", "Click on this survey to acknowledge this!");
		ticket = t;
		this.task = task;
		save();
	}

	public Task getTask() {
		return task;
	}

	public WorkTicket getTicket() {
		return ticket;
	}
}
