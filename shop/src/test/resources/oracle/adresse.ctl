OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE adresse
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
	id,
	land,
	stadt,
	strasse,
	hausnummer,
	plz)
