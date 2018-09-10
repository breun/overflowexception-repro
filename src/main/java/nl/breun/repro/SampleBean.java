package nl.breun.repro;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class SampleBean {

    private final Counter counter;

    public SampleBean(final MeterRegistry registry) {
        this.counter = registry.counter("foo");
    }

    public void inc() {
        counter.increment();
    }
}