package com.devonfw.tools.solicitor.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.github.packageurl.PackageURL;

/**
 * Tests for class {@link PackageURLHelper}.
 *
 */
class PackageURLHelperTests {

  @Test
  void testFromNpmPackageNameWithVersionUnscoped() {

    PackageURL purl = PackageURLHelper.fromNpmPackageNameWithVersion("a@1");
    assertEquals("npm", purl.getType());
    assertEquals("a", purl.getName());
    assertEquals("1", purl.getVersion());
    assertEquals("pkg:npm/a@1", purl.toString());
  }

  @Test
  void testFromNpmPackageNameWithVersionScoped() {

    PackageURL purl = PackageURLHelper.fromNpmPackageNameWithVersion("@b/a@1");
    assertEquals("npm", purl.getType());
    assertEquals("@b", purl.getNamespace());
    assertEquals("a", purl.getName());
    assertEquals("1", purl.getVersion());
    assertEquals("pkg:npm/%40b/a@1", purl.toString());
  }

  @Test
  void testFromNpmPackageNameWithVersionLonger() {

    PackageURL purl = PackageURLHelper.fromNpmPackageNameWithVersion("@somenamespace/package@4.5.35");
    assertEquals("npm", purl.getType());
    assertEquals("@somenamespace", purl.getNamespace());
    assertEquals("package", purl.getName());
    assertEquals("4.5.35", purl.getVersion());
    assertEquals("pkg:npm/%40somenamespace/package@4.5.35", purl.toString());
  }

  @Test
  void testFromNpmPackageNameWithVersionNoVersion() {

    Assertions.assertThrows(SolicitorRuntimeException.class, () -> {
      PackageURLHelper.fromNpmPackageNameWithVersion("@somenamespace/package@");
    });
  }

  @Test
  void testFromNpmPackageNameWithVersionMultipeSlashes() {

    Assertions.assertThrows(SolicitorRuntimeException.class, () -> {
      PackageURLHelper.fromNpmPackageNameWithVersion("@somename/space/package@77");
    });
  }

  @Test
  void testFromNpmPackageNameAndVersionUnscoped() {

    PackageURL purl = PackageURLHelper.fromNpmPackageNameAndVersion("a", "1");
    assertEquals("npm", purl.getType());
    assertEquals("a", purl.getName());
    assertEquals("1", purl.getVersion());
    assertEquals("pkg:npm/a@1", purl.toString());
  }

  @Test
  void testFromNpmPackageNameAndVersionScoped() {

    PackageURL purl = PackageURLHelper.fromNpmPackageNameAndVersion("@b/a", "1");
    assertEquals("npm", purl.getType());
    assertEquals("@b", purl.getNamespace());
    assertEquals("a", purl.getName());
    assertEquals("1", purl.getVersion());
    assertEquals("pkg:npm/%40b/a@1", purl.toString());
  }

  @Test
  void testFromNpmPackageNameWithAndVersionLonger() {

    PackageURL purl = PackageURLHelper.fromNpmPackageNameAndVersion("@somenamespace/package", "4.5.35");
    assertEquals("npm", purl.getType());
    assertEquals("@somenamespace", purl.getNamespace());
    assertEquals("package", purl.getName());
    assertEquals("4.5.35", purl.getVersion());
    assertEquals("pkg:npm/%40somenamespace/package@4.5.35", purl.toString());
  }

  @Test
  void testFromNpmPackageNameAndVersionMultipeSlashes() {

    Assertions.assertThrows(SolicitorRuntimeException.class, () -> {
      PackageURLHelper.fromNpmPackageNameAndVersion("@somename/space/package", "77");
    });
  }

  @Test
  void testFromMavenCoordinates() {

    PackageURL purl = PackageURLHelper.fromMavenCoordinates("group", "artifact", "1.0");
    assertEquals("maven", purl.getType());
    assertEquals("group", purl.getNamespace());
    assertEquals("artifact", purl.getName());
    assertEquals("1.0", purl.getVersion());
    assertEquals("pkg:maven/group/artifact@1.0", purl.toString());
  }

  @Test
  void testFromPyPICoordinates() {

    PackageURL purl = PackageURLHelper.fromPyPICoordinates("name", "1.0");
    assertEquals("pypi", purl.getType());
    assertNull(purl.getNamespace());
    assertEquals("name", purl.getName());
    assertEquals("1.0", purl.getVersion());
    assertEquals("pkg:pypi/name@1.0", purl.toString());
  }
}
