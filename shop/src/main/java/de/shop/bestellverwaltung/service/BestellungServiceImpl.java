package de.shop.bestellverwaltung.service;

import static de.shop.util.Constants.KEINE_ID;


import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Lieferung;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.util.Log;
import de.shop.util.ValidationService;

@Log
public class BestellungServiceImpl implements Serializable, BestellungService {
	private static final long serialVersionUID = -9145947650157430928L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles
			.lookup().lookupClass().getName());

	@PersistenceContext
	private transient EntityManager em;

	@Inject
	private KundeService ks;

	@Inject
	private ValidationService validationService;

	@Inject
	@NeueBestellung
	private transient Event<Bestellung> event;

	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean {0} wurde erzeugt", this);
	}

	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean {0} wird geloescht", this);
	}

	@Override
	public Bestellung findBestellungById(Long id) {
		try {
			final Bestellung bestellung = em.find(Bestellung.class, id);
			return bestellung;
		}

		catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public List<Bestellung> findBestellungenByLieferung(Long id) {
		try {
			final List<Bestellung> bestellungen = em
					.createNamedQuery(
							Bestellung.FIND_BESTELLUNGEN_BY_IDLIEFERUNG,
							Bestellung.class)
					.setParameter(Bestellung.PARAM_LIEFERUNGEN, id)
					.getResultList();
			return bestellungen;
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public Bestellung updateBestellung(Bestellung bestellung) {
		if (bestellung == null) {
			return null;
		}

		em.merge(bestellung);
		return bestellung;
	}

	@Override
	public Bestellung findBestellungByIdMitLieferungen(Long id) {
		try {
			final Bestellung bestellung = em
					.createNamedQuery(
							Bestellung.FIND_BESTELLUNG_BY_ID_FETCH_LIEFERUNGEN,
							Bestellung.class)
					.setParameter(Bestellung.PARAM_ID, id).getSingleResult();
			return bestellung;
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public Kunde findKundeById(Long id) {
		try {
			final Kunde kunde = em
					.createNamedQuery(Bestellung.FIND_KUNDE_BY_IDBESTELLUNG,
							Kunde.class).setParameter(Bestellung.PARAM_ID, id)
					.getSingleResult();
			return kunde;
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public List<Bestellung> findAllBestellungen() {
		try {
			final List<Bestellung> bestellungen;
			bestellungen = em.createNamedQuery(Bestellung.FIND_ALLE_BESTELLUNG,
					Bestellung.class).getResultList();
			return bestellungen;
		}

		catch (NoResultException e) {
			return null;
		}

	}

	@Override
	public List<Bestellung> findBestellungenByKundeId(Long kundeId) {
		try {
			final List<Bestellung> bestellungen = em
					.createNamedQuery(Bestellung.FIND_BESTELLUNGEN_BY_KUNDE,
							Bestellung.class)
					.setParameter(Bestellung.PARAM_KUNDEID, kundeId)
					.getResultList();
			return bestellungen;
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public Bestellung createBestellung(Bestellung bestellung, Kunde kunde,
			Locale locale) {
		if (bestellung == null) {
			return null;
		}

		for (Bestellposition bp : bestellung.getBestellpositionen()) {
			LOGGER.debugf("Bestellposition: {0}", bp);
		}

		kunde = ks.findKundeById(kunde.getId(), FetchType.MIT_BESTELLUNGEN,
				locale);
		kunde.addBestellung(bestellung);
		bestellung.setKunde(kunde);

		bestellung.setId(KEINE_ID);
		for (Bestellposition bp : bestellung.getBestellpositionen()) {
			bp.setId(KEINE_ID);
		}

		validateBestellung(bestellung, locale, Default.class);
		em.persist(bestellung);
		event.fire(bestellung);

		return bestellung;
	}

	private void validateBestellung(Bestellung bestellung, Locale locale,
			Class<?>... groups) {
		final Validator validator = validationService.getValidator(locale);
		System.out.println("Bestellung Validator fuer Deutsch: " + validator);
		final Set<ConstraintViolation<Bestellung>> violations = validator
				.validate(bestellung);
		if (violations != null && !violations.isEmpty()) {
			LOGGER.errorf("BestellungService", "createBestellung", violations);
			throw new BestellungValidationException(bestellung, violations);
		}
	}

	@Override
	public List<Artikel> ladenhueter(int anzahl) {
		final List<Artikel> artikel = em
				.createNamedQuery(Bestellposition.FIND_LADENHUETER,
						Artikel.class).setMaxResults(anzahl).getResultList();
		return artikel;
	}

	@Override
	public List<Lieferung> findLieferungenByLieferant(String lieferant) {
		final List<Lieferung> lieferungen = em
				.createNamedQuery(Lieferung.FIND_LIEFERUNGEN_BY_LIEFERANT,
						Lieferung.class)
				.setParameter(Lieferung.PARAM_LIEFERANT, lieferant)
				.getResultList();
		return lieferungen;
	}

	@Override
	public List<Bestellposition> findBestellpositionenById(Long id) {
		final List<Bestellposition> bestellpositionen = em
				.createNamedQuery(Bestellung.FIND_BESTELLPOSITIONEN_BY_ID,
						Bestellposition.class)
				.setParameter(Bestellung.PARAM_ID, id).getResultList();
		return bestellpositionen;
	}

	@Override
	public void deleteBestellung(Bestellung bestellung) {
		if (bestellung == null) {
			return;
		}

		bestellung = findBestellungById(bestellung.getId());

		if (bestellung == null) {
			return;
		}
		em.remove(bestellung);
	}

	@Override
	public Lieferung createLieferung(Lieferung lieferung,
			List<Bestellung> bestellungen) {
		if (lieferung == null || bestellungen == null || bestellungen.isEmpty()) {
			return null;
		}

		final List<Long> ids = new ArrayList<>();
		for (Bestellung b : bestellungen) {
			ids.add(b.getId());
		}

		bestellungen = findBestellungenByIds(ids);
		lieferung.setBestellungenAsList(bestellungen);
		for (Bestellung bestellung : bestellungen) {
			bestellung.addLieferung(lieferung);
		}

		lieferung.setId(KEINE_ID);
		em.persist(lieferung);
		return lieferung;
	}

	private List<Bestellung> findBestellungenByIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return null;
		}

		
		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<Bestellung> criteriaQuery = builder
				.createQuery(Bestellung.class);
		final Root<Bestellung> b = criteriaQuery.from(Bestellung.class);
		b.fetch("lieferungen", JoinType.LEFT);

		final Path<Long> idPath = b.get("id");
		final List<Predicate> predList = new ArrayList<>();
		for (Long id : ids) {
			final Predicate equal = builder.equal(idPath, id);
			predList.add(equal);
		}
		final Predicate[] predArray = new Predicate[predList.size()];
		final Predicate pred = builder.or(predList.toArray(predArray));
		criteriaQuery.where(pred).distinct(true);

		final TypedQuery<Bestellung> query = em.createQuery(criteriaQuery);
		final List<Bestellung> bestellungen = query.getResultList();
		return bestellungen;
	}

}
