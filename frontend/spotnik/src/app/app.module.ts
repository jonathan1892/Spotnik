import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SearchComponent } from './search/search.component';
import { QueueComponent } from './queue/queue.component';
import { PlayingComponent } from './playing/playing.component';
import { MinuteSecondsPipe } from './minute-seconds.pipe';
import { HealthAlertComponent } from './health-alert/health-alert.component';
import { RoundToNearestPipe } from './round-to-nearest.pipe';

@NgModule({
  declarations: [
    AppComponent,
    SearchComponent,
    QueueComponent,
    PlayingComponent,
    MinuteSecondsPipe,
    HealthAlertComponent,
    RoundToNearestPipe,
  ],
  imports: [
    BrowserModule,
    NgbModule,
    HttpClientModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
