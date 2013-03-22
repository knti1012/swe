package de.shop.bestellverwaltung.domain;

import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Constants.LONG_ANZ_ZIFFERN;
import static de.shop.util.Constants.MIN_ID;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;

import de.shop.util.IdGroup;
import de.shop.util.PreExistingGroup;

@Entity
@Table(name = "lieferung")
@NamedQueries({
		@NamedQuery(name = Lieferung.FIND_LIEFERUNG_BY_ID, query = "SELECT DISTINCT l"
				+ " FROM   Lieferung l"
				+ " WHERE  l.id = :"
				+ Lieferung.PARAM_ID),
		@NamedQuery(name = Lieferung.FIND_LIEFERUNGEN_BY_LIEFERANT, query = "SELECT l"
				+ " FROM   Lieferung l"
				+ " WHERE  UPPER(l.lieferant) LIKE :"
				+ Lieferung.PARAM_LIEFERANT) })
public class Lieferung implements java.io.Serializable {
	private static final long serialVersionUID = 7560752199018702446L;

	
	private static final String PREFIX = "Lieferung.";
	public static final String FIND_LIEFERUNGEN_BY_LIEFERANT = PREFIX
			+ "findLieferungenByLieferant";
	public static final String FIND_LIEFERUNG_BY_ID = PREFIX
			+ "findLieferungById";

	public static final String PARAM_LIEFERANT = "lieferant";
	public static final String PARAM_ID = "id";
	public static final int TRANSPORT_ART_LENGTH_MIN = 2;
	public static final int TRANSPORT_ART_LENGTH_MIN_MAX = 32;

	@Id
	@GeneratedValue
	@Column(name = "id", unique = true, nullable = false, updatable = false, precision = LONG_ANZ_ZIFFERN)
	@Min(value = MIN_ID, message = "{bestellverwaltung.lieferung.id.min}", groups = IdGroup.class)
	private Long id = KEINE_ID;

	@Column(name = "lieferant")
	private String lieferant;

	@Column(name = "transport_art")
	@Enumerated(STRING)
	@NotNull(message = "{bestellverwaltung.lieferung.transport_art.notNull}")
	private TransportType transportArt;

	@ManyToMany(mappedBy = "lieferungen", cascade = { PERSIST, MERGE })
	@NotEmpty(message = "{bestellverwaltung.lieferung.bestellungen.notEmpty}", groups = PreExistingGroup.class)
	@Valid
	@JsonIgnore
	private Set<Bestellung> bestellungen;

	@Past
	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date erzeugt;

	@Past
	@Column(nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date aktualisiert;

	public Lieferung() {
		super();
	}

	public Lieferung(TransportType transportArt) {
		super();
		this.transportArt = transportArt;
	}

	@PrePersist
	protected void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}

	@PreUpdate
	protected void preUpdate() {
		aktualisiert = new Date();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLieferant() {
		return lieferant;
	}

	public void setLieferant(String lieferant) {
		this.lieferant = lieferant;
	}

	public TransportType getTransportArt() {
		return transportArt;
	}

	public void setTransportArt(TransportType transportArt) {
		this.transportArt = transportArt;
	}

	public Set<Bestellung> getBestellungen() {
		return bestellungen == null ? null : Collections
				.unmodifiableSet(bestellungen);
	}

	public void setBestellungen(Set<Bestellung> bestellungen) {
		if (this.bestellungen == null) {
			this.bestellungen = bestellungen;
			return;
		}

		this.bestellungen.clear();
		if (bestellungen != null) {
			this.bestellungen.addAll(bestellungen);
		}
	}

	public void addBestellung(Bestellung bestellung) {
		if (bestellungen == null) {
			bestellungen = new HashSet<>();
		}
		bestellungen.add(bestellung);
	}

	public List<Bestellung> getBestellungenAsList() {
		return bestellungen == null ? null : new ArrayList<>(bestellungen);
	}

	public void setBestellungenAsList(List<Bestellung> bestellungen) {
		this.bestellungen = bestellungen == null ? null : new HashSet<>(
				bestellungen);
	}

	public Date getErzeugt() {
		return erzeugt == null ? null : (Date) erzeugt.clone();
	}

	public void setErzeugt(Date erzeugt) {
		this.erzeugt = erzeugt == null ? null : (Date) erzeugt.clone();
	}

	public Date getAktualisiert() {
		return aktualisiert == null ? null : (Date) aktualisiert.clone();
	}

	public void setAktualisiert(Date aktualisiert) {
		this.aktualisiert = aktualisiert == null ? null : (Date) aktualisiert
				.clone();
	}

	@Override
	public String toString() {
		return "Lieferung [id=" + id + ", lieferant=" + lieferant
				+ ", transportArt=" + transportArt + ", erzeugt=" + erzeugt
				+ ", aktualisiert=" + aktualisiert + ']';
	}
}
