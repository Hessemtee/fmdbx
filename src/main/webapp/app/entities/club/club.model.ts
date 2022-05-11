import { IChampionnat } from 'app/entities/championnat/championnat.model';
import { IJeu } from 'app/entities/jeu/jeu.model';
import { IAbonne } from 'app/entities/abonne/abonne.model';

export interface IClub {
  id?: number;
  nom?: string;
  logoContentType?: string | null;
  logo?: string | null;
  ville?: string | null;
  balance?: number | null;
  masseSalariale?: number | null;
  budgetSalaires?: number | null;
  budgetTransferts?: number | null;
  infrastructuresEntrainement?: string | null;
  infrastructuresJeunes?: string | null;
  recrutementJeunes?: string | null;
  nomStade?: string | null;
  capaciteStade?: number | null;
  previsionMedia?: number | null;
  indiceContinental?: number | null;
  competitionContinentale?: boolean | null;
  championnat?: IChampionnat;
  jeuxes?: IJeu[] | null;
  bookmarks?: IAbonne[] | null;
}

export class Club implements IClub {
  constructor(
    public id?: number,
    public nom?: string,
    public logoContentType?: string | null,
    public logo?: string | null,
    public ville?: string | null,
    public balance?: number | null,
    public masseSalariale?: number | null,
    public budgetSalaires?: number | null,
    public budgetTransferts?: number | null,
    public infrastructuresEntrainement?: string | null,
    public infrastructuresJeunes?: string | null,
    public recrutementJeunes?: string | null,
    public nomStade?: string | null,
    public capaciteStade?: number | null,
    public previsionMedia?: number | null,
    public indiceContinental?: number | null,
    public competitionContinentale?: boolean | null,
    public championnat?: IChampionnat,
    public jeuxes?: IJeu[] | null,
    public bookmarks?: IAbonne[] | null
  ) {
    this.competitionContinentale = this.competitionContinentale ?? false;
  }
}

export function getClubIdentifier(club: IClub): number | undefined {
  return club.id;
}
