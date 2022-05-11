package com.fmdbx.fm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Jeu.
 */
@Entity
@Table(name = "jeu")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Jeu implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nom", nullable = false)
    private String nom;

    @OneToMany(mappedBy = "jeu")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "jeu" }, allowSetters = true)
    private Set<Version> versions = new HashSet<>();

    @ManyToMany(mappedBy = "jeuxes")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "championnat", "jeuxes", "bookmarks" }, allowSetters = true)
    private Set<Club> clubs = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Jeu id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Jeu nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Set<Version> getVersions() {
        return this.versions;
    }

    public void setVersions(Set<Version> versions) {
        if (this.versions != null) {
            this.versions.forEach(i -> i.setJeu(null));
        }
        if (versions != null) {
            versions.forEach(i -> i.setJeu(this));
        }
        this.versions = versions;
    }

    public Jeu versions(Set<Version> versions) {
        this.setVersions(versions);
        return this;
    }

    public Jeu addVersions(Version version) {
        this.versions.add(version);
        version.setJeu(this);
        return this;
    }

    public Jeu removeVersions(Version version) {
        this.versions.remove(version);
        version.setJeu(null);
        return this;
    }

    public Set<Club> getClubs() {
        return this.clubs;
    }

    public void setClubs(Set<Club> clubs) {
        if (this.clubs != null) {
            this.clubs.forEach(i -> i.removeJeux(this));
        }
        if (clubs != null) {
            clubs.forEach(i -> i.addJeux(this));
        }
        this.clubs = clubs;
    }

    public Jeu clubs(Set<Club> clubs) {
        this.setClubs(clubs);
        return this;
    }

    public Jeu addClubs(Club club) {
        this.clubs.add(club);
        club.getJeuxes().add(this);
        return this;
    }

    public Jeu removeClubs(Club club) {
        this.clubs.remove(club);
        club.getJeuxes().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Jeu)) {
            return false;
        }
        return id != null && id.equals(((Jeu) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Jeu{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            "}";
    }
}
