package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;

public class Main extends Application {

    private TextField bustField, waistField, hipsField, torsoField, seatHeightField, backWidthField;
    private TextField armholeDepthField, shoulderLengthField, neckCircField;
    private TextField sleeveLengthField, upperArmCircField, wristCircField;
    private TextField frontSkirtLengthField, backSkirtLengthField;

    private Canvas canvas;
    private PatternBuilder builder;

    @Override
    public void start(Stage primaryStage) {
        builder = new PatternBuilder();

        GridPane inputGrid = new GridPane();
        inputGrid.setPadding(new Insets(10));
        inputGrid.setHgap(10);
        inputGrid.setVgap(6);

        bustField = addMeasurementField(inputGrid, "Обхват груди (см):", 0);
        waistField = addMeasurementField(inputGrid, "Обхват талии (см):", 1);
        hipsField = addMeasurementField(inputGrid, "Обхват бёдер (см):", 2);
        torsoField = addMeasurementField(inputGrid, "Длина туловища (плечо-талия), см:", 3);
        seatHeightField = addMeasurementField(inputGrid, "Высота сидения (талия-ластовица), см:", 4);
        backWidthField = addMeasurementField(inputGrid, "Ширина спины (см):", 5);
        armholeDepthField = addMeasurementField(inputGrid, "Глубина проймы (см):", 6);
        shoulderLengthField = addMeasurementField(inputGrid, "Длина плеча (см):", 7);
        neckCircField = addMeasurementField(inputGrid, "Обхват шеи (см):", 8);

        sleeveLengthField = addMeasurementField(inputGrid, "Длина рукава (см):", 9);
        upperArmCircField = addMeasurementField(inputGrid, "Обхват плеча (Оп, см):", 10);
        wristCircField = addMeasurementField(inputGrid, "Обхват запястья (Оз, см):", 11);

        frontSkirtLengthField = addMeasurementField(inputGrid, "Длина юбки спереди (Дюп, см):", 12);
        backSkirtLengthField = addMeasurementField(inputGrid, "Длина юбки сзади (Дюс, см):", 13);

        Button generateButton = new Button("Построить выкройку");
        Button pdfButton = new Button("Сохранить в PDF");

        inputGrid.add(generateButton, 1, 14);
        inputGrid.add(pdfButton, 1, 15);

        // Canvas и ScrollPane для прокрутки
        canvas = new Canvas(1200, 1200);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        clearCanvas(gc);

        ScrollPane scrollPane = new ScrollPane(canvas);
        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(false);
        scrollPane.setPannable(true);

        setTestValues();

        generateButton.setOnAction(e -> {
            try {
                Measurements m = collectMeasurements();
                PatternData data = builder.buildForCanvas(m);
                drawAll(gc, data);
            } catch (NumberFormatException ex) {
                System.out.println("Ошибка: введите числа во все поля!");
            }
        });

        pdfButton.setOnAction(e -> {
            try {
                Measurements m = collectMeasurements();
                PdfExporter.export(m);
            } catch (NumberFormatException ex) {
                System.out.println("Ошибка: введите числа во все поля!");
            }
        });

        BorderPane root = new BorderPane();
        root.setLeft(new VBox(inputGrid));
        root.setCenter(scrollPane); // прокручиваемая область

        Scene scene = new Scene(root, 1000, 800); // уменьшили окно
        primaryStage.setTitle("Конструктор купальников (Lana-style)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setTestValues() {
        bustField.setText("68");
        waistField.setText("56");
        hipsField.setText("69");
        torsoField.setText("32");
        seatHeightField.setText("54");
        backWidthField.setText("28");
        armholeDepthField.setText("20");
        shoulderLengthField.setText("20");
        neckCircField.setText("28");

        sleeveLengthField.setText("50");
        upperArmCircField.setText("20");
        wristCircField.setText("13.5");

        frontSkirtLengthField.setText("23");
        backSkirtLengthField.setText("28");
    }

    private Measurements collectMeasurements() {
        return new Measurements(
                Double.parseDouble(bustField.getText()),
                Double.parseDouble(waistField.getText()),
                Double.parseDouble(hipsField.getText()),
                Double.parseDouble(torsoField.getText()),
                Double.parseDouble(seatHeightField.getText()),
                Double.parseDouble(backWidthField.getText()),
                Double.parseDouble(armholeDepthField.getText()),
                Double.parseDouble(shoulderLengthField.getText()),
                Double.parseDouble(neckCircField.getText()),
                Double.parseDouble(sleeveLengthField.getText()),
                Double.parseDouble(upperArmCircField.getText()),
                Double.parseDouble(wristCircField.getText()),
                Double.parseDouble(frontSkirtLengthField.getText()),
                Double.parseDouble(backSkirtLengthField.getText())
        );
    }

    private void clearCanvas(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(0.5);
        for (int i = 0; i < canvas.getWidth(); i += 20) gc.strokeLine(i, 0, i, canvas.getHeight());
        for (int i = 0; i < canvas.getHeight(); i += 20) gc.strokeLine(0, i, canvas.getWidth(), i);
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(1);
        gc.strokeLine(0, canvas.getHeight() - 20, canvas.getWidth(), canvas.getHeight() - 20);
        gc.strokeLine(20, 0, 20, canvas.getHeight());
    }

    private void drawPath(GraphicsContext gc, List<PatternPoint> points, Color color, double width) {
        if (points == null || points.size() < 2) return;
        gc.setStroke(color);
        gc.setLineWidth(width);
        gc.beginPath();
        gc.moveTo(points.get(0).x, points.get(0).y);
        for (int i = 1; i < points.size(); i++) {
            gc.lineTo(points.get(i).x, points.get(i).y);
        }
        gc.stroke();
    }

    private void drawAll(GraphicsContext gc, PatternData data) {
        clearCanvas(gc);

        // Лиф
        drawPath(gc, data.frontPoints, Color.BLUE, 2);
        drawPath(gc, data.backPoints, Color.BLACK, 2);

        // Рукав
        drawPath(gc, data.sleevePoints, Color.DARKGREEN, 2);

        // Юбка
        drawPath(gc, data.frontSkirtPoints, Color.PURPLE, 2);
        drawPath(gc, data.backSkirtPoints, Color.PURPLE, 2);
    }

    private TextField addMeasurementField(GridPane grid, String labelText, int row) {
        Label label = new Label(labelText);
        TextField textField = new TextField();
        grid.add(label, 0, row);
        grid.add(textField, 1, row);
        return textField;
    }

    public static void main(String[] args) {
        launch(args);
    }
}