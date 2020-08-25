package com.lifeorganizer360;

import java.time.LocalDateTime;
import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import tornadofx.control.DateTimePicker;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Transient;

@SuppressWarnings({ "restriction", "unchecked", "rawtypes" })
@NodeEntity
public abstract class TaskBase {
	@Id
	@GeneratedValue
	private Long id;

	@Property
	private double xPos, yPos, progress;

	@Property
	private String title, description;

	@Property
	private boolean complete = false, busyWork, collapsed, receeded, hidden;

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
	private Label titlePointer, descriptionPointer;

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

	public long getId() {
		return id;
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

	public void save() {
		Main.getSession().save(this);
	}

	public HBox getProfilePane() {
		TaskBase t = this;
		HBox ret = new HBox();
		ScrollPane scrollDependencies = new ScrollPane();
		VBox dependencyList = new VBox(3);
		scrollDependencies.setContent(dependencyList);
		for (Task t : dependencies) {
			dependencyList.getChildren().add(t.generateDropdownListItem(this));
		}
		Button addBtn = new Button("Add");
		addBtn.setPrefWidth(600);
		dependencyList.getChildren().add(addBtn);

		addBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {
				Main.getPrimaryStage().getScene().setRoot(new CreateTaskForm(CreateTaskForm.PROFILE, t));
			}
		});

		VBox taskForm = new VBox(10);

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

		taskForm.getChildren().add(backBtn);

		taskForm.getChildren().addAll(titleV, descV);

		titleF.setText(title);
		descF.setText(description);

		if (t instanceof Task) {
			taskForm.getChildren().addAll(apContainer, seContainer);
			Task temp = (Task) this;
			awardF.setText(temp.getAward() + "");
			penaltyF.setText(temp.getPenalty() + "");
			startF.setDateTimeValue(temp.getStart());
			endF.setDateTimeValue(temp.getEnd());
		}

		taskForm.getChildren().add(submitBtn);

		Button delBtn = new Button("Delete");
		delBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {
				Main.getPrimaryStage().getScene().setRoot(Main.getWorkspaceContainer());
				Main.getWorkspace().getChildren().remove(workspacePane);
				Main.getSession().delete(Main.getSession().load(TaskBase.class, id));
				Main.getEntities().remove(t);
				for (Line start : startLines) {
					if (Main.getWorkspace().getChildren().contains(start))
						Main.getWorkspace().getChildren().remove(start);
				}
				for (Line end : endLines) {
					if (Main.getWorkspace().getChildren().contains(end))
						Main.getWorkspace().getChildren().remove(end);
				}
				for (Task child : dependencies) {
					child.getDepedenciesOf().remove(t);
					child.getEndLines().removeAll(startLines);
				}
				for (TaskBase parent : dependenciesOf) {
					parent.getDependencies().remove(t);
					parent.getStartLines().removeAll(endLines);
					if (t instanceof Task)
						((VBox) parent.getReceededList().getContent()).getChildren()
								.removeAll(((Task) t).getReceededListItems());

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

				setTitle(titleF.getText());
				setDescription(descF.getText());
				if (t instanceof Task) {
					Task temp = (Task) t;
					temp.setAward(Double.parseDouble(awardF.getText()));
					temp.setPenalty(Double.parseDouble(penaltyF.getText()));
					temp.setStart(startF.getDateTimeValue());
					temp.setEnd(endF.getDateTimeValue());

				}

			}
		});
		ret.getChildren().addAll(taskForm, scrollDependencies);
		profilePane = ret;

		return profilePane;
	}
}
