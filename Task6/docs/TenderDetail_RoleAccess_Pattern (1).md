# Tender-detail pattern for Pre-Tender and Post-Tender

Date: 2026-11-04. Read-only analysis; no code was changed.

Paths are relative to `wamis-master/wamis-master/`.
FE = `AMSModule/src/app/`, BE = `AMS-Backend/src/main/java/cdac/egov/ams/`.

## 1. Summary

Tender-detail has **no Angular route guard doing real work**. Access is role-based because:

1. User-management (UM) assigns **operations** (e.g. `Add Tender`, `View Tender`, `Edit Tender`, `Delete Tender`) to a role, and the menu only carries the operations the logged-in user's role has.
2. The menu click stores the clicked **function** (with its `operations[]`) in `CurrentFunctionService`.
3. The shell component builds the **Add / View tabs** from operation *names*.
4. Every button / API call looks up its operation by name, and if missing shows *"... operation is not assigned."* and does nothing.
5. Each HTTP call sends that operation's **operationId** inside the encrypted `uuid` header via `getHeaderDetails(operationId, toBeLogged)`.
6. The backend `JwtAuthorizationFilter` forwards the token + `uuid` to UM `/validatetoken`; the service layer then reads `HeaderData` (userId, officeId, tenantId, operation) to scope data and stamp audit fields.

Caveats found:
- `AuthGuardService.checkLogin()` returns `true` and `AccessGuardService.canActivateChild()` always returns `true` (`FE/common/security/guards/`). They are not on the tender routes anyway. Deep-linking to `/home/wms/work/tender/add` without going through the menu leaves `currentFunction` undefined.
- The backend tender controller has **no per-endpoint operation check** of its own. Whether UM `/validatetoken` rejects an operation not on the user's role lives in the UM service, which is not in this workspace. Please confirm with the UM team.
- `ControllerSecurityAspect` pointcut is `execution(* cdac.egov.ams.*.controllers.*Controller.*(..))` (one package level), so it does **not** match `wms.work.tenderdetail.controllers`.

## 2. Routing chain (frontend)

| Step | File | What happens |
|---|---|---|
| Menu click | `FE/home/menu-bar/menu-bar.component.ts:271` `showFunction()` (also `FE/home/submenu-bar/submenu-bar.component.ts:72`) | `currentFunctionService.currentFunction.next(functions)`, then splits `functions.path` (e.g. `work/tender`) and navigates to `['home','wms', ...pathSegments]` **only if** `functionCode` is in the WMS list (`CONSTANTS.TENDER_DTL` etc.). In non-dev it first calls `userDetailsService.validateRouter()`; a false result logs the user out. |
| Function code | `FE/home/ams-module/ams-common/module-constants.ts:101` | `TENDER_DTL : 'TENDER'` |
| Home routes | `FE/home/home-routing.module.ts` | `home` → `MenuBarComponent` → `wms` lazy loads `WmsModule` |
| WMS routes | `FE/home/wms/wms-routing.module.ts` | `work` → `WorkRoutingModule` |
| Work routes | `FE/home/wms/work/work-routing.module.ts` | `tender` → lazy `TenderDetailModule` |
| Tender routes | `FE/home/wms/work/tender-detail/tender-detail-routing.module.ts` | `''` → `TenderDetailComponent` with children `add` → `TenderDetailAddComponent`, `view` → `TenderDetailViewComponent` |
| Shell | `FE/home/wms/work/tender-detail/tender-detail.component.ts` | Reads `getCurrentFunction()`, pushes `'add'` if an operation named `Add Tender` exists and `'view'` for `View Tender`; navigates to the first available tab. |
| Shell template | `tender-detail.component.html` | `mat-tab-nav-bar` loops `tabsList`, so a user without `Add Tender` never sees the Add tab. |
| Module | `tender-detail.module.ts` | Declares all components and imports `TenderDetailRoutingModule`, `MaterialModule`, `AmsCommonModule`, `TranslateModule.forChild(isolate)`, and the date adapter providers. |
| Edit jump | `FE/services/dynamic-route.service.ts:272` case `Tender_Add` | Imports `generateDynamicRoute()` from `tender-detail-add.component.ts:703` → `['/','home','wms','work','tender','add']`. |

## 3. Operation names gate tabs and CRUD (frontend)

Model: `FE/common/forms/operation.ts` (`operationId`, `name`, ...) and `FE/common/forms/function.ts` (`path`, `operations[]`).

Pattern used everywhere:

```ts
this.currentFunction = this.currentFunctionService.getCurrentFunction();
this.operations = this.currentFunction.operations;
const op = this.operations.find(o => o.name === 'Add Tender');
if (op?.operationId) { this.httpService.save(obj, op.operationId)... }
else { this.snackBarService.showCustomMessageSnackbar('Add Tender operation is not assigned.', 'text-danger'); }
```

| Action | Operation name | Where |
|---|---|---|
| Add tab | `Add Tender` | `tender-detail.component.ts:60` |
| View tab | `View Tender` | `tender-detail.component.ts:63` |
| Save + dropdown loads (tender types, HSN/SAC, split works) | `Add Tender` | `tender-detail-add.component.ts:258, 291, 324, 367, 488` |
| Update | `Edit Tender` | `tender-detail-add.component.ts:434` |
| Edit icon / action column | `Edit Tender` | `tender-detail-view.component.ts:163` `showOperationwiseButtonProperty()` sets `showEditIcon`, and the HTML uses `*ngIf="showEditIcon"` (`tender-detail-view.component.html:171, 238`) |
| Delete icon + delete call | `Delete Tender` | `tender-detail-view.component.ts:172` (`showDeleteIcon`) and `:395` `delete()`; HTML `:175, 242` |
| List / search / audit | `View Tender` | `tender-detail-view.component.ts:226` (`forScreen: 'Tender - View'`), `:477` search, `:598` audit + `getServerToken` |
| Child screens | `Add Awarding`, `View Awarding` | `tender-detail-add.component.ts:598, 632` |

Edit flow: `editItem()` pushes the row into `TenderDataCommService.tenderForView`, sets `pageFlagChangeForTenders` to `'Edit'` or `'View'`, and routes to Add. The Add component subscribes to that flag (`tender-detail-add.component.ts:139`) to prefill and disable or enable the form.

Services (`FE/home/wms/work/tender-detail/services/`):
- `tender-http.service.ts`: one method per endpoint, each takes `operationId`.
- `tender-pull-data.service.ts`: switches on `forScreen` (`'Tender - View'` → `view`, `'Tender - Search'` → `search`) for `CustomDataSource` paging.
- `tender-data-comm.service.ts`: BehaviorSubjects shared between Add and View.

## 4. getHeaderDetails(operationId, toBeLogged)

`FE/services/common.service.ts:386`

```ts
new HttpHeaders({
  'uuid': this.encryptObject(this.createHeaders(opId)),
  'Content-Type': 'application/json',
  'Log-Enabled': toBeLogged.toString(),
});
```

`createHeaders()` (`common.service.ts:194-230`) builds an `&`-joined string. For a normal user it is:
`userId & operationId & unitId(officeId) & tenantId & username & designation & name & accountTypeId & cashBookId & selectedOfficeId & stateCode`.
AG and SE users get a variant with an extra flag and the office in position 2.

`toBeLogged` is `true` for save/update/delete/view/search (audit log), and `false` for lookups (dropdowns, audit fetch, document checks).

## 5. Backend

| Layer | File | Role |
|---|---|---|
| Auth filter | `BE/security/jwt/filters/JwtAuthorizationFilter.java:55-96` | For every request with a Bearer token it calls `commonService.isValidToken(req)`. 200 means authenticated, 401 sets `expires: tokenExpire`. |
| Token + operation validation | `BE/common/services/CommonService.java:702` `isValidToken()` | POSTs the token to `${sso.apiurl}/validatetoken` with the **same `uuid` header** (op id inside), plus `Log-Enabled` and session cookie. UM validates (and logs) there. |
| Header parsing | `BE/common/forms/AmsCommon.java:104` `getHeaderData(req)` | Decrypts `uuid` and splits into `HeaderData`: `userId`[0], `operation`[1], `officeId`[2], `tenantId`[3], `name`, `designation`, `empName`, `accountTypeId`, `stateCode`. |
| Op → function code (optional) | `BE/common/services/CommonUMService.java:357` `findByOperationId(req, "/getoperation", opId)` | Used in `DocUploadController`/`DocumentController` to resolve the function code server-side. Use it for documents. |
| Controller | `BE/wms/work/tenderdetail/controllers/TenderDetailsController.java` | `@RequestMapping("/tender")`: `GET /tendertype`, `GET /splitworks`, `POST /save`, `/update`, `/delete`, `/display`, `/search`, `/auditdetails`, `/checkdocumentforpretender`. Each wraps the service in try/catch → `MessageService`/`ExceptionHandlerService`. |
| Service | `BE/wms/work/tenderdetail/services/TenderDetailsService.java` | `save` (`:191`): id from `IdGenerator`, `tenantId` from header, `operatedBy = userId`, status `REGISTER`. `delete` (`:316`) only for REGISTER/MODIFY and blocks if awarding or NIT exist. `view` (`:362`) is scoped by `tenantId` and `officeId` from the header. |
| Validation | `services/TenderDetailsValidationService.java` | Form and search validation into `MessageService`. |
| Other | `entities/`, `repositories/`, `forms/` (`PaginatorModel`, `TendersViewReturn`, `Tender`), `constants/TenderDetailsConstants.java` | Standard layering. |

Role access on the backend is therefore: valid token, the operation id in `uuid` (validated by UM), plus data scoping by office and tenant and status rules in the service.

## 6. Step-by-step: build Pre-Tender and Post-Tender the same way

### A. User-management (already partly done)

1. Functions: **Pre-Tender** path `work/pretender`, **Post-Tender** path `work/posttender`, each with a unique `functionCode` (e.g. `PRE_TENDER`, `POST_TENDER`). Please confirm the exact values you entered.
2. Operations per function, with exact names because the frontend matches on name:
   - Pre-Tender: `Add Pre-Tender`, `View Pre-Tender`, `Edit Pre-Tender`, `Delete Pre-Tender` (add `Publish Pre-Tender` if publishing to GePNIC is a separate permission).
   - Post-Tender: `View Post-Tender`, `Pull Post-Tender`, `Retry Post-Tender` (add `Add`/`Edit` only if there are manual entries).
3. Map the operations to roles (for example, a JE gets Add/View and an EE gets View/Edit/Delete).

### B. Frontend (per feature; Pre-Tender shown, Post-Tender identical)

1. `FE/home/ams-module/ams-common/module-constants.ts`: add `PRE_TENDER: 'PRE_TENDER'`, `POST_TENDER: 'POST_TENDER'` (must equal the UM function codes).
2. `FE/home/menu-bar/menu-bar.component.ts` **both** WMS `if` lists (dev ~line 285 and prod ~line 315): add `|| functions.functionCode === CONSTANTS.PRE_TENDER || ... POST_TENDER`. Otherwise the click falls through to `home/ams/...` and you get a 404. Also update `submenu-bar.component.ts` (it matches on `functions.name`, so add `'Pre-Tender'` and `'Post-Tender'`).
3. `FE/home/wms/work/work-routing.module.ts`: add `{ path: 'pretender', loadChildren: () => import('./pretender/pretender.module').then(m => m.PretenderModule) }` and the same for `posttender`, above the `**` route.
4. Create `FE/home/wms/work/pretender/`:
   - `pretender-routing.module.ts`: `''` → `PretenderComponent`, children `add` and `view` (copy `tender-detail-routing.module.ts`).
   - `pretender.component.ts/.html`: copy the tender-detail shell, matching `'Add Pre-Tender'` and `'View Pre-Tender'`.
   - `pretender-add/`: in `ngOnInit` read `operations`; `save()` uses `Add Pre-Tender`, `update()` uses `Edit Pre-Tender`, and dropdowns use `Add Pre-Tender`. Subscribe to `pageFlagChangeForPreTender` for Edit/View prefill. Export `generateDynamicRoute()` returning `['/','home','wms','work','pretender','add']`.
   - `pretender-view/`: `showOperationwiseButtonProperty()` for `Edit Pre-Tender` / `Delete Pre-Tender` (push the `action` column); list/search/audit use `View Pre-Tender`; `editItem()` sets the comm-service subjects and calls `dynamicRouteService.navigateToDynamicRoute('PreTender_Add')`.
   - `services/pretender-http.service.ts`: one method per endpoint, each `(model, operationId)` using `commonService.getHeaderDetails(operationId, true|false)`, with URLs `pretender/save|update|delete|display|search|auditdetails`.
   - `services/pretender-pull-data.service.ts`: `forScreen` `'PreTender - View'` / `'PreTender - Search'`.
   - `services/pretender-data-comm.service.ts`: `preTenderForView`, `pageFlagChangeForPreTender`, `forModification`.
   - `pretender.module.ts`: copy the imports and providers from `tender-detail.module.ts`.
5. `FE/services/dynamic-route.service.ts`: add `case 'PreTender_Add'` and `case 'PostTender_Add'` (if needed).
6. Post-Tender: same skeleton. The View tab lists pulled tenders (View op), the "Pull from GePNIC" button is gated by `Pull Post-Tender`, Retry in transaction history by `Retry Post-Tender`, and AOC download uses `View Post-Tender` with `toBeLogged=false`.
7. Recommended hardening (not in tender-detail): in the shell `ngOnInit`, if `getCurrentFunction()` is undefined, navigate to `home`. This stops the crash on deep links or refresh.

### C. Backend (`BE/wms/work/pretender/` and `BE/wms/work/posttender/`)

1. Packages `controllers/ entities/ repositories/ forms/ services/ constants/`, mirroring `tenderdetail`.
2. `PreTenderController` `@RequestMapping("/pretender")`: `save`, `update`, `delete`, `display`, `search`, `auditdetails` with the same try/catch + `MessageService` shape, returning `List<MessageWithId>` / a `ViewReturn`.
3. Service: start every method with `HeaderData data = AmsCommon.getHeaderData(req);`, use `data.getTenantId()` in keys, `data.getOfficeId()` in list queries, `data.getUserId()` for `operatedBy`. Enforce status rules (delete or edit only in REGISTER/MODIFY, block once published).
4. Optional server-side operation check (stronger than tender-detail): resolve `commonUMService.findByOperationId(req, "/getoperation", data.getOperation())` and reject if its `functionCode` is not `PRE_TENDER` (or the operation name does not match the endpoint).
5. Post-Tender controller `/posttender`: `display`, `search`, `pull` (calls the GePNIC client ported from Task4), `aoc/download`, `transactions`, `retry`, all on the same `HeaderData` pattern.
6. Audit: reuse `CommonAuditService.getAuditDetails(model, <AUD_TABLE>, req)` with new audit table constants.
7. DB: add tables/sequences to `SQL Scripts/` and the operation seed rows for UM.

### D. Test checklist

- A role with only `View Pre-Tender` sees only the View tab, no edit or delete icons, and Add is never reachable from the menu.
- Remove `Edit Pre-Tender`: the update button shows "Edit operation is not assigned."
- An API call with a `View` op id hitting `/pretender/save` should be rejected (confirm UM, or add check C.4).
- The list only returns rows for the user's office and tenant.
