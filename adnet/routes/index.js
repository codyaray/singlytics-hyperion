
/*
 * GET home page.
 */

exports.index = function(req, res){

res.set({
  'status' : 200;
  'Content-Type': 'text/plain',
  'Content-Length': '123',
  'X-URL': 'http://pathtojpeg.com/jpeg';
  'X-href' : 'http://pathtolink.com';
  'uniqueID' : 'uniqueID';
  'appID' : '';
});

};