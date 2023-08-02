package com.devonfw.tools.solicitor.componentinfo;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * This class contains JUnit test methods for the {@link ComponentInfoInventoryProcessor} class.
 */
class ComponentInfoInventoryProcessorTest {
  /**
   * Test the {@link ComponentInfoInventoryProcessor#formatTraceabilityNotes(ComponentInfo)} method with a mock
   * ComponentInfo containing traceability notes.
   */
  @Test
  public void testFormatTraceabilityNotes() {

    // Create a mock ComponentInfo with multiline traceability notes
    ComponentInfo componentInfo = Mockito.mock(ComponentInfo.class);
    Collection<String> traceabilityNotes = Arrays.asList("This is note 1", "Note 2 .",
        "And here's note 3, with more content.");
    Mockito.when(componentInfo.getTraceabilityNotes()).thenReturn(traceabilityNotes);

    // Create an instance of ComponentInfoInventoryProcessor
    ComponentInfoInventoryProcessor processor = new ComponentInfoInventoryProcessor();

    // Call the formatTraceabilityNotes method with the mocked ComponentInfo
    String formattedNotes = processor.formatTraceabilityNotes(componentInfo);

    // Define the expected formatted notes string with the long separator
    String expectedFormattedNotes = "This is note 1" + System.lineSeparator() + "Note 2 ." + System.lineSeparator()
        + "And here's note 3, with more content." + System.lineSeparator() + "----------" + System.lineSeparator();

    // Assert that the formatted notes match the expected formatted notes
    Assertions.assertEquals(expectedFormattedNotes, formattedNotes);
  }

  /**
   * Test the {@link ComponentInfoInventoryProcessor#formatTraceabilityNotes(ComponentInfo)} method with a mock
   * ComponentInfo containing an empty list of traceability notes.
   */
  @Test
  public void testFormatTraceabilityNotesWithEmptyList() {

    // Create a mock ComponentInfo with an empty list of traceability notes
    ComponentInfo componentInfo = Mockito.mock(ComponentInfo.class);
    Collection<String> emptyList = Collections.emptyList();
    Mockito.when(componentInfo.getTraceabilityNotes()).thenReturn(emptyList);

    // Create an instance of ComponentInfoInventoryProcessor
    ComponentInfoInventoryProcessor processor = new ComponentInfoInventoryProcessor();

    // Call the formatTraceabilityNotes method with the mocked ComponentInfo
    String formattedNotes = processor.formatTraceabilityNotes(componentInfo);

    // Assert that the formatted notes are an empty string
    Assertions.assertEquals("", formattedNotes);
  }

  /**
   * Test the {@link ComponentInfoInventoryProcessor#formatTraceabilityNotes(ComponentInfo)} method with a mock
   * ComponentInfo containing null for traceability notes.
   */
  @Test
  public void testFormatTraceabilityNotesWithNull() {

    // Create a mock ComponentInfo with an empty list of traceability notes
    ComponentInfo componentInfo = Mockito.mock(ComponentInfo.class);
    Mockito.when(componentInfo.getTraceabilityNotes()).thenReturn(null);

    // Create an instance of ComponentInfoInventoryProcessor
    ComponentInfoInventoryProcessor processor = new ComponentInfoInventoryProcessor();

    // Call the formatTraceabilityNotes method with the mocked ComponentInfo
    String formattedNotes = processor.formatTraceabilityNotes(componentInfo);

    // Assert that the formatted notes are an empty string
    Assertions.assertEquals("", formattedNotes);
  }

  /**
   * Test the {@link ComponentInfoInventoryProcessor#formatTraceabilityNotes(ComponentInfo)} method with a mock
   * ComponentInfo containing a long list of traceability notes.
   */
  @Test
  public void testFormatTraceabilityNotesWithLongNotes() {

    // Create a mock ComponentInfo with notes that go over multiple lines
    ComponentInfo componentInfo = Mockito.mock(ComponentInfo.class);
    Collection<String> traceabilityNotes = Arrays.asList(
        "Note 1 with long content that goes over several lines. "
            + "This is to test the wrapping and formatting of long notes.",
        "Line 2 of Note 1. This is part of the long content that goes over several lines.",
        "Line 3 of Note 1. This is also part of the long content that goes over several lines.", "",
        "Note 2 is another long note that requires " + "wrapping to fit within the allowed display width.", "",
        "Note 3 is equally long and challenging to format."
            + " It tests whether the formatting logic handles such cases properly.");
    Mockito.when(componentInfo.getTraceabilityNotes()).thenReturn(traceabilityNotes);

    // Create an instance of ComponentInfoInventoryProcessor
    ComponentInfoInventoryProcessor processor = new ComponentInfoInventoryProcessor();

    // Call the formatTraceabilityNotes method with the mocked ComponentInfo
    String formattedNotes = processor.formatTraceabilityNotes(componentInfo);

    // Define the expected formatted notes string with the long separator using System.lineSeparator()
    String expectedFormattedNotes = "Note 1 with long content that goes over several lines. This is to test the wrapping"
        + " and formatting of long notes." + System.lineSeparator()
        + "Line 2 of Note 1. This is part of the long content that goes over several lines." + System.lineSeparator()
        + "Line 3 of Note 1. This is also part of the long content that goes over several lines."
        + System.lineSeparator() + System.lineSeparator()
        + "Note 2 is another long note that requires wrapping to fit within the allowed display width."
        + System.lineSeparator() + System.lineSeparator() + "Note 3 is equally long and challenging to format. It tests"
        + " whether the formatting logic handles such cases properly." + System.lineSeparator() + "----------"
        + System.lineSeparator();

    // Assert that the formatted notes match the expected formatted notes
    Assertions.assertEquals(expectedFormattedNotes, formattedNotes);
  }

  /**
   * Test the {@link ComponentInfoInventoryProcessor#formatTraceabilityNotes(ComponentInfo)} method with a mock
   * ComponentInfo containing notes that go over several lines.
   */
  @Test
  public void testFormatTraceabilityNotesWithMultilineNotes() {

    // Create a mock ComponentInfo with multiline traceability notes
    ComponentInfo componentInfo = Mockito.mock(ComponentInfo.class);
    Collection<String> traceabilityNotes = Arrays.asList("Retrieving licenses from Scancode result file",
        "Scancode Version: 30.1.0", "", "Found NOTICE file: NOTICE.md", "", "Apply curation on Copyright information");
    Mockito.when(componentInfo.getTraceabilityNotes()).thenReturn(traceabilityNotes);

    // Create an instance of ComponentInfoInventoryProcessor
    ComponentInfoInventoryProcessor processor = new ComponentInfoInventoryProcessor();

    // Call the formatTraceabilityNotes method with the mocked ComponentInfo
    String formattedNotes = processor.formatTraceabilityNotes(componentInfo);

    // Define the expected formatted notes string with the long separator using System.lineSeparator()
    String expectedFormattedNotes = "Retrieving licenses from Scancode result file" + System.lineSeparator()
        + "Scancode Version: 30.1.0" + System.lineSeparator() + System.lineSeparator() + "Found NOTICE file: NOTICE.md"
        + System.lineSeparator() + System.lineSeparator() + "Apply curation on Copyright information"
        + System.lineSeparator() + "----------" + System.lineSeparator();

    // Assert that the formatted notes match the expected formatted notes
    Assertions.assertEquals(expectedFormattedNotes, formattedNotes);
  }
}
