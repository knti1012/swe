package de.shop.artikelverwaltung.service;

import static java.util.logging.Level.FINER;
import de.shop.util.ConcurrentDeletedException;


import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.common.base.Strings;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.util.Log;

@Log
public class ArtikelService implements Serializable {
	private static final long serialVersionUID = 3076865030092242363L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@PersistenceContext
	private transient EntityManager em;


	@PostConstruct
	private void postConstruct() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wird geloescht", this);
	}
	
	public List<Artikel> findVerfuegbareArtikel() {
		final List<Artikel> result = em.createNamedQuery(Artikel.FIND_VERFUEGBARE_ARTIKEL, Artikel.class)
				                       .getResultList();
		return result;
	}

	
	public Artikel findArtikelById(Long id) {
		final Artikel artikel = em.find(Artikel.class, id);
		return artikel;
	}
	
	public List<Artikel> findArtikelByIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return Collections.emptyList();
		}
		
		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<Artikel> criteriaQuery = builder.createQuery(Artikel.class);
		final Root<Artikel> a = criteriaQuery.from(Artikel.class);

		final Path<Long> idPath = a.get("id");
		 
		Predicate pred = null;
		if (ids.size() == 1) {
			pred = builder.equal(idPath, ids.get(0));
		}
		else {
			final Predicate[] equals = new Predicate[ids.size()];
			int i = 0;
			for (Long id : ids) {
				equals[i++] = builder.equal(idPath, id);
			}
			
			pred = builder.or(equals);
		}
		
		criteriaQuery.where(pred);
		
		final TypedQuery<Artikel> query = em.createQuery(criteriaQuery);

		final List<Artikel> artikel = query.getResultList();
		return artikel;
	}

	
	public List<Artikel> findArtikelByName(String name) {
		if (Strings.isNullOrEmpty(name)) {
			final List<Artikel> artikel = findVerfuegbareArtikel();
			return artikel;
		}
		final List<Artikel> artikel = em.createNamedQuery(Artikel.FIND_ARTIKEL_BY_NAME, Artikel.class)
                .setParameter(Artikel.PARAM_NAME, "%" + name + "%")
                .getResultList();
		return artikel;
	}
	
	public List<Artikel> findArtikelByMaxPreis(double preis) {
		final List<Artikel> artikel = em.createNamedQuery(Artikel.FIND_ARTIKEL_MAX_PREIS, Artikel.class)
				                        .setParameter(Artikel.PARAM_PREIS, preis)
				                        .getResultList();
		return artikel;
	}
	

	public Artikel createArtikel(Artikel artikel, Locale locale) {

		
		if (artikel == null) {
			return artikel;
		}
		
		em.persist(artikel);
		return artikel;		
	}
	
	public void deleteArtikel(Artikel artikel) {
		if (artikel == null) {
			return;
		}
		
		try {
			artikel = findArtikelById(artikel.getId());
		}
		catch (InvalidArtikelIdException e) {
			return;
		}
		
		if (artikel == null) {
			return;
		}

		em.remove(artikel);
	}
	
	public Artikel updateArtikel(Artikel artikel) {
		if (artikel == null) {
			return null;
		}
		
		//artikel vom EntityManager trenne
		em.detach(artikel);

		Artikel tmp = findArtikelById(artikel.getId());
		if(tmp == null){
			throw new ConcurrentDeletedException(artikel.getId());
		}
		em.detach(tmp);
		
		em.merge(artikel); //OptiisticLockException
		return artikel;
	}
}
