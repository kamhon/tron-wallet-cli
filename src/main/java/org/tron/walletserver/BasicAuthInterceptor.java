package org.tron.walletserver;

import io.grpc.*;

import java.util.Base64;

public class BasicAuthInterceptor implements ClientInterceptor {
    private final String username;
    private final String password;

    public BasicAuthInterceptor(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
                next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                String auth = username + ":" + password;
                String basicAuth = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
                headers.put(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER), basicAuth);
                super.start(responseListener, headers);
            }
        };
    }
}
