import { IPays } from 'app/entities/pays/pays.model';

export interface IChampionnat {
  id?: number;
  nom?: string | null;
  nombreDEquipes?: number | null;
  logoContentType?: string | null;
  logo?: string | null;
  niveau?: number | null;
  reputation?: number | null;
  pays?: IPays;
}

export class Championnat implements IChampionnat {
  constructor(
    public id?: number,
    public nom?: string | null,
    public nombreDEquipes?: number | null,
    public logoContentType?: string | null,
    public logo?: string | null,
    public niveau?: number | null,
    public reputation?: number | null,
    public pays?: IPays
  ) {}
}

export function getChampionnatIdentifier(championnat: IChampionnat): number | undefined {
  return championnat.id;
}
