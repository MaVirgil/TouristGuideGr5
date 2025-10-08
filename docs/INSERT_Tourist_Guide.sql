INSERT IGNORE INTO City (name) VALUES ("Copenhagen"), ("Aabenraa"), ("Aalborg"), ("Aarhus"),
	("Birkerød"), ("Billund"), ("Esbjerg"), ("Fredericia"), ("Frederiksberg"), ("Helsingør"), ("Herlev"),
    ("Herning"), ("Hillerød"), ("Holbæk"), ("Holstebro"), ("Horsens"), ("Humlebæk"), ("Kolding"), ("Køge"),
    ("Næstved"), ("Odense"), ("Randers"), ("Ringsted"), ("Roskilde"), ("Silkeborg"),
    ("Slagelse"), ("Sønderborg"), ("Svendborg"), ("Vejle"), ("Viborg");
    
INSERT IGNORE INTO Tag (name) VALUES ("Restaurant"), ("Gratis"), ("Børnevenlig"),
	("Kunst"), ("Museum"), ("Natur"), ("Underholdning"), ("Koncert");

INSERT IGNORE INTO Attraction (name, description, city_id)
	SELECT A.column0, A.column1, C.id
    FROM
		(VALUES
			ROW("Tivoli", "Copenhagen’s largest amusement park", "Copenhagen"),
            ROW("Louisiana", "Famous art museum north of Copenhagen", "Humlebæk"),
            ROW("ARoS", "Aarhus Art Museum", "Aarhus"),
            ROW("Odense Zoo", "Animals form all over the world", "Odense"),
            ROW("Den Blå Planet", "Aquarium in Copenhagen", "Copenhagen"),
            ROW("Legoland", "LEGO amusement park", "Billund")
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
			SELECT "Louisianna", "Kunst"
            UNION ALL
			SELECT "Louisianna", "Museum"
            UNION ALL
			SELECT "Odense Zoo", "Børnevenlig"
            UNION ALL
            SELECT "Odense Zoo", "Underholdning"
            UNION ALL
            SELECT "Den Blå Planet", "Underholdning"
            UNION ALL
            SELECT "Den Blå Planet", "Børnevenlig"
            UNION ALL
            SELECT "Legoland", "Børnevenlig"
            UNION ALL
            SELECT "Legoland", "Restaurant"
            ) AS A
            JOIN Attraction AS Att ON A.Att_Lookup = Att.name
			JOIN Tag AS T ON A.T_Lookup = T.name;
            
    
