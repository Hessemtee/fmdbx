import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { JeuComponent } from '../list/jeu.component';
import { JeuDetailComponent } from '../detail/jeu-detail.component';
import { JeuUpdateComponent } from '../update/jeu-update.component';
import { JeuRoutingResolveService } from './jeu-routing-resolve.service';

const jeuRoute: Routes = [
  {
    path: '',
    component: JeuComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: JeuDetailComponent,
    resolve: {
      jeu: JeuRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: JeuUpdateComponent,
    resolve: {
      jeu: JeuRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: JeuUpdateComponent,
    resolve: {
      jeu: JeuRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(jeuRoute)],
  exports: [RouterModule],
})
export class JeuRoutingModule {}
