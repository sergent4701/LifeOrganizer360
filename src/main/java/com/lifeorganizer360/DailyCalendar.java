package com.lifeorganizer360;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
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
	LocalDate date;

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
		Pane background = new Pane();
		Pane foreground = new Pane();
		StackPane stack = new StackPane(background, foreground);
		stack.setMinWidth(600);
		stack.setMinHeight(Main.getPrimaryStage().getHeight() - current.getHeight());
		Main.setBackgroundColor(Color.GRAY, background);
		Rectangle rec = new Rectangle(100, stack.getHeight());
		rec.setFill(Color.BLACK);
		rec.heightProperty().bind(stack.heightProperty());
		Line ver1 = new Line(266.66, 0, 266.66, Main.getPrimaryStage().getHeight());
		ver1.endYProperty().bind(stack.heightProperty());

		Line ver2 = new Line(433.33, 0, 433.33, Main.getPrimaryStage().getHeight());
		ver2.endYProperty().bind(stack.heightProperty());

		ver1.setStroke(Color.LIGHTGRAY);
		ver1.setStrokeWidth(1);
		ver2.setStroke(Color.LIGHTGRAY);
		ver2.setStrokeWidth(1);

		background.getChildren().addAll(rec, ver1, ver2);

		scroll.setContent(stack);
		scroll.setMinWidth(620);
		scroll.setFitToWidth(true);
		scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

		for (int i = 0; i < 25; i++) {
			Label time = new Label();
			if (i == 0 || i == 24) {
				time.setText("12:00 AM");
			} else if (i == 12) {
				time.setText("12:00 PM");
			} else if (i > 12) {
				time.setText(i % 12 + ":00 PM");
			} else {
				time.setText(i + ":00 AM");
			}
			Line line = new Line(25, 30 + 50 * i, 600, 30 + 50 * i);
			line.setStroke(Color.LIGHTGRAY);
			line.setStrokeWidth(2);
			time.setLayoutX(25);
			time.setLayoutY(34 + 50 * i);
			time.setTextFill(Color.LIGHTGRAY);
			background.getChildren().addAll(line, time);
		}

		for (WorkTicket t : Main.getTickets()) {
			if (t instanceof RecurringTicket) {
				for (WorkTicket c : ((RecurringTicket) t).getTickets()) {
					if (c.getStart().toLocalDate().equals(l) || c.getEnd().toLocalDate().equals(l))
						foreground.getChildren().add(generateCalendarItem(c, t.getTask()));

				}

			} else if (t.getStart().toLocalDate().equals(l) || t.getEnd().toLocalDate().equals(l)) {
				foreground.getChildren().add(generateCalendarItem(t));
			}
		}
		if (l.equals(LocalDate.now())) {
			Line cursor = new Line(100,
					30 + (5.0 / 6.0) * (60 * LocalTime.now().getHour() + LocalTime.now().getMinute()), 600,
					30 + (5.0 / 6.0) * (60 * LocalTime.now().getHour() + LocalTime.now().getMinute()));
			cursor.setStroke(Color.RED);
			cursor.setStrokeWidth(3);
			foreground.getChildren().add(cursor);
			Timeline cursorTimer = new Timeline(new KeyFrame(Duration.millis(60000), ae -> updateCursor(cursor)));
			cursorTimer.play();
		}
		HBox hor = new HBox(back, scroll, next);
		getChildren().addAll(header, hor);

		hor.setAlignment(Pos.CENTER);

	}

	private void updateCursor(Line c) {
		c.setStartY(30 + (5.0 / 6.0) * (60 * LocalTime.now().getHour() + LocalTime.now().getMinute()));
		c.setEndY(30 + (5.0 / 6.0) * (60 * LocalTime.now().getHour() + LocalTime.now().getMinute()));
		Timeline cursorTimer = new Timeline(new KeyFrame(Duration.millis(60000), ae -> updateCursor(c)));
		cursorTimer.play();
	}

	private StackPane generateCalendarItem(WorkTicket t) {
		return generateCalendarItem(t, t.getTask());
	}

	private StackPane generateCalendarItem(WorkTicket t, Task task) {
		StackPane ret = new StackPane();
		VBox container = new VBox(5);
		Rectangle background = null;
		if (!t.getStart().toLocalDate().equals(t.getEnd().toLocalDate())) {
			if (t.getStart().toLocalDate().equals(date)) {
				background = new Rectangle(166.66,
						(5.0 / 6.0) * (1440 - (60 * t.getStart().getHour() + t.getStart().getMinute())));
				ret.setLayoutY(22 + (5.0 / 6.0) * (60 * t.getStart().getHour() + t.getStart().getMinute()));

			} else {
				background = new Rectangle(166.66, (5.0 / 6.0) * (60 * t.getEnd().getHour() + t.getEnd().getMinute()));
				ret.setLayoutY(30);
			}
		} else {
			background = new Rectangle(166.66, (5.0 / 6.0) * t.getDuration());
			ret.setLayoutY(30 + (5.0 / 6.0) * (60 * t.getStart().getHour() + t.getStart().getMinute()));
		}

		background.setFill(Color.ORANGE);
		background.setOpacity(.5);
		ret.setLayoutX(100);

		Label award = new Label("Award: " + task.getAward());
		Label penalty = new Label("Penalty: " + task.getPenalty());
		Label title = new Label(task.getTitle());
		Label desc = new Label(task.getDescription());

		HBox apcontainer = new HBox(15, award, penalty);
		award.setFont(new Font(12));
		penalty.setFont(new Font(12));

		title.setFont(new Font(18));
		desc.setFont(new Font(14));

		container.getChildren().addAll(apcontainer, title, desc);

		ret.getChildren().addAll(background, container);

		return ret;
	}
}
