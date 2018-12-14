/* package org.apache.beam.examples;

import com.spotify.scio.transforms.AsyncLookupDoFn;

import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.apache.flink.api.java.tuple.Tuple3;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.Dsl;

import com.google.common.util.concurrent.*;

import com.google.common.util.concurrent.ListenableFuture;

class StringTuple3TypeDescriptor extends TypeDescriptor<Tuple3<String, String, String>> {
    private static final long serialVersionUID = 1L;
};

public class MyAsyncLookupDoFn
        extends AsyncLookupDoFn<KV<String, String>, Tuple3<String, String, String>, AsyncHttpClient> {
    private static final long serialVersionUID = 1L;

    @Override
    public ListenableFuture<Tuple3<String, String, String>> asyncLookup(AsyncHttpClient client,
            KV<String, String> input) {
        BoundRequestBuilder getRequest = client.prepareGet(
                "http://www.sethcardoza.com/api/rest/tools/random_password_generator?q=" + input.getValue());
        SettableFuture<Tuple3<String, String, String>> future = SettableFuture.create();
        getRequest.execute().toCompletableFuture().thenApply(r -> {
            future.set(new Tuple3<String, String, String>(input.getKey(), input.getValue(), r.getResponseBody()));
            return future;
        });
        return future;
    }

    @Override
    protected AsyncHttpClient newClient() {
        return Dsl.asyncHttpClient();
    }
};
 */