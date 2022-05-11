import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AbonneComponent } from '../list/abonne.component';
import { AbonneDetailComponent } from '../detail/abonne-detail.component';
import { AbonneUpdateComponent } from '../update/abonne-update.component';
import { AbonneRoutingResolveService } from './abonne-routing-resolve.service';

const abonneRoute: Routes = [
  {
    path: '',
    component: AbonneComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AbonneDetailComponent,
    resolve: {
      abonne: AbonneRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AbonneUpdateComponent,
    resolve: {
      abonne: AbonneRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AbonneUpdateComponent,
    resolve: {
      abonne: AbonneRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(abonneRoute)],
  exports: [RouterModule],
})
export class AbonneRoutingModule {}
