package com.lifeorganizer360;

import java.util.ArrayList;
import javafx.scene.Node;
import javafx.scene.control.Label;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Transient;

@SuppressWarnings("restriction")
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

	@Relationship(type = "DEPENDS_ON", direction = Relationship.OUTGOING)
	private ArrayList<TaskBase> dependencies = new ArrayList<TaskBase>();

	@Transient
	private ArrayList<Arrow> startLines = new ArrayList<Arrow>();

	@Transient
	private ArrayList<Arrow> endLines = new ArrayList<Arrow>();

	@Transient
	private Node pane;

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

	public Node getPane() {
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

	public ArrayList<Arrow> getStartArrows() {
		return startLines;
	}

	public ArrayList<Arrow> getEndArrows() {
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

	public void setPane(Node n) {
		pane = n;
	}
}
