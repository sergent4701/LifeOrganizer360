package com.lifeorganizer360;

import org.neo4j.ogm.annotation.*;

import javafx.event.Event;
import javafx.event.EventHandler;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

@SuppressWarnings("restriction")
@NodeEntity
public class Task extends TaskBase {

	protected Task() {
		super();
	}

	protected Task(String title, String description, double xPos, double yPos) {
		super(title, description, xPos, yPos);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Node getPane() {

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

			topCircle.setFill(Color.WHITE);
			bottomCircle.setFill(Color.WHITE);

			bottomCircle.setCursor(Cursor.HAND);

			HBox bottomBar = new HBox();
			final HBox topBar = new HBox();
			Button edit = new Button("...");

			edit.setCursor(Cursor.HAND);
			edit.setOnAction(new EventHandler() {
				public void handle(Event event) {
					Main.getPrimaryStage().getScene().setRoot(Main.generateTaskForm(e));
				}
			});

			topBar.getChildren().add(edit);

			topBar.setMinHeight(15);
			bottomBar.setMinHeight(15);

			topBar.getChildren().add(topCircle);
			bottomBar.getChildren().add(bottomCircle);

			topBar.setAlignment(Pos.CENTER);
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

						Arrow dependency = new Arrow(Main.getDependencyParent().getX() + 75,
								Main.getDependencyParent().getY() + 93, e.getX() + 73, e.getY() + 8);
						dependency.setStrokeWidth(3);
						dependency.setStroke(Color.BLACK);
						Main.getWorkspace().getChildren().add(dependency.getLines()[0]);
						Main.getWorkspace().getChildren().add(dependency.getLines()[1]);
						Main.getWorkspace().getChildren().add(dependency.getLines()[2]);
						Main.getDependencyParent().getStartArrows().add(dependency);
						e.getEndArrows().add(dependency);

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
					for (Arrow start : e.getStartArrows()) {
						start.setStartX(e.getX() + 75);
						start.setStartY(e.getY() + 93);
					}
					for (Arrow end : e.getEndArrows()) {
						end.setEndX(e.getX() + 75);
						end.setEndY(e.getY() + 8);
					}
				}
			});
			topBar.setOnMouseReleased(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent mouseEvent) {
					topBar.setCursor(Cursor.OPEN_HAND);
					Main.getSession().save(e);
				}
			});

			Label title = new Label(e.getTitle());
			title.setFont(new Font(16));
			title.setWrapText(true);
			setTitlePointer(title);

			Label description = new Label(e.getDescription());
			description.setFont(new Font(12));
			description.setWrapText(true);
			setDescriptionPointer(description);

			VBox content = new VBox();

			content.getChildren().add(title);
			content.getChildren().add(description);

			ret.setTop(topBar);
			ret.setCenter(content);
			ret.setBottom(bottomBar);

			Main.setBackgroundColor(Color.WHITE, ret);
			Main.setBackgroundColor(Color.DARKGREY, bottomBar);
			Main.setBackgroundColor(Color.GREEN, topBar);

			ret.setLayoutX(e.getX());
			ret.setLayoutY(e.getY());

			ret.setPrefWidth(150);
			ret.setMinHeight(100);
			setPane(ret);
			return ret;
		} else {
			return super.getPane();
		}
	}

}
