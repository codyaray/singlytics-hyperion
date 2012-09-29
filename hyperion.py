#!/usr/bin/env python

from collections import defaultdict
from flask import Flask, jsonify, request, Response
from logging import getLogger
from redis import from_url as Redis
from services import ServiceRegistry
from time import time
from uuid import uuid4

import os

REDIS_URL = os.environ.get('REDISTOGO_URL', 'redis://localhost:6379')

# Legend for Redis
# "hm:${hyperion}" => {application => account}
# "am:${application}:${account}" => {service => uid}
# "al:${application}" => {account => hyperion}
# "sl:${service}" => {uid => hyperion}
# "dd:${application}:${account}" => {demographic data}
# "td:${application}:${account}" => SortedSet(timestamp as score, string value)

app = Flask(__name__)
db = Redis(REDIS_URL, db=1)
log = getLogger(__name__)

@app.route('/analytics/<application>/', methods=['GET'])
def hyperion_analytics(application):
  demographics = defaultdict(lambda: defaultdict(int))
  accounts = db.hkeys('al:%s' % application)
  for account in accounts:
    result = db.hgetall('dd:%s:%s' % (application,account))
    for key, value in result.iteritems():
      if key != 'timestamp':
        demographics[key][value] += 1
  return jsonify(demographics=demographics)

@app.route('/profile/<application>/<account>/', methods=['POST', 'PUT'])
def hyperion_profile_update(application, account):
  hyperion_id = db.hget('al:%s' % application, account)
  if hyperion_id is None:
    log.debug('Starting reverse lookup for (%s,%s)', application, account)
    for service, meta in (request.json or {}).iteritems():
      uid = meta.get('id')
      hyperion_id = db.hget('sl:%s' % service, uid)
      if hyperion_id is not None:
        log.debug('Found match of (%s,%s) to %s', service, uid, hyperion_id)
        break
  if hyperion_id is None:
    hyperion_id = uuid4()
  db.hset('al:%s' % application, account, hyperion_id)
  db.hset('hm:%s' % hyperion_id, application, account)
  demographics = {'timestamp' : int(time())}
  for service, meta in (request.json or {}).iteritems():
    uid = meta.get('id')
    db.hset('am:%s:%s' % (application,account), service, uid)
    db.hset('sl:%s' % service, uid, hyperion_id)
    demographics.update(ServiceRegistry.get(service, 'noop').normalize(meta))
  db.hmset('dd:%s:%s' % (application,account), demographics)
  return Response(status=200)

@app.route('/event/<application>/<account>/<event>/', methods=['POST', 'PUT'])
def hyperion_event(application, account, event):
  return Response(status=200)

@app.route('/profile/<application>/<account>/', methods=['GET'])
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
  return Response(status=404)

if __name__ == '__main__':
  debug = False
  if os.environ.get('DEBUG', 'false').lower() == 'true':
    from logging import basicConfig, DEBUG
    basicConfig(
      level=DEBUG,
      format='%(asctime)s %(levelname)-8s %(name)s (%(funcName)s:%(lineno)d) %(message)s',
      datefmt='%Y.%m.%d-%H:%M:%S'
    )
    debug = True
  port = int(os.environ.get('PORT', 5000))
  app.run(host='0.0.0.0', port=port, debug=debug)
