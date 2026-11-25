package com.vikroy;

public class Measurements {
    // 1. Р - рост (справочно)
    public double height;
    // 2. Д - дуга (справочно)
    public double duga;
    // 3. От - обхват груди над грудью
    public double bust;
    // 4. От - обхват талии
    public double waist;
    // 5. Об - обхват бедер
    public double hips;
    // 6. Ош - обхват шеи
    public double neckCirc;
    // 7. Дтс - длина спины до талии
    public double dtc;
    // 8. Всд - высота сидения двойная
    public double vsd;
    // 9. Др - длина рукава
    public double sleeveLength;
    // 10. Оп - обхват плеча (бицепса)
    public double upperArmCirc;
    // 11. Оз - обхват запястья
    public double wristCirc;
    // 12. Вт - верхняя точка оката трусов спереди
    public double vt;
    // 13. Дюп - длина юбки спереди
    public double frontSkirtLength;
    // 14. Дюс - длина юбки сзади
    public double backSkirtLength;
    // 15. Дтп - длина от талии до пола
    public double dtp;
    // 16. Ощ - обхват щиколотки
    public double ankleCirc;
    // 17. Вс - высота сидения
    public double seatHeight;

    // Коэффициент растяжимости (для бифлекса)
    public double stretchFactor = 0.88;

    public Measurements(double height, double duga, double bust, double waist, double hips,
                        double neckCirc, double dtc, double vsd, double sleeveLength,
                        double upperArmCirc, double wristCirc, double vt,
                        double frontSkirtLength, double backSkirtLength, double dtp,
                        double ankleCirc, double seatHeight) {
        this.height = height;
        this.duga = duga;
        this.bust = bust;
        this.waist = waist;
        this.hips = hips;
        this.neckCirc = neckCirc;
        this.dtc = dtc;
        this.vsd = vsd;
        this.sleeveLength = sleeveLength;
        this.upperArmCirc = upperArmCirc;
        this.wristCirc = wristCirc;
        this.vt = vt;
        this.frontSkirtLength = frontSkirtLength;
        this.backSkirtLength = backSkirtLength;
        this.dtp = dtp;
        this.ankleCirc = ankleCirc;
        this.seatHeight = seatHeight;
    }
}