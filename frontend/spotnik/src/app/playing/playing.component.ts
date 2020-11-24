import { Component, OnInit } from '@angular/core';
import { SpotnikService } from '../spotnik.service';
import { interval } from 'rxjs';
import { ActiveTrack } from '../active-track.model';

@Component({
  selector: 'app-playing',
  templateUrl: './playing.component.html',
  styleUrls: ['./playing.component.css']
})
export class PlayingComponent implements OnInit {

	currentlyPlaying: ActiveTrack;

  constructor(
  	private spotnik: SpotnikService
  ) { }

  ngOnInit() {
  	this.describeCurrentlyPlaying();
  	interval(1000).subscribe((val) => { 
  		this.describeCurrentlyPlaying();
  	});
  }

  describeCurrentlyPlaying() {
  	this.spotnik.describeCurrentlyPlaying()
  		.subscribe((result: ActiveTrack) => {
  			this.currentlyPlaying = result;
  		});
  }
}
