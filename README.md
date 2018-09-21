> **UPDATE:** The issue has been fixed.

# overflowexception-repro

This repository demonstrates the occurence of an `OverflowException` when trying to batch StatsD metrics using Micrometer.

Steps to reproduce:

Locally build and install a patched Micrometer StatsD registry that supports batching:

1. `git clone git@github.com:bolcom/micrometer.git` 
1. `cd micrometer`
1. `git checkout statsd-nagle-1.0.x`
1. `./gradlew -Prelease.version=1.0.6.1-BOL-SNAPSHOT clean publishToMavenLocal`

In this project's `pom.xml` set the version of the `micrometer-registry-statsd` dependency to the version you just built
and then run the test:

    ./mvnw test

In the logging output you'll find this error:

    2018-09-10 14:54:56.946 ERROR 53687 --- [      udp-nio-1] i.m.s.r.i.n.channel.ChannelOperations    : [Udp] Error processing connection. Requesting close the channel
    
    io.micrometer.shaded.reactor.core.Exceptions$OverflowException: Could not emit buffer due to lack of requests
    	at io.micrometer.shaded.reactor.core.Exceptions.failWithOverflow(Exceptions.java:215) ~[micrometer-registry-statsd-1.0.6.1-BOL.jar:1.0.6.1-BOL]
    	at io.micrometer.shaded.reactor.core.publisher.FluxBufferPredicate$BufferPredicateSubscriber.emit(FluxBufferPredicate.java:292) ~[micrometer-registry-statsd-1.0.6.1-BOL.jar:1.0.6.1-BOL]
    	at io.micrometer.shaded.reactor.core.publisher.FluxBufferPredicate$BufferPredicateSubscriber.onNextNewBuffer(FluxBufferPredicate.java:251) ~[micrometer-registry-statsd-1.0.6.1-BOL.jar:1.0.6.1-BOL]
    	at io.micrometer.shaded.reactor.core.publisher.FluxBufferPredicate$BufferPredicateSubscriber.tryOnNext(FluxBufferPredicate.java:208) ~[micrometer-registry-statsd-1.0.6.1-BOL.jar:1.0.6.1-BOL]
    	at io.micrometer.shaded.reactor.core.publisher.FluxBufferPredicate$BufferPredicateSubscriber.onNext(FluxBufferPredicate.java:180) ~[micrometer-registry-statsd-1.0.6.1-BOL.jar:1.0.6.1-BOL]
    	at io.micrometer.shaded.reactor.core.publisher.FluxPublishMulticast$CancelMulticaster.onNext(FluxPublishMulticast.java:731) ~[micrometer-registry-statsd-1.0.6.1-BOL.jar:1.0.6.1-BOL]
    	at io.micrometer.shaded.reactor.core.publisher.FluxFlatMap$FlatMapMain.tryEmit(FluxFlatMap.java:484) ~[micrometer-registry-statsd-1.0.6.1-BOL.jar:1.0.6.1-BOL]
    	at io.micrometer.shaded.reactor.core.publisher.FluxFlatMap$FlatMapInner.onNext(FluxFlatMap.java:916) ~[micrometer-registry-statsd-1.0.6.1-BOL.jar:1.0.6.1-BOL]
    	at io.micrometer.shaded.reactor.core.publisher.FluxPublishMulticast$FluxPublishMulticaster.drainAsync(FluxPublishMulticast.java:490) ~[micrometer-registry-statsd-1.0.6.1-BOL.jar:1.0.6.1-BOL]
    	at io.micrometer.shaded.reactor.core.publisher.FluxPublishMulticast$FluxPublishMulticaster.drain(FluxPublishMulticast.java:303) ~[micrometer-registry-statsd-1.0.6.1-BOL.jar:1.0.6.1-BOL]
    	at io.micrometer.shaded.reactor.core.publisher.FluxPublishMulticast$FluxPublishMulticaster.onNext(FluxPublishMulticast.java:274) ~[micrometer-registry-statsd-1.0.6.1-BOL.jar:1.0.6.1-BOL]
    	at io.micrometer.shaded.reactor.core.publisher.UnicastProcessor.drainFused(UnicastProcessor.java:234) ~[micrometer-registry-statsd-1.0.6.1-BOL.jar:1.0.6.1-BOL]
    	at io.micrometer.shaded.reactor.core.publisher.UnicastProcessor.drain(UnicastProcessor.java:267) ~[micrometer-registry-statsd-1.0.6.1-BOL.jar:1.0.6.1-BOL]
    	at io.micrometer.shaded.reactor.core.publisher.UnicastProcessor.onNext(UnicastProcessor.java:343) ~[micrometer-registry-statsd-1.0.6.1-BOL.jar:1.0.6.1-BOL]
    	at io.micrometer.statsd.internal.LogbackMetricsSuppressingUnicastProcessor.lambda$onNext$0(LogbackMetricsSuppressingUnicastProcessor.java:43) ~[micrometer-registry-statsd-1.0.6.1-BOL.jar:1.0.6.1-BOL]
    	at io.micrometer.core.instrument.binder.logging.LogbackMetrics.ignoreMetrics(LogbackMetrics.java:77) ~[micrometer-core-1.0.6.jar:1.0.6]
    	at io.micrometer.statsd.internal.LogbackMetricsSuppressingUnicastProcessor.onNext(LogbackMetricsSuppressingUnicastProcessor.java:43) ~[micrometer-registry-statsd-1.0.6.1-BOL.jar:1.0.6.1-BOL]
    	at io.micrometer.statsd.internal.LogbackMetricsSuppressingUnicastProcessor.onNext(LogbackMetricsSuppressingUnicastProcessor.java:24) ~[micrometer-registry-statsd-1.0.6.1-BOL.jar:1.0.6.1-BOL]
    	at io.micrometer.statsd.StatsdCounter.increment(StatsdCounter.java:43) ~[micrometer-registry-statsd-1.0.6.1-BOL.jar:1.0.6.1-BOL]
    	at io.micrometer.core.instrument.binder.jvm.JvmGcMetrics.lambda$bindTo$0(JvmGcMetrics.java:170) ~[micrometer-core-1.0.6.jar:1.0.6]
    	at sun.management.NotificationEmitterSupport.sendNotification(NotificationEmitterSupport.java:156) ~[na:1.8.0_181]
    	at sun.management.GarbageCollectorImpl.createGCNotification(GarbageCollectorImpl.java:143) ~[na:1.8.0_181]

Set a breakpoint at `FluxBufferPredicate` on line 198 to catch this when running the test in debug mode.
