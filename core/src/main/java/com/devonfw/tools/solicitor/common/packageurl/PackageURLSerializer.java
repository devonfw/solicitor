package com.devonfw.tools.solicitor.common.packageurl;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.packageurl.PackageURL;

/**
 * A json serializer for {@link PackageURL}s.
 *
 */
public class PackageURLSerializer extends StdSerializer<PackageURL> {

  /**
   * Non arg constructor.
   */
  public PackageURLSerializer() {

    this(null);
  }

  /**
   * Constructor taking the class to serialize
   *
   * @param t the class to serialize
   */
  public PackageURLSerializer(Class<PackageURL> t) {

    super(t);
  }

  @Override
  public void serialize(PackageURL value, JsonGenerator gen, SerializerProvider provider) throws IOException {

    gen.writeString(value.toString());
  }
}