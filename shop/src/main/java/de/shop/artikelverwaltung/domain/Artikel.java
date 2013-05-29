package de.shop.artikelverwaltung.domain;


import static de.shop.util.Constants.ERSTE_VERSION;
import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Constants.MIN_ID;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Version;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;

import de.shop.util.IdGroup;
@Entity
@Table(name = "artikel")
@NamedQueries({
		@NamedQuery(name = Artikel.FIND_VERFUEGBARE_ARTIKEL, query = "SELECT      a"
				+ " FROM     Artikel a"
				+ " WHERE    a.lagerbestand>0"
				+ " ORDER BY a.id ASC"),
		@NamedQuery(name = Artikel.FIND_ARTIKEL_BY_KATEGORIE, query = "SELECT      a"
				+ " FROM     Artikel a"
				+ " WHERE    a.kategorie = :"
				+ Artikel.PARAM_KAT
				+ " AND a.lagerbestand>0"
				+ " ORDER BY a.id ASC"),
		@NamedQuery(name = Artikel.FIND_ARTIKEL_MAX_PREIS, query = "SELECT      a"
				+ " FROM     Artikel a"
				+ " WHERE    a.preis < :"
				+ Artikel.PARAM_PREIS + " ORDER BY a.id ASC"),
		@NamedQuery(name = Artikel.FIND_ARTIKEL_BY_NAME, query = "SELECT		 a"
				+ " FROM 	 Artikel a" + " WHERE 	 a.name LIKE :"
				+ Artikel.PARAM_NAME + " ORDER BY a.id ASC") })
public class Artikel implements Serializable {

	private static final long serialVersionUID = 1346429702479816595L;

	private static final int BEZEICHNUNG_LENGTH_MAX = 32;
	private static final String PREFIX = "Artikel.";
	public static final String FIND_VERFUEGBARE_ARTIKEL = PREFIX
			+ "findVerfuegbareArtikel";
	public static final String FIND_ARTIKEL_BY_KATEGORIE = PREFIX
			+ "findArtikelByKategorie";
	public static final String FIND_ARTIKEL_MAX_PREIS = PREFIX
			+ "findArtikelByMaxPreis";
	public static final String FIND_ARTIKEL_BY_NAME = PREFIX
			+ "findArtikelByName";

	public static final String PARAM_KAT = "kategorie";
	public static final String PARAM_PREIS = "preis";
	public static final String PARAM_NAME = "name";

	@Override
	public String toString() {
		return "Artikel [id=" + id + ", version=" + version
				+ ", art=" + art + ", farbe=" + farbe
				+ ", groesse=" + groesse + ", kategorie=" + kategorie
				+ ", lagerbestand=" + lagerbestand + ", name=" + name
				+ ", preis=" + preis + "]";
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false, updatable = false)
	@Min(value = MIN_ID, message = "{artikelverwaltung.artikel.id.min}", groups = IdGroup.class)
	private Long id = KEINE_ID;

	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;
	
	@Column(name = "art", nullable = false)
	@NotNull(message = "{artikelverwaltung.artikel.art.notNull}")
	@Size(max = BEZEICHNUNG_LENGTH_MAX, message = "{artikelverwaltung.artikel.bezeichnung.length}")
	private String art;

	@Column(name = "farbe", nullable = false)
	@NotNull(message = "{artikelverwaltung.artikel.farbe.notNull}")
	private String farbe;

	@Column(name = "groesse", nullable = false)
	@NotNull(message = "{artikelverwaltung.artikel.groesse.notNull}")
	private String groesse;

	@Column(name = "kategorie", nullable = false)
	@NotNull(message = "{artikelverwaltung.artikel.kategorie.notNull}")
	private String kategorie;

	@Column(name = "lagerbestand", nullable = false)
	private BigInteger lagerbestand;

	@Column(name = "name", nullable = false)
	@NotNull(message = "{artikelverwaltung.artikel.name.notNull}")
	private String name;

	@Column(name = "aktualisiert", nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date aktualisiert;

	@Column(name = "erzeugt", nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date erzeugt;

	@Column(name = "preis")
	@DecimalMin("0.0")
	private Double preis;

	public Artikel() {
		super();
//		this.name = "";
	}
	

	public Artikel(String art, double preis) {
		super();
		this.art = art;
		this.preis = preis;
		this.name = "";
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
	
	public String getArt() {
		return this.art;
	}

	public void setArt(String art) {
		this.art = art;
	}

	public String getFarbe() {
		return this.farbe;
	}

	public void setFarbe(String farbe) {
		this.farbe = farbe;
	}

	public String getGroesse() {
		return this.groesse;
	}

	public void setGroesse(String groesse) {
		this.groesse = groesse;
	}

	public String getKategorie() {
		return this.kategorie;
	}

	public void setKategorie(String kategorie) {
		this.kategorie = kategorie;
	}

	public BigInteger getLagerbestand() {
		return this.lagerbestand;
	}

	public void setLagerbestand(BigInteger lagerbestand) {
		this.lagerbestand = lagerbestand;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPreis() {
		return this.preis;
	}

	public void setPreis(Double preis) {
		this.preis = preis;
	}

	public Date getErzeugt() {
		return this.erzeugt;
	}

	public Date getAktualisiert() {
		return this.aktualisiert;
	}

	public void setValues(Artikel artikel) {
		this.version = artikel.version;
		this.art = artikel.art;
		this.farbe = artikel.farbe;
		this.groesse = artikel.groesse;
		this.id = artikel.id;
		this.kategorie = artikel.kategorie;
		this.lagerbestand = artikel.lagerbestand;
		this.name = artikel.name;
		this.preis = artikel.preis;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((art == null) ? 0 : art.hashCode());
		result = prime * result + ((farbe == null) ? 0 : farbe.hashCode());
		result = prime * result + ((groesse == null) ? 0 : groesse.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((kategorie == null) ? 0 : kategorie.hashCode());
		result = prime * result
				+ ((lagerbestand == null) ? 0 : lagerbestand.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((preis == null) ? 0 : preis.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Artikel other = (Artikel) obj;
		if (art == null) {
			if (other.art != null)
				return false;
		} 
		else if (!art.equals(other.art))
			return false;
		if (farbe == null) {
			if (other.farbe != null)
				return false;
		} 
		else if (!farbe.equals(other.farbe))
			return false;
		if (groesse == null) {
			if (other.groesse != null)
				return false;
		} 
		else if (!groesse.equals(other.groesse))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} 
		else if (!id.equals(other.id))
			return false;
		if (kategorie == null) {
			if (other.kategorie != null)
				return false;
		}
		else if (!kategorie.equals(other.kategorie))
			return false;
		if (lagerbestand == null) {
			if (other.lagerbestand != null)
				return false;
		} 
		else if (!lagerbestand.equals(other.lagerbestand))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (preis == null) {
			if (other.preis != null)
				return false;
		}
		else if (!preis.equals(other.preis))
			return false;
		return true;
	}
	
	
}
