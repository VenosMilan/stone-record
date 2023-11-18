INSERT INTO stone (id, manufacture, state_of_stone, thicknes, color, notes, rack, date_of_creation)
VALUES (1,'Kamenictví Novák', 'AVAILABLE', 2.5, 'Černá', 'Žádné poznámky.', 'A1', '2022-10-10 11:30:30');

INSERT INTO stone (id, manufacture, state_of_stone, thicknes, color, notes, rack, date_of_creation)
VALUES (2,'Kamenictví Svoboda', 'SOLD', 3.0, 'Bílá', 'Poškozený roh.', 'B2','2022-10-10 11:30:30');

INSERT INTO stone (id, manufacture, state_of_stone, thicknes, color, notes, rack, date_of_creation)
VALUES (3,'Kamenictví Horák', 'AVAILABLE', 2.0, 'Šedá', 'Žádné poznámky.', 'C3','2022-10-10 11:30:30');

INSERT INTO stone (id, manufacture, state_of_stone, thicknes, color, notes, rack, date_of_creation)
VALUES (4,'Kamenictví Dvořák', 'SOLD', 2.7, 'Žlutá', 'Škrábance na povrchu.', 'D4','2022-10-10 11:30:30');

INSERT INTO dimension (id, stone_id, dimension)
VALUES (1, 1, 10.0);

INSERT INTO dimension (id, stone_id, dimension)
VALUES (2, 1, 20.0);

INSERT INTO dimension (id, stone_id, dimension)
VALUES (3, 1, 30.0);

INSERT INTO dimension (id, stone_id, dimension)
VALUES (4, 1, 5.0);

INSERT INTO dimension (id, stone_id, dimension)
VALUES (5, 2, 15.0);

INSERT INTO dimension (id, stone_id, dimension)
VALUES (6, 2, 10.0);

INSERT INTO dimension (id, stone_id, dimension)
VALUES (7, 3, 15.0);

INSERT INTO dimension (id, stone_id, dimension)
VALUES (8, 3, 10.0);

INSERT INTO dimension (id, stone_id, dimension)
VALUES (9, 3, 15.0);

INSERT INTO dimension (id, stone_id, dimension)
VALUES (10, 3, 10.0);

INSERT INTO dimension (id, stone_id, dimension)
VALUES (11, 3, 20.0);

INSERT INTO dimension (id, stone_id, dimension)
VALUES (12, 3, 30.0);

INSERT INTO dimension (id, stone_id, dimension)
VALUES (13, 4, 5.0);

INSERT INTO dimension (id, stone_id, dimension)
VALUES (14, 4, 5.0);

INSERT INTO dimension (id, stone_id, dimension)
VALUES (15, 4, 7.0);

INSERT INTO dimension (id, stone_id, dimension)
VALUES (16, 4, 9.0);