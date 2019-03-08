package ticktrack.frontend.util;

import okhttp3.MediaType;
import okhttp3.Request;

public final class OkHttpRequestHandler {
    public static Request buildRequestWithoutBody(String url) {
        return new Request.Builder()
                .url(url)
                .build();
    }

    public static Request buildRequestWithBody(String url, String body) {
        return new Request.Builder()
                .url(url)
                .post(
                        okhttp3.RequestBody.create(
                                MediaType.parse("application/json; charset=utf-8"),
                                body)
                ).build();
    }

}
