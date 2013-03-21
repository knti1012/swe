package de.shop.bestellverwaltung.domain;

import de.shop.kundenverwaltung.domain.Kunde;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Bestellung.class)
public abstract class Bestellung_ {

	public static volatile SingularAttribute<Bestellung, Long> id;
	public static volatile SingularAttribute<Bestellung, String> status;
	public static volatile SingularAttribute<Bestellung, Date> aktualisiert;
	public static volatile SingularAttribute<Bestellung, Double> preis;
	public static volatile ListAttribute<Bestellung, Bestellposition> bestellpositionen;
	public static volatile SingularAttribute<Bestellung, Kunde> kunde;
	public static volatile SingularAttribute<Bestellung, Date> erzeugt;
	public static volatile SetAttribute<Bestellung, Lieferung> lieferungen;

}

