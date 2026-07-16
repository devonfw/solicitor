package com.devonfw.tools.solicitor.writer.velocity;

import com.devonfw.tools.solicitor.common.ApplicationComponentCoordinates;
import com.devonfw.tools.solicitor.common.PackageURLHelper;
import com.devonfw.tools.solicitor.common.packageurl.PackageURLHandler;
import com.devonfw.tools.solicitor.common.packageurl.SolicitorMalformedPackageURLException;
import com.devonfw.tools.solicitor.common.packageurl.SolicitorPackageURLUnavailableOperationException;
import com.github.packageurl.PackageURL;

/**
 * An implementation of the {@link StringCapablePackageURLHandler} interface which uses a delegate for the core
 * {@link PackageURLHandler} functionality.
 *
 */
public class DelegatingStringCapablePackageURLHandler implements StringCapablePackageURLHandler {

  private PackageURLHandler delegate;

  /**
   * The constructor.
   *
   * @param delegate the delegate which provides the {@link PackageURLHandler} functionality.
   */
  public DelegatingStringCapablePackageURLHandler(PackageURLHandler delegate) {

    super();
    this.delegate = delegate;
  }

  @Override
  public String sourceDownloadUrlFor(PackageURL packageUrl) throws SolicitorPackageURLUnavailableOperationException {

    return this.delegate.sourceDownloadUrlFor(packageUrl);
  }

  @Override
  public String packageDownloadUrlFor(PackageURL packageUrl) throws SolicitorPackageURLUnavailableOperationException {

    return this.delegate.packageDownloadUrlFor(packageUrl);
  }

  @Override
  public String pathFor(PackageURL packageUrl) {

    return this.delegate.pathFor(packageUrl);
  }

  @Override
  public String sourceArchiveSuffixFor(PackageURL packageUrl) throws SolicitorPackageURLUnavailableOperationException {

    return this.delegate.sourceArchiveSuffixFor(packageUrl);
  }

  @Override
  public ApplicationComponentCoordinates coordinatesFor(PackageURL packageUrl)
      throws SolicitorPackageURLUnavailableOperationException {

    return this.delegate.coordinatesFor(packageUrl);
  }

  @Override
  public String sourceDownloadUrlFor(String packageUrl)
      throws SolicitorPackageURLUnavailableOperationException, SolicitorMalformedPackageURLException {

    return this.delegate.sourceDownloadUrlFor(PackageURLHelper.fromString(packageUrl));
  }

  @Override
  public String packageDownloadUrlFor(String packageUrl)
      throws SolicitorPackageURLUnavailableOperationException, SolicitorMalformedPackageURLException {

    return this.delegate.packageDownloadUrlFor(PackageURLHelper.fromString(packageUrl));
  }

  @Override
  public String pathFor(String packageUrl) throws SolicitorMalformedPackageURLException {

    return this.delegate.pathFor(PackageURLHelper.fromString(packageUrl));
  }

  @Override
  public String sourceArchiveSuffixFor(String packageUrl)
      throws SolicitorPackageURLUnavailableOperationException, SolicitorMalformedPackageURLException {

    return this.delegate.sourceArchiveSuffixFor(PackageURLHelper.fromString(packageUrl));
  }

  @Override
  public ApplicationComponentCoordinates coordinatesFor(String packageUrl)
      throws SolicitorPackageURLUnavailableOperationException, SolicitorMalformedPackageURLException {

    return this.delegate.coordinatesFor(PackageURLHelper.fromString(packageUrl));
  }

}
