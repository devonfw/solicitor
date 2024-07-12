/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.devonfw.tools.solicitor.SolicitorCliProcessor.CommandLineOptions;

/**
 * Runs Solicitor on its own licenses.xml (which needs to exist in target/generated-resources). This creates the
 * solicitor_licenseinfo.html file which is included in the distribution.
 */
@SpringBootTest(properties = { "webcontent.skipdownload=true" })
public class SolicitorCreateOwnLicenseInfoTest {

  @Autowired
  private Solicitor solicitor;

  @Test
  public void test() {

    String[] args = new String[] { "solicitor", "-c", "classpath:ownlicenseinfo/solicitor_own.cfg" };
    SolicitorCliProcessor scp = new SolicitorCliProcessor();
    CommandLineOptions clo = scp.parse(args);

    this.solicitor.run(clo, String.join(" ", args));
  }

}
