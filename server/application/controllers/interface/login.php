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

        if($curr_account == false){
            $result = array(
                "code" => 10001,
                "msg"  => "账户密码错误"
            );
        }else{
            unset($curr_account['password']);
            $result = array(
                "code" => 10000,
                "msg"  => "登陆成功",
                "data" => array($curr_account),
            );
            setcookie("login_info",serialize($curr_account),time()+86400);
        }

        echo json_encode($result);
    }
}