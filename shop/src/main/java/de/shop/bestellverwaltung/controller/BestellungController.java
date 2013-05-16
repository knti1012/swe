package de.shop.bestellverwaltung.controller;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.Flash;
import javax.inject.Inject;
import javax.inject.Named;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.service.BestellungService;
import de.shop.util.Log;
import de.shop.util.Transactional;


/**
 * Dialogsteuerung fuer die Kundenverwaltung
 */
@Named("bc")
@RequestScoped
@Log
public class BestellungController implements Serializable {
	private static final long serialVersionUID = -8817180909526894740L;
	
	private static final String FLASH_BESTELLUNG = "bestellung";
	private static final String JSF_VIEW_BESTELLUNG = "/bestellverwaltung/viewBestellung";

	
	@Inject
	private BestellungService bs;
	private Bestellung bestellung;
	
	private Bestellung neueBestellung;
	
	@Inject
	private Flash flash;
	
	private Long bestellId;

	private String bestellposition;

	@Override
	public String toString() {
		return "BestellungController [bestellungId=" + bestellId + "]";
	}

	public void setBestellId(Long kundeId) {
		this.bestellId = bestellId;
	}

	public Long getBestellId() {
		return bestellId;
	}

	public void setBestellposition(String bestellpoisiton) {
		this.bestellposition = bestellposition;
	}
	
	public String getBestellposition() {
		return bestellposition;
	}
	
	public Bestellung getNeueBestellung() {
		return neueBestellung;
	}
	/**
	 * Action Methode, um einen Kunden zu gegebener ID zu suchen
	 * @return URL fuer Anzeige des gefundenen Kunden; sonst null
	 */
	@Transactional
	public String findBestellungById() {
		final Bestellung bestellung = bs.findBestellungById(bestellId/*, FetchType.NUR_KUNDE, null*/);
		if (bestellung == null) {
			flash.remove(FLASH_BESTELLUNG);
			return null;
		}
		
		flash.put(FLASH_BESTELLUNG, bestellung);
		return JSF_VIEW_BESTELLUNG;
	}
	
	public void createEmptyKunde() {
		if (neueBestellung != null)
			return;
		neueBestellung = new Bestellung();
	}
	
	public String createKunde() {
		try {
			neueBestellung = (Bestellung) bs.createBestellung(neueBestellung,bestellId, null);
		}
		catch (Exception e) {
			final String outcome = "Fehler";
			return outcome;
		}

		// Aufbereitung fuer viewBestellung.xhtml
		bestellId = neueBestellung.getId();
		bestellung = neueBestellung;
		neueBestellung = null;  // zuruecksetzen
		
		
		return JSF_VIEW_BESTELLUNG;
	}
}
