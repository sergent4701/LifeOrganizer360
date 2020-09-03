package com.lifeorganizer360;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Transient;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

@SuppressWarnings({ "rawtypes", "unchecked" })
@NodeEntity
public class Notification extends Saveable implements Comparable {

	@Property
	private LocalDateTime dateCreated = LocalDateTime.now();

	@Property
	private String heading, message;

	@Property
	private boolean alert = true;

	@Transient
	private Pane pane;

	protected Notification() {

	}

	protected Notification(String h, String m) {
		heading = h;
		message = m;
		Main.addNotification(this);
		save();
	}

	public void setAlert(boolean b) {
		if(!b)
			Main.subAlert();
		alert = b;
		save();
	}

	public int compareTo(Object o) {
		Notification n = null;
		if (o instanceof Notification)
			n = (Notification) o;
		return n.getDateCreated().compareTo(dateCreated);
	}

	public LocalDateTime getDateCreated() {
		return dateCreated;
	}

	public String getHeading() {
		return heading;
	}

	public String getMessage() {
		return message;
	}

	public boolean isAlert() {
		return alert;
	}

	public Pane getPane() {
		if (pane == null) {
			Label heading = new Label(this.heading);
			Label message = new Label(this.message);
			Label date = new Label(dateCreated.toString());

			VBox ret = new VBox(3, heading, message, date);
			ret.setPrefHeight(100);
			ret.setPrefWidth(600);
			if (isAlert()) {
				ret.setCursor(Cursor.HAND);
				Main.setBackgroundColor(Color.ORANGE, ret);
				ret.setOnMouseClicked(new EventHandler() {
					public void handle(Event event) {
						doOnClick();
					}
				});
			} else {
				Main.setBackgroundColor(Color.GRAY, ret);
			}

			pane = ret;
		}
		return pane;

	}

	public void doOnClick() {
		setAlert(false);
		pane.setCursor(Cursor.DEFAULT);
		Main.setBackgroundColor(Color.GRAY, pane);
	}
}
