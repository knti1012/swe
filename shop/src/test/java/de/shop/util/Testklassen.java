package de.shop.util;

import java.util.Arrays;
import java.util.List;

import de.shop.artikelverwaltung.domain.ArtikelTest;
import de.shop.artikelverwaltung.service.ArtikelServiceTest;
import de.shop.bestellverwaltung.domain.BestellungTest;
import de.shop.bestellverwaltung.service.BestellungServiceTest;
import de.shop.kundenverwaltung.domain.KundeTest;
import de.shop.kundenverwaltung.service.KundeServiceTest;

public enum Testklassen {
	INSTANCE;

	private List<Class<? extends AbstractTest>> classes = Arrays.asList(
			KundeTest.class, ArtikelTest.class, BestellungTest.class,
			KundeServiceTest.class, ArtikelServiceTest.class,
			BestellungServiceTest.class);

	public static Testklassen getInstance() {
		return INSTANCE;
	}

	public List<Class<? extends AbstractTest>> getTestklassen() {
		return classes;
	}
}
