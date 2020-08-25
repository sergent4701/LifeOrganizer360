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
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import tornadofx.control.DateTimePicker;
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
import javafx.scene.shape.Line;

@SuppressWarnings("restriction")
public class Main extends Application {

	private static final Pane workspace = new Pane();
	private static Stage primaryStage = null;
	private static VBox workspaceContainer = new VBox();
	private static VBox mainContainer = new VBox();

	private static ArrayList<TaskBase> entities;
	private static boolean dependencyInit = false;
	private static TaskBase dependencyParent = null;

	private static double toolBarHeight = 80;
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
		Button startBtn = new Button("Start");
		mainContainer.getChildren().add(startBtn);

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

		logoandgreeting.getChildren().add(logo);
		logoandgreeting.getChildren().add(greeting);

		toolBar.getChildren().add(logoandgreeting);
		toolBar.getChildren().add(addBtn);

		ScrollPane scroll = new ScrollPane();
		scroll.setContent(workspace);

		scroll.setPrefWidth(startingWindowWidth);
		scroll.setPrefHeight(startingWindowHeight - toolBarHeight);

		workspaceContainer.getChildren().add(toolBar);
		workspaceContainer.getChildren().add(scroll);

		Scene scene = new Scene(mainContainer, startingWindowWidth, startingWindowHeight);

		addBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {
				scene.setRoot(new CreateTaskForm(CreateTaskForm.WORKSPACE));
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
		startBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {
				scene.setRoot(workspaceContainer);
				for (TaskBase e : entities) {
					if (!e.isHidden())
						workspace.getChildren().add(e.getWorkspacePane());
				}
				for (TaskBase e : entities) {
					for (TaskBase d : e.getDependencies()) {

						Line dependency = new Line();
						dependency.setStrokeWidth(3);
						dependency.setStroke(Color.BLACK);
						if (!e.isHidden() && !e.isReceeded())
							workspace.getChildren().add(dependency);
						e.addStartLine(dependency);
						d.addEndLine(dependency);

					}
				}
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

	public static VBox getWorkspaceContainer() {
		return workspaceContainer;
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

	public static double getToolBarHeight() {
		return toolBarHeight;
	}

	public static Stage getPrimaryStage() {
		return primaryStage;
	}

	public static ArrayList<TaskBase> getEntities() {
		return entities;
	}

}
