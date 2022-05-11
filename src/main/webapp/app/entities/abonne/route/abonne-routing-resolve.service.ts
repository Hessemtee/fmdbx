import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAbonne, Abonne } from '../abonne.model';
import { AbonneService } from '../service/abonne.service';

@Injectable({ providedIn: 'root' })
export class AbonneRoutingResolveService implements Resolve<IAbonne> {
  constructor(protected service: AbonneService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAbonne> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((abonne: HttpResponse<Abonne>) => {
          if (abonne.body) {
            return of(abonne.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Abonne());
  }
}
