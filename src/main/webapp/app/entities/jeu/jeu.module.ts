import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { JeuComponent } from './list/jeu.component';
import { JeuDetailComponent } from './detail/jeu-detail.component';
import { JeuUpdateComponent } from './update/jeu-update.component';
import { JeuDeleteDialogComponent } from './delete/jeu-delete-dialog.component';
import { JeuRoutingModule } from './route/jeu-routing.module';

@NgModule({
  imports: [SharedModule, JeuRoutingModule],
  declarations: [JeuComponent, JeuDetailComponent, JeuUpdateComponent, JeuDeleteDialogComponent],
  entryComponents: [JeuDeleteDialogComponent],
})
export class JeuModule {}
