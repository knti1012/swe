package de.shop.artikelverwaltung.rest;

import static java.util.logging.Level.FINER;
import static java.util.logging.Level.FINEST;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_XML;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.util.Log;
import de.shop.util.NotFoundException;


@Path("/artikel")
@Produces({ APPLICATION_XML, TEXT_XML, APPLICATION_JSON })
@Consumes
@RequestScoped
@Log
public class ArtikelResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@Inject
	private ArtikelService as;
	
	@Inject
	private UriHelperArtikel uriHelperArtikel;
	
	@PostConstruct
	private void postConstruct() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wurde erzeugt", this);
	}
	
	@PreDestroy
	private void preDestroy() {
		LOGGER.log(FINER, "CDI-faehiges Bean {0} wird geloescht", this);
	}
	
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Artikel findArtikel(@PathParam("id") Long id, @Context UriInfo uriInfo) {
		final Artikel artikel = as.findArtikelById(id);
		if (artikel == null) {
			final String msg = "Kein Artikel gefunden mit der ID " + id;
			throw new NotFoundException(msg);
		}

		return artikel;
	}
	
	@POST
	@Consumes({ APPLICATION_XML, TEXT_XML })
	@Produces
	public Response createArtikel(Artikel artikel, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		artikel = as.createArtikel(artikel, locale);
		LOGGER.log(FINEST, "Artikel: {0}", artikel);
		
		final URI artikelUri = uriHelperArtikel.getUriArtikel(artikel, uriInfo);
		return Response.created(artikelUri).build();
	}
	
	@PUT
	@Consumes({ APPLICATION_XML, TEXT_XML })
	@Produces
	public void updateArtikel(Artikel artikel, @Context UriInfo uriInfo, @Context HttpHeaders headers) {
		
		Artikel origArtikel = as.findArtikelById(artikel.getId());
		if (origArtikel == null) {
			final String msg = "Kein Artikel gefunden mit der ID " + artikel.getId();
			throw new NotFoundException(msg);
		}
		LOGGER.log(FINEST, "Artikel vorher: %s", origArtikel);
	
		origArtikel.setValues(artikel);
		LOGGER.log(FINEST, "Artikel nachher: %s", origArtikel);
		
		artikel = as.updateArtikel(origArtikel);
		if (artikel == null) {
			final String msg = "Kein Artikel gefunden mit der ID " + origArtikel.getId();
			throw new NotFoundException(msg);
		}
	}
}
