package de.shop.artikelverwaltung.rest;

import static org.junit.runners.MethodSorters.NAME_ASCENDING;
import static de.shop.util.TestConstants.ACCEPT;
import static com.jayway.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;
import static de.shop.util.TestConstants.ARTIKEL_ID_PATH_PARAM;
import static de.shop.util.TestConstants.ARTIKEL_ID_PATH;
import static de.shop.util.TestConstants.ARTIKEL_PATH;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;

import de.shop.util.ConcurrentDelete;
import de.shop.util.AbstractResourceTest;
import de.shop.util.ConcurrentUpdate;








import java.lang.invoke.MethodHandles;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.util.AbstractResourceTest;


@RunWith(Arquillian.class)
@FixMethodOrder(NAME_ASCENDING)
public class ArtikelResourceConcurencyTest extends AbstractResourceTest {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
//	private static final Long ARTIKEL_ID_UPD_DEL = Long.valueOf(10007);
	private static final Long ARTIKEL_ID_UPD_UPD = Long.valueOf(10008);
	private static final String NEUE_FARBE = "aaaa";
	private static final String NEUE_FARBE_2 = "bbbb";
	

	@Test
	public void updateUpdate() throws InterruptedException, ExecutionException {
		LOGGER.finer("BEGINN");
		
		//Given
		
		
		final Long artikelId = ARTIKEL_ID_UPD_UPD;
		final String username = USERNAME;
		final String password = PASSWORD;
    	final String neueFarbe = NEUE_FARBE;
    	final String neueFarbe2 = NEUE_FARBE_2;
		
		
		//When
		
		Response response = given().header(ACCEPT, APPLICATION_JSON)
                .pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
                .get(ARTIKEL_ID_PATH);
		JsonObject jsonObject;
		
		try (final JsonReader jsonReader =
	              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			jsonObject = jsonReader.readObject();
		}
		
		// Konkurrierendes Update
		// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem Nachnamen bauen
	  	JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
	 	Set<String> keys = jsonObject.keySet();
	 	for (String k : keys) {
    		if ("farbe".equals(k)) {
    			job.add("farbe", neueFarbe2);
		    }
		    else {
		    	job.add(k, jsonObject.get(k));
		    }
		}
		
	 	final JsonObject jsonObject2 = job.build();
    	final ConcurrentUpdate concurrentUpdate = new ConcurrentUpdate(jsonObject2, ARTIKEL_PATH,
    			                                                       username, password);
    	final ExecutorService executorService = Executors.newSingleThreadExecutor();
		final Future<Response> future = executorService.submit(concurrentUpdate);
		response = future.get();   // Warten bis der "parallele" Thread fertig ist
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		
		
		// Fehlschlagendes Update
		// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem Nachnamen bauen
		job = getJsonBuilderFactory().createObjectBuilder();
		keys = jsonObject.keySet();
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
		assertThat(response.getStatusCode(), is(HTTP_CONFLICT));
		
		LOGGER.finer("ENDE");
	}
	
//	@Ignore
//	@Test
//	public void updateDelete() throws InterruptedException, ExecutionException {
//		LOGGER.finer("BEGINN");
//		
//		//Given
//		final Long  artikelId= ARTIKEL_ID_UPD_DEL;
//		final String username = USERNAME_ADMIN;
//		final String password = PASSWORD_ADMIN;
//		final String username2 = USERNAME;
//		final String password2 = PASSWORD;
//		final String neueFarbe = NEUE_FARBE;
//		
//		//When
//		Response response = given().header(ACCEPT, APPLICATION_JSON)
//                .pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
//                .get(ARTIKEL_ID_PATH);
//		
//		
//		JsonObject jsonObject;
//		
//		try (final JsonReader jsonReader =
//				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
//			jsonObject = jsonReader.readObject();
//		}
//		
//		// Konkurrierendes Delete
//		final ConcurrentDelete concurrentDelete = new ConcurrentDelete("artikel/10007", username2, password2);
//		//final ConcurrentDelete concurrentDelete = new ConcurrentDelete(ARTIKEL_PATH + '/' + artikelId, username2, password2);
//    	final ExecutorService executorService = Executors.newSingleThreadExecutor();
//		final Future<Response> future = executorService.submit(concurrentDelete);
//		response = future.get();   // Warten bis der "parallele" Thread fertig ist
//		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
//		
//	
//		// Fehlschlagendes Update
//		final JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
//		final Set<String> keys = jsonObject.keySet();
//		   	for (String k : keys) {
//		   		if ("farbe".equals(k)) {
//		   			job.add("farbe", neueFarbe);
//		   		}
//		   		else {
//		   			job.add(k, jsonObject.get(k));
//		   		}
//		   	}
//	 	response = given().contentType(APPLICATION_JSON)
//				          .body(jsonObject.toString())
//                          .auth()
//                          .basic(username, password)
//		                  .put(ARTIKEL_PATH);
//				
//		// Then
//		assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
//					
//		
//		
//		LOGGER.finer("ENDE");
//	}
//	
//	@Ignore
//	@Test
//	public void deleteUpdate() throws InterruptedException, ExecutionException {
//		LOGGER.finer("BEGINN");
//		
//		
//		
//		LOGGER.finer("ENDE");
//	}
}
