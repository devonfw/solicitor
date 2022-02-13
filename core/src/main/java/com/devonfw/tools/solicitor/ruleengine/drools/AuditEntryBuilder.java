/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.ruleengine.drools;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devonfw.tools.solicitor.common.LogMessages;

/**
 * A builder for creating and audit/trace entry.
 *
 */
public class AuditEntryBuilder implements Cloneable {

  private static final Logger LOG = LoggerFactory.getLogger(AuditEntryBuilder.class);

  /**
   * A pair of Strings.
   */
  private static class StringPair {
    public String string1;

    public String string2;

    public StringPair(String string1, String string2) {

      super();
      this.string1 = string1;
      this.string2 = string2;
    }
  }

  /**
   * Creates a new instance.
   *
   * @return a builder instance
   */
  public static AuditEntryBuilder instance() {

    return new AuditEntryBuilder();
  }

  private Map<String, String> matchings;

  private Map<String, StringPair> settings;

  private String ruleId;

  /**
   * Private Constructor. Use {@link #instance()} to get an instance instead.
   */
  private AuditEntryBuilder() {

    matchings = new LinkedHashMap<>();
    settings = new LinkedHashMap<>();
  }

  /**
   * Builds the audit entry string from the data contained in the builder.
   *
   * @return The audit entry string
   */
  public String build() {

    StringBuilder detailSb = new StringBuilder();
    detailSb.append("Matching: ");
    List<String> stringList = new ArrayList<>();
    if (matchings.size() > 0) {
      for (Entry<String, String> e : matchings.entrySet()) {
        stringList.add(e.getKey() + "==" + e.getValue());
      }
      detailSb.append(String.join(", ", stringList));
    } else {
      detailSb.append("-default-");
    }
    detailSb.append("; Setting: ");
    stringList.clear();
    for (Entry<String, StringPair> e : settings.entrySet()) {
      String theEntry = e.getKey() + "=" + e.getValue().string1;
      String string2 = e.getValue().string2;
      if (string2 != null) {
        theEntry = theEntry + " (" + string2 + ")";
      }
      stringList.add(theEntry);
    }
    detailSb.append(String.join(", ", stringList));
    String detailInfo = detailSb.toString();

    if (LOG.isDebugEnabled()) {
      LOG.debug(LogMessages.FIRING_RULE.msg(), ModelHelper.getCurrentRuleGroup(), ruleId, detailInfo);
    }

    StringBuilder sb = new StringBuilder();
    sb.append("+ Rule Group: ").append(ModelHelper.getCurrentRuleGroup()).append("; RuleId: ").append(ruleId)
        .append("; ").append(detailInfo);

    return sb.toString();
  }

  /** {@inheritDoc} */
  @Override
  public AuditEntryBuilder clone() {

    AuditEntryBuilder clone = new AuditEntryBuilder();
    clone.matchings = new LinkedHashMap<>(matchings);
    clone.settings = new LinkedHashMap<>(settings);
    clone.ruleId = ruleId;
    return clone;
  }

  /**
   * Does nothing.
   *
   * @return the same builder to allow chaining
   */
  public AuditEntryBuilder nop() {

    return this;
  }

  /**
   * Add information about a matching field.
   *
   * @param fieldName the name of the matching field
   * @param value the machting value
   * @return the same builder to allow chaining
   */
  public AuditEntryBuilder withMatching(String fieldName, String value) {

    matchings.put(fieldName, value);
    return this;
  }

  /**
   * Add a rule id.
   *
   * @param id the rule id to use
   * @return the same builder to allow chaining
   */
  public AuditEntryBuilder withRuleId(String id) {

    ruleId = id;
    return this;
  }

  /**
   * Add information about a field being set to some value.
   *
   * @param fieldName the name of the field
   * @param value the value to which the field was set
   * @return the same builder to allow chaining
   */
  public AuditEntryBuilder withSetting(String fieldName, String value) {

    settings.put(fieldName, new StringPair(value, null));
    return this;
  }

  /**
   * Add information about a field being set to some value.
   *
   * @param fieldName the name of the field
   * @param value the value to which the field was set
   * @param comment an optional comment (might be <code>null</code>
   * @return the same builder to allow chaining
   */
  public AuditEntryBuilder withSetting(String fieldName, String value, String comment) {

    settings.put(fieldName, new StringPair(value, comment));
    return this;
  }

}
