[![Build Status](https://travis-ci.org/dekkr/pagefetcher.svg?branch=master)](https://travis-ci.org/dekkr/pagefetcher)
# Page Fetcher

A simple micro service that fetches the content of a web page.

This initial version is no more than a proxy; later editions will feature a page cache and tidying the content.

url
 
> http://localhost:8080/v1/page?

parameters

> url=http://www.google.com  \[valid uri, required]

> maxAge=60                  \[number of minutes, Optional, not implemented] 

> raw=false                  \[processing of the page, Optional, not implemented]
