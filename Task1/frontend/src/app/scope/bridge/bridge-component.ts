export interface BridgeComponent {
  id: number;
  riverNallahBarrier: string;
  width: number | null;
  length: number | null;
  approvalRoad: string;
  numberOfSpans: number | null;
  spanLength: number | null;
  bridgeActivity: string;
  otherActivityDescription: string;
  officeId: number | null;
  workId: number | null;
  createdAt: string | null;
  updatedAt: string | null;
  revisionTimestamp: string | null;
}

export type BridgeComponentFormValue = Omit<
  BridgeComponent,
  'id' | 'officeId' | 'workId' | 'createdAt' | 'updatedAt' | 'revisionTimestamp'
>;
export type BridgeComponentRequest = BridgeComponentFormValue &
  Pick<BridgeComponent, 'officeId' | 'workId'>;

export interface BridgeAuditRevision {
  revisionNumber: number;
  revisionDate: string;
  revisionType: 'CREATED' | 'UPDATED' | 'DELETED';
  snapshot: BridgeComponent;
}
