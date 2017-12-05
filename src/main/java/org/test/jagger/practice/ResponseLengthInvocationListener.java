package org.test.jagger.practice;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.MetricDescription;
import com.griddynamics.jagger.engine.e1.collector.PercentileAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationInfo;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationListener;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;

/**
 * The listener gathers content length of each response.
 * Test-data file, providing different queries, contains 4 cases, all of them with different length.
 * The added aggregator (percentile-25) will allow to check if the query of the smallest length
 * is used in 25% of invocations (not very useful for real perf-testing, just jagger-related exercise).
 * */
public class ResponseLengthInvocationListener extends ServicesAware implements Provider<InvocationListener> {

    private final String metricName = "response-length";

    @Override
    protected void init() {
        getMetricService().createMetric(new MetricDescription(metricName)
                .displayName("Length of response content")
                .showSummary(true)
                .plotData(true)
                .addAggregator(new PercentileAggregatorProvider(25D))
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