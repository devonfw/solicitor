-- SPDX-License-Identifier: Apache-2.0
--
-- returns all distinct _guessed_ OSS-Licenses texts
select distinct 
	l."effectiveNormalizedLicense",
	GROUP_CONCAT(DISTINCT l."guessedLicenseUrl" ORDER BY "guessedLicenseUrl" DESC SEPARATOR ', ') as "guessedLicenseUrl", 
	ARRAY_AGG(DISTINCT l."guessedLicenseContent" ORDER BY "guessedLicenseContent" DESC)[1] as "guessedLicenseContent", 
	UCASE(REGEXP_REPLACE(l."guessedLicenseContent",'\s','')) as "unifiedGuessedLicenseContent"
from 
	NORMALIZEDLICENSE l
where
	l."effectiveNormalizedLicenseType" like 'OSS-%'
group by 
	"unifiedGuessedLicenseContent",
	"effectiveNormalizedLicense"
order by 
	"effectiveNormalizedLicense",
	"guessedLicenseUrl"
