<?php
class Logout extends CI_Controller {

    public function index()
    {
        setcookie("login_info",'',0);
        $result = array(
            "code" => 10000,
            "msg"  => "注销成功",
        );
        echo json_encode($result);
    }
}