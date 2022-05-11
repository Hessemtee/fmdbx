package com.fmdbx.fm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Commentaire.
 */
@Entity
@Table(name = "commentaire")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Commentaire implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "contenu")
    private String contenu;

    @Column(name = "visible")
    private Boolean visible;

    @ManyToOne
    @JsonIgnoreProperties(value = { "club", "favorises", "pays" }, allowSetters = true)
    private Joueur joueurConcerne;

    @ManyToOne
    @JsonIgnoreProperties(value = { "championnat", "jeuxes", "bookmarks" }, allowSetters = true)
    private Club clubConcerne;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "user", "commentaires", "conversationEnvoies", "conversationRecus", "messages", "bookmarks", "favorises" },
        allowSetters = true
    )
    private Abonne abonne;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Commentaire id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContenu() {
        return this.contenu;
    }

    public Commentaire contenu(String contenu) {
        this.setContenu(contenu);
        return this;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Boolean getVisible() {
        return this.visible;
    }

    public Commentaire visible(Boolean visible) {
        this.setVisible(visible);
        return this;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Joueur getJoueurConcerne() {
        return this.joueurConcerne;
    }

    public void setJoueurConcerne(Joueur joueur) {
        this.joueurConcerne = joueur;
    }

    public Commentaire joueurConcerne(Joueur joueur) {
        this.setJoueurConcerne(joueur);
        return this;
    }

    public Club getClubConcerne() {
        return this.clubConcerne;
    }

    public void setClubConcerne(Club club) {
        this.clubConcerne = club;
    }

    public Commentaire clubConcerne(Club club) {
        this.setClubConcerne(club);
        return this;
    }

    public Abonne getAbonne() {
        return this.abonne;
    }

    public void setAbonne(Abonne abonne) {
        this.abonne = abonne;
    }

    public Commentaire abonne(Abonne abonne) {
        this.setAbonne(abonne);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Commentaire)) {
            return false;
        }
        return id != null && id.equals(((Commentaire) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Commentaire{" +
            "id=" + getId() +
            ", contenu='" + getContenu() + "'" +
            ", visible='" + getVisible() + "'" +
            "}";
    }
}
