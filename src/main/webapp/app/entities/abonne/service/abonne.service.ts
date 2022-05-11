import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAbonne, getAbonneIdentifier } from '../abonne.model';

export type EntityResponseType = HttpResponse<IAbonne>;
export type EntityArrayResponseType = HttpResponse<IAbonne[]>;

@Injectable({ providedIn: 'root' })
export class AbonneService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/abonnes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(abonne: IAbonne): Observable<EntityResponseType> {
    return this.http.post<IAbonne>(this.resourceUrl, abonne, { observe: 'response' });
  }

  update(abonne: IAbonne): Observable<EntityResponseType> {
    return this.http.put<IAbonne>(`${this.resourceUrl}/${getAbonneIdentifier(abonne) as number}`, abonne, { observe: 'response' });
  }

  partialUpdate(abonne: IAbonne): Observable<EntityResponseType> {
    return this.http.patch<IAbonne>(`${this.resourceUrl}/${getAbonneIdentifier(abonne) as number}`, abonne, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IAbonne>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAbonne[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addAbonneToCollectionIfMissing(abonneCollection: IAbonne[], ...abonnesToCheck: (IAbonne | null | undefined)[]): IAbonne[] {
    const abonnes: IAbonne[] = abonnesToCheck.filter(isPresent);
    if (abonnes.length > 0) {
      const abonneCollectionIdentifiers = abonneCollection.map(abonneItem => getAbonneIdentifier(abonneItem)!);
      const abonnesToAdd = abonnes.filter(abonneItem => {
        const abonneIdentifier = getAbonneIdentifier(abonneItem);
        if (abonneIdentifier == null || abonneCollectionIdentifiers.includes(abonneIdentifier)) {
          return false;
        }
        abonneCollectionIdentifiers.push(abonneIdentifier);
        return true;
      });
      return [...abonnesToAdd, ...abonneCollection];
    }
    return abonneCollection;
  }
}
