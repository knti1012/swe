package de.shop.artikelverwaltung.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.util.AbstractDomainTest;

@RunWith(Arquillian.class)
public class ArtikelTest extends AbstractDomainTest {

	private static final Integer ANZAHL_ARTIKEL_PREIS_50 = 15;
	private static final Double PREIS = 50d;
	private static final Double PREIS_NICHT_VORHANDEN = 0.5d;
	private static final Integer ANZAHL_ARTIKEL_PREIS_05 = 0;
	private static final String KATEGORIE = "t-shirt";
	private static final Integer ANZAHL_ARTIKEL_KATEGORIE = 1;
	private static final Integer ANZAHL_ARTIKEL = 10;
	private static final String KATEGORIE_NICHT_VORHANDEN = "KSC-Fanartikel";
	private static final Integer ANZAHL_KATEGORIE_NICHT_VORHANDEN = 0;
	private static final String NAME = "rocket";
	private static final Integer ANZAHL_ARTIKEL_NAME = 1;
	private static final String NAME_NICHT_VORHANDEN = "Sommer";
	private static final Integer ANZAHL_NAME_NICHT_VORHANDEN = 0;

	
	private static final String ART_NEU = "maenner";
	private static final String FARBE_NEU = "beige";
	private static final String GROESSE_NEU = "xxxl";
	private static final String KATEGORIE_NEU = "jacke";
	private static final BigInteger LAGERBESTAND_NEU = new BigInteger("4000");
	private static final String NAME_NEU = "Soft-shell";
	
	private static final Double PREIS_NEU = 79d;

	@Test
	public void findVerfuegbareArtikel() {

		// given
		final Integer anzahl = ANZAHL_ARTIKEL;
		// when
		List<Artikel> artikelResult = getEntityManager().createNamedQuery(
				Artikel.FIND_VERFUEGBARE_ARTIKEL, Artikel.class)
				.getResultList();

		// then
		assertTrue(artikelResult.size() > anzahl);
	}

	@Test
	public void findArtikelByKategorieNichtVorhanden() {
		// given
		final String kat = KATEGORIE_NICHT_VORHANDEN;
		final Integer anzahl = ANZAHL_KATEGORIE_NICHT_VORHANDEN;
		// when
		List<Artikel> artikelResult = getEntityManager()
				.createNamedQuery(Artikel.FIND_ARTIKEL_BY_KATEGORIE,
						Artikel.class).setParameter(Artikel.PARAM_KAT, kat)
				.getResultList();
		// then
		assertThat(artikelResult.size(), is(anzahl));

		for (Artikel a : artikelResult) {

			assertTrue(a.getKategorie().equals(kat));
		}
	}

	@Test
	public void findArtikelByKategorie() {

		// given
		final String kat = KATEGORIE;
		final Integer anzahl = ANZAHL_ARTIKEL_KATEGORIE;
		// when
		List<Artikel> artikelResult = getEntityManager()
				.createNamedQuery(Artikel.FIND_ARTIKEL_BY_KATEGORIE,
						Artikel.class).setParameter(Artikel.PARAM_KAT, kat)
				.getResultList();
		// then
		assertThat(artikelResult.size(), is(anzahl));

		for (Artikel a : artikelResult) {

			assertTrue(a.getKategorie().equals(kat));
		}

	}
	
	@Test
	public void findArtikelByName() {
		//given
		final String name = NAME;
		final Integer anzahl = ANZAHL_ARTIKEL_NAME;
		
		//when
		List<Artikel> artikelResult = getEntityManager()
				.createNamedQuery(Artikel.FIND_ARTIKEL_BY_NAME, Artikel.class)
				.setParameter(Artikel.PARAM_NAME, name)
				.getResultList();
		//then
		assertThat(artikelResult.size(), is(anzahl));
		
		for (Artikel a : artikelResult) {
			
			assertTrue(a.getName().equals(name));
		}
	}

	@Test
	public void findArtikelByNameNichtVorhanden() {
		//given
		final String name = NAME_NICHT_VORHANDEN;
		final Integer anzahl = ANZAHL_NAME_NICHT_VORHANDEN;
		
		//when
		List<Artikel> artikelResult = getEntityManager()
				.createNamedQuery(Artikel.FIND_ARTIKEL_BY_NAME, Artikel.class)
				.setParameter(Artikel.PARAM_NAME, name)
				.getResultList();
		//then
		assertThat(artikelResult.size(), is(anzahl));
		

		
	}

	@Test
	public void findArtikelByMaxPreis() {

		// given
		final Integer anzahl = ANZAHL_ARTIKEL_PREIS_50;
		final Double preis = PREIS;

		// when
		List<Artikel> artikelResult = getEntityManager()
				.createNamedQuery(Artikel.FIND_ARTIKEL_MAX_PREIS, Artikel.class)
				.setParameter(Artikel.PARAM_PREIS, preis).getResultList();

		// then
		assertThat(artikelResult.size(), is(anzahl));

		for (Artikel a : artikelResult) {

			assertTrue(a.getPreis() <= preis);
		}
	}

	@Test
	public void findArtikelByMaxPreisNichtVorhanden() {

		// given
		final Integer anzahl = ANZAHL_ARTIKEL_PREIS_05;
		final Double preis = PREIS_NICHT_VORHANDEN;

		// when
		List<Artikel> artikelResult = getEntityManager()
				.createNamedQuery(Artikel.FIND_ARTIKEL_MAX_PREIS, Artikel.class)
				.setParameter(Artikel.PARAM_PREIS, preis).getResultList();

		// then
		assertThat(artikelResult.size(), is(anzahl));
	}

	@Test
	public void createArtikel() {
		// Given
		Artikel artikel = new Artikel();
		
		artikel.setArt(ART_NEU);
		artikel.setFarbe(FARBE_NEU);
		artikel.setGroesse(GROESSE_NEU);
		artikel.setKategorie(KATEGORIE_NEU);
		artikel.setLagerbestand(LAGERBESTAND_NEU);
		artikel.setName(NAME_NEU);
		artikel.setPreis(PREIS_NEU);

		// When
		try {
			getEntityManager().persist(artikel);
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

		final TypedQuery<Artikel> query = getEntityManager().createNamedQuery(
				Artikel.FIND_ARTIKEL_BY_KATEGORIE, Artikel.class);
		query.setParameter(Artikel.PARAM_KAT, KATEGORIE_NEU);
		final List<Artikel> artikelList = query.getResultList();

		assertThat(artikelList.size(), is(2));
		artikel = (Artikel) artikelList.get(0);
		assertThat(artikel.getId().longValue() > 0, is(true));
		assertThat(artikel.getKategorie(), is(KATEGORIE_NEU));
	}

}
