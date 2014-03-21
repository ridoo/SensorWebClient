
package org.n52.api.v1;

import java.util.Map.Entry;

import org.n52.api.v1.output.CategoryOutputMerger;
import org.n52.io.IoParameters;
import org.n52.io.v1.data.CategoryOutput;
import org.n52.server.series.proxy.TimeseriesService;
import org.n52.server.series.proxy.TimeseriesServiceAggregator;
import org.n52.web.v1.srv.ParameterService;

/**
 * Aggregates the result of multiple category requests by means of a CategoryOutputMerger.
 */
public class CategoryServiceAggregator implements ParameterService<CategoryOutput> {

    private TimeseriesServiceAggregator aggregator = new TimeseriesServiceAggregator();
    
    private CategoryOutputMerger merger = new CategoryOutputMerger();

    @Override
    public CategoryOutput[] getExpandedParameters(IoParameters query) {
        // TODO Auto-generated method stub
        return null;

    }

    @Override
    public CategoryOutput[] getCondensedParameters(IoParameters query) {
        // TODO Auto-generated method stub
        return null;

    }

    @Override
    public CategoryOutput[] getParameters(String[] items) {
        // TODO Auto-generated method stub
        return null;

    }

    @Override
    public CategoryOutput[] getParameters(String[] items, IoParameters query) {
        // TODO Auto-generated method stub
        return null;

    }

    @Override
    public CategoryOutput getParameter(String item) {
        CategoryOutput result = null;
        for (Entry<String, TimeseriesService> entry : aggregator.getServices()) {
            TimeseriesService service = entry.getValue();
            result = service.getCategoriesService().getParameter(item);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    @Override
    public CategoryOutput getParameter(String item, IoParameters query) {
        // TODO Auto-generated method stub
        return null;

    }

    public TimeseriesServiceAggregator getAggregator() {
        return aggregator;
    }

    public void setAggregator(TimeseriesServiceAggregator aggregator) {
        this.aggregator = aggregator;
    }

}
