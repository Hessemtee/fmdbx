import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ClubComponent } from './list/club.component';
import { ClubDetailComponent } from './detail/club-detail.component';
import { ClubUpdateComponent } from './update/club-update.component';
import { ClubDeleteDialogComponent } from './delete/club-delete-dialog.component';
import { ClubRoutingModule } from './route/club-routing.module';

@NgModule({
  imports: [SharedModule, ClubRoutingModule],
  declarations: [ClubComponent, ClubDetailComponent, ClubUpdateComponent, ClubDeleteDialogComponent],
  entryComponents: [ClubDeleteDialogComponent],
})
export class ClubModule {}
