-- SPDX-License-Identifier: Apache-2.0
--
-- generate all Engagments in denormalized form including all hierachical data (actually trivial ...(allden -> "all denormalized")
select 
	e."engagementName" as CORR_KEY_0,
	e.*
from ENGAGEMENT e