dropLoadUtil={
		$dropParams:null,
		initDropLoad : function(dropParams) {
			var defaults_dropParams = {
					pageNumber : 0,//默认 0
					limit : 15,//默认15 条一页
					url : "",//请求的url地址
					$dropload:"",//dropload jquery 元素
					$e:"",//要显示的数据的 jquery元素
					callback:""	//回调函数
				};
			$dropParams = $.extend(defaults_dropParams, dropParams);//拷贝
			_prepare();
		}
}
var _load=function (me) {
	$dropParams.pageNumber++;
    var data = {pageNumber: $dropParams.pageNumber, limit : $dropParams.limit};
    $.ajax({
    	type : "POST",
    	url: $dropParams.url,
        data : data,
   		dataType: 'json',
        success : function(data) {
            if (data.status==200) {
                _loadData(data.result);
            } else {
            	Utils.alert("查询数据异常，请联系客服");
            }
            me.resetload();
            if (loadEnd && $dropParams.pageNumber!=1) {
                me.lock();
                me.noData();
                me.resetload();
            } else {
                me.unlock();
                me.noData(false);
            }
            return;
        },
        error : function () {
        	Utils.alert("服务异常，请联系客服");
            me.resetload();
            return false;
        }
    });
}
//dropload core code
var _prepareEvent=function () {
    $($dropParams.$dropload).dropload({
        scrollArea : window,
        loadUpFn : function(me) {
            _prepareData();
            _load(me);
        },
        loadDownFn : function(me) {
            _load(me);
        },
        threshold : 50,
        distance:50 //拉动距离
    });
}
var _prepareData=function () {
	$dropParams.pageNumber = 0;
	loadEnd = false;
}
var _prepare=function () {
	_prepareData();
	_prepareEvent();
}
var _loadData=function(list) {
    var html = '';
    if (list.length>0) {
    	if($dropParams.callback){
    		html=$dropParams.callback(list);
    	}
    }
    if ($dropParams.pageNumber == 1) {
        $(window).scrollTop(0);
        $($dropParams.$e).empty();
    }
    $($dropParams.$e).append(html);
    if ($dropParams.limit > ifNullReturn(list.length,0)) {
		loadEnd = true;
	}
}