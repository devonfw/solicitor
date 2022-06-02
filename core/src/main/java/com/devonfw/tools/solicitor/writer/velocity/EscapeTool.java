package com.devonfw.tools.solicitor.writer.velocity;

import org.apache.commons.codec.digest.DigestUtils;

import com.devonfw.tools.solicitor.common.LicenseTextHelper;

/**
 * TODO ohecker This type ...
 *
 */
public class EscapeTool extends org.apache.velocity.tools.generic.EscapeTool {

  public String newLinesAsBreaks(String input) {

    return input != null ? input.replace("\n", "<br/>") : null;
  }

  public String wrap100to80(String input) {

    return LicenseTextHelper.wrapIfNecessary(input, 100, 80);
  }

  public String hash(String input) {

    if (input == null) {
      return "";
    }
    return new DigestUtils("SHA-256").digestAsHex(input);
  }
}
