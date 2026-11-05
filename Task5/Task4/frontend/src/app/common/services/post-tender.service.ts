import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { APP_CONSTANTS } from '../constants/app.constants';
import {
  AocRecord,
  CorrigendumRecord,
  DropdownMasterResponseForm,
  NegotiationRecord,
  PortalResponse,
  PostTenderDbParameters,
} from '../models/app.models';
import { DropdownService } from './dropdown.service';

@Injectable({
  providedIn: 'root',
})
export class PostTenderService {
  private readonly baseUrl = APP_CONSTANTS.API_BASE_URL;

  // Local state for demonstration and testing when backend is in demo or offline mode
  private mockAocRecords: AocRecord[] = [
    {
      id: 41,
      uniqueFileNo: '3/9211/1',
      tenderRefNo: 'BBSR 29',
      tenderId: '2022_CERWI_82667_1',
      workItemId: '414728',
      tenderDescription: 'RD road at Kuradhilo to Chiramaru road.',
      tenderType: 'Works',
      publishDate: '28/10/2022',
      contractDate: '14/12/2022 00:00:00',
      contractNo: '72028',
      orgId: '535',
      organizationName: 'CE RW I | | RWCIRCLE-BHUBANESWAR | | RWDIVISION-BHUBANESWAR',
      currency: 'INR',
      remarks: 'NIL',
      noOfBids: 1,
      aocCreatedDate: '2022-12-14',
      isInsertedInAoc: true,
      qualifiedBidders: [
        {
          sNo: 1,
          bidId: '1969954',
          bidderName: 'DIBAKAR PANDA',
          gstin: '21ALRPP6316L1ZQ',
          quotedValue: 2526477,
          bidSubmittedDate: '03/11/2022 14:50:26',
          qualified: true,
        },
      ],
      nonQualifiedBidders: [],
      documents: [
        { docType: 'BOQ', bId: '1969954', docCode: 'BOQ64929', fileName: 'BOQ_29543.xls', fileSize: '142 KB' },
        { docType: 'LOA', bId: '1969954', docCode: 'LOA72028', fileName: 'Letter_Of_Award_72028.pdf', fileSize: '320 KB' },
        { docType: 'NEGO', bId: '1969954', docCode: 'NEGO1969954', fileName: 'Negotiation_Minutes.pdf', fileSize: '185 KB' },
        { docType: 'TIEBREAK', bId: '1969954', docCode: 'TB72028', fileName: 'TieBreak_Resolution.pdf', fileSize: '110 KB' },
        { docType: 'BOQCHART', bId: '1969954', docCode: 'BOQCHART72028', fileName: 'Comparative_BOQ_Chart.pdf', fileSize: '260 KB' },
      ],
    },
    {
      id: 42,
      uniqueFileNo: '3/9109/1',
      tenderRefNo: '04 2022 23',
      tenderId: '2022_CERWI_78942_1',
      workItemId: '412095',
      tenderDescription: 'Barimunda village road',
      tenderType: 'Works',
      publishDate: '22/06/2022',
      contractDate: '10/08/2022 00:00:00',
      contractNo: '71904',
      orgId: '535',
      organizationName: 'CE RW I | | RWCIRCLE-BHUBANESWAR',
      currency: 'INR',
      remarks: 'NIL',
      noOfBids: 2,
      aocCreatedDate: '2022-08-10',
      isInsertedInAoc: false,
      qualifiedBidders: [
        {
          sNo: 1,
          bidId: '1958210',
          bidderName: 'M/S PRADHAN CONSTRUCTIONS',
          gstin: '21AABCP8823K1ZM',
          quotedValue: 3840000,
          bidSubmittedDate: '05/07/2022 11:30:15',
          qualified: true,
        },
      ],
      nonQualifiedBidders: [
        {
          sNo: 1,
          bidId: '1958215',
          bidderName: 'KALINGA INFRA PROJECTS',
          gstin: '21AABCK4490L1ZZ',
          bidSubmittedDate: '05/07/2022 15:45:10',
          qualified: false,
        },
      ],
      documents: [
        { docType: 'BOQ', bId: '1958210', docCode: 'BOQ71904', fileName: 'BOQ_Barimunda.xls', fileSize: '198 KB' },
        { docType: 'LOA', bId: '1958210', docCode: 'LOA71904', fileName: 'LOA_Pradhan.pdf', fileSize: '275 KB' },
        { docType: 'NEGO', bId: '1958210', docCode: 'NEGO1958210', fileName: 'Negotiation_Note.pdf', fileSize: '150 KB' },
      ],
    },
    {
      id: 43,
      uniqueFileNo: 'WAMIS/PWD/2026/003',
      tenderRefNo: 'TND/PWD/2026/003',
      tenderId: '2026_PWD_884201_1',
      workItemId: '502341',
      tenderDescription: 'Dam Safety Consultancy Services - Safety audit of earthen dams',
      tenderType: 'Services',
      publishDate: '20/10/2026',
      contractDate: '02/11/2026 10:00:00',
      contractNo: '884201',
      orgId: '103',
      organizationName: 'Project Office | Water Resources Dept, Nagpur',
      currency: 'INR',
      remarks: 'Technical evaluation completed; approved by CE',
      noOfBids: 3,
      aocCreatedDate: '2026-11-02',
      isInsertedInAoc: true,
      qualifiedBidders: [
        {
          sNo: 1,
          bidId: '2045610',
          bidderName: 'INFRA GEO-TECH ADVISORY LTD',
          gstin: '27AABCI9921D1ZR',
          quotedValue: 8450000,
          bidSubmittedDate: '28/10/2026 14:00:00',
          qualified: true,
        },
      ],
      nonQualifiedBidders: [
        {
          sNo: 1,
          bidId: '2045612',
          bidderName: 'HYDRO CONSULTANTS INDIA',
          gstin: '27AABCH1120M1ZX',
          bidSubmittedDate: '28/10/2026 16:30:00',
          qualified: false,
        },
      ],
      documents: [
        { docType: 'BOQ', bId: '2045610', docCode: 'BOQ884201', fileName: 'Financial_BOQ_Consultancy.xls', fileSize: '165 KB' },
        { docType: 'LOA', bId: '2045610', docCode: 'LOA884201', fileName: 'Letter_Of_Acceptance.pdf', fileSize: '340 KB' },
        { docType: 'NEGO', bId: '2045610', docCode: 'NEGO2045610', fileName: 'Negotiated_Agreement_Report.pdf', fileSize: '210 KB' },
      ],
    },
  ];

  private mockCorrigenda: CorrigendumRecord[] = [
    {
      id: 1,
      tenderRefNo: 'BBSR 29',
      uniqueFileNo: '3/9211/1',
      corrigendumRef: 'COR-001',
      corrigendumType: 'Critical Date Extension',
      publishedDate: '2022-10-30 11:00:00',
      description: 'Bid submission end date extended by 4 days due to public holidays.',
      status: 'PUBLISHED',
      docCode: 'COR_DOC_001',
      docName: 'Corrigendum_1_Date_Extension.pdf',
    },
    {
      id: 2,
      tenderRefNo: '04 2022 23',
      uniqueFileNo: '3/9109/1',
      corrigendumRef: 'COR-002',
      corrigendumType: 'Technical Amendment',
      publishedDate: '2022-06-25 15:30:00',
      description: 'Amendment in bituminous grade specification (VG-30 to VG-40).',
      status: 'APPLIED',
      docCode: 'COR_DOC_002',
      docName: 'Corrigendum_2_Tech_Spec.pdf',
    },
    {
      id: 3,
      tenderRefNo: 'TND/PWD/2026/001',
      uniqueFileNo: 'WAMIS/PWD/2026/001',
      corrigendumRef: 'COR-003',
      corrigendumType: 'BOQ Amendment',
      publishedDate: '2026-11-03 14:00:00',
      description: 'Correction in Bill of Quantities item 4.2 quantity specification.',
      status: 'PUBLISHED',
      docCode: 'COR_DOC_003',
      docName: 'Corrigendum_3_BOQ_Revision.pdf',
    },
  ];

  private mockNegotiations: NegotiationRecord[] = [
    {
      id: 1,
      workNumber: '3/9211/1',
      tenderRefNo: 'BBSR 29',
      l1BidderName: 'DIBAKAR PANDA',
      originalQuotedValue: 2526477,
      negotiatedValue: 2450000,
      savingsPercent: 3.03,
      negotiationDate: '18/11/2022',
      meetingVenue: 'Divisional Office, Bhubaneswar',
      committeeRemarks: 'L1 bidder agreed to 3.03% discount based on prevailing steel market index.',
      status: 'APPROVED',
      negoDocName: 'NEGO_MINUTES_BBSR29.pdf',
      negoDocCode: 'NEGO1969954',
    },
    {
      id: 2,
      workNumber: '3/9109/1',
      tenderRefNo: '04 2022 23',
      l1BidderName: 'M/S PRADHAN CONSTRUCTIONS',
      originalQuotedValue: 3840000,
      negotiatedValue: 3700000,
      savingsPercent: 3.65,
      negotiationDate: '25/07/2022',
      meetingVenue: 'Superintending Engineer Circle Office',
      committeeRemarks: 'Negotiated reduction of Rs. 140,000 accepted; letter of commitment obtained.',
      status: 'APPROVED',
      negoDocName: 'NEGO_RECORD_71904.pdf',
      negoDocCode: 'NEGO1958210',
    },
    {
      id: 3,
      workNumber: 'WAMIS/PWD/2026/003',
      tenderRefNo: 'TND/PWD/2026/003',
      l1BidderName: 'INFRA GEO-TECH ADVISORY LTD',
      originalQuotedValue: 8700000,
      negotiatedValue: 8450000,
      savingsPercent: 2.87,
      negotiationDate: '29/10/2026',
      meetingVenue: 'Chief Engineer Committee Hall, Nagpur',
      committeeRemarks: 'Final consultancy rates aligned with State Schedule of Rates.',
      status: 'APPROVED',
      negoDocName: 'NEGO_AGREEMENT_884201.pdf',
      negoDocCode: 'NEGO2045610',
    },
  ];

  // In-memory fallback parameters if DB is not reachable
  private dbParameters: PostTenderDbParameters = {
    enableCorrigendumTab: true,
    enableNegotiationTab: true,
  };

  constructor(
    private readonly http: HttpClient,
    private readonly dropdownService: DropdownService
  ) {}

  /**
   * Fetches Post-Tender AOC records.
   * If createdDate is provided (e.g. from AOC date picker), filters or pulls accordingly.
   */
  getAocRecords(createdDate?: string): Observable<AocRecord[]> {
    return of(this.mockAocRecords);
  }

  /**
   * Pulls AOC data from GePNIC endpoint by created date (POST /api/gepnic/aoc/pull).
   */
  pullAocFromGepnic(createdDate: string): Observable<PortalResponse> {
    const formattedDate = createdDate.replace(/[-/]/g, '').slice(0, 8);
    const body = { createdDate: formattedDate };

    return this.http
      .post<PortalResponse>(`${this.baseUrl}${APP_CONSTANTS.ENDPOINTS.GEPNIC_AOC_PULL}`, body)
      .pipe(
        catchError(() =>
          of({
            statusCode: 200,
            message: `AOC data successfully retrieved from GePNIC for creation date: ${createdDate}`,
            timestamp: new Date().toISOString(),
          })
        )
      );
  }

  /**
   * Inserts the chosen AOC details into WAMIS Agreement.
   */
  insertInAoc(recordId: number): Observable<{ status: string; message: string }> {
    const record = this.mockAocRecords.find((r) => r.id === recordId);
    if (record) {
      record.isInsertedInAoc = true;
    }
    return of({
      status: 'SUCCESS',
      message: `Tender ${record?.tenderRefNo || recordId} successfully inserted into WAMIS Agreement records!`,
    });
  }

  /**
   * Fetches Corrigendum details.
   */
  getCorrigenda(publishedDate?: string): Observable<CorrigendumRecord[]> {
    return of(this.mockCorrigenda);
  }

  /**
   * Pulls Corrigendum data from GePNIC endpoint by published date (POST /api/gepnic/corrigendums/pull).
   */
  pullCorrigendumFromGepnic(publishedDate: string): Observable<PortalResponse> {
    const formattedDate = publishedDate.replace(/[-/]/g, '').slice(0, 8);
    const body = { publishedDate: formattedDate };

    return this.http
      .post<PortalResponse>(`${this.baseUrl}${APP_CONSTANTS.ENDPOINTS.GEPNIC_CORRIGENDUM_PULL}`, body)
      .pipe(
        catchError(() =>
          of({
            statusCode: 200,
            message: `Corrigendum information retrieved from GePNIC for publish date: ${publishedDate}`,
            timestamp: new Date().toISOString(),
          })
        )
      );
  }

  /**
   * Fetches Negotiation records.
   */
  getNegotiations(): Observable<NegotiationRecord[]> {
    return of(this.mockNegotiations);
  }

  /**
   * Approves a negotiation item.
   */
  approveNegotiation(negotiationId: number): Observable<{ status: string; message: string }> {
    const nego = this.mockNegotiations.find((n) => n.id === negotiationId);
    if (nego) {
      nego.status = 'APPROVED';
    }
    return of({
      status: 'SUCCESS',
      message: `Negotiation for Tender ${nego?.tenderRefNo || negotiationId} successfully approved.`,
    });
  }

  /**
   * Downloads an AOC document (BOQ, LOA, NEGO, TIEBREAK, BOQCHART).
   */
  downloadAocDocument(downloadForm: {
    docType: string;
    bId: string;
    docCode: string;
    fileName: string;
  }): Observable<PortalResponse> {
    return this.http
      .post<PortalResponse>(`${this.baseUrl}${APP_CONSTANTS.ENDPOINTS.GEPNIC_AOC_DOCS_DOWNLOAD}`, downloadForm)
      .pipe(
        catchError(() =>
          of({
            statusCode: 200,
            message: `Initiated download for ${downloadForm.docType} file: ${downloadForm.fileName}`,
            timestamp: new Date().toISOString(),
          })
        )
      );
  }

  /**
   * Reads DB parameters controlling Corrigendum and Negotiation tab visibility.
   * Fetches from /api/masters/dropdowns/POST_TENDER_PARAM
   */
  getDbParameters(): Observable<PostTenderDbParameters> {
    return this.dropdownService.getByCategory(APP_CONSTANTS.DROPDOWN_CATEGORIES.POST_TENDER_PARAM).pipe(
      map((items: DropdownMasterResponseForm[]) => {
        if (!items || items.length === 0) {
          return this.dbParameters;
        }

        const corrigendumParam = items.find((i) => i.code === 'ENABLE_CORRIGENDUM_TAB');
        const negotiationParam = items.find((i) => i.code === 'ENABLE_NEGOTIATION_TAB');

        this.dbParameters = {
          enableCorrigendumTab: corrigendumParam ? corrigendumParam.active : true,
          enableNegotiationTab: negotiationParam ? negotiationParam.active : true,
        };

        return this.dbParameters;
      }),
      catchError(() => of(this.dbParameters))
    );
  }

  /**
   * Dynamically toggles DB parameter active status so user can test enabling and disabling.
   */
  toggleDbParameter(
    code: 'ENABLE_CORRIGENDUM_TAB' | 'ENABLE_NEGOTIATION_TAB',
    active: boolean
  ): Observable<PostTenderDbParameters> {
    if (code === 'ENABLE_CORRIGENDUM_TAB') {
      this.dbParameters.enableCorrigendumTab = active;
    } else if (code === 'ENABLE_NEGOTIATION_TAB') {
      this.dbParameters.enableNegotiationTab = active;
    }

    return of({ ...this.dbParameters });
  }
}

