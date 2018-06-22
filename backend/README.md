# EOS Resource Planner Backend API

The backend for EOSRP currently has two components:
* Java component that inserts the latest price information into a postgres database
* Node.JS component exposing a REST API that queries the database to return records and will later be used to render candlestick charting on the client-side

In the future this architecture will be simplified and redesigned with one tier accessing the DB. For now the goal was to begin collecting historical metrics on the resource prices as soon as possible.

The Java component runs as a batch job every minute scraping the latest resource prices and saving them into a postgres database.

The NodeJS component runs as an Express.js server and uses Massive.js to query the postgres database. At this time it only works with the ram table but will support the cpu and network tables in the future.


You will need a postgres database with the following table schema:

```
TABLE NAME: eosram
Column  |            Type             |
--------+-----------------------------+
dt      | timestamp without time zone | Time stamp of data point
peos    | numeric                     | The price in EOS at time of data point
pusd    | numeric                     | The equivalent price in USD at time of data point
id      | integer                     | Auto-incremented counter
```

## API Setup Requirements

* Node 8
* Git

## API Instructions
Clone the repository and install the dependencies.

```bash
npm install
```

Open `restapi/conf/config.js` and modify the database/server settings to match your environment

```
database: {
  host: '127.0.0.1',
  port: '5432',
  dbname: 'dbname',
  user: 'username',
  pass: 'pw',
},
//~ Server details
server: {
  host: '127.0.0.1',
  port: '8000'
}
```

## Starting the API

```bash
npm run start:dev
```

## Using the API

After launching the server you should see a message saying the server was started on port 8000:
```bash
$ npm run start:dev

> eosrp-backend-api@0.1.0 start:dev
> nodemon server.js

[nodemon] 1.17.5
[nodemon] to restart at any time, enter `rs`
[nodemon] watching: *.*
[nodemon] starting `node server.js`
Initialized DB successfully
todo list RESTful API server started on: 8000

```

Now use a utility like Postman to get some data. The URL options are:
```
http://localhost:8000/v1/ram/1    // Last 1 day of data in minute resolution
http://localhost:8000/v1/ram/3    // Last 3 days of data in minute resolution
http://localhost:8000/v1/ram/7    // Last 7 days of data in hour resolution
http://localhost:8000/v1/ram/14   // Last 14 days of data in hour resolution
http://localhost:8000/v1/ram/30   // Last 30 days of data in hour resolution
http://localhost:8000/v1/ram/90   // Last 90 days of data in day resolution
http://localhost:8000/v1/ram/180  // Last 180 days of data in day resolution
http://localhost:8000/v1/ram/365  // Last 1 year of data in week resolution
http://localhost:8000/v1/ram/all  // All available data in week resolution
```
A successful request will return JSON data containing the price information grouped into candles with open, high, low, and close computed:
```
GET http://localhost:8000/v1/ram/90


RESPONSE (200):
[
    {
        "dt": "2018-06-17T04:00:00.000Z",
        "o": "0.0000152925255706774",
        "h": "0.0000160796653563408",
        "l": "0.0000152925255706774",
        "c": "0.000016079005452629"
    },
    {
        "dt": "2018-06-18T04:00:00.000Z",
        "o": "0.0000160790254122507",
        "h": "0.0000165781105264275",
        "l": "0.0000160790254122507",
        "c": "0.0000165781048168569"
    },
    {
        "dt": "2018-06-19T04:00:00.000Z",
        "o": "0.0000165781256259231",
        "h": "0.0000173864480561336",
        "l": "0.0000165781256259231",
        "c": "0.0000173864480561336"
    },
    {
        "dt": "2018-06-20T04:00:00.000Z",
        "o": "0.0000173864682236967",
        "h": "0.0000176286892090938",
        "l": "0.0000173864682236967",
        "c": "0.0000176286892090938"
    },
    {
        "dt": "2018-06-21T04:00:00.000Z",
        "o": "0.0000176287097679978",
        "h": "0.0000207348716915592",
        "l": "0.0000176287097679978",
        "c": "0.0000207348716915592"
    }
]
