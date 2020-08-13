package com.lifeorganizer360;

import org.neo4j.driver.AuthTokens;
import org.neo4j.ogm.driver.Driver;
import org.neo4j.ogm.drivers.bolt.driver.BoltDriver;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.ogm.session.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;

import static org.neo4j.driver.Values.parameters;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.control.ScrollPane;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

@SuppressWarnings("restriction")
public class Main extends Application {

	private static final Pane workspace = new Pane();
	private static Stage primaryStage = null;
	private static VBox mainContainer = new VBox();

	private static ArrayList<TaskBase> entities;
	private static boolean dependencyInit = false;
	private static TaskBase dependencyParent = null;

	private double toolBarHeight = 80;
	private double startingWindowWidth = 1000;
	private double startingWindowHeight = 688;
	private double minimumWindowWidth = 450;
	private double minimumWindowHeight = 350;

	private static org.neo4j.driver.Driver nativeDriver = null;
	private static Driver ogmDriver = null;
	private static SessionFactory sessionFactory = null;
	private static Session session = null;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void start(final Stage p) {
		primaryStage = p;

		final HBox toolBar = new HBox(5);
		VBox logoandgreeting = new VBox(5);

		Label logo = new Label("Life Organizer 360©");
		logo.setFont(new Font(25));

		Label greeting = new Label("Welcome Srdjan Bozin!");
		greeting.setFont(new Font(18));

		Button addBtn = new Button("+");

		workspace.setMinWidth(startingWindowWidth);
		workspace.setMinHeight(startingWindowHeight - toolBarHeight);

		toolBar.setMinWidth(startingWindowWidth);
		toolBar.setMaxWidth(startingWindowWidth);
		toolBar.setMinHeight(toolBarHeight);
		toolBar.setMaxHeight(toolBarHeight);

		BackgroundFill workspaceBackgroundFill = new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY);
		Background workspaceBackground = new Background(workspaceBackgroundFill);
		workspace.setBackground(workspaceBackground);

		BackgroundFill toolBarBackgroundFill = new BackgroundFill(Color.ORANGE, CornerRadii.EMPTY, Insets.EMPTY);
		Background toolBarBackground = new Background(toolBarBackgroundFill);
		toolBar.setBackground(toolBarBackground);

		for (TaskBase e : entities) {
			workspace.getChildren().add(e.getPane());
		}
		for (TaskBase e : entities) {
			for (TaskBase d : e.getDependencies()) {
				Arrow dependency = new Arrow(e.getX() + 75, e.getY() + 93, d.getX() + 73, d.getY() + 8);
				dependency.setStrokeWidth(3);
				dependency.setStroke(Color.BLACK);
				workspace.getChildren().add(dependency.getLines()[0]);
				workspace.getChildren().add(dependency.getLines()[1]);
				workspace.getChildren().add(dependency.getLines()[2]);
				e.getStartArrows().add(dependency);
				d.getEndArrows().add(dependency);

			}
		}

		logoandgreeting.getChildren().add(logo);
		logoandgreeting.getChildren().add(greeting);

		toolBar.getChildren().add(logoandgreeting);
		toolBar.getChildren().add(addBtn);

		ScrollPane scroll = new ScrollPane();
		scroll.setContent(workspace);

		scroll.setPrefWidth(startingWindowWidth);
		scroll.setPrefHeight(startingWindowHeight - toolBarHeight);

		mainContainer.getChildren().add(toolBar);
		mainContainer.getChildren().add(scroll);

		Scene scene = new Scene(mainContainer, startingWindowWidth, startingWindowHeight);

		addBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {
				scene.setRoot(generateTaskForm());
			}
		});

		scene.widthProperty().addListener(new ChangeListener() {
			public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				workspace.setMinWidth((Double) newValue);
				toolBar.setMinWidth((Double) newValue);
				toolBar.setMaxWidth((Double) newValue);
			}

		});

		scene.heightProperty().addListener(new ChangeListener() {
			public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				workspace.setMinHeight((Double) newValue - 80);
			}

		});

		primaryStage.setMinWidth(minimumWindowWidth);
		primaryStage.setMinHeight(minimumWindowHeight);
		primaryStage.setTitle("Life Organizer 360©");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest(new EventHandler() {
			public void handle(Event event) {
				nativeDriver.close();
				ogmDriver.close();
			}
		});
	}

	public static org.neo4j.driver.Driver connect(String uri, String user, String password) {
		return GraphDatabase.driver(uri, AuthTokens.basic(user, password));
	}

	public static Driver getOGMDriver() {
		if (ogmDriver == null) {
			nativeDriver = connect("bolt://localhost:7687", "neo4j", "LifeOrganizer360");
			return new BoltDriver(nativeDriver);
		}
		return ogmDriver;
	}

	public static void main(String... args) throws Exception {
		ogmDriver = getOGMDriver();
		sessionFactory = new SessionFactory(ogmDriver, "com.lifeorganizer360");
		session = sessionFactory.openSession();

		loadNodes();

//		Session session = sessionFactory.openSession();

//		for (int i = 0; i < 10; i++) {
//			Goal goal = new Goal("TestTitle" + i, "TestDescription" + i, i * 10, i * 10);
//			session.save(goal);
//		}

//		Session session = driver.session();
//		Result result = session.run("CREATE (n:Doggie) RETURN n");
//		System.out.print(result.single());
//		close();
		launch(args);
	}

	public static Session getSession() {
		return session;
	}

	public static void loadNodes() {
		entities = new ArrayList<TaskBase>(session.loadAll(TaskBase.class, -1));
	}

	public static void setDependencyInit(boolean b) {
		dependencyInit = b;
	}

	public static Pane getWorkspace() {
		return workspace;
	}

	public static void setDependencyParent(TaskBase e) {
		dependencyParent = e;
	}

	public static void setBackgroundColor(Color c, Region r) {
		BackgroundFill fill = new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY);
		Background background = new Background(fill);
		r.setBackground(background);
	}

	public static boolean getDependencyInit() {
		return dependencyInit;
	}

	public static TaskBase getDependencyParent() {
		return dependencyParent;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Parent generateTaskForm(final TaskBase t) {
		final String[] old = new String[1];
		old[0] = null;
		VBox taskForm = new VBox(10);

		Button backBtn = new Button("<--");

		final ComboBox<String> dropdown = new ComboBox<String>();
		dropdown.setPromptText("Select Type");
		dropdown.getItems().add("Goal");
		dropdown.getItems().add("Task");

		Label titleL = new Label("Title:");
		final TextField titleF = new TextField();
		VBox titleV = new VBox(3);
		titleV.getChildren().add(titleL);
		titleV.getChildren().add(titleF);
		titleF.setMaxWidth(580);

		Label descL = new Label("Description:");
		final TextArea descF = new TextArea();
		VBox descV = new VBox(3);
		descV.getChildren().add(descL);
		descV.getChildren().add(descF);
		descF.setMaxWidth(580);

		Button submitBtn = new Button("Submit");

		taskForm.getChildren().add(backBtn);
		taskForm.getChildren().add(dropdown);
		taskForm.getChildren().add(titleV);
		taskForm.getChildren().add(descV);
		taskForm.getChildren().add(submitBtn);

		if (t != null) {
			titleF.setText(t.getTitle());
			descF.setText(t.getDescription());
			if (t instanceof Goal) {
				old[0] = "Goal";
			} else if (t instanceof Task) {
				old[0] = "Task";
			}
			dropdown.setValue(old[0]);
			Button delBtn = new Button("Delete");
			delBtn.setOnAction(new EventHandler() {
				public void handle(Event event) {
					primaryStage.getScene().setRoot(mainContainer);
					workspace.getChildren().remove(t.getPane());
					session.delete(session.load(TaskBase.class, t.getId()));
					entities.remove(t);
				}
			});
			taskForm.getChildren().add(delBtn);

		}
		backBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {
				primaryStage.getScene().setRoot(mainContainer);
			}
		});

		submitBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {
				primaryStage.getScene().setRoot(mainContainer);

				if (t == null) {
					workspace.setCursor(Cursor.CROSSHAIR);

					final EventHandler filter = new EventHandler<MouseEvent>() {
						public void handle(MouseEvent e) {
							workspace.setCursor(Cursor.DEFAULT);
							TaskBase x = t;
							switch (dropdown.getValue()) {
							case "Goal":
								x = new Goal(titleF.getText(), descF.getText(), e.getX(), e.getY());
								break;
							case "Task":
								x = new Task(titleF.getText(), descF.getText(), e.getX(), e.getY());
								break;
							}
							entities.add(x);
							workspace.getChildren().add(x.getPane());
							session.save(x);
							workspace.removeEventFilter(MouseEvent.MOUSE_CLICKED, this);

						}

					};

					workspace.addEventFilter(MouseEvent.MOUSE_CLICKED, filter);
				} else if (!old[0].equals(dropdown.getValue())) {
					TaskBase x = null;
					switch (dropdown.getValue()) {
					case "Goal":
						x = new Goal(titleF.getText(), descF.getText(), t.getX(), t.getY());
						break;
					case "Task":
						x = new Task(titleF.getText(), descF.getText(), t.getX(), t.getY());
						break;
					}
					entities.add(x);
					workspace.getChildren().add(x.getPane());
					session.save(x);
					workspace.getChildren().remove(t.getPane());
					session.delete(session.load(TaskBase.class, t.getId()));
					entities.remove(t);
				} else {
					t.setTitle(titleF.getText());
					t.setDescription(descF.getText());
					session.save(t);
				}

			}
		});
		return taskForm;
	}

	public static Parent generateTaskForm() {
		return generateTaskForm(null);
	}

	public static Stage getPrimaryStage() {
		return primaryStage;
	}

}
