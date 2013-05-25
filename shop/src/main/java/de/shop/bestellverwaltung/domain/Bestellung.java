package de.shop.bestellverwaltung.domain;

import static de.shop.util.Constants.ERSTE_VERSION;
import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Constants.MIN_ID;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.net.URI;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.IdGroup;

@Entity
@Table(name = "bestellung")
@NamedQueries({
		@NamedQuery(name = Bestellung.FIND_BESTELLUNGEN_BY_KUNDE, query = "SELECT b"
				+ " FROM   Bestellung b"
				+ " WHERE  b.kunde.id = :"
				+ Bestellung.PARAM_KUNDEID),
		@NamedQuery(name = Bestellung.FIND_BESTELLUNG_BY_ID, query = "SELECT DISTINCT b"
				+ " FROM   Bestellung b"
				+ " WHERE  b.id = :"
				+ Bestellung.PARAM_ID),
		@NamedQuery(name = Bestellung.FIND_KUNDE_BY_IDBESTELLUNG, query = "SELECT b.kunde"
				+ " FROM   Bestellung b"
				+ " WHERE  b.id = :"
				+ Bestellung.PARAM_ID),
		@NamedQuery(name = Bestellung.FIND_ALLE_BESTELLUNG, query = "select b from Bestellung as b"),
		@NamedQuery(name = Bestellung.FIND_BESTELLUNGEN_BY_IDLIEFERUNG, query = "SELECT l.bestellungen"
				+ " FROM   Lieferung l"
				+ " WHERE  l.id = :"
				+ Bestellung.PARAM_LIEFERUNGEN),
		@NamedQuery(name = Bestellung.FIND_KUNDE_BY_ID, query = "SELECT k"
				+ " FROM  Kunde k" + " WHERE k.id = :" + Bestellung.PARAM_KUNDE),
		@NamedQuery(name = Bestellung.FIND_BESTELLPOSITIONEN_BY_ID, query = "SELECT b.bestellpositionen"
				+ " FROM  Bestellung b"
				+ " WHERE b.id = :"
				+ Bestellung.PARAM_ID),
		@NamedQuery(name = Bestellung.FIND_BESTELLUNG_BY_ID_FETCH_LIEFERUNGEN, query = "SELECT DISTINCT b"
				+ " FROM   Bestellung b LEFT JOIN FETCH b.lieferungen"
				+ " WHERE  b.id = :" + Bestellung.PARAM_ID) })
public class Bestellung implements Serializable {

	private static final long serialVersionUID = -374175121702956649L;

	private static final String PREFIX = "Bestellung.";
	public static final String FIND_BESTELLUNGEN_BY_KUNDE = PREFIX
			+ "findBestellungenByKunde";
	public static final String FIND_BESTELLUNG_BY_ID = PREFIX
			+ "findBestellungenByIdFetchLieferungen";
	public static final String FIND_KUNDE_BY_IDBESTELLUNG = PREFIX
			+ "findBestellungKundeById";
	public static final String FIND_ALLE_BESTELLUNG = PREFIX
			+ "findAlleBestellung";
	public static final String FIND_BESTELLUNGEN_BY_IDLIEFERUNG = PREFIX
			+ "findBestellungByLieferung";
	public static final String FIND_KUNDE_BY_ID = PREFIX + "findKundeById";
	public static final String FIND_BESTELLPOSITIONEN_BY_ID = PREFIX
			+ "findBestellpositionenById";
	public static final String FIND_BESTELLUNG_BY_ID_FETCH_LIEFERUNGEN = PREFIX
			+ "findBestellungByIdFetchLieferungen";
	public static final String PARAM_KUNDEID = "k_fk";
	public static final String PARAM_ID = "id";
	public static final String PARAM_LIEFERUNGEN = "lieferungen";
	public static final String PARAM_KUNDE = "kunde";
	public static final String PARAM_BESTELLPOSITION = "b_fk";

	@Override
	public String toString() {
		return "Bestellung [id=" + id + ", version=" + version
				+ ", aktualisiert=" + aktualisiert + ", erzeugt=" + erzeugt
				+ ", preis=" + preis + ", status=" + status + ", kunde="
				+ kunde + "]";
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Min(value = MIN_ID, message = "{bestellverwaltung.bestellung.id.min}", groups = IdGroup.class)
	@Column(name = "id", unique = true, nullable = false, updatable = false)
	private Long id = KEINE_ID;

	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;

	@Column(name = "aktualisiert", nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date aktualisiert;

	@Column(name = "erzeugt", nullable = false)
	@Temporal(TIMESTAMP)
	private Date erzeugt;

	@DecimalMin("0.0")
	@Column(name = "preis", nullable = true)
	private Double preis;

	@NotNull(message = "{bestellverwaltung.bestellung.status.notNull}")
	@Column(name = "status", nullable = false)
	private String status;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "k_fk", nullable = false, updatable = false, insertable = false)
	@JsonIgnore
	private Kunde kunde;

	@Transient
	@JsonProperty("kunde")
	private URI kundeUri;

	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "b_fk", nullable = false, updatable = false)
	@NotEmpty(message = "{bestellverwaltung.bestellung.bestellpositionen.notEmpty}")
	@Valid
	@JsonProperty("bestellpositionen")
	private List<Bestellposition> bestellpositionen;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "bestellung_lieferung", joinColumns = @JoinColumn(name = "b_fk"), 
							inverseJoinColumns = @JoinColumn(name = "l_fk"))
	@JsonIgnore
	private Set<Lieferung> lieferungen;

	@Transient
	@JsonProperty("lieferungen")
	private URI lieferungenUri;

	public Bestellung() {
		super();
	}

	public Bestellung(List<Bestellposition> bestellpositionen) {
		super();
		this.bestellpositionen = bestellpositionen;
	}

	public String bestellpositionenToString() {
		String bpn = "";
		for (Bestellposition bp : bestellpositionen ){
		bpn = bpn + bp.toString() + " ";}
		return bpn;
	}
	
	@PrePersist
	private void prePersist() {
		erzeugt = new Date();
		aktualisiert = new Date();
	}

	@PreUpdate
	private void preUpdate() {
		aktualisiert = new Date();
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Date getAktualisiert() {
		return this.aktualisiert;
	}

	public void setAktualisiert(Date aktualisiert) {
		this.aktualisiert = aktualisiert;
	}
	
	@JsonProperty("datum")
	public Date getErzeugt() {
		return this.erzeugt;
	}
	
	public String getErzeugt(String format) {
		final Format formatter = new SimpleDateFormat(format, Locale.getDefault());
		return formatter.format(erzeugt);
	}

	public void setErzeugt(Date erzeugt) {
		this.erzeugt = erzeugt;
	}

	public Double getPreis() {
		return this.preis;
	}

	public void setPreis(Double preis) {
		this.preis = preis;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Valid
	public Set<Lieferung> getLieferungen() {
		return lieferungen;
	}

	public void setLieferungen(Set<Lieferung> l) {
		this.lieferungen = l;
	}

	public void addLieferung(Lieferung lieferung) {
		if (lieferungen == null) {
			lieferungen = new HashSet<>();
		}
		lieferungen.add(lieferung);
	}

	public URI getLieferungenUri() {
		return lieferungenUri;
	}

	public void setLieferungenUri(URI lieferungenUri) {
		this.lieferungenUri = lieferungenUri;
	}

	@Valid
	public Kunde getKunde() {
		return kunde;
	}

	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
	}

	public URI getKundeUri() {
		return kundeUri;
	}

	public void setKundeUri(URI kundeUri) {
		this.kundeUri = kundeUri;
	}

	public List<Bestellposition> getBestellpositionen() {
		return Collections.unmodifiableList(bestellpositionen);
	}

	public void setBestellpositionen(List<Bestellposition> bestellpositionen) {
		if (this.bestellpositionen == null) {
			this.bestellpositionen = bestellpositionen;
			return;
		}

		this.bestellpositionen.clear();
		if (bestellpositionen != null) {
			this.bestellpositionen.addAll(bestellpositionen);
		}
	}

	public Bestellung addBestellposition(Bestellposition bestellposition) {
		if (bestellpositionen == null) {
			bestellpositionen = new ArrayList<>();
		}

		bestellpositionen.add(bestellposition);
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((kunde == null) ? 0 : kunde.hashCode());
		result = prime * result + ((erzeugt == null) ? 0 : erzeugt.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Bestellung other = (Bestellung) obj;

		if (kunde == null) {
			if (other.kunde != null) {
				return false;
			}
		} 
		else if (!kunde.equals(other.kunde)) {
			return false;
		}

		if (erzeugt == null) {
			if (other.erzeugt != null) {
				return false;
			}
		} 
		else if (!erzeugt.equals(other.erzeugt)) {
			return false;
		}

		return true;
	}

	public void setValues(Bestellung bestellung) {
		this.version = bestellung.version;
		this.aktualisiert = bestellung.aktualisiert;
		this.bestellpositionen = bestellung.bestellpositionen;
		this.erzeugt = bestellung.erzeugt;
		this.kunde = bestellung.kunde;
		this.lieferungen = bestellung.lieferungen;
		this.preis = bestellung.preis;
		this.status = bestellung.status;

	}

}
