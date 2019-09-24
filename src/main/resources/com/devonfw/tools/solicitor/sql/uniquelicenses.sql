-- SPDX-License-Identifier: Apache-2.0
--
-- returns all distinct OSS-Licenses
select distinct 
	l."effectiveNormalizedLicense",
	l."effectiveNormalizedLicenseUrl", 
	l."effectiveNormalizedLicenseContent" 
from 
	NORMALIZEDLICENSE l
where
	l."effectiveNormalizedLicenseType" like 'OSS-%'
order by 
	"effectiveNormalizedLicense",
	"effectiveNormalizedLicenseUrl"
