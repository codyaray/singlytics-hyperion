from datetime import datetime
from itertools import groupby, ifilter
from json import loads as json_decode

STOPWORDS = frozenset(open('stopwords.english').read().strip().split())

MILLISECONDS_PER_MINUTE = 60 * 1000
MILLISECONDS_PER_5_MINUTE = 5 * MILLISECONDS_PER_MINUTE
MILLISECONDS_PER_DAY = 24 * 60 * MILLISECONDS_PER_MINUTE

class Aggregator(object):
  @classmethod
  def rollup(cls, now, events, bucket_size=MILLISECONDS_PER_5_MINUTE):
    rollup = dict((key,len(list(values)))
      for key,values in groupby(events, key=lambda x: align(x, bucket_size)))
    return [{'time' : x, 'value' : rollup.get(x,0)}
      for x in xrange(align(now - MILLISECONDS_PER_DAY, bucket_size), align(now + bucket_size, bucket_size), bucket_size)]

def align(value, bucket_size):
  return (value / bucket_size) * bucket_size

ServiceRegistry = {}

class NoopService(object):
  @classmethod
  def normalize(cls, data):
    return {}
  @classmethod
  def keywords(cls, data):
    return frozenset()
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
  @classmethod
  def keywords(cls, data):
    return frozenset()

class FacebookService(BaseService):
  MAPPINGS = {
    'age'      : lambda data : (datetime.now() - datetime.strptime(data['birthday'], '%m/%d/%Y')).days / 365,
    'gender'   : lambda data : data['gender'],
    'language' : lambda data : '|'.join(x['name'] for x in data['languages']),
    'locale'   : lambda data : data['locale'],
    'location' : lambda data : data['location']['name'],
    'timezone' : lambda data : '%s:00' % data['timezone']
  }
  @classmethod
  def keywords(cls, data):
    return frozenset(ifilter(lambda w: not w in STOPWORDS, tokenize(data.get('bio', ''))))
ServiceRegistry['facebook'] = FacebookService

class FlickrService(BaseService):
  MAPPINGS = {
    'location' : lambda data : data['location']['_content'].partition(',')[0],
    'country'  : lambda data : {'USA' : 'US'}.get(data['location']['_content'].rpartition(', ')[-1]),
    'timezone' : lambda data : data['timezone']['offset']
  }
ServiceRegistry['flickr'] = FlickrService

class GithubService(BaseService):
  MAPPINGS = {
    'location' : lambda data : data['location']
  }
ServiceRegistry['github'] = GithubService

class LinkedInService(BaseService):
  MAPPINGS = {
    'age'      : lambda data : (datetime.now() - datetime(**data['dateOfBirth'])).days / 365,
    'location' : lambda data : data['location']['name'],
    'country'  : lambda data : data['location']['country']['code'].upper()
  }
ServiceRegistry['linkedin'] = LinkedInService

class TwitterService(BaseService):
  MAPPINGS = {
    'age'      : lambda data : (datetime.now() - datetime(**data['dateOfBirth'])).days / 365,
    'language'  : lambda data : {'en' : 'English'}.get(data['lang']),
    'location' : lambda data : data['location']
  }
  @classmethod
  def keywords(cls, data):
    return frozenset(ifilter(lambda w: w not in STOPWORDS, tokenize(data.get('description', ''))))
ServiceRegistry['twitter'] = TwitterService

def safe_expr(function, *args, **kwargs):
  try:
    return function(*args, **kwargs)
  except Exception:
    pass

def tokenize(value):
  return value.lower().replace('.', '').replace(',', '').replace('!', '').replace('?', '').split()
