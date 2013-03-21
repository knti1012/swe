OPTIONS(direct=true)
UNRECOVERABLE LOAD DATA
INTO TABLE bestellung_lieferung
APPEND
FIELDS TERMINATED BY ';' OPTIONALLY ENCLOSED BY '"' (
	id,
	b_fk,
	l_fk)