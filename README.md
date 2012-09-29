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
    Content-Length: 404

    {
      "demographics": {
        "locale": {
          "unknown": 0, 
          "en_US": 1
        }, 
        "gender": {
          "unknown": 0, 
          "male": 1
        }, 
        "location": {
          "unknown": 0, 
          "Chicago": 1
        }, 
        "language": {
          "English|Japanese": 1, 
          "unknown": 0
        }, 
        "timezone": {
          "-5:00": 1, 
          "unknown": 0
        }
      }, 
      "accounts": 1, 
      "events": [
        "foobar"
      ]
    }

For event analytics:

    $ curl -si 'http://localhost:5000/event/ninja-mob/foobar/'
    HTTP/1.0 200 OK
    Content-Type: application/json
    Content-Length: 12739

    {
      "datapoints": [
        [
          1348873200000, 
          0
        ], 
        [
          1348873500000, 
          0
        ], 
        [
          1348873800000, 
          0
        ],
        ...
        [
          1348958700000, 
          1
        ], 
        [
          1348959000000, 
          3
        ], 
        [
          1348959300000, 
          1
        ], 
        [
          1348959600000, 
          2
        ]
      ]
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
