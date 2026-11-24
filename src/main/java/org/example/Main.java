package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    private TextField bustField, waistField, hipsField, torsoField, seatHeightField, backWidthField;
    private TextField armholeDepthField, shoulderLengthField, neckCircField;
    private Canvas canvas;

    @Override
    public void start(Stage primaryStage) {
        GridPane inputGrid = new GridPane();
        inputGrid.setPadding(new Insets(10));
        inputGrid.setHgap(10);
        inputGrid.setVgap(8);

        bustField = addMeasurementField(inputGrid, "Обхват груди (см):", 0);
        waistField = addMeasurementField(inputGrid, "Обхват талии (см):", 1);
        hipsField = addMeasurementField(inputGrid, "Обхват бёдер (см):", 2);
        torsoField = addMeasurementField(inputGrid, "Длина туловища (плечо-талия), см:", 3);
        seatHeightField = addMeasurementField(inputGrid, "Высота сидения (талия-ластовица), см:", 4);
        backWidthField = addMeasurementField(inputGrid, "Ширина спины (см):", 5);

        // Новые мерки
        armholeDepthField = addMeasurementField(inputGrid, "Глубина проймы (см):", 6);
        shoulderLengthField = addMeasurementField(inputGrid, "Длина плеча (см):", 7);
        neckCircField = addMeasurementField(inputGrid, "Обхват шеи (см):", 8);

        Button generateButton = new Button("Построить выкройку");
        Button pdfButton = new Button("Сохранить в PDF");

        inputGrid.add(generateButton, 1, 9);
        inputGrid.add(pdfButton, 1, 10);

        canvas = new Canvas(600, 700);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        clearCanvas(gc);

        generateButton.setOnAction(e -> {
            try {
                double bust = Double.parseDouble(bustField.getText());
                double waist = Double.parseDouble(waistField.getText());
                double hips = Double.parseDouble(hipsField.getText());
                double torso = Double.parseDouble(torsoField.getText());
                double seatHeight = Double.parseDouble(seatHeightField.getText());
                double backWidth = Double.parseDouble(backWidthField.getText());
                double armholeDepth = Double.parseDouble(armholeDepthField.getText());
                double shoulderLength = Double.parseDouble(shoulderLengthField.getText());
                double neckCirc = Double.parseDouble(neckCircField.getText());

                drawPattern(gc, bust, waist, hips, torso, seatHeight, backWidth, armholeDepth, shoulderLength, neckCirc);
            } catch (NumberFormatException ex) {
                System.out.println("Ошибка: введите числа во все поля!");
            }
        });

        pdfButton.setOnAction(e -> {
            try {
                double bust = Double.parseDouble(bustField.getText());
                double waist = Double.parseDouble(waistField.getText());
                double hips = Double.parseDouble(hipsField.getText());
                double torso = Double.parseDouble(torsoField.getText());
                double seatHeight = Double.parseDouble(seatHeightField.getText());
                double backWidth = Double.parseDouble(backWidthField.getText());
                double armholeDepth = Double.parseDouble(armholeDepthField.getText());
                double shoulderLength = Double.parseDouble(shoulderLengthField.getText());
                double neckCirc = Double.parseDouble(neckCircField.getText());

                PdfExporter.export(bust, waist, hips, torso, seatHeight, backWidth, armholeDepth, shoulderLength, neckCirc);
            } catch (NumberFormatException ex) {
                System.out.println("Ошибка: введите числа во все поля!");
            }
        });

        BorderPane root = new BorderPane();
        root.setLeft(new VBox(inputGrid));
        root.setCenter(canvas);

        Scene scene = new Scene(root, 900, 750);
        primaryStage.setTitle("Построение выкройки купальника (PRO)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void clearCanvas(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Сетка
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(0.5);
        for (int i = 0; i < canvas.getWidth(); i += 20) {
            gc.strokeLine(i, 0, i, canvas.getHeight());
        }
        for (int i = 0; i < canvas.getHeight(); i += 20) {
            gc.strokeLine(0, i, canvas.getWidth(), i);
        }

        // Оси
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(1);
        gc.strokeLine(0, canvas.getHeight() - 20, canvas.getWidth(), canvas.getHeight() - 20); // X
        gc.strokeLine(20, 0, 20, canvas.getHeight()); // Y
    }

    private void drawPattern(GraphicsContext gc, double bust, double waist, double hips, double torso,
                             double seatHeight, double backWidth, double armholeDepth, double shoulderLength, double neckCirc) {
        clearCanvas(gc);

        double scale = 2.5;
        double centerX = 300;
        double startY = 100; // Отступ сверху

        // === ФОРМУЛЫ ===
        double stretch = 0.88;
        double chestW = (bust / 4.0) * stretch * scale;
        double waistW = (waist / 4.0) * stretch * scale;
        double hipW = (hips / 4.0) * stretch * scale;
        double bodyH = torso * scale;
        double seatH = seatHeight * scale;
        double totalH = bodyH + seatH;

        double neckDepth = Math.min(chestW / 3.0, 7.0 * scale);
        double backNeckDepth = 2.0 * scale;
        double armholeD = armholeDepth * scale;
        double shoulderL = shoulderLength * scale;

        // Точки
        double topY = startY;
        double bottomY = startY + totalH;
        double armholeStartX = centerX - chestW / 2;

        // --- Рисуем ПЕРЕД (синий) ---
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(2);

        // Плечо и горловина
        gc.strokeLine(centerX, topY, centerX - shoulderL, topY);
        gc.beginPath();
        gc.moveTo(centerX - shoulderL, topY);
        gc.quadraticCurveTo(centerX - shoulderL + neckDepth / 4, topY + neckDepth / 3,
                centerX, topY + neckDepth);
        gc.stroke();

        // Пройма
        gc.beginPath();
        gc.moveTo(centerX - shoulderL, topY);
        gc.quadraticCurveTo(centerX - chestW / 2 - 10, topY + armholeD / 2,
                centerX - waistW / 2, topY + armholeD);
        gc.stroke();

        // Боковой шов и ластовица (с выемкой)
        gc.strokeLine(centerX - waistW / 2, topY + armholeD, centerX - hipW / 2, bottomY - seatH * 0.2);
        gc.beginPath();
        gc.moveTo(centerX - hipW / 2, bottomY - seatH * 0.2);
        gc.quadraticCurveTo(centerX - hipW / 4, bottomY, centerX, bottomY);
        gc.stroke();

        // Вытачка на переде (грудь)
        double dartY = topY + armholeD * 0.6;
        gc.setStroke(Color.BLUE);
        gc.setLineDashes(5, 5);
        gc.strokeLine(centerX - chestW / 4, dartY, centerX - waistW / 4, dartY + 20);
        gc.setLineDashes(null);

        // Осевая
        gc.setStroke(Color.RED);
        gc.setLineDashes(5, 5);
        gc.strokeLine(centerX, topY, centerX, bottomY);
        gc.setLineDashes(null);

        gc.setFill(Color.BLACK);
        gc.fillText("Перед (PRO)", centerX - 50, topY - 20);
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