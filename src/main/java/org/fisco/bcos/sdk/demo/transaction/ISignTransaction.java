package org.fisco.bcos.sdk.demo.transaction;

public interface ISignTransaction {
	/*同步的签名接口，不需要传入回调，直接返回签名结果*/
	public String requestForSign(byte[] rawTxHash);
	/*异步的签名接口，传入回调，当远程调用签名服务时，可以采用异步回调风格，避免堵塞*/
	public void requestForSignAsync(byte[] rawTxHash,ISignedTransactionCallback callback);

}