package de.shop.artikelverwaltung.controller;
import static de.shop.util.Constants.JSF_REDIRECT_SUFFIX;


import static javax.ejb.TransactionAttributeType.REQUIRED;
import static javax.ejb.TransactionAttributeType.SUPPORTS;
import de.shop.util.Client;

import java.util.Locale;
import java.io.Serializable;

import javax.ejb.TransactionAttribute;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.Flash;
import javax.inject.Inject;
import javax.inject.Named;

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
	private static final String FLASH_ARTIKEL = "artikel";
	private static final String JSF_VIEW_ARTIKEL = "/artikelverwaltung/viewArtikel";
	
	@Inject
	private ArtikelService as;
	
	@Inject
	@Client
	private Locale locale;
	
	@Inject
	private Flash flash;
	
	private Long artikelId;
	
	
	private Artikel neuerArtikel; // "Leeres Objekt" fuer Eingabe
	
	private Artikel artikel;

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
	
	
	public void createEmptyArtikel() {
		if (neuerArtikel != null) // Seite nach Fehler erneut anzeigen
			return;
		neuerArtikel = new Artikel();
	}
	
	@TransactionAttribute(REQUIRED)
	public String createArtikel(){
		
			as.createArtikel(neuerArtikel, locale);

			// Aufbereitung fuer viewKunde.xhtml
			artikelId = neuerArtikel.getId();
			artikel = neuerArtikel;
			
			// zuruecksetzen
			neuerArtikel = null;	
		
		
			return JSF_VIEW_ARTIKEL + JSF_REDIRECT_SUFFIX;
	}

	public Artikel getNeuerArtikel() {
		return neuerArtikel;
	}


	//Für Anzeige in JSF-Seite
	public Artikel getArtikel() {
		return artikel;
	}
}
