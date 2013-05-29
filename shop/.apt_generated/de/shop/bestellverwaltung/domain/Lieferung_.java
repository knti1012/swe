package de.shop.bestellverwaltung.domain;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Lieferung.class)
public abstract class Lieferung_ {

	public static volatile SingularAttribute<Lieferung, Long> id;
	public static volatile SetAttribute<Lieferung, Bestellung> bestellungen;
	public static volatile SingularAttribute<Lieferung, String> lieferant;
	public static volatile SingularAttribute<Lieferung, TransportType> transportArt;
	public static volatile SingularAttribute<Lieferung, Date> aktualisiert;
	public static volatile SingularAttribute<Lieferung, Date> erzeugt;
	public static volatile SingularAttribute<Lieferung, Integer> version;

}

