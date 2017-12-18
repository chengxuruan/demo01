
/**
 *快捷登录
 */
$(function () {
    // 给图片标签绑定点击事件
    $(".validImg").click(function () {
        //alert("aaaa")
        swithPicCode();
    });

    //点击更换图片
    function swithPicCode(){
        $(".validImg").attr("src",ctx+"/img/getPictureVerifyImage?time="+new Date());

    }


    /**
     * 获取短信验证码
     */
    $("#clickMes").click(function () {

        var phone = $("#phone").val();
        var picCode = $("#code").val();

        if (isEmpty(phone)){
            layer.tips("手机号不能为空!","#phone");
            return;
        }

        if (isEmpty(picCode)){
            layer.tips("图片验证码不能为空!!","#code");
            return;

        }
        var _this=$(this);

        /**
         * 发送ajax 请求手机短信发送接口
         */
        $.ajax({
            type:"post",
            url:ctx+"/sms/sendPhoneSms",
            data:{
                phone:phone,
                picCode:picCode,
                type:2
            },
            dataType:"json",
            success:function (data) {
                if (data.code==200){
                    time(_this);
                }else {
                    layer.tips(data.msg,"#clickMes");
                }
            }
        })
    });


    var wait=6;
    function time(nodes) {
        if (wait==0){
            nodes.removeAttr("disabled");
            nodes.val('获取验证码');
            nodes.css("color", '#ffffff');
            nodes.css("background","#fcb22f");
            wait = 6;
        }else {
            nodes.attr("disabled", true);
            nodes.css("color", '#fff');
            nodes.css("background", '#ddd');
            nodes.val("重新发送(" + wait + "s)");
            wait--;

            setTimeout(function () {
                time(nodes);
            },1000)
        }
    }



    //点击登录
    $("#login").click(function () {
        var phone = $("#phone").val();
        var picCode = $("#code").val();

        if(isEmpty(phone)){
            layer.tips("手机号不能为空!","#phone");
            return;
        }
        if(isEmpty(picCode)){
            layer.tips("验证码不能为空!",".validImg");
            swithPicCode();
            return;
        }
        var code=$("#verification").val();
        if(isEmpty(code)){
            layer.tips("手机验证码不能为空!","#verification");
            return;
        }

        var params={};
        params.phone=phone;
        params.picCode=picCode;
        params.code=code;

        $.ajax({
            type:"post",
            url:ctx+"/user/quickLogin",
            data:params,
            dataType:"json",
            success:function (data) {
                if (data.code == 200){
                    window.location.href=ctx+"/index";
                }else {
                    layer.tips(data.msg,"#login");
                }
            }
        })

    })
})