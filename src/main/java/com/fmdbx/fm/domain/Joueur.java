package com.fmdbx.fm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Joueur.
 */
@Entity
@Table(name = "joueur")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Joueur implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "nom")
    private String nom;

    @Column(name = "prenom")
    private String prenom;

    @Column(name = "photo")
    private String photo;

    @Column(name = "position")
    private String position;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(name = "nombre_selections")
    private Integer nombreSelections;

    @Column(name = "nombre_buts_internationaux")
    private Integer nombreButsInternationaux;

    @Column(name = "valeur")
    private Integer valeur;

    @Column(name = "salaire")
    private Integer salaire;

    @Column(name = "cout_estime")
    private Integer coutEstime;

    @ManyToOne
    @JsonIgnoreProperties(value = { "championnat", "jeuxes", "bookmarks" }, allowSetters = true)
    private Club club;

    @ManyToMany
    @JoinTable(
        name = "rel_joueur__favoris",
        joinColumns = @JoinColumn(name = "joueur_id"),
        inverseJoinColumns = @JoinColumn(name = "favoris_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(
        value = { "user", "commentaires", "conversationEnvoies", "conversationRecus", "messages", "bookmarks", "favorises" },
        allowSetters = true
    )
    private Set<Abonne> favorises = new HashSet<>();

    @ManyToMany(mappedBy = "joueurs")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "championnats", "joueurs" }, allowSetters = true)
    private Set<Pays> pays = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Joueur id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Joueur nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return this.prenom;
    }

    public Joueur prenom(String prenom) {
        this.setPrenom(prenom);
        return this;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getPhoto() {
        return this.photo;
    }

    public Joueur photo(String photo) {
        this.setPhoto(photo);
        return this;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPosition() {
        return this.position;
    }

    public Joueur position(String position) {
        this.setPosition(position);
        return this;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public LocalDate getDateNaissance() {
        return this.dateNaissance;
    }

    public Joueur dateNaissance(LocalDate dateNaissance) {
        this.setDateNaissance(dateNaissance);
        return this;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public Integer getNombreSelections() {
        return this.nombreSelections;
    }

    public Joueur nombreSelections(Integer nombreSelections) {
        this.setNombreSelections(nombreSelections);
        return this;
    }

    public void setNombreSelections(Integer nombreSelections) {
        this.nombreSelections = nombreSelections;
    }

    public Integer getNombreButsInternationaux() {
        return this.nombreButsInternationaux;
    }

    public Joueur nombreButsInternationaux(Integer nombreButsInternationaux) {
        this.setNombreButsInternationaux(nombreButsInternationaux);
        return this;
    }

    public void setNombreButsInternationaux(Integer nombreButsInternationaux) {
        this.nombreButsInternationaux = nombreButsInternationaux;
    }

    public Integer getValeur() {
        return this.valeur;
    }

    public Joueur valeur(Integer valeur) {
        this.setValeur(valeur);
        return this;
    }

    public void setValeur(Integer valeur) {
        this.valeur = valeur;
    }

    public Integer getSalaire() {
        return this.salaire;
    }

    public Joueur salaire(Integer salaire) {
        this.setSalaire(salaire);
        return this;
    }

    public void setSalaire(Integer salaire) {
        this.salaire = salaire;
    }

    public Integer getCoutEstime() {
        return this.coutEstime;
    }

    public Joueur coutEstime(Integer coutEstime) {
        this.setCoutEstime(coutEstime);
        return this;
    }

    public void setCoutEstime(Integer coutEstime) {
        this.coutEstime = coutEstime;
    }

    public Club getClub() {
        return this.club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public Joueur club(Club club) {
        this.setClub(club);
        return this;
    }

    public Set<Abonne> getFavorises() {
        return this.favorises;
    }

    public void setFavorises(Set<Abonne> abonnes) {
        this.favorises = abonnes;
    }

    public Joueur favorises(Set<Abonne> abonnes) {
        this.setFavorises(abonnes);
        return this;
    }

    public Joueur addFavoris(Abonne abonne) {
        this.favorises.add(abonne);
        abonne.getFavorises().add(this);
        return this;
    }

    public Joueur removeFavoris(Abonne abonne) {
        this.favorises.remove(abonne);
        abonne.getFavorises().remove(this);
        return this;
    }

    public Set<Pays> getPays() {
        return this.pays;
    }

    public void setPays(Set<Pays> pays) {
        if (this.pays != null) {
            this.pays.forEach(i -> i.removeJoueurs(this));
        }
        if (pays != null) {
            pays.forEach(i -> i.addJoueurs(this));
        }
        this.pays = pays;
    }

    public Joueur pays(Set<Pays> pays) {
        this.setPays(pays);
        return this;
    }

    public Joueur addPays(Pays pays) {
        this.pays.add(pays);
        pays.getJoueurs().add(this);
        return this;
    }

    public Joueur removePays(Pays pays) {
        this.pays.remove(pays);
        pays.getJoueurs().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Joueur)) {
            return false;
        }
        return id != null && id.equals(((Joueur) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Joueur{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", prenom='" + getPrenom() + "'" +
            ", photo='" + getPhoto() + "'" +
            ", position='" + getPosition() + "'" +
            ", dateNaissance='" + getDateNaissance() + "'" +
            ", nombreSelections=" + getNombreSelections() +
            ", nombreButsInternationaux=" + getNombreButsInternationaux() +
            ", valeur=" + getValeur() +
            ", salaire=" + getSalaire() +
            ", coutEstime=" + getCoutEstime() +
            "}";
    }
}
