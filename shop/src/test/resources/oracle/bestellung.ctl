OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE bestellung
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
	id,
	k_fk,
	preis,
	status,
	erzeugt,
	aktualisiert)
