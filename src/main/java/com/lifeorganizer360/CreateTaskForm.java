package com.lifeorganizer360;

import java.util.ArrayList;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import tornadofx.control.DateTimePicker;
import javafx.scene.Parent;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class CreateTaskForm extends VBox {
	public static final String WORKSPACE = "WORKSPACE", RECEEDED = "RECEEDED",PROFILE="PROFILE";

	protected CreateTaskForm(String type) {
		this(type, null);
	}

	protected CreateTaskForm(String type, TaskBase parent) {
		VBox v = this;

		this.setSpacing(10);

		Button backBtn = new Button("<--");

		final ComboBox<String> dropdown = new ComboBox<String>();
		dropdown.setValue("Task");

		if (type.equals(WORKSPACE)) {
			dropdown.setPromptText("Select Type");
			dropdown.getItems().add("Goal");
			dropdown.getItems().add("Task");
		}
		Label titleL = new Label("Title:");
		final TextField titleF = new TextField();
		VBox titleV = new VBox(3, titleL, titleF);
		titleF.setMaxWidth(387);

		Label descL = new Label("Description:");
		final TextArea descF = new TextArea();
		VBox descV = new VBox(3, descL, descF);
		descF.setMaxWidth(387);

		Label awardL = new Label("Award:");
		final TextField awardF = new TextField();
		VBox awardV = new VBox(3, awardL, awardF);
		awardF.setMinWidth(188);

		Label penaltyL = new Label("Penalty:");
		final TextField penaltyF = new TextField();
		VBox penaltyV = new VBox(3, penaltyL, penaltyF);
		penaltyF.setMinWidth(188);

		HBox apContainer = new HBox(10, awardV, penaltyV);

		Label startL = new Label("Start:");
		final DateTimePicker startF = new DateTimePicker();
		startF.setFormat("MMM dd, yyyy hh:mm a");
		VBox startV = new VBox(3, startL, startF);

		Label endL = new Label("End:");
		final DateTimePicker endF = new DateTimePicker();
		endF.setFormat("MMM dd, yyyy hh:mm a");
		VBox endV = new VBox(3, endL, endF);

		HBox seContainer = new HBox(10, startV, endV);

		Button submitBtn = new Button("Submit");

		this.getChildren().add(backBtn);
		if (type.equals(WORKSPACE))
			this.getChildren().add(dropdown);
		if (!type.equals(RECEEDED)&&!type.equals(PROFILE)) {
			this.getChildren().addAll(titleV, descV, apContainer, seContainer, submitBtn);
		} else {
			VBox searchPane = new VBox(5);
			TextField search = new TextField();
			searchPane.getChildren().add(search);

			HBox newAndSearch = new HBox(15);

			VBox newContainer = new VBox();
			newContainer.getChildren().addAll(titleV, descV, apContainer, seContainer, submitBtn);

			ScrollPane searchScroll = new ScrollPane();
			searchScroll.setContent(searchPane);
			searchScroll.setFitToWidth(true);

			ArrayList<HBox> tasks = new ArrayList<HBox>();
			for (TaskBase e : Main.getEntities()) {
				if (e instanceof Task) {
					Task t = (Task) e;
					HBox temp = new HBox();

					VBox[] cols = new VBox[3];
					cols[0] = new VBox();
					cols[1] = new VBox();
					cols[2] = new VBox();

					Label title = new Label(t.getTitle());
					Label description = new Label(t.getDescription());
					Label award = new Label(t.getAward() + "");
					Label penalty = new Label(t.getPenalty() + "");
					Label start = new Label();
					Label end = new Label();

					if (t.getStart() != null)
						start = new Label(t.getStart().toString());

					if (t.getEnd() != null)
						end = new Label(t.getEnd().toString());

					cols[0].getChildren().addAll(title, description);
					cols[1].getChildren().addAll(award, penalty);
					cols[2].getChildren().addAll(start, end);

					temp.getChildren().addAll(cols[0], cols[1], cols[2]);

					Main.setBackgroundColor(Color.GRAY, temp);
					temp.setPrefWidth(600);

					temp.setOnMouseClicked(new EventHandler() {
						public void handle(Event event) {
							Main.getPrimaryStage().getScene().setRoot(Main.getWorkspaceContainer());
							parent.dependsOn(t);

							Line dependency = new Line();

							dependency.setStrokeWidth(3);
							dependency.setStroke(Color.BLACK);
							parent.addStartLine(dependency);
							t.addEndLine(dependency);
						}
					});
					temp.setCursor(Cursor.HAND);
					searchPane.getChildren().add(temp);
					tasks.add(temp);
				}
			}

			search.textProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue.equals("")) {
					searchPane.getChildren().clear();
					searchPane.getChildren().add(search);
					searchPane.getChildren().addAll(tasks);
				} else {
					for (HBox r : tasks) {
						if (((Label) ((VBox) r.getChildren().get(0)).getChildren().get(0)).getText().toLowerCase()
								.contains(newValue.toLowerCase())
								|| ((Label) ((VBox) r.getChildren().get(0)).getChildren().get(1)).getText()
										.toLowerCase().contains(newValue.toLowerCase())) {
							if (!searchPane.getChildren().contains(r)) {
								searchPane.getChildren().add(r);
							}
						} else {
							searchPane.getChildren().remove(r);
						}
					}
				}
			});
			newAndSearch.getChildren().addAll(newContainer, searchScroll);
			this.getChildren().add(newAndSearch);
		}

		dropdown.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			switch (newValue) {
			case "Goal":
				this.getChildren().removeAll(apContainer, seContainer);
				break;
			case "Task":
				this.getChildren().removeAll(submitBtn);
				this.getChildren().addAll(apContainer, seContainer, submitBtn);
				break;
			}
		});

		backBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {
				Main.getPrimaryStage().getScene().setRoot(Main.getWorkspaceContainer());
			}
		});

		submitBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {
				Main.getPrimaryStage().getScene().setRoot(Main.getWorkspaceContainer());

				if (type.equals(WORKSPACE)) {
					Main.getWorkspace().setCursor(Cursor.CROSSHAIR);

					final EventHandler filter = new EventHandler<MouseEvent>() {
						public void handle(MouseEvent e) {
							Main.getWorkspace().setCursor(Cursor.DEFAULT);
							TaskBase x = null;
							switch (dropdown.getValue()) {
							case "Goal":
								x = new Goal(titleF.getText(), descF.getText(), e.getX(), e.getY());
								break;
							case "Task":
								x = new Task(titleF.getText(), descF.getText(), Double.parseDouble(awardF.getText()),
										Double.parseDouble(penaltyF.getText()), startF.getDateTimeValue(),
										endF.getDateTimeValue(), e.getX(), e.getY());
								break;
							}
							Main.getEntities().add(x);
							Main.getWorkspace().getChildren().add(x.getWorkspacePane());
							Main.getWorkspace().removeEventFilter(MouseEvent.MOUSE_CLICKED, this);

						}

					};

					Main.getWorkspace().addEventFilter(MouseEvent.MOUSE_CLICKED, filter);
				} else {
					Task x = new Task(titleF.getText(), descF.getText(), Double.parseDouble(awardF.getText()),
							Double.parseDouble(penaltyF.getText()), startF.getDateTimeValue(), endF.getDateTimeValue());
					Main.getEntities().add(x);

					parent.dependsOn(x);

					Line dependency = new Line();

					dependency.setStrokeWidth(3);
					dependency.setStroke(Color.BLACK);
					parent.addStartLine(dependency);
					x.addEndLine(dependency);
				}
			}
		});
	}
}
