<template>
	<div class="app-container">
		<div class="block">
			<el-row :gutter="24">
				<!-- <el-col :span="4">
          <el-input v-model="listQuery.restaurant_id" placeholder="餐厅id"></el-input>
        </el-col> -->
				<el-col :span="4">
					<el-input v-model="params.cid" placeholder="顾客ID"></el-input>
				</el-col>
				<el-col :span="3">
					<el-input v-model="params.mingRating" placeholder="最小评分"></el-input>
				</el-col>
				<el-col :span="3">
					<el-cascader :options="ImageOptions" v-model="selectImageOptions"></el-cascader>
				</el-col>
				<el-col :span="3">
					<el-cascader :options="StateOptions" v-model="selectStateOptions"></el-cascader>
				</el-col>
				<el-col :span="8">
					<el-button type="success" icon="el-icon-search" @click.native="search">{{ $t('button.search')
					}}</el-button>
					<el-button type="primary" icon="el-icon-refresh" @click.native="reset">{{ $t('button.reset')
					}}</el-button>
				</el-col>
			</el-row>
			<br>

		</div>

		<el-table :data="commentList" v-loading="listLoading" element-loading-text="Loading" border fit
			highlight-current-row>
			<el-table-column type="expand">
				<template slot-scope="props">
					<el-form label-position="left" inline class="application-table-expand">
						<el-form-item label="评论内容">
							<div class="flex">
								<span>{{ props.row.content }}</span>
								<div>
									<el-image :key="index" v-for="(item, index) in props.row.images"
										style="width: 100px; height: 100px" :src="item"
										:preview-src-list="props.row.images">
									</el-image>
								</div>
							</div>
						</el-form-item>
						<el-form-item label="我的回复:" v-if="props.row.haveReplay">
							<div class="flex">
								<span>{{ props.row.childComment.content }}</span>
							</div>
						</el-form-item>
					</el-form>
				</template>
			</el-table-column>
			<el-table-column label="评论ID" prop="id">
			</el-table-column>
			<el-table-column label="用户ID" prop="customerId">
			</el-table-column>
			<el-table-column label="用户名" prop="customerName">
			</el-table-column>
			<el-table-column label="订单ID" prop="id">
			</el-table-column>
			<el-table-column label="头像">
				<template slot-scope="scope">
					<!-- <div>{{scope.row}}</div> -->
					<img :src="scope.row.customerImg" style="width:50px;">
				</template>
			</el-table-column>
			<el-table-column label="发布时间" prop="createTime">
			</el-table-column>
			<el-table-column label="评分" prop="rating">
			</el-table-column>
			<el-table-column label="状态" prop="haveReplayStr">
			</el-table-column>
			<el-table-column label="操作" width="300">
				<template slot-scope="scope">
					<!-- <el-dropdown size="small" split-button type="primary">
						操作
						<el-dropdown-menu slot="dropdown">
							<el-dropdown-item @click.native="handleViewImgList(scope.row)">查看图片列表</el-dropdown-item>
							<el-dropdown-item @click.native="handleReply(scope.row)">回复</el-dropdown-item>
						</el-dropdown-menu>
					</el-dropdown> -->
					<el-button v-if="!scope.row.haveReplay" size="small" type="success"
						@click.native="handleReply(scope.row)">回复该评论</el-button>
					<el-button size="small" type="primary"
						@click.native="handleViewOrder(scope.row)">查看该订单信息</el-button>
				</template>

			</el-table-column>
		</el-table>

		<el-pagination background layout="total, sizes, prev, pager, next, jumper" :page-sizes="[10, 20, 50, 100, 500]"
			:page-size="params.size" :total="total" @size-change="changeSize" @current-change="fetchPage"
			@prev-click="fetchPrev" @next-click="fetchNext">
		</el-pagination>

		<div v-if="SelectedComment">
			<el-dialog :title="'回复'+SelectedComment.customerName+'的评论'" :visible.sync="replyComment">
			<el-form>
				<el-form-item label="内容:" label-width="120px">
					<span>{{SelectedComment.content}}</span>
				</el-form-item>
				<el-form-item label="输入您的回复:" label-width="120px">
					<el-input type="textarea" v-model="replyValue"></el-input>
				</el-form-item>
			</el-form>
			<div slot="footer" class="dialog-footer">
				<el-button @click="replyComment = false">取 消</el-button>
				<el-button type="primary" @click="confirmReply">回 复</el-button>
			</div>
		</el-dialog>
		</div>

	</div>
</template>

<script src="./comment.js"></script>
<style scoped>
.flex {
	display: flex;
	flex-direction: column;
}
</style>