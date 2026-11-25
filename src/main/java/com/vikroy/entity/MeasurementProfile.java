package com.vikroy.entity;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@Table(name = "measurement-profile")
public class MeasurementProfile {
    @Id
    private Long id;

    private User user;

    private Double bust;          // Обхват груди
    private Double waist;         // Обхват талии
    private Double hips;          // Обхват бедер
    private Double torsoLength;   // Длина туловища (dtc)
    private Double seatHeight;    // Высота сидения (vsd)
    private Double backWidth;     // Ширина спины
    private Double armholeDepth;  // Глубина проймы
    private Double shoulderLength;// Длина плеча
    private Double neckCirc;      // Обхват шеи
    private Double sleeveLength;  // Длина рукава
    private Double upperArmCirc;  // Обхват плеча
    private Double wristCirc;     // Обхват запястья
    private Double frontSkirtLength; // Длина юбки спереди
    private Double backSkirtLength;  // Длина юбки сзади
    private Double height;   // Рост

}
