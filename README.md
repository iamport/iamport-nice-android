# iamport-nice-android
아임포트를 활용해 나이스정보통신 결제연동을 위한 안드로이드 샘플 프로젝트입니다. 

Graddle 프로젝트는 [iamport-nice-android](https://github.com/iamport/iamport-nice-android-graddle) 에서 확인 가능합니다.  

## Usage  

```java
niceClient = new NiceWebViewClient(this, mainWebView);
mainWebView.setWebViewClient(niceClient);
```

## Cookie 허용  
일부 카드사의 경우 결제모듈 연동을 위해 브라우저 Cookie를 필수로 사용하게 됩니다. 또한, http, https 컨텐츠를 모두 사용가능하도록 아래와 같이 허용 코드를 추가해주어야 합니다. 

```java
if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
	settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
	CookieManager cookieManager = CookieManager.getInstance();
	cookieManager.setAcceptCookie(true);
	cookieManager.setAcceptThirdPartyCookies(mainWebView, true);
}
```

## niceMobileV2  
결제데이터 동기화가 원활하게 이뤄지도록 나이스 결제모듈 v2를 적용하고 있습니다. (Legacy를 위해 `IMP.request_pay()` 호출 시 `niceMobileV2 : true` 파라메터를 적용한 경우)

`niceMobileV2 : true` 파라메터인 경우에는 `NiceWebViewClient` 를 다음과 같이 수정해 사용하셔야 합니다.  

```java
String reqParam = makeBankPayDataV2(url);
```