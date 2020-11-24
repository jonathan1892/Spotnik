import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HealthReport } from './health-report.model';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class SpotnikService {

	host: string;
	username: string;
	password: string;

  constructor(private http: HttpClient) {
  	this.host = "http://spotnik.kloppodurockstar.eu";
  	this.username = "kloppodurockstar";
  	this.password = "letstalkaboutsix";
  }

  getHealthStatus() : Observable<HealthReport> {
    console.log("Fetching health report...");
    return this.http.get<HealthReport>(this.host + '/api/health');
  }

  getLogInLink() : string {
  	return this.host + "/admin/login";
  }

  logOut() : Observable<Object> {
  	console.log("Logging out...");
  	return this.http.get(this.host + "/admin/logout", {headers: this.getHeaders()});
  }

  getHeaders() : HttpHeaders {
  	return new HttpHeaders({
	  	'Content-Type':  'application/json',
	    'Authorization': 'Basic ' + btoa(this.username + ':' + this.password)
		});
  }

  skipSong() : Observable<Object> {
  	console.log("Skip song...");
  	return this.http.get(this.host + "/api/admin/queue/next", {headers: this.getHeaders()});
  }

  queuePlaylist(playlistUri: string) : Observable<Object> {
  	console.log("Queue playlist...");
  	return this.http.put(this.host + "/api/admin/playlist?uri=" + playlistUri, {}, {headers: this.getHeaders()});
  }

  clearPlaylist() : Observable<Object> {
  	console.log("Clearing playlist...");
  	return this.http.delete(this.host + "/api/admin/playlist", {headers: this.getHeaders()});
  }
}
