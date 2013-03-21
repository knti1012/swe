OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE bestellposition
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
	id,
	b_fk,
	ar_fk,
	anzahl,
	preis)
