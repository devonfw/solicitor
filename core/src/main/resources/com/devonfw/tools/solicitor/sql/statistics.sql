-- Selects the category "legal-evaluation" and trims any whitespace
SELECT TRIM('legal-evaluation') AS category, NVL(l."legalApproved", 'blank') AS value, COUNT(*) AS count
FROM NormalizedLicense l
GROUP BY NVL(l."legalApproved", 'blank')

UNION ALL -- Combines the results of this query with the next, including duplicates

-- Selects the category "data-status" and trims any whitespace
SELECT TRIM('data-status') AS category, ac."dataStatus" AS value, COUNT(*) AS count
FROM ApplicationComponent ac
GROUP BY ac."dataStatus";
