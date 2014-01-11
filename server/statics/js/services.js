'use strict';

/* Services */
(function( $ ) {

	$.module('skynet.services', [])
		.factory( '$$cookie', [function() {
	    	
	    	var that = {
	    		set: function(sKey, sValue, oOpts){
	    			var arr = [];
	    			var d, t;
	    			var cfg = $.extend({
	    				'expire': null,
	    				'path': '/',
	    				'domain': null,
	    				'secure': null,
	    				'encode': true
	    			}, oOpts);
	    			
	    			if (cfg.encode == true) {
	    				sValue = escape(sValue);
	    			}
	    			arr.push(sKey + '=' + sValue);

	    			if (cfg.path != null) {
	    				arr.push('path=' + cfg.path);
	    			}
	    			if (cfg.domain != null) {
	    				arr.push('domain=' + cfg.domain);
	    			}
	    			if (cfg.secure != null) {
	    				arr.push(cfg.secure);
	    			}
	    			if (cfg.expire != null) {
	    				d = new Date();
	    				t = d.getTime() + cfg.expire * 3600000;
	    				d.setTime(t);
	    				arr.push('expires=' + d.toGMTString());
	    			}
	    			document.cookie = arr.join(';');
	    		},
	    		get: function(sKey){
	    			sKey = sKey.replace(/([\.\[\]\$])/g, '\\\$1');
	    			var rep = new RegExp(sKey + '=([^;]*)?;', 'i');
	    			var co = document.cookie + ';';
	    			var res = co.match(rep);
	    			if (res) {
	    				return res[1] || "";
	    			}
	    			else {
	    				return '';
	    			}
	    		},
	    		remove: function(sKey, oOpts){
	    			oOpts = oOpts || {};
	    			oOpts.expire = -10;
	    			that.set(sKey, '', oOpts);
	    		}
	    	};
	    	return that;
	  }])
	  .factory( '$$uid', [function() {

	  	return function() {

	  		return Math.random().toString( 36 ).substr( 2, 16 );
	  	};
	  }])
	  .factory( '$$jsonp_req_url', [function() {

	  	return function( host, path ) {

	  		return 'http://' + host + path + '?callback=JSON_CALLBACK';
	  	};
	  }])
	  .factory( '$$poll', [function() {

	  	return function( cron, interval ) {

	  		return setInterval( function() {

	  			cron.call();
	  		}, interval );
	  	};
	  }])
	  .factory( '$$loadImg', [function() {

	  	return function( img, url, next, timeout ) {

	  		var clock;
	  		var now = ( new Date ).getTime();

	  		img.src = url;

	  		clock = setInterval( function() {

	  			if ( img.naturalWidth ) {

	  				// img ok

	  				next.call( null, true );
	  				clearInterval( clock );
	  			}

	  			else if ( ( new Date ).getTime() - now > ( timeout || 5000 ) ) {

	  				next.call( null, false );
	  				clearInterval( clock );
	  			}
	  		}, 16);
	  	};
	  }])
	  .factory( '$$fileUploader', [function() {


	  	return function( el, next ) {

	  		el.ondrop = function (e) {

	  		  e.preventDefault();

	  		  var file = e.dataTransfer.files[0];
	  		      
	  		  next.call( null, file );

	  		  return false;
	  		};
	  	};
	  }])
	  .factory( '$$formUploader', [function() {

	  	return function( form, next ) {

	  		var input = $.element( form ).find( 'input' )[0];

	  		input.onchange = function() {

	  			next.call( null, input.files[0] );

	  			return false;
	  		};
	  	};
	  }])
	  ;

})( angular );
