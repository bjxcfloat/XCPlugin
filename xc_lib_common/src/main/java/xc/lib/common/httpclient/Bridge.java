package xc.lib.common.httpclient;

import xc.lib.common.httpclient.json.IJsonStrategy;

// 通信层入口
public final class Bridge {


    private static Bridge bridge = null;
    private IHttpClientStrategy httpClientStrategy = null;
    private IJsonStrategy jsonStrategy = null;
    private String baseUrl = null;
    private String charset = "utf-8";

    private Bridge() {

    }

    public static Bridge getInstance() {
        synchronized (Bridge.class) {
            if (bridge == null)
                bridge = new Bridge();
        }
        return bridge;
    }

    public IHttpClientStrategy getHttpClientStrategy() {
        return this.httpClientStrategy;
    }


    protected void setHttpClient(IHttpClientStrategy httpClientStrategy) {
        this.httpClientStrategy = httpClientStrategy;
    }

    protected void setCharset(String charset)
    {
        this.charset = charset;
    }

    public String getCharset()
    {
        return this.charset;
    }

    protected void setJsonStrategy(IJsonStrategy jsonStrategy) {
        this.jsonStrategy = jsonStrategy;
    }

    protected void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    private void checkParms() {
        if (this.httpClientStrategy == null || this.jsonStrategy == null) {

            throw new IllegalArgumentException("this.httpClientStrategy is not null or this.jsonStrategy is not null");
        }
    }

    public <T,Invoker> T createService(Class<T> t,Invoker cls) {
        checkParms();
        return ServiceFactory.createService(t, bridge,cls);
    }


    public static class Builder {

        private IHttpClientStrategy httpClientStrategy = null;
        private IJsonStrategy jsonStrategy = null;
        private String baseUrl = null;
        private String charset = "";

        public Builder setHttpClient(IHttpClientStrategy httpClientStrategy) {
            this.httpClientStrategy = httpClientStrategy;
            if (this.jsonStrategy != null)
                this.httpClientStrategy.setJsonStrategy(jsonStrategy);
            return this;
        }

        public Builder setJsonStrategy(IJsonStrategy jsonStrategy) {
            this.jsonStrategy = jsonStrategy;
            if (this.httpClientStrategy != null)
                this.httpClientStrategy.setJsonStrategy(jsonStrategy);
            return this;
        }
        public Builder setCharset(String charset)
        {
            this.charset = charset;
            return this;
        }

        public Builder setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Bridge build() {
            Bridge.getInstance().setHttpClient(this.httpClientStrategy);
            Bridge.getInstance().setJsonStrategy(this.jsonStrategy);
            Bridge.getInstance().setBaseUrl(this.baseUrl);
            if(this.charset!=null&&this.charset.length()>0)
            {
                Bridge.getInstance().setCharset(this.charset);
            }

            return Bridge.getInstance();
        }


    }


}
