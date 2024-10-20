-- Selects the category "legal-evaluation" and trims any whitespace
SELECT TRIM('legal-evaluation') AS category, NVL(l."legalApproved", 'blank') AS value, COUNT(*) AS count
FROM 
	APPLICATION a,
	APPLICATIONCOMPONENT ac,
	NORMALIZEDLICENSE l
where
	a."reportingGroups" LIKE '%#reportingGroup#%' AND 
	a.ID_APPLICATION = ac.PARENT_APPLICATIONCOMPONENT AND
	ac.ID_APPLICATIONCOMPONENT = l.PARENT_NORMALIZEDLICENSE
GROUP BY NVL(l."legalApproved", 'blank')

UNION ALL -- Combines the results of this query with the next, including duplicates

-- Selects the category "data-status" and trims any whitespace
SELECT TRIM('data-status') AS category, ac."dataStatus" AS value, COUNT(*) AS count
FROM
	APPLICATION a,
    APPLICATIONCOMPONENT ac
where
	a."reportingGroups" LIKE '%#reportingGroup#%' AND 
	a.ID_APPLICATION = ac.PARENT_APPLICATIONCOMPONENT
GROUP BY ac."dataStatus";
