package wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import static com.github.tomakehurst.wiremock.client.WireMock.*;


public class TestMetrics {
    // Just a local test..
    public static void main(String[] args){
        WireMockConfiguration conf = new WireMockConfiguration();

        WireMockServer server = new WireMockServer(conf.port(8092).extensions(new PrometheusExporterExtension(), new MetricsExtension()));

        server.start();
        configureFor("localhost", 8092);
		stubFor(get("/kukuriku").willReturn(aResponse()
			.withStatus(200)
			.withHeader("Content-Type", "text/xml")
			.withBody("<response>Some content</response>")));

		stubFor(get(urlMatching("/tralala"))
			.willReturn(aResponse().proxiedFrom("http://www.ynet.co.il")));

		stubFor(get(urlMatching("/trilili"))
			.willReturn(aResponse().proxiedFrom("http://msklyar-non-existing-domain.com/byebye")));

		stubFor(get(urlMatching("/trululu"))
			.willReturn(aResponse().proxiedFrom("http://www.google.com")));
    }
}
