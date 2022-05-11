package com.fmdbx.fm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Version.
 */
@Entity
@Table(name = "version")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Version implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "version", nullable = false)
    private String version;

    @ManyToOne
    @JsonIgnoreProperties(value = { "versions", "clubs" }, allowSetters = true)
    private Jeu jeu;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Version id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersion() {
        return this.version;
    }

    public Version version(String version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Jeu getJeu() {
        return this.jeu;
    }

    public void setJeu(Jeu jeu) {
        this.jeu = jeu;
    }

    public Version jeu(Jeu jeu) {
        this.setJeu(jeu);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Version)) {
            return false;
        }
        return id != null && id.equals(((Version) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Version{" +
            "id=" + getId() +
            ", version='" + getVersion() + "'" +
            "}";
    }
}
