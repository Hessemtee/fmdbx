import { IVersion } from 'app/entities/version/version.model';
import { IClub } from 'app/entities/club/club.model';

export interface IJeu {
  id?: number;
  nom?: string;
  versions?: IVersion[];
  clubs?: IClub[] | null;
}

export class Jeu implements IJeu {
  constructor(public id?: number, public nom?: string, public versions?: IVersion[], public clubs?: IClub[] | null) {}
}

export function getJeuIdentifier(jeu: IJeu): number | undefined {
  return jeu.id;
}
