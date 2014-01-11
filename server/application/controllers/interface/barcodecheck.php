<?php
class Barcodecheck extends CI_Controller {

    public function index()
    {
        $this->load->driver('cache');
        $token= $this->input->get("token");
        $result = $this->cache->file->get($token);
        if($result == false){
            $result = array(
                "code" => 10001,
                "msg"  => "not login"
            );
        }else{
            $this->config->load('account');
            $accounts = $this->config->item("account");
            $curr_account = $accounts[$result['user']];
            $curr_account['ip'] = $result['host'];
            if($curr_account){
                $result = array(
                    "code" => "10000",
                    "data" => $curr_account,
                );
            }else{
                $result = array(
                    "code" => "10002",
                    "msg" => "system error",
                );
            }
        }

        echo json_encode($result);
        return;
    }
}