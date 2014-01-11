<?php
class Barcodeget extends CI_Controller {

    public function index()
    {
        $this->load->driver('cache');
        $code = md5(time() . rand(0,1000));
        $this->cache->file->save($code,false,180);
        $url = "http://chart.googleapis.com/chart?cht=qr&chs=200x200&choe=UTF-8&chld=L|4&chl=" . $code;
        $data = array("url"=>$url,"code"=>$code);
        echo json_encode($data);

    }
}