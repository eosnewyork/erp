//~ Import configurations
var config = require('./conf/config');

//~ Setup server
const express = require('express');
const app = module.exports = express();
const logger = require('morgan');
const bodyParser = require('body-parser');
const http = require('http');
const massive = require('massive');


//~ Connect to the DB
massive({
  host: config.database.host,
  port: config.database.port,
  database: config.database.dbname,
  user: config.database.user,
  password: config.database.password
}).then(db => {
  //~ Print if successful
  console.log("Initialized DB successfully");
  app.set('db', db);

  //~ Initialize controller
  var apiCtrl = require('./controllers/apiCtrl');

  //~ Middleware
  //~ Log requests to the console
  app.use(logger('dev'));

  //~ Parse incoming requests data
  app.use(bodyParser.json());
  app.use(bodyParser.urlencoded({ extended: false}));

  //~ For debug only
  app.use(function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    next();
  });

  //~ Routing endpoints
  app.get('/v1/ram/1', apiCtrl.getRam1d); //~ last day of data
  app.get('/v1/ram/3', apiCtrl.getRam3d); //~ last 3 days of data
  app.get('/v1/ram/7', apiCtrl.getRam7d); //~ last week of data
  app.get('/v1/ram/14', apiCtrl.getRam14d); //~ last 2 weeks of data
  app.get('/v1/ram/30', apiCtrl.getRam30d); //~ last month of data
  app.get('/v1/ram/90', apiCtrl.getRam90d); //~ last 3 mo of data
  app.get('/v1/ram/180', apiCtrl.getRam180d); //~ last 6 mo of data
  app.get('/v1/ram/365', apiCtrl.getRam365d); //~ Last 1 year of data
  app.get('/v1/ram/all', apiCtrl.getRamAll); //~ Return daily resolution for all datapoints

  //~ Generic 404 error for invalid URIs
  app.get('*', function(req, res) {
    res.status(404).send({url: req.originalUrl + ' not found'})
  });

  //~ Launch the server
  app.listen(config.server.port);
  console.log('todo list RESTful API server started on: ' + config.server.port);
});
