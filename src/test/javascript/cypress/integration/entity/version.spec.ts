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

describe('Version e2e test', () => {
  const versionPageUrl = '/version';
  const versionPageUrlPattern = new RegExp('/version(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const versionSample = { version: 'hacking' };

  let version: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/versions+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/versions').as('postEntityRequest');
    cy.intercept('DELETE', '/api/versions/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (version) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/versions/${version.id}`,
      }).then(() => {
        version = undefined;
      });
    }
  });

  it('Versions menu should load Versions page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('version');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Version').should('exist');
    cy.url().should('match', versionPageUrlPattern);
  });

  describe('Version page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(versionPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Version page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/version/new$'));
        cy.getEntityCreateUpdateHeading('Version');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', versionPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/versions',
          body: versionSample,
        }).then(({ body }) => {
          version = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/versions+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [version],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(versionPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Version page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('version');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', versionPageUrlPattern);
      });

      it('edit button click should load edit Version page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Version');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', versionPageUrlPattern);
      });

      it('last delete button click should delete instance of Version', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('version').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', versionPageUrlPattern);

        version = undefined;
      });
    });
  });

  describe('new Version page', () => {
    beforeEach(() => {
      cy.visit(`${versionPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Version');
    });

    it('should create an instance of Version', () => {
      cy.get(`[data-cy="version"]`).type('California Rustic District').should('have.value', 'California Rustic District');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        version = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', versionPageUrlPattern);
    });
  });
});
