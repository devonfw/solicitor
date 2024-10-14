-- SPDX-License-Identifier: Apache-2.0
--
-- returns all distinct notice file texts
select distinct 
	GROUP_CONCAT(DISTINCT CONCAT( ac."artifactId", ' (', ac."version", ')' ) ORDER BY "artifactId" ASC, "version" ASC SEPARATOR ', ') as "artifact",
	GROUP_CONCAT(DISTINCT ac."packageUrl" ORDER BY "packageUrl" ASC SEPARATOR ', ') as "packageUrl",
	ARRAY_AGG(DISTINCT ac."noticeFileContent" ORDER BY "noticeFileContent" DESC)[1] as "noticeFileContent", 
	UCASE(REGEXP_REPLACE(ac."noticeFileContent",'\s','')) as "unifiedNoticeFileContent"
from
	APPLICATION a, 
	APPLICATIONCOMPONENT ac,
	NORMALIZEDLICENSE l
where
	a."reportingGroups" LIKE '%#reportingGroup#%' AND 
	a.ID_APPLICATION = ac.PARENT_APPLICATIONCOMPONENT AND
	ac.ID_APPLICATIONCOMPONENT = l.PARENT_NORMALIZEDLICENSE AND 
	ac."noticeFileUrl" is not null AND
	l."normalizedLicenseType" NOT in ('COMMERCIAL', 'IGNORE')
group by 
	"unifiedNoticeFileContent"
order by 
	"unifiedNoticeFileContent"
