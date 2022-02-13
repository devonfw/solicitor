/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.devonfw.tools.solicitor.SolicitorCliProcessor.CommandLineOptions;

/**
 * Runs Solicitor on its own licenses.xml (which needs to exist in target/generated-resources). This creates the
 * solicitor_licenseinfo.html file which is included in the distribution.
 */
@SpringBootTest(properties = { "webcontent.skipdownload=true" })
@RunWith(SpringRunner.class)
public class SolicitorCreateOwnLicenseInfoTest {

  @Autowired
  private Solicitor solicitor;

  @Test
  public void test() {

    String[] args = new String[] { "solicitor", "-c", "classpath:ownlicenseinfo/solicitor_own.cfg" };
    SolicitorCliProcessor scp = new SolicitorCliProcessor();
    CommandLineOptions clo = scp.parse(args);

    solicitor.run(clo, String.join(" ", args));
  }

}
