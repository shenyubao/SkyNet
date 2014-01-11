<?php  if (!defined('BASEPATH')) exit('No direct script access allowed');
class Qrregister{
    private static $pool = array();

    public static function register(){
        $code = md5(time() . rand(0,1000));

    }
}