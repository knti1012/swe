OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE kunde
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
	id,
	add_fk,
	nachname,
	vorname,
	geschlecht,
	email,
	password,
	erzeugt,
	aktualisiert)
