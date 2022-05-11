package com.fmdbx.fm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Conversation.
 */
@Entity
@Table(name = "conversation")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Conversation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "objet")
    private String objet;

    @OneToMany(mappedBy = "conversation")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "conversation", "redacteur" }, allowSetters = true)
    private Set<Message> messages = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(
        value = { "user", "commentaires", "conversationEnvoies", "conversationRecus", "messages", "bookmarks", "favorises" },
        allowSetters = true
    )
    private Abonne emetteur;

    @ManyToOne
    @JsonIgnoreProperties(
        value = { "user", "commentaires", "conversationEnvoies", "conversationRecus", "messages", "bookmarks", "favorises" },
        allowSetters = true
    )
    private Abonne recepteur;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Conversation id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getObjet() {
        return this.objet;
    }

    public Conversation objet(String objet) {
        this.setObjet(objet);
        return this;
    }

    public void setObjet(String objet) {
        this.objet = objet;
    }

    public Set<Message> getMessages() {
        return this.messages;
    }

    public void setMessages(Set<Message> messages) {
        if (this.messages != null) {
            this.messages.forEach(i -> i.setConversation(null));
        }
        if (messages != null) {
            messages.forEach(i -> i.setConversation(this));
        }
        this.messages = messages;
    }

    public Conversation messages(Set<Message> messages) {
        this.setMessages(messages);
        return this;
    }

    public Conversation addMessage(Message message) {
        this.messages.add(message);
        message.setConversation(this);
        return this;
    }

    public Conversation removeMessage(Message message) {
        this.messages.remove(message);
        message.setConversation(null);
        return this;
    }

    public Abonne getEmetteur() {
        return this.emetteur;
    }

    public void setEmetteur(Abonne abonne) {
        this.emetteur = abonne;
    }

    public Conversation emetteur(Abonne abonne) {
        this.setEmetteur(abonne);
        return this;
    }

    public Abonne getRecepteur() {
        return this.recepteur;
    }

    public void setRecepteur(Abonne abonne) {
        this.recepteur = abonne;
    }

    public Conversation recepteur(Abonne abonne) {
        this.setRecepteur(abonne);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Conversation)) {
            return false;
        }
        return id != null && id.equals(((Conversation) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Conversation{" +
            "id=" + getId() +
            ", objet='" + getObjet() + "'" +
            "}";
    }
}
