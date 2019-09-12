/**
 * SPDX-License-Identifier: Apache-2.0
 */

package com.devonfw.tools.solicitor.common;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class AuditEntryBuilder implements Cloneable {

    private static class StringDouble {
        public StringDouble(String string1, String string2) {

            super();
            this.string1 = string1;
            this.string2 = string2;
        }

        public String string1;

        public String string2;
    }

    private Map<String, String> matchings;

    private Map<String, StringDouble> settings;

    private String ruleName;

    public static AuditEntryBuilder instance() {

        return new AuditEntryBuilder();
    }

    public AuditEntryBuilder() {

        matchings = new LinkedHashMap<>();
        settings = new LinkedHashMap<>();
    }

    @Override
    public AuditEntryBuilder clone() {

        AuditEntryBuilder clone = new AuditEntryBuilder();
        clone.matchings = new LinkedHashMap<>(matchings);
        clone.settings = new LinkedHashMap<>(settings);
        clone.ruleName = ruleName;
        return clone;
    }

    public AuditEntryBuilder withRuleName(String name) {

        ruleName = name;
        return this;
    }

    public AuditEntryBuilder withMatching(String fieldName, String value) {

        matchings.put(fieldName, value);
        return this;
    }

    public AuditEntryBuilder withSetting(String fieldName, String value,
            String comment) {

        // TODO: handle comment
        settings.put(fieldName, new StringDouble(value, comment));
        return this;
    }

    public AuditEntryBuilder withSetting(String fieldName, String value) {

        settings.put(fieldName, new StringDouble(value, null));
        return this;
    }

    public AuditEntryBuilder nop() {

        return this;
    }

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
        for (Entry<String, StringDouble> e : settings.entrySet()) {
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

}
