import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IConversation, getConversationIdentifier } from '../conversation.model';

export type EntityResponseType = HttpResponse<IConversation>;
export type EntityArrayResponseType = HttpResponse<IConversation[]>;

@Injectable({ providedIn: 'root' })
export class ConversationService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/conversations');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(conversation: IConversation): Observable<EntityResponseType> {
    return this.http.post<IConversation>(this.resourceUrl, conversation, { observe: 'response' });
  }

  update(conversation: IConversation): Observable<EntityResponseType> {
    return this.http.put<IConversation>(`${this.resourceUrl}/${getConversationIdentifier(conversation) as number}`, conversation, {
      observe: 'response',
    });
  }

  partialUpdate(conversation: IConversation): Observable<EntityResponseType> {
    return this.http.patch<IConversation>(`${this.resourceUrl}/${getConversationIdentifier(conversation) as number}`, conversation, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IConversation>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IConversation[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addConversationToCollectionIfMissing(
    conversationCollection: IConversation[],
    ...conversationsToCheck: (IConversation | null | undefined)[]
  ): IConversation[] {
    const conversations: IConversation[] = conversationsToCheck.filter(isPresent);
    if (conversations.length > 0) {
      const conversationCollectionIdentifiers = conversationCollection.map(
        conversationItem => getConversationIdentifier(conversationItem)!
      );
      const conversationsToAdd = conversations.filter(conversationItem => {
        const conversationIdentifier = getConversationIdentifier(conversationItem);
        if (conversationIdentifier == null || conversationCollectionIdentifiers.includes(conversationIdentifier)) {
          return false;
        }
        conversationCollectionIdentifiers.push(conversationIdentifier);
        return true;
      });
      return [...conversationsToAdd, ...conversationCollection];
    }
    return conversationCollection;
  }
}
