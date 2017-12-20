package org.test.jagger.practice;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.collector.ResponseValidator;
import com.griddynamics.jagger.engine.e1.collector.ResponseValidatorProvider;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.json.simple.JSONObject;

import java.util.Map;

/**
 * The validator checks if the response headers contain the parameters of request query
 * */
public class QueryParamsInHeadersValidator implements ResponseValidatorProvider {

    private static final Logger log = LoggerFactory.getLogger(QueryParamsInHeadersValidator.class);

    @Override
    public ResponseValidator<JHttpQuery, JHttpEndpoint, JHttpResponse> provide(String taskId,
                                                                               String sessionId,
                                                                               NodeContext kernelContext) {

        return new ResponseValidator<JHttpQuery, JHttpEndpoint, JHttpResponse>(taskId, sessionId, kernelContext) {
            @Override
            public String getName() {
                return "Response Headers Validator";
            }

            @Override
            public boolean validate(JHttpQuery query, JHttpEndpoint endpoint, JHttpResponse result, long duration) {
                Map<String, String> requestParams = query.getQueryParams();
//                HttpHeaders responseHeaders = result.getHeaders();

                JSONObject body = (JSONObject)result.getBody();
                log.warn("!!!!!!!");
//                log.warn(jo.keySet().toString());
                log.warn(body.toJSONString());
                log.warn("!!!!!!!");

                for (String paramName : requestParams.keySet()) {
                    log.warn(body.get(paramName).toString());
                    if (!body.containsKey(paramName)
                            || !body.get(paramName).equals("foo")) {
//                            || !body.get(paramName).equals(requestParams.get(paramName))) {
                        return false;
                    }
                }
                return true;

//                for (String paramName : requestParams.keySet()) {
//                    if (!responseHeaders.containsKey(paramName)
//                            || !responseHeaders.get(paramName).contains(requestParams.get(paramName))) {
//                        return false;
//                    }
//                }
//                return true;
            }
        };
    }
}
