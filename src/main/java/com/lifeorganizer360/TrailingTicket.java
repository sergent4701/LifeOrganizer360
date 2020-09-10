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
public class TrailingTicket extends WorkTicket {

	@Relationship(type = "NEXT_TRAIL", direction = Relationship.OUTGOING)
	private TrailingTicket next;

	protected TrailingTicket() {
		super();
	}

	protected TrailingTicket(Task task, LocalDateTime s, LocalDateTime e, String info) {
		super(task, s, e, info);

	}

	protected TrailingTicket(Task task, LocalDateTime a, String info) {
		super(task, a, info);
	}

	protected TrailingTicket(TrailingTicket t, LocalDateTime a) {
		super(t.getTask(), a, t.getInfo());
		t.setNext(this);
	}

	protected TrailingTicket(TrailingTicket t, LocalDateTime s, LocalDateTime e) {
		super(t.getTask(), s, e, t.getInfo());
		t.setNext(this);
	}

	public void setNext(TrailingTicket t) {
		next = t;
		save();
	}

	public TrailingTicket getNext() {
		return next;
	}
}
