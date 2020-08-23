package com.lifeorganizer360;

import java.time.LocalDateTime;

import org.neo4j.ogm.annotation.*;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.control.ScrollPane;
import javafx.scene.shape.Line;

@SuppressWarnings("restriction")
@NodeEntity
public class Task extends TaskBase {

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

	protected Task() {
		super();
	}

	protected Task(String title, String description, double award, double penalty, LocalDateTime start,
			LocalDateTime end, double xPos, double yPos) {
		super(title, description, xPos, yPos);
		this.start = start;
		this.end = end;
		this.award = award;
		this.penalty = penalty;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Pane getPane() {

		if (super.getPane() == null) {
			TaskBase e = this;
			final BorderPane ret = new BorderPane();

			DropShadow ds = new DropShadow();
			ds.setOffsetY(3.0);
			ds.setOffsetX(3.0);
			ds.setColor(Color.GRAY);

			ret.setEffect(ds);

			Circle topCircle = new Circle(4);
			Circle bottomCircle = new Circle(4);
			setTop(topCircle);
			setBottom(bottomCircle);

			topCircle.setFill(Color.WHITE);
			bottomCircle.setFill(Color.WHITE);

			bottomCircle.setCursor(Cursor.HAND);

			HBox bottomBar = new HBox();
			final Pane topBar = new Pane();
			HBox edit = new HBox(2);
			Circle c1 = new Circle(2);
			Circle c2 = new Circle(2);
			Circle c3 = new Circle(2);
			c1.setFill(Color.WHITE);
			c2.setFill(Color.WHITE);
			c3.setFill(Color.WHITE);
			edit.getChildren().add(c1);
			edit.getChildren().add(c2);
			edit.getChildren().add(c3);

			edit.setCursor(Cursor.HAND);
			edit.setOnMouseClicked(new EventHandler() {
				public void handle(Event event) {
					Main.getPrimaryStage().getScene().setRoot(Main.generateTaskForm(e));
				}
			});

			edit.setAlignment(Pos.CENTER);

			topBar.setMinHeight(15);
			bottomBar.setMinHeight(15);
			edit.setMinHeight(15);

			topBar.setMaxHeight(15);
			bottomBar.setMaxHeight(15);
			edit.setMaxHeight(15);

			topBar.getChildren().add(topCircle);
			topBar.getChildren().add(edit);

			topCircle.setLayoutX(75);
			topCircle.setLayoutY(7.5);
			edit.setLayoutX(125);

			bottomBar.getChildren().add(bottomCircle);

			bottomBar.setAlignment(Pos.CENTER);

			topBar.setCursor(Cursor.OPEN_HAND);

			final double[] mouse = new double[2];

			bottomCircle.setOnMousePressed(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent mouseEvent) {
					Main.getWorkspace().setCursor(Cursor.CROSSHAIR);
					Main.setDependencyInit(true);
					Main.setDependencyParent(e);
				}
			});
			topCircle.setOnMouseEntered(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent mouseEvent) {
					if (Main.getDependencyInit())
						topCircle.setCursor(Cursor.HAND);
				}
			});
			topCircle.setOnMouseExited(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent mouseEvent) {
					topCircle.setCursor(Cursor.OPEN_HAND);
				}
			});
			topCircle.setOnMousePressed(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent mouseEvent) {
					if (Main.getDependencyInit()) {
						Main.getWorkspace().setCursor(Cursor.DEFAULT);
						Main.getDependencyParent().addDependency(e);
						Main.getSession().save(Main.getDependencyParent());

						Line dependency = new Line();

						dependency.setStrokeWidth(3);
						dependency.setStroke(Color.BLACK);
						Main.getWorkspace().getChildren().add(dependency);
						Main.getDependencyParent().addStartLine(dependency);
						e.addEndLine(dependency);

						Main.setDependencyInit(false);
					}
				}
			});

			topBar.setOnMousePressed(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent mouseEvent) {
					mouse[0] = mouseEvent.getSceneX();
					mouse[1] = mouseEvent.getSceneY();
					topBar.setCursor(Cursor.CLOSED_HAND);
				}
			});
			topBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent mouseEvent) {
					double deltaX = mouseEvent.getSceneX() - mouse[0];
					double deltaY = mouseEvent.getSceneY() - mouse[1];
					e.setX(ret.getLayoutX() + deltaX);
					e.setY(ret.getLayoutY() + deltaY);
					mouse[0] = mouseEvent.getSceneX();
					mouse[1] = mouseEvent.getSceneY();
					updateLines();
				}
			});
			topBar.setOnMouseReleased(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent mouseEvent) {
					topBar.setCursor(Cursor.OPEN_HAND);
					Main.getSession().save(e);
				}
			});
			VBox content = new VBox();

			Label title = new Label(e.getTitle());
			title.setFont(new Font(15));
			title.setWrapText(true);
			setTitlePointer(title);
			content.getChildren().add(title);

			ScrollPane descScroll = new ScrollPane();
			descScroll.setMaxWidth(150);
			descScroll.setMinHeight(50);
			descScroll.setMaxHeight(50);
			Label description = new Label(e.getDescription());
			description.setFont(new Font(11));
			description.setWrapText(true);
			setDescriptionPointer(description);
			descScroll.setContent(description);
			descScroll.setFitToWidth(true);
			content.getChildren().add(descScroll);

			ret.setTop(topBar);
			ret.setCenter(content);
			ret.setBottom(bottomBar);

			Main.setBackgroundColor(Color.WHITE, ret);
			Main.setBackgroundColor(Color.DARKGREY, bottomBar);
			Main.setBackgroundColor(Color.GREEN, topBar);

			ret.setLayoutX(e.getX());
			ret.setLayoutY(e.getY());

			ret.setPrefWidth(150);
			setPane(ret);
			return ret;
		} else {
			return super.getPane();
		}
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

}
