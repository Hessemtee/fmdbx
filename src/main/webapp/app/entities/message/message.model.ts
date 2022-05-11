import dayjs from 'dayjs/esm';
import { IConversation } from 'app/entities/conversation/conversation.model';
import { IAbonne } from 'app/entities/abonne/abonne.model';

export interface IMessage {
  id?: number;
  contenu?: string | null;
  date?: dayjs.Dayjs | null;
  conversation?: IConversation;
  redacteur?: IAbonne;
}

export class Message implements IMessage {
  constructor(
    public id?: number,
    public contenu?: string | null,
    public date?: dayjs.Dayjs | null,
    public conversation?: IConversation,
    public redacteur?: IAbonne
  ) {}
}

export function getMessageIdentifier(message: IMessage): number | undefined {
  return message.id;
}
