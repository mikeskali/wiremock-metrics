package wiremock;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import java.util.HashMap;
import java.util.Map;


public class MetricsRegistry {
	PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
	Map<String, DistributionSummary> timersPerPath = new HashMap<>();
	Map<String, Counter> errors = new HashMap<>();

	private static MetricsRegistry metrics;

	public static MetricsRegistry getInstance(){
		if (metrics == null){
			synchronized(MetricsRegistry.class){
				if(metrics == null) {
					metrics = new MetricsRegistry();
				}
			}
		}
		return metrics;
	}

	private MetricsRegistry(){
		registry.config().commonTags("application", "MockServer");
		new JvmMemoryMetrics().bindTo(registry);
		new JvmGcMetrics().bindTo(registry);
		new ProcessorMetrics().bindTo(registry);
		new JvmThreadMetrics().bindTo(registry);
	}

	public Counter getErrorCounter(String err){
		if(!errors.containsKey(err)){
			Counter counter = registry.counter("wiremock-metrics-errors", "errType", err);
			errors.put(err, counter);
		}
		return errors.get(err);
	}

	public DistributionSummary getDistributionSummary(String path, String method, int status){
		String key = path + ":" + method + ":" + status;
		DistributionSummary dist = timersPerPath.get(key);

		if(dist == null){
			synchronized (MetricsRegistry.class){
				dist = timersPerPath.get(key);
				if(dist == null){
					dist = DistributionSummary
						.builder("requestTimer")
						.description("Measuring requests latencies in ms")
						.baseUnit("ms")
						.tags("path", path, "method", method, "status", String.valueOf(status))
						.sla(3,15,50,200,1000,3000, 10000, 20000, 50000)
						.register(registry);
				}
			}
		}
		return dist;
	}

	public void recordTime(String path, String method, int statusCode, int timeMs){
		if (path == null){
			path = "ERROR_NO_PATH_PROVIDED";
		}
		if (method == null){
			method = "ERROR_NO_METHOD_PROVIDED";
		}
		DistributionSummary dist = getDistributionSummary(path, method, statusCode);
		dist.record(timeMs);
	}

	public void increaseError(String err){
		getErrorCounter(err).increment();
	}

	public String  getPrometheusBody(){
		return registry.scrape();
	}
}
