var eosPriceUsd;
var ramPriceEos;
var ramPriceUsd;
var netPriceEos;
var netPriceUsd;
var cpuPriceEos;
var cpuPriceUsd;
var maxRam;
var usedRam;

var chainEndpoint = "https://api.eossweden.org";

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
  var reqBan = getXmlHttpRequestObject();
  var reqGlobal = getXmlHttpRequestObject();

  function updateEosData() {
    if (reqGlobal.readyState == 4 || reqGlobal.readyState == 0) {
      reqGlobal.open("POST", chainEndpoint + "/v1/chain/get_table_rows");
      reqGlobal.onreadystatechange = handleResponseGlobal;
    }

    if (reqEos.readyState == 4 || reqEos.readyState == 0) {
      // reqEos.open("GET", "https://api.coinmarketcap.com/v2/ticker/1765/"); //~ EOS/USD price
      reqEos.open(
        "GET",
        "https://api.newdex.io/v1/ticker?symbol=eosio.token-eos-eusd"
      );
      reqEos.onreadystatechange = handleResponseEos;
    }

    if (reqRam.readyState == 4 || reqRam.readyState == 0) {
      reqRam.open("POST", chainEndpoint + "/v1/chain/get_table_rows");
      reqRam.onreadystatechange = handleResponseRam;
    }

    if (reqBan.readyState == 4 || reqBan.readyState == 0) {
      reqBan.open("POST", chainEndpoint + "/v1/chain/get_account");
      reqBan.onreadystatechange = handleResponseBan;
    }
    reqGlobal.send(
      JSON.stringify({
        json: "true",
        code: "eosio",
        scope: "eosio",
        table: "global"
      })
    );
  }

  function handleResponseGlobal() {
    if (reqGlobal.readyState == 4) {
      parseStateGlobal(JSON.parse(reqGlobal.responseText));
      reqEos.send();
    }
  }

  function handleResponseEos() {
    if (reqEos.readyState == 4) {
      parseStateEos(JSON.parse(reqEos.responseText));
      reqRam.send(
        JSON.stringify({
          json: "true",
          code: "eosio",
          scope: "eosio",
          table: "rammarket",
          limit: "10"
        })
      );
      reqBan.send(JSON.stringify({ account_name: "eosnewyorkio" }));
    }
  }

  function handleResponseRam() {
    if (reqRam.readyState == 4) {
      parseStateRam(JSON.parse(reqRam.responseText));
    }
  }

  function handleResponseBan() {
    if (reqBan.readyState == 4) {
      parseStateBan(JSON.parse(reqBan.responseText));
    }
  }

  function parseStateGlobal(xDoc) {
    if (xDoc == null) return;

    maxRam = xDoc.rows[0].max_ram_size;
    usedRam = xDoc.rows[0].total_ram_bytes_reserved;
  }

  function parseStateEos(xDoc) {
    if (xDoc == null) return;

    var target = document.getElementById("eos-price-usd");
    // eosPriceUsd = xDoc.data.quotes.USD.price;
    eosPriceUsd = xDoc.data.last;
    target.innerHTML = "1 EOS = $" + eosPriceUsd.toFixed(2) + " USD";
  }

  function parseStateRam(xDoc) {
    if (xDoc == null) return;

    var ramBaseBalance = xDoc.rows[0].base.balance; // Amount of RAM bytes in use
    ramBaseBalance = ramBaseBalance.substr(0, ramBaseBalance.indexOf(" "));
    var ramQuoteBalance = xDoc.rows[0].quote.balance; // Amount of EOS in the RAM collector
    ramQuoteBalance = ramQuoteBalance.substr(0, ramQuoteBalance.indexOf(" "));
    ramPriceEos = ((ramQuoteBalance / ramBaseBalance) * 1024).toFixed(8); // Price in KiB
    ramPriceUsd = ramPriceEos * eosPriceUsd;
    var ramUtilization = (usedRam / maxRam) * 100;

    var target = document.getElementById("maxRam");
    target.innerHTML = (maxRam / 1024 / 1024 / 1024).toFixed(2) + " GiB";
    target = document.getElementById("allocatedRam");
    target.innerHTML = (usedRam / 1024 / 1024 / 1024).toFixed(2) + " GiB";
    target = document.getElementById("utilizedRam");
    target.innerHTML = ramUtilization.toFixed(2) + " %";
    target = document.getElementById("ramUtilVal");
    target.innerHTML = ramUtilization.toFixed(2) + "%";
    target = document.getElementById("ramUtilBar");
    target.style.width = ramUtilization.toFixed(2) + "%";
    target = document.getElementById("ram-price-eos");
    target.innerHTML = ramPriceEos + " EOS per KiB";
    target = document.getElementById("ram-price-usd");
    target.innerHTML =
      "~ $" + (ramPriceEos * eosPriceUsd).toFixed(3) + " USD per KiB";
  }

  function parseStateBan(xDoc) {
    if (xDoc == null) return;

    var target = document.getElementById("net-price-eos");
    var netStaked = xDoc.total_resources.net_weight.substr(
      0,
      xDoc.total_resources.net_weight.indexOf(" ")
    );
    var netAvailable = xDoc.net_limit.max / 1024; //~ convert bytes to kilobytes
    netPriceEos = (netStaked / netAvailable / 3).toFixed(8); //~ divide by 3 to get average per day from 3 day avg
    netPriceUsd = netPriceEos * eosPriceUsd;
    target.innerHTML = netPriceEos + " EOS/KiB/Day";
    target = document.getElementById("net-price-usd");
    target.innerHTML =
      "~ $" + (netPriceEos * eosPriceUsd).toFixed(3) + " USD/KiB/Day";

    target = document.getElementById("cpu-price-eos");
    var cpuStaked = xDoc.total_resources.cpu_weight.substr(
      0,
      xDoc.total_resources.cpu_weight.indexOf(" ")
    );
    var cpuAvailable = xDoc.cpu_limit.max / 1000; // convert microseconds to milliseconds
    cpuPriceEos = (cpuStaked / cpuAvailable / 3).toFixed(8); //~ divide by 3 to get average per day from 3 day avg
    cpuPriceUsd = cpuPriceEos * eosPriceUsd;
    target.innerHTML = cpuPriceEos + " EOS/ms/Day";
    target = document.getElementById("cpu-price-usd");
    target.innerHTML =
      "~ $" + (cpuPriceEos * eosPriceUsd).toFixed(3) + " USD/ms/Day";
  }
  /* --- End of EOS data routines --- */

  function eborLoadIsotope() {
    var $container = jQuery("#container"),
      $optionContainer = jQuery("#options"),
      $options = $optionContainer.find('a[href^="#"]').not('a[href="#"]'),
      isOptionLinkClicked = false;

    $container.isotope({
      itemSelector: ".element",
      resizable: false,
      filter: "*",
      transitionDuration: "0.5s",
      layoutMode: "packery"
    });

    if (jQuery("body").hasClass("video-detail"))
      $container.isotope({
        transformsEnabled: false
      });

    jQuery(window).smartresize(function() {
      $container.isotope("layout");
    });

    $options.on("click", function() {
      var $this = jQuery(this),
        href = $this.attr("href");

      if ($this.hasClass("selected")) {
        return;
      } else {
        $options.removeClass("selected");
        $this.addClass("selected");
      }

      jQuery.bbq.pushState("#" + href);
      isOptionLinkClicked = true;
      updateEosData(); //~ Update all prices
      return false;
    });

    jQuery(window)
      .on("hashchange", function() {
        var theFilter = window.location.hash.replace(/^#/, "");

        if (theFilter == false) theFilter = "home";

        $container.isotope({
          filter: "." + theFilter
        });

        if (isOptionLinkClicked == false) {
          $options.removeClass("selected");
          $optionContainer
            .find('a[href="#' + theFilter + '"]')
            .addClass("selected");
        }

        isOptionLinkClicked = false;
      })
      .trigger("hashchange");
  }
  eborLoadIsotope();
  updateEosData(); //~ Update EOS data on page load
  jQuery(window)
    .trigger("resize")
    .trigger("smartresize");
});

$(".splink").on("click", function() {
  "use strict";
  $("html, body").animate({ scrollTop: 0 }, 1000);
});

$(function() {
  $(".calc-change").on("change keydown paste input", function(e) {
    var elem = $(this);
    var target;
    var unitTarget;
    var unitTargetValue;
    var value;
    var currencyTarget;
    var currencyValue;
    var priceValue;
    var roundingUnits;

    //~ Main switch to handle changes to the forms
    switch (elem.attr("id")) {
      case "eos-afford-ram":
      case "ram-afford-unit":
      case "ram-have-unit":
        unitTarget = document.getElementById("ram-afford-unit");
        unitTargetValue = unitTarget.options[unitTarget.selectedIndex].text;
        currencyTarget = document.getElementById("ram-have-unit");
        currencyValue =
          currencyTarget.options[currencyTarget.selectedIndex].text;

        if (currencyValue == "USD") {
          priceValue = ramPriceUsd;
        }
        if (currencyValue == "EOS") {
          priceValue = ramPriceEos;
        }
        value = document.getElementById("eos-afford-ram").value;
        switch (unitTargetValue) {
          case "bytes":
            if (currencyValue == "USD") {
              value = (value / ramPriceUsd) * 1024;
              break;
            }
            if (currencyValue == "EOS") {
              value = (value / ramPriceEos) * 1024;
              break;
            }
          case "KiB":
            if (currencyValue == "USD") {
              value = value / ramPriceUsd;
              break;
            }
            if (currencyValue == "EOS") {
              value = value / ramPriceEos;
              break;
            }
            break;
          case "MiB":
            if (currencyValue == "USD") {
              value = value / ramPriceUsd / 1024;
              break;
            }
            if (currencyValue == "EOS") {
              value = value / ramPriceEos / 1024;
              break;
            }
            break;
          case "GiB":
            if (currencyValue == "USD") {
              value = value / ramPriceUsd / 1024 / 1024;
              break;
            }
            if (currencyValue == "EOS") {
              value = value / ramPriceEos / 1024 / 1024;
              break;
            }
            break;
        }
        target = document.getElementById("result-afford-ram");
        target.innerHTML = value.toFixed(4);
        break;

      case "eos-afford-net":
      case "net-afford-unit":
      case "net-have-unit":
        unitTarget = document.getElementById("net-afford-unit");
        unitTargetValue = unitTarget.options[unitTarget.selectedIndex].text;
        currencyTarget = document.getElementById("net-have-unit");
        currencyValue =
          currencyTarget.options[currencyTarget.selectedIndex].text;

        if (currencyValue == "USD") {
          priceValue = netPriceUsd;
        }
        if (currencyValue == "EOS") {
          priceValue = netPriceEos;
        }
        value = document.getElementById("eos-afford-net").value;
        switch (unitTargetValue) {
          case "bytes / day":
            if (currencyValue == "USD") {
              value = (value / netPriceUsd) * 1024;
              break;
            }
            if (currencyValue == "EOS") {
              value = (value / netPriceEos) * 1024;
              break;
            }
          case "KiB / day":
            if (currencyValue == "USD") {
              value = value / netPriceUsd;
              break;
            }
            if (currencyValue == "EOS") {
              value = value / netPriceEos;
              break;
            }
            break;
          case "MiB / day":
            if (currencyValue == "USD") {
              value = value / netPriceUsd / 1024;
              break;
            }
            if (currencyValue == "EOS") {
              value = value / netPriceEos / 1024;
              break;
            }
            break;
          case "GiB / day":
            if (currencyValue == "USD") {
              value = value / netPriceUsd / 1024 / 1024;
              break;
            }
            if (currencyValue == "EOS") {
              value = value / netPriceEos / 1024 / 1024;
              break;
            }
            break;
        }
        target = document.getElementById("result-afford-net");
        target.innerHTML = value.toFixed(4);
        break;

      case "eos-afford-cpu":
      case "cpu-afford-unit":
      case "cpu-have-unit":
        unitTarget = document.getElementById("cpu-afford-unit");
        unitTargetValue = unitTarget.options[unitTarget.selectedIndex].text;
        currencyTarget = document.getElementById("cpu-have-unit");
        currencyValue =
          currencyTarget.options[currencyTarget.selectedIndex].text;

        if (currencyValue == "USD") {
          priceValue = cpuPriceUsd;
        }
        if (currencyValue == "EOS") {
          priceValue = cpuPriceEos;
        }
        value = document.getElementById("eos-afford-cpu").value;
        switch (unitTargetValue) {
          case "µs / day":
            if (currencyValue == "USD") {
              value = (value / cpuPriceUsd) * 1000;
              break;
            }
            if (currencyValue == "EOS") {
              value = (value / cpuPriceEos) * 1000;
              break;
            }
          case "ms / day":
            if (currencyValue == "USD") {
              value = value / cpuPriceUsd;
              break;
            }
            if (currencyValue == "EOS") {
              value = value / cpuPriceEos;
              break;
            }
            break;
          case "s / day":
            if (currencyValue == "USD") {
              value = value / cpuPriceUsd / 1000;
              break;
            }
            if (currencyValue == "EOS") {
              value = value / cpuPriceEos / 1000;
              break;
            }
            break;
        }
        target = document.getElementById("result-afford-cpu");
        target.innerHTML = value.toFixed(4);
        break;

      case "eos-cost-ram":
      case "ram-need-unit":
      case "ram-cost-unit":
        unitTarget = document.getElementById("ram-need-unit");
        unitTargetValue = unitTarget.options[unitTarget.selectedIndex].text;
        currencyTarget = document.getElementById("ram-cost-unit");
        currencyValue =
          currencyTarget.options[currencyTarget.selectedIndex].text;
        if (currencyValue == "USD") {
          priceValue = ramPriceUsd;
          roundingUnits = 3;
        }
        if (currencyValue == "EOS") {
          priceValue = ramPriceEos;
          roundingUnits = 8;
        }
        value = document.getElementById("eos-cost-ram").value;
        switch (unitTargetValue) {
          case "bytes":
            if (currencyValue == "USD") {
              value = (value * ramPriceUsd) / 1024;
              break;
            }
            if (currencyValue == "EOS") {
              value = (value * ramPriceEos) / 1024;
              break;
            }
          case "KiB":
            if (currencyValue == "USD") {
              value = value * ramPriceUsd;
              break;
            }
            if (currencyValue == "EOS") {
              value = value * ramPriceEos;
              break;
            }
            break;
          case "MiB":
            if (currencyValue == "USD") {
              value = value * ramPriceUsd * 1024;
              break;
            }
            if (currencyValue == "EOS") {
              value = value * ramPriceEos * 1024;
              break;
            }
            break;
          case "GiB":
            if (currencyValue == "USD") {
              value = value * ramPriceUsd * 1024 * 1024;
              break;
            }
            if (currencyValue == "EOS") {
              value = value * ramPriceEos * 1024 * 1024;
              break;
            }
            break;
        }

        target = document.getElementById("result-cost-ram");
        target.innerHTML = value.toFixed(roundingUnits);
        break;

      case "eos-cost-net":
      case "net-need-unit":
      case "net-cost-unit":
        unitTarget = document.getElementById("net-need-unit");
        unitTargetValue = unitTarget.options[unitTarget.selectedIndex].text;
        currencyTarget = document.getElementById("net-cost-unit");
        currencyValue =
          currencyTarget.options[currencyTarget.selectedIndex].text;
        if (currencyValue == "USD") {
          priceValue = netPriceUsd;
          roundingUnits = 3;
        }
        if (currencyValue == "EOS") {
          priceValue = netPriceEos;
          roundingUnits = 8;
        }
        value = document.getElementById("eos-cost-net").value;
        switch (unitTargetValue) {
          case "bytes / day":
            if (currencyValue == "USD") {
              value = (value * netPriceUsd) / 1024;
              break;
            }
            if (currencyValue == "EOS") {
              value = (value * netPriceEos) / 1024;
              break;
            }
          case "KiB / day":
            if (currencyValue == "USD") {
              value = value * netPriceUsd;
              break;
            }
            if (currencyValue == "EOS") {
              value = value * netPriceEos;
              break;
            }
            break;
          case "MiB / day":
            if (currencyValue == "USD") {
              value = value * netPriceUsd * 1024;
              break;
            }
            if (currencyValue == "EOS") {
              value = value * netPriceEos * 1024;
              break;
            }
            break;
          case "GiB / day":
            if (currencyValue == "USD") {
              value = value * netPriceUsd * 1024 * 1024;
              break;
            }
            if (currencyValue == "EOS") {
              value = value * netPriceEos * 1024 * 1024;
              break;
            }
            break;
        }

        target = document.getElementById("result-cost-net");
        target.innerHTML = value.toFixed(roundingUnits);
        break;

      case "eos-cost-cpu":
      case "cpu-need-unit":
      case "cpu-cost-unit":
        unitTarget = document.getElementById("cpu-need-unit");
        unitTargetValue = unitTarget.options[unitTarget.selectedIndex].text;
        currencyTarget = document.getElementById("cpu-cost-unit");
        currencyValue =
          currencyTarget.options[currencyTarget.selectedIndex].text;
        if (currencyValue == "USD") {
          priceValue = cpuPriceUsd;
          roundingUnits = 3;
        }
        if (currencyValue == "EOS") {
          priceValue = cpuPriceEos;
          roundingUnits = 8;
        }
        value = document.getElementById("eos-cost-cpu").value;
        switch (unitTargetValue) {
          case "µs / day":
            if (currencyValue == "USD") {
              value = (value * cpuPriceUsd) / 1000;
              break;
            }
            if (currencyValue == "EOS") {
              value = (value * cpuPriceEos) / 1000;
              break;
            }
          case "ms / day":
            if (currencyValue == "USD") {
              value = value * cpuPriceUsd;
              break;
            }
            if (currencyValue == "EOS") {
              value = value * cpuPriceEos;
              break;
            }
            break;
          case "s / day":
            if (currencyValue == "USD") {
              value = value * cpuPriceUsd * 1000;
              break;
            }
            if (currencyValue == "EOS") {
              value = value * cpuPriceEos * 1000;
              break;
            }
            break;
        }

        target = document.getElementById("result-cost-cpu");
        target.innerHTML = value.toFixed(roundingUnits);
        break;
    }
  });
});
