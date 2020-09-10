package com.lifeorganizer360;

import java.util.Collections;
import java.util.Comparator;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

@SuppressWarnings("unchecked")
public class NotificationDashboard extends VBox {

	protected NotificationDashboard() {
		Button backBtn = new Button("<");

		backBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {
				Main.getPrimaryStage().getScene().setRoot(Main.getMainContainer());
			}
		});

		Label heading = new Label("Notification Dashboard");
		heading.setFont(new Font(25));

		HBox header = new HBox(15, backBtn, heading);
		ScrollPane notificationsScroll = new ScrollPane();
		VBox notificationsContainer = new VBox();
		notificationsScroll.setContent(notificationsContainer);

		Collections.sort(Main.getNotifications());

		for (Notification n : Main.getNotifications()) {
			notificationsContainer.getChildren().add(n.getPane());
		}

		getChildren().addAll(header, notificationsScroll);

	}

}
