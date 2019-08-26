/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.capgemini.solicitor.reader.maven.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement(name = "licenseSummary")
@XmlAccessorType(XmlAccessType.FIELD)
public class LicenseSummary {
    @XmlElementWrapper(name = "dependencies")
    @XmlElement(name = "dependency")
    private ArrayList<Dependency> dependencies;

}
