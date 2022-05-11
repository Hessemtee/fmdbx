package com.fmdbx.fm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fmdbx.fm.IntegrationTest;
import com.fmdbx.fm.domain.Joueur;
import com.fmdbx.fm.repository.JoueurRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link JoueurResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class JoueurResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_PRENOM = "AAAAAAAAAA";
    private static final String UPDATED_PRENOM = "BBBBBBBBBB";

    private static final String DEFAULT_PHOTO = "AAAAAAAAAA";
    private static final String UPDATED_PHOTO = "BBBBBBBBBB";

    private static final String DEFAULT_POSITION = "AAAAAAAAAA";
    private static final String UPDATED_POSITION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_NAISSANCE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_NAISSANCE = LocalDate.now(ZoneId.systemDefault());

    private static final Integer DEFAULT_NOMBRE_SELECTIONS = 1;
    private static final Integer UPDATED_NOMBRE_SELECTIONS = 2;

    private static final Integer DEFAULT_NOMBRE_BUTS_INTERNATIONAUX = 1;
    private static final Integer UPDATED_NOMBRE_BUTS_INTERNATIONAUX = 2;

    private static final Integer DEFAULT_VALEUR = 1;
    private static final Integer UPDATED_VALEUR = 2;

    private static final Integer DEFAULT_SALAIRE = 1;
    private static final Integer UPDATED_SALAIRE = 2;

    private static final Integer DEFAULT_COUT_ESTIME = 1;
    private static final Integer UPDATED_COUT_ESTIME = 2;

    private static final String ENTITY_API_URL = "/api/joueurs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private JoueurRepository joueurRepository;

    @Mock
    private JoueurRepository joueurRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restJoueurMockMvc;

    private Joueur joueur;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Joueur createEntity(EntityManager em) {
        Joueur joueur = new Joueur()
            .nom(DEFAULT_NOM)
            .prenom(DEFAULT_PRENOM)
            .photo(DEFAULT_PHOTO)
            .position(DEFAULT_POSITION)
            .dateNaissance(DEFAULT_DATE_NAISSANCE)
            .nombreSelections(DEFAULT_NOMBRE_SELECTIONS)
            .nombreButsInternationaux(DEFAULT_NOMBRE_BUTS_INTERNATIONAUX)
            .valeur(DEFAULT_VALEUR)
            .salaire(DEFAULT_SALAIRE)
            .coutEstime(DEFAULT_COUT_ESTIME);
        return joueur;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Joueur createUpdatedEntity(EntityManager em) {
        Joueur joueur = new Joueur()
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .photo(UPDATED_PHOTO)
            .position(UPDATED_POSITION)
            .dateNaissance(UPDATED_DATE_NAISSANCE)
            .nombreSelections(UPDATED_NOMBRE_SELECTIONS)
            .nombreButsInternationaux(UPDATED_NOMBRE_BUTS_INTERNATIONAUX)
            .valeur(UPDATED_VALEUR)
            .salaire(UPDATED_SALAIRE)
            .coutEstime(UPDATED_COUT_ESTIME);
        return joueur;
    }

    @BeforeEach
    public void initTest() {
        joueur = createEntity(em);
    }

    @Test
    @Transactional
    void createJoueur() throws Exception {
        int databaseSizeBeforeCreate = joueurRepository.findAll().size();
        // Create the Joueur
        restJoueurMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(joueur)))
            .andExpect(status().isCreated());

        // Validate the Joueur in the database
        List<Joueur> joueurList = joueurRepository.findAll();
        assertThat(joueurList).hasSize(databaseSizeBeforeCreate + 1);
        Joueur testJoueur = joueurList.get(joueurList.size() - 1);
        assertThat(testJoueur.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testJoueur.getPrenom()).isEqualTo(DEFAULT_PRENOM);
        assertThat(testJoueur.getPhoto()).isEqualTo(DEFAULT_PHOTO);
        assertThat(testJoueur.getPosition()).isEqualTo(DEFAULT_POSITION);
        assertThat(testJoueur.getDateNaissance()).isEqualTo(DEFAULT_DATE_NAISSANCE);
        assertThat(testJoueur.getNombreSelections()).isEqualTo(DEFAULT_NOMBRE_SELECTIONS);
        assertThat(testJoueur.getNombreButsInternationaux()).isEqualTo(DEFAULT_NOMBRE_BUTS_INTERNATIONAUX);
        assertThat(testJoueur.getValeur()).isEqualTo(DEFAULT_VALEUR);
        assertThat(testJoueur.getSalaire()).isEqualTo(DEFAULT_SALAIRE);
        assertThat(testJoueur.getCoutEstime()).isEqualTo(DEFAULT_COUT_ESTIME);
    }

    @Test
    @Transactional
    void createJoueurWithExistingId() throws Exception {
        // Create the Joueur with an existing ID
        joueur.setId(1L);

        int databaseSizeBeforeCreate = joueurRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restJoueurMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(joueur)))
            .andExpect(status().isBadRequest());

        // Validate the Joueur in the database
        List<Joueur> joueurList = joueurRepository.findAll();
        assertThat(joueurList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllJoueurs() throws Exception {
        // Initialize the database
        joueurRepository.saveAndFlush(joueur);

        // Get all the joueurList
        restJoueurMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(joueur.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM)))
            .andExpect(jsonPath("$.[*].photo").value(hasItem(DEFAULT_PHOTO)))
            .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION)))
            .andExpect(jsonPath("$.[*].dateNaissance").value(hasItem(DEFAULT_DATE_NAISSANCE.toString())))
            .andExpect(jsonPath("$.[*].nombreSelections").value(hasItem(DEFAULT_NOMBRE_SELECTIONS)))
            .andExpect(jsonPath("$.[*].nombreButsInternationaux").value(hasItem(DEFAULT_NOMBRE_BUTS_INTERNATIONAUX)))
            .andExpect(jsonPath("$.[*].valeur").value(hasItem(DEFAULT_VALEUR)))
            .andExpect(jsonPath("$.[*].salaire").value(hasItem(DEFAULT_SALAIRE)))
            .andExpect(jsonPath("$.[*].coutEstime").value(hasItem(DEFAULT_COUT_ESTIME)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllJoueursWithEagerRelationshipsIsEnabled() throws Exception {
        when(joueurRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restJoueurMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(joueurRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllJoueursWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(joueurRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restJoueurMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(joueurRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getJoueur() throws Exception {
        // Initialize the database
        joueurRepository.saveAndFlush(joueur);

        // Get the joueur
        restJoueurMockMvc
            .perform(get(ENTITY_API_URL_ID, joueur.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(joueur.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.prenom").value(DEFAULT_PRENOM))
            .andExpect(jsonPath("$.photo").value(DEFAULT_PHOTO))
            .andExpect(jsonPath("$.position").value(DEFAULT_POSITION))
            .andExpect(jsonPath("$.dateNaissance").value(DEFAULT_DATE_NAISSANCE.toString()))
            .andExpect(jsonPath("$.nombreSelections").value(DEFAULT_NOMBRE_SELECTIONS))
            .andExpect(jsonPath("$.nombreButsInternationaux").value(DEFAULT_NOMBRE_BUTS_INTERNATIONAUX))
            .andExpect(jsonPath("$.valeur").value(DEFAULT_VALEUR))
            .andExpect(jsonPath("$.salaire").value(DEFAULT_SALAIRE))
            .andExpect(jsonPath("$.coutEstime").value(DEFAULT_COUT_ESTIME));
    }

    @Test
    @Transactional
    void getNonExistingJoueur() throws Exception {
        // Get the joueur
        restJoueurMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewJoueur() throws Exception {
        // Initialize the database
        joueurRepository.saveAndFlush(joueur);

        int databaseSizeBeforeUpdate = joueurRepository.findAll().size();

        // Update the joueur
        Joueur updatedJoueur = joueurRepository.findById(joueur.getId()).get();
        // Disconnect from session so that the updates on updatedJoueur are not directly saved in db
        em.detach(updatedJoueur);
        updatedJoueur
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .photo(UPDATED_PHOTO)
            .position(UPDATED_POSITION)
            .dateNaissance(UPDATED_DATE_NAISSANCE)
            .nombreSelections(UPDATED_NOMBRE_SELECTIONS)
            .nombreButsInternationaux(UPDATED_NOMBRE_BUTS_INTERNATIONAUX)
            .valeur(UPDATED_VALEUR)
            .salaire(UPDATED_SALAIRE)
            .coutEstime(UPDATED_COUT_ESTIME);

        restJoueurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedJoueur.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedJoueur))
            )
            .andExpect(status().isOk());

        // Validate the Joueur in the database
        List<Joueur> joueurList = joueurRepository.findAll();
        assertThat(joueurList).hasSize(databaseSizeBeforeUpdate);
        Joueur testJoueur = joueurList.get(joueurList.size() - 1);
        assertThat(testJoueur.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testJoueur.getPrenom()).isEqualTo(UPDATED_PRENOM);
        assertThat(testJoueur.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testJoueur.getPosition()).isEqualTo(UPDATED_POSITION);
        assertThat(testJoueur.getDateNaissance()).isEqualTo(UPDATED_DATE_NAISSANCE);
        assertThat(testJoueur.getNombreSelections()).isEqualTo(UPDATED_NOMBRE_SELECTIONS);
        assertThat(testJoueur.getNombreButsInternationaux()).isEqualTo(UPDATED_NOMBRE_BUTS_INTERNATIONAUX);
        assertThat(testJoueur.getValeur()).isEqualTo(UPDATED_VALEUR);
        assertThat(testJoueur.getSalaire()).isEqualTo(UPDATED_SALAIRE);
        assertThat(testJoueur.getCoutEstime()).isEqualTo(UPDATED_COUT_ESTIME);
    }

    @Test
    @Transactional
    void putNonExistingJoueur() throws Exception {
        int databaseSizeBeforeUpdate = joueurRepository.findAll().size();
        joueur.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJoueurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, joueur.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(joueur))
            )
            .andExpect(status().isBadRequest());

        // Validate the Joueur in the database
        List<Joueur> joueurList = joueurRepository.findAll();
        assertThat(joueurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchJoueur() throws Exception {
        int databaseSizeBeforeUpdate = joueurRepository.findAll().size();
        joueur.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJoueurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(joueur))
            )
            .andExpect(status().isBadRequest());

        // Validate the Joueur in the database
        List<Joueur> joueurList = joueurRepository.findAll();
        assertThat(joueurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamJoueur() throws Exception {
        int databaseSizeBeforeUpdate = joueurRepository.findAll().size();
        joueur.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJoueurMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(joueur)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Joueur in the database
        List<Joueur> joueurList = joueurRepository.findAll();
        assertThat(joueurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateJoueurWithPatch() throws Exception {
        // Initialize the database
        joueurRepository.saveAndFlush(joueur);

        int databaseSizeBeforeUpdate = joueurRepository.findAll().size();

        // Update the joueur using partial update
        Joueur partialUpdatedJoueur = new Joueur();
        partialUpdatedJoueur.setId(joueur.getId());

        partialUpdatedJoueur
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .position(UPDATED_POSITION)
            .nombreButsInternationaux(UPDATED_NOMBRE_BUTS_INTERNATIONAUX)
            .valeur(UPDATED_VALEUR)
            .salaire(UPDATED_SALAIRE);

        restJoueurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJoueur.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedJoueur))
            )
            .andExpect(status().isOk());

        // Validate the Joueur in the database
        List<Joueur> joueurList = joueurRepository.findAll();
        assertThat(joueurList).hasSize(databaseSizeBeforeUpdate);
        Joueur testJoueur = joueurList.get(joueurList.size() - 1);
        assertThat(testJoueur.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testJoueur.getPrenom()).isEqualTo(UPDATED_PRENOM);
        assertThat(testJoueur.getPhoto()).isEqualTo(DEFAULT_PHOTO);
        assertThat(testJoueur.getPosition()).isEqualTo(UPDATED_POSITION);
        assertThat(testJoueur.getDateNaissance()).isEqualTo(DEFAULT_DATE_NAISSANCE);
        assertThat(testJoueur.getNombreSelections()).isEqualTo(DEFAULT_NOMBRE_SELECTIONS);
        assertThat(testJoueur.getNombreButsInternationaux()).isEqualTo(UPDATED_NOMBRE_BUTS_INTERNATIONAUX);
        assertThat(testJoueur.getValeur()).isEqualTo(UPDATED_VALEUR);
        assertThat(testJoueur.getSalaire()).isEqualTo(UPDATED_SALAIRE);
        assertThat(testJoueur.getCoutEstime()).isEqualTo(DEFAULT_COUT_ESTIME);
    }

    @Test
    @Transactional
    void fullUpdateJoueurWithPatch() throws Exception {
        // Initialize the database
        joueurRepository.saveAndFlush(joueur);

        int databaseSizeBeforeUpdate = joueurRepository.findAll().size();

        // Update the joueur using partial update
        Joueur partialUpdatedJoueur = new Joueur();
        partialUpdatedJoueur.setId(joueur.getId());

        partialUpdatedJoueur
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .photo(UPDATED_PHOTO)
            .position(UPDATED_POSITION)
            .dateNaissance(UPDATED_DATE_NAISSANCE)
            .nombreSelections(UPDATED_NOMBRE_SELECTIONS)
            .nombreButsInternationaux(UPDATED_NOMBRE_BUTS_INTERNATIONAUX)
            .valeur(UPDATED_VALEUR)
            .salaire(UPDATED_SALAIRE)
            .coutEstime(UPDATED_COUT_ESTIME);

        restJoueurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJoueur.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedJoueur))
            )
            .andExpect(status().isOk());

        // Validate the Joueur in the database
        List<Joueur> joueurList = joueurRepository.findAll();
        assertThat(joueurList).hasSize(databaseSizeBeforeUpdate);
        Joueur testJoueur = joueurList.get(joueurList.size() - 1);
        assertThat(testJoueur.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testJoueur.getPrenom()).isEqualTo(UPDATED_PRENOM);
        assertThat(testJoueur.getPhoto()).isEqualTo(UPDATED_PHOTO);
        assertThat(testJoueur.getPosition()).isEqualTo(UPDATED_POSITION);
        assertThat(testJoueur.getDateNaissance()).isEqualTo(UPDATED_DATE_NAISSANCE);
        assertThat(testJoueur.getNombreSelections()).isEqualTo(UPDATED_NOMBRE_SELECTIONS);
        assertThat(testJoueur.getNombreButsInternationaux()).isEqualTo(UPDATED_NOMBRE_BUTS_INTERNATIONAUX);
        assertThat(testJoueur.getValeur()).isEqualTo(UPDATED_VALEUR);
        assertThat(testJoueur.getSalaire()).isEqualTo(UPDATED_SALAIRE);
        assertThat(testJoueur.getCoutEstime()).isEqualTo(UPDATED_COUT_ESTIME);
    }

    @Test
    @Transactional
    void patchNonExistingJoueur() throws Exception {
        int databaseSizeBeforeUpdate = joueurRepository.findAll().size();
        joueur.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJoueurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, joueur.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(joueur))
            )
            .andExpect(status().isBadRequest());

        // Validate the Joueur in the database
        List<Joueur> joueurList = joueurRepository.findAll();
        assertThat(joueurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchJoueur() throws Exception {
        int databaseSizeBeforeUpdate = joueurRepository.findAll().size();
        joueur.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJoueurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(joueur))
            )
            .andExpect(status().isBadRequest());

        // Validate the Joueur in the database
        List<Joueur> joueurList = joueurRepository.findAll();
        assertThat(joueurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamJoueur() throws Exception {
        int databaseSizeBeforeUpdate = joueurRepository.findAll().size();
        joueur.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJoueurMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(joueur)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Joueur in the database
        List<Joueur> joueurList = joueurRepository.findAll();
        assertThat(joueurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteJoueur() throws Exception {
        // Initialize the database
        joueurRepository.saveAndFlush(joueur);

        int databaseSizeBeforeDelete = joueurRepository.findAll().size();

        // Delete the joueur
        restJoueurMockMvc
            .perform(delete(ENTITY_API_URL_ID, joueur.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Joueur> joueurList = joueurRepository.findAll();
        assertThat(joueurList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
