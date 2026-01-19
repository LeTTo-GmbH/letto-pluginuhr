package at.letto.tools;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Hilfsklasse um unsichere HTTP-Requests (ohne Zertifikatsprüfung) durchzuführen
 */
public class HttpFetch {

    /** Erstellt einen HttpClient, der SSL-Zertifikate nicht überprüft */
    private static HttpClient insecureHttpClient()
            throws NoSuchAlgorithmException, KeyManagementException {

        TrustManager[] trustAll = new TrustManager[] {
                new X509TrustManager() {
                    @Override public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                    @Override public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                    @Override public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAll, new SecureRandom());

        // Hostname-Verification deaktivieren:
        // (in Java HttpClient über SSLParameters mit EndpointIdentificationAlgorithm=null)
        SSLParameters sslParameters = new SSLParameters();
        sslParameters.setEndpointIdentificationAlgorithm(null);

        return HttpClient.newBuilder()
                .sslContext(sslContext)
                .sslParameters(sslParameters)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    /**
     * Öffnet einen InputStream zu der angegebenen URL, ohne SSL-Zertifikate zu überprüfen.
     *
     * @param url Die URL, von der der Stream geöffnet werden soll.
     * @return Ein BufferedInputStream zum Lesen der Daten von der URL.
     * @throws IOException Wenn ein I/O-Fehler auftritt oder der HTTP-Statuscode nicht 2xx ist.
     * @throws InterruptedException Wenn die Anfrage unterbrochen wird.
     * @throws NoSuchAlgorithmException Wenn der SSL-Algorithmus nicht gefunden wird.
     * @throws KeyManagementException Wenn ein Fehler bei der Schlüsselverwaltung auftritt.
     */
    public static BufferedInputStream openStreamInsecure(String url)
            throws IOException, InterruptedException, NoSuchAlgorithmException, KeyManagementException {

        HttpClient client = insecureHttpClient();

        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<InputStream> resp = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        int code = resp.statusCode();
        if (code < 200 || code >= 300) {
            // Body ggf. lesen/loggen bevor du wirfst
            throw new IOException("HTTP " + code + " for " + url);
        }

        return new BufferedInputStream(resp.body());
    }
}

