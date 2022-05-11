package com.fmdbx.fm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Championnat.
 */
@Entity
@Table(name = "championnat")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Championnat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "nom")
    private String nom;

    @Column(name = "nombre_d_equipes")
    private Integer nombreDEquipes;

    @Lob
    @Column(name = "logo")
    private byte[] logo;

    @Column(name = "logo_content_type")
    private String logoContentType;

    @Column(name = "niveau")
    private Integer niveau;

    @Column(name = "reputation")
    private Integer reputation;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "championnats", "joueurs" }, allowSetters = true)
    private Pays pays;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Championnat id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Championnat nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Integer getNombreDEquipes() {
        return this.nombreDEquipes;
    }

    public Championnat nombreDEquipes(Integer nombreDEquipes) {
        this.setNombreDEquipes(nombreDEquipes);
        return this;
    }

    public void setNombreDEquipes(Integer nombreDEquipes) {
        this.nombreDEquipes = nombreDEquipes;
    }

    public byte[] getLogo() {
        return this.logo;
    }

    public Championnat logo(byte[] logo) {
        this.setLogo(logo);
        return this;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public String getLogoContentType() {
        return this.logoContentType;
    }

    public Championnat logoContentType(String logoContentType) {
        this.logoContentType = logoContentType;
        return this;
    }

    public void setLogoContentType(String logoContentType) {
        this.logoContentType = logoContentType;
    }

    public Integer getNiveau() {
        return this.niveau;
    }

    public Championnat niveau(Integer niveau) {
        this.setNiveau(niveau);
        return this;
    }

    public void setNiveau(Integer niveau) {
        this.niveau = niveau;
    }

    public Integer getReputation() {
        return this.reputation;
    }

    public Championnat reputation(Integer reputation) {
        this.setReputation(reputation);
        return this;
    }

    public void setReputation(Integer reputation) {
        this.reputation = reputation;
    }

    public Pays getPays() {
        return this.pays;
    }

    public void setPays(Pays pays) {
        this.pays = pays;
    }

    public Championnat pays(Pays pays) {
        this.setPays(pays);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Championnat)) {
            return false;
        }
        return id != null && id.equals(((Championnat) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Championnat{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", nombreDEquipes=" + getNombreDEquipes() +
            ", logo='" + getLogo() + "'" +
            ", logoContentType='" + getLogoContentType() + "'" +
            ", niveau=" + getNiveau() +
            ", reputation=" + getReputation() +
            "}";
    }
}
