package org.n52.api.v1.srv;

import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadataForItemName;
import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;

import java.util.ArrayList;
import java.util.List;

import org.n52.api.v1.io.ServiceConverter;
import org.n52.io.v1.data.ServiceOutput;
import org.n52.server.mgmt.ConfigurationContext;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.web.v1.srv.ServiceParameterService;

public class ServiceOutputAdapter implements ServiceParameterService {

    @Override
    public boolean isKnownTimeseries(String timeseriesId) {
        for (SOSMetadata metadatas : getSOSMetadatas()) {
            if (metadatas.getStationByTimeSeriesId(timeseriesId) != null) {
                return true;
            }
        }
        return false;
    }

	@Override
	public ServiceOutput[] getExpandedParameters(int offset, int size) {
		List<ServiceOutput> allServices = new ArrayList<ServiceOutput>();
		for (SOSMetadata metadata : ConfigurationContext.getSOSMetadatas()) {
		    ServiceConverter converter = new ServiceConverter(metadata);
			allServices.add(converter.convertExpanded(metadata));
		}
		return allServices.toArray(new ServiceOutput[0]);
	}
	
	@Override
    public ServiceOutput[] getCondensedParameters(int offset, int size) {
        List<ServiceOutput> allServices = new ArrayList<ServiceOutput>();
        for (SOSMetadata metadata : ConfigurationContext.getSOSMetadatas()) {
            ServiceConverter converter = new ServiceConverter(metadata);
            allServices.add(converter.convertCondensed(metadata));
        }
        return allServices.toArray(new ServiceOutput[0]);
    }

	@Override
	public ServiceOutput getParameter(String item) {
		SOSMetadata metadata = getSOSMetadataForItemName(item);
		if (metadata != null){
		    ServiceConverter converter = new ServiceConverter(metadata);
			return converter.convertExpanded(metadata);
		}
		return null;
	}

}