package de.shop.kundenverwaltung.controller;

import java.io.Console;
import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Locale;

import org.jboss.logging.Logger;

import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.faces.context.Flash;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;

import org.richfaces.cdi.push.Push;

import de.shop.auth.controller.AuthController;
import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.PasswordGroup;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.EmailExistsException;
import de.shop.kundenverwaltung.service.InvalidKundeException;
import de.shop.kundenverwaltung.service.InvalidKundeIdException;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.util.AbstractShopException;
import de.shop.util.Client;
import de.shop.util.ConcurrentDeletedException;
import de.shop.util.Log;
import de.shop.util.Transactional;
import static de.shop.util.Constants.JSF_REDIRECT_SUFFIX;
import static javax.ejb.TransactionAttributeType.REQUIRED;
import static javax.ejb.TransactionAttributeType.SUPPORTS;
import static javax.persistence.PersistenceContextType.EXTENDED;
import de.shop.util.Messages;
import static de.shop.util.Constants.JSF_INDEX;
import static de.shop.util.Messages.MessagesType.KUNDENVERWALTUNG;


/**
 * Dialogsteuerung fuer die Kundenverwaltung
 */
@Named("kc")
@SessionScoped
@Stateful
@TransactionAttribute(SUPPORTS)
@Log
public class KundeController implements Serializable {
	private static final long serialVersionUID = -8817180909526894740L;
	
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final String FLASH_KUNDE = "kunde";
	private static final String JSF_VIEW_KUNDE = "/kundenverwaltung/viewKunde";
	
	private static final String CLIENT_ID_CREATE_EMAIL = "createKundeForm:email";
	private static final String MSG_KEY_CREATE_KUNDE_EMAIL_EXISTS = "createKunde.emailExists";
	
	private static final Class<?>[] PASSWORD_GROUP = { PasswordGroup.class };
	
	private static final String CLIENT_ID_UPDATE_PASSWORD = "updateKundeForm:password";
	private static final String CLIENT_ID_UPDATE_EMAIL = "updateKundeForm:email";
	private static final String MSG_KEY_UPDATE_KUNDE_DUPLIKAT = "updateKunde.duplikat";
	private static final String MSG_KEY_UPDATE_KUNDE_CONCURRENT_UPDATE = "updateKunde.concurrentUpdate";
	private static final String MSG_KEY_UPDATE_KUNDE_CONCURRENT_DELETE = "updateKunde.concurrentDelete";
	
	@PersistenceContext(type = EXTENDED)
	private transient EntityManager em;
	
	@Inject
	private KundeService ks;
	
	private Kunde kunde;
	
	private boolean geaendertKunde; 
	
	private Adresse adresse;
	
	@Inject
	private Messages messages;
	
	private Kunde neuerKunde;
	
	@Inject
	private Flash flash;
	
	@Inject
	private transient HttpServletRequest request;
	
	@Inject
	private AuthController auth;
	
	@Inject
	@Client
	private Locale locale;
	
	private Long kundeId;

	private String nachname;
	
	@Inject
	@Push(topic = "marketing")
	private transient Event<String> neuerKundeEvent;
	
	@Inject
	@Push(topic = "updateKunde")
	private transient Event<String> updateKundeEvent;

	@Override
	public String toString() {
		return "KundeController [kundeId=" + kundeId + "]";
	}

	public Kunde getKunde() {
		return kunde;
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
	@TransactionAttribute(REQUIRED)
	public String findKundeById() {
		kunde = ks.findKundeById(kundeId, FetchType.NUR_KUNDE, locale);
		if (kunde == null) {
			flash.remove(FLASH_KUNDE);
			return null;
		}
		adresse = kunde.getAdresse();
		flash.put(FLASH_KUNDE, kunde);

		return JSF_VIEW_KUNDE + JSF_REDIRECT_SUFFIX;
	}
	
	@TransactionAttribute(REQUIRED)
	public void loadKunde() {
		String idStr = (String) request.getParameter("kundeId");
		if (idStr == null)
			return;
		
		Long id = null;
		try {
			id = Long.valueOf(idStr);
		}
		catch (NumberFormatException e) {
			return;
		}
		
		kunde = ks.findKundeById(id, FetchType.NUR_KUNDE, locale);
		request.setAttribute("kunde", kunde);
	}
	
	@TransactionAttribute(REQUIRED)
	public void loadKundeById() {
		// Request-Parameter "kundeId" fuer ID des gesuchten Kunden
		final String idStr = request.getParameter("kundeId");
		Long id;
		try {
			id = Long.valueOf(idStr);
		}
		catch (NumberFormatException e) {
			return;
		}
		
		// Suche durch den Anwendungskern
		kunde = ks.findKundeById(id, FetchType.NUR_KUNDE, locale);
		if (kunde == null) {
			return;
		}
	}
	
	public void createEmptyKunde() {
		if (neuerKunde!= null) {
			return;
		}
		neuerKunde = new Kunde();
		
		final Adresse adresse = new Adresse();
		neuerKunde.setAdresse(adresse);
	}
	
	@TransactionAttribute(REQUIRED)
	public String createKunde() {
		try {
			neuerKunde = (Kunde) ks.createKunde(neuerKunde, locale);
		}
		catch (EmailExistsException e) {
			final String outcome = createKundeErrorMsg(e);
			return outcome;
		}	

		// Push-Event fuer Webbrowser
		neuerKundeEvent.fire(String.valueOf(neuerKunde.getId()));
		
		// Aufbereitung fuer viewKunde.xhtml
		kundeId = neuerKunde.getId();
		kunde = neuerKunde;
		neuerKunde = null;  // zuruecksetzen
		
		
		return JSF_VIEW_KUNDE + JSF_REDIRECT_SUFFIX;
	}
	
	private String createKundeErrorMsg(AbstractShopException e) {
		final Class<? extends AbstractShopException> exceptionClass = e.getClass();
		if (exceptionClass.equals(EmailExistsException.class)) {
			messages.error(KUNDENVERWALTUNG, MSG_KEY_CREATE_KUNDE_EMAIL_EXISTS, CLIENT_ID_CREATE_EMAIL);
		}
//		else if (exceptionClass.equals(InvalidKundeException.class)) {
//			final InvalidKundeException orig = (InvalidKundeException) e;
//			messages.error(orig.getViolations(), null);
//		}
		
		return null;
	}
	
	public Class<?>[] getPasswordGroup() {
		return PASSWORD_GROUP.clone();
	}
	
	public void geaendert(ValueChangeEvent e) {
		if (geaendertKunde) {
			return;
		}
		
		if (e.getOldValue() == null) {
			if (e.getNewValue() != null) {
				geaendertKunde = true;
			}
			return;
		}

		if (!e.getOldValue().equals(e.getNewValue())) {
			geaendertKunde = true;				
		}
	}
	

	@TransactionAttribute(REQUIRED)
	public String update() {
		// auth.preserveLogin();
		
		if (!geaendertKunde || kunde == null) {
			return JSF_INDEX;
		}
		
		LOGGER.tracef("Aktualisierter Kunde: %s", kunde);
		try {
			kunde = ks.updateKunde(kunde, locale);
		}
		catch (RuntimeException e) {
			final String outcome = updateErrorMsg(e, kunde.getClass());
			return outcome;
		}

		// Push-Event fuer Webbrowser
		updateKundeEvent.fire(String.valueOf(kunde.getId()));
		
		// ValueChangeListener zuruecksetzen
		geaendertKunde = false;
		
		// Aufbereitung fuer viewKunde.xhtml
		kundeId = kunde.getId();
		
		return JSF_VIEW_KUNDE + JSF_REDIRECT_SUFFIX;
	}
	
	private String updateErrorMsg(RuntimeException e, Class<? extends Kunde> kundeClass) {
		final Class<? extends RuntimeException> exceptionClass = e.getClass();
		if (exceptionClass.equals(InvalidKundeIdException.class)) {
			// Ungueltiges Password: Attribute wurden bereits von JSF validiert
			final InvalidKundeIdException orig = (InvalidKundeIdException) e;
			final Collection<ConstraintViolation<Kunde>> violations = orig.getViolations();
			messages.error(violations, CLIENT_ID_UPDATE_PASSWORD);
		}
		else if (exceptionClass.equals(EmailExistsException.class)) {
			if (kundeClass.equals(Kunde.class)) {
				messages.error(KUNDENVERWALTUNG, MSG_KEY_UPDATE_KUNDE_DUPLIKAT, CLIENT_ID_UPDATE_EMAIL);
			}
		}
		else if (exceptionClass.equals(OptimisticLockException.class)) {
			if (kundeClass.equals(Kunde.class)) {
				messages.error(KUNDENVERWALTUNG, MSG_KEY_UPDATE_KUNDE_CONCURRENT_UPDATE, null);
			}
		}
		else if (exceptionClass.equals(ConcurrentDeletedException.class)) {
			if (kundeClass.equals(Kunde.class)) {
				messages.error(KUNDENVERWALTUNG, MSG_KEY_UPDATE_KUNDE_CONCURRENT_DELETE, null);
			}
		}
		return null;
	}

	
}
