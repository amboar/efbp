package au.id.aj.efbp.lifecycle;

import au.id.aj.efbp.net.Consumer;
import au.id.aj.efbp.net.Producer;

public interface Lifecycle
{
    /**
     * Mark the provided producer as finished, that is, it has exhausted its
     * data source and will push no further data packets into the network. Once
     * all Producer instances in the network have marked themselves as
     * exhausted the network will organically begin its shutdown sequence.
     *
     * @param producer
     *          The producer that will produce no further data packets.
     *
     * @throws IllegalStateException
     *          If the provided producer has been previously marked as
     *          exhausted.
     */
    void shutdown(final Producer<?> producer);

    /**
     * Mark the provided consumer as finished, that is, data packets received
     * beyond the consumer calling shutdown() shall be ignored.
     *
     * @param consumer
     *          The consumer that will process no further data packets.
     *
     * @throws IllegalStateException
     *          If the provided consumer has been previously marked as
     *          shutdown.
     */
    void shutdown(final Consumer<?> consumer);
}
