package com.devonfw.tools.solicitor.model.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.devonfw.tools.solicitor.model.impl.inventory.ApplicationComponentImpl;

/**
 * This class contains a test method for the {@link ApplicationComponentImpl} class, testing both the dataStatus and traceabilityNotes properties.
 */
class ApplicationComponentImplTestTwo {

  /**
   * Tests the dataStatus and traceabilityNotes properties of the {@link ApplicationComponentImpl} class.
   */
  @Test
  void testDataStatusAndTraceabilityNotes() {

    // Create a test instance of ApplicationComponentImpl
    ApplicationComponentImpl component = new ApplicationComponentImpl();

    // Set test data for dataStatus and traceabilityNotes
    String dataStatus = "CURATED";
    String traceabilityNotes = "Traceability note 1, Traceability note 2";

    // Set the dataStatus and traceabilityNotes using the setter methods
    component.setDataStatus(dataStatus);
    component.setTraceabilityNotes(traceabilityNotes);

    // Verify that the getter methods return the expected values
    Assertions.assertEquals(dataStatus, component.getDataStatus());
    Assertions.assertEquals(traceabilityNotes, component.getTraceabilityNotes());
  }
}