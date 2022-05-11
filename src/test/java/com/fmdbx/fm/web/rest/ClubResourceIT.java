package com.fmdbx.fm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fmdbx.fm.IntegrationTest;
import com.fmdbx.fm.domain.Championnat;
import com.fmdbx.fm.domain.Club;
import com.fmdbx.fm.repository.ClubRepository;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link ClubResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ClubResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final byte[] DEFAULT_LOGO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_LOGO = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_LOGO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_LOGO_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_VILLE = "AAAAAAAAAA";
    private static final String UPDATED_VILLE = "BBBBBBBBBB";

    private static final Integer DEFAULT_BALANCE = 1;
    private static final Integer UPDATED_BALANCE = 2;

    private static final Integer DEFAULT_MASSE_SALARIALE = 1;
    private static final Integer UPDATED_MASSE_SALARIALE = 2;

    private static final Integer DEFAULT_BUDGET_SALAIRES = 1;
    private static final Integer UPDATED_BUDGET_SALAIRES = 2;

    private static final Integer DEFAULT_BUDGET_TRANSFERTS = 1;
    private static final Integer UPDATED_BUDGET_TRANSFERTS = 2;

    private static final String DEFAULT_INFRASTRUCTURES_ENTRAINEMENT = "AAAAAAAAAA";
    private static final String UPDATED_INFRASTRUCTURES_ENTRAINEMENT = "BBBBBBBBBB";

    private static final String DEFAULT_INFRASTRUCTURES_JEUNES = "AAAAAAAAAA";
    private static final String UPDATED_INFRASTRUCTURES_JEUNES = "BBBBBBBBBB";

    private static final String DEFAULT_RECRUTEMENT_JEUNES = "AAAAAAAAAA";
    private static final String UPDATED_RECRUTEMENT_JEUNES = "BBBBBBBBBB";

    private static final String DEFAULT_NOM_STADE = "AAAAAAAAAA";
    private static final String UPDATED_NOM_STADE = "BBBBBBBBBB";

    private static final Integer DEFAULT_CAPACITE_STADE = 1;
    private static final Integer UPDATED_CAPACITE_STADE = 2;

    private static final Integer DEFAULT_PREVISION_MEDIA = 1;
    private static final Integer UPDATED_PREVISION_MEDIA = 2;

    private static final Integer DEFAULT_INDICE_CONTINENTAL = 1;
    private static final Integer UPDATED_INDICE_CONTINENTAL = 2;

    private static final Boolean DEFAULT_COMPETITION_CONTINENTALE = false;
    private static final Boolean UPDATED_COMPETITION_CONTINENTALE = true;

    private static final String ENTITY_API_URL = "/api/clubs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ClubRepository clubRepository;

    @Mock
    private ClubRepository clubRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClubMockMvc;

    private Club club;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Club createEntity(EntityManager em) {
        Club club = new Club()
            .nom(DEFAULT_NOM)
            .logo(DEFAULT_LOGO)
            .logoContentType(DEFAULT_LOGO_CONTENT_TYPE)
            .ville(DEFAULT_VILLE)
            .balance(DEFAULT_BALANCE)
            .masseSalariale(DEFAULT_MASSE_SALARIALE)
            .budgetSalaires(DEFAULT_BUDGET_SALAIRES)
            .budgetTransferts(DEFAULT_BUDGET_TRANSFERTS)
            .infrastructuresEntrainement(DEFAULT_INFRASTRUCTURES_ENTRAINEMENT)
            .infrastructuresJeunes(DEFAULT_INFRASTRUCTURES_JEUNES)
            .recrutementJeunes(DEFAULT_RECRUTEMENT_JEUNES)
            .nomStade(DEFAULT_NOM_STADE)
            .capaciteStade(DEFAULT_CAPACITE_STADE)
            .previsionMedia(DEFAULT_PREVISION_MEDIA)
            .indiceContinental(DEFAULT_INDICE_CONTINENTAL)
            .competitionContinentale(DEFAULT_COMPETITION_CONTINENTALE);
        // Add required entity
        Championnat championnat;
        if (TestUtil.findAll(em, Championnat.class).isEmpty()) {
            championnat = ChampionnatResourceIT.createEntity(em);
            em.persist(championnat);
            em.flush();
        } else {
            championnat = TestUtil.findAll(em, Championnat.class).get(0);
        }
        club.setChampionnat(championnat);
        return club;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Club createUpdatedEntity(EntityManager em) {
        Club club = new Club()
            .nom(UPDATED_NOM)
            .logo(UPDATED_LOGO)
            .logoContentType(UPDATED_LOGO_CONTENT_TYPE)
            .ville(UPDATED_VILLE)
            .balance(UPDATED_BALANCE)
            .masseSalariale(UPDATED_MASSE_SALARIALE)
            .budgetSalaires(UPDATED_BUDGET_SALAIRES)
            .budgetTransferts(UPDATED_BUDGET_TRANSFERTS)
            .infrastructuresEntrainement(UPDATED_INFRASTRUCTURES_ENTRAINEMENT)
            .infrastructuresJeunes(UPDATED_INFRASTRUCTURES_JEUNES)
            .recrutementJeunes(UPDATED_RECRUTEMENT_JEUNES)
            .nomStade(UPDATED_NOM_STADE)
            .capaciteStade(UPDATED_CAPACITE_STADE)
            .previsionMedia(UPDATED_PREVISION_MEDIA)
            .indiceContinental(UPDATED_INDICE_CONTINENTAL)
            .competitionContinentale(UPDATED_COMPETITION_CONTINENTALE);
        // Add required entity
        Championnat championnat;
        if (TestUtil.findAll(em, Championnat.class).isEmpty()) {
            championnat = ChampionnatResourceIT.createUpdatedEntity(em);
            em.persist(championnat);
            em.flush();
        } else {
            championnat = TestUtil.findAll(em, Championnat.class).get(0);
        }
        club.setChampionnat(championnat);
        return club;
    }

    @BeforeEach
    public void initTest() {
        club = createEntity(em);
    }

    @Test
    @Transactional
    void createClub() throws Exception {
        int databaseSizeBeforeCreate = clubRepository.findAll().size();
        // Create the Club
        restClubMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(club)))
            .andExpect(status().isCreated());

        // Validate the Club in the database
        List<Club> clubList = clubRepository.findAll();
        assertThat(clubList).hasSize(databaseSizeBeforeCreate + 1);
        Club testClub = clubList.get(clubList.size() - 1);
        assertThat(testClub.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testClub.getLogo()).isEqualTo(DEFAULT_LOGO);
        assertThat(testClub.getLogoContentType()).isEqualTo(DEFAULT_LOGO_CONTENT_TYPE);
        assertThat(testClub.getVille()).isEqualTo(DEFAULT_VILLE);
        assertThat(testClub.getBalance()).isEqualTo(DEFAULT_BALANCE);
        assertThat(testClub.getMasseSalariale()).isEqualTo(DEFAULT_MASSE_SALARIALE);
        assertThat(testClub.getBudgetSalaires()).isEqualTo(DEFAULT_BUDGET_SALAIRES);
        assertThat(testClub.getBudgetTransferts()).isEqualTo(DEFAULT_BUDGET_TRANSFERTS);
        assertThat(testClub.getInfrastructuresEntrainement()).isEqualTo(DEFAULT_INFRASTRUCTURES_ENTRAINEMENT);
        assertThat(testClub.getInfrastructuresJeunes()).isEqualTo(DEFAULT_INFRASTRUCTURES_JEUNES);
        assertThat(testClub.getRecrutementJeunes()).isEqualTo(DEFAULT_RECRUTEMENT_JEUNES);
        assertThat(testClub.getNomStade()).isEqualTo(DEFAULT_NOM_STADE);
        assertThat(testClub.getCapaciteStade()).isEqualTo(DEFAULT_CAPACITE_STADE);
        assertThat(testClub.getPrevisionMedia()).isEqualTo(DEFAULT_PREVISION_MEDIA);
        assertThat(testClub.getIndiceContinental()).isEqualTo(DEFAULT_INDICE_CONTINENTAL);
        assertThat(testClub.getCompetitionContinentale()).isEqualTo(DEFAULT_COMPETITION_CONTINENTALE);
    }

    @Test
    @Transactional
    void createClubWithExistingId() throws Exception {
        // Create the Club with an existing ID
        club.setId(1L);

        int databaseSizeBeforeCreate = clubRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClubMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(club)))
            .andExpect(status().isBadRequest());

        // Validate the Club in the database
        List<Club> clubList = clubRepository.findAll();
        assertThat(clubList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        int databaseSizeBeforeTest = clubRepository.findAll().size();
        // set the field null
        club.setNom(null);

        // Create the Club, which fails.

        restClubMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(club)))
            .andExpect(status().isBadRequest());

        List<Club> clubList = clubRepository.findAll();
        assertThat(clubList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllClubs() throws Exception {
        // Initialize the database
        clubRepository.saveAndFlush(club);

        // Get all the clubList
        restClubMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(club.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].logoContentType").value(hasItem(DEFAULT_LOGO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].logo").value(hasItem(Base64Utils.encodeToString(DEFAULT_LOGO))))
            .andExpect(jsonPath("$.[*].ville").value(hasItem(DEFAULT_VILLE)))
            .andExpect(jsonPath("$.[*].balance").value(hasItem(DEFAULT_BALANCE)))
            .andExpect(jsonPath("$.[*].masseSalariale").value(hasItem(DEFAULT_MASSE_SALARIALE)))
            .andExpect(jsonPath("$.[*].budgetSalaires").value(hasItem(DEFAULT_BUDGET_SALAIRES)))
            .andExpect(jsonPath("$.[*].budgetTransferts").value(hasItem(DEFAULT_BUDGET_TRANSFERTS)))
            .andExpect(jsonPath("$.[*].infrastructuresEntrainement").value(hasItem(DEFAULT_INFRASTRUCTURES_ENTRAINEMENT)))
            .andExpect(jsonPath("$.[*].infrastructuresJeunes").value(hasItem(DEFAULT_INFRASTRUCTURES_JEUNES)))
            .andExpect(jsonPath("$.[*].recrutementJeunes").value(hasItem(DEFAULT_RECRUTEMENT_JEUNES)))
            .andExpect(jsonPath("$.[*].nomStade").value(hasItem(DEFAULT_NOM_STADE)))
            .andExpect(jsonPath("$.[*].capaciteStade").value(hasItem(DEFAULT_CAPACITE_STADE)))
            .andExpect(jsonPath("$.[*].previsionMedia").value(hasItem(DEFAULT_PREVISION_MEDIA)))
            .andExpect(jsonPath("$.[*].indiceContinental").value(hasItem(DEFAULT_INDICE_CONTINENTAL)))
            .andExpect(jsonPath("$.[*].competitionContinentale").value(hasItem(DEFAULT_COMPETITION_CONTINENTALE.booleanValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllClubsWithEagerRelationshipsIsEnabled() throws Exception {
        when(clubRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restClubMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(clubRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllClubsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(clubRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restClubMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(clubRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getClub() throws Exception {
        // Initialize the database
        clubRepository.saveAndFlush(club);

        // Get the club
        restClubMockMvc
            .perform(get(ENTITY_API_URL_ID, club.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(club.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.logoContentType").value(DEFAULT_LOGO_CONTENT_TYPE))
            .andExpect(jsonPath("$.logo").value(Base64Utils.encodeToString(DEFAULT_LOGO)))
            .andExpect(jsonPath("$.ville").value(DEFAULT_VILLE))
            .andExpect(jsonPath("$.balance").value(DEFAULT_BALANCE))
            .andExpect(jsonPath("$.masseSalariale").value(DEFAULT_MASSE_SALARIALE))
            .andExpect(jsonPath("$.budgetSalaires").value(DEFAULT_BUDGET_SALAIRES))
            .andExpect(jsonPath("$.budgetTransferts").value(DEFAULT_BUDGET_TRANSFERTS))
            .andExpect(jsonPath("$.infrastructuresEntrainement").value(DEFAULT_INFRASTRUCTURES_ENTRAINEMENT))
            .andExpect(jsonPath("$.infrastructuresJeunes").value(DEFAULT_INFRASTRUCTURES_JEUNES))
            .andExpect(jsonPath("$.recrutementJeunes").value(DEFAULT_RECRUTEMENT_JEUNES))
            .andExpect(jsonPath("$.nomStade").value(DEFAULT_NOM_STADE))
            .andExpect(jsonPath("$.capaciteStade").value(DEFAULT_CAPACITE_STADE))
            .andExpect(jsonPath("$.previsionMedia").value(DEFAULT_PREVISION_MEDIA))
            .andExpect(jsonPath("$.indiceContinental").value(DEFAULT_INDICE_CONTINENTAL))
            .andExpect(jsonPath("$.competitionContinentale").value(DEFAULT_COMPETITION_CONTINENTALE.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingClub() throws Exception {
        // Get the club
        restClubMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewClub() throws Exception {
        // Initialize the database
        clubRepository.saveAndFlush(club);

        int databaseSizeBeforeUpdate = clubRepository.findAll().size();

        // Update the club
        Club updatedClub = clubRepository.findById(club.getId()).get();
        // Disconnect from session so that the updates on updatedClub are not directly saved in db
        em.detach(updatedClub);
        updatedClub
            .nom(UPDATED_NOM)
            .logo(UPDATED_LOGO)
            .logoContentType(UPDATED_LOGO_CONTENT_TYPE)
            .ville(UPDATED_VILLE)
            .balance(UPDATED_BALANCE)
            .masseSalariale(UPDATED_MASSE_SALARIALE)
            .budgetSalaires(UPDATED_BUDGET_SALAIRES)
            .budgetTransferts(UPDATED_BUDGET_TRANSFERTS)
            .infrastructuresEntrainement(UPDATED_INFRASTRUCTURES_ENTRAINEMENT)
            .infrastructuresJeunes(UPDATED_INFRASTRUCTURES_JEUNES)
            .recrutementJeunes(UPDATED_RECRUTEMENT_JEUNES)
            .nomStade(UPDATED_NOM_STADE)
            .capaciteStade(UPDATED_CAPACITE_STADE)
            .previsionMedia(UPDATED_PREVISION_MEDIA)
            .indiceContinental(UPDATED_INDICE_CONTINENTAL)
            .competitionContinentale(UPDATED_COMPETITION_CONTINENTALE);

        restClubMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedClub.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedClub))
            )
            .andExpect(status().isOk());

        // Validate the Club in the database
        List<Club> clubList = clubRepository.findAll();
        assertThat(clubList).hasSize(databaseSizeBeforeUpdate);
        Club testClub = clubList.get(clubList.size() - 1);
        assertThat(testClub.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testClub.getLogo()).isEqualTo(UPDATED_LOGO);
        assertThat(testClub.getLogoContentType()).isEqualTo(UPDATED_LOGO_CONTENT_TYPE);
        assertThat(testClub.getVille()).isEqualTo(UPDATED_VILLE);
        assertThat(testClub.getBalance()).isEqualTo(UPDATED_BALANCE);
        assertThat(testClub.getMasseSalariale()).isEqualTo(UPDATED_MASSE_SALARIALE);
        assertThat(testClub.getBudgetSalaires()).isEqualTo(UPDATED_BUDGET_SALAIRES);
        assertThat(testClub.getBudgetTransferts()).isEqualTo(UPDATED_BUDGET_TRANSFERTS);
        assertThat(testClub.getInfrastructuresEntrainement()).isEqualTo(UPDATED_INFRASTRUCTURES_ENTRAINEMENT);
        assertThat(testClub.getInfrastructuresJeunes()).isEqualTo(UPDATED_INFRASTRUCTURES_JEUNES);
        assertThat(testClub.getRecrutementJeunes()).isEqualTo(UPDATED_RECRUTEMENT_JEUNES);
        assertThat(testClub.getNomStade()).isEqualTo(UPDATED_NOM_STADE);
        assertThat(testClub.getCapaciteStade()).isEqualTo(UPDATED_CAPACITE_STADE);
        assertThat(testClub.getPrevisionMedia()).isEqualTo(UPDATED_PREVISION_MEDIA);
        assertThat(testClub.getIndiceContinental()).isEqualTo(UPDATED_INDICE_CONTINENTAL);
        assertThat(testClub.getCompetitionContinentale()).isEqualTo(UPDATED_COMPETITION_CONTINENTALE);
    }

    @Test
    @Transactional
    void putNonExistingClub() throws Exception {
        int databaseSizeBeforeUpdate = clubRepository.findAll().size();
        club.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClubMockMvc
            .perform(
                put(ENTITY_API_URL_ID, club.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(club))
            )
            .andExpect(status().isBadRequest());

        // Validate the Club in the database
        List<Club> clubList = clubRepository.findAll();
        assertThat(clubList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClub() throws Exception {
        int databaseSizeBeforeUpdate = clubRepository.findAll().size();
        club.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClubMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(club))
            )
            .andExpect(status().isBadRequest());

        // Validate the Club in the database
        List<Club> clubList = clubRepository.findAll();
        assertThat(clubList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClub() throws Exception {
        int databaseSizeBeforeUpdate = clubRepository.findAll().size();
        club.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClubMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(club)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Club in the database
        List<Club> clubList = clubRepository.findAll();
        assertThat(clubList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClubWithPatch() throws Exception {
        // Initialize the database
        clubRepository.saveAndFlush(club);

        int databaseSizeBeforeUpdate = clubRepository.findAll().size();

        // Update the club using partial update
        Club partialUpdatedClub = new Club();
        partialUpdatedClub.setId(club.getId());

        partialUpdatedClub
            .logo(UPDATED_LOGO)
            .logoContentType(UPDATED_LOGO_CONTENT_TYPE)
            .balance(UPDATED_BALANCE)
            .masseSalariale(UPDATED_MASSE_SALARIALE)
            .budgetSalaires(UPDATED_BUDGET_SALAIRES)
            .budgetTransferts(UPDATED_BUDGET_TRANSFERTS)
            .infrastructuresJeunes(UPDATED_INFRASTRUCTURES_JEUNES)
            .nomStade(UPDATED_NOM_STADE)
            .indiceContinental(UPDATED_INDICE_CONTINENTAL)
            .competitionContinentale(UPDATED_COMPETITION_CONTINENTALE);

        restClubMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClub.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClub))
            )
            .andExpect(status().isOk());

        // Validate the Club in the database
        List<Club> clubList = clubRepository.findAll();
        assertThat(clubList).hasSize(databaseSizeBeforeUpdate);
        Club testClub = clubList.get(clubList.size() - 1);
        assertThat(testClub.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testClub.getLogo()).isEqualTo(UPDATED_LOGO);
        assertThat(testClub.getLogoContentType()).isEqualTo(UPDATED_LOGO_CONTENT_TYPE);
        assertThat(testClub.getVille()).isEqualTo(DEFAULT_VILLE);
        assertThat(testClub.getBalance()).isEqualTo(UPDATED_BALANCE);
        assertThat(testClub.getMasseSalariale()).isEqualTo(UPDATED_MASSE_SALARIALE);
        assertThat(testClub.getBudgetSalaires()).isEqualTo(UPDATED_BUDGET_SALAIRES);
        assertThat(testClub.getBudgetTransferts()).isEqualTo(UPDATED_BUDGET_TRANSFERTS);
        assertThat(testClub.getInfrastructuresEntrainement()).isEqualTo(DEFAULT_INFRASTRUCTURES_ENTRAINEMENT);
        assertThat(testClub.getInfrastructuresJeunes()).isEqualTo(UPDATED_INFRASTRUCTURES_JEUNES);
        assertThat(testClub.getRecrutementJeunes()).isEqualTo(DEFAULT_RECRUTEMENT_JEUNES);
        assertThat(testClub.getNomStade()).isEqualTo(UPDATED_NOM_STADE);
        assertThat(testClub.getCapaciteStade()).isEqualTo(DEFAULT_CAPACITE_STADE);
        assertThat(testClub.getPrevisionMedia()).isEqualTo(DEFAULT_PREVISION_MEDIA);
        assertThat(testClub.getIndiceContinental()).isEqualTo(UPDATED_INDICE_CONTINENTAL);
        assertThat(testClub.getCompetitionContinentale()).isEqualTo(UPDATED_COMPETITION_CONTINENTALE);
    }

    @Test
    @Transactional
    void fullUpdateClubWithPatch() throws Exception {
        // Initialize the database
        clubRepository.saveAndFlush(club);

        int databaseSizeBeforeUpdate = clubRepository.findAll().size();

        // Update the club using partial update
        Club partialUpdatedClub = new Club();
        partialUpdatedClub.setId(club.getId());

        partialUpdatedClub
            .nom(UPDATED_NOM)
            .logo(UPDATED_LOGO)
            .logoContentType(UPDATED_LOGO_CONTENT_TYPE)
            .ville(UPDATED_VILLE)
            .balance(UPDATED_BALANCE)
            .masseSalariale(UPDATED_MASSE_SALARIALE)
            .budgetSalaires(UPDATED_BUDGET_SALAIRES)
            .budgetTransferts(UPDATED_BUDGET_TRANSFERTS)
            .infrastructuresEntrainement(UPDATED_INFRASTRUCTURES_ENTRAINEMENT)
            .infrastructuresJeunes(UPDATED_INFRASTRUCTURES_JEUNES)
            .recrutementJeunes(UPDATED_RECRUTEMENT_JEUNES)
            .nomStade(UPDATED_NOM_STADE)
            .capaciteStade(UPDATED_CAPACITE_STADE)
            .previsionMedia(UPDATED_PREVISION_MEDIA)
            .indiceContinental(UPDATED_INDICE_CONTINENTAL)
            .competitionContinentale(UPDATED_COMPETITION_CONTINENTALE);

        restClubMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClub.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClub))
            )
            .andExpect(status().isOk());

        // Validate the Club in the database
        List<Club> clubList = clubRepository.findAll();
        assertThat(clubList).hasSize(databaseSizeBeforeUpdate);
        Club testClub = clubList.get(clubList.size() - 1);
        assertThat(testClub.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testClub.getLogo()).isEqualTo(UPDATED_LOGO);
        assertThat(testClub.getLogoContentType()).isEqualTo(UPDATED_LOGO_CONTENT_TYPE);
        assertThat(testClub.getVille()).isEqualTo(UPDATED_VILLE);
        assertThat(testClub.getBalance()).isEqualTo(UPDATED_BALANCE);
        assertThat(testClub.getMasseSalariale()).isEqualTo(UPDATED_MASSE_SALARIALE);
        assertThat(testClub.getBudgetSalaires()).isEqualTo(UPDATED_BUDGET_SALAIRES);
        assertThat(testClub.getBudgetTransferts()).isEqualTo(UPDATED_BUDGET_TRANSFERTS);
        assertThat(testClub.getInfrastructuresEntrainement()).isEqualTo(UPDATED_INFRASTRUCTURES_ENTRAINEMENT);
        assertThat(testClub.getInfrastructuresJeunes()).isEqualTo(UPDATED_INFRASTRUCTURES_JEUNES);
        assertThat(testClub.getRecrutementJeunes()).isEqualTo(UPDATED_RECRUTEMENT_JEUNES);
        assertThat(testClub.getNomStade()).isEqualTo(UPDATED_NOM_STADE);
        assertThat(testClub.getCapaciteStade()).isEqualTo(UPDATED_CAPACITE_STADE);
        assertThat(testClub.getPrevisionMedia()).isEqualTo(UPDATED_PREVISION_MEDIA);
        assertThat(testClub.getIndiceContinental()).isEqualTo(UPDATED_INDICE_CONTINENTAL);
        assertThat(testClub.getCompetitionContinentale()).isEqualTo(UPDATED_COMPETITION_CONTINENTALE);
    }

    @Test
    @Transactional
    void patchNonExistingClub() throws Exception {
        int databaseSizeBeforeUpdate = clubRepository.findAll().size();
        club.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClubMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, club.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(club))
            )
            .andExpect(status().isBadRequest());

        // Validate the Club in the database
        List<Club> clubList = clubRepository.findAll();
        assertThat(clubList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClub() throws Exception {
        int databaseSizeBeforeUpdate = clubRepository.findAll().size();
        club.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClubMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(club))
            )
            .andExpect(status().isBadRequest());

        // Validate the Club in the database
        List<Club> clubList = clubRepository.findAll();
        assertThat(clubList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClub() throws Exception {
        int databaseSizeBeforeUpdate = clubRepository.findAll().size();
        club.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClubMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(club)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Club in the database
        List<Club> clubList = clubRepository.findAll();
        assertThat(clubList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClub() throws Exception {
        // Initialize the database
        clubRepository.saveAndFlush(club);

        int databaseSizeBeforeDelete = clubRepository.findAll().size();

        // Delete the club
        restClubMockMvc
            .perform(delete(ENTITY_API_URL_ID, club.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Club> clubList = clubRepository.findAll();
        assertThat(clubList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
