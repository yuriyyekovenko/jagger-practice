package org.test.jagger.practice;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.collector.ResponseValidator;
import com.griddynamics.jagger.engine.e1.collector.ResponseValidatorProvider;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import org.json.simple.JSONObject;

import java.util.Map;

/**
 * The validator checks if the response body contains the parameters of request query
 * */
public class QueryParamsInBodyValidator implements ResponseValidatorProvider {

    @Override
    public ResponseValidator<JHttpQuery, JHttpEndpoint, JHttpResponse> provide(String taskId,
                                                                               String sessionId,
                                                                               NodeContext kernelContext) {

        return new ResponseValidator<JHttpQuery, JHttpEndpoint, JHttpResponse>(taskId, sessionId, kernelContext) {
            @Override
            public String getName() {
                return "Response Body Validator";
            }

            @Override
            public boolean validate(JHttpQuery query, JHttpEndpoint endpoint, JHttpResponse result, long duration) {
                Map<String, String> requestParams = query.getQueryParams();
                JSONObject body = (JSONObject)result.getBody();

                for (String paramName : requestParams.keySet()) {
                    if (!body.containsKey(paramName)
                            || !body.get(paramName).equals(requestParams.get(paramName))) {
                        return false;
                    }
                }
                return true;
            }
        };
    }
}
