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
  var reqBan = getXmlHttpRequestObject();
  var eosPriceUsd;

  function updateEosData() {
    if (reqEos.readyState == 4 || reqEos.readyState == 0) {
      reqEos.open("GET", "https://api.coinmarketcap.com/v2/ticker/1765/"); //~ EOS/USD price
      reqEos.onreadystatechange = handleResponseEos;
    }

    if (reqBan.readyState == 4 || reqBan.readyState == 0) {
      reqBan.open("POST", "https://api.eossweden.org/v1/chain/get_account");
      reqBan.onreadystatechange = handleResponseBan;
    }
    reqEos.send();
    reqBan.send(JSON.stringify({ account_name: "eosnewyorkio" }));
  }

  function handleResponseEos() {
    if (reqEos.readyState == 4) {
      parseStateEos(JSON.parse(reqEos.responseText));
    }
  }

  function handleResponseBan() {
    if (reqBan.readyState == 4) {
      parseStateBan(JSON.parse(reqBan.responseText));
    }
  }

  function parseStateEos(xDoc) {
    if (xDoc == null) return;

    eosPriceUsd = xDoc.data.quotes.USD.price;
  }

  function parseStateBan(xDoc) {
    if (xDoc == null) return;

    var target = document.getElementById("cpu-price-eos");
    var cpuStaked = xDoc.total_resources.cpu_weight.substr(
      0,
      xDoc.total_resources.cpu_weight.indexOf(" ")
    );
    var cpuAvailable = xDoc.cpu_limit.max / 1000; // convert microseconds to milliseconds
    var cpuPrice = cpuStaked / cpuAvailable;
    target.innerHTML = cpuPrice.toFixed(8) + " EOS per Ms";
    target = document.getElementById("cpu-price-usd");
    target.innerHTML =
      "~ $" + (cpuPrice.toFixed(8) * eosPriceUsd).toFixed(3) + " USD per Ms";
  }
  /* --- End of EOS data routines --- */

  updateEosData(); //~ Update EOS data on page load
});
