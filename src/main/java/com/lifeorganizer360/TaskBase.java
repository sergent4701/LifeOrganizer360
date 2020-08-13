package com.lifeorganizer360;

import java.util.ArrayList;
import javafx.scene.Node;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Transient;

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

	protected TaskBase() {

	}

	protected TaskBase(String title, String description, double xPos, double yPos) {
		this.title = title;
		this.description = description;
		this.xPos = xPos;
		this.yPos = yPos;
	}
	public abstract Node generatePane();

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

	public void setX(double x) {
		xPos = x;
	}

	public void setY(double y) {
		yPos = y;
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

}
