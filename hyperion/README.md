# Hyperion
Hyperion is Python Flask app that uses redis as a store. It receives data from clients then servers the data up to analytics and ad servers.

## Setup
For python you just need to install flask and redis packages.

Either:

	$ easy_install flask redis
or

	$ pip install flask redis

## Running

Then to run it:

	$ python hyperion.py

To update profile:

	$ curl -si -XPUT -d '{"twitter":{"id":"456"},"foursquare":{"id":789}}' -H 'Content-Type: application/json' 'http://localhost:5000/hyperion/abc/123/'
	HTTP/1.0 200 OK
	Content-Type: text/html; charset=utf-8
	Content-Length: 0

For analytics:

	$ curl -si 'http://localhost:5000/hyperion/abc/'
	HTTP/1.0 200 OK
	Content-Type: application/json
	Content-Length: 65

	{
	  "demographics": {
	    "gender": {
	      "male": 1
	    }
	  }
	}

For ad server:

	$ curl -si 'http://localhost:5000/hyperion/abc/123/'
	HTTP/1.0 200 OK
	Content-Type: application/json
	Content-Length: 48

	{
	  "demographics": {
	    "gender": "male"
	  }
	}
