<?php
if ( ! function_exists('getLoginInfo')){
function getLoginInfo(){
    $logininfo = unserialize($_COOKIE['login_info']);
    if(empty($logininfo)){
        return false;
    }
    return $logininfo;
}
}
