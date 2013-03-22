package de.shop.kundenverwaltung.service;

import static de.shop.util.Constants.KEINE_ID;
import static java.util.logging.Level.FINER;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellposition_;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Bestellung_;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.domain.Kunde_;
import de.shop.kundenverwaltung.domain.PasswordGroup;
import de.shop.util.Default;
import de.shop.util.IdGroup;
import de.shop.util.Log;
import de.shop.util.ValidatorProvider;

@Log
public class KundeService implements Serializable {
	private static final long serialVersionUID = -5520738420154763865L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles
			.lookup().lookupClass().getName());

	public enum FetchType {
		NUR_KUNDE, MIT_BESTELLUNGEN
	}

	public enum OrderType {
		KEINE, ID
	}

	@PersistenceContext
	private transient EntityManager em;

	@Inject
	private ValidatorProvider validatorProvider;

	@PostConstruct
	private void postConstruct() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wurde erzeugt", this);
	}

	@PreDestroy
	private void preDestroy() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wird geloescht", this);
	}

	public List<Kunde> findAllKunden(FetchType fetch, OrderType order) {
		List<Kunde> kunden;
		switch (fetch) {
		case NUR_KUNDE:
			kunden = OrderType.ID.equals(order) ? em.createNamedQuery(
					Kunde.FIND_KUNDEN_ORDER_BY_ID, Kunde.class).getResultList()
					: em.createNamedQuery(Kunde.FIND_KUNDEN, Kunde.class)
							.getResultList();
			break;

		case MIT_BESTELLUNGEN:
			kunden = em.createNamedQuery(Kunde.FIND_KUNDEN_FETCH_BESTELLUNGEN,
					Kunde.class).getResultList();
			break;

		default:
			kunden = OrderType.ID.equals(order) ? em.createNamedQuery(
					Kunde.FIND_KUNDEN_ORDER_BY_ID, Kunde.class).getResultList()
					: em.createNamedQuery(Kunde.FIND_KUNDEN, Kunde.class)
							.getResultList();
			break;
		}

		return kunden;
	}

	public List<Kunde> findKundenByNachname(String nachname, FetchType fetch,
			Locale locale) {

		validateNachname(nachname, locale);

		List<Kunde> kunden;
		switch (fetch) {
		case NUR_KUNDE:
			kunden = em
					.createNamedQuery(Kunde.FIND_KUNDEN_BY_NACHNAME,
							Kunde.class)
					.setParameter(Kunde.PARAM_KUNDE_NACHNAME, nachname)
					.getResultList();
			break;

		case MIT_BESTELLUNGEN:
			kunden = em
					.createNamedQuery(
							Kunde.FIND_KUNDEN_BY_NACHNAME_FETCH_BESTELLUNGEN,
							Kunde.class)
					.setParameter(Kunde.PARAM_KUNDE_NACHNAME, nachname)
					.getResultList();
			break;

		default:
			kunden = em
					.createNamedQuery(Kunde.FIND_KUNDEN_BY_NACHNAME,
							Kunde.class)
					.setParameter(Kunde.PARAM_KUNDE_NACHNAME, nachname)
					.getResultList();
			break;
		}

		return kunden;
	}

	private void validateNachname(String nachname, Locale locale) {
		final Validator validator = validatorProvider.getValidator(locale);
		final Set<ConstraintViolation<Kunde>> violations = validator
				.validateValue(Kunde.class, "nachname", nachname, Default.class);
		if (!violations.isEmpty())
			throw new InvalidNachnameException(nachname, violations);
	}

	public List<String> findNachnamenByPrefix(String nachnamePrefix) {
		final List<String> nachnamen = em
				.createNamedQuery(Kunde.FIND_NACHNAMEN_BY_PREFIX, String.class)
				.setParameter(Kunde.PARAM_KUNDE_NACHNAME_PREFIX,
						nachnamePrefix + '%').getResultList();
		return nachnamen;
	}

	public Kunde findKundeById(Long id, FetchType fetch, Locale locale) {
		validateKundeId(id, locale);

		Kunde kunde = null;
		try {
			switch (fetch) {
			case NUR_KUNDE:
				kunde = em.find(Kunde.class, id);
				break;

			case MIT_BESTELLUNGEN:
				kunde = em
						.createNamedQuery(
								Kunde.FIND_KUNDE_BY_ID_FETCH_BESTELLUNGEN,
								Kunde.class)
						.setParameter(Kunde.PARAM_KUNDE_ID, id)
						.getSingleResult();
				break;

			default:
				kunde = em.find(Kunde.class, id);
				break;
			}
		} catch (NoResultException e) {
			return null;
		}

		return kunde;
	}

	private void validateKundeId(Long kundeId, Locale locale) {
		final Validator validator = validatorProvider.getValidator(locale);
		final Set<ConstraintViolation<Kunde>> violations = validator
				.validateValue(Kunde.class, "id", kundeId, IdGroup.class);
		if (!violations.isEmpty())
			throw new InvalidKundeIdException(kundeId, violations);
	}

	public List<Long> findIdsByPrefix(String idPrefix) {
		final List<Long> ids = em
				.createNamedQuery(Kunde.FIND_IDS_BY_PREFIX, Long.class)
				.setParameter(Kunde.PARAM_KUNDE_ID_PREFIX, idPrefix + '%')
				.getResultList();
		return ids;
	}

	public Kunde findKundeByEmail(String email, Locale locale) {
		validateEmail(email, locale);
		try {
			final Kunde kunde = em
					.createNamedQuery(Kunde.FIND_KUNDE_BY_EMAIL, Kunde.class)
					.setParameter(Kunde.PARAM_KUNDE_EMAIL, email)
					.getSingleResult();
			return kunde;
		} catch (NoResultException e) {
			return null;
		}
	}

	private void validateEmail(String email, Locale locale) {
		final Validator validator = validatorProvider.getValidator(locale);
		final Set<ConstraintViolation<Kunde>> violations = validator
				.validateValue(Kunde.class, "email", email, Default.class);
		if (!violations.isEmpty())
			throw new InvalidEmailException(email, violations);
	}

	public Kunde createKunde(Kunde kunde, Locale locale) {
		if (kunde == null) {
			return kunde;
		}

		validateKunde(kunde, locale, Default.class, PasswordGroup.class);

		try {
			em.createNamedQuery(Kunde.FIND_KUNDE_BY_EMAIL, Kunde.class)
					.setParameter(Kunde.PARAM_KUNDE_EMAIL, kunde.getEmail())
					.getSingleResult();
			throw new EmailExistsException(kunde.getEmail());
		} catch (NoResultException e) {
			LOGGER.finest("Email-Adresse existiert noch nicht");
		}

		kunde.setId(KEINE_ID);
		em.persist(kunde);
		return kunde;
	}

	private void validateKunde(Kunde kunde, Locale locale, Class<?>... groups) {
		final Validator validator = validatorProvider.getValidator(locale);

		final Set<ConstraintViolation<Kunde>> violations = validator.validate(
				kunde, groups);
		if (!violations.isEmpty()) {

			throw new KundeValidationException(kunde, violations);
		}
	}

	public Kunde updateKunde(Kunde kunde, Locale locale) {
		if (kunde == null) {
			return null;
		}

		validateKunde(kunde, locale, Default.class, PasswordGroup.class,
				IdGroup.class);

		try {
			final Kunde vorhandenerKunde = em
					.createNamedQuery(Kunde.FIND_KUNDE_BY_EMAIL, Kunde.class)
					.setParameter(Kunde.PARAM_KUNDE_EMAIL, kunde.getEmail())
					.getSingleResult();

			if (vorhandenerKunde.getId().longValue() != kunde.getId()
					.longValue()) {
				throw new EmailExistsException(kunde.getEmail());
			}
		} catch (NoResultException e) {
			LOGGER.finest("Neue Email-Adresse");
		}

		em.merge(kunde);
		return kunde;
	}

	public void deleteKunde(Kunde kunde) {
		if (kunde == null) {
			return;
		}

		try {
			kunde = findKundeById(kunde.getId(), FetchType.MIT_BESTELLUNGEN,
					Locale.getDefault());
		} catch (InvalidKundeIdException e) {
			return;
		}

		if (kunde == null) {
			return;
		}

		if (!kunde.getBestellungen().isEmpty()) {
			throw new KundeDeleteBestellungException(kunde);
		}

		em.remove(kunde);
	}

	public List<Kunde> findKundenByPLZ(String plz) {
		final List<Kunde> kunden = em
				.createNamedQuery(Kunde.FIND_KUNDEN_BY_PLZ, Kunde.class)
				.setParameter(Kunde.PARAM_KUNDE_ADRESSE_PLZ, plz)
				.getResultList();
		return kunden;
	}

	public List<Kunde> findKundenBySeit(Date seit) {
		final List<Kunde> kunden = em
				.createNamedQuery(Kunde.FIND_KUNDEN_BY_DATE, Kunde.class)
				.setParameter(Kunde.FIND_KUNDEN_BY_DATE, seit).getResultList();
		return kunden;
	}

	public List<Kunde> findKundenByNachnameCriteria(String nachname) {
		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<Kunde> criteriaQuery = builder
				.createQuery(Kunde.class);
		final Root<Kunde> k = criteriaQuery.from(Kunde.class);

		final Path<String> nachnamePath = k.get(Kunde_.nachname);
		
		final Predicate pred = builder.equal(nachnamePath, nachname);
		criteriaQuery.where(pred);

		final List<Kunde> kunden = em.createQuery(criteriaQuery)
				.getResultList();
		return kunden;
	}

	public List<Kunde> findKundenMitMinBestMenge(short minMenge) {
		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<Kunde> criteriaQuery = builder
				.createQuery(Kunde.class);
		final Root<Kunde> k = criteriaQuery.from(Kunde.class);

		final Join<Kunde, Bestellung> b = k.join(Kunde_.bestellungen);
		final Join<Bestellung, Bestellposition> bp = b
				.join(Bestellung_.bestellpositionen);
		criteriaQuery
				.where(builder.gt(bp.<Integer> get(Bestellposition_.anzahl),
						minMenge)).distinct(true);

		final List<Kunde> kunden = em.createQuery(criteriaQuery)
				.getResultList();
		return kunden;
	}

}
