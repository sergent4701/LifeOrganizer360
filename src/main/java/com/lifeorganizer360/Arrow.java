package com.lifeorganizer360;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

@SuppressWarnings("restriction")
public class Arrow {

	private Line[] arrow = new Line[3];

	private int scaling = 10;

	protected Arrow() {
		this(0, 0, 0, 0);
	}

	protected Arrow(double startX, double startY, double endX, double endY) {
		arrow[0] = new Line(startX, startY, endX, endY);
		arrow[1] = generateLeg(45, scaling, arrow[0].getStartX(), arrow[0].getStartY(), arrow[0].getEndX(),
				arrow[0].getEndY());
		arrow[2] = generateLeg(315, scaling, arrow[0].getStartX(), arrow[0].getStartY(), arrow[0].getEndX(),
				arrow[0].getEndY());
	}

	private Line generateLeg(int deg, int scale, double s1, double s2, double e1, double e2) {
		double len = Math.sqrt(Math.pow(s1 - e1, 2) + Math.pow(s2 - e2, 2));
		double x = e1 + (scale / len)
				* (Math.cos(Math.toRadians(deg)) * (s1 - e1) - Math.sin(Math.toRadians(deg)) * (s2 - e2));
		double y = e2 + (scale / len)
				* (Math.sin(Math.toRadians(deg)) * (s1 - e1) + Math.cos(Math.toRadians(deg)) * (s2 - e2));
		return new Line(x, y, e1, e2);
	}

	private void updateLeg(Line l, int deg, int scale, double s1, double s2, double e1, double e2) {
		double len = Math.sqrt(Math.pow(s1 - e1, 2) + Math.pow(s2 - e2, 2));
		double x = e1 + (scale / len)
				* (Math.cos(Math.toRadians(deg)) * (s1 - e1) - Math.sin(Math.toRadians(deg)) * (s2 - e2));
		double y = e2 + (scale / len)
				* (Math.sin(Math.toRadians(deg)) * (s1 - e1) + Math.cos(Math.toRadians(deg)) * (s2 - e2));
		l.setStartX(x);
		l.setStartY(y);
		l.setEndX(e1);
		l.setEndY(e2);
	}

	public void setStrokeWidth(double w) {
		for (Line l : arrow) {
			l.setStrokeWidth(w);
		}
	}

	public void setStroke(Color c) {
		for (Line l : arrow) {
			l.setStroke(c);
		}
	}

	public void setStartX(double x) {
		arrow[0].setStartX(x);
		updateLeg(arrow[1], 45, scaling, x, arrow[0].getStartY(), arrow[0].getEndX(), arrow[0].getEndY());
		updateLeg(arrow[2], 315, scaling, x, arrow[0].getStartY(), arrow[0].getEndX(), arrow[0].getEndY());
	}

	public void setStartY(double y) {
		arrow[0].setStartY(y);
		updateLeg(arrow[1], 45, scaling, arrow[0].getStartX(), y, arrow[0].getEndX(), arrow[0].getEndY());
		updateLeg(arrow[2], 315, scaling, arrow[0].getStartX(), y, arrow[0].getEndX(), arrow[0].getEndY());
	}

	public void setEndX(double x) {
		arrow[0].setEndX(x);
		updateLeg(arrow[1], 45, scaling, arrow[0].getStartX(), arrow[0].getStartY(), x, arrow[0].getEndY());
		updateLeg(arrow[2], 315, scaling, arrow[0].getStartX(), arrow[0].getStartY(), x, arrow[0].getEndY());
	}

	public void setEndY(double y) {
		arrow[0].setEndY(y);
		updateLeg(arrow[1], 45, scaling, arrow[0].getStartX(), arrow[0].getStartY(), arrow[0].getEndX(), y);
		updateLeg(arrow[2], 315, scaling, arrow[0].getStartX(), arrow[0].getStartY(), arrow[0].getEndX(), y);
	}

	public Line[] getLines() {
		return arrow;
	}

}
