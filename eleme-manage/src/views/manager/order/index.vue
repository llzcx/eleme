<template>
  <div class="app-container">
    <div class="block">
      <el-row :gutter="24">
        <!-- <el-col :span="4">
          <el-input v-model="listQuery.restaurant_id" placeholder="餐厅id"></el-input>
        </el-col> -->
        <el-col :span="4">
          <el-input v-model="listQuery.oid" placeholder="订单ID"></el-input>
        </el-col>
        <el-col :span="4">
          <el-input v-model="listQuery.shopId" placeholder="商家ID"></el-input>
        </el-col>
        <el-col :span="3">
          <el-cascader :options="listQuery.StateOptions" v-model="listQuery.selectedState"></el-cascader>
        </el-col>
        <el-col :span="8">
          <el-button type="success" icon="el-icon-search" @click.native="search">{{ $t('button.search') }}</el-button>
          <el-button type="primary" icon="el-icon-refresh" @click.native="reset">{{ $t('button.reset') }}</el-button>
        </el-col>
      </el-row>
      <br>

    </div>

    <el-table :data="list" v-loading="listLoading" element-loading-text="Loading" border fit highlight-current-row
      :row-key="getRowKeys">
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form label-position="left" inline class="application-table-expand">
            <el-form-item label="收货人">
              <span>{{ props.row.consignee }}</span>
            </el-form-item>
            <el-form-item label="店铺名称">
              <span>{{ props.row.shopName }}</span>
            </el-form-item>
            <el-form-item label="收货地址">
              <span>{{ props.row.addressName }}</span>
            </el-form-item>
            <el-form-item label="收货地址经纬度">
              <span>{{ props.row.geohash }}</span>
            </el-form-item>
            <el-form-item label="店铺ID">
              <span>{{ props.row.shopId }}</span>
            </el-form-item>
            <el-form-item label="店铺地址">
              <span>{{ props.row.shopAddress }}</span>
            </el-form-item>
            <el-form-item label="店铺经纬度">
              <span>{{ props.row.shopGeohash }}</span>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>

      <el-table-column label="订单ID" prop="id">
      </el-table-column>
      <el-table-column label="总价格" prop="totalPrice">
      </el-table-column>
      <el-table-column label="状态" prop="orderStateStr">
      </el-table-column>
      <el-table-column label="下单时间" prop="createTime">
      </el-table-column>
      <el-table-column label="预计送达时间" prop="expectedTime">
      </el-table-column>
      <el-table-column label="操作" width="350">
        <template slot-scope="scope">
          <el-dropdown size="small" split-button type="primary">
            操作
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item @click.native="handleViewOrder(scope.row)">查看详情</el-dropdown-item>
              <el-dropdown-item v-if="scope.row.orderStateStr == '已支付'"
                @click.native="handleToReceiving(scope.row.id)">确认接单</el-dropdown-item>
              <el-dropdown-item v-if="scope.row.orderStateStr == '正在制作'"
                @click.native="handleToDispatch(scope.row.id)">确认派送</el-dropdown-item>
              <el-dropdown-item v-if="scope.row.orderStateStr == '正在派送'"
                @click.native="handleToFinish(scope.row.id)">确认送达</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <el-button size="small" type="success" @click.native="handleViewOrder(scope.row)">菜品信息</el-button>
          <el-button size="small" type="success" @click.native="handleOrderTrack(scope.row)">订单追踪</el-button>
        </template>

      </el-table-column>
    </el-table>

    <el-pagination background layout="total, sizes, prev, pager, next, jumper" :page-sizes="[10, 20, 50, 100, 500]"
      :page-size="listQuery.size" :total="total" @size-change="changeSize" @current-change="fetchPage"
      @prev-click="fetchPrev" @next-click="fetchNext">
    </el-pagination>

    <el-dialog title="订单追踪" :visible.sync="trackOrderShow">
      <div v-if="selectTrackOrder && selectTrackOrder.createTime">
        <el-steps
          v-if="!((selectTrackOrder && selectTrackOrder.cancelTime) || (selectTrackOrder && selectTrackOrder.timeoutTime))"
          :active="getStateValue(selectTrackOrder)" align-center>
          <el-step title="订单创建" :description="selectTrackOrder.createTime"></el-step>
          <el-step title="用户下单" :description="selectTrackOrder.payTime"></el-step>
          <el-step title="商家接单" :description="selectTrackOrder.receivingTime"></el-step>
          <el-step title="商家配送" :description="selectTrackOrder.dispatchTime"></el-step>
          <el-step title="商家送达" :description="selectTrackOrder.arrivalTime"></el-step>
          <el-step title="订单完成" :description="selectTrackOrder.finishTime"></el-step>
        </el-steps>
        <el-steps v-else :active="getStateValue(selectTrackOrder)" align-center>
          <el-step title="订单创建" :description="selectTrackOrder.createTime"></el-step>
          <el-step title="用户下单" :description="selectTrackOrder.payTime"></el-step>
          <el-step v-if="selectTrackOrder && selectTrackOrder.cancelTime" title="订单取消"
            :description="selectTrackOrder.cancelTime"></el-step>
          <el-step v-if="selectTrackOrder && selectTrackOrder.timeoutTime" title="订单超时"
            :description="selectTrackOrder.timeoutTime"></el-step>
        </el-steps>
      </div>
    </el-dialog>
  </div>
</template>

<script src="./order.js"></script>
<style rel="stylesheet/scss" lang="scss" scoped>
@import "src/styles/common.scss";
</style>
