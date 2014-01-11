<?php
class Barcodelogin extends CI_Controller {

	public function index()
	{
		$this->load->helper("SSO");
        $this->load->driver('cache');
		$link = array();
		$ip   	= $this->input->get("ip");
		$port 	= $this->input->get("port");
		$user 	= $this->input->get("user");
		$token	= $this->input->get("token");
        $pwd    = $this->input->get("pwd");

        $this->config->load('account');
        $accounts = $this->config->item("account");
        $current_account = $accounts[$user];
        if(empty($token)|| empty($current_account) || $current_account['password'] != $pwd){
			$result = array(
                "code" => 10001,
                "msg"  => "登陆失败",
                "data" => $link,
			);
		}else{
            $host = $ip.":".$port;
            $data = array("user" => $user,"host"=>$host);
            $this->cache->file->save($token,$data,180);

			$result = array(
	                "code" => 10000,
	                "msg"  => "登陆成功",
			);
		}

		echo json_encode($result);
	}
}