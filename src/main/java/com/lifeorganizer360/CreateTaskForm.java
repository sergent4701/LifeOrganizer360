package com.lifeorganizer360;

import java.time.LocalDateTime;
import java.util.ArrayList;

import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import tornadofx.control.DateTimePicker;
import javafx.scene.Parent;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class CreateTaskForm extends VBox {
	public static final String WORKSPACE = "WORKSPACE", RECEEDED = "RECEEDED", PROFILE = "PROFILE";

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

		Label ticketsL = new Label("Tickets:");
		Button addTicket = new Button("Add");

		VBox ticketList = new VBox(5);
		ticketList.getChildren().add(createTicketItem());

		addTicket.setOnAction(new EventHandler() {
			public void handle(Event event) {
				ticketList.getChildren().add(createTicketItem());
			}
		});

		VBox ticketContainer = new VBox(ticketsL, ticketList, addTicket);

		Button submitBtn = new Button("Submit");

		this.getChildren().add(backBtn);
		if (type.equals(WORKSPACE))
			this.getChildren().add(dropdown);
		if (!type.equals(RECEEDED) && !type.equals(PROFILE)) {
			this.getChildren().addAll(titleV, descV, apContainer, ticketContainer, submitBtn);
		} else {
			VBox searchPane = new VBox(5);
			TextField search = new TextField();
			searchPane.getChildren().add(search);

			HBox newAndSearch = new HBox(15);

			VBox newContainer = new VBox();

			newContainer.getChildren().addAll(titleV, descV, apContainer, ticketContainer, submitBtn);

			ScrollPane searchScroll = new ScrollPane();
			searchScroll.setContent(searchPane);
			searchScroll.setFitToWidth(true);

			ArrayList<HBox> tasks = new ArrayList<HBox>();
			for (TaskBase e : Main.getEntities()) {
				if (e instanceof Task) {
					Task t = (Task) e;
					HBox temp = new HBox();

					VBox[] cols = new VBox[2];
					cols[0] = new VBox();
					cols[1] = new VBox();

					Label title = new Label(t.getTitle());
					Label description = new Label(t.getDescription());
					Label award = new Label(t.getAward() + "");
					Label penalty = new Label(t.getPenalty() + "");

					cols[0].getChildren().addAll(title, description);
					cols[1].getChildren().addAll(award, penalty);

					temp.getChildren().addAll(cols[0], cols[1]);

					Main.setBackgroundColor(Color.GRAY, temp);
					temp.setPrefWidth(600);

					temp.setOnMouseClicked(new EventHandler() {
						public void handle(Event event) {
							parent.dependsOn(t);

							Line dependency = new Line();

							dependency.setStrokeWidth(3);
							dependency.setStroke(Color.BLACK);
							parent.addStartLine(dependency);
							t.addEndLine(dependency);
							if (type.equals(PROFILE)) {
								Main.getPrimaryStage().getScene().setRoot(parent.getProfilePane());
								if (!parent.isReceeded()) {
									Main.getWorkspace().getChildren().addAll(dependency);
								}
							} else
								Main.getPrimaryStage().getScene().setRoot(Main.getWorkspaceContainer());

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
				this.getChildren().removeAll(apContainer, ticketContainer);
				break;
			case "Task":
				this.getChildren().removeAll(submitBtn);
				this.getChildren().addAll(apContainer, ticketContainer, submitBtn);
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
										Double.parseDouble(penaltyF.getText()), e.getX(), e.getY());
								for (Node n : ticketList.getChildren()) {
									WorkTicket t = null;
									VBox v = (VBox) n;
									LocalDateTime start = ((DateTimePicker) ((VBox) ((HBox) v.getChildren().get(0))
											.getChildren().get(0)).getChildren().get(1)).getDateTimeValue();
									LocalDateTime end = ((DateTimePicker) ((VBox) ((HBox) v.getChildren().get(0))
											.getChildren().get(1)).getChildren().get(1)).getDateTimeValue();
									if (((CheckBox) ((VBox) v.getChildren().get(1)).getChildren().get(0))
											.isSelected()) {
										LocalDateTime stopDate = ((DateTimePicker) ((VBox) ((VBox) v.getChildren()
												.get(1)).getChildren().get(2)).getChildren().get(1)).getDateTimeValue();
										String type = (String) ((ComboBox) ((VBox) v.getChildren().get(1)).getChildren()
												.get(1)).getValue();
										if (type.equals(RecurringTicket.WEEKLY)) {
											HBox daysContainer = (HBox) ((VBox) v.getChildren().get(1)).getChildren()
													.get(3);
											boolean[] days = new boolean[7];
											for (int i = 0; i < 7; i++) {
												days[i] = ((CheckBox) daysContainer.getChildren().get(i)).isSelected();
											}
											t = new RecurringTicket((Task) x, start, end, stopDate, type, days);
										} else
											t = new RecurringTicket((Task) x, start, end, stopDate, type);
									} else
										t = new WorkTicket((Task) x, start, end);
									((Task) x).addTicket(t);
									Main.addTicket(t);
									t.save();
								}
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
							Double.parseDouble(penaltyF.getText()));
					for (Node n : ticketList.getChildren()) {
						WorkTicket t = null;
						VBox v = (VBox) n;
						LocalDateTime start = ((DateTimePicker) ((VBox) ((HBox) v.getChildren().get(0)).getChildren()
								.get(0)).getChildren().get(1)).getDateTimeValue();
						LocalDateTime end = ((DateTimePicker) ((VBox) ((HBox) v.getChildren().get(0)).getChildren()
								.get(1)).getChildren().get(1)).getDateTimeValue();
						if (((CheckBox) ((VBox) v.getChildren().get(1)).getChildren().get(0)).isSelected()) {
							String type = (String) ((ComboBox) ((VBox) v.getChildren().get(1)).getChildren().get(1))
									.getValue();
							LocalDateTime stopDate = ((DateTimePicker) ((VBox) ((VBox) v.getChildren().get(1))
									.getChildren().get(2)).getChildren().get(1)).getDateTimeValue();
							if (type.equals(RecurringTicket.WEEKLY)) {
								HBox daysContainer = (HBox) ((VBox) v.getChildren().get(1)).getChildren().get(3);
								boolean[] days = new boolean[7];
								for (int i = 0; i < 7; i++) {
									days[i] = ((CheckBox) daysContainer.getChildren().get(i)).isSelected();
								}
								t = new RecurringTicket((Task) x, start, end, stopDate, type, days);
							} else
								t = new RecurringTicket((Task) x, start, end, stopDate, type);
						} else
							t = new WorkTicket((Task) x, start, end);
						((Task) x).addTicket(t);
						Main.addTicket(t);
						t.save();
					}

					Main.getEntities().add(x);

					parent.dependsOn(x);

					Line dependency = new Line();

					dependency.setStrokeWidth(3);
					dependency.setStroke(Color.BLACK);
					parent.addStartLine(dependency);
					x.addEndLine(dependency);

					if (type.equals(PROFILE) && !parent.isReceeded())
						Main.getWorkspace().getChildren().addAll(x.getWorkspacePane(), dependency);
					x.setHidden(false);

				}
				if (!type.equals(PROFILE))
					Main.getPrimaryStage().getScene().setRoot(Main.getWorkspaceContainer());
				else
					Main.getPrimaryStage().getScene().setRoot(parent.getProfilePane());
			}
		});
	}

	public static Pane createTicketItem(WorkTicket t) {
		VBox ret = new VBox(3);
		Label header = null;
		if (t instanceof RecurringTicket) {
			header = new Label("Recurring Task");
		} else
			header = new Label("Task");
		Button delBtn = new Button("X");
		HBox row = new HBox(new Label("Start: " + t.getStart()), new Label("End: " + t.getEnd()), delBtn);

		ret.getChildren().addAll(header, row);
		delBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {
				t.delete();
				((Pane) ret.getParent()).getChildren().remove(ret);
			}
		});
		return ret;
	}

	public static Pane createTicketItem() {

		Label startL = new Label("Start:");
		final DateTimePicker startF = new DateTimePicker();
		startF.setFormat("MMM dd, yyyy hh:mm a");
		VBox startV = new VBox(3, startL, startF);

		Label endL = new Label("End:");
		final DateTimePicker endF = new DateTimePicker();
		endF.setFormat("MMM dd, yyyy hh:mm a");
		VBox endV = new VBox(3, endL, endF);

		Label stopDateL = new Label("Stop Date:");
		final DateTimePicker stopDateF = new DateTimePicker();
		stopDateF.setFormat("MMM dd, yyyy hh:mm a");
		VBox stopDateV = new VBox(3, stopDateL, stopDateF);

		Button delBtn = new Button("X");

		HBox secontainer = new HBox(10, startV, endV, delBtn);
		CheckBox recurring = new CheckBox("Recurring?");

		final ComboBox<String> dropdown = new ComboBox<String>();
		dropdown.setPromptText("Select Frequency");
		dropdown.getItems().add(RecurringTicket.DAILY);
		dropdown.getItems().add(RecurringTicket.EVERYOTHERDAY);
		dropdown.getItems().add(RecurringTicket.WEEKLY);

		CheckBox[] days = new CheckBox[7];
		days[0] = new CheckBox("M");
		days[1] = new CheckBox("Tu");
		days[2] = new CheckBox("W");
		days[3] = new CheckBox("Th");
		days[4] = new CheckBox("F");
		days[5] = new CheckBox("Sat");
		days[6] = new CheckBox("Sun");

		HBox daysContainer = new HBox(3, days);

		VBox recurringcontainer = new VBox(10, recurring);
		recurring.selectedProperty()
				.addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
					if (new_val) {
						if (!recurringcontainer.getChildren().contains(dropdown)) {
							recurringcontainer.getChildren().addAll(dropdown, stopDateV);
						}
					} else {
						recurringcontainer.getChildren().removeAll(dropdown, stopDateV);
					}
				});
		dropdown.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			switch (newValue) {
			case RecurringTicket.DAILY:
			case RecurringTicket.EVERYOTHERDAY:
				recurringcontainer.getChildren().remove(daysContainer);
				break;
			case RecurringTicket.WEEKLY:
				recurringcontainer.getChildren().add(daysContainer);
				break;
			}
		});
		VBox ret = new VBox(secontainer, recurringcontainer);

		delBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {
				((Pane) ret.getParent()).getChildren().remove(ret);
			}
		});
		return ret;

	}
}
