import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IJeu, Jeu } from '../jeu.model';
import { JeuService } from '../service/jeu.service';

@Injectable({ providedIn: 'root' })
export class JeuRoutingResolveService implements Resolve<IJeu> {
  constructor(protected service: JeuService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IJeu> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((jeu: HttpResponse<Jeu>) => {
          if (jeu.body) {
            return of(jeu.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Jeu());
  }
}
