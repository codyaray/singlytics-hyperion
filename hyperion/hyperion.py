#!/usr/bin/env python

from flask import Flask
from redis import Redis

# Legend for Redis
# "hm:${hyperion}" => {application => account}
# "am:${application}:${account}" => {service => uid}
# "al:${application}" => {account => hyperion}
# "sl:${service}" => {uid => hyperion}
# "dd:${application}:${account}" => {demographic data}
# "td:${application}:${account}" => SortedSet(timestamp as score, string value)

app = Flask(__name__)
db = Redis(db=1)

@app.route('/hyperion/<application>/', methods=['GET'])
def hyperion_analytics(application):
  pass

@app.route('/hyperion/<application>/<account>/', methods=['POST', 'PUT'])
def hyperion_profile_update(application, account):
  pass

@app.route('/hyperion/<application>/<account>/', methods=['GET'])
def hyperion_profile_retrieval(application, account):
  pass

if __name__ == '__main__':
  app.run(host='0.0.0.0', debug=True)
