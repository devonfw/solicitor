-- SPDX-License-Identifier: Apache-2.0
--
-- generate all NormalizedLicenses in denormalized form including all hierachical data  (allden -> "all denormalized")
select
    CONCAT(NVL(a."applicationName",'-'),NVL(ac."groupId",'-'),NVL(ac."artifactId",'-'),NVL(ac."version",'-'),NVL(l."normalizedLicense",'-')) as CORR_KEY_0,
    CONCAT(NVL(a."applicationName",'-'),NVL(ac."groupId",'-'),NVL(ac."artifactId",'-'),NVL(l."normalizedLicense",'-')) as CORR_KEY_1,
    CONCAT(NVL(a."applicationName",'-'),NVL(ac."groupId",'-'),NVL(ac."artifactId",'-'),NVL(ac."version",'-')) as CORR_KEY_2,
    CONCAT(NVL(a."applicationName",'-'),NVL(ac."groupId",'-'),NVL(ac."artifactId",'-')) as CORR_KEY_3,
	e.*,
	a.*,
	ac.*,
	l.*
from 
	ENGAGEMENT e, 
	APPLICATION a,
	APPLICATIONCOMPONENT ac,
	NORMALIZEDLICENSE l
where 
	e.ID_ENGAGEMENT = a.PARENT_APPLICATION AND 
	a."reportingGroups" LIKE '%#reportingGroup#%' AND 
	a.ID_APPLICATION = ac.PARENT_APPLICATIONCOMPONENT AND 
	ac.ID_APPLICATIONCOMPONENT = l.PARENT_NORMALIZEDLICENSE AND
	l."normalizedLicenseType" not in ('COMMERCIAL', 'IGNORE')
order by
	UPPER("ID_APPLICATION"), -- sort by ID so assuring we have the same order as defined in config
	UPPER("groupId"), 
	UPPER("artifactId"),
	UPPER("version"),
	UPPER("effectiveNormalizedLicense"),
	UPPER("normalizedLicense")