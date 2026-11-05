// Stand-in for the menu the backend sends in QMS (module > submodule > function).
// Same field names as the reference, so only this list is dropped at integration.
export interface MenuFunction {
  name: string;
  path: string;
  functionCode: string;
  // shown in the menu but not built yet in this application
  disabled?: boolean;
}

export interface MenuSubmodule {
  name: string;
  functions: MenuFunction[];
}

export interface MenuModule {
  name: string;
  moduleCode: string;
  submodules: MenuSubmodule[];
}

export const MENU_MODULES: MenuModule[] = [
  { name: 'Profile', moduleCode: 'Profile', submodules: [] },
  { name: 'Master', moduleCode: 'Master', submodules: [] },
  { name: 'HRMS', moduleCode: 'HRMS', submodules: [] },
  {
    name: 'WMS',
    moduleCode: 'WMS',
    submodules: [
      { name: 'Master', functions: [] },
      {
        name: 'Work and Sub Work',
        functions: [
          { name: 'Work Details', path: 'wms/work-details', functionCode: 'WorkDetails', disabled: true },
          { name: 'Sub Work Details', path: 'wms/sub-work-details', functionCode: 'SubWorkDetails', disabled: true },
        ],
      },
      {
        name: 'Tender',
        functions: [
          { name: 'Pre-Tender', path: 'wms/pre-tender/add', functionCode: 'PreTender' },
          { name: 'Post-Tender', path: 'wms/post-tender', functionCode: 'PostTender' },
        ],
      },
    ],
  },
  { name: 'e-MB', moduleCode: 'eMB', submodules: [] },
  { name: 'eBill', moduleCode: 'eBill', submodules: [] },
  { name: 'AMS', moduleCode: 'AMS', submodules: [] },
  { name: 'Um Module', moduleCode: 'UM', submodules: [] },
  { name: 'MIS', moduleCode: 'MIS', submodules: [] },
  {
    name: 'QMS',
    moduleCode: 'qms',
    submodules: [
      {
        name: 'Master',
        functions: [{ name: 'Item', path: 'qms/item', functionCode: 'Item' }],
      },
      {
        name: 'Work Assignment',
        functions: [{ name: 'Work Assignment', path: 'qms/work-assignment/add', functionCode: 'WorkAssignment' }],
      },
      {
        name: 'Work Inspection',
        functions: [
          { name: 'Work Inspection and Reporting', path: 'qms/work-inspection/add', functionCode: 'WorkInspectionAndReporting' },
        ],
      },
    ],
  },
];
