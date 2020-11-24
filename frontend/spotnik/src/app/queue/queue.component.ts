import { Component, OnInit } from '@angular/core';
import { interval } from 'rxjs';
import { SpotnikService } from '../spotnik.service';
import { Track } from '../track.model';
import { QueueDescription } from '../queue-description.model';

@Component({
  selector: 'app-queue',
  templateUrl: './queue.component.html',
  styleUrls: ['./queue.component.css']
})
export class QueueComponent implements OnInit {

	queueDescription: QueueDescription;
  isLoadingTracks: boolean;
  countdownToReload: number;

  constructor(
  	private spotnik: SpotnikService
  ) { }

  ngOnInit() {
  	this.fetchQueue();
  	interval(1000).subscribe((val) => { 
      const refreshCount = 5;
      if (val % refreshCount == 0) {
        this.fetchQueue();
      }
  		
      this.countdownToReload = refreshCount - (val % refreshCount);
  	});
  }

  fetchQueue() {
    this.isLoadingTracks = true;
  	this.spotnik.describeQueue().subscribe((result: QueueDescription) => { 
	  	this.queueDescription=result;
      console.log(result);
      this.isLoadingTracks = false;
	  });
  }
}
