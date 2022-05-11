import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IChampionnat, getChampionnatIdentifier } from '../championnat.model';

export type EntityResponseType = HttpResponse<IChampionnat>;
export type EntityArrayResponseType = HttpResponse<IChampionnat[]>;

@Injectable({ providedIn: 'root' })
export class ChampionnatService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/championnats');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(championnat: IChampionnat): Observable<EntityResponseType> {
    return this.http.post<IChampionnat>(this.resourceUrl, championnat, { observe: 'response' });
  }

  update(championnat: IChampionnat): Observable<EntityResponseType> {
    return this.http.put<IChampionnat>(`${this.resourceUrl}/${getChampionnatIdentifier(championnat) as number}`, championnat, {
      observe: 'response',
    });
  }

  partialUpdate(championnat: IChampionnat): Observable<EntityResponseType> {
    return this.http.patch<IChampionnat>(`${this.resourceUrl}/${getChampionnatIdentifier(championnat) as number}`, championnat, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IChampionnat>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IChampionnat[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addChampionnatToCollectionIfMissing(
    championnatCollection: IChampionnat[],
    ...championnatsToCheck: (IChampionnat | null | undefined)[]
  ): IChampionnat[] {
    const championnats: IChampionnat[] = championnatsToCheck.filter(isPresent);
    if (championnats.length > 0) {
      const championnatCollectionIdentifiers = championnatCollection.map(championnatItem => getChampionnatIdentifier(championnatItem)!);
      const championnatsToAdd = championnats.filter(championnatItem => {
        const championnatIdentifier = getChampionnatIdentifier(championnatItem);
        if (championnatIdentifier == null || championnatCollectionIdentifiers.includes(championnatIdentifier)) {
          return false;
        }
        championnatCollectionIdentifiers.push(championnatIdentifier);
        return true;
      });
      return [...championnatsToAdd, ...championnatCollection];
    }
    return championnatCollection;
  }
}
