-- liquibase formatted sql

-- changeset vikroy:002-create-measurement-profile
CREATE TABLE measurement_profile (
                                     id BIGSERIAL PRIMARY KEY,
                                     user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                     created_at TIMESTAMP NOT NULL DEFAULT now(),

                                     height DOUBLE PRECISION,
                                     bust DOUBLE PRECISION,
                                     waist DOUBLE PRECISION,
                                     hips DOUBLE PRECISION,
                                     neck_circ DOUBLE PRECISION,
                                     dtc DOUBLE PRECISION,
                                     vsd DOUBLE PRECISION,
                                     sleeve_length DOUBLE PRECISION,
                                     upper_arm_circ DOUBLE PRECISION,
                                     wrist_circ DOUBLE PRECISION,
                                     vt DOUBLE PRECISION,
                                     front_skirt_length DOUBLE PRECISION,
                                     back_skirt_length DOUBLE PRECISION,
                                     dtp DOUBLE PRECISION,
                                     ankle_circ DOUBLE PRECISION,
                                     seat_height DOUBLE PRECISION
);

CREATE INDEX idx_measurement_profile_user_id ON measurement_profile(user_id);

-- rollback DROP TABLE measurement_profile;