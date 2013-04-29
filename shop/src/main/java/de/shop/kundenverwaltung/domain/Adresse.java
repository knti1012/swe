package de.shop.kundenverwaltung.domain;


import static de.shop.util.Constants.ERSTE_VERSION;
import static de.shop.util.Constants.MIN_ID;
import de.shop.util.IdGroup;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static de.shop.util.Constants.KEINE_ID;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;



@Entity
@Table (name = "adresse")
public class Adresse implements Serializable {

	
	private static final long serialVersionUID = 3191064758202013433L;

	public static final int PLZ_LENGTH_MIN = 2;
	public static final int PLZ_LENGTH_MAX = 8;
	public static final int STRASSE_LENGTH_MIN = 2;
	public static final int STRASSE_LENGTH_MAX = 32;
	public static final int HAUSNR_LENGTH_MAX = 4;
	public static final int STADT_LENGTH_MIN = 2;
	public static final int STADT_LENGTH_MAX = 32;
	public static final int LAND_LENGTH_MIN = 2;
	public static final int LAND_LENGTH_MAX = 32;

	@Override
	public String toString() {
		return "Adresse [id=" + id + ", version=" + version
				+ ", hausnummer=" + hausnummer + ", land="
				+ land + ", plz=" + plz + ", stadt=" + stadt + ", strasse="
				+ strasse + "]";
	}

	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false, updatable = false)
	@Min(value = MIN_ID, message = "{kundenverwaltung.adresse.id.min}", groups = IdGroup.class)
	private Long id = KEINE_ID;
	
	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;

	@Column(name = "hausnummer")
	@Min(value = 1, message = "{kundenverwaltung.adresse.hausnummer.min}")
	private Integer hausnummer;

	@Column(name = "land", nullable = false)
	@NotNull(message = "{kundenverwaltung.adresse.land.notNull}")
	@Size(min = LAND_LENGTH_MIN, max = LAND_LENGTH_MAX, message = "{kundenverwaltung.adresse.land.length}")
	private String land;
	
	
	@Column(name = "plz", length = PLZ_LENGTH_MAX, nullable = false)
	@NotNull(message = "{kundenverwaltung.adresse.plz.notNull}")
	@Size(min = PLZ_LENGTH_MIN, max = PLZ_LENGTH_MAX, message = "{kundenverwaltung.adresse.plz.length}")
	@Pattern(regexp = "\\d{5}", message = "{kundenverwaltung.adresse.plz}")
	private String plz;

	
	@Column(name = "stadt", length = STADT_LENGTH_MAX, nullable = false)
	@NotNull(message = "{kundenverwaltung.adresse.stadt.notNull}")
	@Size(min = STADT_LENGTH_MIN, max = STADT_LENGTH_MAX, message = "{kundenverwaltung.adresse.stadt.length}")
	private String stadt;
	
	
	@Column(name = "strasse", length = STRASSE_LENGTH_MAX, nullable = false)
	@NotNull(message = "{kundenverwaltung.adresse.strasse.notNull}")
	@Size(min = STRASSE_LENGTH_MIN, max = STRASSE_LENGTH_MAX, message = "{kundenverwaltung.adresse.strasse.length}")
	private String strasse;

	
	public Adresse() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public int getVersion() {
		return this.version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}

	public Integer getHausnummer() {
		return this.hausnummer;
	}

	public void setHausnummer(Integer hausnummer) {
		this.hausnummer = hausnummer;
	}

	public String getLand() {
		return this.land;
	}

	public void setLand(String land) {
		this.land = land;
	}

	public String getPlz() {
		return this.plz;
	}

	public void setPlz(String plz) {
		this.plz = plz;
	}

	public String getStadt() {
		return this.stadt;
	}

	public void setStadt(String stadt) {
		this.stadt = stadt;
	}

	public String getStrasse() {
		return this.strasse;
	}

	public void setStrasse(String strasse) {
		this.strasse = strasse;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((hausnummer == null) ? 0 : hausnummer.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((land == null) ? 0 : land.hashCode());
		result = prime * result + ((plz == null) ? 0 : plz.hashCode());
		result = prime * result + ((stadt == null) ? 0 : stadt.hashCode());
		result = prime * result + ((strasse == null) ? 0 : strasse.hashCode());
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
		final Adresse other = (Adresse) obj;
		if (hausnummer == null) {
			if (other.hausnummer != null)
				return false;
		} 
		else if (!hausnummer.equals(other.hausnummer))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} 
		else if (!id.equals(other.id))
			return false;
		if (land == null) {
			if (other.land != null)
				return false;
		} 
		else if (!land.equals(other.land))
			return false;
		if (plz == null) {
			if (other.plz != null)
				return false;
		} 
		else if (!plz.equals(other.plz))
			return false;
		if (stadt == null) {
			if (other.stadt != null)
				return false;
		} 
		else if (!stadt.equals(other.stadt))
			return false;
		if (strasse == null) {
			if (other.strasse != null)
				return false;
		} 
		else if (!strasse.equals(other.strasse))
			return false;
		return true;
	}

	public void setValues(Adresse adresse) {
		this.setVersion(adresse.getVersion());
		this.setId(adresse.getId());
		this.setStrasse(adresse.getStrasse());
		this.setHausnummer(adresse.getHausnummer());
		this.setPlz(adresse.getPlz());
		this.setStadt(adresse.getStadt());
		this.setLand(adresse.getLand());
		
	}
}