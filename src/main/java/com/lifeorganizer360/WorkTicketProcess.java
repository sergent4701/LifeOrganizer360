package com.lifeorganizer360;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class WorkTicketProcess extends Notification {

	@Relationship(type = "PARENT_TICKET", direction = Relationship.OUTGOING)
	private WorkTicket ticket;

	@Relationship(type = "PARENT_TASK", direction = Relationship.OUTGOING)
	private Task task;

	protected WorkTicketProcess() {

	}

	protected WorkTicketProcess(WorkTicket t) {
		this(t, t.getTask());
	}

	protected WorkTicketProcess(WorkTicket t, Task task) {
		super((t.isInstance() ? "Instance Reminder: " + task.getTitle() + "!"
				: "Work Ticket: " + task.getTitle() + " requires action!"),
				(t.isInstance() ? "Have you fullfilled this reminder?"
						: "Please complete this survey to recieve award($" + task.getAward() + "), penalty($"
								+ task.getPenalty() + "), or extension."));

		this.ticket = t;
		this.task = task;
		save();
	}

	public void doOnClick() {
		Main.getPrimaryStage().getScene().setRoot(new WorkTicketSurvey(this));
	}

	public WorkTicket getTicket() {
		return ticket;
	}

	public Task getTask() {
		return task;
	}
}
