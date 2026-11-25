package com.vikroy.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "measurement_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeasurementProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // === Мерки (см) ===

    private Double height;
    private Double bust;            // Обхват груди
    private Double waist;           // Обхват талии
    private Double hips;            // Обхват бёдер
    private Double neckCirc;        // Обхват шеи
    private Double dtc;             // Длина спины до талии
    private Double vsd;             // Высота сидения двойная
    private Double sleeveLength;    // Длина рукава
    private Double upperArmCirc;    // Обхват плеча (бицепса)
    private Double wristCirc;       // Обхват запястья
    private Double vt;              // Верхняя точка оката трусов спереди
    private Double frontSkirtLength;// Длина юбки спереди
    private Double backSkirtLength; // Длина юбки сзади
    private Double dtp;             // Длина от талии до пола
    private Double ankleCirc;       // Обхват щиколотки
    private Double seatHeight;      // Высота сидения

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}