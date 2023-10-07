package guru.qa.niffler.api.interceptor;

import guru.qa.niffler.api.context.CookieContext;
import okhttp3.Interceptor;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

public class RecievedCodeInterceptor implements Interceptor {
    private static final String JSESSIONID = "JSESSIONID";
    private static final String XSRF_TOKEN = "XSRF-TOKEN";
    @Override
    public Response intercept(Chain chain) throws IOException {
        CookieContext cookieContext = CookieContext.getInstance();
        Response response = chain.proceed(chain.request());
        List<String> setCookieHeader = response.headers("Set-cookie");
        if (!setCookieHeader.isEmpty()) {
            for (String header : setCookieHeader) {
                for (String cookie : header.split(";")) {
                    String[] rawCookie = cookie.split("=");
                    if (JSESSIONID.equals(rawCookie[0])) {
                        cookieContext.setJsessionid(rawCookie.length == 2 ? rawCookie[1] : null);
                    } else if (XSRF_TOKEN.equals(rawCookie[0])) {
                        cookieContext.setXsrf(rawCookie.length == 2 ? rawCookie[1] : null);
                    }
                }
            }
        }
        return response;
    }
}