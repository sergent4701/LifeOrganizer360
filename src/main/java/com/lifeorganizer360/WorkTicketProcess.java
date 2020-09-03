package com.lifeorganizer360;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class WorkTicketProcess extends Notification {
	@Relationship(type = "BELONGS_TO", direction = Relationship.OUTGOING)
	private WorkTicket ticket;

	@Relationship(type = "PARENT_TAST", direction = Relationship.OUTGOING)
	private Task task;

	protected WorkTicketProcess() {

	}

	protected WorkTicketProcess(WorkTicket t) {
		this(t, t.getTask());
	}

	protected WorkTicketProcess(WorkTicket t, Task task) {
		super("Work Ticket: " + task.getTitle() + " requires action!", "Please complete this survey to recieve award($"
				+ task.getAward() + "), penalty($" + task.getPenalty() + "), or extension.");
		ticket = t;
		this.task = task;
		save();
	}

	public void doOnClick() {
		Main.getPrimaryStage().getScene().setRoot(new WorkTicketSurvey(this));
	}

	public Task getTask() {
		return task;
	}

	public WorkTicket getTicket() {
		return ticket;
	}
}
