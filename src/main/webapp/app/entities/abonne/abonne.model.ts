import { IUser } from 'app/entities/user/user.model';
import { ICommentaire } from 'app/entities/commentaire/commentaire.model';
import { IConversation } from 'app/entities/conversation/conversation.model';
import { IMessage } from 'app/entities/message/message.model';
import { IClub } from 'app/entities/club/club.model';
import { IJoueur } from 'app/entities/joueur/joueur.model';

export interface IAbonne {
  id?: number;
  nom?: string | null;
  avatarContentType?: string | null;
  avatar?: string | null;
  premium?: boolean | null;
  user?: IUser;
  commentaires?: ICommentaire[] | null;
  conversationEnvoies?: IConversation[] | null;
  conversationRecus?: IConversation[] | null;
  messages?: IMessage[] | null;
  bookmarks?: IClub[] | null;
  favorises?: IJoueur[] | null;
}

export class Abonne implements IAbonne {
  constructor(
    public id?: number,
    public nom?: string | null,
    public avatarContentType?: string | null,
    public avatar?: string | null,
    public premium?: boolean | null,
    public user?: IUser,
    public commentaires?: ICommentaire[] | null,
    public conversationEnvoies?: IConversation[] | null,
    public conversationRecus?: IConversation[] | null,
    public messages?: IMessage[] | null,
    public bookmarks?: IClub[] | null,
    public favorises?: IJoueur[] | null
  ) {
    this.premium = this.premium ?? false;
  }
}

export function getAbonneIdentifier(abonne: IAbonne): number | undefined {
  return abonne.id;
}
