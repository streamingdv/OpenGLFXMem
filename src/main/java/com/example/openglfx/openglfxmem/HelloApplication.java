package com.example.openglfx.openglfxmem;

import com.huskerdev.openglfx.canvas.GLCanvas;
import com.huskerdev.openglfx.canvas.GLProfile;
import com.huskerdev.openglfx.canvas.events.GLDisposeEvent;
import com.huskerdev.openglfx.canvas.events.GLInitializeEvent;
import com.huskerdev.openglfx.canvas.events.GLRenderEvent;
import com.huskerdev.openglfx.canvas.events.GLReshapeEvent;
import com.huskerdev.openglfx.lwjgl.LWJGLExecutor;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class HelloApplication extends Application {

    private StackPane root;

    private volatile int counter = 0;

    @Override
    public void start(final Stage stage) throws IOException {
        this.root = new StackPane();
        final Scene scene = new Scene(this.root, 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        this.root.getChildren().add(this.createOpenGLCanvas());
        System.out.println("Application started");

        new Thread(() -> {
            try {
                Thread.sleep(15_000); // add cheap hack delay
            } catch (InterruptedException e) {
                // ignore
            }
            Platform.runLater(() -> {
                System.out.println("Start mem test");

                final Timeline fiveSecondsWonder = new Timeline(
                        new KeyFrame(Duration.millis(1500),
                                event -> {
                                    final int currentCounter = this.counter;
                                    this.counter = currentCounter + 1;

                                    if(this.counter < 1000) {
                                        System.out.println("Create new OpenGLCanvas ----------------------");
                                        this.root.getChildren().forEach(node -> {
                                            if(node instanceof GLCanvas canvas){
                                                try {
                                                    canvas.dispose();
                                                } catch ( Exception e) {
                                                    // catch
                                                    System.out.println("!!! " + e);
                                                }

                                            }
                                        });
                                        this.root.getChildren().clear();
                                        this.root.getChildren().add(this.createOpenGLCanvas());
                                    }
                                }));
                fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);
                fiveSecondsWonder.play();
            });
        }).start();
    }

    public static void main(final String[] args) {
        Application.launch();
    }

    private void init(final GLInitializeEvent glInitializeEvent) {
        System.out.println("init for id " + this.counter);
    }

    private void rendering(final GLRenderEvent glRenderEvent) {
        System.out.println("rendering for id " + this.counter);
    }

    private void reshape(final GLReshapeEvent glReshapeEvent) {
        System.out.println("reshape for id " + this.counter);
    }

    private void dispose(final GLDisposeEvent glDisposeEvent) {
        System.out.println("dispose for id " + this.counter);
    }

    private GLCanvas createOpenGLCanvas() {
        final GLCanvas canvas = GLCanvas.create(LWJGLExecutor.LWJGL_MODULE, GLProfile.Core);
        canvas.addOnInitEvent(this::init);
        canvas.addOnReshapeEvent(this::reshape);
        canvas.addOnRenderEvent(this::rendering);
        canvas.addOnDisposeEvent(this::dispose);
        return canvas;
    }
}