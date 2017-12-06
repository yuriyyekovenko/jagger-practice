package org.test.jagger.practice;

import com.griddynamics.jagger.engine.e1.collector.JHttpResponseStatusValidatorProvider;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.user.test.configurations.JLoadScenario;
import com.griddynamics.jagger.user.test.configurations.JLoadTest;
import com.griddynamics.jagger.user.test.configurations.JParallelTestsGroup;
import com.griddynamics.jagger.user.test.configurations.JTestDefinition;
import com.griddynamics.jagger.user.test.configurations.auxiliary.Id;
import com.griddynamics.jagger.user.test.configurations.limits.JLimit;
import com.griddynamics.jagger.user.test.configurations.limits.JLimitVsRefValue;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.*;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfile;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileInvocation;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileUserGroups;
import com.griddynamics.jagger.user.test.configurations.load.JLoadProfileUsers;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.InvocationCount;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.NumberOfUsers;
import com.griddynamics.jagger.user.test.configurations.load.auxiliary.ThreadCount;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteria;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaBackground;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaDuration;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationCriteriaIterations;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.DurationInSeconds;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.IterationsNumber;
import com.griddynamics.jagger.user.test.configurations.termination.auxiliary.MaxDurationInSeconds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Configuration
public class PracticeJLoadScenarioProvider {

    private static final Logger log = LoggerFactory.getLogger(PracticeJLoadScenarioProvider.class);


    private static final List<JHttpEndpoint> endpoint =
            Arrays.asList(new JHttpEndpoint(URI.create("http://httpbin.org")));

    @Bean
    public JLoadScenario practiceScenario() {
        return JLoadScenario.builder(
                Id.of("practice_scenario"),
//                buildFirstTestGroup(),
//                buildSecondTestGroup(),
                buildThirdTestGroup()
        ).build();
    }

    private JParallelTestsGroup buildFirstTestGroup() {

        JTestDefinition definition = JTestDefinition
                .builder(Id.of("test1 definition"), endpoint)
                .withQueryProvider(Arrays.asList(new JHttpQuery().get().path("/get")))
                .addValidator(JHttpResponseStatusValidatorProvider.of(200))
                .addValidator(new ResponseJsonFormatValidator())
                .addListener(new ServerSideProcessingInvocationListener())
                .build();

        JLoadProfile profile = JLoadProfileInvocation
                .builder(InvocationCount.of(5), ThreadCount.of(2)).build();

        JTerminationCriteria termination = JTerminationCriteriaIterations.of(
                IterationsNumber.of(5), MaxDurationInSeconds.of(30));

        JLimit errorsLimit = JLimitVsRefValue
                .builder(JMetricName.PERF_SUCCESS_RATE_FAILS, RefValue.of(0.0))
                .withOnlyErrors(LowErrThresh.of(0.99), UpErrThresh.of(1.01))
                .build();

        // indicate if latency percentile 95 is too high
        JLimit latencyPercentileLimit = JLimitVsRefValue
                .builder(JMetricName.PERF_LATENCY_PERCENTILE(95D), RefValue.of(0.4D))
                .withOnlyUpperThresholds(UpWarnThresh.of(1.2), UpErrThresh.of(1.5))
                .build();

        JLoadTest test = JLoadTest
                .builder(Id.of("test1"), definition, profile, termination)
                .withLimits(errorsLimit, latencyPercentileLimit)
                .build();

        return JParallelTestsGroup.builder(Id.of("group1"), test).build();
    }

    private JParallelTestsGroup buildSecondTestGroup() {

        JTestDefinition definition = JTestDefinition
                .builder(Id.of("test2 definition"), endpoint)
                .withQueryProvider(Arrays.asList(new JHttpQuery().get().path("/xml")))
                .addValidator(JHttpResponseStatusValidatorProvider.of(200))
                .addValidator(new ResponseXmlFormatValidator())
                .addListener(new LengthSumInvocationListener())
                .build();

        JLoadProfileUsers u1 = JLoadProfileUsers.builder(NumberOfUsers.of(1))
                .withStartDelayInSeconds(0)
                .build();

        JLoadProfileUsers u2 = JLoadProfileUsers.builder(NumberOfUsers.of(1))
                .withStartDelayInSeconds(20)
                .build();

        JLoadProfileUsers u3 = JLoadProfileUsers.builder(NumberOfUsers.of(1))
                .withStartDelayInSeconds(40)
                .build();

        JLoadProfile groupProfile = JLoadProfileUserGroups.builder(u1, u2, u3)
                .withDelayBetweenInvocationsInMilliseconds(15000)
                .build();

        JTerminationCriteria termination = JTerminationCriteriaDuration.of(
                DurationInSeconds.of(120 + 20 + 20));

        JLoadTest test = JLoadTest.builder(Id.of("test2"), definition, groupProfile, termination)
                .build();

        return JParallelTestsGroup.builder(Id.of("group2"), test).build();
    }

    private JParallelTestsGroup buildThirdTestGroup() {

        JTestDefinition definition1 = JTestDefinition
                .builder(Id.of("test3_1 definition"), endpoint)
                .withQueryProvider(new ResponseHeadersQueriesProvider())
                .addValidator(JHttpResponseStatusValidatorProvider.of(200))
                .addValidator(new QueryParamsInHeadersValidator())
                .addListener(new ResponseLengthInvocationListener())
                .build();

        JLoadProfile foregroundUserGroupProfile = JLoadProfileUserGroups
                .builder(JLoadProfileUsers.builder(NumberOfUsers.of(1)).build())
                .withDelayBetweenInvocationsInMilliseconds(20000)
                .build();

        JTerminationCriteria foregroundUserTermination =
                JTerminationCriteriaDuration.of(DurationInSeconds.of(180));

        JLimit shortResponsesWarningLimit = JLimitVsRefValue
                .builder("responselength-25", RefValue.of(54.0))
                .withOnlyWarnings(LowWarnThresh.of(0.98), UpWarnThresh.of(1.01))
                .build();

        JLimit nextResponsesWarningLimit = JLimitVsRefValue
                .builder("responselength-50", RefValue.of(60.0))
                .withOnlyWarnings(LowWarnThresh.of(0.98), UpWarnThresh.of(1.01))
                .build();

        JLoadTest foregroundUser = JLoadTest.builder(Id.of("foreground user"),
                definition1, foregroundUserGroupProfile, foregroundUserTermination)
                .withLimits(shortResponsesWarningLimit, nextResponsesWarningLimit)
                .build();

        //-- background user ----------------------------------------------
        JTestDefinition definition2 = JTestDefinition
                .builder(Id.of("test3_2 definition"), endpoint)
                .withQueryProvider(new ResponseHeadersQueriesProvider())
                .addValidator(new QueryParamsInHeadersValidator())
                .addListener(new ResponseLengthInvocationListener())
                .build();

        JLoadProfile backgroundUserGroupProfile = JLoadProfileUserGroups
                .builder(JLoadProfileUsers.builder(NumberOfUsers.of(1)).build())
                .withDelayBetweenInvocationsInMilliseconds(15000)
                .build();

        JTerminationCriteria backgroundUserTermination =
                JTerminationCriteriaBackground.getInstance();

        JLoadTest backgroundUser = JLoadTest.builder(Id.of("background user"),
                definition2, backgroundUserGroupProfile, backgroundUserTermination)
                .build();

        return JParallelTestsGroup.builder(Id.of("group3"), foregroundUser, backgroundUser).build();
    }
}

