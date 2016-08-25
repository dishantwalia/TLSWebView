# TLSWebView

## Introduction

This is open-source project for everyone who need to use WebView and server authentication requires TLSv1.2.

## Used libs

It used [OkHttp](https://github.com/square/okhttp) to create HttpClient.

## How to do it
First you must to create your own HttpClient.

```java
private WebViewClient buildClient() {
    return new WebViewClient() {
        private OkHttpClient okHttp = new OkHttpClient.Builder().sslSocketFactory(new TLSSocketFactory(), TLSSocketFactory.getTrustManager()).build();

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            Request okHttpRequest = new Request.Builder().url(url).build();
            try {
                Response response = okHttp.newCall(okHttpRequest).execute();
                return new WebResourceResponse("text/html", "UTF-8", response.body().byteStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    };
}
```
    
The most important thing is to build OkHttpClient with your own SocketFacotry. Here you can see all code from TLSSocketFatory class:
    
 ```java
public class TLSSocketFactory extends SSLSocketFactory {

    private static final String TAG = "TLSSocketFactory";
    
    private SSLSocketFactory internalSSLSocketFactory;
    
    public TLSSocketFactory() {
        SSLContext context = getSslContext();
        internalSSLSocketFactory = context.getSocketFactory();
    }
    
    @Override
    public String[] getDefaultCipherSuites() {
        return internalSSLSocketFactory.getDefaultCipherSuites();
    }
    
    @Override
    public String[] getSupportedCipherSuites() {
        return internalSSLSocketFactory.getSupportedCipherSuites();
    }
    
    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(s, host, port, autoClose));
    }
    
    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(host, port));
    }
    
    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(host, port, localHost, localPort));
    }
    
    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(host, port));
    }
    
    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(address, port, localAddress, localPort));
    }
    
    private Socket enableTLSOnSocket(Socket socket) {
        if(socket != null && (socket instanceof SSLSocket)) {
            ((SSLSocket)socket).setEnabledProtocols(new String[] {"TLSv1.1", "TLSv1.2"});
        }
        return socket;
    }
    
    public static X509TrustManager getTrustManager() {
        return new X509TrustManager() {
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }
    
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
    
            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
        };
    }
    
    public static SSLContext getSslContext() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, null);
            return sslContext;
        } catch (Exception ex) {
            Log.e(TAG, "Problem while gettinh SslContext", ex);
            throw new RuntimeException(ex);
        }
    }
}
```

And that is all ;)
