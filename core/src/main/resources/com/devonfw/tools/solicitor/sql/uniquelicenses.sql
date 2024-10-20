-- SPDX-License-Identifier: Apache-2.0
--
-- returns all distinct OSS-Licenses texts
select distinct 
	l."effectiveNormalizedLicense",
	GROUP_CONCAT(DISTINCT l."effectiveNormalizedLicenseUrl" ORDER BY "effectiveNormalizedLicenseUrl" DESC SEPARATOR ', ') as "effectiveNormalizedLicenseUrl", 
	ARRAY_AGG(DISTINCT l."effectiveNormalizedLicenseContent" ORDER BY "effectiveNormalizedLicenseContent" DESC)[1] as "effectiveNormalizedLicenseContent", 
	UCASE(REGEXP_REPLACE(l."effectiveNormalizedLicenseContent",'\s','')) as "unifiedEffectiveNormalizedLicenseContent"
from 
	APPLICATION a,
	APPLICATIONCOMPONENT ac,
	NORMALIZEDLICENSE l
where
	a."reportingGroups" LIKE '%#reportingGroup#%' AND 
	a.ID_APPLICATION = ac.PARENT_APPLICATIONCOMPONENT AND
	ac.ID_APPLICATIONCOMPONENT = l.PARENT_NORMALIZEDLICENSE AND 
	l."effectiveNormalizedLicenseType" like 'OSS-%'
group by 
	"unifiedEffectiveNormalizedLicenseContent",
	"effectiveNormalizedLicense"
order by 
	"effectiveNormalizedLicense",
	"effectiveNormalizedLicenseUrl"
