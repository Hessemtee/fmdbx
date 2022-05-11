import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IClub, getClubIdentifier } from '../club.model';

export type EntityResponseType = HttpResponse<IClub>;
export type EntityArrayResponseType = HttpResponse<IClub[]>;

@Injectable({ providedIn: 'root' })
export class ClubService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/clubs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(club: IClub): Observable<EntityResponseType> {
    return this.http.post<IClub>(this.resourceUrl, club, { observe: 'response' });
  }

  update(club: IClub): Observable<EntityResponseType> {
    return this.http.put<IClub>(`${this.resourceUrl}/${getClubIdentifier(club) as number}`, club, { observe: 'response' });
  }

  partialUpdate(club: IClub): Observable<EntityResponseType> {
    return this.http.patch<IClub>(`${this.resourceUrl}/${getClubIdentifier(club) as number}`, club, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IClub>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IClub[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addClubToCollectionIfMissing(clubCollection: IClub[], ...clubsToCheck: (IClub | null | undefined)[]): IClub[] {
    const clubs: IClub[] = clubsToCheck.filter(isPresent);
    if (clubs.length > 0) {
      const clubCollectionIdentifiers = clubCollection.map(clubItem => getClubIdentifier(clubItem)!);
      const clubsToAdd = clubs.filter(clubItem => {
        const clubIdentifier = getClubIdentifier(clubItem);
        if (clubIdentifier == null || clubCollectionIdentifiers.includes(clubIdentifier)) {
          return false;
        }
        clubCollectionIdentifiers.push(clubIdentifier);
        return true;
      });
      return [...clubsToAdd, ...clubCollection];
    }
    return clubCollection;
  }
}
