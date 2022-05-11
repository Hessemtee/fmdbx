package com.fmdbx.fm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Message.
 */
@Entity
@Table(name = "message")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "contenu")
    private String contenu;

    @Column(name = "date")
    private LocalDate date;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "messages", "emetteur", "recepteur" }, allowSetters = true)
    private Conversation conversation;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "user", "commentaires", "conversationEnvoies", "conversationRecus", "messages", "bookmarks", "favorises" },
        allowSetters = true
    )
    private Abonne redacteur;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Message id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContenu() {
        return this.contenu;
    }

    public Message contenu(String contenu) {
        this.setContenu(contenu);
        return this;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public Message date(LocalDate date) {
        this.setDate(date);
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Conversation getConversation() {
        return this.conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public Message conversation(Conversation conversation) {
        this.setConversation(conversation);
        return this;
    }

    public Abonne getRedacteur() {
        return this.redacteur;
    }

    public void setRedacteur(Abonne abonne) {
        this.redacteur = abonne;
    }

    public Message redacteur(Abonne abonne) {
        this.setRedacteur(abonne);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Message)) {
            return false;
        }
        return id != null && id.equals(((Message) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Message{" +
            "id=" + getId() +
            ", contenu='" + getContenu() + "'" +
            ", date='" + getDate() + "'" +
            "}";
    }
}
