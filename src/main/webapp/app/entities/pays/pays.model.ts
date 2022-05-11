import { IChampionnat } from 'app/entities/championnat/championnat.model';
import { IJoueur } from 'app/entities/joueur/joueur.model';

export interface IPays {
  id?: number;
  nom?: string | null;
  drapeauContentType?: string | null;
  drapeau?: string | null;
  confederation?: string | null;
  indiceConf?: number | null;
  rankingFifa?: number | null;
  anneesAvantNationalite?: number | null;
  importanceEnJeu?: number | null;
  championnats?: IChampionnat[] | null;
  joueurs?: IJoueur[] | null;
}

export class Pays implements IPays {
  constructor(
    public id?: number,
    public nom?: string | null,
    public drapeauContentType?: string | null,
    public drapeau?: string | null,
    public confederation?: string | null,
    public indiceConf?: number | null,
    public rankingFifa?: number | null,
    public anneesAvantNationalite?: number | null,
    public importanceEnJeu?: number | null,
    public championnats?: IChampionnat[] | null,
    public joueurs?: IJoueur[] | null
  ) {}
}

export function getPaysIdentifier(pays: IPays): number | undefined {
  return pays.id;
}
