package com.lifeorganizer360;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

@SuppressWarnings("unchecked")
public class DailyCalendar extends VBox {

	private LocalDate date;

	private HashMap<WorkTicket, ArrayList<Node>> tickets = new HashMap<WorkTicket, ArrayList<Node>>();

	protected DailyCalendar(LocalDate l) {
		date = l;

		Button backBtn = new Button("<");

		Button back = new Button("<");
		Button next = new Button(">");

		backBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {
				Main.getPrimaryStage().getScene().setRoot(Main.getMainContainer());
			}
		});
		back.setOnAction(new EventHandler() {
			public void handle(Event event) {
				Main.getPrimaryStage().getScene().setRoot(new DailyCalendar(l.minusDays(1)));
			}
		});
		next.setOnAction(new EventHandler() {
			public void handle(Event event) {
				Main.getPrimaryStage().getScene().setRoot(new DailyCalendar(l.plusDays(1)));
			}
		});
		Label current = new Label(l.getDayOfWeek() + ", " + l.getMonth() + " " + l.getDayOfMonth());
		current.setFont(new Font(21));

		HBox header = new HBox(15, backBtn, current);

		ScrollPane scroll = new ScrollPane();

		GridPane grid = new GridPane();

		grid.getColumnConstraints().add(new ColumnConstraints(100));
		grid.getColumnConstraints().add(new ColumnConstraints(300));

		scroll.setContent(grid);
		scroll.setMinWidth(418);
		scroll.setFitToWidth(true);
		scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scroll.setVvalue(0.5753362019856776);

		for (int i = 0; i < 25; i++) {
			Label time = new Label();
			HBox timeCont = new HBox(time);
			timeCont.setAlignment(Pos.BASELINE_RIGHT);
			Main.setBackgroundColor(Color.BLACK, timeCont);
			timeCont.setStyle(
					"-fx-border-style: solid hidden hidden hidden;-fx-border-width:2px;-fx-border-color:lightgray");

			Pane calendarCell = new Pane();
			calendarCell.setStyle(
					"-fx-border-style: solid hidden hidden hidden;-fx-border-width:2px;-fx-border-color:lightgray");

			if (i == 0 || i == 24) {
				time.setText("12:00 AM");
			} else if (i == 12) {
				time.setText("12:00 PM");
			} else if (i > 12) {
				time.setText(i % 12 + ":00 PM");
			} else {
				time.setText(i + ":00 AM");
			}
			time.setTextFill(Color.LIGHTGRAY);
			grid.add(timeCont, 0, i);
			grid.getRowConstraints().add(new RowConstraints(60));
			grid.add(calendarCell, 1, i);
		}

		for (WorkTicket t : Main.getTickets()) {
			if (!t.isInstance()) {
				if (t instanceof RecurringTicket) {
					for (WorkTicket c : ((RecurringTicket) t).getTickets()) {
						if (c.getStart().toLocalDate().equals(l) || c.getEnd().toLocalDate().equals(l))
							generateCalendarItem(c, t.getTask(), grid);
					}

				} else if (t.getStart().toLocalDate().equals(l) || t.getEnd().toLocalDate().equals(l)) {
					generateCalendarItem(t, grid);
				}
			}
		}
		if (l.equals(LocalDate.now())) {
			Rectangle[] cursor = new Rectangle[2];
			cursor[0] = new Rectangle(100, 3);
			cursor[1] = new Rectangle(300, 3);
			for (Rectangle r : cursor) {
				r.setFill(Color.RED);
			}
			updateCursor(cursor, grid);
			Timeline oneMinTimer = new Timeline(new KeyFrame(
					Duration.millis(60000 - System.currentTimeMillis() % 60000), ae -> updateCursor(cursor, grid)));
			oneMinTimer.play();

		}

		HBox hor = new HBox(back, scroll, next, generateDisplayMenu());
		getChildren().addAll(header, hor);

		hor.setAlignment(Pos.CENTER);

	}

	private Pane getCurrentCell(int i, GridPane g) {
		Node n = getNodeFromGridPane(g, 1, i);
		Pane currentCell = null;
		if (n == null) {
			currentCell = new Pane();
			g.add(currentCell, 1, i);
		} else
			currentCell = (Pane) n;
		return currentCell;
	}

	private void updateCursor(Rectangle[] cursor, GridPane grid) {
		if (cursor[0].getParent() != null)
			((Pane) cursor[0].getParent()).getChildren().remove(cursor[0]);
		if (cursor[1].getParent() != null)
			((Pane) cursor[1].getParent()).getChildren().remove(cursor[1]);

		getCurrentCell(LocalTime.now().getHour(), grid).getChildren().addAll(cursor[0], cursor[1]);

		for (Rectangle r : cursor)
			r.setLayoutY(LocalTime.now().getMinute());

		Timeline cursorTimer = new Timeline(new KeyFrame(Duration.millis(60000), ae -> updateCursor(cursor, grid)));
		cursorTimer.play();
	}

	private void generateCalendarItem(WorkTicket t, GridPane g) {
		generateCalendarItem(t, t.getTask(), g);
	}

	private void generateCalendarItem(WorkTicket t, Task task, GridPane g) {
		boolean includeStart = true, includeEnd = true;
		int j = t.getEnd().getHour();
		int k = t.getStart().getHour();

		if (!date.equals(t.getEnd().toLocalDate())) {
			j = 23;
			includeEnd = false;
		}
		if (!date.equals(t.getStart().toLocalDate())) {
			k = 0;
			includeStart = false;
		}
		Color color = task.getColor();
		for (int i = k; i <= j; i++) {
			Pane currentCell = getCurrentCell(i, g);

			Rectangle rect = new Rectangle();

			rect.setWidth(300);
			rect.setFill(color);
			rect.setOpacity(.5);

			if (includeStart && i == t.getStart().getHour()) {
				rect.setLayoutY(t.getStart().getMinute());
				if (t.getStart().getHour() == t.getEnd().getHour()) {
					rect.setHeight(t.getDuration());

					currentCell.getChildren().addAll(getTop(rect, color), getBottom(rect, color), getLeft(rect, color),
							getRight(rect, color));
				} else {
					rect.setHeight(60 - t.getStart().getMinute());
					currentCell.getChildren().addAll(getTop(rect, color), getLeft(rect, color), getRight(rect, color));
				}
			} else if (includeEnd && i == t.getEnd().getHour()) {
				rect.setHeight(t.getEnd().getMinute());
				currentCell.getChildren().addAll(getBottom(rect, color), getLeft(rect, color), getRight(rect, color));
			} else {
				rect.setHeight(60);
				currentCell.getChildren().addAll(getLeft(rect, color), getRight(rect, color));
			}
			rect.setCursor(Cursor.HAND);
			rect.setOnMouseEntered(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent mouseEvent) {
					((HBox) getChildren().get(1)).getChildren().remove(3);
					((HBox) getChildren().get(1)).getChildren().add(generateDisplayMenu(task, t));
				}
			});
			rect.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent mouseEvent) {
					Main.getPrimaryStage().getScene().setRoot(task.getProfilePane());
				}
			});
			currentCell.getChildren().add(rect);

			if (!tickets.containsKey(t)) {
				tickets.put(t, new ArrayList<Node>());
			}
			tickets.get(t).add(rect);

		}
		VBox container = new VBox(5);

		if (((Rectangle) tickets.get(t).get(0)).getHeight() >= 16) {
			((Pane) ((Rectangle) tickets.get(t).get(0)).getParent()).getChildren().add(container);
			container.setLayoutY(tickets.get(t).get(0).getLayoutY() + 1);
		} else if (tickets.get(t).size() >= 2 && ((Rectangle) tickets.get(t).get(1)).getHeight() >= 16) {
			((Pane) ((Rectangle) tickets.get(t).get(1)).getParent()).getChildren().add(container);
		}

		Label title = new Label(task.getTitle());
		title.setFont(new Font(15));

		container.getChildren().add(title);
		container.setLayoutX(10);

		tickets.get(t).add(container);
	}

	private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
		for (Node node : gridPane.getChildren()) {
			if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
				return node;
			}
		}
		return null;
	}

	private Node getTop(Rectangle r, Color c) {
		Rectangle ret = new Rectangle(r.getWidth(), 1);
		ret.setLayoutY(r.getLayoutY());
		ret.setFill(c);
		return ret;
	}

	private Node getBottom(Rectangle r, Color c) {
		Rectangle ret = new Rectangle(r.getWidth(), 1);
		ret.setLayoutY(r.getLayoutY() + r.getHeight() - 1);
		ret.setFill(c);
		return ret;

	}

	private Node getLeft(Rectangle r, Color c) {
		Rectangle ret = new Rectangle(1, r.getHeight());
		ret.setLayoutY(r.getLayoutY());
		ret.setFill(c);
		return ret;
	}

	private Node getRight(Rectangle r, Color c) {
		Rectangle ret = new Rectangle(1, r.getHeight());
		ret.setLayoutX(r.getWidth() - 1);
		ret.setLayoutY(r.getLayoutY());
		ret.setFill(c);
		return ret;
	}

	private Node generateDisplayMenu() {
		for (WorkTicket t : tickets.keySet())
			if (!(t instanceof RecurringTicket) && t.getTask() != null)
				return generateDisplayMenu(t.getTask(), t);
		VBox ret = new VBox(new Label("Hover to see ticket information!"));
		ret.setPrefWidth(400);
		return ret;
	}

	private Node generateDisplayMenu(Task task, WorkTicket t) {
		VBox ret = new VBox();
		ret.setPrefWidth(400);

		Label award = new Label("Award: " + task.getAward());
		Label penalty = new Label("Penalty: " + task.getPenalty());
		HBox apcontainer = new HBox(15, award, penalty);
		award.setFont(new Font(12));
		penalty.setFont(new Font(12));
		Label title = new Label(task.getTitle());
		title.setFont(new Font(18));
		Label description = new Label(task.getDescription());
		title.setFont(new Font(18));
		Label start = new Label("Start: " + t.getStart());
		Label end = new Label("End: " + t.getEnd());
		HBox secontainer = new HBox(15, start, end);
		start.setFont(new Font(14));
		end.setFont(new Font(14));

		ret.getChildren().addAll(apcontainer, title, description, secontainer);

		return ret;
	}
}
