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

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.util.Duration;
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
import javafx.scene.layout.BorderPane;
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
	private static HBox mainContainer = new HBox();
	private static Button notificationsBtn = new Button();
	private static Label greeting;

	private static ArrayList<TaskBase> entities;
	private static ArrayList<Saveable> nodes;
	private static ArrayList<Notification> notifications = new ArrayList<Notification>();
	private static ArrayList<WorkTicket> tickets;
	private static Account user;
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

		Button workspaceBtn = new Button("Workspace");
		oneMinFunction();
		updateNotifications();

		VBox functionalBtns = new VBox(3, workspaceBtn, notificationsBtn);

		Button dailyBtn = new Button("Daily");
		Button weeklyBtn = new Button("Weekly");
		Button monthlyBtn = new Button("Monthly");
		Button yearlyBtn = new Button("Yearly");
		VBox calendarBtns = new VBox(3, dailyBtn, weeklyBtn, monthlyBtn, yearlyBtn);

		HBox menu = new HBox(5, calendarBtns, functionalBtns);

		Button backBtn = new Button("<");

		mainContainer.getChildren().add(menu);
		mainContainer.setAlignment(Pos.CENTER);

		primaryStage = p;

		final HBox toolBar = new HBox(5);
		VBox logoandgreeting = new VBox(5);

		Label logo = new Label("Life Organizer 360©");
		logo.setFont(new Font(25));

		greeting = new Label();
		greeting.setFont(new Font(18));
		updateBalance();

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

		toolBar.getChildren().addAll(logoandgreeting, addBtn, backBtn);

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
		backBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {
				scene.setRoot(mainContainer);
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
		workspaceBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {
				scene.setRoot(workspaceContainer);
				for (TaskBase e : entities) {
					if (!e.isHidden() && !workspace.getChildren().contains(e.getWorkspacePane()))
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
		notificationsBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {
				scene.setRoot(new NotificationDashboard());
			}
		});
		dailyBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {
				scene.setRoot(new DailyCalendar(LocalDate.now()));
			}
		});
		weeklyBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {
				scene.setRoot(new WeeklyCalendar(LocalDate.now()));
			}
		});
		monthlyBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {
				scene.setRoot(new MonthlyCalendar(LocalDate.now()));
			}
		});
		yearlyBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {
				scene.setRoot(new YearlyCalendar(LocalDate.now()));
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

		nodes = new ArrayList<Saveable>(session.loadAll(Saveable.class, 3));

		entities = new ArrayList<TaskBase>();
		tickets = new ArrayList<WorkTicket>();
		for (Saveable s : nodes)
			if (s instanceof TaskBase)
				entities.add((TaskBase) s);
			else if (s instanceof Notification) {
				notifications.add((Notification) s);
			} else if (s instanceof WorkTicket && ((WorkTicket) s).getTask() != null)
				tickets.add((WorkTicket) s);
			else if (s instanceof Account)
				user = (Account) s;
		if (user == null)
			user = new Account("Srdjan", "Bozin");

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

	public static ArrayList<Notification> getNotifications() {
		return notifications;
	}

	public static ArrayList<WorkTicket> getTickets() {
		return tickets;
	}

	public static void addTicket(WorkTicket t) {
		tickets.add(t);
	}

	public static void addNotification(Notification n) {
		if (!notifications.contains(n))
			notifications.add(n);
		updateNotifications();
	}

	public static void deleteNotification(Notification n) {
		notifications.remove(n);
		updateNotifications();
	}

	public static void deleteTicket(WorkTicket t) {
		tickets.remove(t);
	}

	public static HBox getMainContainer() {
		return mainContainer;
	}

	private void oneMinFunction() {

		for (WorkTicket t : tickets) {
			if (t instanceof RecurringTicket) {
				RecurringTicket r = (RecurringTicket) t;
				for (WorkTicket sub : r.getTickets()) {
					if (sub.getStatus().equals(WorkTicket.INPROGRESS)
							&& sub.getEnd().compareTo(LocalDateTime.now()) < 0) {
						sub.setStatus(WorkTicket.REQUIRESACTION, r.getTask());
						sub.setEndNotification(r.getTask());
					} else if (sub.getStatus().equals(WorkTicket.INPROGRESS)
							&& sub.getStart().compareTo(LocalDateTime.now()) < 0) {
						sub.setStartNotification(r.getTask());
					}
				}

			} else if (t.getStatus().equals(WorkTicket.INPROGRESS) && t.getEnd().compareTo(LocalDateTime.now()) < 0) {
				t.setStatus(WorkTicket.REQUIRESACTION, t.getTask());
				t.setEndNotification();
			} else if (t.getStatus().equals(WorkTicket.INPROGRESS) && t.getStart().compareTo(LocalDateTime.now()) < 0) {
				t.setStartNotification();
			}
		}

		Timeline oneMinTimer = new Timeline(
				new KeyFrame(Duration.millis(60000 - System.currentTimeMillis() % 60000), ae -> oneMinFunction()));
		oneMinTimer.play();

	}

	public static Account getUser() {
		return user;
	}

	public static void updateNotifications() {
		int i = getNumberOfActive();
		notificationsBtn.setText("Notifications " + (i > 0 ? "(" + i + ")" : ""));

	}

	public static int getNumberOfActive() {
		int i = 0;
		for (Notification n : notifications) {
			if (n.isAlert())
				i++;
		}
		return i;
	}

	public static void updateBalance() {
		DecimalFormat df = new DecimalFormat("#0.00");

		greeting.setText("Welcome " + user.getFirstName() + " " + user.getLastName() + ", you have $"
				+ df.format(user.getBalance()) + " in your account!");
	}

}
