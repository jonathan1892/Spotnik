import { Pipe, PipeTransform } from '@angular/core';
import * as moment from 'moment';

@Pipe({
  name: 'minuteSeconds'
})
export class MinuteSecondsPipe implements PipeTransform {

	transform(value: number): string {
		const duration = moment.duration(value, 'seconds');
		var format: string;
		if (duration.asHours() >= 1) {
			format = 'HH:mm:ss';
		} else {
			format = 'mm:ss'
		}

	  return moment.utc(duration.as('milliseconds')).format(format);
	}
}