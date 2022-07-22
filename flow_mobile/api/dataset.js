import { httpService } from '@/common/request.js'

export function getSqlDictList (sqlName,params) {
	return httpService(
		`/database/datamodel/dataSet/getDataByName/${sqlName}/json`,
		'GET',
		params
	)
}
