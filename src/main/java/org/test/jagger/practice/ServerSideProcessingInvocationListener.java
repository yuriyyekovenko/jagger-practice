package org.test.jagger.practice;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.AvgMetricAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.MetricDescription;
import com.griddynamics.jagger.engine.e1.collector.SumMetricAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationInfo;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationListener;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;

/**
 * Calculate total time spent by server to process test invocations
 * */
public class ServerSideProcessingInvocationListener extends ServicesAware implements Provider<InvocationListener> {

    private final String metricName = "server-side-processing-time";

    @Override
    protected void init() {
        getMetricService().createMetric(new MetricDescription(metricName)
                .displayName("Server side processing time, sec")
                .showSummary(true)
                .addAggregator(new SumMetricAggregatorProvider())
        );
    }

    @Override
    public InvocationListener provide() {
        return new InvocationListener() {

            @Override
            public void onStart(InvocationInfo invocationInfo) {
            }

            @Override
            public void onSuccess(InvocationInfo invocationInfo) {
                JHttpResponse response = (JHttpResponse) invocationInfo.getResult();
                Double serverTime = Double.valueOf(response.getHeaders().get("x-processed-time").get(0));
                getMetricService().saveValue(metricName, serverTime);
            }

            @Override
            public void onFail(InvocationInfo invocationInfo, InvocationException e) {
            }

            @Override
            public void onError(InvocationInfo invocationInfo, Throwable error) {
            }
        };
    }
}
