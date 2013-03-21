OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE artikel
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
	id,
	name,
	groesse,
	farbe,
	preis,
	art,
	kategorie,
	lagerbestand,
	erzeugt,
	aktualisiert)
