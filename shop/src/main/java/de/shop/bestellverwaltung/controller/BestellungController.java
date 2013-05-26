package de.shop.bestellverwaltung.controller;

import static de.shop.util.Constants.JSF_DEFAULT_ERROR;
import static de.shop.util.Constants.JSF_REDIRECT_SUFFIX;
import static javax.ejb.TransactionAttributeType.REQUIRED;

import org.jboss.logging.Logger;
import org.richfaces.cdi.push.Push;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.ejb.TransactionAttribute;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.faces.context.Flash;
import javax.inject.Inject;
import javax.inject.Named;





import javax.servlet.http.HttpServletRequest;


//import de.shop.bestellverwaltung.service.AbstractBestellungValidationException;
import de.shop.auth.controller.AuthController;
import de.shop.auth.controller.KundeLoggedIn;
import de.shop.bestellverwaltung.domain.Bestellposition;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.service.BestellungService;
import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.EmailExistsException;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.util.Client;
import de.shop.util.Log;
import de.shop.util.Transactional;





/**
 * Dialogsteuerung fuer die Bestellverwaltung
 */
@Named("bc")
@RequestScoped
@Log
public class BestellungController implements Serializable {
	private static final long serialVersionUID = -8817180909526894740L;
	
	private static final String FLASH_BESTELLUNG = "bestellung";
	private static final String JSF_VIEW_BESTELLUNG = "/bestellverwaltung/viewBestellung";
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	@Inject
	private Warenkorb warenkorb;
	
	@Inject
	@Push(topic = "marketing")
	private transient Event<String> neueBestellungEvent;
	
	@Inject
	private KundeService ks;
	
	@Inject
	private AuthController auth;
	
	@Inject
	@KundeLoggedIn
	private Kunde kunde;
	
	@Inject
	@Client
	private Locale locale;
	
	
	@Inject
	private BestellungService bs;
	private Bestellung bestellung;
	
	@Inject
	private transient HttpServletRequest request;
	
	
	@Inject
	private Flash flash;	
	private Long bestellId;
	
	private Bestellung neueBestellung;
	
	@Override
	public String toString() {
		return "BestellungController [bestellungId=" + bestellId + "]";
	}

	public void setBestellId(Long bestellId) {
		this.bestellId = bestellId;
	}

	public Long getBestellId() {
		return bestellId;
	}

	/**
	 * Action Methode, um einen Kunden zu gegebener ID zu suchen
	 * @return URL fuer Anzeige des gefundenen Kunden; sonst null
	 */
	@Transactional
	public String findBestellungById() {
		final Bestellung bestellung = bs.findBestellungById(bestellId);
		if (bestellung == null) {
			flash.remove(FLASH_BESTELLUNG);
			return null;
		}
		
		flash.put(FLASH_BESTELLUNG, bestellung);
		return JSF_VIEW_BESTELLUNG;
	}
	
	@Transactional
	public String bestellen() {
		auth.preserveLogin();
		
		if (warenkorb == null || warenkorb.getPositionen() == null || warenkorb.getPositionen().isEmpty()) {
			// Darf nicht passieren, wenn der Button zum Bestellen verfuegbar ist
			return JSF_DEFAULT_ERROR;
		}
		
		// Den eingeloggten Kunden mit seinen Bestellungen ermitteln, und dann die neue Bestellung zu ergaenzen
		kunde = ks.findKundeById(kunde.getId(), FetchType.MIT_BESTELLUNGEN, locale);
		
		// Aus dem Warenkorb nur Positionen mit Anzahl > 0
		final List<Bestellposition> positionen = warenkorb.getPositionen();
		final List<Bestellposition> neuePositionen = new ArrayList<>(positionen.size());
		for (Bestellposition bp : positionen) {
			if (bp.getAnzahl() > 0) {
				neuePositionen.add(bp);
			}
		}
		
		// Warenkorb zuruecksetzen
		warenkorb.endConversation();
		
		// Neue Bestellung mit neuen Bestellpositionen erstellen
		Bestellung bestellung = new Bestellung();
		bestellung.setBestellpositionen(neuePositionen);
		LOGGER.tracef("Neue Bestellung: %s\nBestellpositionen: %s", bestellung, bestellung.getBestellpositionen());
		
		// Bestellung mit VORHANDENEM Kunden verknuepfen:
		// dessen Bestellungen muessen geladen sein, weil es eine bidirektionale Beziehung ist
		try {
			bestellung = bs.createBestellung(bestellung, kunde, locale);
		}
	catch (Exception e/*AbstractBestellungValidationException e*/) {
	/*		// Validierungsfehler KOENNEN NICHT AUFTRETEN, da Attribute durch JSF validiert wurden
			// und in der Klasse Bestellung keine Validierungs-Methoden vorhanden sind
			throw new IllegalStateException(e);*/
		}

		// Bestellung im Flash speichern wegen anschliessendem Redirect
		flash.put("bestellung", bestellung);
		
		return JSF_VIEW_BESTELLUNG;
	}
	

	@TransactionAttribute(REQUIRED)
	public void loadBestellungById() {
		// Request-Parameter "kundeId" fuer ID des gesuchten Kunden
		final String idStr = request.getParameter("bestellungId");
		Long id;
		try {
			id = Long.valueOf(idStr);
		}
		catch (NumberFormatException e) {
			return;
		}
		
		// Suche durch den Anwendungskern
		bestellung = bs.findBestellungById(id);
		if (bestellung == null) {
			return;
		}
	}
	
	public void createEmptyBestellung() {
		
		if (neueBestellung!= null) {
			return;
		}
		neueBestellung = new Bestellung();
		
	}
	@TransactionAttribute(REQUIRED)
	public String createBestellung() {
		try {
			Bestellung bestellung = new Bestellung();
			neueBestellung = (Bestellung) bs.createBestellung(bestellung, kunde, locale);
		}
		catch (Exception e) {
			
			
		}	

		// Push-Event fuer Webbrowser
		neueBestellungEvent.fire(String.valueOf(neueBestellung.getId()));
		
		// Aufbereitung fuer viewKunde.xhtml
		//kundeId = neuerKunde.getId();
		bestellung = neueBestellung;
		neueBestellung = null;  // zuruecksetzen
		
		
		return JSF_VIEW_BESTELLUNG + JSF_REDIRECT_SUFFIX;
	}
	
	
	}

