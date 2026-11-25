package com.vikroy;

public class PatternCalculator {

    // Метод принимает мерки и возвращает строку с координатами (пока для примера)
    public static String calculate(double bust, double waist, double hips, double torsoLength) {
        // Здесь будут реальные формулы. Пока — заглушка для проверки.
        // Например, ширина спинки = обхват груди / 4 + прибавка
        double backWidth = bust / 4 + 2.0;
        double frontWidth = bust / 4 + 1.5;
        double sideSeamLength = torsoLength * 0.6;

        return String.format(
                "Ширина спинки: %.2f см\n" +
                        "Ширина переда: %.2f см\n" +
                        "Длина бокового шва: %.2f см",
                backWidth, frontWidth, sideSeamLength
        );
    }
}