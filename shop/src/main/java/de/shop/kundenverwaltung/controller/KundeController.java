package de.shop.kundenverwaltung.controller;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.Flash;
import javax.inject.Inject;
import javax.inject.Named;

import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.util.Log;
import de.shop.util.Transactional;


/**
 * Dialogsteuerung fuer die Kundenverwaltung
 */
@Named("kc")
@RequestScoped
@Log
public class KundeController implements Serializable {
	private static final long serialVersionUID = -8817180909526894740L;
	
	private static final String FLASH_KUNDE = "kunde";
	private static final String JSF_VIEW_KUNDE = "/kundenverwaltung/viewKunde";

	
	@Inject
	private KundeService ks;
	private Kunde kunde;
	
	private Kunde neuerKunde;
	
	@Inject
	private Flash flash;
	
	private Long kundeId;

	private String nachname;

	@Override
	public String toString() {
		return "KundeController [kundeId=" + kundeId + "]";
	}

	public void setKundeId(Long kundeId) {
		this.kundeId = kundeId;
	}

	public Long getKundeId() {
		return kundeId;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}
	
	public String getNachname() {
		return nachname;
	}
	
	public Kunde getNeuerKunde() {
		return neuerKunde;
	}
	/**
	 * Action Methode, um einen Kunden zu gegebener ID zu suchen
	 * @return URL fuer Anzeige des gefundenen Kunden; sonst null
	 */
	@Transactional
	public String findKundeById() {
		final Kunde kunde = ks.findKundeById(kundeId, FetchType.NUR_KUNDE, null);
		if (kunde == null) {
			flash.remove(FLASH_KUNDE);
			return null;
		}
		
		flash.put(FLASH_KUNDE, kunde);
		return JSF_VIEW_KUNDE;
	}
	
	public void createEmptyKunde() {
		if (neuerKunde!= null)
			return;
		neuerKunde = new Kunde();
	}
	
	public String createKunde() {
		try {
			neuerKunde = (Kunde) ks.createKunde(neuerKunde, null);
		}
		catch (Exception e) {
			final String outcome = "Fehler";
			return outcome;
		}

		// Aufbereitung fuer viewKunde.xhtml
		kundeId = neuerKunde.getId();
		kunde = neuerKunde;
		neuerKunde = null;  // zuruecksetzen
		
		
		return JSF_VIEW_KUNDE;
	}
}
