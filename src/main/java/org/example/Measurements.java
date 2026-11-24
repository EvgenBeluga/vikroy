package org.example;

public class Measurements {
    // Основные мерки
    public double bust, waist, hips;
    public double torso, seatHeight;
    public double backWidth, armholeDepth, shoulderLength, neckCirc;

    // Новые мерки для рукава и юбки (согласно методике Lana)
    public double sleeveLength; // Др - длина рукава
    public double upperArmCirc; // Оп - обхват плеча
    public double wristCirc;    // Оз - обхват запястья
    public double frontSkirtLength; // Дюп
    public double backSkirtLength;  // Дюс

    // Коэффициенты (можно будет вынести в настройки)
    public double stretchFactor = 0.88;

    // Конструктор для удобства
    public Measurements(double bust, double waist, double hips, double torso, double seatHeight,
                        double backWidth, double armholeDepth, double shoulderLength, double neckCirc,
                        double sleeveLength, double upperArmCirc, double wristCirc,
                        double frontSkirtLength, double backSkirtLength) {
        this.bust = bust;
        this.waist = waist;
        this.hips = hips;
        this.torso = torso;
        this.seatHeight = seatHeight;
        this.backWidth = backWidth;
        this.armholeDepth = armholeDepth;
        this.shoulderLength = shoulderLength;
        this.neckCirc = neckCirc;
        this.sleeveLength = sleeveLength;
        this.upperArmCirc = upperArmCirc;
        this.wristCirc = wristCirc;
        this.frontSkirtLength = frontSkirtLength;
        this.backSkirtLength = backSkirtLength;
    }
}