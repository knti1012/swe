package de.shop.artikelverwaltung.rest;

import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.util.AbstractResourceTest;


@RunWith(Arquillian.class)
@FixMethodOrder(NAME_ASCENDING)
public class ArtikelResourceConcurencyTest extends AbstractResourceTest {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	
	@Test
	public void updateUpdate() throws InterruptedException, ExecutionException {
		LOGGER.finer("BEGINN");
		
		
		
		
		LOGGER.finer("ENDE");
	}
	
	@Test
	public void updateDelete() throws InterruptedException, ExecutionException {
		LOGGER.finer("BEGINN");
	
		
		
		
		LOGGER.finer("ENDE");
	}
	
	@Test
	public void deleteUpdate() throws InterruptedException, ExecutionException {
		LOGGER.finer("BEGINN");
		
		
		
		LOGGER.finer("ENDE");
	}
}
