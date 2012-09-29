#!/usr/bin/env python

from collections import defaultdict
from flask import Flask, jsonify, request, Response
from redis import Redis, WatchError
from time import time
from uuid import uuid4

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
  demographics = defaultdict(lambda: defaultdict(int))
  accounts = db.hkeys('al:%s' % application)
  for account in accounts:
    result = db.hgetall('dd:%s:%s' % (application,account))
    for key, value in result.iteritems():
      if key != 'timestamp':
        demographics[key][value] += 1
  return jsonify(demographics=demographics)

@app.route('/hyperion/<application>/<account>/', methods=['POST', 'PUT'])
def hyperion_profile_update(application, account):
  with db.pipeline() as pipe:
    while True:
      try:
        pipe.watch('al:%s' % application)
        hyperion_id = pipe.hget('al:%s' % application, account)
        pipe.multi()
        if hyperion_id is None:
          hyperion_id = uuid4()
          pipe.hset('al:%s' % application, account, hyperion_id)
        pipe.hset('hm:%s' % hyperion_id, application, account)
        for service, meta in (request.json or {}).iteritems():
          uid = meta.pop('id')
          pipe.hset('am:%s:%s' % (application,account), service, uid)
          pipe.hset('sl:%s' % service, uid, hyperion_id)
          pipe.hmset('dd:%s:%s' % (application,account), {'timestamp' : int(time())})
        pipe.execute()
        break
      except WatchError:
        pass
  return Response(status=200)

@app.route('/hyperion/<application>/<account>/<event>/', methods=['POST', 'PUT'])
def hyperion_event(application, account, event):
  return Response(status=200)

@app.route('/hyperion/<application>/<account>/', methods=['GET'])
def hyperion_profile_retrieval(application, account):
  hyperion_id = db.hget('al:%s' % application, account)
  if hyperion_id is not None:
    applications_to_accounts = db.hgetall('hm:%s' % hyperion_id)
    demographics, timestamp = {}, 0
    for application, account in applications_to_accounts.iteritems():
      result = db.hgetall('dd:%s:%s' % (application,account))
      current = result.pop('timestamp')
      if current > timestamp:
        demographics.update(result)
        timestamp = current
      else:
        result.update(demographics)
        demographics = result
    return jsonify(demographics=demographics)
  return Response(status=400)

if __name__ == '__main__':
  port = int(os.environ.get('PORT', 5000))
  app.run(host='0.0.0.0', port=port)
