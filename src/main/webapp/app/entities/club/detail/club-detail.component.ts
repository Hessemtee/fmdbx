import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IClub } from '../club.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-club-detail',
  templateUrl: './club-detail.component.html',
})
export class ClubDetailComponent implements OnInit {
  club: IClub | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ club }) => {
      this.club = club;
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
