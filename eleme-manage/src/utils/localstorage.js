// 存储localStorage

const LocalStorage = {
	setLocal : (key, value) => {
		localStorage.setItem(key, JSON.stringify(value));
	},
	getLocal : (key) => {
		if(localStorage.getItem(key)===undefined || localStorage.getItem(key)===null ) return null
		try {
			return JSON.parse(localStorage.getItem(key))
		} catch (error) {
			return null
		}
		
	},
	delLocal : (key) => {
		localStorage.removeItem(key);
	},
	clearLocal : () => {
		localStorage.clear();
	}
}

 export default LocalStorage;