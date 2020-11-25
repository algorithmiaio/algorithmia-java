package com.algorithmia.client;

import com.algorithmia.algo.AlgorithmiaConf;
import com.algorithmia.algo.APIException;

import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.client.methods.AsyncByteConsumer;
import org.apache.http.nio.client.methods.ZeroCopyConsumer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.nio.protocol.BasicAsyncResponseConsumer;
import org.apache.http.nio.protocol.BasicAsyncRequestProducer;
import org.apache.http.nio.IOControl;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.InterruptedException;
import java.nio.ByteBuffer;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.stream;

public class HttpClient {

    final private Auth auth;
    final private String apiAddress;

    private static String userAgent = "algorithmia-java/" + "1.0.16";

    private static List<CloseableHttpAsyncClient> clients = new LinkedList<CloseableHttpAsyncClient>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                synchronized (clients) {
                    for (CloseableHttpAsyncClient client : clients) {
                        try {
                            client.close();
                        } catch (IOException e) {
                        }
                    }
                }
            }
        });
    }

    private final CloseableHttpAsyncClient client;

    public HttpClient(Auth auth, String apiAddress, int maxConnections,String pemPath) {
        this.auth = auth;
        if (apiAddress != null) {
            this.apiAddress = apiAddress;
        } else {
            this.apiAddress = AlgorithmiaConf.apiAddress();
        }
        HttpAsyncClientBuilder clientBuilder = HttpAsyncClientBuilder.create();
        if (pemPath != null) {
            try {
                SSLContext context = getSSLContext(pemPath);
                clientBuilder.setSSLContext(context);
            } catch (Exception e) {

            }
        }

        client = clientBuilder
                .setMaxConnTotal(maxConnections)
                .setMaxConnPerRoute(maxConnections)
                .useSystemProperties()
                .build();

        synchronized (clients) {
            clients.add(client);
        }
        client.start();
    }


    /**
     * Builds SLLContext from supplied .PEM
     */
    private SSLContext getSSLContext(String pemPath) throws Exception {
        SSLContext context = SSLContext.getInstance("TLS");


        byte[] certBytes = Files.readAllBytes(new File(pemPath).toPath());
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate) factory.generateCertificates(new ByteArrayInputStream(certBytes));

        KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(null);
        keystore.setCertificateEntry("cert-alias", cert);

        //Gets Default Trust Store
        TrustManagerFactory tmx = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        tmx.init((KeyStore) null);
        //Trust store built from our supplied pem
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(keystore);


        TrustManager[] tm = tmf.getTrustManagers();
        TrustManager[] tx = tmx.getTrustManagers();
        Stream stream1 = stream(tmf.getTrustManagers()).filter(x -> x instanceof TrustManager);
        Stream stream2 = stream(tmx.getTrustManagers()).filter(x -> x instanceof TrustManager);

        TrustManager[] t = (TrustManager[]) Stream.concat(stream1,stream2).collect(Collectors.toList());

        context.init(null, t, null);

        return context;
    }

    /**
     * Constructs the url for a given path
     */
    private String getUrl(String path) {
        return apiAddress + path;
    }

    /**
     * Modifies request in place to add on any query parameters
     */
    private void addQueryParameters(HttpRequestBase request, Map<String, String> params) {
        if (params != null) {
            URIBuilder builder = new URIBuilder(request.getURI());
            for (Map.Entry<String, String> param : params.entrySet()) {
                builder.addParameter(param.getKey(), param.getValue());
            }
            try {
                request.setURI(builder.build());
            } catch (URISyntaxException e) {
                throw new RuntimeException("Unable to construct API URI", e);
            }
        }
    }

    /**
     * GET requests
     */
    public HttpResponse get(String path) throws APIException {
        final HttpGet request = new HttpGet(getUrl(path));
        return this.execute(request);
    }

    public <T> T get(String path, TypeToken<T> typeToken, Map<String, String> params) throws APIException {
        final HttpGet request = new HttpGet(getUrl(path));
        addQueryParameters(request, params);
        return this.execute(request, new HttpClientHelpers.JsonDeserializeResponseHandler<T>(typeToken));
    }

    public <T> Future<T> get(String path, HttpAsyncResponseConsumer<T> consumer) {
        final HttpGet request = new HttpGet(getUrl(path));
        return this.executeAsync(request, consumer);
    }

    public void getFile(String path, File destination) throws APIException {
        final HttpGet request = new HttpGet(getUrl(path));
        this.executeGetFile(request, destination);
    }

    /**
     * POST requests
     */
    public HttpResponse post(String path) throws APIException {
        final HttpPost request = new HttpPost(getUrl(path));
        return this.execute(request);
    }

    public HttpResponse post(String path, HttpEntity data) throws APIException {
        final HttpPost request = new HttpPost(getUrl(path));
        request.setEntity(data);
        return this.execute(request);
    }

    public <T> Future<T> post(String path, HttpEntity data, HttpAsyncResponseConsumer<T> consumer) {
        return post(getUrl(path), data, consumer, null);
    }

    public <T> Future<T> post(String path, HttpEntity data, HttpAsyncResponseConsumer<T> consumer, Map<String, String> parameters) {
        final HttpPost request = new HttpPost(getUrl(path));
        request.setEntity(data);
        addQueryParameters(request, parameters);
        return this.executeAsync(request, consumer);
    }

    /**
     * PUT requests
     */
    public HttpResponse put(String path) throws APIException {
        final HttpPut request = new HttpPut(getUrl(path));
        return this.execute(request);
    }

    public HttpResponse put(String path, HttpEntity data) throws APIException {
        final HttpPut request = new HttpPut(getUrl(path));
        request.setEntity(data);
        return this.execute(request);
    }

    public <T> Future<T> put(String path, HttpEntity data, HttpAsyncResponseConsumer<T> consumer) {
        final HttpPut request = new HttpPut(getUrl(path));
        request.setEntity(data);
        return this.executeAsync(request, consumer);
    }

    /**
     * DELETE requests
     */
    public HttpResponse delete(String path) throws APIException {
        final HttpDelete request = new HttpDelete(getUrl(path));
        return execute(request);
    }

    public <T> Future<T> delete(String path, HttpAsyncResponseConsumer<T> consumer) {
        final HttpDelete request = new HttpDelete(getUrl(path));
        return executeAsync(request, consumer);
    }

    /**
     * HEAD requests
     */
    public HttpResponse head(String path) throws APIException {
        final HttpHead request = new HttpHead(getUrl(path));
        return executeHead(request);
    }

    public <T> Future<T> head(String path, HttpAsyncResponseConsumer<T> consumer) {
        final HttpHead request = new HttpHead(getUrl(path));
        return executeAsync(request, consumer);
    }

    /**
     * PATCH requests
     */
    public HttpResponse patch(String path, StringEntity entity) throws APIException {
        final HttpPatch request = new HttpPatch(getUrl(path));
        request.setEntity(entity);
        return this.execute(request);
    }

    /**
     * execute methods to execute a request
     */
    private HttpResponse execute(HttpUriRequest request) throws APIException {
        return execute(request, new BasicAsyncResponseConsumer());
    }

    private HttpResponse executeHead(HttpUriRequest request) throws APIException {
        // We don't use the BasicAsyncResponseConsumer because it barfs when the
        // content length is too long.
        return execute(request, new HttpResponseConsumer());
    }

    private void executeGetFile(HttpUriRequest request, File destination) throws APIException {
        // We don't use the BasicAsyncResponseConsumer because it barfs when the
        // content length is too long.
        try {
            ZeroCopyConsumer<File> consumer = new ZeroCopyConsumer<File>(destination) {
                @Override
                protected File process(final HttpResponse response, final File file, final ContentType contentType) throws Exception {
                    if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                        throw new APIException("Download failed: " + response.getStatusLine());
                    }
                    return file;
                }
            };

            execute(request, consumer);
        } catch (FileNotFoundException e) {
            throw new APIException("Could not find destination file: " + destination.getAbsolutePath());
        }
    }

    private <T> T execute(HttpUriRequest request, HttpAsyncResponseConsumer<T> consumer) throws APIException {
        try {
            return executeAsync(request, consumer).get();
        } catch (ExecutionException e) {
            throw new APIException(e.getCause().getMessage());
        } catch (CancellationException e) {
            throw new APIException("API connection cancelled: " + request.getURI().toString() + " (" + e.getMessage() + ")", e);
        } catch (InterruptedException e) {
            throw new APIException("API connection interrupted: " + request.getURI().toString() + " (" + e.getMessage() + ")", e);
        }
    }

    private <T> Future<T> executeAsync(HttpUriRequest request, HttpAsyncResponseConsumer<T> consumer) {
        if (this.auth != null) {
            this.auth.authenticateRequest(request);
        }
        request.addHeader("User-Agent", HttpClient.userAgent);
        final URI uri = request.getURI();
        final HttpHost target = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
        return client.execute(new BasicAsyncRequestProducer(target, request), consumer, null);
    }

    /**
     * A consumer that drops the body of a response. It's useful when you just want the HTTP headers.
     */
    static class HttpResponseConsumer extends AsyncByteConsumer<HttpResponse> {
        private HttpResponse response;

        @Override
        protected void onResponseReceived(final HttpResponse response) {
            this.response = response;
        }

        @Override
        protected HttpResponse buildResult(final HttpContext context) throws Exception {
            return this.response;
        }

        @Override
        protected void onByteReceived(final ByteBuffer buf, final IOControl ioctrl) {
        }
    }

    @Override
    protected void finalize() throws IOException {
        synchronized (clients) {
            client.close();
            clients.remove(client);
        }
    }

    public void close() throws IOException {
        this.finalize();
    }
}
