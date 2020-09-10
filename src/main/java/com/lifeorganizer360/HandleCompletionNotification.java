package com.lifeorganizer360;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class HandleCompletionNotification extends Notification {

	@Relationship(type = "PARENT_TASK", direction = Relationship.OUTGOING)
	private Task task;

	protected HandleCompletionNotification() {

	}

	protected HandleCompletionNotification(Task t) {
		super("Is this task complete? : " + t.getTitle(),
				"Either add more tickets to the task or complete this task to remove this notification.");
		this.task = t;
		save();
	}

	public void doOnClick() {
		Main.getPrimaryStage().getScene().setRoot(task.getProfilePane());
	}

	public Task getTask() {
		return task;
	}
}
