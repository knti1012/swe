package de.shop.artikelverwaltung.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;

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
import de.shop.util.AbstractTest;

@RunWith(Arquillian.class)
public class ArtikelServiceTest extends AbstractTest {

	private static final String ART_NEU = "maenner";
	private static final String FARBE_NEU = "beige";
	private static final String GROESSE_NEU = "xxxl";
	private static final String KATEGORIE_NEU = "jacke";
	private static final BigInteger LAGERBESTAND_NEU = new BigInteger("4000");
	private static final String NAME_NEU = "Soft-shell";
	private static final Double PREIS_NEU = 79d;

	
	private static final Long ID_ARTIKEL_VORHANDEN = (long) 10004;
	private static final Long ID_ARTIKEL_NICHT_VORHANDEN = (long) 1000;
	private static final String NAME_VORHANDEN = "rocket";
	private static final Integer ANZAHL_NAME = 1;
	private static final String NAME_NICHT_VORHANDEN = "Sommer";
	private static final Integer ANZAHL_NAME_NICHT_VORHANDEN = 0;
	
	private static final Double PREIS_VORHANDEN = 50d;
	private static final Double PREIS_NICHT_VORHANDEN = 0.5d;
	private static final Integer ANZAHL_ARTIKEL_PREIS_NICHT_VORHANDEN = 0;

	private static final Long ARTIKEL_ID_DELETE = (long) 10008;
	private static final BigInteger ARTIKEL_NEUER_LAGERBESTAND = new BigInteger(
			"5000");

	@Inject
	private ArtikelService as;

	@Test
	public void findVerfuegbareArtikel() throws RollbackException,
			HeuristicMixedException, HeuristicRollbackException,
			SystemException, NotSupportedException {

		// given
		

		final UserTransaction trans = getUserTransaction();
		trans.commit();
		// when
		final Collection<Artikel> artikelVorher = as.findVerfuegbareArtikel();
		// then
		trans.begin();
		final Collection<Artikel> artikelNachher = as.findVerfuegbareArtikel();
		trans.commit();
		assertThat(artikelVorher.size(), is(artikelNachher.size()));
		
	}

	@Test
	public void findArtikelMitIDVorhanden() {
		// given
		final Long id = ID_ARTIKEL_VORHANDEN;
		// when
		final Artikel artikel = as.findArtikelById(id);
		// then
		assertThat(artikel.getId(), is(ID_ARTIKEL_VORHANDEN));
	}

	@Test
	public void findArtikelMitIDNichtVorhanden() {
		// given
		final Long id = ID_ARTIKEL_NICHT_VORHANDEN;
		// when
		final Artikel artikel = as.findArtikelById(id);
		// then
		assertThat(artikel, is(nullValue()));

	}

	@Test
	public void findArtikelMitNameVorhanden() {
		// given
		final String name = NAME_VORHANDEN;
		final Integer anzahl = ANZAHL_NAME;
		// when
		final Collection<Artikel> artikelResult = as.findArtikelByName(name);
		// then
		assertThat(artikelResult, is(notNullValue()));
		assertThat(artikelResult.size(), is(anzahl));

		for (Artikel a : artikelResult) {
			assertTrue(a.getName().equals(name));
		}

	}

	@Test
	public void findArtikelMitNameNichtVorhanden() {
		// given
		final String name = NAME_NICHT_VORHANDEN;
		final Integer anzahl = ANZAHL_NAME_NICHT_VORHANDEN;
		// when
		final Collection<Artikel> artikelResult = as.findArtikelByName(name);
		// then
		assertThat(artikelResult.size(), is(anzahl));

	}

	@Test
	public void findArtikelMitMaxPreisVorhanden() throws RollbackException,
			HeuristicMixedException, HeuristicRollbackException,
			SystemException, NotSupportedException {
		// given
		final Double preis = PREIS_VORHANDEN;
		

		final UserTransaction trans = getUserTransaction();
		trans.commit();
		// when
		final Collection<Artikel> artikelVorher = as
				.findArtikelByMaxPreis(preis);
		// then

		trans.begin();
		final Collection<Artikel> artikelNachher = as
				.findArtikelByMaxPreis(preis);
		trans.commit();
		assertThat(artikelVorher.size(), is(artikelNachher.size()));

		for (Artikel a : artikelVorher) {
			assertTrue(a.getPreis() <= preis);
		}
	}

	@Test
	public void findArtikelMitMaxPreisNichtVorhanden() {
		// given
		final Double preis = PREIS_NICHT_VORHANDEN;
		final Integer anzahl = ANZAHL_ARTIKEL_PREIS_NICHT_VORHANDEN;
		// when
		final Collection<Artikel> artikelResult = as
				.findArtikelByMaxPreis(preis);
		// then
		assertThat(artikelResult.size(), is(anzahl));
	}

	@Test
	public void createArtikel() throws RollbackException,
			HeuristicMixedException, HeuristicRollbackException,
			SystemException, NotSupportedException {
		// Given

		final String art = ART_NEU;
		final String farbe = FARBE_NEU;
		final String groesse = GROESSE_NEU;
		final String kategorie = KATEGORIE_NEU;
		final BigInteger lagerbestand = LAGERBESTAND_NEU;
		final String name = NAME_NEU;
		final Double preis = PREIS_NEU;

		final Collection<Artikel> artikelVorher = as.findVerfuegbareArtikel();

		// When

		final UserTransaction trans = getUserTransaction();

		Artikel artikel = new Artikel();
		artikel.setArt(art);
		artikel.setFarbe(farbe);
		artikel.setGroesse(groesse);
		artikel.setKategorie(kategorie);
		artikel.setLagerbestand(lagerbestand);
		artikel.setName(name);
		artikel.setPreis(preis);

		final Date datumVorher = new Date();

		Artikel neuerArtikel = as.createArtikel(artikel, LOCALE);
		trans.commit();

		// Then
		assertThat(
				datumVorher.getTime() <= neuerArtikel.getErzeugt().getTime(),
				is(true));

		trans.begin();
		final Collection<Artikel> artikelList = as.findVerfuegbareArtikel();
		trans.commit();

		assertThat(artikelVorher.size() + 1, is(artikelList.size()));
		for (Artikel a : artikelList) {
			assertThat(a.getId().longValue() > 0, is(true));
			if (a.getName() == name)
				assertThat(a.getKategorie(), is(KATEGORIE_NEU));

		}
	}

	@Test
	public void deleteArtikel() throws RollbackException,
			HeuristicMixedException, HeuristicRollbackException,
			SystemException, NotSupportedException {
		// Given
		final Long artikelId = ARTIKEL_ID_DELETE;

		final Collection<Artikel> artikelVorher = as.findVerfuegbareArtikel();
		final UserTransaction trans = getUserTransaction();
		trans.commit();

		// When
		trans.begin();
		final Artikel artikel = as.findArtikelById(artikelId);
		trans.commit();

		trans.begin();
		as.deleteArtikel(artikel);
		trans.commit();

		// Then
		trans.begin();
		final Collection<Artikel> artikelNachher = as.findVerfuegbareArtikel();
		trans.commit();
		assertThat(artikelVorher.size() - 1, is(artikelNachher.size()));
	}

	@Test
	public void neuerLagerbestandFuerArtikel() throws RollbackException,
			HeuristicMixedException, HeuristicRollbackException,
			SystemException, NotSupportedException {
		// Given
		final Long artikelId = ID_ARTIKEL_VORHANDEN;
		final BigInteger neuerLagerbestand = ARTIKEL_NEUER_LAGERBESTAND;

		// When
		Artikel artikel = as.findArtikelById(artikelId);
		final UserTransaction trans = getUserTransaction();
		trans.commit();

		
		artikel.setLagerbestand(neuerLagerbestand);

		trans.begin();
		artikel = as.updateArtikel(artikel);
		trans.commit();

		// Then
		assertThat(artikel.getLagerbestand(), is(neuerLagerbestand));
		trans.begin();
		artikel = as.findArtikelById(artikelId);
		trans.commit();
		assertThat(artikel.getLagerbestand(), is(neuerLagerbestand));
	}

}