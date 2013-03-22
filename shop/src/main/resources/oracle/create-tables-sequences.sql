CREATE SEQUENCE hibernate_sequence START WITH ${sequence.start};

CREATE TABLE adresse(
	id NUMBER NOT NULL PRIMARY KEY,
	land VARCHAR2(32) NOT NULL,
	stadt VARCHAR2(32) NOT NULL,
	strasse VARCHAR2(32) NOT NULL,
	hausnummer	INTEGER NOT NULL,
	plz VARCHAR2(7) NOT NULL
)CACHE;

CREATE TABLE kunde(
	id NUMBER NOT NULL PRIMARY KEY,
	add_fk NUMBER NOT NULL REFERENCES adresse(id),
	nachname VARCHAR2(32) NOT NULL,
	vorname VARCHAR2(32) NOT NULL,
	geschlecht CHAR(1) NOT NULL,
	email VARCHAR2(128) NOT NULL UNIQUE,
	password VARCHAR2(256) NOT NULL,
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL	
)CACHE;
CREATE INDEX kunde_adresse_index ON kunde(add_fk);


CREATE TABLE artikel(
	id NUMBER NOT NULL PRIMARY KEY,
 	name VARCHAR2(32) not null,
 	groesse VARCHAR2(32) not null,
 	farbe VARCHAR2(32) not null,
	preis REAL,
 	art VARCHAR2(32) not null,
 	kategorie VARCHAR2(32) not null,
 	lagerbestand INTEGER not null,
 	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL	
)CACHE;	

CREATE TABLE bestellung(
	id NUMBER NOT NULL PRIMARY KEY,
	k_fk NUMBER NOT NULL REFERENCES kunde(id),
	preis REAL,
 	status VARCHAR2 (32) NOT NULL,
	erzeugt TIMESTAMP NOT NULL,
	aktualisiert TIMESTAMP NOT NULL	
)CACHE;
CREATE INDEX bestellung_kunde_index ON bestellung(k_fk);

CREATE TABLE bestellposition(
	id NUMBER NOT NULL PRIMARY KEY,
	b_fk NUMBER NOT NULL REFERENCES bestellung(id),
	ar_fk NUMBER NOT NULL REFERENCES artikel(id),
	anzahl SMALLINT NOT NULL,
	preis REAL
)CACHE;
CREATE INDEX bestpos_bestellung_index ON bestellposition(b_fk);
CREATE INDEX bestpos_artikel_index ON bestellposition(ar_fk);

CREATE TABLE lieferung(
	id NUMBER NOT NULL PRIMARY KEY,
	lieferant VARCHAR2(12) NOT NULL,
	transport_art VARCHAR2(20),
	erzeugt DATE NOT NULL,
	aktualisiert DATE NOT NULL
)CACHE;

CREATE TABLE bestellung_lieferung(
	id NUMBER NOT NULL PRIMARY KEY,
	b_fk NUMBER NOT NULL REFERENCES bestellung(id),
	l_fk NUMBER NOT NULL REFERENCES lieferung(id)
)CACHE;

CREATE INDEX be_l_bestellung_index ON bestellung_lieferung(b_fk);
CREATE INDEX be_l_lieferung_index ON bestellung_lieferung(l_fk);
