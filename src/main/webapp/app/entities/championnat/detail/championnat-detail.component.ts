import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IChampionnat } from '../championnat.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-championnat-detail',
  templateUrl: './championnat-detail.component.html',
})
export class ChampionnatDetailComponent implements OnInit {
  championnat: IChampionnat | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ championnat }) => {
      this.championnat = championnat;
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
