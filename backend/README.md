# ERP Backend V2

The backend for EOSRP has two components right now:
* Java component that inserts the latest price information into a postgres database
* Node.JS component exposing a REST API that queries the database to return records and will later be used to render candlestick charting on the client-side

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
