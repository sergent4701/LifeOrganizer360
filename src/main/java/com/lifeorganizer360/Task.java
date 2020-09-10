package com.lifeorganizer360;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.neo4j.ogm.annotation.*;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.control.ScrollPane;
import javafx.scene.shape.Line;

@SuppressWarnings("restriction")
@NodeEntity
public class Task extends TaskBase {
	public static Color[] colors = new Color[] { Color.AQUA, Color.BLUE, Color.BROWN, Color.CADETBLUE, Color.CORAL,
			Color.DARKBLUE, Color.DARKCYAN, Color.DARKORCHID, Color.DEEPPINK, Color.MEDIUMVIOLETRED };

	@Property
	private double award, penalty;

	@Property
	private int color;

	@Transient
	private ArrayList<Node> receededListItems = new ArrayList<Node>();

	@Relationship(type = "TICKET", direction = Relationship.OUTGOING)
	private ArrayList<WorkTicket> tickets = new ArrayList<WorkTicket>();

	@Relationship(type = "HANDLE_COMPLETION_NOTIFICATION", direction = Relationship.OUTGOING)
	private Notification handleCompletionNotification;

	protected Task() {
		super();
	}

	protected Task(String title, String description, double award, double penalty, double xPos, double yPos, Color c) {
		super(title, description, xPos, yPos);
		this.award = award;
		this.penalty = penalty;
		setColor(c);
		save();
	}

	protected Task(String title, String description, double award, double penalty, Color c) {
		super(title, description, 0, 0);
		this.award = award;
		this.penalty = penalty;
		setHidden(true);
		setColor(c);
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

			Circle topCircle = new Circle(4);
			Circle bottomCircle = new Circle(4);

			topCircle.setFill(Color.WHITE);
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

			topBar.getChildren().addAll(topCircle, edit);

			topCircle.setLayoutX(75);
			topCircle.setLayoutY(7.5);
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
			topCircle.setOnMouseEntered(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent mouseEvent) {
					if (Main.getDependencyInit())
						topCircle.setCursor(Cursor.HAND);
				}
			});
			topCircle.setOnMouseExited(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent mouseEvent) {
					topCircle.setCursor(Cursor.OPEN_HAND);
				}
			});
			topCircle.setOnMousePressed(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent mouseEvent) {
					if (Main.getDependencyInit()) {
						Main.getWorkspace().setCursor(Cursor.DEFAULT);
						Main.getDependencyParent().dependsOn((Task) e);

						Line dependency = new Line();

						dependency.setStrokeWidth(3);
						dependency.setStroke(Color.BLACK);
						Main.getWorkspace().getChildren().add(dependency);
						Main.getDependencyParent().addStartLine(dependency);
						e.addEndLine(dependency);

						Main.setDependencyInit(false);
					}
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
					bottomBar.getChildren().add(bottomCircle);
				}
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
			Main.setBackgroundColor(Color.GREEN, topBar);

			ret.setLayoutX(e.getX());
			ret.setLayoutY(e.getY());

			ret.setPrefWidth(150);
			setPane(ret);
			return ret;
		} else {
			return super.getWorkspacePane();
		}
	}

	public double getAward() {
		return award;
	}

	public double getPenalty() {
		return penalty;
	}

	public void setAward(double a) {
		award = a;
		save();
	}

	public void setPenalty(double p) {
		penalty = p;
		save();
	}

	public Node generateReceededListItem() {
		Label title = new Label(getTitle());
		receededListItems.add(title);
		return title;
	}

	public ArrayList<Node> getReceededListItems() {
		return receededListItems;
	}

	public Node generateDropdownListItem(TaskBase parent) {
		return generateDropdownListItem(parent, 0);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Node generateDropdownListItem(TaskBase parent, int level) {
		Task t = this;

		double masterOffset = 10;

		VBox container = new VBox(3);
		Pane freePane = new Pane();
		HBox taskContent = new HBox();

		VBox[] cols = new VBox[3];
		cols[0] = new VBox();
		cols[1] = new VBox();
		cols[2] = new VBox();

		Label title = new Label(getTitle());
		Label description = new Label(getDescription());
		Label award = new Label(getAward() + "");
		Label penalty = new Label(getPenalty() + "");
		Label start = new Label();
		Label end = new Label();

		cols[0].getChildren().addAll(title, description);
		cols[1].getChildren().addAll(award, penalty);
		cols[2].getChildren().addAll(start, end);

		Button delBtn = new Button("X");
		delBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {
				parent.doesntDependOn(t);
				((VBox) container.getParent()).getChildren().remove(container);

				Iterator<Line> iter = parent.getStartLines().iterator();
				Iterator<Line> iter2 = t.getEndLines().iterator();
				((VBox) parent.getReceededList().getContent()).getChildren()
						.removeAll(((Task) t).getReceededListItems());

				if (parent.isReceeded() && t.getDepedenciesOf().isEmpty()) {
					t.setHidden(false);
				}

				while (iter.hasNext()) {
					Line start = iter.next();
					while (iter2.hasNext()) {
						if (iter2.next() == start) {
							iter.remove();
							iter2.remove();
							Main.getWorkspace().getChildren().remove(start);
						}
					}
				}

			}
		});

		Main.setBackgroundColor(Color.RED, delBtn);
		Button dropBtn = new Button("ˇ");

		freePane.getChildren().addAll(taskContent, delBtn, dropBtn);

		taskContent.setLayoutX(masterOffset + level * 30);
		taskContent.setLayoutY(10);

		taskContent.setCursor(Cursor.HAND);

		taskContent.setOnMouseClicked(new EventHandler() {
			public void handle(Event event) {
				Main.getPrimaryStage().getScene().setRoot(getProfilePane());
			}
		});

		delBtn.setLayoutX(520);
		delBtn.setLayoutY(10);

		dropBtn.setLayoutX(560);
		dropBtn.setLayoutY(10);

		boolean[] clicked = new boolean[1];
		clicked[0] = false;

		dropBtn.setOnAction(new EventHandler() {
			public void handle(Event event) {

				for (Task d : getDependencies()) {
					if (!clicked[0]) {
						Node n = d.generateDropdownListItem(t, level + 1);
						n.managedProperty().bind(n.visibleProperty());
						container.getChildren().add(n);
						dropBtn.setText("^");
					} else {
						if (dropBtn.getText().equals("ˇ")) {
							dropBtn.setText("^");
							for (int i = 1; i < container.getChildren().size(); i++) {
								container.getChildren().get(i).setVisible(true);
							}
						} else {
							dropBtn.setText("ˇ");
							for (int i = 1; i < container.getChildren().size(); i++) {
								container.getChildren().get(i).setVisible(false);
							}
						}
					}
				}
				clicked[0] = true;
			}
		});

		taskContent.getChildren().addAll(cols[0], cols[1], cols[2]);

		Main.setBackgroundColor(Color.GRAY, freePane);

		freePane.setPrefWidth(600);

		container.getChildren().addAll(freePane);

		return container;
	}

	public void setHandleCompletionNotification(boolean b) {
		if (handleCompletionNotification == null)
			handleCompletionNotification = new HandleCompletionNotification(this);
		if (!isComplete() && b) {
			Main.addNotification(handleCompletionNotification);
		} else if (!b) {
			Main.deleteNotification(handleCompletionNotification);
		}
		save();
	}

	public ArrayList<WorkTicket> getTickets() {
		return tickets;
	}

	public void addTicket(WorkTicket t) {
		tickets.add(t);
		setHandleCompletionNotification(false);
		save();
	}

	public void deleteTicket(WorkTicket t) {
		tickets.remove(t);
		setHandleCompletionNotification(sendCompletionRequest());
		save();
	}

	public boolean sendCompletionRequest() {
		boolean notify = true;

		for (WorkTicket w : getTickets()) {

			if (w instanceof RecurringTicket)
				for (WorkTicket sub : ((RecurringTicket) w).getTickets()) {
					if (sub.getStatus().equals(WorkTicket.INPROGRESS))
						notify = false;
				}
			else if (w.getStatus().equals(WorkTicket.INPROGRESS))
				notify = false;

		}
		return notify;
	}

	public Color getColor() {
		return colors[color];
	}

	public void setColor(Color c) {
		for (int i = 0; i < colors.length; i++) {
			if (colors[i].equals(c))
				color = i;
			save();
		}
	}

	public Notification getHandleCompletionNotification() {
		return handleCompletionNotification;
	}
}
