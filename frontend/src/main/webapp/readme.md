# Requirements

* nodejs 6+
* npm 3+

# Install
Only installs dependencies.

```
npm install
```

# Build production bundle
Environment vars should be set.
Default is development bundle.
Production bundle has compressed code and enabled Angular production mode https://angular.io/api/core/enableProdMode.

```
_USE_PROD_MODE_=[0, 1, false, true] _JAVA_HOST_='//hostname:port' _LANG_=[en, ru] npm run build
```
Current base directory will be saved in **webapp/.base** file.

# Build development bundle
Defaults are:
* \_USE\_PROD\_MODE\_ = false
* \_JAVA\_HOST\_ = '//localhost:55080'
* \_LANG\_ = 'ru'

```
npm run build
```

# Test bundle

```
npm run test-server
```

# Run development server
Next command will create a bundle in memory (has not save it to disk), run web server and watch for changes.

```
npm start
```
