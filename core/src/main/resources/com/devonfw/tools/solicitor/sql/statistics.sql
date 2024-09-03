-- Selects the category "legal-evaluation" and counts occurrences of each value in the 'legalApproved' column.
SELECT 'legal-evaluation' AS "category", "legalApproved" AS "value", COUNT(*) AS "count"
FROM "NormalizedLicense"
GROUP BY "legalApproved"

UNION ALL -- Combines the results of this query with the next, including duplicates

-- Selects the category "data-status" and counts occurrences of each value in the 'dataStatus' column.
SELECT 'data-status' AS "category", "dataStatus" AS "value", COUNT(*) AS "count"
FROM "ApplicationComponent"
GROUP BY "dataStatus";
