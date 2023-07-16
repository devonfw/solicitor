package com.devonfw.tools.solicitor.model.impl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devonfw.tools.solicitor.model.impl.inventory.ApplicationComponentImpl;

/**
 * This class contains JUnit test methods for the {@link ApplicationComponentImpl} class.
 */
class ApplicationComponentImplTest {
  private ApplicationComponentImpl component;

  /**
   * Initializes the {@code component} object before each test.
   */
  @BeforeEach
  public void setUp() {
    this.component = new ApplicationComponentImpl();
  }

  /**
   * Tests the {@link ApplicationComponentImpl#setDataStatus(String)} and {@link ApplicationComponentImpl#getDataStatus()} methods.
   */
  @Test
  public void testDataStatus() {
    // Set dataStatus
    String dataStatus = "VALID";
    this.component.setDataStatus(dataStatus);

    // Get dataStatus and assert
    String retrievedDataStatus = this.component.getDataStatus();
    Assertions.assertEquals(dataStatus, retrievedDataStatus);
  }

  /**
   * Tests the {@link ApplicationComponentImpl#setTraceabilityNotes(String)} and {@link ApplicationComponentImpl#getTraceabilityNotes()} methods.
   */
  @Test
  public void testTraceabilityNotes() {
    // Set traceabilityNotes
    String traceabilityNotes = "Note 1, Note 2, Note 3";
    this.component.setTraceabilityNotes(traceabilityNotes);

    // Get traceabilityNotes and assert
    String retrievedTraceabilityNotes = this.component.getTraceabilityNotes();
    Assertions.assertEquals(traceabilityNotes, retrievedTraceabilityNotes);
  }
}