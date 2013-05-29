package de.shop.artikelverwaltung.controller;
import static de.shop.util.Constants.JSF_INDEX;
import static de.shop.util.Constants.JSF_REDIRECT_SUFFIX;

import org.jboss.logging.Logger;







import org.richfaces.cdi.push.Push;

import static javax.ejb.TransactionAttributeType.REQUIRED;
import static javax.ejb.TransactionAttributeType.SUPPORTS;
import static javax.persistence.PersistenceContextType.EXTENDED;
import de.shop.util.Client;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.io.Serializable;
import java.lang.invoke.MethodHandles;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.TransactionAttribute;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.faces.context.Flash;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.util.Log;
import de.shop.util.Transactional;


/**
 * Dialogsteuerung fuer die Artikelverwaltung
 */
@Named("ac")
@RequestScoped
@Log
@TransactionAttribute(SUPPORTS)
public class ArtikelController implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6337596008107442042L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	private static final String FLASH_ARTIKEL = "artikel";
	private static final String JSF_VIEW_ARTIKEL = "/artikelverwaltung/viewArtikel";
	
	private static final String JSF_LIST_ARTIKEL = "/artikelverwaltung/listArtikel";
	private static final int ANZAHL_LADENHUETER = 5;
	
	private static final String JSF_SELECT_ARTIKEL = "/artikelverwaltung/selectArtikel";
	private static final String SESSION_VERFUEGBARE_ARTIKEL = "verfuegbareArtikel";

	private String name;
	
	@PersistenceContext(type = EXTENDED)
	private transient EntityManager em;
	
	@Inject
	private ArtikelService as;
	
	@Inject
	@Client
	private Locale locale;
	
	@Inject
	private Flash flash;
	
	
	private Artikel artikel;
	
	private boolean geaendertArtikel;
	
	private Long artikelId;
	
//	private List<Artikel> artikels = Collections.emptyList();
	
	
	
	private Artikel neuerArtikel; //  = new Artikel(); // "Leeres Objekt" fuer Eingabe

	@Inject
	@Push(topic = "updateArtikel")
	private transient Event<String> updateArtikelEvent;
	
//	@Inject
//	@Push(topic = "test")
//	private transient Event<String> neuerArtikelEvent;

	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}

	
	@Override
	public String toString() {
		return "ArtikelController [artikelId=" + artikelId + "]";
	}

	
	
	public void setArtikelId(Long artikelId) {
		this.artikelId = artikelId;
	}

	public Long getArtikelId() {
		return artikelId;
	}


	@Transactional
	public String findArtikelById() {
		final Artikel artikel = as.findArtikelById(artikelId);
		if (artikel == null) {
			flash.remove(FLASH_ARTIKEL);
			return null;
		}
		
		flash.put(FLASH_ARTIKEL, artikel);
		return JSF_VIEW_ARTIKEL;
	}
	
	
	@Transactional
	public String findArtikelByName() {
		final List<Artikel> artikel = as.findArtikelByName(name);
		flash.put(FLASH_ARTIKEL, artikel);

		return JSF_LIST_ARTIKEL;
	}
	
	public void createEmptyArtikel() {
		if (neuerArtikel != null) // Seite nach Fehler erneut anzeigen
			return;
		neuerArtikel = new Artikel();
		System.out.println("Leerer Artikel erstellt");
	}
	
	@TransactionAttribute(REQUIRED)
	public String createArtikel(){
		
			neuerArtikel = as.createArtikel(neuerArtikel, locale);
			System.out.println("Neuer Artikel erstellt");

//			neuerArtikelEvent.fire(String.valueOf(neuerArtikel.getId()));


			// Aufbereitung fuer viewKunde.xhtml
			artikelId = neuerArtikel.getId();
			artikel = neuerArtikel;
			
			// zuruecksetzen
			neuerArtikel = null;	
		
		
			return JSF_VIEW_ARTIKEL + JSF_REDIRECT_SUFFIX;
	}
	
	@TransactionAttribute(REQUIRED)
	public String update() {
		// auth.preserveLogin();
		
		if (!geaendertArtikel || artikel == null) {
			return JSF_INDEX;
		}
		
		LOGGER.tracef("Aktualisierter Artikel: %s", artikel);
//		try {
			artikel = as.updateArtikel(artikel);
//		}
//		catch (RuntimeException e) {
//			final String outcome = updateErrorMsg(e, artikel.getClass());
//			return outcome;
//		}

		// Push-Event fuer Webbrowser
		updateArtikelEvent.fire(String.valueOf(artikel.getId()));
		
		// ValueChangeListener zuruecksetzen
		geaendertArtikel = false;
		
		// Aufbereitung fuer viewKunde.xhtml
		artikelId = artikel.getId();
		
		return JSF_VIEW_ARTIKEL + JSF_REDIRECT_SUFFIX;
	}
	
	
	
	public void geaendert(ValueChangeEvent e) {
		if (geaendertArtikel) {
			return;
		}
		
		if (e.getOldValue() == null) {
			if (e.getNewValue() != null) {
				geaendertArtikel = true;
			}
			return;
		}

		if (!e.getOldValue().equals(e.getNewValue())) {
			geaendertArtikel = true;				
		}
	}


	public Artikel getNeuerArtikel() {
		return neuerArtikel;
	}


	//Für Anzeige in JSF-Seite
	public Artikel getArtikel() {
		return artikel;
	}
	

//	public ArtikelController(){
//		neuerArtikel = new Artikel();
//	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
