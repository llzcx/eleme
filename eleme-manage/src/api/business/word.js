import request from '@/utils/request'

export function getWordList() {
	return request({
		url: '/es/list',
		method: 'get',
	})
}

export function deleteWord(word) {
	return request({
		url: '/es/'+word,
		method: 'delete',
	},)
}

export function addWord(word) {
	return request({
		url: '/es/'+word,
		method: 'post',
	})
}

