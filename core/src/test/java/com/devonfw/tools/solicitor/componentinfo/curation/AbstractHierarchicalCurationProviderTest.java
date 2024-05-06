package com.devonfw.tools.solicitor.componentinfo.curation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.devonfw.tools.solicitor.common.packageurl.AllKindsPackageURLHandler;
import com.devonfw.tools.solicitor.componentinfo.ComponentInfoAdapterException;
import com.devonfw.tools.solicitor.componentinfo.SelectorCurationDataHandle;
import com.devonfw.tools.solicitor.componentinfo.curation.model.ComponentInfoCuration;
import com.devonfw.tools.solicitor.componentinfo.curation.model.CopyrightCuration;
import com.devonfw.tools.solicitor.componentinfo.curation.model.CurationOperation;

/**
 * Tests for {@link AbstractHierarchicalCurationProvider}.
 *
 */
class AbstractHierarchicalCurationProviderTest {

  @Test
  void testFindCurationsHierarchyOnDefaultCurationDataSelector()
      throws ComponentInfoAdapterNonExistingCurationDataSelectorException, ComponentInfoAdapterException,
      CurationInvalidException {

    AbstractHierarchicalCurationProvider provider = createObjectUnderTest(true);

    ComponentInfoCuration result = provider.findCurations("pkg:/maven/ch.qos.logback/logback-classic@1.2.3",
        new SelectorCurationDataHandle(null));
    assertEquals("pkg/maven/ch/qos/logback/logback-classic/1.2.3", result.getName());
    assertEquals(7, result.getCopyrightCurations().size());
    assertTrue(result.getCopyrightCurations().get(0).getNewCopyright().contains("theDefault"));
    assertTrue(result.getCopyrightCurations().get(0).getNewCopyright()
        .endsWith("pkg/maven/ch/qos/logback/logback-classic/1.2.3"));
    assertTrue(result.getCopyrightCurations().get(6).getNewCopyright().endsWith("pkg"));

  }

  @Test
  void testFindCurationsHierarchyOnNonDefaultCurationDataSelector()
      throws ComponentInfoAdapterNonExistingCurationDataSelectorException, ComponentInfoAdapterException,
      CurationInvalidException {

    AbstractHierarchicalCurationProvider provider = createObjectUnderTest(true);

    ComponentInfoCuration result = provider.findCurations("pkg:/maven/ch.qos.logback/logback-classic@1.2.3",
        new SelectorCurationDataHandle("someSelector"));
    assertEquals("pkg/maven/ch/qos/logback/logback-classic/1.2.3", result.getName());
    assertEquals(7, result.getCopyrightCurations().size());
    assertTrue(result.getCopyrightCurations().get(0).getNewCopyright().contains("someSelector"));
    assertTrue(result.getCopyrightCurations().get(0).getNewCopyright()
        .endsWith("pkg/maven/ch/qos/logback/logback-classic/1.2.3"));
    assertTrue(result.getCopyrightCurations().get(6).getNewCopyright().endsWith("pkg"));

  }

  @Test
  void testFindCurationsHierarchyWhenCurationExistOnlyForVersion()
      throws ComponentInfoAdapterNonExistingCurationDataSelectorException, ComponentInfoAdapterException,
      CurationInvalidException {

    AbstractHierarchicalCurationProvider provider = createObjectUnderTest(true);

    ComponentInfoCuration result = provider.findCurations("pkg:/maven/ch.qos.logback/logback-classic@1.2.3",
        new SelectorCurationDataHandle("curationonlyonversion"));
    assertEquals("pkg/maven/ch/qos/logback/logback-classic/1.2.3", result.getName());
    assertEquals(1, result.getCopyrightCurations().size());
    assertTrue(result.getCopyrightCurations().get(0).getNewCopyright().contains("curationonlyonversion"));
    assertTrue(result.getCopyrightCurations().get(0).getNewCopyright()
        .endsWith("pkg/maven/ch/qos/logback/logback-classic/1.2.3"));
  }

  @Test
  void testFindCurationsHierarchyDisabled() throws ComponentInfoAdapterNonExistingCurationDataSelectorException,
      ComponentInfoAdapterException, CurationInvalidException {

    AbstractHierarchicalCurationProvider provider = createObjectUnderTest(false);

    ComponentInfoCuration result = provider.findCurations("pkg:/maven/ch.qos.logback/logback-classic@1.2.3",
        new SelectorCurationDataHandle(null));
    assertEquals("pkg/maven/ch/qos/logback/logback-classic/1.2.3", result.getName());
    assertEquals(1, result.getCopyrightCurations().size());
    assertTrue(result.getCopyrightCurations().get(0).getNewCopyright().contains("theDefault"));
    assertTrue(result.getCopyrightCurations().get(0).getNewCopyright()
        .endsWith("pkg/maven/ch/qos/logback/logback-classic/1.2.3"));
  }

  @Test
  void testFindCurationsInvalidSelector() throws ComponentInfoAdapterNonExistingCurationDataSelectorException,
      ComponentInfoAdapterException, CurationInvalidException {

    AbstractHierarchicalCurationProvider provider = createObjectUnderTest(true);

    ComponentInfoAdapterNonExistingCurationDataSelectorException e = assertThrows(
        ComponentInfoAdapterNonExistingCurationDataSelectorException.class,
        () -> provider.findCurations("pkg:/maven/ch.qos.logback/logback-classic@1.2.3",
            new SelectorCurationDataHandle("invalid")));
    assertEquals("test1", e.getMessage());
  }

  @Test
  void testFindCurationsNonexistentSelector() throws ComponentInfoAdapterNonExistingCurationDataSelectorException,
      ComponentInfoAdapterException, CurationInvalidException {

    AbstractHierarchicalCurationProvider provider = createObjectUnderTest(true);

    ComponentInfoAdapterNonExistingCurationDataSelectorException e = assertThrows(
        ComponentInfoAdapterNonExistingCurationDataSelectorException.class,
        () -> provider.findCurations("pkg:/maven/ch.qos.logback/logback-classic@1.2.3",
            new SelectorCurationDataHandle("nonexistent")));
    assertEquals("test2", e.getMessage());
  }

  /**
   * Creates a instance of the {@link AbstractHierarchicalCurationProvider}. Abstract methods will be implemented to
   * allow for testing.
   *
   * @return the object to be tested.
   */
  private AbstractHierarchicalCurationProvider createObjectUnderTest(boolean evaluateHierarchy) {

    AllKindsPackageURLHandler packageUrlHandler = Mockito.mock(AllKindsPackageURLHandler.class);
    Mockito.when(packageUrlHandler.pathFor("pkg:/maven/ch.qos.logback/logback-classic@1.2.3"))
        .thenReturn("pkg/maven/ch/qos/logback/logback-classic/1.2.3");

    AbstractHierarchicalCurationProvider provider = new AbstractHierarchicalCurationProvider(packageUrlHandler) {

      @Override
      protected void validateEffectiveCurationDataSelector(String effectiveCurationDataSelector)
          throws ComponentInfoAdapterNonExistingCurationDataSelectorException {

        if (effectiveCurationDataSelector.equals("invalid")) {
          throw new ComponentInfoAdapterNonExistingCurationDataSelectorException("test1");
        }

      }

      @Override
      protected boolean isHierarchyEvaluation() {

        return evaluateHierarchy;
      }

      @Override
      protected ComponentInfoCuration fetchCurationFromRepository(String effectiveCurationDataSelector,
          String pathFragmentWithinRepo) throws ComponentInfoAdapterException, CurationInvalidException {

        if (effectiveCurationDataSelector.equals("nonexistent") || effectiveCurationDataSelector.equals("empty")) {
          return null;
        }
        if (effectiveCurationDataSelector.equals("curationonlyonversion")
            && !pathFragmentWithinRepo.equals("pkg/maven/ch/qos/logback/logback-classic/1.2.3")) {
          return null;
        }
        ComponentInfoCuration cic = new ComponentInfoCuration();
        cic.setName(pathFragmentWithinRepo);
        CopyrightCuration cc = new CopyrightCuration();
        cc.setOperation(CurationOperation.ADD);
        cc.setNewCopyright("Copyright " + effectiveCurationDataSelector + " " + pathFragmentWithinRepo);
        cic.setCopyrightCurations(List.of(cc));
        return cic;
      }

      @Override
      protected String determineEffectiveCurationDataSelector(String curationDataSelector) {

        if (curationDataSelector == null) {
          return "theDefault";
        } else {
          return curationDataSelector;
        }
      }

      @Override
      protected void assureCurationDataSelectorAvailable(String effectiveCurationDataSelector)
          throws ComponentInfoAdapterException, ComponentInfoAdapterNonExistingCurationDataSelectorException {

        if (effectiveCurationDataSelector.equals("nonexistent")) {
          throw new ComponentInfoAdapterNonExistingCurationDataSelectorException("test2");
        }

      }
    };
    return provider;
  }

}
