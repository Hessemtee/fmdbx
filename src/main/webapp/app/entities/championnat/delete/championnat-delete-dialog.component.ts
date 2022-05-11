import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IChampionnat } from '../championnat.model';
import { ChampionnatService } from '../service/championnat.service';

@Component({
  templateUrl: './championnat-delete-dialog.component.html',
})
export class ChampionnatDeleteDialogComponent {
  championnat?: IChampionnat;

  constructor(protected championnatService: ChampionnatService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.championnatService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
