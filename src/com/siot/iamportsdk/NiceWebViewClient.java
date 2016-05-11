package com.siot.iamportsdk;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class NiceWebViewClient extends WebViewClient {

	private Activity activity;
	private WebView target;
	private String BANK_TID = "";
	
	final int RESCODE = 1;
	final String NICE_URL = "https://web.nicepay.co.kr/smart/interfaceURL.jsp";			// NICEPAY SMART 요청 URL
	final String NICE_BANK_URL = "https://web.nicepay.co.kr/smart/bank/payTrans.jsp";	// 계좌이체 거래 요청 URL
	final String KTFC_SCHEME = "kftc-bankpay";
	final String KTFC_PACKAGE = "com.kftc.bankpay.android";
	
	public NiceWebViewClient(Activity activity, WebView target) {
		this.activity = activity;
		this.target = target;
	}
	
	public void bankPayPostProcess(String bankpayCode, String bankpayValue) {
		String postData = "callbackparam2="+BANK_TID+"&bankpay_code="+bankpayCode+"&bankpay_value="+bankpayValue;
		target.postUrl(NICE_BANK_URL,EncodingUtils.getBytes(postData,"euc-kr"));
	}
	
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		
		if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("javascript:")) {
			Intent intent = null;
			
			try {
				/* START - BankPay(실시간계좌이체)에 대해서는 예외적으로 처리 */
				if ( url.startsWith(KTFC_SCHEME) ) {
					if ( isPackageInstalled("com.kftc.bankpay.android") ) {
						try {
							String reqParam = makeBankPayData(url);
							
							intent = new Intent(Intent.ACTION_MAIN);
		                    intent.setComponent(new ComponentName("com.kftc.bankpay.android","com.kftc.bankpay.android.activity.MainActivity"));
		                    intent.putExtra("requestInfo",reqParam);
		                    activity.startActivityForResult(intent,RESCODE);
		                    
		                    return true;
						} catch (URISyntaxException e) {
							return false;
						}
					} else {
						activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + KTFC_PACKAGE)));
						return true;
					}
				}
				/* END - BankPay(실시간계좌이체)에 대해서는 예외적으로 처리 */
				
				intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME); //IntentURI처리
				Uri uri = Uri.parse(intent.getDataString());
				
				activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
				return true;
			} catch (URISyntaxException ex) {
				return false;
			} catch (ActivityNotFoundException e) {
				if ( intent == null )	return false;
				
				String packageName = intent.getPackage();
		        if (packageName != null) {
		            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
		            return true;
		        }
		        
		        return false;
			}
		}
		
		return false;
	}
	
	protected boolean handle3rdPartyPaymentScheme(String scheme) {
		
		
		return false;
	}
	
	private String makeBankPayData(String url) throws URISyntaxException {
		BANK_TID = "";
		List<NameValuePair> params = URLEncodedUtils.parse(new URI(url), "UTF-8");
		
		StringBuilder ret_data = new StringBuilder();
		List<String> keys = Arrays.asList(new String[] {"firm_name", "amount", "serial_no", "approve_no", "receipt_yn", "user_key", "callbackparam2", ""}); 
		
		String k,v;
		for (NameValuePair param : params) {
			k = param.getName();
			v = param.getValue();
			
			if ( keys.contains(k) ) {
				if ( "user_key".equals(k) ) {
					BANK_TID = v;
				}
				ret_data.append("&").append(k).append("=").append(v);
			}
		}
		
		ret_data.append("&callbackparam1="+"nothing");
		ret_data.append("&callbackparam3="+"nothing");
		
    	return ret_data.toString();
	}
	
	private boolean isPackageInstalled(String pkgName) {
		try {
			activity.getPackageManager().getPackageInfo(pkgName, PackageManager.GET_ACTIVITIES);
			return true;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
}
