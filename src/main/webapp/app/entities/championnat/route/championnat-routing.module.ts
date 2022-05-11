import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ChampionnatComponent } from '../list/championnat.component';
import { ChampionnatDetailComponent } from '../detail/championnat-detail.component';
import { ChampionnatUpdateComponent } from '../update/championnat-update.component';
import { ChampionnatRoutingResolveService } from './championnat-routing-resolve.service';

const championnatRoute: Routes = [
  {
    path: '',
    component: ChampionnatComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ChampionnatDetailComponent,
    resolve: {
      championnat: ChampionnatRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ChampionnatUpdateComponent,
    resolve: {
      championnat: ChampionnatRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ChampionnatUpdateComponent,
    resolve: {
      championnat: ChampionnatRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(championnatRoute)],
  exports: [RouterModule],
})
export class ChampionnatRoutingModule {}
