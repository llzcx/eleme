<template>
  <div class="app-container">
    <div class="block">
      <el-row>
        <!--<el-col :span="24">-->
          <!--<el-button type="success" size="mini" icon="el-icon-edit" @click.native="openSendOutForm"-->
                     <!--v-show="form.statusName =='待发货'">发货-->
          <!--</el-button>-->
          <!--<el-button type="default" size="mini" @click="printOrder">打 印</el-button>-->
        <!--</el-col>-->
      </el-row>
    </div>

    <el-form ref="print"   label-width="150px" >
      <el-row>
        <h3>订单基本信息</h3>
        <el-col :span="12">
          <el-form-item label="订单ID">
            <span v-if="form.id"> {{ form.id}}</span>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="订餐人">
              {{ form.consignee}}
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="支付总金额">
            <span> {{ form.totalPrice}}元</span>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="状态">
            <span> <strong>{{ form.orderStateStr }} </strong></span>
          </el-form-item>
        </el-col>

        <h3>收货信息</h3>
        <el-col :span="12">
          <el-form-item label="收货人">
            <span>{{ form.consignee}}</span>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联系电话">
            <span>{{ form.phone}}</span>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="收货地址">
            <span>{{ form.addressName}}</span>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <h3>订单明细</h3>
        </el-col>
        <el-table :data="form.orderDetailsList" border fit  >
          <el-table-column label="商品">
            <template slot-scope="scope">
              <!--<router-link :to="{path:'goodsEdit?id='+scope.row.goods.id}">-->
                <span> {{scope.row.goodsName}}</span>
              <!--</router-link>-->
            </template>
          </el-table-column>
          <el-table-column label="图片">
            <template slot-scope="scope">
            <img :src="scope.row.imagePath" style="width:50px;">
            </template>
          </el-table-column>
          <el-table-column label="价格">
            <template slot-scope="scope">
              {{formatPrice(scope.row.price)}}
            </template>
          </el-table-column>
          <el-table-column label="数量">
            <template slot-scope="scope">
              {{scope.row.num}}
            </template>
          </el-table-column>
          <el-table-column label="打包费">
            <template slot-scope="scope">
              {{scope.row.packingFee==null?0:scope.row.packingFee}}
            </template>
          </el-table-column>
          <el-table-column label="合计">
            <template slot-scope="scope">
              {{ formatPrice(scope.row.price*scope.row.num+(scope.row.packingFee==null?0:scope.row.packingFee))}}元
            </template>
          </el-table-column>


        </el-table>


      </el-row>

    </el-form>

  </div>
</template>

<script src="./orderDetail.js"></script>


<style rel="stylesheet/scss" lang="scss" scoped>
  @import "src/styles/common.scss";
</style>

