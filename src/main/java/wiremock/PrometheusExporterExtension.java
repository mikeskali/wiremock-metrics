package wiremock;

import com.github.tomakehurst.wiremock.admin.Router;
import com.github.tomakehurst.wiremock.extension.AdminApiExtension;
import com.github.tomakehurst.wiremock.http.RequestMethod;

public class PrometheusExporterExtension implements AdminApiExtension {

    @Override
    public void contributeAdminApiRoutes(Router router) {
        router.add(RequestMethod.GET, "/metrics", new PrometheusAdminTask());
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }
}
