package de.shop.bestellverwaltung.domain;


import static de.shop.util.Constants.MIN_ID;

import java.io.Serializable;
import java.net.URI;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;
import javax.persistence.Table;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.util.IdGroup;

@Entity
@Table(name = "bestellposition")
@NamedQueries({
    @NamedQuery(name  = Bestellposition.FIND_LADENHUETER,
   	            query = "SELECT a"
   	            	    + " FROM   Artikel a"
   	            	    + " WHERE  a NOT IN (SELECT bp.artikel FROM Bestellposition bp)")
})
@XmlRootElement
public class Bestellposition implements Serializable {
	private static final long serialVersionUID = 2222771733641950913L;
	
	private static final String PREFIX = "Bestellposition.";
	public static final String FIND_LADENHUETER = PREFIX + "findLadenhueter";
	private static final int ANZAHL_MIN = 1;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Min(value = MIN_ID, message = "{kundenverwaltung.bestellposition.id.min}", groups = IdGroup.class)
	@Column(name = "id", unique = true, nullable = false, updatable = false)
	@XmlAttribute
	private Long id;
	
	@ManyToOne(optional = false)
    @JoinColumn(name = "ar_fk", nullable = false)
	@NotNull(message = "{bestellverwaltung.bestellposition.artikel.notNull}")
	@XmlTransient
	private Artikel artikel;

	@Transient
	@XmlElement(name = "artikel", required = true)
	private URI artikelUri;
	
	@Column(name = "anzahl", nullable = false)
	@Min(value = ANZAHL_MIN, message = "{bestellverwaltung.bestellposition.anzahl.min}")
	@XmlElement
	private Integer anzahl;
	
	@Column(name = "preis", nullable = true)

	@DecimalMin("0.0")
	@XmlElement
	private Double preis;
	
	public Bestellposition() {
		super();
	}
	
	public Bestellposition(Artikel artikel) {
		super();
		this.artikel = artikel;
		this.anzahl = 1;
	}
	
	public Bestellposition(Artikel artikel, Integer anzahl) {
		super();
		this.artikel = artikel;
		this.anzahl = anzahl;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Artikel getArtikel() {
		return artikel;
	}

	public void setArtikel(Artikel artikel) {
		this.artikel = artikel;
	}

	public URI getArtikelUri() {
		return artikelUri;
	}

	public void setArtikelUri(URI artikelUri) {
		this.artikelUri = artikelUri;
	}
	
	public Integer getAnzahl() {
		return anzahl;
	}
	public void setAnzahl(Integer anzahl) {
		this.anzahl = anzahl;
	}
	
	public Double getPreis() {
		return preis;
	}
	public void setPreis(Double preis) {
		this.preis = preis;
	}
	
	@Override
	public String toString() {
		final Long artikelId = artikel == null ? null : artikel.getId();
		return "Bestellposition [id=" + id + ", artikel=" + artikelId
			   + ", anzahl=" + anzahl + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + anzahl;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((artikel == null) ? 0 : artikel.hashCode());
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
		Bestellposition other = (Bestellposition) obj;
		
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		}
		else if (!id.equals(other.id)) {
			return false;
		}
		
		if (artikel == null) {
			if (other.artikel != null) {
				return false;
			}
		}
		else if (!artikel.equals(other.artikel)) {
			return false;
		}
		
		return true;
	}
}
