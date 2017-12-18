<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>投资列表</title>
    <link rel="stylesheet" href="${ctx}/css/reset.css">
    <link rel="stylesheet" href="${ctx}/css/common.css">
    <link rel="stylesheet" href="${ctx}/css/investmentList.css">
    <link rel="stylesheet" href="${ctx}/css/page.css">
    <script type="text/javascript" src="${ctx}/js/assets/jquery-3.1.0.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/common.js"></script>
    <script type="text/javascript" src="${ctx}/js/layer/layer.js"></script>
    <script type="text/javascript" src="${ctx}/js/assets/radialIndicator.js"></script>
    <script type="text/javascript" >
        var ctx="${ctx}";
    </script>
    <script type="text/javascript" src="${ctx}/js/invest.list.js"></script>
</head>

<body>
<#include "../include/header.ftl">

<div class="list_container">
    <div class="date_tab" id="dateTab">
        <div>
            <div class="fl ketou" id="validItem" style="">
                <div class="tab list_active" id="quanbu">全部期限</div>
                <div class="tab">0-30天</div>
                <div class="tab">30-90天</div>
                <div class="tab">90天以上</div>
                <div class="tab">历史项目</div>
            </div>
        </div>
    </div>
    <table class="list_table">
        <thead>
        <tr class="table_header">
            <th style="width: 196px;" id="itemRate" class="order_item ketou" data-name="item.item_rate">
                预期年化收益</th>
            <th id="itemCycle" class="order_item ketou" data-name="item.item_cycle" style="">期限</th>
            <th class="lishi" style="display: none;">预期年化收益</th>
            <th class="lishi" style="display: none;">期限</th>
            <th>
                <span class="lishi" style="display: none;">产品</span>
                <select id="itemType" class="ketou" style=""  onchange="initItemData(this.value)">
                    <option value="">产品</option>
                    <option value="2">车商宝</option>
                    <option value="5">车信宝</option>
                    <option value="3">车贷宝</option>
                    <option value="1">学车宝</option>
                </select>
            </th>
            <th>信用等级</th>
            <th>担保机构</th>
            <th>投资进度</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody id="pcItemList">
        </tbody>
    </table>
    <div class="pages">
        <nav>
         <ul id="pages" style="margin:75px auto 110px;" class="pagination">

            </ul>
        </nav>
    </div>
</div>


</body>
</html>