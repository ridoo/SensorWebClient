package org.n52.server.series.proxy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TimeseriesServiceAggregator {
    
    private Map<String, TimeseriesService> dataServices = new HashMap<String, TimeseriesService>();

    public Set<Entry<String, TimeseriesService>> getServices() {
        return Collections.unmodifiableSet(dataServices.entrySet());
    }
    
    Map<String, TimeseriesService> getDataServices() {
        return dataServices;
    }

    void setDataServices(Map<String, TimeseriesService> dataServices) {
        this.dataServices = dataServices;
    }
    
}
