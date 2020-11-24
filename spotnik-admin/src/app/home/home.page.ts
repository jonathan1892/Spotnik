import { Component, OnInit } from '@angular/core';
import { SpotnikService } from '../spotnik.service';
import { HealthReport } from '../health-report.model';
import { interval } from 'rxjs';

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss'],
})
export class HomePage implements OnInit {

	isBackendReachable: boolean;
	hasAccessToken: boolean;

	playlistUri: string;

  constructor(private spotnik : SpotnikService) {}

  ngOnInit() {
  	this.checkHealthReport();
  	interval(1000).subscribe((val) => { 
  		this.checkHealthReport();
  	});
  }

  checkHealthReport() {
    this.spotnik.getHealthStatus()
      .subscribe((result: HealthReport) => {
      	this.isBackendReachable = true;
        this.hasAccessToken = result.hasAccessToken;
      }, (error) => {
      	this.isBackendReachable = false;
        this.hasAccessToken = false;
      })
  }

  logIn() : void {
  	window.open(this.spotnik.getLogInLink());
  }

  logOut() : void {
  	this.spotnik.logOut().subscribe();
  }

  skipSong() : void {
  	this.spotnik.skipSong().subscribe();
  }

  queuePlaylist() : void {
  	this.spotnik.queuePlaylist(this.playlistUri).subscribe();
  }

  clearPlaylist() : void {
  		this.spotnik.clearPlaylist().subscribe();
  }
}
