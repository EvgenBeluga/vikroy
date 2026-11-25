package com.vikroy.repository;

import com.vikroy.entity.MeasurementProfile;
import com.vikroy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeasurementProfileRepository extends JpaRepository<MeasurementProfile, Long> {

    /** Последние сохранённые мерки пользователя (для генерации PDF) */
    Optional<MeasurementProfile> findFirstByUserOrderByIdDesc(User user);

    /** Все мерки пользователя, самые новые сверху */
    List<MeasurementProfile> findAllByUserOrderByCreatedAtDesc(User user);
}