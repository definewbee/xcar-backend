package io.doeasy.xcar.abey;

import org.web3j.abi.datatypes.Event;

/**
 * @author kris.wang
 */
public interface EventBuilder<T extends Enum> {
    /**
     * build eth event
     * @return
     */
    Event build(T type);
}
