package com.company.snake;

import java.util.LinkedList;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {
	
	private static final int WINDOW_SIZE = 500;
	private static final int BODY_WIDTH = 20;
	private static final double SPEED = 0.1;

	private static Circle food;
	private Rectangle snakeHead;
	private Rectangle snakeBody;

	private static final LinkedList<Rectangle> snakeBodyParts = new LinkedList<>();
	private static final LinkedList<Pair> currentCoordinatesList = new LinkedList<>();
	private final LinkedList<Pair> coordinatesHistoryList = new LinkedList<>();

	private boolean isUpEnabled = true;
	private boolean isDownEnabled = true;
	private boolean isRightEnabled = true;
	private boolean isLeftEnabled = false;

	private enum Direction {
		UP,
		DOWN,
		RIGHT,
		LEFT
	}

	private Direction dir = Direction.RIGHT;

	@Override
	public void start(Stage arg0) throws Exception {
		GridPane root = new GridPane();
		Timeline timeline = new Timeline();
		Scene scene = new Scene(root, WINDOW_SIZE, WINDOW_SIZE, Color.BLACK);

		snakeHead = new Rectangle(BODY_WIDTH, BODY_WIDTH);
		snakeHead.setFill(Color.LIMEGREEN);
		snakeBodyParts.add(snakeHead);

		snakeBody = new Rectangle(BODY_WIDTH, BODY_WIDTH);

		spawnFood(root);

		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent arg0) {
				if (arg0.getCode() == KeyCode.UP && isUpEnabled) {
					dir = Direction.UP;

					isDownEnabled = false;
					isRightEnabled = true;
					isLeftEnabled = true;
				} else if (arg0.getCode() == KeyCode.DOWN && isDownEnabled) {
					dir = Direction.DOWN;

					isUpEnabled = false;
					isRightEnabled = true;
					isLeftEnabled = true;
				} else if (arg0.getCode() == KeyCode.RIGHT && isRightEnabled) {
					dir = Direction.RIGHT;

					isUpEnabled = true;
					isDownEnabled = true;
					isLeftEnabled = false;
				} else if (arg0.getCode() == KeyCode.LEFT && isLeftEnabled) {
					dir = Direction.LEFT;

					isUpEnabled = true;
					isDownEnabled = true;
					isRightEnabled = false;
				}
			}
		});

		KeyFrame keyFrame = new KeyFrame(Duration.seconds(SPEED), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				switch (dir) {
					case UP -> {
						isDownEnabled = false;
						if (snakeHead.getTranslateY() <= 0) {
							snakeHead.setTranslateY(WINDOW_SIZE - BODY_WIDTH);
						} else {
							snakeHead.setTranslateY(snakeHead.getTranslateY() - BODY_WIDTH);
						}
					}
					case DOWN -> {
						isUpEnabled = false;
						if (snakeHead.getTranslateY() >= WINDOW_SIZE - BODY_WIDTH) {
							snakeHead.setTranslateY(0);
						} else {
							snakeHead.setTranslateY(snakeHead.getTranslateY() + BODY_WIDTH);
						}
					}
					case RIGHT -> {
						isLeftEnabled = false;
						if (snakeHead.getTranslateX() >= WINDOW_SIZE - BODY_WIDTH) {
							snakeHead.setTranslateX(0);
						} else {
							snakeHead.setTranslateX(snakeHead.getTranslateX() + BODY_WIDTH);
						}
					}
					case LEFT-> {
						isRightEnabled = false;
						if (snakeHead.getTranslateX() <= 0) {
							snakeHead.setTranslateX(WINDOW_SIZE - BODY_WIDTH);
						} else {
							snakeHead.setTranslateX(snakeHead.getTranslateX() - BODY_WIDTH);
						}
					}
                    default ->
                        throw new IllegalStateException("Unexpected value: " + dir);
                }

				coordinatesHistoryList.addFirst(new Pair(snakeHead.getTranslateX(), snakeHead.getTranslateY()));

				if (snakeHead.getBoundsInParent().intersects(food.getBoundsInParent())) {
					root.getChildren().remove(food);

					spawnFood(root);

					snakeBody = new Rectangle(BODY_WIDTH, BODY_WIDTH);
					snakeBody.setTranslateX(coordinatesHistoryList.get(snakeBodyParts.size()).x);
					snakeBody.setTranslateY(coordinatesHistoryList.get(snakeBodyParts.size()).y);
					snakeBody.setFill(Color.LIGHTGREEN);
					snakeBodyParts.add(snakeBody);

					root.getChildren().add(snakeBody);
				}

				if (snakeBody != null) {
					for (int i = 0; i < snakeBodyParts.size(); i++) {
						snakeBodyParts.get(i).setTranslateX(coordinatesHistoryList.get(i).x);
						snakeBodyParts.get(i).setTranslateY(coordinatesHistoryList.get(i).y);
					}
				}

				for (var item : snakeBodyParts) {
					currentCoordinatesList.add(new Pair(item.getTranslateX(), item.getTranslateY()));
				}
				currentCoordinatesList.remove(0);

				for (var item : currentCoordinatesList) {
					double posX = snakeHead.getTranslateX();
					double posY = snakeHead.getTranslateY();

					if (posX == item.x && posY == item.y) {
						int count = 0;

						currentCoordinatesList.clear();
						for (var item2 : snakeBodyParts) {
							if (count != 0) {
								root.getChildren().remove(item2);
							}

							count++;
						}

						snakeBodyParts.clear();
						snakeBodyParts.add(snakeHead);
					}
				}

				currentCoordinatesList.clear();
			}
		});

		root.getChildren().addAll(snakeHead);

		timeline.getKeyFrames().add(keyFrame);
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.playFromStart();

		arg0.setResizable(false);
		arg0.setTitle("Snake");
		arg0.setScene(scene);
		arg0.show();
	}

	public static void spawnFood(GridPane root) {
		Random rndX = new Random();
		Random rndY = new Random();

		int setPositionX;
		int setPositionY;
		boolean c = true;

		do {
			setPositionX = rndX.nextInt(WINDOW_SIZE);
			setPositionY = rndY.nextInt(WINDOW_SIZE);
		} while ((((setPositionX % BODY_WIDTH) != 0) || ((setPositionY % BODY_WIDTH) != 0)));

		food = new Circle((BODY_WIDTH / 2.0) - 1);
		food.setFill(Color.ALICEBLUE);
		food.setTranslateX(setPositionX + 1);
		food.setTranslateY(setPositionY);

		for (var item : snakeBodyParts) {
			if (item.getBoundsInParent().intersects(food.getBoundsInParent())) {
				c = false;
				spawnFood(root);
			}
		}

		if (c) {
			root.getChildren().add(food);
		}
	}

	public static class Pair {
		double x;
		double y;

		public Pair(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
