package com.devonfw.tools.solicitor;

import org.springframework.core.annotation.Order;

import com.devonfw.tools.solicitor.model.ModelRoot;
import com.devonfw.tools.solicitor.model.inventory.ApplicationComponent;
import com.devonfw.tools.solicitor.model.inventory.RawLicense;
import com.devonfw.tools.solicitor.model.masterdata.Application;
import com.devonfw.tools.solicitor.model.masterdata.Engagement;
import com.devonfw.tools.solicitor.reader.Reader;
import com.devonfw.tools.solicitor.ruleengine.RuleEngine;
import com.devonfw.tools.solicitor.writer.Writer;

/**
 * An {@link InventoryProcessor} might perform changes on the data model. {@link InventoryProcessor}s are called after
 * reading the raw model data {@link Engagement}, {@link Application}s, {@link ApplicationComponent}s,
 * {@link RawLicense}s with the {@link Reader}s and before doing the reporting via the {@link Writer}s.
 *
 * The {@link RuleEngine} is the most important {@link InventoryProcessor}. It executes the central Drools rule engine.
 *
 * Execution order of {@link InventoryProcessor}s is controlled by an {@link Order} annotation.
 */
public interface InventoryProcessor {

  /**
   * Processing order: After the readers have read the raw data. Use in {@link Order} annotation of the implementing
   * spring bean.
   */
  public static final int AFTER_READERS = 50;

  /**
   * Processing order: Execute before rule engine. Use in {@link Order} annotation of the implementing spring bean.
   */
  public static final int BEFORE_RULE_ENGINE = 100;

  /**
   * Processing order to be used for the rule engine. Use in {@link Order} annotation of the implementing spring bean.
   */
  public static final int RULE_ENGINE = 200;

  /**
   * Processing order: Execute after rule engine. Use in {@link Order} annotation of the implementing spring bean.
   */
  public static final int AFTER_RULE_ENGINE = 300;

  /**
   * Processes the data model.
   *
   * @param modelRoot the root object of the data model
   */
  public void processInventory(ModelRoot modelRoot);

}
