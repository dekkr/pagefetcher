[![Build Status](https://travis-ci.org/dekkr/pagefetcher.svg?branch=master)](https://travis-ci.org/dekkr/pagefetcher)
[![Coverage Status](https://coveralls.io/repos/dekkr/pagefetcher/badge.svg)](https://coveralls.io/r/dekkr/pagefetcher)
# Page Fetcher

A simple micro service that fetches the content of a web page.

This initial version is no more than a proxy; later editions will feature a page cache.

url
 
> http://localhost:8080/v1/page?

parameters

> url=http://www.google.com  \[valid uri, required]

> maxAge=60                  \[number of minutes, Optional, defaults to 1440 (1 day)] 

> raw=true                  \[processing of the page, Optional, defaults to false]
