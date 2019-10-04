/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.common;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A builder for creating and audit/trace entry.
 *
 */
public class AuditEntryBuilder implements Cloneable {

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

    private String ruleName;

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

        StringBuilder sb = new StringBuilder();
        sb.append("+ Rule: ").append(ruleName);
        sb.append("; Matching: ");
        List<String> stringList = new ArrayList<>();
        if (matchings.size() > 0) {
            for (Entry<String, String> e : matchings.entrySet()) {
                stringList.add(e.getKey() + "==" + e.getValue());
            }
            sb.append(String.join(", ", stringList));
        } else {
            sb.append("-default-");
        }
        sb.append("; Setting: ");
        stringList.clear();
        for (Entry<String, StringPair> e : settings.entrySet()) {
            String theEntry = e.getKey() + "=" + e.getValue().string1;
            String string2 = e.getValue().string2;
            if (string2 != null) {
                theEntry = theEntry + " (" + string2 + ")";
            }
            stringList.add(theEntry);
        }
        sb.append(String.join(", ", stringList));
        return sb.toString();
    }

    /** {@inheritDoc} */
    @Override
    public AuditEntryBuilder clone() {

        AuditEntryBuilder clone = new AuditEntryBuilder();
        clone.matchings = new LinkedHashMap<>(matchings);
        clone.settings = new LinkedHashMap<>(settings);
        clone.ruleName = ruleName;
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
     * Add a rule name.
     *
     * @param name the rule name to use
     * @return the same builder to allow chaining
     */
    public AuditEntryBuilder withRuleName(String name) {

        ruleName = name;
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
