package nl.breun.repro;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SampleBeanTest {

    private static final Logger log = LoggerFactory.getLogger(SampleBeanTest.class);

    @Autowired
    private SampleBean sampleBean;

    @Test
    public void whenSendingLotsOfMetrics_anOverflowExceptionIsLoggedAndSendingMetricsStops() {
        for (int i = 0; i < 1_000_000; i++) {
            sampleBean.inc();
        }
        log.info("Test finished");
    }
}