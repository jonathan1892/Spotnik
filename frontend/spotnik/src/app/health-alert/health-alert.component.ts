import { Component, OnInit } from '@angular/core';
import { interval } from 'rxjs';
import { SpotnikService } from '../spotnik.service';
import { HealthReport } from '../health-report.model';

@Component({
  selector: 'app-health-alert',
  templateUrl: './health-alert.component.html',
  styleUrls: ['./health-alert.component.css']
})
export class HealthAlertComponent implements OnInit {

	isBackendReady: boolean;

  constructor(
  	private spotnik: SpotnikService
  ) { }

  ngOnInit() {
    this.isBackendReady = true; 

    this.checkHealthReport();
    interval(30000).subscribe((val) => { 
      this.checkHealthReport();
    });
  }

  checkHealthReport() {
    this.spotnik.getHealthStatus()
      .subscribe((result: HealthReport) => {
        this.isBackendReady = result.hasAccessToken;
        console.log(this.isBackendReady);
      }, (error) => {
        this.isBackendReady = false;
      })
  }

}
