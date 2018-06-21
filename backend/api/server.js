//~ Import configurations
var config = require('./conf/config');

//~ Setup server
const express = require('express');
const app = express();
const logger = require('morgan');
const bodyParser = require('body-parser');
const http = require('http');
const massive = require('massive');

//~ Log requests to the console
app.use(logger('dev'));

//~ Parse incoming requests data
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false}));


//~ Connect to the DB
massive({
  host: config.database.host,
  port: config.database.port,
  database: config.database.dbname,
  user: config.database.user,
  password: config.database.password
}).then(db => {
  console.log("Initialized DB successfully");

  app.set('db', db);
  //~ Setup the routes and start the server
  var routes = require('./api/routes/erpApiRoutes');
  routes(app);
  app.listen(config.server.port);
  console.log('todo list RESTful API server started on: ' + config.server.port);
})
