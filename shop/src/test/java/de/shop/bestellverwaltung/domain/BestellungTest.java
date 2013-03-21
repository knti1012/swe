package de.shop.bestellverwaltung.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.AbstractDomainTest;


@RunWith(Arquillian.class)
public class BestellungTest extends AbstractDomainTest {
	private static final Long KUNDEID = 3534482L;
	private static final Long BESTELLUNGID = 1006L;
	
	private static final Long KUNDEID_NICHT_VORHANDEN = 8888L;
	private static final Long BESTELLUNGID_NICHT_VORHANDEN = 10101010L;
	
	private static final Long NEU_BESTELLUNG_ARTIKEL_ID = 10004L;
	private static final Integer NEU_BESTELLPOS_ANZAHL_ARTIKEL = 2;
	private static final String NEU_BESTELLUNG_STATUS = "in Bearbeitung";
	
	@PersistenceContext
	private EntityManager em;

	@Test
	public void findBestellungByIdKunde() {
		// Given
		final Long id = KUNDEID;

		// When
		final TypedQuery<Bestellung> query = em.createNamedQuery(
				Bestellung.FIND_BESTELLUNGEN_BY_KUNDE, Bestellung.class);
		query.setParameter(Bestellung.PARAM_KUNDEID, id);
		final List<Bestellung> bestellungen = query.getResultList();
		// Then
		assertThat(bestellungen.isEmpty(), is(false));
		for (Bestellung b : bestellungen) {
			assertThat(b.getKunde().getId(), is(id));
		}
	}
	
	@Test
	public void findBestellungByIdKundeNichtVorhanden() {
		// Given
		final Long id = KUNDEID_NICHT_VORHANDEN;
		
		// When 
		final TypedQuery<Bestellung> query = em.createNamedQuery(
				Bestellung.FIND_BESTELLUNGEN_BY_KUNDE, Bestellung.class);
		query.setParameter(Bestellung.PARAM_KUNDEID, id);
		final List<Bestellung> bestellungen = query.getResultList();
		// Then
		assertThat(bestellungen.isEmpty(), is(true));
		
	}

	@Test
	public void findBestellungenById() {
		// Given
		final Long id = BESTELLUNGID;
		// When
		final Bestellung bestellung = getEntityManager()
				.createNamedQuery(Bestellung.FIND_BESTELLUNG_BY_ID,
						Bestellung.class).setParameter(Bestellung.PARAM_ID, id)
				.getSingleResult();

		// Then
		assertThat(bestellung.getId(), is(id));
	}

	@Test
	public void findBestellungByIdNichtVorhanden() {
		// Given
		final Long id =	BESTELLUNGID_NICHT_VORHANDEN;
		
		// When // Then
		thrown.expect(NoResultException.class);
		em.createNamedQuery(Bestellung.FIND_BESTELLUNG_BY_ID, Bestellung.class)
								      .setParameter(Bestellung.PARAM_ID, id).getSingleResult();	
	}

	@Test
	public void findKundeByIDBestellung() {
		// Given
		final Long id = BESTELLUNGID;
		// When
		final TypedQuery<Kunde> query = em.createNamedQuery(
				Bestellung.FIND_KUNDE_BY_IDBESTELLUNG, Kunde.class);
		query.setParameter(Bestellung.PARAM_ID, id);
		final Kunde kunde = query.getResultList().get(0);
		// Then
		assertThat(kunde.getId(), is(KUNDEID));
	}

	@Test
	public void createBestellung() {
		
		// Given
		Kunde kunde = em.find(Kunde.class, KUNDEID);
		Artikel artikel = em.find(Artikel.class, NEU_BESTELLUNG_ARTIKEL_ID);
		int anzahlArtikel = NEU_BESTELLPOS_ANZAHL_ARTIKEL;
		String bestellungStatus = NEU_BESTELLUNG_STATUS;
		int anzahlAlteBestellungen = getEntityManager()
						.createNamedQuery(Bestellung.FIND_ALLE_BESTELLUNG,
						Bestellung.class).getResultList().size();
		// when
		Bestellung bestellung = new Bestellung();
		bestellung.setStatus(bestellungStatus);

		Bestellposition bp = new Bestellposition();
		bp.setArtikel(artikel);
		bp.setAnzahl(anzahlArtikel);

		List<Bestellposition> bestellpositionen = new ArrayList<Bestellposition>();
		bestellpositionen.add(bp);

		bestellung.setBestellpositionen(bestellpositionen);
		bestellung.setKunde(kunde);

		List<Bestellung> bestellungen = new ArrayList<Bestellung>();
		bestellungen.add(bestellung);
		kunde.setBestellungen(bestellungen);

		try {
			getEntityManager().persist(bestellung);
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

		final List<Bestellung> neueBestellungs = getEntityManager()
				.createNamedQuery(Bestellung.FIND_ALLE_BESTELLUNG,
						Bestellung.class).getResultList();

		assertThat(neueBestellungs.size(), is(anzahlAlteBestellungen + 1));
		
	}
}
