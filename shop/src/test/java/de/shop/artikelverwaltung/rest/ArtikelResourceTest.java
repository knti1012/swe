package de.shop.artikelverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestConstants.ACCEPT;
import static de.shop.util.TestConstants.ARTIKEL_URI;
import static de.shop.util.TestConstants.BESTELLUNGEN_ID_KUNDE_PATH;
import static de.shop.util.TestConstants.BESTELLUNGEN_ID_PATH;
import static de.shop.util.TestConstants.BESTELLUNGEN_ID_PATH_PARAM;
import static de.shop.util.TestConstants.BESTELLUNGEN_PATH;
import static de.shop.util.TestConstants.ARTIKEL_ID_PATH_PARAM;
import static de.shop.util.TestConstants.ARTIKEL_PATH;
import static de.shop.util.TestConstants.ARTIKEL_ID_PATH;
import static de.shop.util.TestConstants.KUNDEN_ID_PATH;
import static de.shop.util.TestConstants.KUNDEN_ID_PATH_PARAM;
import static de.shop.util.TestConstants.KUNDEN_PATH;
import static de.shop.util.TestConstants.KUNDEN_URI;
import static de.shop.util.TestConstants.LOCATION;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.util.Set;
import java.util.logging.Logger;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;

import de.shop.util.AbstractResourceTest;




@RunWith(Arquillian.class)
@FixMethodOrder(NAME_ASCENDING)
public class ArtikelResourceTest extends AbstractResourceTest {
	
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final Long ARTIKEL_ID_VORHANDEN = Long.valueOf(10004);
	private static final String ARTIKEL_NAME_NEU = "frühlings";
	private static final String ARTIKEL__KATEGORIE_NEU = "t-shirt";
	private static final String ARTIKEL_ART_NEU = "maenner";
	private static final String ARTIKEL_GROESSE_NEU = "m";
	private static final String ARTIKEL_FARBE_NEU = "schwarz";
	private static final Long ARTIKEL_PREIS_NEU = Long.valueOf(100);
	private static final Long ARTIKEL_LAGERBESTAND_NEU = Long.valueOf(23);
	private static final Long ARTIKEL_ID_UPDATE = Long.valueOf(10004);
	private static final String NEUE_FARBE = "rot";
	
	@Test
	public void  findArtikelById() {
		LOGGER.finer("BEGINN");
		//Given
		
		final Long artikelId = ARTIKEL_ID_VORHANDEN;
		
		//When 
		Response response = given().header(ACCEPT, APPLICATION_JSON)
									.pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
									.get(ARTIKEL_ID_PATH);
		//Then
		assertThat(response.getStatusCode(), is(HTTP_OK));
		
		try (final JsonReader jsonReader =
	              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonObject jsonObject = jsonReader.readObject();
			assertThat(jsonObject.getJsonNumber("id").longValue(), is(artikelId.longValue()));
		}
		
		LOGGER.finer("ENDE");
	
	}
	

	@Test
	public void createArtikel() {
		LOGGER.finer("BEGINN");
		
		//Given
		final String name  = ARTIKEL_NAME_NEU;
		final String kategorie = ARTIKEL__KATEGORIE_NEU;
		final String art = ARTIKEL_ART_NEU;
		final String groesse = ARTIKEL_GROESSE_NEU;
		final String farbe = ARTIKEL_FARBE_NEU;
		final Long preis = ARTIKEL_PREIS_NEU;
		final Long lagerbestand = ARTIKEL_LAGERBESTAND_NEU;
		final String username = USERNAME_ADMIN;
		final String password = PASSWORD_ADMIN;
		
		// Neues, client-seitiges Bestellungsobjekt als JSON-Datensatz
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()		                    
						                      .add("art", art)
						                      .add("farbe", farbe)
						                      .add("groesse", groesse)
						                      .add("kategorie", kategorie)
						                      .add("lagerbestand", lagerbestand)
						                      .add("name", name)
						                      .add("preis", preis)
						                      .build();
		//when
		final Response response = given().contentType(APPLICATION_JSON)
                .body(jsonObject.toString())
                .auth()
                .basic(username, password)
                .post(ARTIKEL_PATH);
		
		// Then
		
		assertThat(response.getStatusCode(), is(HTTP_CREATED));
		
	}
	
	@Test
	public void updateArtikel(){
		LOGGER.finer("BEGINN");
		
		//Given
		final Long artikelId = ARTIKEL_ID_UPDATE;
		final String neueFarbe = NEUE_FARBE;
		final String username = USERNAME_ADMIN;
		final String password = PASSWORD_ADMIN;
		
		// When
				Response response = given().header(ACCEPT, APPLICATION_JSON)
						                   .pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
		                                   .get(ARTIKEL_ID_PATH);
		
		
				JsonObject jsonObject;
				try (final JsonReader jsonReader =
						              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
					jsonObject = jsonReader.readObject();
				}
				
				assertThat(jsonObject.getJsonNumber("id").longValue(), is(artikelId.longValue()));
				
		    	// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem Nachnamen bauen
		    	final JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
		    	final Set<String> keys = jsonObject.keySet();
		    	for (String k : keys) {
		    		if ("farbe".equals(k)) {
		    			job.add("farbe", neueFarbe);
		    		}
		    		else {
		    			job.add(k, jsonObject.get(k));
		    		}
		    	}
		    	jsonObject = job.build();
		
				response = given().contentType(APPLICATION_JSON)
				          .body(jsonObject.toString())
                        .auth()
                        .basic(username, password)
                        .put(ARTIKEL_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
	
	}
	
}