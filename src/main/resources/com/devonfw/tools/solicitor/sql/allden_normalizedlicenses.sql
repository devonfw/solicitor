-- SPDX-License-Identifier: Apache-2.0
--
-- generate all NormalizedLicenses in denormalized form including all hierachical data  (allden -> "all denormalized")
select
    CONCAT(a."applicationName",ac."groupId",ac."artifactId",ac."version",l."normalizedLicense") as CORR_KEY_0,
    CONCAT(a."applicationName",ac."groupId",ac."artifactId",l."normalizedLicense") as CORR_KEY_1,
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
	a.ID_APPLICATION = ac.PARENT_APPLICATIONCOMPONENT AND 
	ac.ID_APPLICATIONCOMPONENT = l.PARENT_NORMALIZEDLICENSE
order by
	UPPER("ID_APPLICATION"), -- sort by ID so assuring we have the same order as defined in config
	UPPER("groupId"), 
	UPPER("artifactId"),
	UPPER("version"),
	UPPER("effectiveNormalizedLicense")