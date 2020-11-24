import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HealthAlertComponent } from './health-alert.component';

describe('HealthAlertComponent', () => {
  let component: HealthAlertComponent;
  let fixture: ComponentFixture<HealthAlertComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HealthAlertComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HealthAlertComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
