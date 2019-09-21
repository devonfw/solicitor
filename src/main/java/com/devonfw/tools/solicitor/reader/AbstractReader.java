/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.reader;

import org.springframework.beans.factory.annotation.Autowired;

import com.devonfw.tools.solicitor.model.ModelFactory;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;

/**
 * Abstract base functionality of a {@link Reader}.
 */
public abstract class AbstractReader implements Reader {

    private ModelFactory modelFactory;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(String type) {

        return getSupportedType().equals(type);
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
     * This method sets the field <tt>modelFactory</tt>.
     *
     * @param modelFactory the new value of the field modelFactory
     */
    @Autowired
    public void setModelFactory(ModelFactory modelFactory) {

        this.modelFactory = modelFactory;
    }

    /**
     * Returns the supported type.
     * 
     * @return the supported type
     */
    public abstract String getSupportedType();

    /**
     * Adds a {@link RawLicense} to the given {@link ApplicationComponent}.
     * 
     * @param appComponent
     * @param name
     * @param url
     */
    public void addRawLicense(ApplicationComponent appComponent, String name,
            String url, String path) {

        RawLicense mlic = modelFactory.newRawLicense();
        mlic.setApplicationComponent(appComponent);
        mlic.setDeclaredLicense(name);
        mlic.setLicenseUrl(url);
        String trace;
        if (name == null && url == null) {
            trace = "+ Component info (without license) read in '"
                    + getSupportedType() + "' format from '" + path + "'";
        } else {
            trace = "+ Component/License info read in '" + getSupportedType()
                    + "' format from '" + path + "'";

        }
        mlic.setTrace(trace);
    }

}
