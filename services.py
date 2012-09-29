from datetime import datetime
from itertools import groupby
from json import loads as json_decode

MILLISECONDS_PER_MINUTE = 60 * 1000
MILLISECONDS_PER_5_MINUTE = 5 * MILLISECONDS_PER_MINUTE
MILLISECONDS_PER_DAY = 24 * 60 * MILLISECONDS_PER_MINUTE

class Aggregator(object):
  @classmethod
  def rollup(cls, now, events, bucket_size=MILLISECONDS_PER_5_MINUTE):
    rollup = dict((key,len(list(values)))
      for key,values in groupby(events, key=lambda x: align(x, bucket_size)))
    return [(x,rollup.get(x,0))
      for x in xrange(align(now - MILLISECONDS_PER_DAY, bucket_size), align(now + bucket_size, bucket_size), bucket_size)]
  @classmethod
  def merge(cls, events):
    pass

def align(value, bucket_size):
  return (value / bucket_size) * bucket_size

ServiceRegistry = {}

class NoopService(object):
  @classmethod
  def normalize(cls, data):
    return {}
ServiceRegistry['noop'] = NoopService

class BaseService(object):
  MAPPINGS = {}
  @classmethod
  def normalize(cls, data):
    result = {}
    for name, function in cls.MAPPINGS.iteritems():
      value = safe_expr(function, data)
      if value is not None:
        result[name] = value
    return result

class FacebookService(BaseService):
  MAPPINGS = {
    'age'      : lambda data : (datetime.now() - datetime.strptime(data['birthday'], '%m/%d/%Y')).days / 365,
    'gender'   : lambda data : data['gender'],
    'language' : lambda data : '|'.join(x['name'] for x in json_decode(data['languages'])),
    'locale'   : lambda data : data['locale'],
    'location' : lambda data : json_decode(data['location'])['name'],
    'timezone' : lambda data : '%s:00' % data['timezone']
  }
ServiceRegistry['facebook'] = FacebookService

class FlickrService(BaseService):
  MAPPINGS = {
    'location' : lambda data : location.partition(',')[0],
    'country'  : lambda data : {'USA' : 'US'}.get(location.rpartition(', ')[-1]),
    'timezone' : lambda data : json_decode(data['timezone'])['offset']
  }
ServiceRegistry['flickr'] = FlickrService

class GithubService(BaseService):
  MAPPINGS = {
    'location' : lambda data : data['location']
  }
ServiceRegistry['github'] = GithubService

class LinkedInService(BaseService):
  MAPPINGS = {
    'location' : lambda data : location['name'],
    'country'  : lambda data : location['country']['code'].upper()
  }
ServiceRegistry['linkedin'] = LinkedInService

def safe_expr(function, *args, **kwargs):
  try:
    return function(*args, **kwargs)
  except Exception:
    pass
