'use strict';

/* Controllers */
(function( $ ) {

	$.module('skynet.controllers', ['skynet.services', 'skynet.directives']).
	  controller('index', ['$scope', '$location', '$$cookie', function( $scope, $location, $$cookie ) {
	  	// 是否已登陆
	  	var is_login = $$cookie.get( 'login' ) == 1;

	  	if ( !is_login ) {

	  		$location.path( '/login' );
	  		return;
	  	}

	  	else {

	  		$location.path( '/desktop' );
	  		return;
	  	}

	  }])
	  .controller('login', ['$scope', '$rootScope', '$location', '$http', '$$cookie', function( $scope, $rootScope, $location, $http, $$cookie ) {
	  	// 登陆页
	  	var is_login = $$cookie.get( 'login' ) == 1;

	  	if ( is_login ) {

	  		$location.path( '/' );
	  		return;
	  	}

	  	var login_ok_next = function( res ) {

	  		$scope.log = '';
	  		$scope.login_fail = false;

	  		$.forEach( $.extend( res.data, { login: 1 } ), function( value, key ) {

	  			$$cookie.set( key, value );
	  		});
	  		$location.path( '/desktop' );
	  		$rootScope.$broadcast( 'user_login' );
	  	};

	  	// 设置controller逻辑

	  	$scope.login_fail = false;
	  	$scope.log = '';

	  	$scope.form = {

	  		user:'',
	  		pwd: ''
	  	};

	  	$scope.qr_src = '';

	  	$scope.submit = function( form ) {

	  		// validate
	  		if ( !form.user || !form.pwd ) {

	  			return;
	  		}

	  		$http({

	  			method: 'GET',
	  			params: form,
	  			url: '/interface/login'
	  			// headers: {

	  			// 	'Content-Type': 'application/x-www-form-urlencoded'
	  			// }

	  		}).success( function( res ) {


	  			if ( res.code == 10000 ) {

	  				login_ok_next( res );
	  			}

	  			else {

	  				// show msg 
	  				$scope.login_fail = true;
	  				$scope.log = res.code == 10001 ? '密码不正确，请重新输入' : res.code == 10002 ? '请先在手机应用中登陆' : '未知错误' ;
	  			}
	  		});
	  	};

	  	// init qr

	  	$http({

	  		method: 'GET',
	  		url: '/interface/barcodeget'
	  	}).success( function( res ) {

	  		$scope.qr_src = res.url;

	  		var clock = setInterval( function() {

	  			$http({

	  				method: 'GET',
	  				params: { token: res.code },
	  				url: '/interface/barcodecheck',
	  				timeout: 1000
	  			}).success( function( res ) {

	  				if ( res.code == 10000 ) {

	  					clearInterval( clock );
	  					login_ok_next( res );
	  				}
	  			})
	  		}, 1000 );
	  	});

	  }])
	  .controller( 'desktop', ['$scope', '$location', '$http', '$$cookie', '$$jsonp_req_url', '$$poll', '$$loadImg', '$$uid', '$$fileUploader', '$$formUploader', function( $scope, $location, $http, $$cookie, $$jsonp_req_url, $$poll, $$loadImg, $$uid, $$fileUploader, $$formUploader ) {

	  	// 是否已登陆
	  	var is_login = $$cookie.get( 'login' ) == 1;

	  	if ( !is_login ) {

	  		$location.path( '/login' );
	  		return;
	  	}

	  	var GB = Math.pow( 1024, 3 );
	  	var path_device_info = '/device/get';
	  	var path_send_url = '/url/set';
	  	var path_read_clipboard = '/clipboard/get';
	  	var path_push_clipboard = '/clipboard/set';
	  	var path_camera_shot = '/camera/picture';
	  	var path_get_geo = '/gps/gps';
	  	var path_to_apk  = '/upload?dir=apk';
	  	var path_to_install = '/apk/install';
	  	var path_to_file = '/upload?dir=file';
	  	var path_to_screen = '/screen/shot ';

	  	var parse_data = function( data ) {

	  		var _info = $scope.device_info;

	  		$.forEach( data, function( value, key ) {

	  			_info[key] = value;
	  		});

	  		_info.memory_avail = ( _info.InterAvailSize / GB ).toFixed(2);
	  		_info.memory_total = ( _info.InterTotalSize / GB ).toFixed(2);

	  		_info.sd_avail = ( _info.ExternAvailSize / GB ).toFixed(2);
	  		_info.sd_total = ( _info.ExternTotalSize / GB ).toFixed(2);

	  		_info.w_memory_avail = ~~( _info.memory_avail * 202 / _info.memory_total );
	  		_info.w_memory_left = ~~( ( _info.memory_total - _info.memory_avail ) * 202 / _info.memory_total );

	  		_info.w_sd_avail = ~~( _info.sd_avail * 202 / _info.sd_total );
	  		_info.w_sd_left  = ~~( ( _info.sd_total - _info.sd_avail ) * 202 / _info.sd_total );
	  	};
	  	var genUrl = function( path ) {

	  		return $$jsonp_req_url( $scope.mobile_host, path );
	  	};
	  	var getImgUrl = function() {

	  		return 'http://' + $scope.mobile_host + path_camera_shot + '?_id=' + $$uid();
	  	};

	  	var map;

	  	// file upload

	  	var file_upload_next = function( file ) {

	  		var data = new FormData();

	  		data.append( 'file', file );

	  		var xhr = new XMLHttpRequest();
	  		xhr.open( 'POST', 'http://' + $scope.mobile_host + path_to_file, true );
	  		xhr.send( data );
	  	};

	  	var apk_uploader_next = function( file ) {

	  		var filename = file.name;
	  		var data = new FormData();

	  		data.append( 'apk', file );

	  		var xhr = new XMLHttpRequest();
	  		xhr.open( 'POST', 'http://' + $scope.mobile_host + path_to_apk, true );
	  		xhr.onreadystatechange = function() {

	  			if ( xhr.readyState == 4 ) {

	  				$http.
	  				jsonp( genUrl( path_to_install ) + '&apk=' + filename );
	  			}
	  		};
	  		xhr.send( data );
	  	};

	  	// 设备简历
	  	$scope.mobile_host = decodeURIComponent( $$cookie.get( 'ip' ) );
	  	$scope.device_info = {};

	  	$http
	  	.jsonp( genUrl( path_device_info ) )
	  	.success( function( res ) {

	  		parse_data( res.data );
	  	});

	  	// 工具箱


	  	$scope.hide = {
	  		file: '',
	  		url: 'is-hide',
	  		clipboard: 'is-hide',
	  		app: 'is-hide'
	  	};
	  	$scope.active = {
	  		file: 'active',
	  		url: '',
	  		clipboard: '',
	  		app: ''
	  	};
	  	$scope.anchor_left = 36;
	  	$scope.tab = function( which ) {

	  		$.forEach( $scope.hide, function( value, key ) {

	  			$scope.hide[key] = which == key ? '' : 'is-hide';
	  			$scope.active[key] = which == key ? 'active' : '';
	  		});

	  		if ( which == 'file' ) $scope.anchor_left = 36;
	  		if ( which == 'url' ) $scope.anchor_left = 102;
	  		if ( which == 'clipboard' ) $scope.anchor_left = 168;
	  		if ( which == 'apk' ) $scope.anchor_left = 233;
	  	};

	  	$scope.air_url = '';
	  	$scope.send_url = function() {

	  		$http
	  		.jsonp( genUrl( path_send_url ) + '&url=' + encodeURIComponent( $scope.air_url ) );
	  		$scope.air_url = '';
	  	};


	  	$scope.air_clipboard = '';
	  	$scope.read_clipboard = function() {

	  		$http
	  		.jsonp( genUrl( path_read_clipboard ) )
	  		.success( function( res ) {

	  			res.data && ( $scope.air_clipboard = res.data );
	  		});
	  	};
	  	$scope.push_clipboard = function() {

	  		$scope.air_clipboard && $http.jsonp( genUrl( path_push_clipboard ) + '&text=' + encodeURIComponent( $scope.air_clipboard ) );
	  		$scope.air_clipboard = '';
	  	};

	  	$$formUploader( document.getElementById( 'form_input'), file_upload_next );
	  	$$fileUploader( document.getElementById( 'form_uploader'), file_upload_next );

	  	$$formUploader( document.getElementById( 'apk_input'), apk_uploader_next );
	  	$$fileUploader( document.getElementById( 'apk_uploader'), apk_uploader_next );

	  	// btns

	  	$scope.show_camera = 0;
	  	$scope.is_ready = 1;
	  	$scope.not_ready = 0;
	  	$scope.open_camera = function() {


	  		var clock;
	  		var img = document.getElementById( 'camera_img' );
	  		var loadImg = function() {

	  			$$loadImg( img, getImgUrl(), function( ok ) {
	  				
	  				if ( !$scope.show_camera ) return;

	  				clearTimeout( clock );
	  				clock = setTimeout( function() {

  						loadImg();
  					}, 500 );
	  			}, 500);
	  		};
	  		// $scope.is_ready = 0;
	  		// $scope.not_ready = 1;
	  		$scope.show_camera = 1;
	  		loadImg();
	  	};
	  	$scope.close_camera = function() {

	  		$scope.show_camera = 0;
	  	};

	  	$scope.show_map = 0;
	  	$scope.open_map = function() {


	  		$http
	  		.jsonp( genUrl( path_get_geo ) )
	  		.success( function( res ) {

	  			var point;
	  			var lng = res.altitude;
	  			var lat = res.latitude;

	  			$scope.show_map = 1;

	  			setTimeout( function() {

	  				if ( !map ) {

	  					map = new BMap.Map( 'map_box' );
	  					map.enableDragging();
	  					map.enableScrollWheelZoom();
	  					map.addControl( new BMap.NavigationControl() );  //添加默认缩放平移控件
	  				}
	  				point = new BMap.Point( lng, lat );
	  				map.centerAndZoom( point, 16 );
	  				map.addOverlay( new BMap.Marker( point ) );

	  			}, 0);
	  		});
	  	};
	  	$scope.close_map = function() {

	  		$scope.show_map = 0;
	  	};


	  	$scope.screen_show = 0;
	  	$scope.screen_src = '';
	  	$scope.screen_shot = function() {
	  		$scope.screen_show = 1;
	  		var img = document.getElementById( 'screen_img' );
	  		var win = document.getElementById( 'drag_ss_win' );
	  		$$loadImg( img, 'http://' + $scope.mobile_host + path_to_screen, function( ok ) {

	  			if ( ok ) {
	  				win.style.height = img.naturalHeight * 400 / img.naturalWidth + 'px';
	  			}
	  		}, 10000);
	  	};
	  	$scope.close_screen = function() {

	  		$scope.screen_show = 0;
	  	};
	  }])
	  .controller( 'taskbar', ['$scope', '$http', '$location', '$$cookie', '$$jsonp_req_url', '$$poll', function( $scope, $http, $location, $$cookie, $$jsonp_req_url, $$poll ) {

	  	// 初始化

	  	var host = decodeURIComponent( $$cookie.get( 'ip' ) );
	  	var path_device_info = '/device/get';
	  	var cron_id;

	  	var refresh = function() {

	  		$scope.task_show = true;
	  		// cron_id = $$poll( function() {
	  			$http
	  			.jsonp( $$jsonp_req_url( host, path_device_info ) )
	  			.success( function( res ) {

	  				var data = res.data;

	  				$scope.battery_level = data.BatteryLevel;
	  				$scope.battery_level_w = + data.BatteryLevel / 2;
	  				$scope.wifi = data.wifiSSID;
	  			});
	  		// }, 10*1000 );
	  	};

	  	$scope.task_show = false;
	  	$scope.battery_level = 0;
	  	$scope.battery_level_w = 0;
	  	$scope.wifi = '';

	  	if ( host ) {

	  		refresh();
	  	}

	  	$scope.$on( 'user_login', function() {

	  		host = decodeURIComponent( $$cookie.get( 'ip' ) );
	  		refresh();
	  	});

	  	$scope.logout = function() {

	  		$http({

	  			method: 'GET',
	  			url: '/interface/logout'
	  		});

	  		$$cookie.remove( 'login' );
	  		$$cookie.remove( 'ip' );
	  		$location.path( '/' );
	  		$scope.task_show = false;

	  		window.location.reload();
	  	};

	  }])
.controller('Ctrl2', function($scope) {
    $scope.format = 'M/d/yy h:mm:ss a';
  })
  ;

})( angular );
