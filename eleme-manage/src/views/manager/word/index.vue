<template>
	<div class="app-container">
		<div class="block">
			<el-tag :key="tag" v-for="tag in List" closable :disable-transitions="false"
				@close="handleClose(tag)">
				{{ tag }}
			</el-tag>
			<el-input class="input-new-tag" v-if="inputVisible" v-model="inputValue" ref="saveTagInput" size="small"
				@keyup.enter.native="handleInputConfirm" @blur="handleInputConfirm">
			</el-input>
			<el-button v-else class="button-new-tag" size="small" @click="showInput">+ 添加词汇</el-button>

		</div>
	</div>
</template>
  
<script src="./word.js"></script>
  
  
<style lang="less">
.demo-table-expand {
	font-size: 0;
}

.demo-table-expand label {
	width: 90px;
	color: #99a9bf;
}

.demo-table-expand .el-form-item {
	margin-right: 0;
	margin-bottom: 0;
	width: 50%;
}

.table_container {
	padding: 20px;
}

.Pagination {
	display: flex;
	justify-content: flex-start;
	margin-top: 8px;
}

.avatar-uploader .el-upload {
	border: 1px dashed #d9d9d9;
	border-radius: 6px;
	cursor: pointer;
	position: relative;
	overflow: hidden;
}

.avatar-uploader .el-upload:hover {
	border-color: #20a0ff;
}

.avatar-uploader-icon {
	font-size: 28px;
	color: #8c939d;
	width: 120px;
	height: 120px;
	line-height: 120px;
	text-align: center;
}

.avatar {
	width: 120px;
	height: 120px;
	display: block;
}

.el-tag + .el-tag {
    margin-left: 10px;
	margin-bottom: 5px;
  }
  .button-new-tag {
    margin-left: 10px;
    height: 32px;
    line-height: 30px;
    padding-top: 0;
    padding-bottom: 0;
  }
  .input-new-tag {
    width: 90px;
    margin-left: 10px;
    vertical-align: bottom;
  }
</style>
  