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

describe('Club e2e test', () => {
  const clubPageUrl = '/club';
  const clubPageUrlPattern = new RegExp('/club(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const clubSample = { nom: 'customer Games' };

  let club: any;
  //let championnat: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/championnats',
      body: {"nom":"frictionless Computers","nombreDEquipes":26302,"logo":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci5wbmc=","logoContentType":"unknown","niveau":2618,"reputation":65650},
    }).then(({ body }) => {
      championnat = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/clubs+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/clubs').as('postEntityRequest');
    cy.intercept('DELETE', '/api/clubs/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/championnats', {
      statusCode: 200,
      body: [championnat],
    });

    cy.intercept('GET', '/api/jeus', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/abonnes', {
      statusCode: 200,
      body: [],
    });

  });
   */

  afterEach(() => {
    if (club) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/clubs/${club.id}`,
      }).then(() => {
        club = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (championnat) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/championnats/${championnat.id}`,
      }).then(() => {
        championnat = undefined;
      });
    }
  });
   */

  it('Clubs menu should load Clubs page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('club');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Club').should('exist');
    cy.url().should('match', clubPageUrlPattern);
  });

  describe('Club page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(clubPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Club page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/club/new$'));
        cy.getEntityCreateUpdateHeading('Club');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', clubPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/clubs',
          body: {
            ...clubSample,
            championnat: championnat,
          },
        }).then(({ body }) => {
          club = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/clubs+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [club],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(clubPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(clubPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response!.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Club page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('club');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', clubPageUrlPattern);
      });

      it('edit button click should load edit Club page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Club');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', clubPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of Club', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('club').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', clubPageUrlPattern);

        club = undefined;
      });
    });
  });

  describe('new Club page', () => {
    beforeEach(() => {
      cy.visit(`${clubPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Club');
    });

    it.skip('should create an instance of Club', () => {
      cy.get(`[data-cy="nom"]`).type('Analyst Upgradable Designer').should('have.value', 'Analyst Upgradable Designer');

      cy.setFieldImageAsBytesOfEntity('logo', 'integration-test.png', 'image/png');

      cy.get(`[data-cy="ville"]`).type('Bedfordshire').should('have.value', 'Bedfordshire');

      cy.get(`[data-cy="balance"]`).type('57904').should('have.value', '57904');

      cy.get(`[data-cy="masseSalariale"]`).type('34897').should('have.value', '34897');

      cy.get(`[data-cy="budgetSalaires"]`).type('56359').should('have.value', '56359');

      cy.get(`[data-cy="budgetTransferts"]`).type('77264').should('have.value', '77264');

      cy.get(`[data-cy="infrastructuresEntrainement"]`).type('invoice solutions Knolls').should('have.value', 'invoice solutions Knolls');

      cy.get(`[data-cy="infrastructuresJeunes"]`).type('Customer-focused Ball').should('have.value', 'Customer-focused Ball');

      cy.get(`[data-cy="recrutementJeunes"]`).type('payment').should('have.value', 'payment');

      cy.get(`[data-cy="nomStade"]`).type('El Marketing').should('have.value', 'El Marketing');

      cy.get(`[data-cy="capaciteStade"]`).type('42499').should('have.value', '42499');

      cy.get(`[data-cy="previsionMedia"]`).type('76796').should('have.value', '76796');

      cy.get(`[data-cy="indiceContinental"]`).type('7295').should('have.value', '7295');

      cy.get(`[data-cy="competitionContinentale"]`).should('not.be.checked');
      cy.get(`[data-cy="competitionContinentale"]`).click().should('be.checked');

      cy.get(`[data-cy="championnat"]`).select(1);

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        club = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', clubPageUrlPattern);
    });
  });
});
