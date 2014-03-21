package org.n52.server.series.proxy;

import org.n52.io.v1.data.CategoryOutput;
import org.n52.io.v1.data.FeatureOutput;
import org.n52.io.v1.data.OfferingOutput;
import org.n52.io.v1.data.PhenomenonOutput;
import org.n52.io.v1.data.ProcedureOutput;
import org.n52.io.v1.data.StationOutput;
import org.n52.io.v1.data.TimeseriesMetadataOutput;
import org.n52.io.v1.data.TimeseriesOutput;
import org.n52.web.v1.srv.CountingMetadataService;
import org.n52.web.v1.srv.ParameterService;
import org.n52.web.v1.srv.SearchService;
import org.n52.web.v1.srv.ServiceParameterService;
import org.n52.web.v1.srv.TimeseriesDataService;

public interface TimeseriesService {

    public ParameterService<PhenomenonOutput> getPhenomenaService();

    public ParameterService<ProcedureOutput> getProceduresService();
    
    public ParameterService<CategoryOutput> getCategoriesService();

    public ParameterService<OfferingOutput> getOfferingsService();
    
    public ParameterService<FeatureOutput> getFeaturesService();
    
    public ParameterService<StationOutput> getStationsService();
    
    public ParameterService<TimeseriesMetadataOutput> getTimeseriesService();
    
    public TimeseriesDataService getTimeseriesDataService();

    public CountingMetadataService getCountingMetadataService();

    public ServiceParameterService getServicesService();

    public SearchService getSearchService();

}
