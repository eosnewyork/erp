jQuery(window).load(function($) {
  "use strict";

  /* --- Begin EOS data update routines --- */
  function getXmlHttpRequestObject() {
    if (window.XMLHttpRequest) {
      return new XMLHttpRequest();
    } else if (window.ActiveXObject) {
      return new ActiveXObject("Microsoft.XMLHTTP");
    } else {
      alert(
        "Your Browser does not support AJAX!\nIt's about time to upgrade don't you think?"
      );
    }
  }

  var reqEos = getXmlHttpRequestObject();
  var reqRam = getXmlHttpRequestObject();
  var reqGlobal = getXmlHttpRequestObject();
  var eosPriceUsd;
  var maxRam;

  function updateEosData() {
    if (reqGlobal.readyState == 4 || reqGlobal.readyState == 0) {
      reqGlobal.open(
        "POST",
        "https://api.eossweden.org/v1/chain/get_table_rows"
      );
      reqGlobal.onreadystatechange = handleResponseGlobal;
    }

    if (reqEos.readyState == 4 || reqEos.readyState == 0) {
      reqEos.open("GET", "https://api.coinmarketcap.com/v2/ticker/1765/"); //~ EOS/USD price
      reqEos.onreadystatechange = handleResponseEos;
    }

    if (reqRam.readyState == 4 || reqRam.readyState == 0) {
      reqRam.open("POST", "https://api.eossweden.org/v1/chain/get_table_rows");
      reqRam.onreadystatechange = handleResponseRam;
    }
    reqEos.send();
    reqRam.send(
      JSON.stringify({
        json: "true",
        code: "eosio",
        scope: "eosio",
        table: "rammarket",
        limit: "10"
      })
    );
  }

  function handleResponseEos() {
    if (reqEos.readyState == 4) {
      parseStateEos(JSON.parse(reqEos.responseText));
    }
  }

  function handleResponseRam() {
    if (reqRam.readyState == 4) {
      parseStateRam(JSON.parse(reqRam.responseText));
    }
  }

  function handleResponseGlobal() {
    if (reqGlobal.readyState == 4) {
      parseStateGlobal(JSON.parse(reqGlobal.responseText));
    }
  }

  function parseStateGlobal(xDoc) {
    if (xDoc == null) return;

    maxRam = xDoc.rows[0].max_ram_size;
  }

  function parseStateEos(xDoc) {
    if (xDoc == null) return;

    eosPriceUsd = xDoc.data.quotes.USD.price;
  }

  function parseStateRam(xDoc) {
    if (xDoc == null) return;

    var ramBaseBalance = xDoc.rows[0].base.balance; // Amount of RAM bytes in use
    ramBaseBalance = ramBaseBalance.substr(0, ramBaseBalance.indexOf(" "));
    var ramQuoteBalance = xDoc.rows[0].quote.balance; // Amount of EOS in the RAM collector
    ramQuoteBalance = ramQuoteBalance.substr(0, ramQuoteBalance.indexOf(" "));
    var ramUsed = 1 - (ramBaseBalance - maxRam);
    var ramPriceEos = (ramQuoteBalance / ramBaseBalance).toFixed(8) * 1024; // Price in kb

    var target = document.getElementById("ram-price-eos");
    target.innerHTML = ramPriceEos + " EOS per Kb";
    target = document.getElementById("ram-price-usd");
    target.innerHTML =
      "~ $" + (ramPriceEos * eosPriceUsd).toFixed(3) + " USD per Kb";
  }
  /* --- End of EOS data routines --- */

  updateEosData(); //~ Update EOS data on page load
});
