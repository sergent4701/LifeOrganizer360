package com.lifeorganizer360;

import org.neo4j.ogm.annotation.*;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
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
public class Goal extends TaskBase {

	protected Goal() {
		super();
	}

	protected Goal(String title, String description, double xPos, double yPos) {
		super(title, description, xPos, yPos);
	}

	@SuppressWarnings("restriction")
	public Node generatePane() {
		TaskBase e = this;
		final BorderPane ret = new BorderPane();

		DropShadow ds = new DropShadow();
		ds.setOffsetY(3.0);
		ds.setOffsetX(3.0);
		ds.setColor(Color.GRAY);

		ret.setEffect(ds);

		Circle bottomCircle = new Circle(4);

		bottomCircle.setFill(Color.WHITE);
		bottomCircle.setCursor(Cursor.HAND);

		HBox bottomBar = new HBox();
		final HBox topBar = new HBox();

		topBar.setMinHeight(15);
		bottomBar.setMinHeight(15);

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
				ret.relocate(ret.getLayoutX() + deltaX, ret.getLayoutY() + deltaY);
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

		Label description = new Label(e.getDescription());
		description.setFont(new Font(12));
		description.setWrapText(true);

		VBox content = new VBox();

		content.getChildren().add(title);
		content.getChildren().add(description);

		ret.setTop(topBar);
		ret.setCenter(content);
		ret.setBottom(bottomBar);

		Main.setBackgroundColor(Color.WHITE, ret);
		Main.setBackgroundColor(Color.DARKGREY, bottomBar);
		Main.setBackgroundColor(Color.BLUE, topBar);

		ret.setLayoutX(e.getX());
		ret.setLayoutY(e.getY());

		ret.setPrefWidth(150);
		ret.setMinHeight(100);
		return ret;

	}

}
