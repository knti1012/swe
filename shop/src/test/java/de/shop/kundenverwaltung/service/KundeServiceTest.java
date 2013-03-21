package de.shop.kundenverwaltung.service;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Date;
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

import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.util.AbstractTest;

@RunWith(Arquillian.class)
public class KundeServiceTest extends AbstractTest {
	private static final String KUNDE_NACHNAME_VORHANDEN = "Drescher";
	private static final Long KUNDE_ID_VORHANDEN = Long.valueOf(2239833);
	private static final Long KUNDE_ID_NICHT_VORHANDEN = Long.valueOf(1000);
	private static final Long KUNDE_ID_DELETE_TEST = Long.valueOf(4473720);
	private static final String KUNDE_NACHNAME_NICHT_VORHANDEN = "Beta";

	private static final String PLZ_VORHANDEN = "92619";
	private static final String PLZ_NICHT_VORHANDEN = "000";

	private static final String NACHNAME_NEU = "Test";
	private static final String EMAIL_NEU = "theo@test.de";
	private static final String PASSWORT_NEU = "55231n";
	private static final String GESCHLECHT_NEU2 = "W";

	private static final String VORNAME_NEU = "Someone";
	private static final String PLZ_NEU = "76133";
	private static final String STADT_NEU = "Karlsruhe";
	private static final String STRASSE_NEU = "Moltkestra\u00DFe";
	private static final Integer HAUSNR_NEU = 40;

	@Inject
	private KundeService ks;

	@Test
	public void findKundenMitNachnameVorhanden() {
		// Given
		final String nachname = KUNDE_NACHNAME_VORHANDEN;

		// When
		final Collection<Kunde> kunden = ks.findKundenByNachname(nachname,
				FetchType.NUR_KUNDE, LOCALE);

		// Then
		assertThat(kunden, is(notNullValue()));

		assertThat(kunden.isEmpty(), is(false));

		for (Kunde k : kunden) {
			assertThat(k.getNachname(), is(nachname));
		}
	}

	@Test
	public void findKundeById() {
		// Given
		final Long id = KUNDE_ID_VORHANDEN;

		// When
		final Kunde kunde = ks.findKundeById(id, FetchType.NUR_KUNDE, LOCALE);
		// Then
		assertThat(kunde.getId(), is(id));

	}

	@Test
	public void findKundenMitNachnameNichtVorhanden() {
		// Given
		final String nachname = KUNDE_NACHNAME_NICHT_VORHANDEN;

		// When
		final List<Kunde> kunden = ks.findKundenByNachname(nachname,
				FetchType.NUR_KUNDE, LOCALE);

		// Then
		assertThat(kunden.isEmpty(), is(true));
	}

	@Test
	public void findKundenMitIdNichtVorhanden() {
		// Given
		final Long id = KUNDE_ID_NICHT_VORHANDEN;

		// When
		final Kunde kunde = ks.findKundeById(id, FetchType.NUR_KUNDE, LOCALE);

		// Then
		assertThat(kunde, is(nullValue()));
	}

	@Test
	public void findKundenMitPLZVorhanden() {
		// Given
		final String plz = PLZ_VORHANDEN;

		// When
		final Collection<Kunde> kunden = ks.findKundenByPLZ(plz);

		// Then
		assertThat(kunden, is(notNullValue()));
		assertThat(kunden.isEmpty(), is(false));

		for (Kunde k : kunden) {
			assertThat(k.getAdresse(), is(notNullValue()));
			assertThat(k.getAdresse().getPlz(), is(plz));
		}
	}

	@Test
	public void findKundenMitPLZNichtVorhanden() {
		// Given
		final String plz = PLZ_NICHT_VORHANDEN;

		// When
		final List<Kunde> kunden = ks.findKundenByPLZ(plz);

		// Then
		assertTrue(kunden.isEmpty());
	}


	@Test
	public void findKundenByNachnameCriteria() {
		// Given
		final String nachname = KUNDE_NACHNAME_VORHANDEN;

		// When
		final List<Kunde> kunden = ks.findKundenByNachnameCriteria(nachname);

		// Then
		assertTrue(!(kunden.isEmpty()));
		for (Kunde k : kunden) {

			assertThat(k.getNachname(), is(nachname));
		}
	}


	@Test
	public void createkunde() throws RollbackException,
			HeuristicMixedException, HeuristicRollbackException,
			SystemException, NotSupportedException {
		// Given

		final Collection<Kunde> kundenVorher = ks.findAllKunden(
				FetchType.NUR_KUNDE, null);
		final UserTransaction trans = getUserTransaction();
		trans.commit();

		Kunde kunde = new Kunde();
		kunde.setNachname(NACHNAME_NEU);
		kunde.setVorname(VORNAME_NEU);
		kunde.setEmail(EMAIL_NEU);
		kunde.setGeschlecht(GESCHLECHT_NEU2);
		kunde.setPassword(PASSWORT_NEU);

		final Adresse adresse = new Adresse();
		adresse.setPlz(PLZ_NEU);
		adresse.setStadt(STADT_NEU);
		adresse.setStrasse(STRASSE_NEU);
		adresse.setHausnummer(HAUSNR_NEU);
		adresse.setLand("land");
		kunde.setAdresse(adresse);

		final Date datumVorher = new Date();

		trans.begin();
		Kunde neuerKunde = ks.createKunde(kunde, LOCALE);
		trans.commit();

		assertThat(datumVorher.getTime() <= neuerKunde.getErzeugt().getTime(),
				is(true));

		trans.begin();
		final Collection<Kunde> kundenNachher = ks.findAllKunden(
				FetchType.NUR_KUNDE, null);
		trans.commit();

		assertThat(kundenVorher.size() + 1, is(kundenNachher.size()));
		for (Kunde k : kundenVorher) {
			assertTrue(k.getErzeugt().getTime() < neuerKunde.getErzeugt()
					.getTime());
		}

		trans.begin();
		neuerKunde = ks.findKundeById(neuerKunde.getId(), FetchType.NUR_KUNDE,
				LOCALE);
		trans.commit();

		assertThat(neuerKunde.getNachname(), is(NACHNAME_NEU));
		assertThat(neuerKunde.getEmail(), is(EMAIL_NEU));
		assertThat(neuerKunde.getGeschlecht(), is(GESCHLECHT_NEU2));
		assertThat(neuerKunde.getPassword(), is(PASSWORT_NEU));
	}

	@Test
	public void createDuplikatKunde() throws RollbackException,
			HeuristicMixedException, HeuristicRollbackException,
			SystemException, NotSupportedException {
		// Given
		final Long kundeId = KUNDE_ID_VORHANDEN;

		final Kunde k = ks.findKundeById(kundeId, FetchType.NUR_KUNDE, LOCALE);
		final UserTransaction trans = getUserTransaction();
		trans.commit();

		assertThat(k, is(notNullValue()));
		assertThat(k, is(instanceOf(Kunde.class)));

		// When
		final Kunde neuerKunde = new Kunde();
		neuerKunde.setNachname(k.getNachname());
		neuerKunde.setVorname(k.getVorname());
		neuerKunde.setEmail(k.getEmail());
		neuerKunde.setAdresse(k.getAdresse());

		// Then
		thrown.expect(EmailExistsException.class);
		trans.begin();
		ks.createKunde(neuerKunde, LOCALE);
	}

	@Test
	public void deleteKunde() throws RollbackException,
			HeuristicMixedException, HeuristicRollbackException,
			SystemException, NotSupportedException {
		// Given
		final Long kundeId = KUNDE_ID_DELETE_TEST;

		final Collection<Kunde> kundenVorher = ks.findAllKunden(
				FetchType.NUR_KUNDE, null);
		final UserTransaction trans = getUserTransaction();
		trans.commit();

		// When
		trans.begin();
		final Kunde kunde = ks.findKundeById(kundeId,
				FetchType.MIT_BESTELLUNGEN, LOCALE);
		trans.commit();
		trans.begin();
		ks.deleteKunde(kunde);
		trans.commit();

		// Then
		trans.begin();
		final Collection<Kunde> kundenNachher = ks.findAllKunden(
				FetchType.NUR_KUNDE, null);
		trans.commit();
		assertTrue(kundenVorher.size() - 1 == kundenNachher.size());
	}

	@Test
	public void neuerNameFuerKunde() throws RollbackException,
			HeuristicMixedException, HeuristicRollbackException,
			SystemException, NotSupportedException {
		// Given
		final Long kundeId = KUNDE_ID_VORHANDEN;

		// When
		Kunde kunde = ks.findKundeById(kundeId, FetchType.NUR_KUNDE, LOCALE);
		final UserTransaction trans = getUserTransaction();
		trans.commit();

		final String alterNachname = kunde.getNachname();
		final String neuerNachname = alterNachname
				+ alterNachname.charAt(alterNachname.length() - 1);
		kunde.setNachname(neuerNachname);

		trans.begin();
		kunde = ks.updateKunde(kunde, LOCALE);
		trans.commit();

		// Then
		assertThat(kunde.getNachname(), is(neuerNachname));
		trans.begin();
		kunde = ks.findKundeById(kundeId, FetchType.NUR_KUNDE, LOCALE);
		trans.commit();
		assertThat(kunde.getNachname(), is(neuerNachname));
	}

}
