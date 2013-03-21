package de.shop.bestellverwaltung.service;

import java.util.List;
import java.util.Locale;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Lieferung;
import de.shop.kundenverwaltung.domain.Kunde;

public interface BestellungService {

	Bestellung findBestellungById(Long id);

	Bestellung findBestellungByIdMitLieferungen(Long id);

	List<Bestellung> findBestellungenByLieferung(Long id);

	Kunde findKundeById(Long id);

	List<Bestellung> findBestellungenByKundeId(Long kundeId);

	Bestellung createBestellung(Bestellung bestellung, Kunde kunde,
			Locale locale);

	List<Artikel> ladenhueter(int anzahl);

	List<Lieferung> findLieferungenByLieferant(String lieferant);

	List<Bestellposition> findBestellpositionenById(Long id);

	List<Bestellung> findAllBestellungen();

	Lieferung createLieferung(Lieferung lieferung, List<Bestellung> bestellungen);

	void deleteBestellung(Bestellung bestellung);

	Bestellung updateBestellung(Bestellung bestellung);

}
