package omut.javafx.apps.controllers;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.PointLight;
import javafx.scene.AmbientLight;
import javafx.scene.text.Text;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class JavaFx3DController {

    @FXML
    private AnchorPane subScenePane;

    @FXML
    private Label helpLabel;

    private SubScene subScene;
    private Group worldRoot;
    private Group rotateGroup;
    private PerspectiveCamera camera;

    private Rotate xRotate = new Rotate(0, Rotate.X_AXIS);
    private Rotate yRotate = new Rotate(0, Rotate.Y_AXIS);

    private double mouseOldX, mouseOldY;
    private double cameraInitialZ = -2000;
    private double zoomSpeed = 40;
    private double wheelZoomFactor = 1;

    @FXML
    public void initialize() {
        worldRoot = new Group();
        rotateGroup = new Group();
        rotateGroup.getTransforms().addAll(xRotate, yRotate);
        worldRoot.getChildren().add(rotateGroup);

        createShapes();
        createAxes(1000, 1);
        addLights();

        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.setTranslateZ(cameraInitialZ);

        subScene = new SubScene(worldRoot, 800, 600, true, null);
        subScene.setFill(Color.web("#1e1e1e"));
        subScene.setCamera(camera);

        subScenePane.getChildren().add(subScene);
        AnchorPane.setTopAnchor(subScene, 0.0);
        AnchorPane.setBottomAnchor(subScene, 0.0);
        AnchorPane.setLeftAnchor(subScene, 0.0);
        AnchorPane.setRightAnchor(subScene, 0.0);


        subScene.addEventHandler(ScrollEvent.SCROLL, this::handleScrollZoom);
        subScene.setOnMousePressed(e -> {
            mouseOldX = e.getSceneX();
            mouseOldY = e.getSceneY();
        });

        subScene.setOnMouseDragged(e -> {
            double dx = e.getSceneX() - mouseOldX;
            double dy = e.getSceneY() - mouseOldY;
            yRotate.setAngle(yRotate.getAngle() + dx * 0.5);
            xRotate.setAngle(xRotate.getAngle() - dy * 0.5);
            mouseOldX = e.getSceneX();
            mouseOldY = e.getSceneY();
        });

        subScenePane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventHandler(KeyEvent.KEY_PRESSED, this::handleKeyPress);
            }
        });

        helpLabel.setText("Wheel: zoom | +- : zoom | Arrows / WASD : rotate");
    }

    private void createShapes() {

        Box box = new Box(150, 150, 150);
        PhongMaterial boxMat = new PhongMaterial(Color.CORNFLOWERBLUE);
        box.setMaterial(boxMat);
        box.setTranslateX(-200);

        Sphere sphere = new Sphere(90);
        PhongMaterial sphereMat = new PhongMaterial(Color.SALMON);
        sphere.setMaterial(sphereMat);
        sphere.setTranslateX(0);

        Cylinder cylinder = new Cylinder(50, 220);
        PhongMaterial cylMat = new PhongMaterial(Color.LIGHTGREEN);
        cylinder.setMaterial(cylMat);
        cylinder.setTranslateX(200);

        rotateGroup.getChildren().addAll(box, sphere, cylinder);
    }

    private void addLights() {
        AmbientLight ambient = new AmbientLight(Color.color(0.35, 0.35, 0.35));
        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateX(0);
        light.setTranslateY(-200);
        light.setTranslateZ(-500);

        worldRoot.getChildren().addAll(ambient, light);
    }

    private void createAxes(double length, double thickness) {
        Group axes = new Group();

        Box xAxis = new Box(length, thickness, thickness);
        PhongMaterial xMat = new PhongMaterial(Color.RED);
        xAxis.setMaterial(xMat);

        Box yAxis = new Box(thickness, length, thickness);
        PhongMaterial yMat = new PhongMaterial(Color.LIMEGREEN);
        yAxis.setMaterial(yMat);

        Box zAxis = new Box(thickness, thickness, length);
        PhongMaterial zMat = new PhongMaterial(Color.DODGERBLUE);
        zAxis.setMaterial(zMat);

        Text xLabel = new Text("X");
        xLabel.setFill(Color.RED);
        xLabel.setTranslateX(length / 2 + 10);

        Text yLabel = new Text("Y");
        yLabel.setFill(Color.LIMEGREEN);
        yLabel.setTranslateY(-length / 2 - 10);

        Text zLabel = new Text("Z");
        zLabel.setFill(Color.DODGERBLUE);
        zLabel.setTranslateZ(length / 2 + 10);

        axes.getChildren().addAll(xAxis, yAxis, zAxis, xLabel, yLabel, zLabel);

        rotateGroup.getChildren().add(axes);
    }

    private void handleScrollZoom(ScrollEvent e) {
        double delta = e.getDeltaY();
        double newZ = camera.getTranslateZ() + (delta > 0 ? wheelZoomFactor * zoomSpeed : -wheelZoomFactor * zoomSpeed);
        camera.setTranslateZ(clamp(newZ, -4000, -150));
    }

    private void handleKeyPress(KeyEvent e) {
        KeyCode code = e.getCode();
        switch (code) {
            case PLUS:
            case EQUALS:
            case ADD:
            case DIGIT6:
                camera.setTranslateZ(camera.getTranslateZ() + zoomSpeed);
                break;
            case MINUS:
            case SUBTRACT:
                camera.setTranslateZ(camera.getTranslateZ() - zoomSpeed);
                break;
            case LEFT:
            case A:
                yRotate.setAngle(yRotate.getAngle() - 10);
                break;
            case RIGHT:
            case D:
                yRotate.setAngle(yRotate.getAngle() + 10);
                break;
            case UP:
            case W:
                xRotate.setAngle(xRotate.getAngle() - 10);
                break;
            case DOWN:
            case S:
                xRotate.setAngle(xRotate.getAngle() + 10);
                break;
            case R:
                xRotate.setAngle(0);
                yRotate.setAngle(0);
                camera.setTranslateZ(cameraInitialZ);
                break;
            default:
                break;
        }
    }

    private double clamp(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }
}