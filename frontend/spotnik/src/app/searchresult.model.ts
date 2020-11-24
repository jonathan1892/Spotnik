import { Track } from './track.model';

export class SearchResult {

	adding: boolean;
	added: boolean;
	
	constructor(
		public track: Track
	) {
		this.adding = false;
		this.added = false;
	}
}