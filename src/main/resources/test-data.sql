insert into batteries (capacity, battery_type) values (5000, 0);


insert into camera_sensors(megapixels, matrix_size, pixel_size, sensor_name)
    values (50, '1/1.31', '1.22µm', 'Samsung GN2');
insert into cameras(has_optical_stabilization, sensor_id, camera_type)
    values (true, 1, 0);

insert into camera_sensors(megapixels, matrix_size, pixel_size, sensor_name)
    values (48, '1/2', '0.8µm', 'Sony IMX566');
insert into cameras(has_optical_stabilization, sensor_id, camera_type)
    values (true, 2, 1);

insert into camera_sensors(megapixels, matrix_size, pixel_size, sensor_name)
    values (12, '1/2.55', '1.22µm', 'Sony IMX386');
insert into cameras(has_optical_stabilization, sensor_id, camera_type)
    values (false, 3, 4);

insert into camera_sensors(megapixels, matrix_size, pixel_size, sensor_name)
    values (11.1, '1/3.6', '1.22µm', 'Samsung S5K3T2');
insert into cameras(has_optical_stabilization, sensor_id, camera_type)
    values (false, 4, 5);


insert into screen_resolutions(horizontal_pixels, vertical_pixels)
    values (1440, 3120);
insert into displays(diagonal, display_type, refresh_rate, resolution_id)
    values (6.71, 0, 120, 1);


insert into countries(name) VALUES ('USA');
insert into manufacturers(name, country_id)
    values ('Google', 1);


insert into operating_systems(name, version)
    values ('Android', '13');


insert into processors(cores, max_frequency, technology_node, model)
    values (8, 2.8, 5, 'Google Tensor G3');


insert into phone_specs(height, width, thickness, weight, sim_count,
                        charger_type, network_type, material, has_bluetooth, has_nfc, has_wifi,
                        battery_id, display_id, operating_system_id, processor_id)
    values (163.9, 75.9, 8.7, 210, 1,
            0, 3, 0, true, true, true,
            1, 1, 1, 1);


insert into specifications_cameras(specification_id, camera_id)
    values (1, 1);
insert into specifications_cameras(specification_id, camera_id)
    values (1, 2);
insert into specifications_cameras(specification_id, camera_id)
    values (1, 3);
insert into specifications_cameras(specification_id, camera_id)
    values (1, 4);


insert into variants(ram_size, rom_size, color) values (12, 256, 8);
insert into variants(ram_size, rom_size, color) values (12, 512, 4);


insert into phones(manufacturer_id, release_date, specification_id, model)
    values (1, '2023-10-04', 1, 'Pixel 8 Pro');


insert into phones_variants(phone_id, variant_id, price, quantity)
    values (1, 1, 89990, 12);
insert into phones_variants(phone_id, variant_id, price, quantity)
    values (1, 2, 92990, 78);

----------------------------------------------------------------

insert into batteries (capacity, battery_type) values (4352, 1);


insert into camera_sensors(megapixels, matrix_size, pixel_size, sensor_name)
values (48, '1/1.33', '1.22µm', 'Sony IMX800');
insert into cameras(has_optical_stabilization, sensor_id, camera_type)
values (true, 5, 0);

insert into camera_sensors(megapixels, matrix_size, pixel_size, sensor_name)
values (12, '1/3.6', '1µm', 'Sony IMX714');
insert into cameras(has_optical_stabilization, sensor_id, camera_type)
values (false, 6, 4);

insert into camera_sensors(megapixels, matrix_size, pixel_size, sensor_name)
values (12, '1/3.4', '1µm', 'Sony IMX772');
insert into cameras(has_optical_stabilization, sensor_id, camera_type)
values (true, 7, 1);

insert into camera_sensors(megapixels, matrix_size, pixel_size, sensor_name)
values (12, '1/3.6', '1.22µm', 'Sony IMX663');
insert into cameras(has_optical_stabilization, sensor_id, camera_type)
values (false, 8, 5);


insert into screen_resolutions(horizontal_pixels, vertical_pixels)
values (1284, 2778);
insert into displays(diagonal, display_type, refresh_rate, resolution_id)
values (6.7, 0, 120, 2);


insert into manufacturers(name, country_id)
values ('Apple', 1);


insert into operating_systems(name, version)
values ('iOS', '17.0');


insert into processors(cores, max_frequency, technology_node, model)
values (6, 3.46, 3, 'Apple A17 Bionic');


insert into phone_specs(height, width, thickness, weight, sim_count,
                        charger_type, network_type, material, has_bluetooth, has_nfc, has_wifi,
                        battery_id, display_id, operating_system_id, processor_id)
values (160.8, 78.1, 7.7, 240, 1,
        0, 3, 2, true, true, true,
        2, 2, 2, 2);


insert into specifications_cameras(specification_id, camera_id)
values (2, 5);
insert into specifications_cameras(specification_id, camera_id)
values (2, 6);
insert into specifications_cameras(specification_id, camera_id)
values (2, 7);
insert into specifications_cameras(specification_id, camera_id)
values (2, 8);


insert into variants(ram_size, rom_size, color) values (8, 256, 8);
insert into variants(ram_size, rom_size, color) values (8, 256, 7);
insert into variants(ram_size, rom_size, color) values (8, 256, 10);
insert into variants(ram_size, rom_size, color) values (8, 256, 4);
insert into variants(ram_size, rom_size, color) values (8, 256, 5);

insert into variants(ram_size, rom_size, color) values (8, 512, 8);
insert into variants(ram_size, rom_size, color) values (8, 512, 7);
insert into variants(ram_size, rom_size, color) values (8, 512, 10);
insert into variants(ram_size, rom_size, color) values (8, 512, 4);
insert into variants(ram_size, rom_size, color) values (8, 512, 5);

insert into variants(ram_size, rom_size, color) values (8, 1024, 8);
insert into variants(ram_size, rom_size, color) values (8, 1024, 7);
insert into variants(ram_size, rom_size, color) values (8, 1024, 10);
insert into variants(ram_size, rom_size, color) values (8, 1024, 4);
insert into variants(ram_size, rom_size, color) values (8, 1024, 5);


insert into phones(manufacturer_id, release_date, specification_id, model)
values (2, '2023-09-22', 2, 'Iphone 15 Pro Max');


insert into phones_variants(phone_id, variant_id, price, quantity)
values (2, 3, 125990, 99);
insert into phones_variants(phone_id, variant_id, price, quantity)
values (2, 4, 12790, 178);
insert into phones_variants(phone_id, variant_id, price, quantity)
values (2, 5, 126990, 19);
insert into phones_variants(phone_id, variant_id, price, quantity)
values (2, 6, 129990, 25);
insert into phones_variants(phone_id, variant_id, price, quantity)
values (2, 7, 129990, 56);

insert into phones_variants(phone_id, variant_id, price, quantity)
values (2, 8, 144990, 201);
insert into phones_variants(phone_id, variant_id, price, quantity)
values (2, 9, 142990, 190);
insert into phones_variants(phone_id, variant_id, price, quantity)
values (2, 10, 149990, 1);
insert into phones_variants(phone_id, variant_id, price, quantity)
values (2, 11, 147990, 0);
insert into phones_variants(phone_id, variant_id, price, quantity)
values (2, 12, 152990, 0);

insert into phones_variants(phone_id, variant_id, price, quantity)
values (2, 13, 169990, 3);
insert into phones_variants(phone_id, variant_id, price, quantity)
values (2, 14, 169990, 101);
insert into phones_variants(phone_id, variant_id, price, quantity)
values (2, 15, 163990, 103);
insert into phones_variants(phone_id, variant_id, price, quantity)
values (2, 16, 164990, 7);
insert into phones_variants(phone_id, variant_id, price, quantity)
values (2, 17, 161990, 134);

----------------------------------------------------------------

insert into batteries (capacity, battery_type) values (4500, 0);


insert into camera_sensors(megapixels, matrix_size, pixel_size, sensor_name)
values (50, '1/1.56', '1µm', 'Sony IMX766');
insert into cameras(has_optical_stabilization, sensor_id, camera_type)
values (true, 9, 0);

insert into camera_sensors(megapixels, matrix_size, pixel_size, sensor_name)
values (50, '1/2.55', '1µm', 'Samsung JN1');
insert into cameras(has_optical_stabilization, sensor_id, camera_type)
values (false, 10, 4);

insert into camera_sensors(megapixels, matrix_size, pixel_size, sensor_name)
values (32, '1/2.8', '0.8µm', 'Sony IMX616');
insert into cameras(has_optical_stabilization, sensor_id, camera_type)
values (false, 11, 5);


insert into screen_resolutions(horizontal_pixels, vertical_pixels)
values (1080, 2400);
insert into displays(diagonal, display_type, refresh_rate, resolution_id)
values (6.67, 0, 120, 3);


insert into countries(name)
values ('England');
insert into manufacturers(name, country_id)
values ('Nothing', 2);


insert into processors(cores, max_frequency, technology_node, model)
values (8, 3.2, 4, 'Qualcomm Snapdragon 8+ Gen 1');


insert into phone_specs(height, width, thickness, weight, sim_count,
                        charger_type, network_type, material, has_bluetooth, has_nfc, has_wifi,
                        battery_id, display_id, operating_system_id, processor_id)
values (163.9, 77, 8.3, 205, 2,
        0, 3, 0, true, true, true,
        3, 3, 1, 3);


insert into specifications_cameras(specification_id, camera_id)
values (3, 9);
insert into specifications_cameras(specification_id, camera_id)
values (3, 10);
insert into specifications_cameras(specification_id, camera_id)
values (3, 11);


insert into variants(ram_size, rom_size, color) values (8, 128, 8);
insert into variants(ram_size, rom_size, color) values (12, 128, 8);
insert into variants(ram_size, rom_size, color) values (8, 128, 7);
insert into variants(ram_size, rom_size, color) values (12, 128, 7);
insert into variants(ram_size, rom_size, color) values (12, 256, 7);


insert into phones(manufacturer_id, release_date, specification_id, model)
values (3, '2023-07-11', 3, 'Nothing Phone 2');


insert into phones_variants(phone_id, variant_id, price, quantity)
values (3, 1, 69990, 30);
insert into phones_variants(phone_id, variant_id, price, quantity)
values (3, 3, 63990, 11);
insert into phones_variants(phone_id, variant_id, price, quantity)
values (3, 17, 65990, 1);
insert into phones_variants(phone_id, variant_id, price, quantity)
values (3, 18, 63990, 2);

insert into phones_variants(phone_id, variant_id, price, quantity)
values (3, 19, 61990, 0);
insert into phones_variants(phone_id, variant_id, price, quantity)
values (3, 20, 69990, 15);
insert into phones_variants(phone_id, variant_id, price, quantity)
values (3, 21, 65990, 23);
insert into phones_variants(phone_id, variant_id, price, quantity)
values (3, 22, 62990, 54);

----------------------------------------------------------------

insert into camera_sensors(megapixels, matrix_size, pixel_size, sensor_name)
values (60, '1/2.8', '0.8µm', 'OmniVision OV616');
insert into cameras(has_optical_stabilization, sensor_id, camera_type)
values (false, 12, 5);


insert into manufacturers(name, country_id)
values ('Motorola', 1);


insert into operating_systems(name, version)
values ('Android', '12');


insert into phone_specs(height, width, thickness, weight, sim_count,
                        charger_type, network_type, material, has_bluetooth, has_nfc, has_wifi,
                        battery_id, display_id, operating_system_id, processor_id)
values (163.9, 77, 8.3, 205, 2,
        0, 3, 0, true, true, true,
        3, 3, 3, 3);


insert into specifications_cameras(specification_id, camera_id)
values (4, 9);
insert into specifications_cameras(specification_id, camera_id)
values (4, 10);
insert into specifications_cameras(specification_id, camera_id)
values (4, 12);


insert into phones(manufacturer_id, release_date, specification_id, model)
values (4, '2022-09-08', 4, 'Motorola Edge 30 Ultra');


insert into phones_variants(phone_id, variant_id, price, quantity)
values (4, 1, 42990, 4);
insert into phones_variants(phone_id, variant_id, price, quantity)
values (4, 3, 44990, 7);
insert into phones_variants(phone_id, variant_id, price, quantity)
values (4, 17, 43990, 1);
insert into phones_variants(phone_id, variant_id, price, quantity)
values (4, 18, 42990, 5);

insert into phones_variants(phone_id, variant_id, price, quantity)
values (4, 19, 41990, 0);
insert into phones_variants(phone_id, variant_id, price, quantity)
values (4, 20, 45990, 5);
insert into phones_variants(phone_id, variant_id, price, quantity)
values (4, 21, 44990, 1);
insert into phones_variants(phone_id, variant_id, price, quantity)
values (4, 22, 46990, 12);

----------------------------------------------------------------

insert into batteries (capacity, battery_type) values (1000, 1);


insert into screen_resolutions(horizontal_pixels, vertical_pixels)
values (240, 320);
insert into displays(diagonal, display_type, refresh_rate, resolution_id)
values (2.4, 2, 60, 4);


insert into countries(name) VALUES ('China');
insert into manufacturers(name, country_id)
values ('BQ', 3);


insert into operating_systems(name, version)
values ('Symbian', '9.3');


insert into processors(cores, max_frequency, technology_node, model)
values (1, 0.2, 5, 'Texas Instruments OMAP1510');


insert into phone_specs(height, width, thickness, weight, sim_count,
                        charger_type, network_type, material, has_bluetooth, has_nfc, has_wifi,
                        battery_id, display_id, operating_system_id, processor_id)
values (124.5, 53.5, 10.5, 83, 3,
        1, 0, 3, true, true, true,
        4, 4, 4, 4);


insert into variants(ram_size, rom_size, color) values (1, 1, 7);

insert into phones(manufacturer_id, release_date, specification_id, model)
values (5, '2022-05-14', 5, 'BQ 2400 Classic');


insert into phones_variants(phone_id, variant_id, price, quantity)
values (5, 23, 1299, 230);