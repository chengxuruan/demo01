
$(function () {
    $('#rate').radialIndicator();
    var val=$("#rate").attr("data-val");
    var radialObj=$("#rate").data('radialIndicator');
    radialObj.option("barColor","orange");
    radialObj.option("percentage",true);
    radialObj.option("barWidth",10);
    radialObj.option("radius",40);
    radialObj.value(val);

    $("#tabs div").click(function () {
        //点击添加样式
        $(this).addClass('tab_active');

        var show =$('#contents .tab_content').eq($(this).index());
        show.show();
        //清除其它非当前节点样式
        $('#tabs div').not($(this)).removeClass('tab_active');
        $('#contents .tab_content').not(show).hide();

        //当点击项目投资记录时
        if($(this).index()==2){
            /**
             * 获取项目投资记录
             *   ajax 拼接tr
             *    追加tr 到 recordList
             */
            // alert("投资用户列表");
            //loadInvestRecodesList($("#itemId").val());
        }
    });
});


//充值方法
function toRecharge(){
    $.ajax({
        type:"post",
        url:ctx+"/user/userAuthCheck",
        dataType:"json",
        success:function (data) {
            if (data.code==200){
                window.location.href = ctx+"/account/rechargePage";
            }else{
                layer.confirm(data.msg, {
                    btn: ['执行认证','稍后认证'] //按钮
                }, function(){
                    window.location.href=ctx+"/user/auth";
                });
            }
        }
    })
}


function doInvest() {
    var usableAmount = parseFloat($("#ye").attr("data-value"));//账户余额
    var amount = $("#usableMoney").val();//投资金额
    var itemId = parseInt($("#itemId").val());//投资人id

    if (usableAmount == 0) {
        layer.tips("可用余额不满足本次投资金额，请先进行充值操作!", "#tz");
        return;
    }

    if(isEmpty(amount)){
        layer.tips("请输入投资金额", "#usableMoney");
        return;
    }

    if(amount>usableAmount){
        layer.tips("投资金额不能大于账户可用余额", "#usableMoney");
        return;
    }

    // 起投金额
    var sinleMinInvestAmount=$("#minInvestMoney").attr("data-value");
    if (sinleMinInvestAmount>0 & amount<sinleMinInvestAmount){
        layer.tips("投资金额不能小于起投金额", "#usableMoney");
        return;
    }

    //最大投资金额
    var sinleMaxInvestAmount=parseFloat($("#maxInvestMoney").attr("data-value"));
    if(sinleMaxInvestAmount>0){
        if(amount>sinleMaxInvestAmount){
            layer.tips("投资金额不能大于单笔最大投标金额", "#usableMoney");
            return;
        }
    }


    //密码确认框
    layer.prompt({title: '请输入交易密码', formType: 1}, function(pass, index){
        layer.close(index);
        var businessPassword=pass;
        if(isEmpty(businessPassword)){
            layer.msg("交易密码不能为空!");
            return;
        }


        $.ajax({
            type:"post",
            url:ctx+"/busItemInvest/userInvest",
            data:{
                itemId:itemId,
                amount:amount,
                businessPassword:businessPassword
            },
            dataType:"json",

            success:function (data) {
                if (data.code==200){
                    layer.msg("项目投标成功!");
                }else {
                    layer.msg(data.msg);
                }
            }
        })
    });
}
