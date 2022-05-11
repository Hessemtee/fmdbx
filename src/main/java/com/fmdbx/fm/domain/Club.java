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
 * A Club.
 */
@Entity
@Table(name = "club")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Club implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nom", nullable = false)
    private String nom;

    @Lob
    @Column(name = "logo")
    private byte[] logo;

    @Column(name = "logo_content_type")
    private String logoContentType;

    @Column(name = "ville")
    private String ville;

    @Column(name = "balance")
    private Integer balance;

    @Column(name = "masse_salariale")
    private Integer masseSalariale;

    @Column(name = "budget_salaires")
    private Integer budgetSalaires;

    @Column(name = "budget_transferts")
    private Integer budgetTransferts;

    @Column(name = "infrastructures_entrainement")
    private String infrastructuresEntrainement;

    @Column(name = "infrastructures_jeunes")
    private String infrastructuresJeunes;

    @Column(name = "recrutement_jeunes")
    private String recrutementJeunes;

    @Column(name = "nom_stade")
    private String nomStade;

    @Column(name = "capacite_stade")
    private Integer capaciteStade;

    @Column(name = "prevision_media")
    private Integer previsionMedia;

    @Column(name = "indice_continental")
    private Integer indiceContinental;

    @Column(name = "competition_continentale")
    private Boolean competitionContinentale;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "pays" }, allowSetters = true)
    private Championnat championnat;

    @ManyToMany
    @JoinTable(name = "rel_club__jeux", joinColumns = @JoinColumn(name = "club_id"), inverseJoinColumns = @JoinColumn(name = "jeux_id"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "versions", "clubs" }, allowSetters = true)
    private Set<Jeu> jeuxes = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_club__bookmarks",
        joinColumns = @JoinColumn(name = "club_id"),
        inverseJoinColumns = @JoinColumn(name = "bookmarks_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(
        value = { "user", "commentaires", "conversationEnvoies", "conversationRecus", "messages", "bookmarks", "favorises" },
        allowSetters = true
    )
    private Set<Abonne> bookmarks = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Club id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Club nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public byte[] getLogo() {
        return this.logo;
    }

    public Club logo(byte[] logo) {
        this.setLogo(logo);
        return this;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public String getLogoContentType() {
        return this.logoContentType;
    }

    public Club logoContentType(String logoContentType) {
        this.logoContentType = logoContentType;
        return this;
    }

    public void setLogoContentType(String logoContentType) {
        this.logoContentType = logoContentType;
    }

    public String getVille() {
        return this.ville;
    }

    public Club ville(String ville) {
        this.setVille(ville);
        return this;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public Integer getBalance() {
        return this.balance;
    }

    public Club balance(Integer balance) {
        this.setBalance(balance);
        return this;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getMasseSalariale() {
        return this.masseSalariale;
    }

    public Club masseSalariale(Integer masseSalariale) {
        this.setMasseSalariale(masseSalariale);
        return this;
    }

    public void setMasseSalariale(Integer masseSalariale) {
        this.masseSalariale = masseSalariale;
    }

    public Integer getBudgetSalaires() {
        return this.budgetSalaires;
    }

    public Club budgetSalaires(Integer budgetSalaires) {
        this.setBudgetSalaires(budgetSalaires);
        return this;
    }

    public void setBudgetSalaires(Integer budgetSalaires) {
        this.budgetSalaires = budgetSalaires;
    }

    public Integer getBudgetTransferts() {
        return this.budgetTransferts;
    }

    public Club budgetTransferts(Integer budgetTransferts) {
        this.setBudgetTransferts(budgetTransferts);
        return this;
    }

    public void setBudgetTransferts(Integer budgetTransferts) {
        this.budgetTransferts = budgetTransferts;
    }

    public String getInfrastructuresEntrainement() {
        return this.infrastructuresEntrainement;
    }

    public Club infrastructuresEntrainement(String infrastructuresEntrainement) {
        this.setInfrastructuresEntrainement(infrastructuresEntrainement);
        return this;
    }

    public void setInfrastructuresEntrainement(String infrastructuresEntrainement) {
        this.infrastructuresEntrainement = infrastructuresEntrainement;
    }

    public String getInfrastructuresJeunes() {
        return this.infrastructuresJeunes;
    }

    public Club infrastructuresJeunes(String infrastructuresJeunes) {
        this.setInfrastructuresJeunes(infrastructuresJeunes);
        return this;
    }

    public void setInfrastructuresJeunes(String infrastructuresJeunes) {
        this.infrastructuresJeunes = infrastructuresJeunes;
    }

    public String getRecrutementJeunes() {
        return this.recrutementJeunes;
    }

    public Club recrutementJeunes(String recrutementJeunes) {
        this.setRecrutementJeunes(recrutementJeunes);
        return this;
    }

    public void setRecrutementJeunes(String recrutementJeunes) {
        this.recrutementJeunes = recrutementJeunes;
    }

    public String getNomStade() {
        return this.nomStade;
    }

    public Club nomStade(String nomStade) {
        this.setNomStade(nomStade);
        return this;
    }

    public void setNomStade(String nomStade) {
        this.nomStade = nomStade;
    }

    public Integer getCapaciteStade() {
        return this.capaciteStade;
    }

    public Club capaciteStade(Integer capaciteStade) {
        this.setCapaciteStade(capaciteStade);
        return this;
    }

    public void setCapaciteStade(Integer capaciteStade) {
        this.capaciteStade = capaciteStade;
    }

    public Integer getPrevisionMedia() {
        return this.previsionMedia;
    }

    public Club previsionMedia(Integer previsionMedia) {
        this.setPrevisionMedia(previsionMedia);
        return this;
    }

    public void setPrevisionMedia(Integer previsionMedia) {
        this.previsionMedia = previsionMedia;
    }

    public Integer getIndiceContinental() {
        return this.indiceContinental;
    }

    public Club indiceContinental(Integer indiceContinental) {
        this.setIndiceContinental(indiceContinental);
        return this;
    }

    public void setIndiceContinental(Integer indiceContinental) {
        this.indiceContinental = indiceContinental;
    }

    public Boolean getCompetitionContinentale() {
        return this.competitionContinentale;
    }

    public Club competitionContinentale(Boolean competitionContinentale) {
        this.setCompetitionContinentale(competitionContinentale);
        return this;
    }

    public void setCompetitionContinentale(Boolean competitionContinentale) {
        this.competitionContinentale = competitionContinentale;
    }

    public Championnat getChampionnat() {
        return this.championnat;
    }

    public void setChampionnat(Championnat championnat) {
        this.championnat = championnat;
    }

    public Club championnat(Championnat championnat) {
        this.setChampionnat(championnat);
        return this;
    }

    public Set<Jeu> getJeuxes() {
        return this.jeuxes;
    }

    public void setJeuxes(Set<Jeu> jeus) {
        this.jeuxes = jeus;
    }

    public Club jeuxes(Set<Jeu> jeus) {
        this.setJeuxes(jeus);
        return this;
    }

    public Club addJeux(Jeu jeu) {
        this.jeuxes.add(jeu);
        jeu.getClubs().add(this);
        return this;
    }

    public Club removeJeux(Jeu jeu) {
        this.jeuxes.remove(jeu);
        jeu.getClubs().remove(this);
        return this;
    }

    public Set<Abonne> getBookmarks() {
        return this.bookmarks;
    }

    public void setBookmarks(Set<Abonne> abonnes) {
        this.bookmarks = abonnes;
    }

    public Club bookmarks(Set<Abonne> abonnes) {
        this.setBookmarks(abonnes);
        return this;
    }

    public Club addBookmarks(Abonne abonne) {
        this.bookmarks.add(abonne);
        abonne.getBookmarks().add(this);
        return this;
    }

    public Club removeBookmarks(Abonne abonne) {
        this.bookmarks.remove(abonne);
        abonne.getBookmarks().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Club)) {
            return false;
        }
        return id != null && id.equals(((Club) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Club{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", logo='" + getLogo() + "'" +
            ", logoContentType='" + getLogoContentType() + "'" +
            ", ville='" + getVille() + "'" +
            ", balance=" + getBalance() +
            ", masseSalariale=" + getMasseSalariale() +
            ", budgetSalaires=" + getBudgetSalaires() +
            ", budgetTransferts=" + getBudgetTransferts() +
            ", infrastructuresEntrainement='" + getInfrastructuresEntrainement() + "'" +
            ", infrastructuresJeunes='" + getInfrastructuresJeunes() + "'" +
            ", recrutementJeunes='" + getRecrutementJeunes() + "'" +
            ", nomStade='" + getNomStade() + "'" +
            ", capaciteStade=" + getCapaciteStade() +
            ", previsionMedia=" + getPrevisionMedia() +
            ", indiceContinental=" + getIndiceContinental() +
            ", competitionContinentale='" + getCompetitionContinentale() + "'" +
            "}";
    }
}
