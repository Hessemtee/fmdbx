import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IJeu } from '../jeu.model';

@Component({
  selector: 'jhi-jeu-detail',
  templateUrl: './jeu-detail.component.html',
})
export class JeuDetailComponent implements OnInit {
  jeu: IJeu | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ jeu }) => {
      this.jeu = jeu;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
