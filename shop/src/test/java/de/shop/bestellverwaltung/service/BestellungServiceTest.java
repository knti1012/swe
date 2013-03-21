package de.shop.bestellverwaltung.service;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Lieferung;
import de.shop.bestellverwaltung.domain.TransportType;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.util.AbstractTest;

@RunWith(Arquillian.class)
public class BestellungServiceTest extends AbstractTest {
	private static final Long KUNDE_ID_VORHANDEN = Long.valueOf(5987847);
	private static final Long KUNDE_ID_NICHT_VORHANDEN = Long.valueOf(10);
	private static final Long ARTIKEL_1_ID = Long.valueOf(10004);
	private static final Integer ARTIKEL_1_ANZAHL = 1;
	private static final Long ARTIKEL_2_ID = Long.valueOf(10005);
	private static final Integer ARTIKEL_2_ANZAHL = 2;

	private static final String LIEFERANT_VORHANDEN = "DHL";
	private static final String LIEFERANT_NICHT_VORHANDEN = "SPEEDY";

	private static final Long BESTELLUNG_ID_VORHANDEN = Long.valueOf(1003);
	private static final Long BESTELLUNG_ID_NICHT_VORHANDEN = Long.valueOf(222);
	private static final Long BESTELLUNG_ID2A_VORHANDEN = Long.valueOf(1002);
	private static final Long BESTELLUNG_ID2B_VORHANDEN = Long.valueOf(1005);
	private static final Double PREIS = 300D;
	private static final String LIEFERANT_NAME = "UPS";
	private static final Long BESTELLUNG_ID_DELETE_TEST = 1001L;

	@Inject
	private BestellungService bs;

	@Inject
	private KundeService ks;

	@Inject
	private ArtikelService as;

	@Test
	public void createBestellung() throws RollbackException,
			HeuristicMixedException, HeuristicRollbackException,
			SystemException, NotSupportedException {

		// Given
		final Long kundeId = KUNDE_ID_VORHANDEN;
		final Long artikel1Id = ARTIKEL_1_ID;
		final Integer artikel1Anzahl = ARTIKEL_1_ANZAHL;
		final Long artikel2Id = ARTIKEL_2_ID;
		final Integer artikel2Anzahl = ARTIKEL_2_ANZAHL;
		final Double preis = (PREIS);
		// When

		Artikel artikel = as.findArtikelById(artikel1Id);
		final UserTransaction trans = getUserTransaction();
		trans.commit();

		Bestellung bestellung = new Bestellung();
		Bestellposition bpos = new Bestellposition(artikel,
				(int) artikel1Anzahl);
		bestellung.addBestellposition(bpos);

		bestellung.setPreis(preis);

		trans.begin();
		artikel = as.findArtikelById(artikel2Id);
		trans.commit();

		bpos = new Bestellposition(artikel, (int) artikel2Anzahl);
		bestellung.addBestellposition(bpos);
		bestellung.setStatus("Eingangen");

		trans.begin();
		Kunde kunde = ks.findKundeById(kundeId, FetchType.MIT_BESTELLUNGEN,
				LOCALE);
		assertThat(kunde, is(notNullValue()));
		trans.commit();

		kunde.addBestellung(bestellung);
		bestellung.setKunde(kunde);

		trans.begin();
		bestellung = bs.createBestellung(bestellung, kunde, LOCALE);
		assertThat(bestellung, is(notNullValue()));
		trans.commit();

		// Then
		assertThat(bestellung.getBestellpositionen().size(), is(2));
		for (Bestellposition bp : bestellung.getBestellpositionen()) {
			assertThat(bp.getArtikel().getId(),
					anyOf(is(artikel1Id), is(artikel2Id)));
		}

		kunde = bestellung.getKunde();
		assertThat(kunde.getId(), is(kundeId));
	}

	@Test
	public void deleteBestellung() throws RollbackException,
			HeuristicMixedException, HeuristicRollbackException,
			SystemException, NotSupportedException {
		// Given
		final Long bestellungId = BESTELLUNG_ID_DELETE_TEST;

		final Collection<Bestellung> bestellungVorher = bs
				.findAllBestellungen();
		final UserTransaction trans = getUserTransaction();
		trans.commit();

		// When
		trans.begin();
		final Bestellung bestellung = bs.findBestellungById(bestellungId);
		trans.commit();
		trans.begin();
		bs.deleteBestellung(bestellung);
		trans.commit();

		// Then
		trans.begin();
		final Collection<Bestellung> bestellungNachher = bs
				.findAllBestellungen();
		trans.commit();
		assertTrue(bestellungVorher.size() - 1 == bestellungNachher.size());
	}

	@Test
	public void neuerStatusFuerBestellung() throws RollbackException,
			HeuristicMixedException, HeuristicRollbackException,
			SystemException, NotSupportedException {
		// Given
		final Long bestellungId = BESTELLUNG_ID_VORHANDEN;

		// When
		Bestellung bestellung = bs.findBestellungById(bestellungId);
		final UserTransaction trans = getUserTransaction();
		trans.commit();

		final String alterStatus = bestellung.getStatus();
		final String neuerStatus = alterStatus
				+ alterStatus.charAt(alterStatus.length() - 1);
		bestellung.setStatus(neuerStatus);

		trans.begin();
		bestellung = bs.updateBestellung(bestellung);
		trans.commit();

		// Then
		assertThat(bestellung.getStatus(), is(neuerStatus));
		trans.begin();
		bestellung = bs.findBestellungById(bestellungId);
		trans.commit();
		assertThat(bestellung.getStatus(), is(neuerStatus));
	}

	@Test
	public void findBestellungByIdVorhanden() {
		// Given
		final Long id = BESTELLUNG_ID_VORHANDEN;

		// When
		final Bestellung bestellung = bs.findBestellungById(id);

		// Then
		assertThat(bestellung.getId(), is(id));
	}

	@Test
	public void findBestellungByIdNichtVorhanden() {
		// Given
		final Long id = BESTELLUNG_ID_NICHT_VORHANDEN;

		// When
		final Bestellung bestellung = bs.findBestellungById(id);

		// then
		assertThat(bestellung, is(nullValue()));
	}

	
	@Test
	public void findBestellungenByKundeIdVorhanden() {
		// Given
		final Long id = KUNDE_ID_VORHANDEN;

		// When
		final Collection<Bestellung> bestellungen = bs
				.findBestellungenByKundeId(id);

		// Then
		assertThat(bestellungen.isEmpty(), is(false));

		for (Bestellung b : bestellungen) {
			assertThat(b.getKunde().getId(), is(id));
		}
	}

	@Test
	public void findBestellungenByKundeIdNichtVorhanden() {
		// Given
		final Long id = KUNDE_ID_NICHT_VORHANDEN;

		// When
		final Collection<Bestellung> bestellungen = bs
				.findBestellungenByKundeId(id);

		// Then
		assertThat(bestellungen.isEmpty(), is(true));
	}

	@Test
	public void findKundeByBestellungIdVorhanden() {
		// Given
		final Long id = BESTELLUNG_ID_VORHANDEN;

		// When
		final Kunde kunde = bs.findKundeById(id);

		// Then
		assertThat(kunde.getId(), is(KUNDE_ID_VORHANDEN));
	}

	@Test
	public void findKundeByBestellungIdNichtVorhanden() {
		// Given
		final Long id = BESTELLUNG_ID_NICHT_VORHANDEN;

		// When // then
		final Kunde kunde = bs.findKundeById(id);

		// then
		assertThat(kunde, is(nullValue()));
	}

	@Test
	public void findLieferungVorhanden() {
		// Given
		final String lieferant = LIEFERANT_VORHANDEN;

		// When
		final Collection<Lieferung> lieferungen = bs
				.findLieferungenByLieferant(lieferant);

		// Then
		assertThat(lieferungen.isEmpty(), is(false));
		for (Lieferung l : lieferungen) {
			assertThat(l.getLieferant(), is(lieferant));

			final Collection<Bestellung> bestellungen = l.getBestellungen();

			assertThat(bestellungen.isEmpty(), is(false));

			for (Bestellung b : bestellungen) {
				assertThat(b.getKunde(), is(notNullValue()));
			}
		}
	}

	@Test
	public void findLieferungNichtVorhanden() {
		// Given
		final String lieferant = LIEFERANT_NICHT_VORHANDEN;

		// When
		final List<Lieferung> lieferungen = bs
				.findLieferungenByLieferant(lieferant);

		// Then
		assertThat(lieferungen.isEmpty(), is(true));
	}

	@Test
	public void createLieferung() throws RollbackException,
			HeuristicMixedException, HeuristicRollbackException,
			SystemException, NotSupportedException {
		// Given
		final Long bestellungId = BESTELLUNG_ID_VORHANDEN;
		final TransportType neueTransportArt = TransportType.SCHNELL;
		final String lieferant = LIEFERANT_NAME;
		// When
		Lieferung neueLieferung = new Lieferung();
		neueLieferung.setTransportArt(neueTransportArt);
		neueLieferung.setLieferant(lieferant);

		final List<Bestellung> bestellungen = new ArrayList<Bestellung>();
		final Bestellung bestellung = bs
				.findBestellungByIdMitLieferungen(bestellungId);
		final UserTransaction trans = getUserTransaction();
		trans.commit();

		assertThat(bestellung.getId(), is(bestellungId));
		bestellungen.add(bestellung);
		neueLieferung.setBestellungenAsList(bestellungen);

		
	}

	@Test
	public void createLieferung2() throws RollbackException,
			HeuristicMixedException, HeuristicRollbackException,
			SystemException, NotSupportedException {
		// Given
		final Long bestellungId2a = BESTELLUNG_ID2A_VORHANDEN;
		final Long bestellungId2b = BESTELLUNG_ID2B_VORHANDEN;
		final TransportType neueTransportArt = TransportType.STANDARD;

		// When
		Lieferung neueLieferung = new Lieferung();
		neueLieferung.setTransportArt(neueTransportArt);
		neueLieferung.setLieferant("DHL");

		final List<Bestellung> bestellungen = new ArrayList<Bestellung>();
		Bestellung bestellung = bs
				.findBestellungByIdMitLieferungen(bestellungId2a);
		final UserTransaction trans = getUserTransaction();
		trans.commit();

		assertThat(bestellung.getId(), is(bestellungId2a));
		bestellungen.add(bestellung);

		trans.begin();
		bestellung = bs.findBestellungByIdMitLieferungen(bestellungId2b);
		trans.commit();

		assertThat(bestellung.getId(), is(bestellungId2b));
		bestellungen.add(bestellung);

	}
}
