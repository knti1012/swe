package de.shop.kundenverwaltung.domain;

import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Constants.ERSTE_VERSION;
import static de.shop.util.Constants.MIN_ID;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.TemporalType.TIMESTAMP;
import static javax.persistence.FetchType.EAGER;

import javax.persistence.ElementCollection;
import javax.persistence.JoinColumn;

import java.util.Set;

import de.shop.auth.service.jboss.AuthService.RolleType;

import javax.persistence.UniqueConstraint;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.ScriptAssert;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.util.IdGroup;

@Entity
@Table(name = "kunde")
@Inheritance
@NamedQueries({
		@NamedQuery(name = Kunde.FIND_KUNDEN, query = "SELECT k"
				+ " FROM   Kunde k"),
		@NamedQuery(name = Kunde.FIND_KUNDEN_BY_ID, query = "SELECT k"
				+ " FROM  Kunde k" + " WHERE k.id = :" + Kunde.PARAM_KUNDE_ID),
		@NamedQuery(name = Kunde.FIND_KUNDEN_FETCH_BESTELLUNGEN, query = "SELECT  DISTINCT k"
				+ " FROM Kunde k LEFT JOIN FETCH k.bestellungen"),
		@NamedQuery(name = Kunde.FIND_KUNDEN_ORDER_BY_ID, query = "SELECT   k"
				+ " FROM  Kunde k" + " ORDER BY k.id"),
		@NamedQuery(name = Kunde.FIND_IDS_BY_PREFIX, query = "SELECT   k.id"
				+ " FROM  Kunde k" + " WHERE CONCAT('', k.id) LIKE :"
				+ Kunde.PARAM_KUNDE_ID_PREFIX + " ORDER BY k.id"),
		@NamedQuery(name = Kunde.FIND_KUNDEN_BY_NACHNAME, query = "SELECT k"
				+ " FROM   Kunde k" + " WHERE  UPPER(k.nachname) = UPPER(:"
				+ Kunde.PARAM_KUNDE_NACHNAME + ")"),
		@NamedQuery(name = Kunde.FIND_NACHNAMEN_BY_PREFIX, query = "SELECT   DISTINCT k.nachname"
				+ " FROM  Kunde k "
				+ " WHERE UPPER(k.nachname) LIKE UPPER(:"
				+ Kunde.PARAM_KUNDE_NACHNAME_PREFIX + ")"),
		@NamedQuery(name = Kunde.FIND_KUNDEN_BY_NACHNAME_FETCH_BESTELLUNGEN, query = "SELECT DISTINCT k"
				+ " FROM   Kunde k LEFT JOIN FETCH k.bestellungen"
				+ " WHERE  UPPER(k.nachname) = UPPER(:"
				+ Kunde.PARAM_KUNDE_NACHNAME + ")"),
		@NamedQuery(name = Kunde.FIND_KUNDE_BY_ID_FETCH_BESTELLUNGEN, query = "SELECT DISTINCT k"
				+ " FROM   Kunde k LEFT JOIN FETCH k.bestellungen"
				+ " WHERE  k.id = :" + Kunde.PARAM_KUNDE_ID),
		@NamedQuery(name = Kunde.FIND_KUNDE_BY_EMAIL, query = "SELECT DISTINCT k"
				+ " FROM   Kunde k"
				+ " WHERE  k.email = :"
				+ Kunde.PARAM_KUNDE_EMAIL),
		@NamedQuery(name = Kunde.FIND_KUNDEN_BY_PLZ, query = "SELECT k"
				+ " FROM  Kunde k" + " WHERE k.adresse.plz = :"
				+ Kunde.PARAM_KUNDE_ADRESSE_PLZ),
		@NamedQuery(name = Kunde.FIND_KUNDEN_BY_DATE, query = "SELECT k"
				+ " FROM  Kunde k" + " WHERE k.erzeugt = :"
				+ Kunde.PARAM_KUNDE_ERZEUGT) })
@ScriptAssert(lang = "javascript", script = "(_this.password == null && _this.passwordWdh == null)"
		+ "|| (_this.password != null && _this.password.equals(_this.passwordWdh))", message = "{kundenverwaltung.kunde.password.notEqual}", groups = PasswordGroup.class)
public class Kunde implements Serializable {

	private static final long serialVersionUID = -9003409977721553025L;

	private static final String NAME_PATTERN = "[A-Z\u00C4\u00D6\u00DC][a-z\u00E4\u00F6\u00FC\u00DF]+";
	private static final String PREFIX_ADEL = "(o'|von|von der|von und zu|van)?";

	public static final String NACHNAME_PATTERN = PREFIX_ADEL + NAME_PATTERN
			+ "(-" + NAME_PATTERN + ")?";
	public static final int NACHNAME_LENGTH_MIN = 2;
	public static final int NACHNAME_LENGTH_MAX = 32;
	public static final int VORNAME_LENGTH_MIN = 2;
	public static final int VORNAME_LENGTH_MAX = 32;
	public static final int EMAIL_LENGTH_MAX = 128;
	public static final int DETAILS_LENGTH_MAX = 128 * 1024;
	public static final int PASSWORD_LENGTH_MAX = 256;

	private static final String PREFIX = "Kunde.";
	public static final String FIND_KUNDEN = PREFIX + "findKunden";
	public static final String FIND_KUNDEN_BY_ID = PREFIX + "findKundenById";
	public static final String FIND_KUNDEN_FETCH_BESTELLUNGEN = PREFIX
			+ "findKundenFetchBestellungen";
	public static final String FIND_KUNDEN_ORDER_BY_ID = PREFIX
			+ "findKundenOrderById";
	public static final String FIND_IDS_BY_PREFIX = PREFIX + "findIdsByPrefix";
	public static final String FIND_KUNDEN_BY_NACHNAME = PREFIX
			+ "findKundenByNachname";
	public static final String FIND_KUNDEN_BY_NACHNAME_FETCH_BESTELLUNGEN = PREFIX
			+ "findKundenByNachnameFetchBestellungen";
	public static final String FIND_NACHNAMEN_BY_PREFIX = PREFIX
			+ "findNachnamenByPrefix";
	public static final String FIND_KUNDE_BY_ID_FETCH_BESTELLUNGEN = PREFIX
			+ "findKundeByIdFetchBestellungen";
	public static final String FIND_KUNDE_BY_EMAIL = PREFIX
			+ "findKundeByEmail";
	public static final String FIND_KUNDEN_BY_PLZ = PREFIX + "findKundenByPlz";
	public static final String FIND_KUNDEN_BY_DATE = PREFIX
			+ "findKundenByDate";

	public static final String PARAM_KUNDE_ID = "kundeId";
	public static final String PARAM_KUNDE_ID_PREFIX = "idPrefix";
	public static final String PARAM_KUNDE_NACHNAME = "nachname";
	public static final String PARAM_KUNDE_NACHNAME_PREFIX = "nachnamePrefix";
	public static final String PARAM_KUNDE_ADRESSE_PLZ = "plz";
	public static final String PARAM_KUNDE_ERZEUGT = "erzeugt";
	public static final String PARAM_KUNDE_EMAIL = "email";

	@Override
	public String toString() {
		return "Kunde [id=" + id + ", version=" + version
				+ ", aktualisiert=" + aktualisiert
				+ ", email=" + email + ", erzeugt=" + erzeugt + ", geschlecht="
				+ geschlecht + ", nachname=" + nachname + ", password="
				+ password + ", passwordWdh=" + passwordWdh
				+  ", vorname=" + vorname + "]";
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Min(value = MIN_ID, message = "{kundenverwaltung.kunde.id.min}", groups = IdGroup.class)
	@Column(name = "id", unique = true, nullable = false, updatable = false)
	private Long id = KEINE_ID;

	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;
	
	@Column(length = EMAIL_LENGTH_MAX, nullable = false, unique = true)
	@Email(message = "{kundenverwaltung.kunde.email.pattern}")
	private String email;

	@Column(name = "geschlecht")
	@NotNull(message = "{kundenverwaltung.kunde.geschlecht.notNull}")
	private String geschlecht;

	@Column(name = "nachname", nullable = false)
	@NotNull(message = "{kundenverwaltung.kunde.nachname.notNull}")
	@Pattern(regexp = NACHNAME_PATTERN, message = "{kundenverwaltung.kunde.nachname.pattern}")
	@Size(min = NACHNAME_LENGTH_MIN, max = NACHNAME_LENGTH_MAX, message = "{kundenverwaltung.kunde.nachname.length}")
	private String nachname;

	@Column(name = "password", length = PASSWORD_LENGTH_MAX)
	@NotNull(message = "{kundenverwaltung.kunde.password.notNull}")
	private String password;

	@Transient
	@JsonIgnore
	private String passwordWdh;

	@Column(name = "vorname")
	@NotNull(message = "{kundenverwaltung.kunde.nachname.notNull}")
	@Size(min = NACHNAME_LENGTH_MIN, max = NACHNAME_LENGTH_MAX, message = "{kundenverwaltung.kunde.vorname.length}")
	private String vorname;

	@Column(name = "erzeugt", nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date erzeugt;

	@Column(name = "aktualisiert", nullable = false)
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date aktualisiert;

	@Transient
	@JsonProperty("bestellungen")
	private URI bestellungenUri;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "k_fk", nullable = false)
	@JsonIgnore
	private List<Bestellung> bestellungen;

	@OneToOne(optional = false, fetch = FetchType.EAGER, cascade = {PERSIST, MERGE, REMOVE })
	@JoinColumn(name = "add_fk")
	@NotNull(message = "{kundenverwaltung.kunde.adresse.notNull}")
	private Adresse adresse; 
	
	@Transient
	@AssertTrue(message = "{kundenverwaltung.kunde.agb}")
	private boolean agbAkzeptiert;
	
	@ElementCollection(fetch = EAGER)
	@CollectionTable(name = "kunde_rolle",
	                 joinColumns = @JoinColumn(name = "kunde_fk", nullable = false),
	                 uniqueConstraints =  @UniqueConstraint(columnNames = { "kunde_fk", "rolle_fk" }))
	@Column(table = "kunde_rolle", name = "rolle_fk", nullable = false)
	private Set<RolleType> rollen;

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
		return version;
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

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getErzeugt() {
		return this.erzeugt;
	}

	public void setErzeugt(Date erzeugt) {
		this.erzeugt = erzeugt;
	}

	public String getGeschlecht() {
		return this.geschlecht;
	}

	public void setGeschlecht(String geschlecht) {
		this.geschlecht = geschlecht;
	}

	public String getNachname() {
		return this.nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordWdh() {
		return passwordWdh;
	}

	public void setPasswordWdh(String passwordWdh) {
		this.passwordWdh = passwordWdh;
	}
	
	public void setAgbAkzeptiert(boolean agbAkzeptiert) {
		this.agbAkzeptiert = agbAkzeptiert;
	}

	public boolean isAgbAkzeptiert() {
		return agbAkzeptiert;
	}
	
	public String getVorname() {
		return this.vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public Adresse getAdresse() {
		return adresse;
	}

	public void setAdresse(Adresse adresse) {
		this.adresse = adresse;
	}

	public List<Bestellung> getBestellungen() {
		return Collections.unmodifiableList(bestellungen);
	}

	public void setBestellungen(List<Bestellung> bestellungen) {
		if (this.bestellungen == null) {
			this.bestellungen = bestellungen;
			return;
		}

		this.bestellungen.clear();
		if (bestellungen != null) {
			this.bestellungen.addAll(bestellungen);
		}
	}

	public Kunde addBestellung(Bestellung bestellung) {
		if (bestellungen == null) {
			bestellungen = new ArrayList<>();
		}
		bestellungen.add(bestellung);
		return this;
	}
	
	public Set<RolleType> getRollen() {
		return rollen;
	}

	public void setRollen(Set<RolleType> rollen) {
		this.rollen = rollen;
	}

	@PostLoad
	protected void postLoad() {
		passwordWdh = password;
		agbAkzeptiert = true;
	}
	
	public void setValues(Kunde kunde) {
		this.version = kunde.version;
		this.adresse = kunde.adresse;
		this.bestellungen = kunde.bestellungen;
		this.email = kunde.email;
		this.geschlecht = kunde.geschlecht;
		this.nachname = kunde.nachname;
		this.password = kunde.password;
		this.passwordWdh = kunde.password;
		this.agbAkzeptiert = kunde.agbAkzeptiert;
		this.vorname = kunde.vorname;
	}

	public void setBestellungenUri(URI bestellungenUri2) {
	
		
	}

}
