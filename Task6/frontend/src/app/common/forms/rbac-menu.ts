import { Message } from './message';

// The answer of GET /rbac/menu (same fields as the backend RbacMenu). Every screen of the user's roles is in the tree,
// with the operations the user may do on it; a screen with no operations is greyed out.
export interface RbacFunction {
  functionId: number;
  functionCode: string;
  name: string;
  path: string;
  operations: string[];
}

export interface RbacSubmodule {
  name: string;
  sortOrder: number;
  functions: RbacFunction[];
  submodules: RbacSubmodule[];
}

export interface RbacModule {
  moduleCode: string;
  name: string;
  sortOrder: number;
  submodules: RbacSubmodule[];
}

export interface RbacMenu {
  userId: number | null;
  username: string | null;
  valid: boolean;
  message: Message | null;
  noAccessMessage: string;
  modules: RbacModule[];
}
