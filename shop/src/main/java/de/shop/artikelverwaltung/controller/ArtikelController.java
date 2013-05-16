package de.shop.artikelverwaltung.controller;

import java.io.Serializable;

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
	private Flash flash;
	
	private Long artikelId;

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

	/**
	 * Action Methode, um einen Artikel zu gegebener ID zu suchen
	 * @return URL fuer Anzeige des gefundenen Artikel; sonst null
	 */
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
}
