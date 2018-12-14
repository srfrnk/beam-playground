/* package org.apache.beam.examples;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.beam.sdk.io.range.ByteKey;
import org.apache.beam.sdk.io.range.ByteKeyRange;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.splittabledofn.ByteKeyRangeTracker;
import org.apache.beam.sdk.values.KV;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

@DoFn.UnboundedPerElement
public class SplitDoFn extends DoFn<String, KV<String, String>> {
    private static final long serialVersionUID = 1L;
    private static KafkaConsumer<String, String> kafkaConsumer;
    private static Cleanup cleanup;

    private static class Cleanup {
        public void finalize() {
            kafkaConsumer.close();
        }
    }

    static {
        Properties kafkaProps = new Properties();
        kafkaProps.put("bootstrap.servers", "broker.kafka.svc.cluster.local:9092");
        kafkaProps.put("group.id", "SplitDoFn");
        kafkaProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaConsumer = new KafkaConsumer<>(kafkaProps);
        cleanup = new Cleanup();
    }

    @ProcessElement
    public ProcessContinuation process(ProcessContext c, ByteKeyRangeTracker tracker) {

        String topic = c.element();
        ByteKey byteKey = tracker.currentRestriction().getStartKey();

        if (tracker.tryClaim(byteKey)) {
            try {
                kafkaConsumer.subscribe(Arrays.asList(topic));
                ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    if (record != null) {
                        c.output(KV.of(topic, record.value()));
                    } else {
                        c.output(KV.of(topic, "NULL RECORD"));
                    }
                }
                kafkaConsumer.commitAsync();
                return ProcessContinuation.resume();
            } catch (IllegalArgumentException e) {
                return ProcessContinuation.resume();
            }
        }
        return ProcessContinuation.stop();
    }

    @GetInitialRestriction
    public ByteKeyRange getInitialRange(String element) {
        ByteKey startKey = ByteKey.copyFrom(element.getBytes());
        return ByteKeyRange.of(startKey, ByteKey.EMPTY);
    }

    @NewTracker
    public ByteKeyRangeTracker newTracker(ByteKeyRange restriction) {
        return ByteKeyRangeTracker.of(restriction);
    }
}
 */