package com.lifeorganizer360;

import java.time.LocalDateTime;

import org.neo4j.ogm.annotation.*;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.control.Button;

@SuppressWarnings("restriction")
@NodeEntity
public class Goal extends TaskBase {

	protected Goal() {
		super();
	}

	protected Goal(String title, String description, double award, double penalty, LocalDateTime start,
			LocalDateTime end, double xPos, double yPos) {
		super(title, description, award, penalty, start, end, xPos, yPos);
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

			Circle bottomCircle = new Circle(4);
			setBottom(bottomCircle);

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

			topBar.getChildren().add(edit);

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
			Main.setBackgroundColor(Color.BLUE, topBar);

			ret.setLayoutX(e.getX());
			ret.setLayoutY(e.getY());

			ret.setPrefWidth(150);
			setPane(ret);
			return ret;
		} else {
			return super.getPane();
		}
	}

}
