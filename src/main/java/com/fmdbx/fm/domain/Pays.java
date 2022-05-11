package com.fmdbx.fm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Pays.
 */
@Entity
@Table(name = "pays")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Pays implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "nom")
    private String nom;

    @Lob
    @Column(name = "drapeau")
    private byte[] drapeau;

    @Column(name = "drapeau_content_type")
    private String drapeauContentType;

    @Column(name = "confederation")
    private String confederation;

    @Column(name = "indice_conf")
    private Integer indiceConf;

    @Column(name = "ranking_fifa")
    private Integer rankingFifa;

    @Column(name = "annees_avant_nationalite")
    private Integer anneesAvantNationalite;

    @Column(name = "importance_en_jeu")
    private Integer importanceEnJeu;

    @OneToMany(mappedBy = "pays")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "pays" }, allowSetters = true)
    private Set<Championnat> championnats = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "rel_pays__joueurs",
        joinColumns = @JoinColumn(name = "pays_id"),
        inverseJoinColumns = @JoinColumn(name = "joueurs_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "club", "favorises", "pays" }, allowSetters = true)
    private Set<Joueur> joueurs = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Pays id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Pays nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public byte[] getDrapeau() {
        return this.drapeau;
    }

    public Pays drapeau(byte[] drapeau) {
        this.setDrapeau(drapeau);
        return this;
    }

    public void setDrapeau(byte[] drapeau) {
        this.drapeau = drapeau;
    }

    public String getDrapeauContentType() {
        return this.drapeauContentType;
    }

    public Pays drapeauContentType(String drapeauContentType) {
        this.drapeauContentType = drapeauContentType;
        return this;
    }

    public void setDrapeauContentType(String drapeauContentType) {
        this.drapeauContentType = drapeauContentType;
    }

    public String getConfederation() {
        return this.confederation;
    }

    public Pays confederation(String confederation) {
        this.setConfederation(confederation);
        return this;
    }

    public void setConfederation(String confederation) {
        this.confederation = confederation;
    }

    public Integer getIndiceConf() {
        return this.indiceConf;
    }

    public Pays indiceConf(Integer indiceConf) {
        this.setIndiceConf(indiceConf);
        return this;
    }

    public void setIndiceConf(Integer indiceConf) {
        this.indiceConf = indiceConf;
    }

    public Integer getRankingFifa() {
        return this.rankingFifa;
    }

    public Pays rankingFifa(Integer rankingFifa) {
        this.setRankingFifa(rankingFifa);
        return this;
    }

    public void setRankingFifa(Integer rankingFifa) {
        this.rankingFifa = rankingFifa;
    }

    public Integer getAnneesAvantNationalite() {
        return this.anneesAvantNationalite;
    }

    public Pays anneesAvantNationalite(Integer anneesAvantNationalite) {
        this.setAnneesAvantNationalite(anneesAvantNationalite);
        return this;
    }

    public void setAnneesAvantNationalite(Integer anneesAvantNationalite) {
        this.anneesAvantNationalite = anneesAvantNationalite;
    }

    public Integer getImportanceEnJeu() {
        return this.importanceEnJeu;
    }

    public Pays importanceEnJeu(Integer importanceEnJeu) {
        this.setImportanceEnJeu(importanceEnJeu);
        return this;
    }

    public void setImportanceEnJeu(Integer importanceEnJeu) {
        this.importanceEnJeu = importanceEnJeu;
    }

    public Set<Championnat> getChampionnats() {
        return this.championnats;
    }

    public void setChampionnats(Set<Championnat> championnats) {
        if (this.championnats != null) {
            this.championnats.forEach(i -> i.setPays(null));
        }
        if (championnats != null) {
            championnats.forEach(i -> i.setPays(this));
        }
        this.championnats = championnats;
    }

    public Pays championnats(Set<Championnat> championnats) {
        this.setChampionnats(championnats);
        return this;
    }

    public Pays addChampionnat(Championnat championnat) {
        this.championnats.add(championnat);
        championnat.setPays(this);
        return this;
    }

    public Pays removeChampionnat(Championnat championnat) {
        this.championnats.remove(championnat);
        championnat.setPays(null);
        return this;
    }

    public Set<Joueur> getJoueurs() {
        return this.joueurs;
    }

    public void setJoueurs(Set<Joueur> joueurs) {
        this.joueurs = joueurs;
    }

    public Pays joueurs(Set<Joueur> joueurs) {
        this.setJoueurs(joueurs);
        return this;
    }

    public Pays addJoueurs(Joueur joueur) {
        this.joueurs.add(joueur);
        joueur.getPays().add(this);
        return this;
    }

    public Pays removeJoueurs(Joueur joueur) {
        this.joueurs.remove(joueur);
        joueur.getPays().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pays)) {
            return false;
        }
        return id != null && id.equals(((Pays) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Pays{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", drapeau='" + getDrapeau() + "'" +
            ", drapeauContentType='" + getDrapeauContentType() + "'" +
            ", confederation='" + getConfederation() + "'" +
            ", indiceConf=" + getIndiceConf() +
            ", rankingFifa=" + getRankingFifa() +
            ", anneesAvantNationalite=" + getAnneesAvantNationalite() +
            ", importanceEnJeu=" + getImportanceEnJeu() +
            "}";
    }
}
