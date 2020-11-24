import { Component, OnInit } from '@angular/core';
import { interval } from 'rxjs';
import { SpotnikService } from '../spotnik.service';
import { Track } from '../track.model';
import { SearchResult } from '../searchresult.model';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {

	searchResults: SearchResult[];
	performingSearch: boolean;

  constructor(
  	private spotnik: SpotnikService
  ) { }

  ngOnInit() {}

  performSearch(query: string) {
  	this.performingSearch = true;
  	this.spotnik.findTracks(query)
	  	.subscribe((results: Array<Track>) => { 
	  		this.performingSearch = false;
	  		this.searchResults = this.convertToSeachResults(results);
	  	});
  }

  convertToSeachResults(results: Array<Track>) : SearchResult[] {
  	let searchResults = [];
		results.forEach(result => {
			searchResults.push(new SearchResult(result));
		});
		return searchResults;
  }

  queueSearchResult(searchResult: SearchResult) {
  	console.log("Queuing result=" + searchResult);
  	if (searchResult.added) {
  		console.log("Song already added by this user, not adding again...")
  		return;
  	}

  	searchResult.adding = true;
  	this.spotnik.queueTrack(searchResult.track).subscribe((result) => {
  		searchResult.adding = false;
  		searchResult.added = true;
  	}, (error) => {
  		console.log(error);
  		searchResult.adding = false;
  	});
  }
}
