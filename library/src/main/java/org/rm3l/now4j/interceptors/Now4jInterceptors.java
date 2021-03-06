/*
 * Copyright (c) 2017 Armel Soro
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.rm3l.now4j.interceptors;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rm3l.now4j.NowClient;

import java.io.IOException;

public final class Now4jInterceptors {

    public static final HeadersInterceptor HEADERS_INTERCEPTOR = new HeadersInterceptor();

    private Now4jInterceptors() {
        throw new UnsupportedOperationException("Not instantiable");
    }

    public static final class HeadersInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            final Request requestWithNewHeaders = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-Requested-By", "now4j")
                    .build();
            return chain.proceed(requestWithNewHeaders);
        }
    }

    public static class AuthenticationInterceptor implements Interceptor {

        @NotNull
        private final String token;

        public AuthenticationInterceptor(@NotNull String token) {
            this.token = token;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            final Request requestWithNewHeaders = chain.request().newBuilder()
                    .addHeader("Authorization",
                            String.format("Bearer %s", this.token))
                    .build();
            return chain.proceed(requestWithNewHeaders);
        }
    }

    public static class TeamInterceptor implements Interceptor {

        public static final String NO_TEAM = "";

        @Nullable
        private final String team;

        public TeamInterceptor(@Nullable final String team) {
            this.team = team;
        }

        @Override
        public Response intercept(final Chain chain) throws IOException {
            final Request newRequest;
            if (this.team == null || NO_TEAM.equals(this.team.trim())) {
                newRequest = chain.request();
            } else {
                final Request request = chain.request();
                final HttpUrl newUrl = request.url().newBuilder()
                        .addQueryParameter("team", team)
                        .build();
                newRequest = request.newBuilder().url(newUrl).build();
            }
            return chain.proceed(newRequest);
        }
    }

}
