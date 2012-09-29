from json import loads as json_decode

ServiceRegistry = {}

class NoopService(object):

  @classmethod
  def normalize(cls, data):
    return {}

ServiceRegistry['noop'] = NoopService

class FacebookService(object):
  @classmethod
  def normalize(cls, data):
    return {
      'gender' : data['gender'],
      'language' : '|'.join(x['name'] for x in json_decode(data['languages'])),
      'locale' : data['locale'],
      'location' : json_decode(data['location'])['name'],
      'timezone' : '%s:00' % data['timezone']
    }
ServiceRegistry['facebook'] = FacebookService

#flickr {u'timezone': u'{"offset":"-06:00","label":"Central Time (US & Canada)"}', u'location': u'{"_content":"Chicago, USA"}'}
class FlickrService(object):
  @classmethod
  def normalize(cls, data):
    return {
      'timezone' : json_decode(data['timezone'])['offset']
    }
ServiceRegistry['flickr'] = FlickrService

#github {u'location': u'Chicago', u'email': u'ninja@nin.ja'}
class GithubService(object):
  @classmethod
  def normalize(cls, data):
    return {}
ServiceRegistry['github'] = GithubService

#linkedin {u'location': u'{"name":"Greater Chicago Area","country":{"code":"us"}}'}
class LinkedInService(object):
  @classmethod
  def normalize(cls, data):
    return {}
ServiceRegistry['linkedin'] = LinkedInService


