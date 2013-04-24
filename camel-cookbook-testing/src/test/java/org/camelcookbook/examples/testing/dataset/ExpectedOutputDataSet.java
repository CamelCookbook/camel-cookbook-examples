package org.camelcookbook.examples.testing.dataset;

import org.apache.camel.component.dataset.DataSetSupport;

/**
 * Data set used to generate expected messages coming out of the route.
 */
public class ExpectedOutputDataSet extends DataSetSupport {
    @Override
    protected Object createMessageBody(long messageIndex) {
        return "Modified: message " + messageIndex;
    }

}
