/*-- V2: Driver account support

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_user_driver'
    ) THEN
ALTER TABLE app_user
    ADD CONSTRAINT fk_user_driver
        FOREIGN KEY (driver_id)
            REFERENCES driver(id)
            ON DELETE SET NULL;
END IF;
END $$;

INSERT INTO app_user
(username, password, full_name, email, phone, active, driver_id)
VALUES
    (
        'ali.bensalah',
        '123456',
        'Ali Ben Salah',
        'ali2@tms.local',
        '+21622111222',
        TRUE,
        1
    )
    ON CONFLICT (username) DO NOTHING;

INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id
FROM app_user u
         JOIN role r ON r.code = 'DRIVER'
WHERE u.username = 'ali.bensalah'
    ON CONFLICT DO NOTHING;*/