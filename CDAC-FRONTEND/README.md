# CDACFRONTEND

This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 21.2.25.

## Application structure

The app uses NgModules with a root module, a shared layout, and lazy-loaded feature modules:

- `src/app/core` contains application-wide infrastructure; `CoreModule` provides HTTP.
- `src/app/shared` contains reusable Angular imports shared by feature modules.
- `src/app/layout` contains the main shell plus separate header, sidebar, and footer components.
- `src/app/features` contains the WMS, AMS, MIS, HRMS, EBILL, EMB, and UMS features. Each feature owns its routes, pages, and models; WMS also owns its service.

The existing module URLs are preserved: `/wms`, `/wmsdemo`, `/mis`, `/ams`, `/ebill`, `/emb`, `/hrms`, and `/ums`. The application root redirects to `/wms`.

The WMS feature calls the Spring Boot API at `/api/work`. The Angular development server uses `proxy.conf.json` to forward API requests to `http://localhost:8080`; start the backend before using WMS CRUD or audit history.

## Development server

To start a local development server, run:

```bash
ng serve
```

Once the server is running, open your browser and navigate to `http://localhost:4200/`. The application will automatically reload whenever you modify any of the source files.

## Code scaffolding

Angular CLI includes powerful code scaffolding tools. To generate a new component, run:

```bash
ng generate component component-name
```

For a complete list of available schematics (such as `components`, `directives`, or `pipes`), run:

```bash
ng generate --help
```

## Building

To build the project run:

```bash
ng build
```

This will compile your project and store the build artifacts in the `dist/` directory. By default, the production build optimizes your application for performance and speed.

## Running unit tests

To execute unit tests with the [Vitest](https://vitest.dev/) test runner, use the following command:

```bash
ng test
```

## Running end-to-end tests

For end-to-end (e2e) testing, run:

```bash
ng e2e
```

Angular CLI does not come with an end-to-end testing framework by default. You can choose one that suits your needs.

## Additional Resources

For more information on using the Angular CLI, including detailed command references, visit the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.
