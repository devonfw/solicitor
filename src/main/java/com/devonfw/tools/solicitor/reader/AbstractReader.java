/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.reader;

import org.springframework.beans.factory.annotation.Autowired;

import com.devonfw.tools.solicitor.common.InputStreamFactory;
import com.devonfw.tools.solicitor.model.ModelFactory;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;

/**
 * Abstract base functionality of a
 * {@link com.devonfw.tools.solicitor.reader.Reader}.
 */
public abstract class AbstractReader implements Reader {

    private ModelFactory modelFactory;

    @Autowired
    protected InputStreamFactory inputStreamFactory;

    /** {@inheritDoc} */
    @Override
    public boolean accept(String type) {

        return getSupportedType().equals(type);
    }

    /**
     * Adds a {@link com.devonfw.tools.solicitor.model.inventory.RawLicense} to
     * the given
     * {@link com.devonfw.tools.solicitor.model.inventory.ApplicationComponent}.
     *
     * @param appComponent a
     *        {@link com.devonfw.tools.solicitor.model.inventory.ApplicationComponent}
     *        object.
     * @param name a {@link java.lang.String} object.
     * @param url a {@link java.lang.String} object.
     * @param path a {@link java.lang.String} object.
     */
    public void addRawLicense(ApplicationComponent appComponent, String name, String url, String path) {

        RawLicense mlic = modelFactory.newRawLicense();
        mlic.setApplicationComponent(appComponent);
        mlic.setDeclaredLicense(name);
        mlic.setLicenseUrl(url);
        String trace;
        if (name == null && url == null) {
            trace = "+ Component info (without license) read in '" + getSupportedType() + "' format from '" + path
                    + "'";
        } else {
            trace = "+ Component/License info read in '" + getSupportedType() + "' format from '" + path + "'";

        }
        mlic.setTrace(trace);
    }

    /**
     * This method gets the field <tt>modelFactory</tt>.
     *
     * @return the field modelFactory
     */
    public ModelFactory getModelFactory() {

        return modelFactory;
    }

    /**
     * Returns the supported type.
     *
     * @return the supported type
     */
    public abstract String getSupportedType();

    /**
     * This method sets the field <tt>inputStreamFactory</tt>.
     *
     * @param inputStreamFactory the new value of the field inputStreamFactory
     */
    public void setInputStreamFactory(InputStreamFactory inputStreamFactory) {

        this.inputStreamFactory = inputStreamFactory;
    }

    /**
     * This method sets the field <tt>modelFactory</tt>.
     *
     * @param modelFactory the new value of the field modelFactory
     */
    @Autowired
    public void setModelFactory(ModelFactory modelFactory) {

        this.modelFactory = modelFactory;
    }

}
