import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'abonne',
        data: { pageTitle: 'fmdbxApp.abonne.home.title' },
        loadChildren: () => import('./abonne/abonne.module').then(m => m.AbonneModule),
      },
      {
        path: 'jeu',
        data: { pageTitle: 'fmdbxApp.jeu.home.title' },
        loadChildren: () => import('./jeu/jeu.module').then(m => m.JeuModule),
      },
      {
        path: 'version',
        data: { pageTitle: 'fmdbxApp.version.home.title' },
        loadChildren: () => import('./version/version.module').then(m => m.VersionModule),
      },
      {
        path: 'club',
        data: { pageTitle: 'fmdbxApp.club.home.title' },
        loadChildren: () => import('./club/club.module').then(m => m.ClubModule),
      },
      {
        path: 'championnat',
        data: { pageTitle: 'fmdbxApp.championnat.home.title' },
        loadChildren: () => import('./championnat/championnat.module').then(m => m.ChampionnatModule),
      },
      {
        path: 'pays',
        data: { pageTitle: 'fmdbxApp.pays.home.title' },
        loadChildren: () => import('./pays/pays.module').then(m => m.PaysModule),
      },
      {
        path: 'joueur',
        data: { pageTitle: 'fmdbxApp.joueur.home.title' },
        loadChildren: () => import('./joueur/joueur.module').then(m => m.JoueurModule),
      },
      {
        path: 'commentaire',
        data: { pageTitle: 'fmdbxApp.commentaire.home.title' },
        loadChildren: () => import('./commentaire/commentaire.module').then(m => m.CommentaireModule),
      },
      {
        path: 'message',
        data: { pageTitle: 'fmdbxApp.message.home.title' },
        loadChildren: () => import('./message/message.module').then(m => m.MessageModule),
      },
      {
        path: 'conversation',
        data: { pageTitle: 'fmdbxApp.conversation.home.title' },
        loadChildren: () => import('./conversation/conversation.module').then(m => m.ConversationModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
