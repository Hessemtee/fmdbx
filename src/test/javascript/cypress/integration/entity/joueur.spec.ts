import { entityItemSelector } from '../../support/commands';
import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Joueur e2e test', () => {
  const joueurPageUrl = '/joueur';
  const joueurPageUrlPattern = new RegExp('/joueur(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const joueurSample = {};

  let joueur: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/joueurs+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/joueurs').as('postEntityRequest');
    cy.intercept('DELETE', '/api/joueurs/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (joueur) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/joueurs/${joueur.id}`,
      }).then(() => {
        joueur = undefined;
      });
    }
  });

  it('Joueurs menu should load Joueurs page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('joueur');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Joueur').should('exist');
    cy.url().should('match', joueurPageUrlPattern);
  });

  describe('Joueur page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(joueurPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Joueur page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/joueur/new$'));
        cy.getEntityCreateUpdateHeading('Joueur');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', joueurPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/joueurs',
          body: joueurSample,
        }).then(({ body }) => {
          joueur = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/joueurs+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [joueur],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(joueurPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Joueur page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('joueur');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', joueurPageUrlPattern);
      });

      it('edit button click should load edit Joueur page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Joueur');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', joueurPageUrlPattern);
      });

      it('last delete button click should delete instance of Joueur', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('joueur').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', joueurPageUrlPattern);

        joueur = undefined;
      });
    });
  });

  describe('new Joueur page', () => {
    beforeEach(() => {
      cy.visit(`${joueurPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Joueur');
    });

    it('should create an instance of Joueur', () => {
      cy.get(`[data-cy="nom"]`).type('Track invoice Unit').should('have.value', 'Track invoice Unit');

      cy.get(`[data-cy="prenom"]`).type('solution-oriented').should('have.value', 'solution-oriented');

      cy.get(`[data-cy="photo"]`).type('Right-sized 24/365 Buckinghamshire').should('have.value', 'Right-sized 24/365 Buckinghamshire');

      cy.get(`[data-cy="position"]`).type('Technician Savings').should('have.value', 'Technician Savings');

      cy.get(`[data-cy="dateNaissance"]`).type('2022-05-10').should('have.value', '2022-05-10');

      cy.get(`[data-cy="nombreSelections"]`).type('20261').should('have.value', '20261');

      cy.get(`[data-cy="nombreButsInternationaux"]`).type('94682').should('have.value', '94682');

      cy.get(`[data-cy="valeur"]`).type('20545').should('have.value', '20545');

      cy.get(`[data-cy="salaire"]`).type('36491').should('have.value', '36491');

      cy.get(`[data-cy="coutEstime"]`).type('87517').should('have.value', '87517');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        joueur = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', joueurPageUrlPattern);
    });
  });
});
