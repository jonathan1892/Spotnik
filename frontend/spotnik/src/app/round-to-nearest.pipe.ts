import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'roundToNearest'
})
export class RoundToNearestPipe implements PipeTransform {

  transform(value: number, divider: number): number {
    return Math.round(value / divider) * divider;
  }
  
}
