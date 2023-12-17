package com.example.snake;

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
import javafx.util.Pair;

import java.util.LinkedList;
import java.util.Random;

public class Main extends Application {

    private static final int WINDOW_SIZE = 500;
    private static final int BODY_WIDTH = 20;
    private static final double SPEED = 0.1;

    private static Circle food;

    private Rectangle snakehead;
    private Rectangle snakebody;

    private static final LinkedList<Rectangle> snakebodyList = new LinkedList<>();
    private final LinkedList<Pair<Double, Double>> snakeBodyListHistory = new LinkedList<>();

    private enum Direction {
        UP,
        DOWN,
        RIGHT,
        LEFT
    }
    private Direction dir = Direction.RIGHT;

    @Override
    public void start(Stage stage) {
        GridPane root = new GridPane();
        Timeline timeline = new Timeline();
        Scene scene = new Scene(root, WINDOW_SIZE, WINDOW_SIZE, Color.BLACK);

        snakehead = new Rectangle(BODY_WIDTH, BODY_WIDTH);
        snakehead.setFill(Color.LIMEGREEN);
        snakebodyList.add(snakehead);

        spawnFood(root);

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.UP && dir != Direction.DOWN) {
                    dir = Direction.UP;
                } else if (keyEvent.getCode() == KeyCode.DOWN && dir != Direction.UP) {
                    dir = Direction.DOWN;
                } else if (keyEvent.getCode() == KeyCode.RIGHT &&  dir != Direction.LEFT) {
                    dir = Direction.RIGHT;
                } else if (keyEvent.getCode() == KeyCode.LEFT &&  dir != Direction.RIGHT) {
                    dir = Direction.LEFT;
                }
            }
        });

        KeyFrame keyFrame = new KeyFrame(Duration.seconds(SPEED), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                switch (dir) {
                    case UP -> {
                        if (snakehead.getTranslateY() <= 0) {
                            snakehead.setTranslateY(WINDOW_SIZE - BODY_WIDTH);
                        } else {
                            snakehead.setTranslateY(snakehead.getTranslateY() - BODY_WIDTH);
                        }
                    }
                    case DOWN -> {
                        if (snakehead.getTranslateY() >= WINDOW_SIZE - BODY_WIDTH) {
                            snakehead.setTranslateY(0);
                        } else {
                            snakehead.setTranslateY(snakehead.getTranslateY() + BODY_WIDTH);
                        }
                    }
                    case RIGHT -> {
                        if (snakehead.getTranslateX() >= WINDOW_SIZE - BODY_WIDTH) {
                            snakehead.setTranslateX(0);
                        } else {
                            snakehead.setTranslateX(snakehead.getTranslateX() + BODY_WIDTH);
                        }
                    }
                    case LEFT -> {
                        if (snakehead.getTranslateX() <= 0) {
                            snakehead.setTranslateX(WINDOW_SIZE - BODY_WIDTH);
                        } else {
                            snakehead.setTranslateX(snakehead.getTranslateX() - BODY_WIDTH);
                        }
                    }
                }

                snakeBodyListHistory.addFirst(new Pair<>(snakehead.getTranslateX(), snakehead.getTranslateY()));

                if (snakeBodyListHistory.size() > 1) snakeBodyListHistory.subList(snakebodyList.size() + 1, snakeBodyListHistory.size()).clear();

                if (snakehead.getBoundsInParent().intersects(food.getBoundsInParent())) {
                    root.getChildren().remove(food);

                    spawnFood(root);

                    snakebody = new Rectangle(BODY_WIDTH, BODY_WIDTH);
                    snakebody.setTranslateX(snakeBodyListHistory.get(snakebodyList.size()).getKey());
                    snakebody.setTranslateY(snakeBodyListHistory.get(snakebodyList.size()).getValue());
                    snakebody.setFill(Color.LIGHTGREEN);
                    snakebodyList.add(snakebody);

                    root.getChildren().add(snakebody);
                }

                if (snakebody != null) {
                    for (int i = 0; i < snakebodyList.size(); i++) {
                        snakebodyList.get(i).setTranslateX(snakeBodyListHistory.get(i).getKey());
                        snakebodyList.get(i).setTranslateY(snakeBodyListHistory.get(i).getValue());
                    }
                }

                for (var x : snakebodyList) {
                    if (x == snakebodyList.get(0)) continue;

                    if (snakehead.getTranslateX() == x.getTranslateX() && snakehead.getTranslateY() == x.getTranslateY()) {
                        for (var y : snakebodyList) {
                            if (y == snakebodyList.get(0)) continue;

                            root.getChildren().remove(y);
                        }

                        snakebodyList.clear();
                        snakeBodyListHistory.clear();

                        snakebodyList.add(snakehead);
                    }
                }
            }
        });

        root.getChildren().add(snakehead);

        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.playFromStart();

        stage.setResizable(false);
        stage.setTitle("Snake");
        stage.setScene(scene);
        stage.show();
    }

    public static void spawnFood(GridPane root) {
        Random rndX = new Random();
        Random rndY = new Random();

        int setPositionX;
        int setPositionY;

        do {
            setPositionX = rndX.nextInt(WINDOW_SIZE);
            setPositionY = rndY.nextInt(WINDOW_SIZE);
        } while ((((setPositionX % BODY_WIDTH) != 0) || ((setPositionY % BODY_WIDTH) != 0)));

        food = new Circle((BODY_WIDTH / 2.0) - 1);
        food.setFill(Color.ALICEBLUE);
        food.setTranslateX(setPositionX + 1);
        food.setTranslateY(setPositionY);

        for (var x : snakebodyList) {
            if (x.getBoundsInParent().intersects(food.getBoundsInParent())) {
                spawnFood(root);
                return;
            }
        }

        root.getChildren().add(food);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
