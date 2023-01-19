<template>


	<div class="app-container chat-out">
		<el-container>
			<div  style="border: 2px solid rgba(0, 0, 0, 0.2)">
				<el-aside width="200px">
					<div class="chatObject-container">
						<div v-if="chatObject.length">
							<div v-for="(item, index) in chatObject" class="chat-item" @click="pullData(item)">
								<div class="chat-item-left">
									<img :src="item.img">
								</div>
								<div class="chat-item-right">
									<span>{{ item.name }}</span>
								</div>
							</div>
						</div>
						<div v-else>
							暂时消息记录
						</div>
					</div>
				</el-aside>
			</div>
			<el-container>
				<el-header height="50px">
					<el-form ref="print" label-width="150px" v-if="form != null">
						<el-row>
							<h3>基本信息</h3>
							<el-col :span="12">
								<el-form-item label="用户Id">
									<span v-if="form.id"> {{ form.id }}</span>
								</el-form-item>
							</el-col>
						</el-row>
					</el-form>
				</el-header>
				<el-main>
					<div class="chat-main" v-if="form != null">
						<!-- 聊天内容 -->
						<div v-if="loveArray.length" class="mt-10">
							<ul>
								<li v-if="loveArray.length" v-for="(item, index) in loveArray" :key="index"
									:class="'text-' + item.class" :style="{ '--url': item.url }">
									<span>{{ item.text }}</span>
								</li>
							</ul>
						</div>
						<div v-else>
							暂无聊天记录
						</div>
					</div>
					<div v-else>
						点击左侧用户栏
					</div>
					<div v-if="form != null" class="chat-input-container">
						<el-row :gutter="20">
							<el-col :span="17">
								<el-input placeholder="请输入内容" v-model="inputValue" clearable>
								</el-input>
							</el-col>
							<el-col :span="3">
								<el-button type="primary" size="medium" @click="sendMsg()">发送消息</el-button>
							</el-col>
						</el-row>
					</div>
				</el-main>
			</el-container>
		</el-container>

	</div>
</template>


<script src="./message.js"></script>

<style lang="less">
.chat-main {
	margin: 0;
	margin-top: 40px;
	padding: 0;
	width: 100%;
	height: 700px;
	overflow: auto;
	font-size: 16px;
	position: relative;
	background-color: #ededed;
	box-shadow: 0 0 10px 3px rgba(0, 0, 0, 0.2);
}

.chat-main .tar .tar_back {
	background-color: #ededed;
	box-shadow: 0px 1px 1px 0px rgba(212, 212, 212, 0.6);
}

.van-popover__wrapper {
	width: 100%;
}

.chat-main ul {
	padding: 20px 45px;
	list-style: none;
	// height: 780px;
	// overflow: auto;

}

.chat-main li {
	padding: 10px 10px 10px 10px;
	margin-bottom: 5px;
	position: relative;
	/* 让页面内的文字不可被选中 */
	-webkit-user-select: none;
	-moz-user-select: none;
	-o-user-select: none;
	user-select: none;
}

.chat-main li span {
	display: inline-block;
	border-radius: 7px;
	background: #a6e860;
	padding: 6px 10px 8px 10px;
	max-width: 88%;
	word-wrap: break-word;
}

.chat-main li.text-left span {
	background: white;

}

/* 伪元素类,在文本框前面加上头像 */
.chat-main li.text-left:before {
	content: "";
	width: 38px;
	height: 38px;
	border-radius: 3px;
	display: block;
	background-image: var(--url);
	background-size: 100%;
	position: absolute;
	left: -40px;
	top: 10px;
}

/* 伪元素类 加上气泡框的角 */
.chat-main li.text-left span:after {
	content: "";
	width: 0px;
	height: 0px;
	display: block;
	/* transparent 透明色 */
	border: 7px solid transparent;
	/* 在右边生成一个白色的小三角 */
	border-right: 7px solid white;
	position: absolute;
	left: -3px;
	top: 12px;
}

.text-right {
	text-align: right;
}

.text-right span {
	text-align: left;
}

.chat-main li.text-right:after {
	content: '';
	width: 38px;
	height: 38px;
	border-radius: 3px;
	display: block;
	background-image: var(--url);
	background-size: 100%;
	position: absolute;
	right: -38px;
	top: 10px;
}

.chat-main li.text-right span:after {
	content: "";
	width: 0px;
	height: 0px;
	display: block;
	border: 7px solid transparent;
	border-left: 7px solid #a6e860;
	position: absolute;
	right: -3px;
	top: 11px;
}

.changeButton {
	text-align: center;
}

p {
	/* text-align: center; */
	margin-left: 70px;
	font-size: 12px;
	color: rgb(133, 131, 131);
}

.button {
	margin: 50px auto;
	border-radius: 8px;
}

.mt-10 {
	margin-top: 10px;
	// height: 780px;
	// overflow: auto;
	margin-bottom: 20px;
}

.chat-out {
	font-weight: 700;
	color: #303133;

}

.chat-item {
	box-shadow: 0 0 10px 3px rgba(0, 0, 0, 0.2);
	height: 90px;
	width: 100%;
	display: flex;
	flex-direction: row;
	color: rgb(133, 131, 131);
	margin-bottom: 7px;
}

.chat-item-left {
	flex: 3;
	display: flex;
	justify-content: center;
	align-items: center;
	background-color: #ededed;
}

.chat-item-left img {
	width: 55px;
	height: 55px;
	margin: 5px 5px 5px 0;
	border-radius: 27.5px;
}

.chat-item-right {
	flex: 4;
	background-color: #ededed;
	display: flex;
	justify-content: center;
	align-items: center;
}

.chat-item-right span {
	margin-left: -40px;
}

.chatObject-container {
	height: 800px;
	overflow: auto;
}

.chatObject-container::-webkit-scrollbar {
	width: 4px;
}

.chatObject-container::-webkit-scrollbar-thumb {
	background-color: #d9d9d9;
}

.chat-main::-webkit-scrollbar {
	width: 4px;
}

.chat-main::-webkit-scrollbar-thumb {
	background-color: #d9d9d9;
}

.chat-input {
	font-family: Tahoma, sans-serif;
	width: 600px;
	height: 120px;
	padding: 5px;
	border: 3px solid #ccc;
	// background-image: url(bg.gif);
	background-repeat: no-repeat;
	background-position: bottom right;
}

.chat-input-container {
	margin-top: 20px;
}
</style>