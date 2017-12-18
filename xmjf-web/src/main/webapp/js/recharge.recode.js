

$(function () {
   // var pageNum =1;
    loadRechargeRecodesData();//
});

function loadRechargeRecodesData(pageNum ) {

    var pama = {};
        pama.pageNum=pageNum;
    $.ajax({
        type:"post",
        url:ctx+"/account/queryRechargeRecodesByUserId",
        dataType:"json",
        data:pama,
        success:function (data) {
            var paginator = data.paginator;// 分页信息
            var list = data.list;

            if (list.length>0){
                initDivsHtml(list);//动态添加节点记录

                initNavigatePages(paginator)//动态添加分页记录
            }else{
                alert("记录不存在");
            }
        }
    })
}

//dom动态添加节点记录

function initDivsHtml(list) {
    if (list.length>0){
        var divs = "";

        for (var j=0;j<list.length;j++){
            var tempData = list[j];
            //拼接
            divs=divs+"<div class='table-content-first'>";
            divs=divs+tempData.auditTime+"</div>";//时间

            divs=divs+"<div class='table-content-center'>";
            divs=divs+tempData.actualAmount+"元"+"</div>";//金额

            divs=divs+"<div class='table-content-first'>";//支付状态
            var status = tempData.status;
            if (status==0){
                divs=divs+"支付失败";
            }
            if(status==1){
                divs=divs+"已支付";
            }
            if(status==2){
                divs=divs+"待支付";
            }
            divs = divs+"</div>";
        }
        console.log(divs)
        $("#rechargeList").html(divs);//设置该节点的类容
    }
}

//动态添加分页记录
function initNavigatePages(paginator) {

    var pageNums = paginator.navigatepageNums;
    if (pageNums.length>0){
        var listTemp = "";

        for (var j=0; j<pageNums.length; j++){
            var page = pageNums[j];

            var href = "javascript:toPageData("+page+")";
            if (page==paginator.pageNum){
                listTemp = listTemp+"<li class='active'><a href='"+href+"' title='第"+page+"页' >"+page+"</a></li>";
            }else{
                listTemp = listTemp+"<li ><a href='"+href+"' title='第"+page+"页' >"+page+"</a></li>";
            }
        }
           // alert(listTemp);
        $("#pages").html(listTemp);//追加分页节点
    }
}

/*function initNavigatePages(paginator) {
    var navigatepageNums=paginator.navigatepageNums;// 数组
    if(navigatepageNums.length>0){
        /!**
         * 拼接导航页内容
         *!/
        var lis="";
   var href=
        /!**
         * 首页
         * 上一页
         * 下一页
         * 末页
         *!/
        if(!paginator.isFirstPage){
            lis=lis+"<li ><a href='javascript:toPageDate(1) title='首页' >首页</a></li>";
        }
        if(paginator.hasPreviousPage){
            lis=lis+"<li ><a href='javascript:toPageDate("+(paginator.pageNum-1)+")' title='上一页' >上一页</a></li>";
        }
        for(var i=0;i<navigatepageNums.length;i++){
            var page=navigatepageNums[i];
            var href="javascript:toPageDate("+page+")";
            if(paginator.pageNum==page){
                lis=lis+"<li class='active'><a href='"+href+"' title='第"+page+"页' >"+page+"</a></li>";
            }else{

                lis=lis+"<li ><a href='"+href+"' title='第"+page+"页' >"+page+"</a></li>";
            }
        }
        if(paginator.hasNextPage){
            lis=lis+"<li ><a href='javascript:toPageDate("+(paginator.pageNum+1)+")' title='下一页' >下一页</a></li>";
        }
        if(!paginator.isLastPage){
            lis=lis+"<li ><a href='javascript:toPageDate("+(paginator.lastPage)+")'title='末页' >末页</a></li>";
        }
        $("#pages").html(lis);
    }
}*/

//切换页码 重新刷新列表内容
function toPageData(pageNum) {

    loadRechargeRecodesData(pageNum );//分页

}