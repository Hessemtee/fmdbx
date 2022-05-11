import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ICommentaire, getCommentaireIdentifier } from '../commentaire.model';

export type EntityResponseType = HttpResponse<ICommentaire>;
export type EntityArrayResponseType = HttpResponse<ICommentaire[]>;

@Injectable({ providedIn: 'root' })
export class CommentaireService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/commentaires');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(commentaire: ICommentaire): Observable<EntityResponseType> {
    return this.http.post<ICommentaire>(this.resourceUrl, commentaire, { observe: 'response' });
  }

  update(commentaire: ICommentaire): Observable<EntityResponseType> {
    return this.http.put<ICommentaire>(`${this.resourceUrl}/${getCommentaireIdentifier(commentaire) as number}`, commentaire, {
      observe: 'response',
    });
  }

  partialUpdate(commentaire: ICommentaire): Observable<EntityResponseType> {
    return this.http.patch<ICommentaire>(`${this.resourceUrl}/${getCommentaireIdentifier(commentaire) as number}`, commentaire, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ICommentaire>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ICommentaire[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addCommentaireToCollectionIfMissing(
    commentaireCollection: ICommentaire[],
    ...commentairesToCheck: (ICommentaire | null | undefined)[]
  ): ICommentaire[] {
    const commentaires: ICommentaire[] = commentairesToCheck.filter(isPresent);
    if (commentaires.length > 0) {
      const commentaireCollectionIdentifiers = commentaireCollection.map(commentaireItem => getCommentaireIdentifier(commentaireItem)!);
      const commentairesToAdd = commentaires.filter(commentaireItem => {
        const commentaireIdentifier = getCommentaireIdentifier(commentaireItem);
        if (commentaireIdentifier == null || commentaireCollectionIdentifiers.includes(commentaireIdentifier)) {
          return false;
        }
        commentaireCollectionIdentifiers.push(commentaireIdentifier);
        return true;
      });
      return [...commentairesToAdd, ...commentaireCollection];
    }
    return commentaireCollection;
  }
}
