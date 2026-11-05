import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { SubWorkDetail, WorkDetail } from '../models/app.models';

/**
 * Works and their sub works for the Pre-Tender screen.
 * TEMPORARY: the Work Details and Sub Work Details screens and their backend do not exist yet, so the data
 * below is a fixed sample kept in the frontend. When the backend is ready, only these methods change
 * (to HTTP calls); the screens already use them as observables.
 */
@Injectable({
  providedIn: 'root',
})
export class WorkService {
  private readonly works: WorkDetail[] = [
    {
      id: 9434,
      registrationNumber: '2025060504',
      workNumber: 'W/PWD/2025/101',
      nameOfWork: 'C/O road & Pulli from main road towards house of Verma & others',
      workCategory: 'Regular',
      programType: 'State Plan',
      workType: 'Roads',
      district: 'Shimla',
      scheme: 'Mukhya Mantri Sadak Yojana',
      budgetYear: '2025-26',
    },
    {
      id: 9435,
      registrationNumber: '2025060505',
      workNumber: 'W/PWD/2025/102',
      nameOfWork: 'C/O link road to Gujjar Basti Lamlehar',
      workCategory: 'Regular',
      programType: 'NABARD',
      workType: 'Roads',
      district: 'Kangra',
      scheme: 'RIDF XXIX',
      budgetYear: '2025-26',
    },
    {
      id: 9436,
      registrationNumber: '2025070112',
      workNumber: 'W/PWD/2025/115',
      nameOfWork: 'Construction of Government Senior Secondary School building at Palampur',
      workCategory: 'Deposit',
      programType: 'Deposit Work',
      workType: 'Buildings',
      district: 'Kangra',
      scheme: 'Samagra Shiksha',
      budgetYear: '2025-26',
    },
    {
      id: 9437,
      registrationNumber: '2026010203',
      workNumber: 'W/PWD/2026/007',
      nameOfWork: 'Improvement of Banapur main canal Antarkiyari-Kosarada road',
      workCategory: 'Regular',
      programType: 'Central Assistance',
      workType: 'Irrigation',
      district: 'Mandi',
      scheme: 'Special Central Assistance (CAPEX)',
      budgetYear: '2026-27',
    },
  ];

  private readonly subWorks: SubWorkDetail[] = [
    { id: 1, workId: 9434, registrationNumber: '2025060504/1', subWorkNumber: 'SW-101-01', description: 'C/O R/Wall from RD 0/065 to 0/090 including drainage', workCategory: 'Regular', programType: 'State Plan', workType: 'Roads', scheme: 'Mukhya Mantri Sadak Yojana' },
    { id: 2, workId: 9434, registrationNumber: '2025060504/2', subWorkNumber: 'SW-101-02', description: 'P/L hume pipe culvert (900 mm) at RD 0/120', workCategory: 'Regular', programType: 'State Plan', workType: 'Roads', scheme: 'Mukhya Mantri Sadak Yojana' },
    { id: 3, workId: 9434, registrationNumber: '2025060504/3', subWorkNumber: 'SW-101-03', description: 'Metalling and tarring from RD 0/000 to 0/450', workCategory: 'Regular', programType: 'State Plan', workType: 'Roads', scheme: 'Mukhya Mantri Sadak Yojana' },
    { id: 4, workId: 9435, registrationNumber: '2025060505/1', subWorkNumber: 'SW-102-01', description: 'P/L G-II, G-III & CC pavement road RD 0/033 to 0/090', workCategory: 'Regular', programType: 'NABARD', workType: 'Roads', scheme: 'RIDF XXIX' },
    { id: 5, workId: 9435, registrationNumber: '2025060505/2', subWorkNumber: 'SW-102-02', description: 'C/O breast wall and parapets RD 0/090 to 0/210', workCategory: 'Regular', programType: 'NABARD', workType: 'Roads', scheme: 'RIDF XXIX' },
    { id: 6, workId: 9436, registrationNumber: '2025070112/1', subWorkNumber: 'SW-115-01', description: 'Main academic block, ground and first floor (civil works)', workCategory: 'Deposit', programType: 'Deposit Work', workType: 'Buildings', scheme: 'Samagra Shiksha' },
    { id: 7, workId: 9436, registrationNumber: '2025070112/2', subWorkNumber: 'SW-115-02', description: 'Internal electrification and fire fighting system', workCategory: 'Deposit', programType: 'Deposit Work', workType: 'Buildings', scheme: 'Samagra Shiksha' },
    { id: 8, workId: 9436, registrationNumber: '2025070112/3', subWorkNumber: 'SW-115-03', description: 'Boundary wall, gate and approach path', workCategory: 'Deposit', programType: 'Deposit Work', workType: 'Buildings', scheme: 'Samagra Shiksha' },
    { id: 9, workId: 9436, registrationNumber: '2025070112/4', subWorkNumber: 'SW-115-04', description: 'Water supply and sanitary installations', workCategory: 'Deposit', programType: 'Deposit Work', workType: 'Buildings', scheme: 'Samagra Shiksha' },
    { id: 10, workId: 9437, registrationNumber: '2026010203/1', subWorkNumber: 'SW-007-01', description: 'Canal lining Reach 1, RD 0/000 to 1/200', workCategory: 'Regular', programType: 'Central Assistance', workType: 'Irrigation', scheme: 'Special Central Assistance (CAPEX)' },
    { id: 11, workId: 9437, registrationNumber: '2026010203/2', subWorkNumber: 'SW-007-02', description: 'Road widening Antarkiyari to Kosarada, RD 1/200 to 3/800', workCategory: 'Regular', programType: 'Central Assistance', workType: 'Irrigation', scheme: 'Special Central Assistance (CAPEX)' },
  ];

  /** Works whose registration number, work number or name contains the text. */
  searchWorks(text: string): Observable<WorkDetail[]> {
    const term = text.trim().toLowerCase();
    return of(this.works.filter((w) =>
      !term || [w.registrationNumber, w.workNumber, w.nameOfWork].some((v) => v.toLowerCase().includes(term))
    ));
  }

  /** Sub works of a work whose registration number, number or description contains the text. */
  searchSubWorks(workId: number, text: string): Observable<SubWorkDetail[]> {
    const term = text.trim().toLowerCase();
    return of(this.subWorks.filter((s) =>
      s.workId === workId &&
      (!term || [s.registrationNumber, s.subWorkNumber, s.description].some((v) => v.toLowerCase().includes(term)))
    ));
  }

  getWork(id: number): Observable<WorkDetail | undefined> {
    return of(this.works.find((w) => w.id === id));
  }
}
