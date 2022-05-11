import { IJoueur } from 'app/entities/joueur/joueur.model';
import { IClub } from 'app/entities/club/club.model';
import { IAbonne } from 'app/entities/abonne/abonne.model';

export interface ICommentaire {
  id?: number;
  contenu?: string | null;
  visible?: boolean | null;
  joueurConcerne?: IJoueur | null;
  clubConcerne?: IClub | null;
  abonne?: IAbonne;
}

export class Commentaire implements ICommentaire {
  constructor(
    public id?: number,
    public contenu?: string | null,
    public visible?: boolean | null,
    public joueurConcerne?: IJoueur | null,
    public clubConcerne?: IClub | null,
    public abonne?: IAbonne
  ) {
    this.visible = this.visible ?? false;
  }
}

export function getCommentaireIdentifier(commentaire: ICommentaire): number | undefined {
  return commentaire.id;
}
