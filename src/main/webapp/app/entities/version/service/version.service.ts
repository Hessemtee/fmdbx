import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IVersion, getVersionIdentifier } from '../version.model';

export type EntityResponseType = HttpResponse<IVersion>;
export type EntityArrayResponseType = HttpResponse<IVersion[]>;

@Injectable({ providedIn: 'root' })
export class VersionService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/versions');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(version: IVersion): Observable<EntityResponseType> {
    return this.http.post<IVersion>(this.resourceUrl, version, { observe: 'response' });
  }

  update(version: IVersion): Observable<EntityResponseType> {
    return this.http.put<IVersion>(`${this.resourceUrl}/${getVersionIdentifier(version) as number}`, version, { observe: 'response' });
  }

  partialUpdate(version: IVersion): Observable<EntityResponseType> {
    return this.http.patch<IVersion>(`${this.resourceUrl}/${getVersionIdentifier(version) as number}`, version, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IVersion>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IVersion[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addVersionToCollectionIfMissing(versionCollection: IVersion[], ...versionsToCheck: (IVersion | null | undefined)[]): IVersion[] {
    const versions: IVersion[] = versionsToCheck.filter(isPresent);
    if (versions.length > 0) {
      const versionCollectionIdentifiers = versionCollection.map(versionItem => getVersionIdentifier(versionItem)!);
      const versionsToAdd = versions.filter(versionItem => {
        const versionIdentifier = getVersionIdentifier(versionItem);
        if (versionIdentifier == null || versionCollectionIdentifiers.includes(versionIdentifier)) {
          return false;
        }
        versionCollectionIdentifiers.push(versionIdentifier);
        return true;
      });
      return [...versionsToAdd, ...versionCollection];
    }
    return versionCollection;
  }
}
