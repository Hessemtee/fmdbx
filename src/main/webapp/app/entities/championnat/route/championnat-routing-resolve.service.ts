import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IChampionnat, Championnat } from '../championnat.model';
import { ChampionnatService } from '../service/championnat.service';

@Injectable({ providedIn: 'root' })
export class ChampionnatRoutingResolveService implements Resolve<IChampionnat> {
  constructor(protected service: ChampionnatService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IChampionnat> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((championnat: HttpResponse<Championnat>) => {
          if (championnat.body) {
            return of(championnat.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Championnat());
  }
}
