import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IJoueur, getJoueurIdentifier } from '../joueur.model';

export type EntityResponseType = HttpResponse<IJoueur>;
export type EntityArrayResponseType = HttpResponse<IJoueur[]>;

@Injectable({ providedIn: 'root' })
export class JoueurService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/joueurs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(joueur: IJoueur): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(joueur);
    return this.http
      .post<IJoueur>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(joueur: IJoueur): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(joueur);
    return this.http
      .put<IJoueur>(`${this.resourceUrl}/${getJoueurIdentifier(joueur) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(joueur: IJoueur): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(joueur);
    return this.http
      .patch<IJoueur>(`${this.resourceUrl}/${getJoueurIdentifier(joueur) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IJoueur>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IJoueur[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addJoueurToCollectionIfMissing(joueurCollection: IJoueur[], ...joueursToCheck: (IJoueur | null | undefined)[]): IJoueur[] {
    const joueurs: IJoueur[] = joueursToCheck.filter(isPresent);
    if (joueurs.length > 0) {
      const joueurCollectionIdentifiers = joueurCollection.map(joueurItem => getJoueurIdentifier(joueurItem)!);
      const joueursToAdd = joueurs.filter(joueurItem => {
        const joueurIdentifier = getJoueurIdentifier(joueurItem);
        if (joueurIdentifier == null || joueurCollectionIdentifiers.includes(joueurIdentifier)) {
          return false;
        }
        joueurCollectionIdentifiers.push(joueurIdentifier);
        return true;
      });
      return [...joueursToAdd, ...joueurCollection];
    }
    return joueurCollection;
  }

  protected convertDateFromClient(joueur: IJoueur): IJoueur {
    return Object.assign({}, joueur, {
      dateNaissance: joueur.dateNaissance?.isValid() ? joueur.dateNaissance.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.dateNaissance = res.body.dateNaissance ? dayjs(res.body.dateNaissance) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((joueur: IJoueur) => {
        joueur.dateNaissance = joueur.dateNaissance ? dayjs(joueur.dateNaissance) : undefined;
      });
    }
    return res;
  }
}
