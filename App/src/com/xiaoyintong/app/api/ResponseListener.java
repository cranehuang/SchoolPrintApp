package com.xiaoyintong.app.api;


public interface ResponseListener {
	/**正确返回请求结果时的处理方法
	 * @param info json格式的正确信息*/
    public void processResponse(String info);
    
   /** 请求结束时，相应的处理，如UI的重置等*/
    public void requestFinished(boolean isSucceed);
    
}
