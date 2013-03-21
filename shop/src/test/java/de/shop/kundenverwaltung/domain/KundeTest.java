package de.shop.kundenverwaltung.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.util.AbstractDomainTest;

@RunWith(Arquillian.class)
public class KundeTest extends AbstractDomainTest {
	private static final String NACHNAME_VORHANDEN = "Drescher";
	private static final String NACHNAME_NICHT_VORHANDEN = "Nicht";
	private static final Long ID_VORHANDEN = 2239833L;
	private static final String EMAIL_VORHANDEN = "MandyDrescher@spambob.com";
	private static final String EMAIL_NICHT_VORHANDEN = "Nicht";
	private static final String GESCHLECHT = "W";
	private static final String PASSWORT = "55231n";

	private static final String NACHNAME_NEU = "Test";
	private static final String VORNAME_NEU = "Theo";
	private static final String EMAIL_NEU = "theo@test.de";
	private static final String PLZ_NEU = "11111";
	private static final String STADT_NEU = "Testort";
	private static final String STRASSE_NEU = "Testweg";
	private static final Integer HAUSNR_NEU = 24;

	@Test
	public void findKundeByIdVorhanden() {
		// Given
		final Long id = ID_VORHANDEN;

		// When
		final Kunde kunde = getEntityManager()
				.createNamedQuery(Kunde.FIND_KUNDEN_BY_ID, Kunde.class)
				.setParameter(Kunde.PARAM_KUNDE_ID, id).getSingleResult();

		// Then
		assertThat(kunde.getId(), is(id));
	}

	@Test
	public void findKundeByEmailVorhanden() {
		// Given
		final String email = EMAIL_VORHANDEN;

		// When
		final Kunde kunde = getEntityManager()
				.createNamedQuery(Kunde.FIND_KUNDE_BY_EMAIL, Kunde.class)
				.setParameter(Kunde.PARAM_KUNDE_EMAIL, email).getSingleResult();

		// Then
		assertThat(kunde.getEmail(), is(email));
	}

	@Test
	public void findKundeByEmailNichtVorhanden() {
		// Given
		final String email = EMAIL_NICHT_VORHANDEN;

		// When / Then
		thrown.expect(NoResultException.class);
		getEntityManager()
				.createNamedQuery(Kunde.FIND_KUNDE_BY_EMAIL, Kunde.class)
				.setParameter(Kunde.PARAM_KUNDE_EMAIL, email).getSingleResult();
	}

	@Test
	public void findKundenByNachnameVorhanden() {
		// Given
		final String nachname = NACHNAME_VORHANDEN;

		// When
		final TypedQuery<Kunde> query = getEntityManager().createNamedQuery(
				Kunde.FIND_KUNDEN_BY_NACHNAME, Kunde.class);
		query.setParameter(Kunde.PARAM_KUNDE_NACHNAME, nachname);
		final List<Kunde> kunden = query.getResultList();

		// Then
		assertThat(kunden.isEmpty(), is(false));
		for (Kunde k : kunden) {
			assertThat(k.getNachname(), is(nachname));
		}
	}

	@Test
	public void findKundenByNachnameNichtVorhanden() {
		// Given
		final String nachname = NACHNAME_NICHT_VORHANDEN;

		// When
		final List<Kunde> kunden = getEntityManager()
				.createNamedQuery(Kunde.FIND_KUNDEN_BY_NACHNAME, Kunde.class)
				.setParameter(Kunde.PARAM_KUNDE_NACHNAME, nachname)
				.getResultList();

		// Then
		assertThat(kunden.isEmpty(), is(true));
	}

	@Test
	public void createkunde() {
		// Given

		TypedQuery<Kunde> query1 = getEntityManager().createNamedQuery(
				Kunde.FIND_KUNDEN, Kunde.class);
		int anzahlKundenAlt = query1.getResultList().size();

		TypedQuery<Kunde> query2 = getEntityManager().createNamedQuery(
				Kunde.FIND_KUNDEN_BY_NACHNAME, Kunde.class);
		query2.setParameter(Kunde.PARAM_KUNDE_NACHNAME, NACHNAME_NEU);
		int anzahlKundenMitNachnameVorhanden = query2.getResultList().size();

		Kunde kunde = new Kunde();
		kunde.setNachname(NACHNAME_NEU);
		kunde.setVorname(VORNAME_NEU);
		kunde.setEmail(EMAIL_NEU);
		kunde.setGeschlecht(GESCHLECHT);
		kunde.setPassword(PASSWORT);

		final Adresse adresse = new Adresse();
		adresse.setPlz(PLZ_NEU);
		adresse.setStadt(STADT_NEU);
		adresse.setStrasse(STRASSE_NEU);
		adresse.setHausnummer(HAUSNR_NEU);
		adresse.setLand("land");
		kunde.setAdresse(adresse);

		// When
		try {
			getEntityManager().persist(kunde);
		} catch (ConstraintViolationException e) {

			final Set<ConstraintViolation<?>> violations = e
					.getConstraintViolations();
			for (ConstraintViolation<?> v : violations) {
				System.err.println("!!! FEHLERMELDUNG>>> " + v.getMessage());
				System.err.println("!!! ATTRIBUT>>> " + v.getPropertyPath());
				System.err
						.println("!!! ATTRIBUTWERT>>> " + v.getInvalidValue());
			}
			throw new RuntimeException(e);
		}

		// Then

		TypedQuery<Kunde> query3 = getEntityManager().createNamedQuery(
				Kunde.FIND_KUNDEN, Kunde.class);
		int anzahlKundenNeu = query3.getResultList().size();

		TypedQuery<Kunde> query4 = getEntityManager().createNamedQuery(
				Kunde.FIND_KUNDEN_BY_NACHNAME, Kunde.class);
		query4.setParameter(Kunde.PARAM_KUNDE_NACHNAME, NACHNAME_NEU);
		final List<Kunde> kunden = query4.getResultList();

		assertThat(anzahlKundenNeu, is(anzahlKundenAlt + 1));

		assertThat(kunden.size(), is(anzahlKundenMitNachnameVorhanden + 1));
		kunde = kunden.get(0);
		assertThat(kunde.getId() > 0, is(true));
		assertThat(kunde.getNachname(), is(NACHNAME_NEU));
	}
}
