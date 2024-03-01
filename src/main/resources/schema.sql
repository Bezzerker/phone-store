create table batteries (
    id SERIAL PRIMARY KEY,
    capacity INTEGER NOT NULL CHECK (capacity > 0),
    battery_type SMALLINT NOT NULL CHECK (battery_type >= 0 AND battery_type <= 1),
    UNIQUE (capacity, battery_type)
);

create table camera_sensors (
    id SERIAL PRIMARY KEY,
    sensor_name VARCHAR(255) UNIQUE NOT NULL CHECK (LENGTH(sensor_name) > 0),
    megapixels NUMERIC(5,2) NOT NULL,
    matrix_size VARCHAR(10) NOT NULL CHECK (LENGTH(matrix_size) > 0),
    pixel_size VARCHAR(10) NOT NULL CHECK (LENGTH(pixel_size) > 0)
);

create table cameras (
    id BIGSERIAL PRIMARY KEY,
    camera_type SMALLINT CHECK (camera_type >= 0 AND camera_type <= 5),
    has_optical_stabilization BOOLEAN NOT NULL,
    sensor_id INTEGER NOT NULL REFERENCES camera_sensors ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE (camera_type, has_optical_stabilization, sensor_id)
);

create table screen_resolutions (
    id SERIAL PRIMARY KEY,
    horizontal_pixels INTEGER NOT NULL CHECK (horizontal_pixels > 0),
    vertical_pixels INTEGER NOT NULL CHECK (vertical_pixels > 0),
    UNIQUE (horizontal_pixels, vertical_pixels)
);

create table displays (
    id SERIAL PRIMARY KEY,
    diagonal NUMERIC(5,2) NOT NULL CHECK (diagonal > 0),
    resolution_id INTEGER NOT NULL REFERENCES screen_resolutions ON DELETE CASCADE ON UPDATE CASCADE,
    refresh_rate INTEGER NOT NULL CHECK (refresh_rate > 0),
    display_type SMALLINT NOT NULL CHECK (display_type >= 0 AND display_type <= 2),
    UNIQUE (diagonal, resolution_id, refresh_rate, display_type)
);

create table countries (
    id SERIAL PRIMARY KEY,
    name VARCHAR(60) UNIQUE NOT NULL CHECK (LENGTH(name) > 0)
);

create table manufacturers (
    id SERIAL PRIMARY KEY,
    country_id INTEGER NOT NULL REFERENCES countries ON DELETE CASCADE ON UPDATE CASCADE,
    name VARCHAR(100) NOT NULL CHECK (LENGTH(name) > 0),
    UNIQUE (country_id, name)
);

create table operating_systems (
    id SERIAL PRIMARY KEY,
    name VARCHAR(80) NOT NULL CHECK (LENGTH(name) > 0),
    version VARCHAR(80) NOT NULL CHECK (LENGTH(version) > 0),
    UNIQUE (name, version)
);

create table processors (
    id SERIAL PRIMARY KEY,
    model VARCHAR(100) NOT NULL UNIQUE CHECK (LENGTH(model) > 0),
    cores INTEGER NOT NULL CHECK (cores > 0),
    max_frequency NUMERIC(5,2) NOT NULL CHECK (max_frequency > 0),
    technology_node INTEGER NOT NULL CHECK (technology_node > 0 AND technology_node < 100)
);

create table phone_specs (
    id BIGSERIAL PRIMARY KEY,
    network_type SMALLINT NOT NULL CHECK (network_type >= 0 AND network_type <= 3),
    sim_count INTEGER NOT NULL CHECK (sim_count > 0 AND sim_count <= 5),

    has_bluetooth BOOLEAN NOT NULL,
    has_nfc BOOLEAN NOT NULL,
    has_wifi BOOLEAN NOT NULL,

    height NUMERIC(5,2) NOT NULL CHECK (height > 0 AND height < 200),
    width NUMERIC(5,2) NOT NULL CHECK (width > 0 AND width < 200),
    thickness NUMERIC(5,2) NOT NULL CHECK (thickness > 0 AND thickness < 50),
    weight NUMERIC(5,2) NOT NULL CHECK (weight > 0 AND weight < 1000),

    material SMALLINT NOT NULL CHECK (material >= 0 and material <= 6),
    charger_type SMALLINT NOT NULL CHECK (charger_type >= 0 and charger_type <= 3),


    operating_system_id INTEGER REFERENCES operating_systems
                            ON UPDATE CASCADE
                            ON DELETE SET NULL,
    display_id INTEGER REFERENCES displays
                            ON UPDATE CASCADE
                            ON DELETE SET NULL,
    processor_id INTEGER REFERENCES processors
                            ON UPDATE CASCADE
                            ON DELETE SET NULL,
    battery_id INTEGER REFERENCES batteries
                            ON UPDATE CASCADE
                            ON DELETE SET NULL
);

create table specifications_cameras (
    camera_id BIGINT NOT NULL REFERENCES cameras
                            ON UPDATE CASCADE
                            ON DELETE CASCADE,
    specification_id BIGINT NOT NULL REFERENCES phone_specs
                            ON UPDATE CASCADE
                            ON DELETE CASCADE,
    UNIQUE (camera_id, specification_id)
);

create table phones (
    id BIGSERIAL PRIMARY KEY,
    model VARCHAR(80) NOT NULL CHECK (LENGTH(model) > 0),
    manufacturer_id INTEGER REFERENCES manufacturers
                            ON UPDATE CASCADE
                            ON DELETE SET NULL,
    release_date TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    specification_id BIGINT NOT NULL REFERENCES phone_specs
                            ON UPDATE CASCADE
                            ON DELETE CASCADE,
    UNIQUE (model, manufacturer_id, release_date, specification_id)
);

create table variants (
    id SERIAL PRIMARY KEY,
    ram_size INTEGER NOT NULL CHECK (ram_size > 0),
    rom_size INTEGER NOT NULL CHECK (rom_size > 0),
    color SMALLINT NOT NULL CHECK (color >= 0 and color <= 10),
    UNIQUE (ram_size, rom_size, color)
);

create table phones_variants (
    id BIGSERIAL PRIMARY KEY,
    price NUMERIC(15,2) NOT NULL CHECK (price > 0),
    quantity INTEGER NOT NULL CHECK (quantity >= 0),
    phone_id BIGINT NOT NULL REFERENCES phones
                            ON UPDATE CASCADE
                            ON DELETE CASCADE,
    variant_id INTEGER NOT NULL REFERENCES variants
                            ON UPDATE CASCADE
                            ON DELETE CASCADE,
    UNIQUE (phone_id, variant_id)
);

CREATE INDEX idx_phones_variants_phone_id_key ON phones_variants(phone_id);
CREATE INDEX idx_phones_variants_variant_id_key ON phones_variants(variant_id);
