package com.devonfw.tools.solicitor;

import java.lang.reflect.InvocationTargetException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.devonfw.tools.solicitor.common.SolicitorRuntimeException;
import com.devonfw.tools.solicitor.common.content.InMemoryMapContentProvider;
import com.devonfw.tools.solicitor.common.content.web.WebContent;
import com.devonfw.tools.solicitor.model.ModelFactory;
import com.devonfw.tools.solicitor.model.impl.ModelFactoryImpl;

/**
 * TODO ohecker This type ...
 *
 */
@Configuration
public class ModelFactoryConfiguration {

  private String modelFactoryClassName;

  private InMemoryMapContentProvider<WebContent> licenseContentProvider;

  private SolicitorVersion solicitorVersion;

  @Value("${solicitor.modelfactory-classname}")
  public void setModelFactoryClassName(String modelFactoryClassName) {

    this.modelFactoryClassName = modelFactoryClassName;
  }

  @Autowired
  public void setLicenseContentProvider(InMemoryMapContentProvider<WebContent> licenseContentProvider) {

    this.licenseContentProvider = licenseContentProvider;
  }

  @Autowired
  public void setSolicitorVersion(SolicitorVersion solicitorVersion) {

    this.solicitorVersion = solicitorVersion;
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
