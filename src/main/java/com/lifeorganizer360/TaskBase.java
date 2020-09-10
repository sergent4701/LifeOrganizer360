package com.lifeorganizer360;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import tornadofx.control.DateTimePicker;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.HBox;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Transient;

@SuppressWarnings({ "restriction", "unchecked", "rawtypes" })
@NodeEntity
public abstract class TaskBase extends Saveable {

	@Property
	private double xPos, yPos, progress = -1;

	@Property
	private String title, description;

	@Property
	private boolean active = true, collapsed, receeded, hidden, complete;

	@Relationship(type = "DEPENDS_ON", direction = Relationship.OUTGOING)
	private ArrayList<Task> dependencies = new ArrayList<Task>();

	@Relationship(type = "DEPENDENCY_OF", direction = Relationship.OUTGOING)
	private ArrayList<TaskBase> dependenciesOf = new ArrayList<TaskBase>();

	@Transient
	private ArrayList<Line> startLines = new ArrayList<Line>(), endLines = new ArrayList<Line>();

	@Transient
	private ScrollPane receededList;

	@Transient
	private Pane workspacePane;

	@Transient
	private HBox profilePane;

	@Transient
	private Label titlePointer = new Label(), descriptionPointer = new Label();

	protected TaskBase() {

	}

	protected TaskBase(String title, String description, double xPos, double yPos) {
		this.title = title;
		this.description = description;
		this.xPos = xPos;
		this.yPos = yPos;
	}

	public void setTitlePointer(Label t) {
		titlePointer = t;
	}

	public void setDescriptionPointer(Label d) {
		descriptionPointer = d;
	}

	public ScrollPane getReceededList() {
		if (receededList == null) {
			ScrollPane ret = new ScrollPane();
			ret.setMaxWidth(150);
			ret.setMinHeight(50);
			ret.setMaxHeight(50);

			VBox dependencyList = new VBox();

			for (Task t : dependencies) {
				dependencyList.getChildren().add(t.generateReceededListItem());
			}

			ret.setContent(dependencyList);
			ret.setFitToWidth(true);
			receededList = ret;
		}
		return receededList;
	}

	public ArrayList<TaskBase> getDepedenciesOf() {
		return dependenciesOf;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean b) {
		complete = b;
	}

	public boolean isCollapsed() {
		return collapsed;
	}

	public void setCollapsed(boolean c) {
		collapsed = c;
		save();
	}

	public boolean isReceeded() {
		return receeded;
	}

	public void setHidden(boolean h) {
		if (h) {
			Main.getWorkspace().getChildren().remove(getWorkspacePane());
			for (Line l : startLines) {
				Main.getWorkspace().getChildren().remove(l);
			}
			for (Task t : dependencies) {
				boolean hide = true;
				for (TaskBase parent : t.getDepedenciesOf()) {
					if (!parent.isReceeded() && this != parent) {
						hide = false;
					}
				}
				if (hide) {
					t.setHidden(true);
				}
			}
		} else {
			if (!Main.getWorkspace().getChildren().contains(getWorkspacePane()))
				Main.getWorkspace().getChildren().add(getWorkspacePane());
			if (!receeded) {
				for (Line l : startLines) {
					if (!Main.getWorkspace().getChildren().contains(l))
						Main.getWorkspace().getChildren().add(l);
				}
				for (Task t : dependencies) {
					t.setHidden(false);
				}
			}
		}
		hidden = h;
		save();
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setReceeded(boolean r) {
		receeded = r;
		save();
	}

	public Pane getWorkspacePane() {
		return workspacePane;
	};

	public double getX() {
		return xPos;
	}

	public double getY() {
		return yPos;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public void setTitle(String t) {
		title = t;
		titlePointer.setText(t);
		save();
	}

	public void setDescription(String d) {
		description = d;
		descriptionPointer.setText(d);
		save();
	}

	public void setX(double x) {
		xPos = x;
		if (workspacePane != null)
			workspacePane.setLayoutX(getX());
		save();
	}

	public void setY(double y) {
		yPos = y;
		if (workspacePane != null)
			workspacePane.setLayoutY(getY());
		save();
	}

	public void dependsOn(Task t) {
		if (receededList != null)
			((VBox) receededList.getContent()).getChildren().add(t.generateReceededListItem());
		dependencies.add(t);
		t.getDepedenciesOf().add(this);
		save();
		t.save();
	}

	public void doesntDependOn(Task t) {
		dependencies.remove(t);
		t.getDepedenciesOf().remove(this);
		save();
		t.save();
	}

	public ArrayList<Task> getDependencies() {
		return dependencies;
	}

	public ArrayList<Line> getStartLines() {
		return startLines;
	}

	public ArrayList<Line> getEndLines() {
		return endLines;
	}

	public void setPane(Pane n) {
		workspacePane = n;
	}

	public void updateLines() {
		updateStartLines();
		updateEndLines();
	}

	private void updateEndLines() {
		for (Line a : endLines) {
			drawEndLine(a);
		}
	}

	public void drawStartLine(Line a) {
		a.startXProperty().bind(getWorkspacePane().widthProperty().divide(2).add(xPos));
		a.startYProperty().bind(getWorkspacePane().heightProperty().add(yPos).subtract(7.5));

	}

	public void addStartLine(Line a) {
		drawStartLine(a);
		startLines.add(a);
	}

	private void updateStartLines() {
		for (Line a : startLines) {
			drawStartLine(a);
		}
	}

	public void drawEndLine(Line a) {
		a.endXProperty().bind(getWorkspacePane().widthProperty().divide(2).add(xPos));
		a.setEndY(yPos + 7.5);
	}

	public void addEndLine(Line a) {
		drawEndLine(a);
		endLines.add(a);
	}

	public HBox getProfilePane() {
		TaskBase a = this;
		HBox ret = new HBox();
		ScrollPane scrollDependencies = new ScrollPane();
		ScrollPane profileScroll = new ScrollPane();

		VBox dependencyList = new VBox(3);
		scrollDependencies.setContent(dependencyList);
		for (Task x : dependencies) {
			dependencyList.getChildren().add(x.generateDropdownListItem(this));
		}
		Button addBtn = new Button("Add");
		addBtn.setPrefWidth(600);
		dependencyList.getChildren().add(addBtn);

		addBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {
				Main.getPrimaryStage().getScene().setRoot(new CreateTaskForm(CreateTaskForm.PROFILE, a));
			}
		});

		VBox taskForm = new VBox(10);
		profileScroll.setContent(taskForm);

		Button backBtn = new Button("<--");

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
		colorPicker.setValue(((Task) a).getColor());

		Label ticketsL = new Label("Tickets:");
		Button addTicket = new Button("Add");

		VBox ticketList = new VBox(50);

		ArrayList<HashMap> justAdded = new ArrayList<HashMap>();
		addTicket.setOnAction(new EventHandler() {
			public void handle(Event event) {
				HashMap data = CreateTaskForm.createTicketItem(justAdded);
				Pane temp = (Pane) data.get("Return");
				ticketList.getChildren().add(temp);
			}
		});

		VBox ticketContainer = new VBox(ticketsL, ticketList, addTicket);

		Button submitBtn = new Button("Submit");

		taskForm.getChildren().add(backBtn);

		taskForm.getChildren().addAll(titleV, descV);

		titleF.setText(title);
		descF.setText(description);

		if (a instanceof Task) {
			taskForm.getChildren().addAll(apContainer, colorPicker, ticketContainer);
			Task temp = (Task) this;
			awardF.setText(temp.getAward() + "");
			penaltyF.setText(temp.getPenalty() + "");
			for (WorkTicket t : Main.getTickets())
				if (t.getTask().equals(temp))
					ticketList.getChildren().add(CreateTaskForm.createTicketItem(t));
		}

		taskForm.getChildren().add(submitBtn);

		Button delBtn = new Button("Delete");
		delBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {
				if (a instanceof Task) {
					ArrayList<WorkTicket> copy = new ArrayList(((Task) a).getTickets());
					for (WorkTicket l : copy)
						l.delete();
					if (((Task) a).getHandleCompletionNotification() != null) {
						((Task) a).getHandleCompletionNotification().delete();
					}
				}

				Main.getPrimaryStage().getScene().setRoot(Main.getWorkspaceContainer());
				Main.getWorkspace().getChildren().remove(workspacePane);
				delete();
				Main.getEntities().remove(a);
				for (Line start : startLines) {
					if (Main.getWorkspace().getChildren().contains(start))
						Main.getWorkspace().getChildren().remove(start);
				}
				for (Line end : endLines) {
					if (Main.getWorkspace().getChildren().contains(end))
						Main.getWorkspace().getChildren().remove(end);
				}
				for (Task child : dependencies) {
					child.getDepedenciesOf().remove(a);
					child.getEndLines().removeAll(startLines);
				}
				for (TaskBase parent : dependenciesOf) {
					parent.getDependencies().remove(a);
					parent.getStartLines().removeAll(endLines);
					if (a instanceof Task)
						((VBox) parent.getReceededList().getContent()).getChildren()
								.removeAll(((Task) a).getReceededListItems());

				}
			}
		});
		taskForm.getChildren().add(delBtn);

		backBtn.setOnAction(new EventHandler() {

			public void handle(Event event) {
				Main.getPrimaryStage().getScene().setRoot(Main.getWorkspaceContainer());
			}
		});

		submitBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {
				Main.getPrimaryStage().getScene().setRoot(Main.getWorkspaceContainer());
				for (HashMap n : justAdded) {
					CreateTaskForm.saveTicket(n, (Task) a);
				}

				setTitle(titleF.getText());
				setDescription(descF.getText());
				if (a instanceof Task) {
					Task temp = (Task) a;
					temp.setAward(Double.parseDouble(awardF.getText()));
					temp.setPenalty(Double.parseDouble(penaltyF.getText()));
					temp.setColor(colorPicker.getValue());
				}

			}
		});
		ret.getChildren().addAll(profileScroll, scrollDependencies);
		profilePane = ret;

		return profilePane;
	}
}
