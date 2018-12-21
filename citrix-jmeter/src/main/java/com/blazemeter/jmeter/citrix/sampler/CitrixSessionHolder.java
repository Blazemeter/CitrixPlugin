/**
 * 
 */
package com.blazemeter.jmeter.citrix.sampler;

import com.blazemeter.jmeter.citrix.client.CitrixClient;

/**
 * Holds the Citrix Client and underlying session
 */
public class CitrixSessionHolder {
    protected static final ThreadLocal<CitrixClient> LOCAL_CLIENT = new ThreadLocal<>();

    /**
     * 
     */
    private CitrixSessionHolder() {
        super();
    }
    
    /**
     * @return {@link CitrixClient} or null if not initialized
     */
    public static CitrixClient getClient() {
        return LOCAL_CLIENT.get();
    }

    /**
     * Setup CitrixClient as soon as available
     * @param client {@link CitrixClient} or null if not initialized
     */
    static void setClient(CitrixClient client) {
        LOCAL_CLIENT.set(client);
    }

}
