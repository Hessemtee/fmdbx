import { IMessage } from 'app/entities/message/message.model';
import { IAbonne } from 'app/entities/abonne/abonne.model';

export interface IConversation {
  id?: number;
  objet?: string | null;
  messages?: IMessage[] | null;
  emetteur?: IAbonne | null;
  recepteur?: IAbonne | null;
}

export class Conversation implements IConversation {
  constructor(
    public id?: number,
    public objet?: string | null,
    public messages?: IMessage[] | null,
    public emetteur?: IAbonne | null,
    public recepteur?: IAbonne | null
  ) {}
}

export function getConversationIdentifier(conversation: IConversation): number | undefined {
  return conversation.id;
}
