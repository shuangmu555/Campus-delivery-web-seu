<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="商店编码" prop="sId">
        <el-input
          v-model="queryParams.sId"
          placeholder="请输入商店编码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="店长编码" prop="uId">
        <el-input
          v-model="queryParams.uId"
          placeholder="请输入店长编码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="商店名称" prop="sname">
        <el-input
          v-model="queryParams.sname"
          placeholder="请输入商店名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="storeListList" stripe @selection-change="handleSelectionChange">
      <el-table-column label="商店描述" type="expand"  width="100">
          <template #default="props">
            <p><b>商店地点: </b></p>
            <p>{{ splitDescription1(props.row.description) }}</p>
            <p><b>商店描述: </b></p>
            <p>{{ splitDescription2(props.row.description) }}</p>
          </template>
        </el-table-column>


      <el-table-column label="商店编码" align="center" prop="sId" width="100" />
      <el-table-column label="店长编码" align="center" prop="uId" width="100" />
      <el-table-column label="商店名称" align="center" prop="sname" />
      <el-table-column label="图标" align="center" prop="logo">
        <template #default="scope">
          <image-preview :src="scope.row.logo" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="110">
        <template #default="scope">
          <el-button link type="primary" icon="ShoppingCart" @click="handlegoshopping(scope.row)">购物</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改商家列表对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="storeListRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="商店名称" prop="sname">
          <el-input v-model="form.sname" placeholder="请输入商店名称" />
        </el-form-item>
        <el-form-item label="图标" prop="logo">
          <image-upload v-model="form.logo"/>
        </el-form-item>
        <el-form-item label="商店描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="StoreList">
import { listStoreList, getStoreList, delStoreList, addStoreList, updateStoreList } from "@/api/storeList/storeList";

const { proxy } = getCurrentInstance();

const storeListList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");

const router = useRouter();

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    sId: null,
    uId: null,
    sname: null,
    logo: null,
    description: null
  },
  rules: {
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询商家列表列表 */
function getList() {
  loading.value = true;
  listStoreList(queryParams.value).then(response => {
    storeListList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

// 取消按钮
function cancel() {
  open.value = false;
  reset();
}

// 表单重置
function reset() {
  form.value = {
    sId: null,
    uId: null,
    sname: null,
    logo: null,
    description: null
  };
  proxy.resetForm("storeListRef");
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.sId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加商家列表";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _sId = row.sId || ids.value
  // console.log(row);
  getStoreList(_sId).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改商家列表";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["storeListRef"].validate(valid => {
    if (valid) {
      if (form.value.sId != null) {
        updateStoreList(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addStoreList(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _sIds = row.sId || ids.value;
  proxy.$modal.confirm('是否确认删除商家列表编号为"' + _sIds + '"的数据项？').then(function() {
    return delStoreList(_sIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 购物按钮跳转 */
function handlegoshopping(row) {
  const sid = row.sId;
  router.push("/goshopping/itemList/store/" + sid);
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('storeList/storeList/export', {
    ...queryParams.value
  }, `storeList_${new Date().getTime()}.xlsx`)
}

function splitDescription1(str) {
  const parts = str.split(' ');
  return parts[0];
}

function splitDescription2(str) {
  const parts = str.split(' ');
  return parts.slice(1).join(' ');
}


getList();
</script>
