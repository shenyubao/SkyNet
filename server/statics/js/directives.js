'use strict';

/* Directives */

(function ( $ ) {

	$.module('skynet.directives', []).
	    directive('xdrag', function ( $compile ) {

		  function link( scope, element, attrs ) {

		  	var start = { x: 0, y: 0 };
		  	var dragging = false;
		  	var dragged = document.getElementById( attrs.xdrag );

		  	var drag_do = function( e ) {

		  		var offsetX = e.x - start.x;
		  		var offsetY = e.y - start.y;

		  		dragged.style.webkitTransform = 'translate3d('+offsetX+'px,'+offsetY+'px, 0)';
		  	};

		  	var drag_end = function( e ) {

		  		dragging = false;
		  		dragged.style.webkitTransform = '';

		  		var offsetX = e.x - start.x;
		  		var offsetY = e.y - start.y;

		  		var nowX = +dragged.style.left.replace( 'px', '' );
		  		var nowY = +dragged.style.top.replace( 'px', '' );

		  		dragged.style.left = nowX + offsetX + 'px';
		  		dragged.style.top  = nowY + offsetY + 'px';

		  		$.element( document ).unbind( 'mousemove', drag_do );
		  	};

		  	element.bind( 'mousedown', function( e ) {

		  		dragging = true;
		  		start.x = e.x;
		  		start.y = e.y;

		  		$.element( document ).bind( 'mousemove', drag_do );

		  		
		  	});

		  	$.element( dragged ).bind( 'mouseup', drag_end );
	      };
	   
	      return {
	        link: link
	      };
		});
})( angular );

