package de.shop.artikelverwaltung.service;

import java.util.Collection;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;

import de.shop.artikelverwaltung.domain.Artikel;

@ApplicationException(rollback = true)
public class InvalidArtikelIdException extends ArtikelServiceException {
	private static final long serialVersionUID = -8973151010781329074L;
	
	private final Long artikelId;
	private final Collection<ConstraintViolation<Artikel>> violations;
	
	public InvalidArtikelIdException(Long artikelId, Collection<ConstraintViolation<Artikel>> violations) {
		super("Ungueltige Artikel-ID: " + artikelId + ", Violations: " + violations);
		this.artikelId = artikelId;
		this.violations = violations;
	}

	public Long getArtikelId() {
		return artikelId;
	}

	public Collection<ConstraintViolation<Artikel>> getViolations() {
		return violations;
		}
	}