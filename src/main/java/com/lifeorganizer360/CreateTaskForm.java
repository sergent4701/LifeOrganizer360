package com.lifeorganizer360;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
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
public class CreateTaskForm extends ScrollPane {
	public static final String WORKSPACE = "WORKSPACE", RECEEDED = "RECEEDED", PROFILE = "PROFILE";

	protected CreateTaskForm(String type) {
		this(type, null);
	}

	protected CreateTaskForm(String type, TaskBase parent) {
		VBox v = new VBox(10);

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

		ColorPicker colorPicker = new ColorPicker();
		colorPicker.getCustomColors().addAll(Task.colors);
		colorPicker.setValue(Task.colors[0]);

		Label ticketsL = new Label("Tickets:");
		Button addTicket = new Button("Add");

		VBox ticketList = new VBox(50);

		ArrayList<HashMap> justAdded = new ArrayList<HashMap>();
		addTicket.setOnAction(new EventHandler() {
			public void handle(Event event) {
				HashMap data = CreateTaskForm.createTicketItem(justAdded);
				Pane p = (Pane) data.get("Return");
				ticketList.getChildren().add(p);
			}
		});

		HashMap data = CreateTaskForm.createTicketItem(justAdded);
		Pane p = (Pane) data.get("Return");
		ticketList.getChildren().add(p);

		VBox ticketContainer = new VBox(ticketsL, ticketList, addTicket);

		Button submitBtn = new Button("Submit");

		v.getChildren().add(backBtn);
		if (type.equals(WORKSPACE))
			v.getChildren().add(dropdown);
		if (!type.equals(RECEEDED) && !type.equals(PROFILE)) {
			v.getChildren().addAll(titleV, descV, apContainer, colorPicker, ticketContainer, submitBtn);
		} else {
			VBox searchPane = new VBox(5);
			TextField search = new TextField();
			searchPane.getChildren().add(search);

			HBox newAndSearch = new HBox(15);

			VBox newContainer = new VBox();

			newContainer.getChildren().addAll(titleV, descV, apContainer, colorPicker, ticketContainer, submitBtn);

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
			v.getChildren().add(newAndSearch);
		}

		dropdown.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			switch (newValue) {
			case "Goal":
				v.getChildren().removeAll(apContainer, colorPicker, ticketContainer);
				break;
			case "Task":
				v.getChildren().removeAll(submitBtn);
				v.getChildren().addAll(apContainer, colorPicker, ticketContainer, submitBtn);
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
										Double.parseDouble(penaltyF.getText()), e.getX(), e.getY(),
										colorPicker.getValue());
								for (HashMap n : justAdded) {
									saveTicket(n, (Task) x);
								}
								((Task) x).setHandleCompletionNotification(((Task) x).sendCompletionRequest());
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
							Double.parseDouble(penaltyF.getText()), colorPicker.getValue());
					for (HashMap n : justAdded) {
						saveTicket(n, (Task) x);
					}
					((Task) x).setHandleCompletionNotification(((Task) x).sendCompletionRequest());

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
		setContent(v);
	}

	public static Pane createTicketItem(WorkTicket t) {
		VBox ret = new VBox(3);
		Button delBtn = new Button("X");
		Label header = null;
		HBox row = null;

		if (t instanceof RecurringTicket) {
			header = new Label("Recurring Task");
			row = new HBox(new Label("Start: " + t.getStart()),
					new Label("End: " + ((RecurringTicket) t).getStopDate()), delBtn);
		} else if (t instanceof TrailingTicket) {
			header = new Label("Trailing Task");
			row = new HBox(new Label("Start: " + t.getStart()), new Label("End: " + t.getEnd()), delBtn);
		} else if (t.isInstance()) {
			header = new Label("Instance Task");
			row = new HBox(new Label("Reminder Time" + t.getStart()), delBtn);
		} else {
			header = new Label("Task");
			row = new HBox(new Label("Start: " + t.getStart()), new Label("End: " + t.getEnd()), delBtn);
		}

		ret.getChildren().addAll(header, row);
		delBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {
				t.delete();
				((Pane) ret.getParent()).getChildren().remove(ret);
			}
		});
		return ret;
	}

	public static HashMap createTicketItem(ArrayList<HashMap> list) {

		HashMap<String, Node> content = new HashMap<String, Node>();

		list.add(content);

		Button delBtn = new Button("X");
		Label infoL = new Label("Additional Info:");
		HBox temp = new HBox(3, infoL, delBtn);
		final TextArea infoF = new TextArea();
		content.put("InfoBox", infoF);
		VBox infoV = new VBox(3, temp, infoF);
		infoF.setMaxWidth(387);
		infoF.setMaxHeight(70);

		Label startL = new Label("Start:");
		final DateTimePicker startF = new DateTimePicker();
		startF.setFormat("MMM dd, yyyy hh:mm a");
		content.put("StartPicker", startF);
		VBox startV = new VBox(3, startL, startF);

		Label endL = new Label("End:");
		final DateTimePicker endF = new DateTimePicker();
		endF.setFormat("MMM dd, yyyy hh:mm a");
		content.put("EndPicker", endF);
		VBox endV = new VBox(3, endL, endF);

		Label stopDateL = new Label("Stop Date:");
		final DateTimePicker stopDateF = new DateTimePicker();
		stopDateF.setFormat("MMM dd, yyyy hh:mm a");
		content.put("StopDatePicker", stopDateF);
		VBox stopDateV = new VBox(3, stopDateL, stopDateF);

		HBox secontainer = new HBox(10, startV, endV);

		CheckBox instance = new CheckBox("Instance?");
		content.put("InstanceCheckBox", instance);

		CheckBox recurring = new CheckBox("Recurring?");
		content.put("RecurringCheckBox", recurring);

		CheckBox trailingCheckBox = new CheckBox("Trailing?");
		content.put("TrailingCheckBox", trailingCheckBox);

		instance.selectedProperty()
				.addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
					if (new_val) {
						secontainer.getChildren().removeAll(endV);
						startL.setText("Reminder Time:");

					} else {
						if (!secontainer.getChildren().contains(endV))
							secontainer.getChildren().addAll(endV);
						startL.setText("Start:");
					}
				});

		final ComboBox<String> dropdown = new ComboBox<String>();
		dropdown.setPromptText("Select Frequency");
		dropdown.getItems().add(RecurringTicket.DAILY);
		dropdown.getItems().add(RecurringTicket.EVERYOTHERDAY);
		dropdown.getItems().add(RecurringTicket.WEEKLY);
		content.put("Dropdown", dropdown);

		CheckBox[] days = new CheckBox[7];
		days[0] = new CheckBox("M");
		days[1] = new CheckBox("Tu");
		days[2] = new CheckBox("W");
		days[3] = new CheckBox("Th");
		days[4] = new CheckBox("F");
		days[5] = new CheckBox("Sat");
		days[6] = new CheckBox("Sun");

		for (int i = 0; i < days.length; i++) {
			content.put(i + "", days[i]);
		}

		HBox daysContainer = new HBox(3, days);

		VBox recurringcontainer = new VBox(10, recurring);

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
		VBox seSelection = new VBox(instance, secontainer);
		VBox ret = new VBox(5, infoV, seSelection, recurringcontainer, trailingCheckBox);

		trailingCheckBox.selectedProperty()
				.addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
					if (new_val) {
						ret.getChildren().removeAll(recurringcontainer);
					} else {
						if (!ret.getChildren().contains(recurringcontainer)) {
							ret.getChildren().removeAll(trailingCheckBox);
							ret.getChildren().addAll(recurringcontainer, trailingCheckBox);
						}
					}
				});
		delBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {
				((Pane) ret.getParent()).getChildren().remove(ret);
				list.remove(content);
			}
		});
		recurring.selectedProperty()
				.addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
					if (new_val) {
						if (!recurringcontainer.getChildren().contains(dropdown)) {
							recurringcontainer.getChildren().addAll(dropdown, stopDateV);
						}
						ret.getChildren().removeAll(trailingCheckBox);
					} else {
						recurringcontainer.getChildren().removeAll(dropdown, stopDateV);
						if (!ret.getChildren().contains(trailingCheckBox)) {
							ret.getChildren().addAll(trailingCheckBox);
						}
					}
				});
		content.put("Return", ret);
		return content;
	}

	public static void saveTicket(HashMap n, Task a) {
		WorkTicket t = null;

		String info = ((TextArea) n.get("InfoBox")).getText();

		boolean instance = ((CheckBox) n.get("InstanceCheckBox")).isSelected();

		LocalDateTime start = ((DateTimePicker) n.get("StartPicker")).getDateTimeValue();
		LocalDateTime end = ((DateTimePicker) n.get("EndPicker")).getDateTimeValue();
		if (((CheckBox) n.get("RecurringCheckBox")).isSelected()) {
			String type = (String) ((ComboBox) n.get("Dropdown")).getValue();
			LocalDateTime stopDate = ((DateTimePicker) n.get("StopDatePicker")).getDateTimeValue();
			if (type.equals(RecurringTicket.WEEKLY)) {
				boolean[] days = new boolean[7];
				for (int i = 0; i < 7; i++) {
					days[i] = ((CheckBox) n.get(i + "")).isSelected();
				}
				if (instance)
					t = new RecurringTicket(a, start, stopDate, type, days, info);
				else
					t = new RecurringTicket(a, start, end, stopDate, type, days, info);
			} else {
				if (instance)
					t = new RecurringTicket(a, start, stopDate, type, info);
				else
					t = new RecurringTicket(a, start, end, stopDate, type, info);
			}
		} else if (((CheckBox) n.get("TrailingCheckBox")).isSelected()) {
			if (instance)
				t = new TrailingTicket(a, start, info);
			else
				t = new TrailingTicket(a, start, end, info);
		} else {
			if (instance)
				t = new WorkTicket(a, start, info);
			else
				t = new WorkTicket(a, start, end, info);
		}
		a.addTicket(t);
		Main.addTicket(t);
		t.save();
	}

}
