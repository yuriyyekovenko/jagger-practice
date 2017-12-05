package org.test.jagger.practice;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.collector.ResponseValidator;
import com.griddynamics.jagger.engine.e1.collector.ResponseValidatorProvider;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import org.springframework.http.HttpHeaders;

/**
 * The validator checks if the response is in JSON format
 * */
public class ResponseJsonFormatValidator implements ResponseValidatorProvider {

    @Override
    public ResponseValidator<JHttpQuery, JHttpEndpoint, JHttpResponse> provide(String taskId,
                                                                               String sessionId,
                                                                               NodeContext kernelContext) {

        return new ResponseValidator<JHttpQuery, JHttpEndpoint, JHttpResponse>(taskId, sessionId, kernelContext) {
            @Override
            public String getName() {
                return "Response in Json format Validator";
            }

            @Override
            public boolean validate(JHttpQuery query, JHttpEndpoint endpoint, JHttpResponse result, long duration) {
                HttpHeaders responseHeaders = result.getHeaders();
                return responseHeaders.get("content-type").contains("application/json");
            }
        };
    }
}
