package org.n52.api.v1.srv;

import static org.n52.server.mgmt.ConfigurationContext.getServiceMetadatas;

import java.util.ArrayList;
import java.util.List;

import org.n52.api.v1.io.OfferingConverter;
import org.n52.io.v1.data.OfferingOutput;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.n52.web.v1.srv.ParameterService;

public class OfferingOutputAdapter implements ParameterService<OfferingOutput> {

	@Override
	public OfferingOutput[] getExpandedParameters(int offset, int size) {
		List<OfferingOutput> allOfferings = new ArrayList<OfferingOutput>();
		for (SOSMetadata metadata : getServiceMetadatas().values()) {
		    OfferingConverter converter = new OfferingConverter(metadata);
			TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
			allOfferings.addAll(converter.convertExpanded(lookup.getOfferingsAsArray()));
		}
		return allOfferings.toArray(new OfferingOutput[0]);
	}
	
	@Override
    public OfferingOutput[] getCondensedParameters(int offset, int size) {
        List<OfferingOutput> allOfferings = new ArrayList<OfferingOutput>();
        for (SOSMetadata metadata : getServiceMetadatas().values()) {
            OfferingConverter converter = new OfferingConverter(metadata);
            TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
            allOfferings.addAll(converter.convertCondensed(lookup.getOfferingsAsArray()));
        }
        return allOfferings.toArray(new OfferingOutput[0]);
    }

	@Override
	public OfferingOutput getParameter(String offeringId) {
		for (SOSMetadata metadata : getServiceMetadatas().values()) {
			TimeseriesParametersLookup lookup = metadata.getTimeseriesParametersLookup();
			if(lookup.containsOffering(offeringId)) {
			    OfferingConverter converter = new OfferingConverter(metadata);
				return converter.convertExpanded(lookup.getOffering(offeringId));
			}
		}
		return null;
	}

}