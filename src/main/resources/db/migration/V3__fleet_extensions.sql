-- Fleet extensions ported from BusnissMarb fleet module

ALTER TABLE transport_mission
    ADD COLUMN IF NOT EXISTS cancellation_reason TEXT;

CREATE TABLE mission_expense (
    id               BIGSERIAL PRIMARY KEY,
    mission_id       BIGINT NOT NULL REFERENCES transport_mission(id) ON DELETE CASCADE,
    expense_type     VARCHAR(50) NOT NULL,
    amount           NUMERIC(15,2) NOT NULL,
    currency         VARCHAR(3) NOT NULL DEFAULT 'EUR',
    expense_date     TIMESTAMPTZ NOT NULL,
    description      VARCHAR(300),
    reimbursable     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_mission_expense_mission ON mission_expense(mission_id);

CREATE TABLE fleet_machine (
    id                 BIGSERIAL PRIMARY KEY,
    reference          VARCHAR(50) NOT NULL UNIQUE,
    serial_number      VARCHAR(100),
    name               VARCHAR(200) NOT NULL,
    brand              VARCHAR(100),
    model              VARCHAR(100),
    category           VARCHAR(50),
    purchase_date      DATE,
    purchase_price     NUMERIC(15,2),
    power_unit         VARCHAR(20),
    power_value        NUMERIC(10,2),
    initial_hours      NUMERIC(12,2) NOT NULL DEFAULT 0,
    current_hours      NUMERIC(12,2) NOT NULL DEFAULT 0,
    location           VARCHAR(200),
    status             VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    notes              TEXT,
    active             BOOLEAN NOT NULL DEFAULT TRUE,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE machine_maintenance_rule (
    id                         BIGSERIAL PRIMARY KEY,
    machine_id                 BIGINT NOT NULL REFERENCES fleet_machine(id) ON DELETE CASCADE,
    code                       VARCHAR(10),
    description                VARCHAR(200),
    action_type                VARCHAR(50),
    interval_hours             INT,
    interval_days              INT,
    consumable                 VARCHAR(100),
    quantity                   NUMERIC(10,2),
    quantity_unit              VARCHAR(20),
    last_performed_hours       NUMERIC(12,2),
    last_performed_date        DATE,
    active                     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at                 TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_machine_rule_machine ON machine_maintenance_rule(machine_id);

CREATE TABLE work_order (
    id                 BIGSERIAL PRIMARY KEY,
    reference          VARCHAR(50) NOT NULL UNIQUE,
    entity_type        VARCHAR(20) NOT NULL,
    entity_id          BIGINT NOT NULL,
    order_type         VARCHAR(20) NOT NULL DEFAULT 'CORRECTIVE',
    priority           VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    status             VARCHAR(30) NOT NULL DEFAULT 'PLANNED',
    maintenance_type   VARCHAR(50),
    description        TEXT,
    scheduled_date     DATE,
    started_at         TIMESTAMPTZ,
    completed_at       TIMESTAMPTZ,
    mileage_at_order   NUMERIC(12,2),
    hours_at_order     NUMERIC(12,2),
    estimated_cost     NUMERIC(15,2),
    actual_cost        NUMERIC(15,2) NOT NULL DEFAULT 0,
    notes              TEXT,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_work_order_entity ON work_order(entity_type, entity_id);
CREATE INDEX idx_work_order_status ON work_order(status);
