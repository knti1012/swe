-- ===============================================================================
-- Indexe in den *generierten* Tabellen anlegen
-- ===============================================================================
CREATE INDEX kunde_adresse_index ON kunde(add_fk);
CREATE INDEX bestellung_kunde_index ON bestellung(k_fk);
CREATE INDEX bestpos_bestellung_index ON bestellposition(b_fk);
CREATE INDEX bestpos_artikel_index ON bestellposition(ar_fk);
CREATE INDEX be_l_bestellung_index ON bestellung_lieferung(b_fk);
CREATE INDEX be_l_lieferung_index ON bestellung_lieferung(l_fk);
