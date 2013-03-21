OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE lieferung
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
	id,
	lieferant,
	transport_art,
	erzeugt,
	aktualisiert)