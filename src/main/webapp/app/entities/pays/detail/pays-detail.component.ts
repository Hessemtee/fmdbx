import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPays } from '../pays.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-pays-detail',
  templateUrl: './pays-detail.component.html',
})
export class PaysDetailComponent implements OnInit {
  pays: IPays | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ pays }) => {
      this.pays = pays;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
