package com.lifeorganizer360;

import java.time.LocalDateTime;
import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

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
	private double xPos;

	@Property
	private double yPos;

	@Property
	private String title;

	@Property
	private String description;

	@Property
	private boolean complete = false;

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
	private boolean busyWork;

	@Relationship(type = "DEPENDS_ON", direction = Relationship.OUTGOING)
	private ArrayList<TaskBase> dependencies = new ArrayList<TaskBase>();

	@Transient
	private ArrayList<Line> startLines = new ArrayList<Line>();

	@Transient
	private ArrayList<Line> endLines = new ArrayList<Line>();

	@Transient
	private Pane pane;

	@Transient
	private Label titlePointer, descriptionPointer;

	@Transient
	private Circle top, bottom;

	protected TaskBase() {

	}

	protected TaskBase(String title, String description, double award, double penalty, LocalDateTime start,
			LocalDateTime end, double xPos, double yPos) {
		this.title = title;
		this.description = description;
		this.award = award;
		this.penalty = penalty;
		this.start = start;
		this.end = end;
		this.xPos = xPos;
		this.yPos = yPos;
	}

	public void save(String title, String description, double award, double penalty, LocalDateTime start,
			LocalDateTime end) {
		this.title = title;
		this.description = description;
		this.award = award;
		this.penalty = penalty;
		this.start = start;
		this.end = end;
	}

	public Pane getPane() {
		return pane;
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

	public double getAward() {
		return award;
	}

	public double getPenalty() {
		return penalty;
	}

	public LocalDateTime getStart() {
		return start;
	}

	public LocalDateTime getEnd() {
		return end;
	}

	public void setAward(double a) {
		award = a;
	}

	public void setPenalty(double p) {
		penalty = p;
	}

	public void setStart(LocalDateTime s) {
		start = s;
	}

	public void setEnd(LocalDateTime e) {
		end = e;
	}

	public void setTitle(String t) {
		title = t;
		if (titlePointer != null)
			titlePointer.setText(t);
	}

	public void setDescription(String d) {
		description = d;
		if (descriptionPointer != null)
			descriptionPointer.setText(d);
	}

	public void setX(double x) {
		xPos = x;
		if (pane != null)
			pane.setLayoutX(getX());
	}

	public void setY(double y) {
		yPos = y;
		if (pane != null)
			pane.setLayoutY(getY());
	}

	public void addDependency(TaskBase t) {
		dependencies.add(t);
	}

	public ArrayList<TaskBase> getDependencies() {
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

	public void setTitlePointer(Label l) {
		titlePointer = l;
	}

	public void setDescriptionPointer(Label l) {
		descriptionPointer = l;
	}

	public void setPane(Pane n) {
		pane = n;
	}

	public void setTop(Circle c) {
		top = c;
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

	public void setBottom(Circle c) {
		bottom = c;
	}

	public void drawStartLine(Line a) {
		if (pane != null) {
			a.startXProperty().bind(pane.widthProperty().divide(2).add(xPos));
			a.startYProperty().bind(pane.heightProperty().add(yPos).subtract(7.5));
		}
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
		if (pane != null) {
			a.endXProperty().bind(pane.widthProperty().divide(2).add(xPos));
			a.setEndY(yPos + 7.5);
		}
	}

	public void addEndLine(Line a) {
		drawEndLine(a);
		endLines.add(a);
	}

	public Circle getTop() {
		return top;
	}

	public Circle getBottom() {
		return bottom;
	}
}
