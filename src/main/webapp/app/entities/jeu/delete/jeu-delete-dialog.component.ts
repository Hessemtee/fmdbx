import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IJeu } from '../jeu.model';
import { JeuService } from '../service/jeu.service';

@Component({
  templateUrl: './jeu-delete-dialog.component.html',
})
export class JeuDeleteDialogComponent {
  jeu?: IJeu;

  constructor(protected jeuService: JeuService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.jeuService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
