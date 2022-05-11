import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IJeu, getJeuIdentifier } from '../jeu.model';

export type EntityResponseType = HttpResponse<IJeu>;
export type EntityArrayResponseType = HttpResponse<IJeu[]>;

@Injectable({ providedIn: 'root' })
export class JeuService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/jeus');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(jeu: IJeu): Observable<EntityResponseType> {
    return this.http.post<IJeu>(this.resourceUrl, jeu, { observe: 'response' });
  }

  update(jeu: IJeu): Observable<EntityResponseType> {
    return this.http.put<IJeu>(`${this.resourceUrl}/${getJeuIdentifier(jeu) as number}`, jeu, { observe: 'response' });
  }

  partialUpdate(jeu: IJeu): Observable<EntityResponseType> {
    return this.http.patch<IJeu>(`${this.resourceUrl}/${getJeuIdentifier(jeu) as number}`, jeu, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IJeu>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IJeu[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addJeuToCollectionIfMissing(jeuCollection: IJeu[], ...jeusToCheck: (IJeu | null | undefined)[]): IJeu[] {
    const jeus: IJeu[] = jeusToCheck.filter(isPresent);
    if (jeus.length > 0) {
      const jeuCollectionIdentifiers = jeuCollection.map(jeuItem => getJeuIdentifier(jeuItem)!);
      const jeusToAdd = jeus.filter(jeuItem => {
        const jeuIdentifier = getJeuIdentifier(jeuItem);
        if (jeuIdentifier == null || jeuCollectionIdentifiers.includes(jeuIdentifier)) {
          return false;
        }
        jeuCollectionIdentifiers.push(jeuIdentifier);
        return true;
      });
      return [...jeusToAdd, ...jeuCollection];
    }
    return jeuCollection;
  }
}
