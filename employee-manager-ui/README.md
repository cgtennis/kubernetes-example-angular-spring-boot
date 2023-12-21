# Employeemanagerapp

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 9.1.5.

Node.js version check: `node -v` v18.17.1
Angular CLI version check: `ng version`: Angular CL 9.1.1

### manual build
```
npm install --force
```

### local launch
```
ng serve
```

### resolve build error `opensslErrorStack: [ 'error:03000086:digital envelope routines::initialization error' ]`
https://stackoverflow.com/questions/74726224/opensslerrorstack-error03000086digital-envelope-routinesinitialization-e

Option 2. Open a terminal and paste these as described:
Linux and macOS (Windows Git Bash): `export NODE_OPTIONS=--openssl-legacy-provider`
Windows command prompt: `set NODE_OPTIONS=--openssl-legacy-provider`
Windows PowerShell: `$env:NODE_OPTIONS = "--openssl-legacy-provider`