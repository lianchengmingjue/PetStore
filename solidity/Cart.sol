pragma solidity ^0.4.25;
pragma experimental ABIEncoderV2;
import "./Table.sol";

contract Cart {
    event InsertEvent(int256 ret,string emit_describe);
    event RemoveEvent(int256 ret,string emit_describe);

    TableFactory tableFactory;
    constructor() public {
        // 构造函数中创建t_account表
        createTable();
    }
    function createTable() private {
         tableFactory = TableFactory(0x1001);
        // 账户管理表, key : account, field : asset_value
        // |  账户id即邮箱(主键)  |    宠物id    |
        // |-------------------- |--------------|
        // |        user_id      |     pet_id   |
        // |---------------------|--------------|
        //
        // 创建表
        tableFactory.createTable("t_cart", "user_id", "pet_id");
    }

    /*返回指定user_id下全部pet_id信息*/
    function select(string user_id) public returns(string[])
    {
        Table table = tableFactory.openTable("t_cart");
        Entries entries = table.select(user_id, table.newCondition()); 

        string[] memory pet_id_list = new string[](uint256(entries.size()));
        for (int256 i = 0; i < entries.size(); ++i) {
            Entry entry = entries.get(i);
            pet_id_list[uint256(i)] = entry.getString("pet_id");
        }

        return (pet_id_list);
    }


    /*
        描述 : 增加购物车信息
        参数 ：
                user_id : 用户id
                pet_id : 宠物id
                emit_description : 提交事件描述，是一个涵盖上述参数的string
        返回值：
                0  成功
                -1 失败
     */
    function insert(string user_id,string pet_id,string emit_description) public returns(int256)
    {
        int256 ret_code;
        Table table = tableFactory.openTable("t_cart");
        
        Entry entry = table.newEntry();
        entry.set("user_id", user_id);
        entry.set("pet_id", pet_id);

        int count = table.insert(user_id, entry);
        if (count == 1) {
            ret_code = 0;
        } else {
            ret_code = -1;
        }
        
        emit InsertEvent(ret_code, emit_description);
        return ret_code;
    }

    /*
        描述 : 删除购物车信息
        参数 ：
                user_id : 用户id
                pet_id : 宠物id
                emit_description : 提交事件描述，是一个涵盖上述参数的string
        返回值：
                0  成功
                -1 失败
     */
    function remove(string user_id,string pet_id,string emit_description) public returns(int256)
    {
        int256 ret_code = 0;
        Table table = tableFactory.openTable("t_cart");

        Condition condition = table.newCondition();
        condition.EQ("pet_id",pet_id);
        int count = table.remove(user_id, condition);
        if (count == 1) {
            ret_code = 0;
        } else {
            ret_code = -1;
        }
        emit RemoveEvent(ret_code, emit_description);
        return ret_code;
    }

     /*
        描述 : 检查信息是否唯一
        参数 ：
                user_id : 用户id
                pet_id : 宠物id
        返回值：
                0  唯一
                -1 数据库中已有记录
     */
    function unique(string user_id,string pet_id) public returns(int256){
        int256 ret_code = 0;
        Table table = tableFactory.openTable("t_cart");

        Condition condition = table.newCondition();
        condition.EQ("pet_id",pet_id);
        Entries entries = table.select(user_id, condition); 
        if (entries.size()==0){
            ret_code=0;
        }else{
            ret_code=-1;
        }
        return ret_code;
    }
}
