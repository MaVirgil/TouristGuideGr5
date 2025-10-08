
DROP ALL OBJECTS;

CREATE TABLE Tag (
                     id INT NOT NULL UNIQUE AUTO_INCREMENT,
                     name VARCHAR(50) NOT NULL UNIQUE,
                     PRIMARY KEY (id)
);

CREATE TABLE City (
                      id INT NOT NULL UNIQUE AUTO_INCREMENT,
                      name VARCHAR(255) NOT NULL UNIQUE,
                      PRIMARY KEY (id)
);

CREATE TABLE Attraction (
                            id INT NOT NULL UNIQUE AUTO_INCREMENT,
                            name VARCHAR(255) NOT NULL UNIQUE,
                            description VARCHAR(255) NOT NULL,
                            city_id INT NOT NULL,
                            PRIMARY KEY (id),
                            FOREIGN KEY (city_id) REFERENCES city (id)
                                ON DELETE RESTRICT
);

CREATE TABLE Tags_Attraction_Junction (
                                          attraction_id INT NOT NULL,
                                          tag_id INT NOT NULL,
                                          PRIMARY KEY (attraction_id, tag_id),
                                          FOREIGN KEY (attraction_id) REFERENCES Attraction (id)
                                              ON DELETE RESTRICT,
                                          FOREIGN KEY (tag_id) REFERENCES tag (id)
                                              ON DELETE RESTRICT
);


INSERT INTO City (name) VALUES ('Copenhagen'), ('Aarhus');

INSERT INTO Tag (name) VALUES
                           ('Restaurant'), ('Gratis'), ('Børnevenlig'),
                           ('Kunst'), ('Museum'), ('Underholdning'), ('Koncert');

INSERT INTO Attraction (name, description, city_id)
VALUES
    ('Tivoli', 'Copenhagen’s largest amusement park',
     (SELECT id FROM City WHERE name = 'Copenhagen')),
    ('ARoS', 'Aarhus Art Museum',
     (SELECT id FROM City WHERE name = 'Aarhus'));

INSERT INTO Tags_Attraction_Junction (attraction_id, tag_id)
SELECT Att.id, T.id
FROM Attraction AS Att, Tag AS T
WHERE Att.name = 'Tivoli' AND T.name = 'Restaurant';

INSERT INTO Tags_Attraction_Junction (attraction_id, tag_id)
SELECT Att.id, T.id
FROM Attraction AS Att, Tag AS T
WHERE Att.name = 'Tivoli' AND T.name = 'Børnevenlig';

INSERT INTO Tags_Attraction_Junction (attraction_id, tag_id)
SELECT Att.id, T.id
FROM Attraction AS Att, Tag AS T
WHERE Att.name = 'Tivoli' AND T.name = 'Underholdning';

INSERT INTO Tags_Attraction_Junction (attraction_id, tag_id)
SELECT Att.id, T.id
FROM Attraction AS Att, Tag AS T
WHERE Att.name = 'Tivoli' AND T.name = 'Koncert';

INSERT INTO Tags_Attraction_Junction (attraction_id, tag_id)
SELECT Att.id, T.id
FROM Attraction AS Att, Tag AS T
WHERE Att.name = 'ARoS' AND T.name = 'Kunst';

INSERT INTO Tags_Attraction_Junction (attraction_id, tag_id)
SELECT Att.id, T.id
FROM Attraction AS Att, Tag AS T
WHERE Att.name = 'ARoS' AND T.name = 'Museum';