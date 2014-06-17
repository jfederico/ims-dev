import logging
from google.appengine.api import memcache
from google.appengine.ext import db
import oauth

# A Partial Model for an OAuth Comsumer Key
class OAuthConsumerKey(db.Model):
  consumer = db.StringProperty()
  secret = db.StringProperty()

class LTI_OAuthDataStore(oauth.OAuthDataStore):

    def __init__(self, web, options):
        self.web = web
        self.options = options

    def lookup_consumer(self, key):
        memkey = 'OAuthConsumerKey-' + key
        secret = memcache.get(memkey)
        if secret is not None :
	    return oauth.OAuthConsumer(key, secret)

        que = db.Query(OAuthConsumerKey).filter('consumer =',key)
        results = que.fetch(limit=1)
        if len(results) > 0 : 
            secret = results[0].secret
            memcache.set(memkey, secret, 7200)
	    return oauth.OAuthConsumer(key, secret)

        # Demo hard coded hacks if there are no keys
        que = db.Query(OAuthConsumerKey)
        results = que.fetch(limit=1)
        if len(results) < 1 : 
	    if key == "lmsng.school.edu" : 
                 memcache.set(memkey, 'secret', 7200)
                 return oauth.OAuthConsumer(key, "secret")
	    if key == "12345" : 
                 memcache.set(memkey, 'secret', 7200)
                 return oauth.OAuthConsumer(key, "secret")

        logging.info("Did not find consumer "+key)
        return None

    # We don't do request_tokens
    def lookup_token(self, token_type, token):
        return oauth.OAuthToken(None, None)

    # Trust all nonces
    def lookup_nonce(self, oauth_consumer, oauth_token, nonce):
        return None

    # We don't do request_tokens
    def fetch_request_token(self, oauth_consumer):
        return None

    # We don't do request_tokens
    def fetch_access_token(self, oauth_consumer, oauth_token):
        return None

    # We don't do request_tokens
    def authorize_request_token(self, oauth_token, user):
        return None

