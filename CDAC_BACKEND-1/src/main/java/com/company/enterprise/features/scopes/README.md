# Scopes module

The Scopes feature is isolated under `com.company.enterprise.features.scopes` on the backend and `src/app/features/scopes` on the frontend.

## Data model

- `WorkDetails` is the parent record. Its generated `work_id` is the primary key and it stores `sub_work_details`.
- `WorkScopes` stores one or more scope rows for a work record, linked by `work_id`.
- `BridgeActivity` is stored as a string enum: `REPAIR_TO_WORK`, `NEW_ACTIVITY`, `MAINTENANCE`, or `OTHER_ACTIVITY`.

## API

All endpoints use `/api/scopes/work-details`:

- `GET /api/scopes/work-details` and `GET /api/scopes/work-details/{workId}`
- `POST /api/scopes/work-details`
- `PUT /api/scopes/work-details/{workId}`
- `DELETE /api/scopes/work-details/{workId}` (also deletes its scope rows)

Create and update requests include a `subWorkDetails` string and a non-empty `workScopes` array.

## Porting or removing the feature

To remove it from this application, remove the frontend route and sidebar item, then remove the `features/scopes` folders from the frontend and backend. The backend relies only on the existing Spring Web, JPA, Validation, Lombok, `ApiResponse`, and `ResourceNotFoundException` support; map the latter two to the equivalent shared classes in a destination application. The existing Nginx `/api/` proxy already forwards the Scopes endpoints, so no feature-specific proxy rule is needed.
