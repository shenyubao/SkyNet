<?php
class Clientlogin extends CI_Controller {

    public function index()
    {
        $this->load->driver('cache');
        $this->load->helper("SSO");
        $user = $this->input->get("user");
        $pwd  = $this->input->get("pwd");
        $ip   = $this->input->get("ip");

        $this->config->load('account');
        $accounts = $this->config->item("account");
        $curr_account = $accounts[$user];

        if($curr_account == false){
            $result = array(
                "code" => 10001,
                "msg"  => "error in password"
            );
        }else{
            unset($curr_account['password']);
            $result = array(
                "code" => 10000,
                "msg"  => "success",
            );
        }

        $key = "ip_".$curr_account['id'];
        $this->cache->file->save($key,$ip,86400);

        echo json_encode($result);
    }
}