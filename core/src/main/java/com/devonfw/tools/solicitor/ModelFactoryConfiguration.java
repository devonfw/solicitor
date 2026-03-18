package com.devonfw.tools.solicitor;

import java.lang.reflect.InvocationTargetException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.model.ModelFactory;
import com.devonfw.tools.solicitor.model.impl.ModelFactoryImpl;

/**
 * Configuration class to instantiate the ModelFactory implementation class configured via property
 * "solicitor.modelfactory-classname". The configured class must be a subclass of {@link ModelFactoryImpl} and must have
 * a default constructor. The instantiated ModelFactory is registered as a Spring bean and can thus be injected in other
 * classes.
 *
 */
@Configuration
public class ModelFactoryConfiguration {

  private String modelFactoryClassName;

  @Value("${solicitor.modelfactory-classname}")
  public void setModelFactoryClassName(String modelFactoryClassName) {

    this.modelFactoryClassName = modelFactoryClassName;
  }

  @Bean
  public ModelFactory modelFactory() {

    try {
      ModelFactoryImpl modelFactory = (ModelFactoryImpl) Class.forName(this.modelFactoryClassName).getConstructor()
          .newInstance();
      return modelFactory;
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
        | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
      throw new SolicitorRuntimeException("Could not instantiate ModelFactory implementation class", e);
    }
  }

}
