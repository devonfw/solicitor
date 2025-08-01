----------- category "legal evaluation" -----------------

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

SELECT TRIM('legal-evaluation') AS category, TRIM('total') AS value, COUNT(*) AS count
FROM
  APPLICATION a,
  APPLICATIONCOMPONENT ac,
  NORMALIZEDLICENSE l
where
  a."reportingGroups" LIKE '%#reportingGroup#%' AND
  a.ID_APPLICATION = ac.PARENT_APPLICATIONCOMPONENT AND
  ac.ID_APPLICATIONCOMPONENT = l.PARENT_NORMALIZEDLICENSE

----------- category "effective-normalized-license-type" -----------------

UNION ALL -- Combines the results of this query with the next, including duplicates

SELECT TRIM('effective-normalized-license-type') AS category, NVL(l."effectiveNormalizedLicenseType", 'blank') AS value, COUNT(*) AS count
FROM
  APPLICATION a,
  APPLICATIONCOMPONENT ac,
  NORMALIZEDLICENSE l
where
  a."reportingGroups" LIKE '%#reportingGroup#%' AND
  a.ID_APPLICATION = ac.PARENT_APPLICATIONCOMPONENT AND
  ac.ID_APPLICATIONCOMPONENT = l.PARENT_NORMALIZEDLICENSE
GROUP BY NVL(l."effectiveNormalizedLicenseType", 'blank')

UNION ALL -- Combines the results of this query with the next, including duplicates

SELECT TRIM('effective-normalized-license-type') AS category, TRIM( 'total') AS value, COUNT(*) AS count
FROM
  APPLICATION a,
  APPLICATIONCOMPONENT ac,
  NORMALIZEDLICENSE l
where
  a."reportingGroups" LIKE '%#reportingGroup#%' AND
  a.ID_APPLICATION = ac.PARENT_APPLICATIONCOMPONENT AND
  ac.ID_APPLICATIONCOMPONENT = l.PARENT_NORMALIZEDLICENSE

----------- category "license-compliance" -----------------

UNION ALL -- Combines the results of this query with the next, including duplicates

SELECT TRIM('license-compliance') AS category, NVL(l."licenseCompliance", 'blank') AS value, COUNT(*) AS count
FROM
  APPLICATION a,
  APPLICATIONCOMPONENT ac,
  NORMALIZEDLICENSE l
where
  a."reportingGroups" LIKE '%#reportingGroup#%' AND
  a.ID_APPLICATION = ac.PARENT_APPLICATIONCOMPONENT AND
  ac.ID_APPLICATIONCOMPONENT = l.PARENT_NORMALIZEDLICENSE
GROUP BY NVL(l."licenseCompliance", 'blank')

UNION ALL -- Combines the results of this query with the next, including duplicates

SELECT TRIM('license-compliance') AS category, TRIM( 'total') AS value, COUNT(*) AS count
FROM
  APPLICATION a,
  APPLICATIONCOMPONENT ac,
  NORMALIZEDLICENSE l
where
  a."reportingGroups" LIKE '%#reportingGroup#%' AND
  a.ID_APPLICATION = ac.PARENT_APPLICATIONCOMPONENT AND
  ac.ID_APPLICATIONCOMPONENT = l.PARENT_NORMALIZEDLICENSE

----------- category "data-status" -----------------

UNION ALL -- Combines the results of this query with the next, including duplicates

SELECT TRIM('data-status') AS category, NVL(ac."dataStatus", 'blank') AS value, COUNT(*) AS count
FROM
	APPLICATION a,
    APPLICATIONCOMPONENT ac
where
	a."reportingGroups" LIKE '%#reportingGroup#%' AND
	a.ID_APPLICATION = ac.PARENT_APPLICATIONCOMPONENT
GROUP BY NVL(ac."dataStatus", 'blank')

UNION ALL -- Combines the results of this query with the next, including duplicates

SELECT TRIM('data-status') AS category, TRIM('total') AS value, COUNT(*) AS count
FROM
  APPLICATION a,
    APPLICATIONCOMPONENT ac
where
  a."reportingGroups" LIKE '%#reportingGroup#%' AND
  a.ID_APPLICATION = ac.PARENT_APPLICATIONCOMPONENT

