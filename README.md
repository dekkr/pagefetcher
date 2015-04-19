[![Build Status](https://travis-ci.org/dekkr/pagefetcher.svg?branch=master)](https://travis-ci.org/dekkr/pagefetcher)
# Page Fetcher

A simple micro service that fetches the content of a web page.
If the page already exists in the local cache, the cached page will be returned.

The maximum age of a cached page can be specified.

Default, the page content is processed, to ensure valid HTML is returned and all relative links are replaced by absolute links.
This behaviour can be turned off.

## Usage

Example url
 
> http://localhost:8080/v1/page?url=http://www.google.com&maxAge=60&raw=true

parameter | example value | description
----------|---------------|-------------
url | url=http://www.google.com | a valid url \[required]
maxAge | maxAge=60 | maximum age of a cache page, in minutes. \[Optional] 
raw | raw=true  | Disable page processing \[Optional, default =false]


## Configuration

Example of application.conf

```
nl.dekkr.pagefetcher {
  api {
    host = "localhost"
    port = 8080
    timeout = 5 #seconds
  }
  persistence {
    flavor = "postgres"  # postgres / mongo / prevayler / memory
    postgres {
      user = "postgres"
      password = "postgres"
      databaseName = "pagefetcher"
      serverName = "localhost"
      portNumber = 5432
    }
    mongo {
      host = "localhost"
      port = 27017
      db = "db"
      user = "mongo"
      password = "mongo"
    }
  }
}
```

