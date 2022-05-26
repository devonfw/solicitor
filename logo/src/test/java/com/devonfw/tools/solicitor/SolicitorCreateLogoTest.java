/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor;

import java.awt.Rectangle;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

/**
 * Creates variants/animations of the Solicitor Logo. We use a (pseudo) test to easily integrate into the maven
 * lifecycle. Due to high resource consumption this test is disabled by default and might be enabled by setting system
 * property <code>createLogos</code>.
 *
 */
@EnabledIfSystemProperty(named = "createLogos", matches = "\\.*", //
    disabledReason = "pseudo test to create logos disabled by default due to long "
        + "execution time and resource consumption")
public class SolicitorCreateLogoTest {

  private static final String[] COLORS = { "#4cbdec", "#66f6ff", "#23576e" };

  private static final int[] WIDTHS = { 5000, 2500, 1000, 500, 200 };

  /**
   * Test for creating different variants (size/coloring) of the logo.
   *
   * @throws Exception if something goes wrong
   */
  @Test
  public void createLogoVariants() throws Exception {

    for (int width : WIDTHS) {
      for (String color : COLORS) {
        Map<String, String> parameters = getDefaultParameterset(width / 5000.0);
        parameters.put("##FONT_COLOR##", color);
        createImagesFromTemplate(parameters, true,
            "target/logo/static/solicitor_logo_" + color.replace("#", "") + "_" + width, width,
            (int) Math.round(width / 5000.0 * 1500.0));
      }
    }
  }

  /**
   * Test for creating a sequence of frames showing an animation of the logo. By default this test is disabled to avoid
   * long build times.
   *
   * @throws Exception if something goes wrong
   */
  @Test
  public void createAnimationFrames() throws Exception {

    double length = 7.0;
    double framesPerSecond = 10;
    double color_start = 2.0;
    double color_end = 3.0;
    double flyin_start = 2.5;
    double flyin_end = 5.0;

    for (int width : WIDTHS) {
      for (String color : COLORS) {
        for (int frame = 0; frame < Math.round(length * framesPerSecond); frame++) {
          String fontColorCode = color.replace("#", "");
          double scale = width / 5000.0;
          Map<String, String> parameters = getDefaultParameterset(scale);
          double t = frame / framesPerSecond;
          String x_soli = interpolate(t, -1.0, -2710.0, flyin_start, -2710.0, flyin_end, 290.0, 100.0, 290.0);
          String x_itor = interpolate(t, -1.0, 5978.0, flyin_start, 5978.0, flyin_end, 2978.0, 100.0, 2978.0);
          String o = interpolate(t, -1.0, 0.0, color_start, 0.0, color_end, 1.0, 100.0, 1.0);
          String c = interpolate(t, -1.0, fontColorCode, color_start, fontColorCode, color_end, "fd7700", 100.0,
              "fd7700");
          parameters.put("##X_SOLI##", x_soli);
          parameters.put("##X_ITOR##", x_itor);
          parameters.put("##FONT_COLOR##", color);
          parameters.put("##MGGLASS_GLASS_COLOR##", "#" + c);
          parameters.put("##MGGLASS_HOLDER_COLOR##", "#" + c);
          parameters.put("##MGGLASS_HOLDER_OPACITY##", o);
          createImagesFromTemplate(parameters, false, "target/logo/animation_" + color.replace("#", "") + "_" + width
              + "/solicitor_" + String.format("%04d", frame), (int) Math.round(5000 * scale),
              (int) Math.round(1500 * scale));
        }
      }
    }
  }

  /**
   * Loads the template and applies parameters, then saves to disk.
   */
  private void createImagesFromTemplate(Map<String, String> parameters, boolean withSVG, String outFileName, int width,
      int height) throws Exception {

    String svgSource = getSVGSource(parameters);
    if (withSVG) {
      saveAsSVG(outFileName + ".svg", svgSource);
    }
    saveAsPNG(outFileName + ".png", svgSource, width, height);
  }

  /**
   * Gets the template and applies parameters.
   */
  private String getSVGSource(Map<String, String> parameters) throws IOException, FileNotFoundException {

    String result;
    try (InputStream in = new FileInputStream("src/test/resources/logo/logo_template.svg")) {

      String template = IOHelperSimple.readStringFromInputStream(in);
      result = template;
      for (Entry<String, String> parameterEntry : parameters.entrySet()) {
        result = result.replace(parameterEntry.getKey(), parameterEntry.getValue());
      }

    }
    return result;
  }

  /**
   * Gets the default parameterset.
   */
  private Map<String, String> getDefaultParameterset(double scale) {

    Map<String, String> parameters = new TreeMap<>();
    parameters.put("##FONT_COLOR##", "#4cbdec");
    parameters.put("##FONT_FAMILY##", "Ubuntu-Bold, Ubuntu");
    parameters.put("##FONT_WEIGHT##", "700");
    parameters.put("##FONT_SIZE##", "1000");
    parameters.put("##MGGLASS_HOLDER_COLOR##", "#fd7700");
    parameters.put("##MGGLASS_GLASS_COLOR##", "#fd7700");
    parameters.put("##MGGLASS_HOLDER_OPACITY##", "1");
    parameters.put("##WIDTH##", Double.toString(5000 * scale));
    parameters.put("##HEIGHT##", Double.toString(1500 * scale));
    parameters.put("##BG_COLOR##", "none");
    parameters.put("##SCALE##", Double.toString(scale));
    parameters.put("##X_SOLI##", "290");
    parameters.put("##X_ITOR##", "2978");
    parameters.put("##X_C##", "2232");
    return parameters;
  }

  /**
   * Convert the the SVG to PNG and save to file.
   */
  private void saveAsPNG(String outFileName, String svgSource, int width, int height) throws Exception {

    IOHelperSimple.checkAndCreateLocation(outFileName);

    try (StringReader stringReader = new StringReader(svgSource);
        OutputStream ostream = new FileOutputStream(outFileName)) {

      TranscoderInput ti = new TranscoderInput(stringReader);
      TranscoderOutput to = new TranscoderOutput(ostream);

      PNGTranscoder transcoder = new PNGTranscoder();

      Rectangle rect = new Rectangle(0, 0, width, height);
      transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, Float.valueOf(rect.width));
      transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, Float.valueOf(rect.height));
      transcoder.addTranscodingHint(PNGTranscoder.KEY_AOI, rect);

      System.out.println("Writing Logo to: " + outFileName);
      transcoder.transcode(ti, to);

    }
  }

  /**
   * Save the SVG to file.
   */
  private void saveAsSVG(String outFileName, String svgSource) throws IOException {

    IOHelperSimple.checkAndCreateLocation(outFileName);
    System.out.println("Writing Logo to: " + outFileName);
    try (FileWriter fw = new FileWriter(outFileName)) {
      fw.append(svgSource);
    }
  }

  /**
   * Interpolate values on a given path.
   */
  private String interpolate(double t, Object... args) {

    if (args.length % 2 != 0) {
      throw new IllegalArgumentException("Needs to have even number of arguments");
    }
    double t_array[] = new double[args.length / 2];
    Object v_array[] = new Object[args.length / 2];
    for (int i = 0; i < args.length / 2; i++) {
      t_array[i] = (double) args[2 * i];
      if (i > 1 && t_array[i] <= t_array[i - 1]) {
        throw new IllegalArgumentException(
            "Needs to be increasing! " + t_array[i] + " is not greater than" + t_array[i - 1]);

      }
      v_array[i] = args[2 * i + 1];
    }

    double intervalFraction = 0;
    Object low = null;
    Object high = null;
    for (int i = 0; i < args.length / 2; i++) {
      if (t >= t_array[i]) {
        intervalFraction = (t - t_array[i]) / (t_array[i + 1] - t_array[i]);
        low = v_array[i];
        high = v_array[i + 1];
      }
    }
    if (low instanceof String) {
      return interpolateColor(intervalFraction, low, high);
    } else {
      return interpolateNumber(intervalFraction, low, high);
    }
  }

  /**
   * Interpolate between two colors.
   */
  private String interpolateColor(double intervalFraction, Object low, Object high) {

    int color = Integer.parseUnsignedInt((String) low, 16);
    int lcComponents[] = new int[3];
    lcComponents[0] = color % 256;
    color = color / 256;
    lcComponents[1] = color % 256;
    color = color / 256;
    lcComponents[2] = color % 256;

    color = Integer.parseUnsignedInt((String) high, 16);
    int hcComponents[] = new int[3];
    hcComponents[0] = color % 256;
    color = color / 256;
    hcComponents[1] = color % 256;
    color = color / 256;
    hcComponents[2] = color % 256;

    int rcComponents[] = new int[3];
    for (int i = 0; i < 3; i++) {
      rcComponents[i] = (int) Math.round((hcComponents[i] - lcComponents[i]) * intervalFraction + lcComponents[i]);
    }
    int result = ((rcComponents[2] * 256) + rcComponents[1]) * 256 + rcComponents[0];
    return Integer.toHexString(result);
  }

  /**
   * Interpolate between two numbers.
   */
  private String interpolateNumber(double intervalFraction, Object low, Object high) {

    double rescale = Math.sin(Math.PI / 2 * intervalFraction);
    rescale = rescale * rescale;

    double result = ((double) high - (double) low) * rescale + (double) low;
    return Double.toString(result);
  }

}
