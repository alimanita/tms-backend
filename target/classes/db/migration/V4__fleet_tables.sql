-- Tables for fleet modules: tires, oil changes, documents, maintenance plans

CREATE TABLE tire (
    id              BIGSERIAL PRIMARY KEY,
    serial_number   VARCHAR(100) NOT NULL UNIQUE,
    brand           VARCHAR(100),
    model           VARCHAR(100),
    size            VARCHAR(50),
    tire_type       VARCHAR(50),
    purchase_date   DATE,
    purchase_cost   NUMERIC(15,2),
    max_km          NUMERIC(12,2),
    status          VARCHAR(30) NOT NULL DEFAULT 'STOCK',
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE tire_assignment (
    id               BIGSERIAL PRIMARY KEY,
    tire_id          BIGINT NOT NULL REFERENCES tire(id),
    vehicle_id       BIGINT NOT NULL REFERENCES vehicle(id),
    position         VARCHAR(30) NOT NULL,
    mount_date       DATE NOT NULL,
    mount_mileage    NUMERIC(12,2) NOT NULL,
    unmount_date     DATE,
    unmount_mileage  NUMERIC(12,2),
    reason_unmount   VARCHAR(200),
    notes            TEXT,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_tire_assignment_vehicle ON tire_assignment(vehicle_id);
CREATE INDEX idx_tire_assignment_tire ON tire_assignment(tire_id);

CREATE TABLE oil_change (
    id                  BIGSERIAL PRIMARY KEY,
    vehicle_id          BIGINT NOT NULL REFERENCES vehicle(id),
    oil_type            VARCHAR(100) NOT NULL,
    change_date         DATE NOT NULL,
    mileage_at_change   NUMERIC(12,2) NOT NULL,
    quantity_liters     NUMERIC(10,2) NOT NULL,
    unit_cost           NUMERIC(15,2),
    total_cost          NUMERIC(15,2),
    next_change_km      NUMERIC(12,2),
    next_change_date    DATE,
    performed_by        VARCHAR(200),
    notes               TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_oil_change_vehicle ON oil_change(vehicle_id);

CREATE TABLE fleet_document (
    id                BIGSERIAL PRIMARY KEY,
    vehicle_id        BIGINT REFERENCES vehicle(id),
    driver_id         BIGINT REFERENCES driver(id),
    document_type     VARCHAR(50) NOT NULL,
    reference_number  VARCHAR(100),
    issuer            VARCHAR(200),
    issue_date        DATE,
    expiry_date       DATE,
    amount            NUMERIC(15,2),
    file_path         VARCHAR(500),
    file_name         VARCHAR(255),
    status            VARCHAR(50),
    notes             TEXT,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_fleet_document_vehicle ON fleet_document(vehicle_id);
CREATE INDEX idx_fleet_document_driver ON fleet_document(driver_id);
CREATE INDEX idx_fleet_document_expiry ON fleet_document(expiry_date);

CREATE TABLE maintenance_plan (
    id                    BIGSERIAL PRIMARY KEY,
    vehicle_id            BIGINT NOT NULL REFERENCES vehicle(id),
    maintenance_type      VARCHAR(50) NOT NULL,
    trigger_type          VARCHAR(30) NOT NULL,
    trigger_value         NUMERIC(12,2) NOT NULL,
    last_performed_date   DATE,
    last_performed_km     NUMERIC(12,2),
    next_due_date         DATE,
    next_due_km           NUMERIC(12,2),
    alert_threshold       NUMERIC(12,2),
    active                BOOLEAN NOT NULL DEFAULT TRUE,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_maintenance_plan_vehicle ON maintenance_plan(vehicle_id);
