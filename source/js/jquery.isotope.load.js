jQuery(window).load(function($) {
    "use strict";

    /* --- Begin EOS data update routines --- */
    function getXmlHttpRequestObject() {
      if (window.XMLHttpRequest) {
        return new XMLHttpRequest();
      } else if (window.ActiveXObject) {
        return new ActiveXObject("Microsoft.XMLHTTP");
      } else {
        alert("Your Browser does not support AJAX!\nIt's about time to upgrade don't you think?");
      }
    }

    var reqEos = getXmlHttpRequestObject();
    var reqRam = getXmlHttpRequestObject();
    var reqBan = getXmlHttpRequestObject();
    var reqGlobal = getXmlHttpRequestObject();
    var eosPriceUsd;
    var maxRam;

    function updateEosData() {
      if (reqGlobal.readyState == 4 || reqGlobal.readyState == 0) {
        reqGlobal.open("POST", "https://api.eosnewyork.io:/v1/chain/get_table_rows");
        reqGlobal.onreadystatechange = handleResponseGlobal;
      }

      if (reqEos.readyState == 4 || reqEos.readyState == 0) {
        reqEos.open("GET", "https://api.coinmarketcap.com/v2/ticker/1765/"); //~ EOS/USD price
        reqEos.onreadystatechange = handleResponseEos;
      }

      if (reqRam.readyState == 4 || reqRam.readyState == 0) {
        reqRam.open("POST", "https://api.eosnewyork.io:/v1/chain/get_table_rows");
        reqRam.onreadystatechange = handleResponseRam;
      }

      if (reqBan.readyState == 4 || reqBan.readyState == 0) {
        reqBan.open("POST", "https://api.eosnewyork.io/v1/chain/get_account");
        reqBan.onreadystatechange = handleResponseBan;
      }
      reqGlobal.send(JSON.stringify({json:"true", code:"eosio", scope:"eosio", table:"global"}));
      reqEos.send();
      reqRam.send(JSON.stringify({json:"true", code:"eosio", scope:"eosio", table:"rammarket", limit:"10"}));
      reqBan.send(JSON.stringify({account_name:"eosnewyorkio"}));
    }

    function handleResponseGlobal() {
      if (reqGlobal.readyState == 4)
      {
        parseStateGlobal(JSON.parse(reqGlobal.responseText));;
      }
    }

    function handleResponseEos() {
      if (reqEos.readyState == 4)
      {
        parseStateEos(JSON.parse(reqEos.responseText));;
      }
    }

    function handleResponseRam() {
      if (reqRam.readyState == 4)
      {
        parseStateRam(JSON.parse(reqRam.responseText));
      }
    }

    function handleResponseBan() {
      if (reqBan.readyState == 4)
      {
        parseStateBan(JSON.parse(reqBan.responseText));
      }
    }

    function parseStateGlobal(xDoc){
      if(xDoc == null)
      return

      maxRam = xDoc.rows[0].max_ram_size;
    }

    function parseStateEos(xDoc){
      if(xDoc == null)
      return

      var target = document.getElementById("eos-price-usd");
      eosPriceUsd = xDoc.data.quotes.USD.price;
      target.innerHTML = "1 EOS = $" + eosPriceUsd.toFixed(2) + " USD";
    }

    function parseStateRam(xDoc){

      if(xDoc == null)
      return

      var ramBaseBalance = xDoc.rows[0].base.balance; // Amount of RAM bytes in use
      ramBaseBalance = ramBaseBalance.substr(0,ramBaseBalance.indexOf(' '));
      var ramQuoteBalance = xDoc.rows[0].quote.balance; // Amount of EOS in the RAM collector
      ramQuoteBalance = ramQuoteBalance.substr(0,ramQuoteBalance.indexOf(' '));
      var ramUsed = 1 - (ramBaseBalance - maxRam);
      var ramPriceEos = (ramQuoteBalance / ramBaseBalance).toFixed(8) * 1024; // Price in kb

      var target = document.getElementById("ram-price-eos");
      target.innerHTML = ramPriceEos + " EOS per Kb";
      target = document.getElementById("ram-price-usd");
      target.innerHTML = "~ $" + (ramPriceEos * eosPriceUsd).toFixed(3) + " USD per Kb";



      /** coming soon
      var target = document.getElementById("rampriceeos");
      target.innerHTML = ramPriceEos + " EOS";
      target = document.getElementById("rampriceusd");
      target.innerHTML = "$" + (ramPriceEos * eosPriceUsd).toFixed(3) + " USD";

      target = document.getElementById("ramusedb");
      target.innerHTML = ramUsed + " b";
      target = document.getElementById("ramusedkb");
      target.innerHTML = (ramUsed/1024).toFixed(2) + " Kb";
      target = document.getElementById("ramusedmb");
      target.innerHTML = (ramUsed/1024/1024).toFixed(2) + " Mb";
      target = document.getElementById("ramusedgb");
      target.innerHTML = (ramUsed/1024/1024/1024).toFixed(2) + " Gb";
      target = document.getElementById("ramutilization");
      target.innerHTML = ((ramUsed / maxRam) * 100).toFixed(2) + " %";
      **/
    }

    function parseStateBan(xDoc){
      if(xDoc == null)
      return

      var target = document.getElementById("net-price-eos");
      var netStaked = xDoc.total_resources.net_weight.substr(0,xDoc.total_resources.net_weight.indexOf(' '));
      var netAvailable = xDoc.net_limit.max / 1024; // convert bytes to kilobytes
      var netPrice = netStaked / netAvailable;
      target.innerHTML = netPrice.toFixed(8) + " EOS per Kb";
      target = document.getElementById("net-price-usd");
      target.innerHTML = "~ $" + (netPrice.toFixed(8) * eosPriceUsd).toFixed(3) + " USD per Kb";

      target = document.getElementById("cpu-price-eos");
      var cpuStaked = xDoc.total_resources.cpu_weight.substr(0,xDoc.total_resources.cpu_weight.indexOf(' '));
      var cpuAvailable = xDoc.cpu_limit.max / 1000; // convert microseconds to milliseconds
      var cpuPrice = cpuStaked / cpuAvailable;
      target.innerHTML = cpuPrice.toFixed(8) + " EOS per Ms";
      target = document.getElementById("cpu-price-usd");
      target.innerHTML = "~ $" + (cpuPrice.toFixed(8) * eosPriceUsd).toFixed(3) + " USD per Ms";
    }
    /* --- End of EOS data routines --- */

    function eborLoadIsotope() {
        var $container = jQuery('#container'),
            $optionContainer = jQuery('#options'),
            $options = $optionContainer.find('a[href^="#"]').not('a[href="#"]'),
            isOptionLinkClicked = false;


        $container.isotope({
            itemSelector: '.element',
            resizable: false,
            filter: '*',
            transitionDuration: '0.5s',
            layoutMode: 'packery'
        });



        if (jQuery('body').hasClass('video-detail'))
            $container.isotope({
                transformsEnabled: false,
            });

        jQuery(window).smartresize(function() {
            $container.isotope('layout');
        });


        $options.on('click', function() {
            var $this = jQuery(this),
                href = $this.attr('href');

            if ($this.hasClass('selected')) {
                return;
            } else {
                $options.removeClass('selected');
                $this.addClass('selected');
            }

            jQuery.bbq.pushState('#' + href);
            isOptionLinkClicked = true;
            updateEosData(); //~ Update all prices
            return false;
        });

        jQuery(window).on('hashchange', function() {
            var theFilter = window.location.hash.replace(/^#/, '');

            if (theFilter == false)
                theFilter = 'home';

            $container.isotope({
                filter: '.' + theFilter
            });

            if (isOptionLinkClicked == false) {
                $options.removeClass('selected');
                $optionContainer.find('a[href="#' + theFilter + '"]').addClass('selected');
            }

            isOptionLinkClicked = false;
        }).trigger('hashchange');




    }


    eborLoadIsotope();
    updateEosData();  //~ Update EOS data on page load


    jQuery(window).trigger('resize').trigger('smartresize');



});

	$('.splink').on('click', function() {
		"use strict";
    	$("html, body").animate({ scrollTop: 0 }, 1000);
 	});
