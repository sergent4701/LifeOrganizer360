package com.lifeorganizer360;

import java.util.ArrayList;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

@SuppressWarnings("unchecked")
public class TaskList extends VBox {
	protected TaskList() {
		Button backBtn = new Button("<");

		backBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {
				Main.getPrimaryStage().getScene().setRoot(Main.getMainContainer());
			}
		});

		Label heading = new Label("Task List");
		heading.setFont(new Font(25));

		HBox header = new HBox(15, backBtn, heading);

		ScrollPane scroll = new ScrollPane();

		Label searchL = new Label("Search:");
		TextField searchF = new TextField();
		VBox search = new VBox(3, searchL, searchF);
		search.setMaxWidth(387);

		VBox results = new VBox(3);

		ArrayList<HBox> tasks = new ArrayList();

		for (TaskBase e : Main.getEntities()) {
			HBox temp = new HBox();

			VBox[] cols = new VBox[2];
			cols[0] = new VBox();
			cols[1] = new VBox();

			Label title = new Label(e.getTitle());
			Label description = new Label(e.getDescription());
			cols[0].getChildren().addAll(title, description);

			temp.getChildren().add(cols[0]);

			Main.setBackgroundColor(Color.GRAY, temp);

			temp.setPrefWidth(600);

			temp.setOnMouseClicked(new EventHandler() {
				public void handle(Event event) {
					Main.getPrimaryStage().getScene().setRoot(e.getProfilePane());
				}
			});

			searchF.textProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue.equals("")) {
					results.getChildren().clear();
					results.getChildren().add(search);
					results.getChildren().addAll(tasks);
				} else {
					for (HBox r : tasks) {
						if (((Label) ((VBox) r.getChildren().get(0)).getChildren().get(0)).getText().toLowerCase()
								.contains(newValue.toLowerCase())
								|| ((Label) ((VBox) r.getChildren().get(0)).getChildren().get(1)).getText()
										.toLowerCase().contains(newValue.toLowerCase())) {
							if (!results.getChildren().contains(r)) {
								results.getChildren().add(r);
							}
						} else {
							results.getChildren().remove(r);
						}
					}
				}
			});
			temp.setCursor(Cursor.HAND);
			results.getChildren().add(temp);
			tasks.add(temp);

			if (e instanceof Task) {
				Task t = (Task) e;

				Label award = new Label(t.getAward() + "");
				Label penalty = new Label(t.getPenalty() + "");

				cols[1].getChildren().addAll(award, penalty);

				temp.getChildren().add(cols[1]);
			}

		}

		VBox content = new VBox(5, search, results);
		scroll.setContent(content);

		getChildren().addAll(header, scroll);
	}

}
