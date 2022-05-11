import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { AbonneComponent } from './list/abonne.component';
import { AbonneDetailComponent } from './detail/abonne-detail.component';
import { AbonneUpdateComponent } from './update/abonne-update.component';
import { AbonneDeleteDialogComponent } from './delete/abonne-delete-dialog.component';
import { AbonneRoutingModule } from './route/abonne-routing.module';

@NgModule({
  imports: [SharedModule, AbonneRoutingModule],
  declarations: [AbonneComponent, AbonneDetailComponent, AbonneUpdateComponent, AbonneDeleteDialogComponent],
  entryComponents: [AbonneDeleteDialogComponent],
})
export class AbonneModule {}
