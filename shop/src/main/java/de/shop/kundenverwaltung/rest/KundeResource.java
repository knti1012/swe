package de.shop.kundenverwaltung.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;


import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.rest.UriHelperBestellung;
import de.shop.bestellverwaltung.service.BestellungService;
import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.util.Log;
import de.shop.util.NotFoundException;
import de.shop.util.Transactional;

@Path("/kunden")
@Produces(APPLICATION_JSON)
@Consumes
@RequestScoped
@Transactional
@Log
public class KundeResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles
			.lookup().lookupClass().getName());
	private static final String VERSION = "1.0";

	@Inject
	private KundeService ks;

	@Inject
	private BestellungService bs;

	@Inject
	private UriHelperKunde uriHelperKunde;

	@Inject
	private UriHelperBestellung uriHelperBestellung;
	
	@PersistenceContext
	private EntityManager em;

	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf ("CDI-faehiges Bean {0} wurde erzeugt", this);
	}

	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf ("CDI-faehiges Bean {0} wird geloescht", this);
	}

	@GET
	@Produces(TEXT_PLAIN)
	@Path("version")
	public String getVersion() {
		return VERSION;
	}

	@GET
	@Path("{id:[1-9][0-9]*}")
	public Kunde findKundeById(@PathParam("id") Long id,
			@Context UriInfo uriInfo, @Context HttpHeaders headers) {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales
				.get(0);
		final Kunde kunde = ks.findKundeById(id, FetchType.NUR_KUNDE, locale);
		if (kunde == null) {
			final String msg = "Kein Kunde gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}

		uriHelperKunde.updateUriKunde(kunde, uriInfo);

		return kunde;
	}

	@GET
	public Collection<Kunde> findKundenByNachname(
			@QueryParam("nachname") @DefaultValue("") String nachname,
			@Context UriInfo uriInfo, @Context HttpHeaders headers) {
		Collection<Kunde> kunden = null;
		if ("".equals(nachname)) {
			kunden = ks.findAllKunden(FetchType.NUR_KUNDE, null);
			if (kunden.isEmpty()) {
				final String msg = "Keine Kunden vorhanden";
				throw new NotFoundException(msg);
			}
		} 
		else {
			final List<Locale> locales = headers.getAcceptableLanguages();
			final Locale locale = locales.isEmpty() ? Locale.getDefault()
					: locales.get(0);
			kunden = ks.findKundenByNachname(nachname, FetchType.NUR_KUNDE,
					locale);
			if (kunden.isEmpty()) {
				final String msg = "Kein Kunde gefunden mit Nachname "
						+ nachname;
				throw new NotFoundException(msg);
			}
		}

		for (Kunde kunde : kunden) {
			uriHelperKunde.updateUriKunde(kunde, uriInfo);
		}

		return kunden;
	}

	@GET
	@Path("{id:[1-9][0-9]*}/bestellungen")
	public Collection<Bestellung> findBestellungenByKundeId(
			@PathParam("id") Long kundeId, @Context UriInfo uriInfo) {
		final Collection<Bestellung> bestellungen = bs
				.findBestellungenByKundeId(kundeId);
		if (bestellungen.isEmpty()) {
			final String msg = "Kein Kunde gefunden mit der ID " + kundeId;
			throw new NotFoundException(msg);
		}

		for (Bestellung bestellung : bestellungen) {
			uriHelperBestellung.updateUriBestellung(bestellung, uriInfo);
		}

		return bestellungen;
	}

	@POST
	@Consumes(APPLICATION_JSON)
	@Produces
	public Response createKunde(Kunde kunde, @Context UriInfo uriInfo,
			@Context HttpHeaders headers) {

		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales
				.get(0);
		kunde = ks.createKunde(kunde, locale);
		LOGGER.debugf ("Kunde: {0}", kunde);
		final URI kundeUri = uriHelperKunde.getUriKunde(kunde, uriInfo);
		return Response.created(kundeUri).build();
	}

	@PUT
	@Consumes(APPLICATION_JSON)
	@Produces
	public void updateKunde(Kunde kunde, @Context UriInfo uriInfo,
			@Context HttpHeaders headers) {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales
				.get(0);
		Kunde origKunde = ks.findKundeById(kunde.getId(), FetchType.NUR_KUNDE,
				locale);
		if (origKunde == null) {
			final String msg = "Kein Kunde gefunden mit der ID "
					+ kunde.getId();
			throw new NotFoundException(msg);
		}
		LOGGER.debugf("Kunde vorher: %s", origKunde);

		Adresse adresseAlt = em.find(Adresse.class, kunde.getAdresse().getId());
		adresseAlt.setValues(kunde.getAdresse());
		kunde.setAdresse(adresseAlt);
		
		origKunde.setValues(kunde);
		
		
		LOGGER.debugf("Kunde nachher: %s", origKunde);
		

		kunde = ks.updateKunde(origKunde, locale);
		if (kunde == null) {
			final String msg = "Kein Kunde gefunden mit der ID "
					+ origKunde.getId();
			throw new NotFoundException(msg);
		}
	}

	@Path("{id:[0-9]+}")
	@DELETE
	@Produces
	public void deleteKunde(@PathParam("id") Long kundeId,
			@Context HttpHeaders headers) {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales
				.get(0);
		final Kunde kunde = ks.findKundeById(kundeId, FetchType.NUR_KUNDE,
				locale);
		ks.deleteKunde(kunde);
	}

}
