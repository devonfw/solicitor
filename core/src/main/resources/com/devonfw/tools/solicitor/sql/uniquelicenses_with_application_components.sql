-- SPDX-License-Identifier: Apache-2.0
--
-- returns all distinct OSS-Licenses texts
select distinct 
	GROUP_CONCAT(DISTINCT l."normalizedLicense" ORDER BY "normalizedLicense" ASC SEPARATOR ', ') as "normalizedLicense",
	GROUP_CONCAT(DISTINCT CONCAT( ac."artifactId", ' (', ac."version", ')' ) ORDER BY "artifactId" ASC, "version" ASC SEPARATOR ', ') as "artifact",
	GROUP_CONCAT(DISTINCT ac."packageUrl" ORDER BY "packageUrl" ASC SEPARATOR ', ') as "packageUrl",
	ARRAY_AGG(DISTINCT l."normalizedLicenseContent" ORDER BY "normalizedLicenseContent" DESC)[1] as "normalizedLicenseContent", 
	UCASE(REGEXP_REPLACE(l."normalizedLicenseContent",'\s','')) as "unifiedNormalizedLicenseContent"
from
	APPLICATION a,
	APPLICATIONCOMPONENT ac,
	NORMALIZEDLICENSE l
where
	a."reportingGroups" LIKE '%#reportingGroup#%' AND 
	a.ID_APPLICATION = ac.PARENT_APPLICATIONCOMPONENT AND
	ac.ID_APPLICATIONCOMPONENT = l.PARENT_NORMALIZEDLICENSE AND 
	l."normalizedLicenseType" NOT in ('COMMERCIAL', 'IGNORE') AND
	l."normalizedLicenseUrl" is not null
group by 
	"unifiedNormalizedLicenseContent"
order by 
	"unifiedNormalizedLicenseContent"
