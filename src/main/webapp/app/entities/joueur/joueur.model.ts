import dayjs from 'dayjs/esm';
import { IClub } from 'app/entities/club/club.model';
import { IAbonne } from 'app/entities/abonne/abonne.model';
import { IPays } from 'app/entities/pays/pays.model';

export interface IJoueur {
  id?: number;
  nom?: string | null;
  prenom?: string | null;
  photo?: string | null;
  position?: string | null;
  dateNaissance?: dayjs.Dayjs | null;
  nombreSelections?: number | null;
  nombreButsInternationaux?: number | null;
  valeur?: number | null;
  salaire?: number | null;
  coutEstime?: number | null;
  club?: IClub | null;
  favorises?: IAbonne[] | null;
  pays?: IPays[] | null;
}

export class Joueur implements IJoueur {
  constructor(
    public id?: number,
    public nom?: string | null,
    public prenom?: string | null,
    public photo?: string | null,
    public position?: string | null,
    public dateNaissance?: dayjs.Dayjs | null,
    public nombreSelections?: number | null,
    public nombreButsInternationaux?: number | null,
    public valeur?: number | null,
    public salaire?: number | null,
    public coutEstime?: number | null,
    public club?: IClub | null,
    public favorises?: IAbonne[] | null,
    public pays?: IPays[] | null
  ) {}
}

export function getJoueurIdentifier(joueur: IJoueur): number | undefined {
  return joueur.id;
}
