/**
 * Copyright 2019 Capgemini SE.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.capgemini.solicitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SolicitorApplication {

    public static void main(String[] args) {

        ApplicationContext context =
                SpringApplication.run(SolicitorApplication.class, args);
        Solicitor solicitor = context.getBean(Solicitor.class);
        solicitor.run(args);

    }

}
