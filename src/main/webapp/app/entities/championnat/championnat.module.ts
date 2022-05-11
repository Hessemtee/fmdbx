import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ChampionnatComponent } from './list/championnat.component';
import { ChampionnatDetailComponent } from './detail/championnat-detail.component';
import { ChampionnatUpdateComponent } from './update/championnat-update.component';
import { ChampionnatDeleteDialogComponent } from './delete/championnat-delete-dialog.component';
import { ChampionnatRoutingModule } from './route/championnat-routing.module';

@NgModule({
  imports: [SharedModule, ChampionnatRoutingModule],
  declarations: [ChampionnatComponent, ChampionnatDetailComponent, ChampionnatUpdateComponent, ChampionnatDeleteDialogComponent],
  entryComponents: [ChampionnatDeleteDialogComponent],
})
export class ChampionnatModule {}
