import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAbonne } from '../abonne.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-abonne-detail',
  templateUrl: './abonne-detail.component.html',
})
export class AbonneDetailComponent implements OnInit {
  abonne: IAbonne | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ abonne }) => {
      this.abonne = abonne;
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
