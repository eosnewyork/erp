'use strict';

var app = require('../server');
var db = app.get('db');
var moment = require('moment');

//~ Setup date variables
const oneDayAgo = moment().utc().subtract(24, 'hour').format("YYYY-MM-DD HH:mm:SS");
const threeDaysAgo = moment().utc().subtract(3, 'day').format('YYYY-MM-DD');
const sevenDaysAgo = moment().utc().subtract(7, 'day').format('YYYY-MM-DD HH:mm:SS');
const fourteenDaysAgo = moment().utc().subtract(14, 'day').format('YYYY-MM-DD');
const thirtyDaysAgo = moment().utc().subtract(30, 'day').format('YYYY-MM-DD');
const ninetyDaysAgo = moment().utc().subtract(90, 'day').format('YYYY-MM-DD');
const oneEightyDaysAgo = moment().utc().subtract(180, 'day').format('YYYY-MM-DD');
const oneYearAgo = moment().utc().subtract(365, 'day').format('YYYY-MM-DD');

module.exports = {

  //~ 1 day resolution
  getRam1d: function(req, res) {
    db.build_minute_candles(oneDayAgo).then(results => {
      res.status(200).send(results);
    })
  },

  //~ 3 day resolution
  getRam3d: function(req, res) {
    db.build_minute_candles(threeDaysAgo).then(results => {
      res.status(200).send(results);
    })
  },

  //~ 7 day resolution
  getRam7d: function(req, res) {
    db.build_hour_candles(sevenDaysAgo).then(results => {
      res.status(200).send(results);
    })
  },

  //~ 14 day resolution
  getRam14d: function(req, res) {
    db.build_hour_candles(fourteenDaysAgo).then(results => {
      res.status(200).send(results);
    })
  },

  //~ 30 day resolution
  getRam30d: function(req, res) {
    db.build_hour_candles(thirtyDaysAgo).then(results => {
      res.status(200).send(results);
    })
  },

  //~ 90 day resolution
  getRam90d: function(req, res) {
    db.build_day_candles(ninetyDaysAgo).then(results => {
      res.status(200).send(results);
    })
  },

  //~ 180 day resolution
  getRam180d: function(req, res) {
    db.build_day_candles(oneEightyDaysAgo).then(results => {
      res.status(200).send(results);
    })
  },

  //~ 365 day resolution
  getRam365d: function(req, res) {
    db.build_week_candles(oneYearAgo).then(results => {
      res.status(200).send(results);
    })
  },

  //~ All data
  getRamAll: function(req, res) {
    db.build_alldata_week_candles().then(results => {
      res.status(200).send(results);
    })
  }
}
