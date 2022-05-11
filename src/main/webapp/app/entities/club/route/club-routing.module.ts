import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ClubComponent } from '../list/club.component';
import { ClubDetailComponent } from '../detail/club-detail.component';
import { ClubUpdateComponent } from '../update/club-update.component';
import { ClubRoutingResolveService } from './club-routing-resolve.service';

const clubRoute: Routes = [
  {
    path: '',
    component: ClubComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ClubDetailComponent,
    resolve: {
      club: ClubRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ClubUpdateComponent,
    resolve: {
      club: ClubRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ClubUpdateComponent,
    resolve: {
      club: ClubRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(clubRoute)],
  exports: [RouterModule],
})
export class ClubRoutingModule {}
