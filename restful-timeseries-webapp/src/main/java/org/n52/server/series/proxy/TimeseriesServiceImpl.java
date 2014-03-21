package org.n52.server.series.proxy;

import org.n52.io.v1.data.CategoryOutput;
import org.n52.io.v1.data.FeatureOutput;
import org.n52.io.v1.data.OfferingOutput;
import org.n52.io.v1.data.PhenomenonOutput;
import org.n52.io.v1.data.ProcedureOutput;
import org.n52.io.v1.data.StationOutput;
import org.n52.io.v1.data.TimeseriesMetadataOutput;
import org.n52.web.v1.srv.CountingMetadataService;
import org.n52.web.v1.srv.ParameterService;
import org.n52.web.v1.srv.SearchService;
import org.n52.web.v1.srv.ServiceParameterService;
import org.n52.web.v1.srv.TimeseriesDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeseriesServiceImpl implements TimeseriesService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeseriesServiceImpl.class);

    private String name;
    
    private ParameterService<PhenomenonOutput> phenomenonaService;
    
    private ParameterService<ProcedureOutput> procedureService;
    
    private ParameterService<CategoryOutput> categoryService;
    
    private ParameterService<OfferingOutput> offeringService;
    
    private ParameterService<FeatureOutput> featureService;
    
    private ParameterService<StationOutput> stationService;
    
    private ParameterService<TimeseriesMetadataOutput> timeseriesService;
    
    private TimeseriesDataService timeseriesDataService;
    
    private CountingMetadataService contingMetadataService;
    
    private ServiceParameterService servicesService;
    
    private SearchService searchService;
    
    public TimeseriesServiceImpl(String name) {
        this.name = name;
    }
    
    public String getName() {
        return new String(name);
    }

    @Override
    public ParameterService<PhenomenonOutput> getPhenomenaService() {
        if (phenomenonaService == null) {
            LOGGER.warn("No phenomena service configured for '{}'.", name);
        }
        return phenomenonaService;
    }

    @Override
    public ParameterService<ProcedureOutput> getProceduresService() {
        if (procedureService == null) {
            LOGGER.warn("No procedure service configured for '{}'.", name);
        }
        return procedureService;
    }

    @Override
    public ParameterService<CategoryOutput> getCategoriesService() {
        if (categoryService == null) {
            LOGGER.warn("No procedure service configured for '{}'.", name);
        }
        return categoryService;
    }

    @Override
    public ParameterService<OfferingOutput> getOfferingsService() {
        if (offeringService == null) {
            LOGGER.warn("No procedure service configured for '{}'.", name);
        }
        return offeringService;
    }

    @Override
    public ParameterService<FeatureOutput> getFeaturesService() {
        if (featureService == null) {
            LOGGER.warn("No ParameterService<FeatureOutput> configured for '{}'.", name);
        }
        return featureService;
    }

    @Override
    public ParameterService<StationOutput> getStationsService() {
        if (stationService == null) {
            LOGGER.warn("No ParameterService<StationOutput configured for '{}'.", name);
        }
        return stationService;
    }

    @Override
    public ParameterService<TimeseriesMetadataOutput> getTimeseriesService() {
        if (timeseriesService == null) {
            LOGGER.warn("No ParameterService<TimeseriesMetadataOutput> configured for '{}'.", name);
        }
        return timeseriesService;
    }

    @Override
    public TimeseriesDataService getTimeseriesDataService() {
        if (timeseriesDataService == null) {
            LOGGER.warn("No TimeseriesDataService configured for '{}'.", name);
        }
        return timeseriesDataService;
    }

    @Override
    public CountingMetadataService getCountingMetadataService() {
        if (contingMetadataService == null) {
            LOGGER.warn("No CountingMetadataService service configured for '{}'.", name);
        }
        return contingMetadataService;
    }

    @Override
    public ServiceParameterService getServicesService() {
        if (servicesService == null) {
            LOGGER.warn("No ServiceParameterService configured for '{}'.", name);
        }
        return servicesService;
    }

    @Override
    public SearchService getSearchService() {
        if (searchService == null) {
            LOGGER.warn("No SearchService configured for '{}'.", name);
        }
        return searchService;
    }

}
