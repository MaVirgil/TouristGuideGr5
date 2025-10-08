DROP DATABASE IF EXISTS tourist_guide;
CREATE DATABASE tourist_guide
	DEFAULT CHARACTER SET utf8mb4;
USE tourist_guide;

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
                            description TINYTEXT NOT NULL,
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

INSERT IGNORE INTO City (name) VALUES ("Copenhagen"), ("Aarhus");

INSERT IGNORE INTO Tag (name) VALUES ("Børnevenlig"),
	("Kunst"), ("Museum"), ("Underholdning"), ("Koncert");

INSERT IGNORE INTO Attraction (name, description, city_id)
SELECT A.column0, A.column1, C.id
FROM
    (VALUES
         ROW("Tivoli", "Copenhagen’s largest amusement park", "Copenhagen"),
         ROW("ARoS", "Aarhus Art Museum", "Aarhus"),
    ) AS A (column0, column1, City_Lookup)
        JOIN
    City AS C ON A.City_Lookup = C.name;

INSERT IGNORE INTO Tags_Attraction_Junction (attraction_id, tag_id)
SELECT Att.id, T.id
FROM
    (SELECT "Tivoli" AS Att_Lookup, "Restaurant" AS T_Lookup
     UNION ALL
     SELECT "Tivoli", "Børnevenlig"
     UNION ALL
     SELECT "Tivoli", "Underholdning"
     UNION ALL
     SELECT "Tivoli", "Koncert"
     UNION ALL
     SELECT "ARoS", "Kunst"
     UNION ALL
     SELECT "ARoS", "Museum"
     UNION ALL
    ) AS A
        JOIN Attraction AS Att ON A.Att_Lookup = Att.name
        JOIN Tag AS T ON A.T_Lookup = T.name;