import { TestBed } from '@angular/core/testing';

import { SpotnikService } from './spotnik.service';

describe('SpotnikService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: SpotnikService = TestBed.get(SpotnikService);
    expect(service).toBeTruthy();
  });
});
