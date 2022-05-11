import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IVersion } from '../version.model';
import { VersionService } from '../service/version.service';
import { VersionDeleteDialogComponent } from '../delete/version-delete-dialog.component';

@Component({
  selector: 'jhi-version',
  templateUrl: './version.component.html',
})
export class VersionComponent implements OnInit {
  versions?: IVersion[];
  isLoading = false;

  constructor(protected versionService: VersionService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.versionService.query().subscribe({
      next: (res: HttpResponse<IVersion[]>) => {
        this.isLoading = false;
        this.versions = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IVersion): number {
    return item.id!;
  }

  delete(version: IVersion): void {
    const modalRef = this.modalService.open(VersionDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.version = version;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
