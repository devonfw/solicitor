/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.capgemini.solicitor.reader.maven.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class License {
    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "url")
    private String url;

    @XmlElement(name = "distribution")
    private String distribution;

}
