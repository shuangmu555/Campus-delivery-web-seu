package com.ruoyi.store.mapper;

import java.util.List;
import com.ruoyi.store.domain.myStore;

/**
 * 我的商店Mapper接口
 * 
 * @author xpl
 * @date 2024-05-13
 */
public interface myStoreMapper 
{
    /**
     * 查询我的商店
     * 
     * @param sId 我的商店主键
     * @return 我的商店
     */
    public myStore selectmyStoreBySId(Long sId);

    /**
     * 查询我的商店列表
     * 
     * @param myStore 我的商店
     * @return 我的商店集合
     */
    public List<myStore> selectmyStoreList(myStore myStore);

    /**
     * 新增我的商店
     * 
     * @param myStore 我的商店
     * @return 结果
     */
    public int insertmyStore(myStore myStore);

    /**
     * 修改我的商店
     * 
     * @param myStore 我的商店
     * @return 结果
     */
    public int updatemyStore(myStore myStore);

    /**
     * 删除我的商店
     * 
     * @param sId 我的商店主键
     * @return 结果
     */
    public int deletemyStoreBySId(Long sId);

    /**
     * 批量删除我的商店
     * 
     * @param sIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletemyStoreBySIds(Long[] sIds);
}
