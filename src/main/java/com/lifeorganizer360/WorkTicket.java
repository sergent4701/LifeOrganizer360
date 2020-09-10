package com.lifeorganizer360;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Transient;

import javafx.scene.paint.Color;

@NodeEntity
public class WorkTicket extends Saveable {

	@Transient
	public static final String INPROGRESS = "In-progress", REQUIRESACTION = "Requires Action", AWARD = "Award",
			PENALIZE = "Penalize", EXTEND = "Extend", REPLACED = "Replaced";

	@Property
	private String status = INPROGRESS;

	@Property
	private String additionalInfo;

	@Property
	private double award, penalty;

	@Property
	private LocalDateTime start, end;

	@Property
	private int difficulty, focus, anxiety, frustration, workPace;

	@Property
	private double adjustedAward, adjustedPenalty;

	@Property
	private LocalDateTime realStart, realEnd;

	@Property
	private boolean busyWork, extendable, instance;

	@Relationship(type = "EXTENDS_TO", direction = Relationship.OUTGOING)
	private WorkTicket extension;

	@Relationship(type = "BELONGS_TO", direction = Relationship.OUTGOING)
	private Task task;

	@Relationship(type = "NOTIFYS_START", direction = Relationship.OUTGOING)
	private Notification startNotification;

	@Relationship(type = "NOTIFYS_END", direction = Relationship.OUTGOING)
	private Notification endNotification;

	@Relationship(type = "NOTIFYS_AP", direction = Relationship.OUTGOING)
	private Notification apNotification;

	protected WorkTicket() {
	}

	protected WorkTicket(Task t, LocalDateTime a, String additionalInfo) {
		this(t, a, a, additionalInfo);
		this.instance = true;
	}

	protected WorkTicket(Task t, LocalDateTime s, LocalDateTime e, String additionalInfo) {
		this.start = s;
		this.end = e;
		this.task = t;
		this.instance = false;
		this.additionalInfo = additionalInfo;
	}

	public LocalDateTime getStart() {
		return start;
	}

	public LocalDateTime getEnd() {
		return end;
	}

	public void setStart(LocalDateTime s) {
		start = s;
		save();
	}

	public void setEnd(LocalDateTime e) {
		end = e;
		save();
	}

	public Task getTask() {
		return task;
	}

	public void setStatus(String s, Task t) {
		if (status.equals(REQUIRESACTION) && !s.equals(REQUIRESACTION)) {
			t.setHandleCompletionNotification(t.sendCompletionRequest());
		}

		status = s;
		save();
	}

	public String getStatus() {
		return status;
	}

	public void delete() {
		if (task != null)
			task.deleteTicket(this);
		Main.deleteTicket(this);
		if (startNotification != null)
			startNotification.delete();
		if (endNotification != null)
			endNotification.delete();
		if (apNotification != null)
			apNotification.delete();
		super.delete();
	}

	public long getDuration() {
		return start.until(end, ChronoUnit.MINUTES);
	}

	public boolean isExtendable() {
		return extendable;
	}

	public void generateStartNotification() {

	}

	public void surveryUpdate(double adjustedAward, double adjustedPenalty, int difficulty, int focus, int anxiety,
			int frustration, int workPace, boolean busyWork, LocalDateTime adjustedStart, LocalDateTime adjustedEnd) {
		this.adjustedAward = adjustedAward;
		this.adjustedPenalty = adjustedPenalty;
		this.difficulty = difficulty;
		this.focus = focus;
		this.anxiety = anxiety;
		this.frustration = frustration;
		this.workPace = workPace;
		this.busyWork = busyWork;
		this.realStart = adjustedStart;
		this.realEnd = adjustedEnd;
	}

	public boolean isInstance() {
		return instance;
	}

	public void setStartNotification() {
		setStartNotification(this.getTask());
	}

	public void setStartNotification(Task t) {
		if (startNotification == null)
			startNotification = new WorkTicketStart(this, t);
		save();
	}

	public void setEndNotification() {
		setEndNotification(this.getTask());
	}

	public void setEndNotification(Task t) {
		if (endNotification == null)
			endNotification = new WorkTicketProcess(this, t);
		save();
	}

	public void setAPNotification(double a) {
		if (apNotification == null)
			apNotification = new AwardPenaltyNotification(a);
		save();
	}

	public String getInfo() {
		return additionalInfo;
	}
}
