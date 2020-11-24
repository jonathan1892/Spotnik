import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Track } from './track.model';
import { ActiveTrack } from './active-track.model';
import { QueueDescription } from './queue-description.model';
import { HealthReport } from './health-report.model';
import { environment } from './../environments/environment';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SpotnikService {

	host: string;

  constructor(
  	private http: HttpClient
  ) {
  	this.host = environment.apiUrl;
  }

  findTracks(query: string) : Observable<Track[]> {
  	console.log("Finding tracks for query=" + query);
  	const urlParams = new HttpParams().set('q', query);
  	const options = { params: urlParams };
  	return this.http.get<Track[]>(this.host + '/api/search', options);
  }

  queueTrack(track: Track) : Observable<Object>  {
  	console.log("Queuing track=" + track.trackName + ", " + track.artists + ", " + track.albumName);
  	const urlParams = new HttpParams().set('uri', track.uri);
  	const options = { params: urlParams };
  	return this.http.put(this.host + '/api/queue', {}, options);
  }

  describeQueue() : Observable<QueueDescription> {
  	console.log("Fetching queue information...");
  	return this.http.get<QueueDescription>(this.host + '/api/queue');
  }

  describeCurrentlyPlaying() : Observable<ActiveTrack> {
  	console.log("Fetching currently playing information...");
  	return this.http.get<ActiveTrack>(this.host + '/api/playing');
  }

  getHealthStatus() : Observable<HealthReport> {
    console.log("Fetching health report...");
    return this.http.get<HealthReport>(this.host + '/api/health');
  }
}
