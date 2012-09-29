# Hyperion
Hyperion is Python Flask app that uses redis as a store. It receives data from clients then servers the data up to analytics and ad servers.

## Public Endpoint 

The service is publicly available at:

	http://singlytics-hyperion.herokuapp.com/

## Local Development

### Setup
For python you just need to install flask and redis packages.

Either:

	$ easy_install flask redis
or

	$ pip install flask redis

### Running

Then to run it:

	$ python hyperion.py

You can then access it via:

	http://localhost:5000/

## Using the service

To update profile:

	$ curl -si -XPUT -d '{"twitter":{"id":"456"},"foursquare":{"id":789}}' -H 'Content-Type: application/json' 'http://singlytics-hyperion.herokuapp.com/profile/myapp/aprofile/'
	HTTP/1.0 200 OK
	Content-Type: text/html; charset=utf-8
	Content-Length: 0

To update event:

	$ curl -si -XPUT 'http://singlytics-hyperion.herokuapp.com/event/myapp/aprofile/myevent/?context=foobar'
	HTTP/1.0 200 OK
	Content-Type: text/html; charset=utf-8
	Content-Length: 0

For analytics:

	$ curl -si 'http://singlytics-hyperion.herokuapp.com/analytics/myapp/'
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

	$ curl -si 'http://singlytics-hyperion.herokuapp.com/profile/myapp/aprofile/'
	HTTP/1.0 200 OK
	Content-Type: application/json
	Content-Length: 48

	{
	  "demographics": {
	    "gender": "male"
	  }
	}
