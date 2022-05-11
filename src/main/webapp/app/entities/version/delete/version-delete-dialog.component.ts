import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IVersion } from '../version.model';
import { VersionService } from '../service/version.service';

@Component({
  templateUrl: './version-delete-dialog.component.html',
})
export class VersionDeleteDialogComponent {
  version?: IVersion;

  constructor(protected versionService: VersionService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.versionService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
