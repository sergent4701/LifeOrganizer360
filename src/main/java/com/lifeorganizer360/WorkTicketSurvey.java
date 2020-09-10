package com.lifeorganizer360;

import java.time.LocalDateTime;

import org.neo4j.ogm.annotation.Property;

import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import tornadofx.control.DateTimePicker;

@SuppressWarnings("unchecked")
public class WorkTicketSurvey extends VBox {
	protected WorkTicketSurvey(WorkTicketProcess w) {

//		 extendable;

		Button backBtn = new Button("<");

		backBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {
				Main.getPrimaryStage().getScene().setRoot(new NotificationDashboard());
			}
		});
		Label difficultyL = new Label("Difficulty:");
		Label focusL = new Label("Focus:");
		Label anxietyL = new Label("Anxiety:");
		Label frustrationyL = new Label("Frustration:");
		Label workPaceL = new Label("Work Pace:");
		Label busyWorkL = new Label("Busy Work:");

		Label adjustedAwardL = new Label("Adjusted Award:");
		Label adjustedPenaltyL = new Label("Adjusted Penalty:");

		Label adjustedStartL = new Label("Adjusted Start:");
		Label adjustedEndL = new Label("Adjusted End:");

		TextField difficultyF = new TextField();
		difficultyF.setMaxWidth(25);
		TextField focusF = new TextField();
		focusF.setMaxWidth(25);
		TextField anxietyF = new TextField();
		anxietyF.setMaxWidth(25);
		TextField frustrationF = new TextField();
		frustrationF.setMaxWidth(25);
		TextField workPaceF = new TextField();
		workPaceF.setMaxWidth(25);
		CheckBox busyWorkF = new CheckBox();
		busyWorkF.setMaxWidth(25);

		TextField adjustedAwardF = new TextField(w.getTask().getAward() + "");
		adjustedAwardF.setMaxWidth(50);
		TextField adjustedPenaltyF = new TextField(w.getTask().getPenalty() + "");
		adjustedPenaltyF.setMaxWidth(50);

		DateTimePicker adjustedStartF = new DateTimePicker();
		adjustedAwardF.setMaxWidth(150);
		adjustedStartF.setDateTimeValue(w.getTicket().getStart());
		DateTimePicker adjustedEndF = new DateTimePicker();
		adjustedEndF.setMaxWidth(150);
		adjustedEndF.setDateTimeValue(w.getTicket().getEnd());

		HBox difficultyC = new HBox(5, difficultyL, difficultyF);
		HBox focusC = new HBox(5, focusL, focusF);
		HBox anxietyC = new HBox(5, anxietyL, anxietyF);
		HBox frustrationyC = new HBox(5, frustrationyL, frustrationF);
		HBox workPaceC = new HBox(5, workPaceL, workPaceF);
		HBox busyWorkC = new HBox(5, busyWorkL, busyWorkF);

		HBox adjustedAwardC = new HBox(5, adjustedAwardL, adjustedAwardF);
		HBox penaltyAwardC = new HBox(5, adjustedPenaltyL, adjustedPenaltyF);

		HBox adjustedStartC = new HBox(5, adjustedStartL, adjustedStartF);
		HBox adjustedEndC = new HBox(5, adjustedEndL, adjustedEndF);

		final ComboBox<String> dropdown = new ComboBox<String>();
		dropdown.setPromptText("Select Action");
		dropdown.getItems().add(WorkTicket.AWARD);
		dropdown.getItems().add(WorkTicket.PENALIZE);
		if (w.getTicket().isExtendable())
			dropdown.getItems().add(WorkTicket.EXTEND);

		CheckBox trailingCheckBox = new CheckBox("Trailing?");

		Label trailingStartL = new Label("Reminder Date:");
		DateTimePicker trailingStartF = new DateTimePicker();
		trailingStartF.setFormat("MMM dd, yyyy hh:mm a");
		VBox startV = new VBox(3, trailingStartL, trailingStartF);

		Label trailingEndL = new Label("End:");
		DateTimePicker trailingEndF = new DateTimePicker();
		trailingEndF.setFormat("MMM dd, yyyy hh:mm a");
		VBox endV = new VBox(3, trailingEndL, trailingEndF);

		Button submitBtn = new Button("Submit");
		submitBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {
				w.getPane().setCursor(Cursor.DEFAULT);
				Main.setBackgroundColor(Color.GRAY, w.getPane());
				w.getTicket().surveryUpdate(Double.parseDouble(adjustedAwardF.getText()),
						Double.parseDouble(adjustedPenaltyF.getText()), Integer.parseInt(difficultyF.getText()),
						Integer.parseInt(focusF.getText()), Integer.parseInt(anxietyF.getText()),
						Integer.parseInt(frustrationF.getText()), Integer.parseInt(workPaceF.getText()),
						busyWorkF.isSelected(), adjustedStartF.getDateTimeValue(), adjustedEndF.getDateTimeValue());
				w.setAlert(false);
				w.getTicket().setStatus(dropdown.getValue(), w.getTask());
				switch (dropdown.getValue()) {
				case WorkTicket.AWARD:
					Main.getUser().award(w.getTask().getAward(), w.getTicket());
					break;
				case WorkTicket.PENALIZE:
					Main.getUser().penalize(w.getTask().getPenalty(), w.getTicket());
					break;
				case WorkTicket.EXTEND:
					break;
				}
				TrailingTicket t = null;

				if (w.getTicket() instanceof TrailingTicket && trailingCheckBox.isSelected()) {
					if (w.getTicket().isInstance())
						t = new TrailingTicket((TrailingTicket) w.getTicket(), trailingStartF.getDateTimeValue());
					else {
						t = new TrailingTicket((TrailingTicket) w.getTicket(), trailingStartF.getDateTimeValue(),
								trailingEndF.getDateTimeValue());
					}
					w.getTask().addTicket(t);
					Main.addTicket(t);
					t.save();
				}
				Main.getPrimaryStage().getScene().setRoot(new NotificationDashboard());
			}
		});

		HBox trailingDates = new HBox(5, startV);
		if (!w.getTicket().isInstance()) {
			trailingDates.getChildren().add(endV);
			trailingStartL.setText("Start:");
		}

		HBox trailingContainer = new HBox(5, trailingCheckBox);

		trailingCheckBox.selectedProperty()
				.addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
					if (new_val) {
						if (!trailingContainer.getChildren().contains(trailingDates)) {
							trailingContainer.getChildren().addAll(trailingDates);
						}
					} else {
						trailingContainer.getChildren().removeAll(trailingDates);
					}
				});

		VBox[] cols = new VBox[2];
		cols[0] = new VBox(difficultyC, focusC, anxietyC, adjustedAwardC, adjustedStartC, dropdown, submitBtn);
		cols[1] = new VBox(frustrationyC, workPaceC, busyWorkC, penaltyAwardC, adjustedEndC);

		if (w.getTicket() instanceof TrailingTicket)
			cols[1].getChildren().add(trailingContainer);

		HBox hor = new HBox(5, cols[0], cols[1]);

		getChildren().addAll(backBtn, hor);
		setAlignment(Pos.BASELINE_RIGHT);

	}
}
