/**
 * SPDX-License-Identifier: Apache-2.0
 */
package com.devonfw.tools.solicitor.licensetexts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.devonfw.tools.solicitor.SolicitorVersion;
import com.devonfw.tools.solicitor.common.content.ClasspathContentProvider;
import com.devonfw.tools.solicitor.common.content.ContentFactory;
import com.devonfw.tools.solicitor.common.content.ContentProvider;
import com.devonfw.tools.solicitor.common.content.FilesystemCachingContentProvider;
import com.devonfw.tools.solicitor.common.content.InMemoryMapContentProvider;
import com.devonfw.tools.solicitor.common.content.web.WebContent;

/**
 * Java Spring configuration for the beans in the area of the {@link ContentProvider} for
 * {@link GuessedLicenseUrlContent}.
 */
@Configuration
public class LicenseUrlGuesserConfiguration {

  @Value("${solicitor.classpath-guessedlicenseurl-cache-locations}")
  private String[] cachePaths;

  @Autowired
  private InMemoryMapContentProvider<WebContent> inMemoryMapWebContentProvider;

  @Autowired
  private SolicitorVersion solicitorVersion;

  @Bean
  public ContentFactory<GuessedLicenseUrlContent> guessedLicenseUrlContentFactory() {

    return new GuessedLicenseUrlContentFactory();
  }

  @Bean
  public InMemoryMapContentProvider<GuessedLicenseUrlContent> inMemoryMapGuessedLicenseUrlContentProvider() {

    return new InMemoryMapContentProvider<>(guessedLicenseUrlContentFactory(),
        classpathGuessedLicenseUrlContentProvider());

  }

  @Bean
  public ClasspathContentProvider<GuessedLicenseUrlContent> classpathGuessedLicenseUrlContentProvider() {

    return new ClasspathContentProvider<>(guessedLicenseUrlContentFactory(),
        filesystemCachingGuessedLicenseUrlContentProvider(), this.cachePaths);
  }

  @Bean
  public FilesystemCachingContentProvider<GuessedLicenseUrlContent> filesystemCachingGuessedLicenseUrlContentProvider() {

    return new FilesystemCachingContentProvider<>(guessedLicenseUrlContentFactory(), strategyLicenseUrlGuesser(),
        "licenseurls");
  }

  @Bean
  public StrategyLicenseUrlGuesser strategyLicenseUrlGuesser() {

    return new StrategyLicenseUrlGuesser(this.inMemoryMapWebContentProvider, this.solicitorVersion);
  }
}
