package com.devonfw.tools.solicitor.componentinfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    List<String> traceabilityNotes = Arrays.asList("Note 1", "Note 2", "Note 3");
    Mockito.when(componentInfo.getTraceabilityNotes()).thenReturn(traceabilityNotes);

    // Create an instance of ComponentInfoInventoryProcessor
    ComponentInfoInventoryProcessor processor = new ComponentInfoInventoryProcessor();

    // Call the formatTraceabilityNotes method with the mocked ComponentInfo
    String formattedNotes = processor.formatTraceabilityNotes(componentInfo);

    // Define the expected formatted notes string with the long separator
    String expectedFormattedNotes = "Note 1" + System.lineSeparator() + "Note 2" + System.lineSeparator() + "Note 3";

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
    List<String> emptyList = Collections.emptyList();
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

}
