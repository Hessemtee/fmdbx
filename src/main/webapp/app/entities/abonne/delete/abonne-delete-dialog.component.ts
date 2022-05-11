import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IAbonne } from '../abonne.model';
import { AbonneService } from '../service/abonne.service';

@Component({
  templateUrl: './abonne-delete-dialog.component.html',
})
export class AbonneDeleteDialogComponent {
  abonne?: IAbonne;

  constructor(protected abonneService: AbonneService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.abonneService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
