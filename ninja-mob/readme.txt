PUT (or POST) /hyperion/<application>/<account>/
Content-Type: application/json

{
 '<service>' : { …profile data… },
 '<service>' : { …profile data… }
}

 curl -si -XPUT -d '{"twitter""id":"456"},"foursquare""id":789}}'
 -H 'Content-Type: application/json' 'http://localhost:5000/hyperion/abc/123/'

 Where abc is appname (constants can be defined within the app)
 123 is singly ID

 First draft of the analytics server. It's a Python Flask app. You just need to install flask and redis.

 Either:
 $ easy_install flask redis
 or
 $ pip install flask redis

 Then to run it:
 $ python hyperion.py

 To update:
 $ curl -si -XPUT -d '{"twitter":{"id":"456"},"foursquare":{"id":789}}' -H 'Content-Type: application/json' 'http://localhost:5000/hyperion/abc/123/'
 HTTP/1.0 200 OK
 Content-Type: text/html; charset=utf-8
 Content-Length: 0
 Server: Werkzeug/0.8.3 Python/2.6.7
 Date: Fri, 28 Sep 2012 21:50:01 GMT

 For analytics:
 $ curl -si 'http://localhost:5000/hyperion/abc/'
 HTTP/1.0 200 OK
 Content-Type: application/json
 Content-Length: 65
 Server: Werkzeug/0.8.3 Python/2.6.7
 Date: Fri, 28 Sep 2012 21:51:23 GMT

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
 Server: Werkzeug/0.8.3 Python/2.6.7
 Date: Fri, 28 Sep 2012 21:59:16 GMT

 {
   "demographics": {
     "gender": "male"
   }
 }
