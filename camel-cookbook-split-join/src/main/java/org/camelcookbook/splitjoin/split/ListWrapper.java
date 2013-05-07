package org.camelcookbook.splitjoin.split;

import java.util.List;

/**
 * Simple bean that contains a List of Strings.
 */
public class ListWrapper {

    private List<String> wrapped;

    public List<String> getWrapped() {
        return wrapped;
    }

    public void setWrapped(List<String> wrapped) {
        this.wrapped = wrapped;
    }
}
