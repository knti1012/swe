package de.shop.bestellverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestConstants.ACCEPT;
import static de.shop.util.TestConstants.ARTIKEL_URI;
import static de.shop.util.TestConstants.BESTELLUNGEN_ID_KUNDE_PATH;
import static de.shop.util.TestConstants.BESTELLUNGEN_ID_PATH;
import static de.shop.util.TestConstants.BESTELLUNGEN_ID_PATH_PARAM;
import static de.shop.util.TestConstants.BESTELLUNGEN_PATH;
//import static de.shop.util.TestConstants.KUNDEN_ID_PATH;
//import static de.shop.util.TestConstants.KUNDEN_ID_PATH_PARAM;
//import static de.shop.util.TestConstants.KUNDEN_ID_PATH;
//import static de.shop.util.TestConstants.KUNDEN_ID_PATH_PARAM;
//import static de.shop.util.TestConstants.KUNDEN_ID_PATH;
//import static de.shop.util.TestConstants.KUNDEN_ID_PATH_PARAM;
//import static de.shop.util.TestConstants.KUNDEN_PATH;
import static de.shop.util.TestConstants.KUNDEN_URI;
import static de.shop.util.TestConstants.LIEFERUNG_URI;
import static de.shop.util.TestConstants.LOCATION;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
//import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
//import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;
//import java.util.Set;
import java.util.logging.Logger;

import javax.json.JsonObject;
//import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;
//import com.jayway.restassured.specification.RequestSpecification;




//import com.jayway.restassured.specification.RequestSpecification;






import de.shop.util.AbstractResourceTest;


@RunWith(Arquillian.class)
@FixMethodOrder(NAME_ASCENDING)
public class BestellungResourceTest extends AbstractResourceTest {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final Long BESTELLUNG_ID_VORHANDEN = Long.valueOf(1001);
	//private static final Long KUNDE_ID_VORHANDEN = Long.valueOf(2239833);
	
	private static final Long PREIS_NEU = Long.valueOf(200);

	private static final String STATUS_NEU = "in Bearbeitung";

	private static final Long BESTELLUNG_ID_DELETE = Long.valueOf(1405357);

	private static final Long KUNDE_ID_VORHANDEN = Long.valueOf(101);

	private static final Long ARTIKEL_ID_VORHANDEN_1 = Long.valueOf(301);

	//private static final Long ARTIKEL_ID_VORHANDEN_2 = null;

	
	@Ignore
	@Test
	public void findBestellungById() {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long bestellungId = BESTELLUNG_ID_VORHANDEN;
		final String status = STATUS_NEU;
		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(BESTELLUNGEN_ID_PATH_PARAM, bestellungId)
				                         .get(BESTELLUNGEN_ID_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_OK));
		
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonObject jsonObject = jsonReader.readObject();
			assertThat(jsonObject.getJsonNumber("id").longValue(), is(bestellungId.longValue()));
			assertThat(jsonObject.getString("status"), is(status));
			assertThat(jsonObject.getJsonNumber("id").longValue(),
					is(bestellungId.longValue()));
		}

		LOGGER.finer("ENDE");
	}
	
	@Ignore
	@Test
	public void deleteBestellung() {
		LOGGER.finer("BEGINN");
		
		//Given
		final Long bestellungId = BESTELLUNG_ID_DELETE;
		
		//When
		Response response = given() .pathParameter(BESTELLUNGEN_ID_PATH_PARAM,bestellungId)
									.delete( BESTELLUNGEN_ID_PATH);
		//Then
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		LOGGER.finer("ENDE");
	}
	
	
	
	@Test
	public void findKundeByBestellungId() {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long bestellungId = BESTELLUNG_ID_VORHANDEN;
		
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(BESTELLUNGEN_ID_PATH_PARAM, bestellungId)
                                         .get(BESTELLUNGEN_ID_KUNDE_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_OK));
		
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonObject jsonObject = jsonReader.readObject();
			assertThat(jsonObject.getJsonNumber("id"), is(notNullValue()));
					  // endsWith("/kunden/" + jsonObject.getInt("id") + "/bestellungen"));
		}

		LOGGER.finer("ENDE");
	
	}

	@Ignore
	@Test
	public void createBestellung() {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long kundeId = KUNDE_ID_VORHANDEN;
		final Long artikelId1 = ARTIKEL_ID_VORHANDEN_1;
		//final Long artikelId2 = ARTIKEL_ID_VORHANDEN_2;
		final Long preis = PREIS_NEU;
		final String status = STATUS_NEU;
		//final Long bestellungId = BESTELLUNG_ID_VORHANDEN;
		
		
		final String username = USERNAME_ADMIN;
		final String password = PASSWORD_ADMIN;
		
		// Neues, client-seitiges Bestellungsobjekt als JSON-Datensatz
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
				                      	.add("kundeUri", KUNDEN_URI + "/" + kundeId)
				                      	
				            		   .add("preis",preis)
				            		   .add("status",status)
				            		   .add("bestellposition", getJsonBuilderFactory().createArrayBuilder()
				            		                            .add(getJsonBuilderFactory().createObjectBuilder()
				            		                               
				            		                                 .add("anzahl", "2")
				            		                                 .add("artikelUri", ARTIKEL_URI + "/"+ artikelId1 )))
				            		   .add("kundeUri", KUNDEN_URI + "/" )
				            		   .add("lieferungUri", LIEFERUNG_URI + "/" )
				            		   
				            		                            
				                      .build();

		// When
		final Response response = given().contentType(APPLICATION_JSON)
				                         .body(jsonObject.toString())
				                         .auth()
				                         .basic(username, password)
				                         .post(BESTELLUNGEN_PATH);
		
		assertThat(response.getStatusCode(), is(HTTP_CREATED));
		final String location = response.getHeader(LOCATION);
		final int startPos = location.lastIndexOf('/');
		final String idStr = location.substring(startPos + 1);
		final Long id = Long.valueOf(idStr);
		assertThat(id.longValue() > 0, is(true));

		LOGGER.finer("ENDE");
	}
}
