import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { SearchComponent } from './search/search.component';
import { QueueComponent } from './queue/queue.component';

const routes: Routes = [
	{path: '', component: SearchComponent},
	{path: 'queue', component: QueueComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
