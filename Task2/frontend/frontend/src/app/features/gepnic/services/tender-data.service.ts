import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface SharedTenderData {
  // Core WAMIS Tender Push Data
  wamisTenderId: string;
  officeId: string;
  tenderTitle: string;
  workDescription: string;
  estimatedCost: number;
  tenderFee: number;
  emdAmount: number;
  publishDate: string;
  submissionEndDate: string;
  natureOfWork: string;
  roadType: string;
  roadLength: string;
  roadWidth: string;
  workPlanType: string;
  cdWorks: string;

  // Extra respective IDs for NIT, BOQ, and Tendering tabs
  nitId: string;
  nitDate: string;
  emdValue: number;
  submissionDate: string;
  closingDate: string;
  specialConditions: string;
  uploadedNoticeFile?: string;

  boqId: string;
  totalBoqAmount: number;
  dsrSchedule: string;

  tenderConfigId: string;
  tenderCategory: string;
  biddingSystem: string;
  evaluationMethod: string;
  preQualification: string;
  paymentMode: string;
}

@Injectable({
  providedIn: 'root'
})
export class TenderDataService {

  private defaultData: SharedTenderData = {
    wamisTenderId: 'WAMIS-TND-2026-000123',
    officeId: 'OFF-001',
    tenderTitle: 'Road Improvement Work in Pune Division',
    workDescription: 'Asphalt resurfacing and drainage strengthening on MDR-12',
    estimatedCost: 6660150,
    tenderFee: 1500,
    emdAmount: 50000,
    publishDate: '2026-11-01T10:00',
    submissionEndDate: '2026-11-20T17:00',
    natureOfWork: 'Maintenance',
    roadType: 'Bituminous Concrete (BC)',
    roadLength: '4.8',
    roadWidth: '7.00 m (Two Lane)',
    workPlanType: 'Annual Plan 2026-27',
    cdWorks: '2 Culverts, 1 Minor Bridge',

    // Extra ID and fields for NIT
    nitId: 'NIT-PWD-PUNE-2026-089',
    nitDate: '2026-11-01',
    emdValue: 50000,
    submissionDate: '2026-11-05',
    closingDate: '2026-11-20',
    specialConditions: 'Standard PWD Maharashtra Civil Conditions (Form B-1)',
    uploadedNoticeFile: 'NIT-Notice-2026-089.pdf',

    // Extra ID and fields for BOQ
    boqId: 'BOQ-2026-PUNE-001',
    totalBoqAmount: 6660150,
    dsrSchedule: 'PWD DSR 2026-27 (Pune Region)',

    // Extra ID and fields for Tendering
    tenderConfigId: 'TND-CFG-2026-904',
    tenderCategory: 'Open Tender (National Competitive Bidding)',
    biddingSystem: 'Two Packet (Technical & Financial)',
    evaluationMethod: 'Least Cost System (L1 Bidder)',
    preQualification: 'Mandatory Class-I PWD Registration',
    paymentMode: 'Online Net Banking / RTGS'
  };

  private tenderData$ = new BehaviorSubject<SharedTenderData>(this.defaultData);

  getTenderData(): Observable<SharedTenderData> {
    return this.tenderData$.asObservable();
  }

  getCurrentValue(): SharedTenderData {
    return this.tenderData$.getValue();
  }

  updateTenderData(updated: Partial<SharedTenderData>): void {
    const current = this.tenderData$.getValue();
    this.tenderData$.next({
      ...current,
      ...updated
    });
  }
}
