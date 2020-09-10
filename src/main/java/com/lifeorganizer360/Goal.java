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
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.control.Button;

@SuppressWarnings("restriction")
@NodeEntity
public class Goal extends TaskBase {

	protected Goal() {
		super();
	}

	protected Goal(String title, String description, double xPos, double yPos) {
		super(title, description, xPos, yPos);
		save();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Pane getWorkspacePane() {
		if (super.getWorkspacePane() == null) {
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

			Pane bottomBar = new Pane();
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
					Main.getPrimaryStage().getScene().setRoot(getProfilePane());
				}
			});

			Line horLine = new Line(0, 0, 12, 0);
			Line topLine = new Line(8, 4, 12, 0);
			Line bottomLine = new Line(8, -4, 12, 0);
			Pane arrowIcon = new Pane();

			arrowIcon.getChildren().addAll(horLine, topLine, bottomLine);
			arrowIcon.setCursor(Cursor.HAND);

			Line burger1 = new Line(0, 0, 12, 0);
			Line burger2 = new Line(0, 4, 12, 4);
			Line burger3 = new Line(0, 8, 12, 8);
			Pane burgerIcon = new Pane();

			burgerIcon.getChildren().addAll(burger1, burger2, burger3);
			burgerIcon.setCursor(Cursor.HAND);

			Line leftDropDown = new Line(0, 0, -5, -5);
			Line rightDropDown = new Line(0, 0, 5, -5);
			Pane dropDownDown = new Pane();
			dropDownDown.getChildren().addAll(leftDropDown, rightDropDown);
			dropDownDown.setCursor(Cursor.HAND);

			Line leftDropUp = new Line(0, 0, -5, 5);
			Line rightDropUp = new Line(0, 0, 5, 5);
			Pane dropDownUp = new Pane();
			dropDownUp.getChildren().addAll(leftDropUp, rightDropUp);
			dropDownUp.setCursor(Cursor.HAND);

			Line topDown = new Line(-4, 0, 4, 0);
			Line leftRight = new Line(0, -4, 0, 4);
			Pane plus = new Pane();
			plus.getChildren().addAll(topDown, leftRight);
			plus.setCursor(Cursor.HAND);

			topDown.setStroke(Color.WHITE);
			topDown.setStrokeWidth(2);

			leftRight.setStroke(Color.WHITE);
			leftRight.setStrokeWidth(2);

			leftDropUp.setStroke(Color.WHITE);
			leftDropUp.setStrokeWidth(2);

			rightDropUp.setStroke(Color.WHITE);
			rightDropUp.setStrokeWidth(2);

			leftDropDown.setStroke(Color.WHITE);
			leftDropDown.setStrokeWidth(2);

			rightDropDown.setStroke(Color.WHITE);
			rightDropDown.setStrokeWidth(2);

			burger1.setStroke(Color.WHITE);
			burger1.setStrokeWidth(2);

			burger2.setStroke(Color.WHITE);
			burger2.setStrokeWidth(2);

			burger3.setStroke(Color.WHITE);
			burger3.setStrokeWidth(2);

			topLine.setStroke(Color.WHITE);
			topLine.setStrokeWidth(2);

			bottomLine.setStroke(Color.WHITE);
			bottomLine.setStrokeWidth(2);

			horLine.setStroke(Color.WHITE);
			horLine.setStrokeWidth(2);

			edit.setAlignment(Pos.CENTER);

			topBar.setMinHeight(15);
			bottomBar.setMinHeight(15);
			edit.setMinHeight(15);

			topBar.setMaxHeight(15);
			bottomBar.setMaxHeight(15);
			edit.setMaxHeight(15);

			topBar.getChildren().add(edit);

			edit.setLayoutX(125);

			bottomCircle.setLayoutX(75);
			bottomCircle.setLayoutY(7.5);

			plus.setLayoutX(75);
			plus.setLayoutY(7.5);

			arrowIcon.setLayoutX(7);
			arrowIcon.setLayoutY(7);

			burgerIcon.setLayoutX(7);
			burgerIcon.setLayoutY(3.5);

			dropDownDown.setLayoutX(135);
			dropDownDown.setLayoutY(10);

			dropDownUp.setLayoutX(135);
			dropDownUp.setLayoutY(5);

			if (isCollapsed()) {
				bottomBar.getChildren().add(dropDownDown);
			} else {
				bottomBar.getChildren().add(dropDownUp);
			}

			if (isReceeded()) {
				bottomBar.getChildren().addAll(plus, arrowIcon);
			} else {
				bottomBar.getChildren().addAll(bottomCircle, burgerIcon);
			}

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

			if (!isCollapsed()) {
				content.getChildren().add(descScroll);
				if (isReceeded()) {
					content.getChildren().add(getReceededList());
				}
			}
			burgerIcon.setOnMouseClicked(new EventHandler() {
				public void handle(Event event) {
					bottomBar.getChildren().remove(burgerIcon);
					bottomBar.getChildren().add(arrowIcon);
					if (!isCollapsed())
						content.getChildren().add(getReceededList());
					setReceeded(true);
					for (Task t : getDependencies()) {
						boolean hide = true;
						for (TaskBase parent : t.getDepedenciesOf()) {
							if (!parent.isReceeded() && e != parent) {
								hide = false;
							}
						}
						if (hide) {
							t.setHidden(true);
						}
					}
					for (Line l : getStartLines()) {
						Main.getWorkspace().getChildren().remove(l);
					}
					bottomBar.getChildren().remove(bottomCircle);
					bottomBar.getChildren().add(plus);
				}
			});

			arrowIcon.setOnMouseClicked(new EventHandler() {
				public void handle(Event event) {
					bottomBar.getChildren().remove(arrowIcon);
					bottomBar.getChildren().add(burgerIcon);
					setReceeded(false);
					content.getChildren().remove(getReceededList());
					for (Task t : getDependencies()) {
						t.setHidden(false);
					}
					for (Line l : getStartLines()) {
						Main.getWorkspace().getChildren().add(l);
					}
					bottomBar.getChildren().remove(plus);
					bottomBar.getChildren().add(bottomCircle);				}
			});

			dropDownUp.setOnMouseClicked(new EventHandler() {
				public void handle(Event event) {
					bottomBar.getChildren().remove(dropDownUp);
					bottomBar.getChildren().add(dropDownDown);
					setCollapsed(true);
					content.getChildren().remove(descScroll);
					if (isReceeded()) {
						content.getChildren().remove(getReceededList());
					}
				}
			});
			dropDownDown.setOnMouseClicked(new EventHandler() {
				public void handle(Event event) {
					bottomBar.getChildren().remove(dropDownDown);
					bottomBar.getChildren().add(dropDownUp);
					setCollapsed(false);
					content.getChildren().add(descScroll);
					if (isReceeded()) {
						content.getChildren().add(getReceededList());
					}
				}
			});
			plus.setOnMouseClicked(new EventHandler() {
				public void handle(Event event) {
					Main.getPrimaryStage().getScene().setRoot(new CreateTaskForm(CreateTaskForm.RECEEDED, e));
				}
			});

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
			return super.getWorkspacePane();
		}
	}

}
