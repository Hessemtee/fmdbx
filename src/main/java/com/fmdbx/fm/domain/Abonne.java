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
 * A Abonne.
 */
@Entity
@Table(name = "abonne")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Abonne implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "nom")
    private String nom;

    @Lob
    @Column(name = "avatar")
    private byte[] avatar;

    @Column(name = "avatar_content_type")
    private String avatarContentType;

    @Column(name = "premium")
    private Boolean premium;

    @OneToOne(optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private User user;

    @OneToMany(mappedBy = "abonne")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "joueurConcerne", "clubConcerne", "abonne" }, allowSetters = true)
    private Set<Commentaire> commentaires = new HashSet<>();

    @OneToMany(mappedBy = "emetteur")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "messages", "emetteur", "recepteur" }, allowSetters = true)
    private Set<Conversation> conversationEnvoies = new HashSet<>();

    @OneToMany(mappedBy = "recepteur")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "messages", "emetteur", "recepteur" }, allowSetters = true)
    private Set<Conversation> conversationRecus = new HashSet<>();

    @OneToMany(mappedBy = "redacteur")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "conversation", "redacteur" }, allowSetters = true)
    private Set<Message> messages = new HashSet<>();

    @ManyToMany(mappedBy = "bookmarks")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "championnat", "jeuxes", "bookmarks" }, allowSetters = true)
    private Set<Club> bookmarks = new HashSet<>();

    @ManyToMany(mappedBy = "favorises")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "club", "favorises", "pays" }, allowSetters = true)
    private Set<Joueur> favorises = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Abonne id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Abonne nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public byte[] getAvatar() {
        return this.avatar;
    }

    public Abonne avatar(byte[] avatar) {
        this.setAvatar(avatar);
        return this;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public String getAvatarContentType() {
        return this.avatarContentType;
    }

    public Abonne avatarContentType(String avatarContentType) {
        this.avatarContentType = avatarContentType;
        return this;
    }

    public void setAvatarContentType(String avatarContentType) {
        this.avatarContentType = avatarContentType;
    }

    public Boolean getPremium() {
        return this.premium;
    }

    public Abonne premium(Boolean premium) {
        this.setPremium(premium);
        return this;
    }

    public void setPremium(Boolean premium) {
        this.premium = premium;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Abonne user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Commentaire> getCommentaires() {
        return this.commentaires;
    }

    public void setCommentaires(Set<Commentaire> commentaires) {
        if (this.commentaires != null) {
            this.commentaires.forEach(i -> i.setAbonne(null));
        }
        if (commentaires != null) {
            commentaires.forEach(i -> i.setAbonne(this));
        }
        this.commentaires = commentaires;
    }

    public Abonne commentaires(Set<Commentaire> commentaires) {
        this.setCommentaires(commentaires);
        return this;
    }

    public Abonne addCommentaire(Commentaire commentaire) {
        this.commentaires.add(commentaire);
        commentaire.setAbonne(this);
        return this;
    }

    public Abonne removeCommentaire(Commentaire commentaire) {
        this.commentaires.remove(commentaire);
        commentaire.setAbonne(null);
        return this;
    }

    public Set<Conversation> getConversationEnvoies() {
        return this.conversationEnvoies;
    }

    public void setConversationEnvoies(Set<Conversation> conversations) {
        if (this.conversationEnvoies != null) {
            this.conversationEnvoies.forEach(i -> i.setEmetteur(null));
        }
        if (conversations != null) {
            conversations.forEach(i -> i.setEmetteur(this));
        }
        this.conversationEnvoies = conversations;
    }

    public Abonne conversationEnvoies(Set<Conversation> conversations) {
        this.setConversationEnvoies(conversations);
        return this;
    }

    public Abonne addConversationEnvoie(Conversation conversation) {
        this.conversationEnvoies.add(conversation);
        conversation.setEmetteur(this);
        return this;
    }

    public Abonne removeConversationEnvoie(Conversation conversation) {
        this.conversationEnvoies.remove(conversation);
        conversation.setEmetteur(null);
        return this;
    }

    public Set<Conversation> getConversationRecus() {
        return this.conversationRecus;
    }

    public void setConversationRecus(Set<Conversation> conversations) {
        if (this.conversationRecus != null) {
            this.conversationRecus.forEach(i -> i.setRecepteur(null));
        }
        if (conversations != null) {
            conversations.forEach(i -> i.setRecepteur(this));
        }
        this.conversationRecus = conversations;
    }

    public Abonne conversationRecus(Set<Conversation> conversations) {
        this.setConversationRecus(conversations);
        return this;
    }

    public Abonne addConversationRecu(Conversation conversation) {
        this.conversationRecus.add(conversation);
        conversation.setRecepteur(this);
        return this;
    }

    public Abonne removeConversationRecu(Conversation conversation) {
        this.conversationRecus.remove(conversation);
        conversation.setRecepteur(null);
        return this;
    }

    public Set<Message> getMessages() {
        return this.messages;
    }

    public void setMessages(Set<Message> messages) {
        if (this.messages != null) {
            this.messages.forEach(i -> i.setRedacteur(null));
        }
        if (messages != null) {
            messages.forEach(i -> i.setRedacteur(this));
        }
        this.messages = messages;
    }

    public Abonne messages(Set<Message> messages) {
        this.setMessages(messages);
        return this;
    }

    public Abonne addMessage(Message message) {
        this.messages.add(message);
        message.setRedacteur(this);
        return this;
    }

    public Abonne removeMessage(Message message) {
        this.messages.remove(message);
        message.setRedacteur(null);
        return this;
    }

    public Set<Club> getBookmarks() {
        return this.bookmarks;
    }

    public void setBookmarks(Set<Club> clubs) {
        if (this.bookmarks != null) {
            this.bookmarks.forEach(i -> i.removeBookmarks(this));
        }
        if (clubs != null) {
            clubs.forEach(i -> i.addBookmarks(this));
        }
        this.bookmarks = clubs;
    }

    public Abonne bookmarks(Set<Club> clubs) {
        this.setBookmarks(clubs);
        return this;
    }

    public Abonne addBookmarks(Club club) {
        this.bookmarks.add(club);
        club.getBookmarks().add(this);
        return this;
    }

    public Abonne removeBookmarks(Club club) {
        this.bookmarks.remove(club);
        club.getBookmarks().remove(this);
        return this;
    }

    public Set<Joueur> getFavorises() {
        return this.favorises;
    }

    public void setFavorises(Set<Joueur> joueurs) {
        if (this.favorises != null) {
            this.favorises.forEach(i -> i.removeFavoris(this));
        }
        if (joueurs != null) {
            joueurs.forEach(i -> i.addFavoris(this));
        }
        this.favorises = joueurs;
    }

    public Abonne favorises(Set<Joueur> joueurs) {
        this.setFavorises(joueurs);
        return this;
    }

    public Abonne addFavoris(Joueur joueur) {
        this.favorises.add(joueur);
        joueur.getFavorises().add(this);
        return this;
    }

    public Abonne removeFavoris(Joueur joueur) {
        this.favorises.remove(joueur);
        joueur.getFavorises().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Abonne)) {
            return false;
        }
        return id != null && id.equals(((Abonne) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Abonne{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", avatar='" + getAvatar() + "'" +
            ", avatarContentType='" + getAvatarContentType() + "'" +
            ", premium='" + getPremium() + "'" +
            "}";
    }
}
