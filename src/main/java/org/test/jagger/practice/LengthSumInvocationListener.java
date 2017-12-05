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
 * Calculate total size and average size of the responses received during the test
 * */
public class LengthSumInvocationListener extends ServicesAware implements Provider<InvocationListener> {

    private final String metricName = "received-content-size";

    @Override
    protected void init() {
        getMetricService().createMetric(new MetricDescription(metricName)
                .displayName("Size of received content")
                .showSummary(true)
                .addAggregator(new SumMetricAggregatorProvider())
                .addAggregator(new AvgMetricAggregatorProvider())
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
                getMetricService().saveValue(metricName, response.getHeaders().getContentLength());
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
