package org.camelcookbook.examples.testing.dataset;

import org.apache.camel.component.dataset.DataSetSupport;

/**
 * @author jkorab
 */
public class InputDataSet extends DataSetSupport {
    @Override
    protected Object createMessageBody(long l) {
        return "message " + l;
    }
}
