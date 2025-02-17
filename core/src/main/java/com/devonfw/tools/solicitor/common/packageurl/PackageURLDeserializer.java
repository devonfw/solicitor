package com.devonfw.tools.solicitor.common.packageurl;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;

/**
 * A json deserializer for {@link PackageURL}s.
 *
 */
public class PackageURLDeserializer extends StdDeserializer<PackageURL> {

  /**
   * Non arg constructor.
   */
  public PackageURLDeserializer() {

    this(null);
  }

  /**
   * Constructor taking the value class to deserialize.
   *
   * @param vc the value class to deserialize.
   */
  public PackageURLDeserializer(Class<PackageURL> vc) {

    super(vc);
  }

  @Override
  public PackageURL deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

    String url = p.getText();
    try {
      return new PackageURL(url);
    } catch (MalformedPackageURLException e) {
      throw new IOException("Malformed PackageURL", e);
    }
  }
}