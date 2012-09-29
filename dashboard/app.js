var express = require('express');
var app = express();

app.get("/", function (req, res) {
  res.send("app is running!")
})

app.listen(3000)
console.log('Maybe listening on port 3000, I dunno, whatever...')
