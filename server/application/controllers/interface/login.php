<?php
class Login extends CI_Controller {

    public function index()
    {
        $this->load->helper("SSO");
        $user = $this->input->get("user");
        $pwd  = $this->input->get("pwd");
        $this->config->load('account');
        $accounts = $this->config->item("account");
        $curr_account = false;
        foreach($accounts[0] as $account){
            if($user == $account['account'] && $pwd == $account['password']){
                $curr_account = $account;
                break;
            }
        }

        //输出密码错误
        if($curr_account == false){
            $result = array(
                "code" => 10001,
                "msg"  => "error in password"
            );
            echo json_encode($result);
            return;
        }

        //客户端没有登陆
        $this->load->driver('cache');
        $key = "ip_".$curr_account['id'];
        $ip = $this->cache->file->get($key);
        if(empty($ip)){
            $result = array(
                "code" => 10002,
                "msg"  => "Client Not login"
            );
            echo json_encode($result);
            return;
        }

        //输出登陆成功
        $curr_account['ip'] = $ip;
        unset($curr_account['password']);
        $result = array(
            "code" => 10000,
            "msg"  => "success",
            "data" => $curr_account,
                );

        setcookie("login_info",serialize($curr_account),time()+86400);
        echo json_encode($result);



    }
}