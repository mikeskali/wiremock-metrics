package wiremock;

import com.github.tomakehurst.wiremock.core.Admin;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.PostServeAction;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;

public class MetricsExtension extends PostServeAction {
	@Override
	public void doAction(ServeEvent serveEvent, Admin admin, Parameters parameters) {
		super.doAction(serveEvent, admin, parameters);
	}

	@Override
	public void doGlobalAction(ServeEvent serveEvent, Admin admin) {
		super.doGlobalAction(serveEvent, admin);

		try {
			String path = "";

			StubMapping stabMapping = serveEvent.getStubMapping();
			if (stabMapping != null) {
				if (stabMapping.getRequest() != null) {
					path = stabMapping.getRequest().getUrlPattern();
				}
			}

			if (path.length() == 0) {
				path = serveEvent.getRequest().getUrl().split("\\?")[0];
			}

			MetricsRegistry.getInstance().recordTime(
				path,
				serveEvent.getRequest().getMethod().getName(),
				serveEvent.getResponse().getStatus(),
				serveEvent.getTiming().getTotalTime());
		} catch (Exception e){
			MetricsRegistry.getInstance().increaseError(e.getClass().getName());
		}
	}

	@Override
	public String getName() {
		return this.getClass().getName();
	}
}
