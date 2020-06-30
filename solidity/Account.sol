pragma solidity ^0.4.25;

import "./Table.sol";

contract Account {
    // event
    event RegisterEvent(int256 ret,string emit_describe);
    event UpdateEvent(int256 ret,string emit_describe);
    event RemoveEvent(int256 ret,string emit_describe);

    constructor() public {
        // 构造函数中创建t_account表
        createTable();
    }

    function createTable() private {
        TableFactory tf = TableFactory(0x1001);
        // 账户管理表, key : account, field : asset_value
        // |  账户id即邮箱(主键)  |    密码    |  用户昵称  |  账户余额   |  用户头像   |  管理员权限   |
        // |-------------------- |------------|------------|------------|------------|--------------|
        // |        user_id      |  password  |    name    |  balance   |  photo_url | administrator|
        // |---------------------|------------|------------|------------|------------|--------------|
        //
        // 创建表
        tf.createTable("t_account", "user_id", "password,name,balance,photo_url,administrator");
    }

    /*打开账户表格 */
    function openTable() private returns(Table) {
        TableFactory tf = TableFactory(0x1001);
        Table table = tf.openTable("t_account");
        return table;
    }

     /*
    描述 :检查user_id是否已经存在
    参数 ：
            user_id : 用户id
    返回值：
            0  不存在
            -1 已存在
     */
    function uniqueId(string user_id) public returns(int256){
	int ret_code;
        Table table = openTable();
        Entries entries = table.select(user_id, table.newCondition()); 

        if (entries.size()==0){
            ret_code=0;
        }else{
            ret_code=-1;
        }
        return ret_code;
    }

    /*
    描述 : 账户注册
    参数 ：
            user_id : 账户id即用户邮箱
            password : 登录密码
            name: 用户昵称
            balance  : 资产金额
            photo_url : 头像信息
            administrator : 管理员属性
            emit_description ：提交事件描述，是一个涵盖上述参数的string
    返回值：
            0  账户注册成功
            -1 账户注册失败
    */
    function regist(string user_id, string password,string name,int256 balance,string photo_url,string administrator,string emit_description) public returns(int256){
        int256 ret_code = 0;
        Table table = openTable();

        Entry entry = table.newEntry();
        entry.set("user_id", user_id);
        entry.set("password", password);
        entry.set("name", name);
        entry.set("balance", balance);
        entry.set("photo_url", photo_url);
        entry.set("administrator",administrator);

        // 插入
        int count = table.insert(user_id, entry);
        if (count == 1) {
            // 成功
            ret_code = 0;
        } else {
            // 失败? 无权限或者其他错误
            ret_code = -1;
        }
        
        emit RegisterEvent(ret_code, emit_description);
        return ret_code;
    }

    /*
    描述 : 删除账户
    参数 ：
            user_id : 账户id
            emit_description ：提交事件描述，是一个涵盖上述参数的string
    返回值：
            0 :成功
            -1 :失败
    */
    function remove(string user_id,string emit_description) public returns(int256){
        int256 ret_code = 0;
        Table table = openTable();
        // 删除

        int count = table.remove(user_id, table.newCondition());
        if (count == 1) {
            // 成功
            ret_code = 0;
        } else {
            // 失败? 无权限或者其他错误
            ret_code = -1;
        }
        emit RemoveEvent(ret_code, emit_description);
        return ret_code;
    }

    /*
    描述 : 根据账户id修改信息
    参数 ：
            user_id : 账户id即用户邮箱
            password : 登录密码
            name: 用户昵称
            balance  : 资产金额
            administrator : 管理员属性
            emit_description ：修改事件描述，是一个涵盖上述参数的string

    返回值：
            参数一： 成功返回0, 账户不存在返回-1
            参数二： 第一个参数为0时有效，账户密码，账户余额等信息
    */
    function update(string user_id, string password,string name,int256 balance,string photo_url,string administrator,string emit_description) public returns(int256){
        int256 ret_code = 0;
        Table table = openTable();

        Entry entry = table.newEntry();
        entry.set("user_id", user_id);
        entry.set("password", password);
        entry.set("name", name);
        entry.set("balance", balance);
        entry.set("photo_url", photo_url);
        entry.set("administrator",administrator);

        // 修改
        int count = table.update(user_id, entry,table.newCondition());
        if (count == 1) {
            // 成功
            ret_code = 0;
        } else {
            // 失败? 无权限或者其他错误
            ret_code = -1;
        }
        
        emit UpdateEvent(ret_code, emit_description);
        return ret_code;
    }
    
    /*
    描述 : 根据账户id查询信息
    参数 ：
            user_id : 账户id

    返回值：
            参数一： 成功返回0, 账户不存在返回-1
            参数二： 第一个参数为0时有效，账户密码，账户余额等信息
    */
    function select(string user_id) public constant returns(int256, string,string,int256,string,string) {
        // 打开表
        Table table = openTable();
        // 查询
        Entries entries = table.select(user_id, table.newCondition());
        if (0 == uint256(entries.size())) {
            return (-1, '','',0,'','');
        } else {
            Entry entry = entries.get(0);
            return (0,entry.getString("password"),entry.getString("name"),entry.getInt("balance"),entry.getString("photo_url"),entry.getString("administrator"));
        }
    }

}
