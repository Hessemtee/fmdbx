import { IJeu } from 'app/entities/jeu/jeu.model';

export interface IVersion {
  id?: number;
  version?: string;
  jeu?: IJeu | null;
}

export class Version implements IVersion {
  constructor(public id?: number, public version?: string, public jeu?: IJeu | null) {}
}

export function getVersionIdentifier(version: IVersion): number | undefined {
  return version.id;
}
