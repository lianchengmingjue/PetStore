pragma solidity ^0.4.25;
pragma experimental ABIEncoderV2;

import "./Table.sol";

contract Arbitration
{
    //event
    event ArbitrationEvent(int256 ret, string from_account, string to_account, uint256 amount);

    struct ArbitrationEntries
    {
        string[] a_id;
        string[] t_id;
        string[] time;
        string[] desc;
        string[] comp;
        string[] u_id;
    }
    ArbitrationEntries arbitrationEntries;

    constructor() public {
        // 构造函数中创建t_arbitration表
        createTable();
    }


        function createTable() private {
        TableFactory tf = TableFactory(0x1001);
        // 仲裁表：
        //arbitration_id      string   仲裁id 
        //transaction_id      string   交易id
        //arbitration_time    string   申请时间
        //description         string   描述
        //complete            string   仲裁是否被完成：0：待审核 -1：拒绝仲裁 +1：仲裁已执行
        //user_id             string   申请人id       
        // 创建表
        tf.createTable("t_arbitration","key", "arbitration_id,transaction_id,arbitration_time,description,complete,user_id");
    }

    //申请仲裁: 输入交易id，请求撤销这个交易
    //其他输入：仲裁id，时间，描述，申请人id
    function request(string a_id,string t_id,string time,string desc,string u_id) public returns(int256)
    {
        TableFactory tf = TableFactory(0x1001);
        Table table = tf.openTable("t_arbitration");
        Entry entry = table.newEntry();
        entry.set("key","");
        entry.set("arbitration_id",a_id);
        entry.set("transaction_id",t_id);
        entry.set("arbitration_time",time);
        entry.set("description",desc);
        entry.set("complete","0");
        entry.set("user_id",u_id);
        table.insert("",entry);

        return 0;
    }


    // 处理仲裁
    // 输入仲裁id和flag，flag=0拒绝，flag!=0通过
    function verify(string a_id,int flag) public returns(int256)
    {
        TableFactory tf = TableFactory(0x1001);
        Table table = tf.openTable("t_arbitration");

        Condition condition = table.newCondition();
        condition.EQ("arbitration_id",a_id);
        Entries entries = table.select("",condition);
        Entry entry = entries.get(0);
        if (flag == 0)//拒绝
        {
            entry.set("complete","-1");
            table.update("",entry,condition);
        }
        else
        {
            entry.set("complete","+1");
            table.update("",entry,condition);
            string memory t_id = entry.getString("transaction_id");
            //回退金额
            //获取买卖家id和金额
            table = tf.openTable("t_transaction");
            condition = table.newCondition();
            condition.EQ("transaction_id",t_id);
            entries = table.select("",condition);
            entry = entries.get(0);//获得transaction
            string memory s_id = entry.getString("seller_id");
            string memory b_id = entry.getString("buyer_id");
            string memory p_id = entry.getString("pet_id");
            int256 price = entry.getInt("price");
            //执行回退
            table = tf.openTable("t_account");
            //更新卖家
            entries = table.select(s_id,table.newCondition());
            entry = entries.get(0);
            entry.set("balance",entry.getInt("balance")-price);
            table.update(s_id, entry, table.newCondition());
            //更新买家
            entries= table.select(b_id,table.newCondition());
            entry = entries.get(0);
            entry.set("balance",entry.getInt("balance")+price);
            table.update(b_id, entry, table.newCondition());
            //更新宠物
            table = tf.openTable("t_pet");
            condition = table.newCondition();
            condition.EQ("pet_id",p_id);
            entries = table.select("key", condition);
            entry = entries.get(0);
            entry.set("on_sell","0");
            entry.set("user_id",s_id);
            table.update("key",entry,condition);
        }

        return 0;
    }



    // 查询数据
    //返回 仲裁号，交易号，描述，时间
    function select(string id) public constant returns(string[],string[],string[],string[],string[],string[])
    {
        Table table = TableFactory(0x1001).openTable("t_arbitration");

        // 条件为空表示不筛选 也可以根据需要使用条件筛选
        Condition condition = table.newCondition();
        condition.EQ("arbitration_id",id);
        Entries entries = table.select("", condition);


        arbitrationEntries. a_id   = new string[](uint256(entries.size()));
        arbitrationEntries. t_id   = new string[](uint256(entries.size()));
        arbitrationEntries. time   = new string[](uint256(entries.size()));
        arbitrationEntries. desc   = new string[](uint256(entries.size()));
        arbitrationEntries. comp   = new string[](uint256(entries.size()));
        arbitrationEntries. u_id   = new string[](uint256(entries.size()));
        

        for(int i=0; i<entries.size(); ++i) {
            Entry entry = entries.get(i);

            arbitrationEntries.a_id  [uint256(i)] = entry.getString("arbitration_id");
            arbitrationEntries.t_id  [uint256(i)] = entry.getString("transaction_id");
            arbitrationEntries.time  [uint256(i)] = entry.getString("arbitration_time");
            arbitrationEntries.desc  [uint256(i)] = entry.getString("description");
            arbitrationEntries.comp  [uint256(i)] = entry.getString("complete");
            arbitrationEntries.u_id  [uint256(i)] = entry.getString("user_id");
        }

        return 
        (
            arbitrationEntries.a_id ,
            arbitrationEntries.t_id,
            arbitrationEntries.time,
            arbitrationEntries.desc,
            arbitrationEntries.comp,
            arbitrationEntries.u_id
        );
    }

    //显示所有仲裁
    function allArbitrations() public constant returns(string[],string[],string[],string[],string[],string[])
    {
        Table table = TableFactory(0x1001).openTable("t_arbitration");

        // 条件为空表示不筛选 也可以根据需要使用条件筛选
        Condition condition = table.newCondition();
        Entries entries = table.select("", condition);


        arbitrationEntries. a_id   = new string[](uint256(entries.size()));
        arbitrationEntries. t_id   = new string[](uint256(entries.size()));
        arbitrationEntries. time   = new string[](uint256(entries.size()));
        arbitrationEntries. desc   = new string[](uint256(entries.size()));
        arbitrationEntries. comp   = new string[](uint256(entries.size()));
        arbitrationEntries. u_id   = new string[](uint256(entries.size()));
        

        for(int i=0; i<entries.size(); ++i) {
            Entry entry = entries.get(i);

            arbitrationEntries.a_id  [uint256(i)] = entry.getString("arbitration_id");
            arbitrationEntries.t_id  [uint256(i)] = entry.getString("transaction_id");
            arbitrationEntries.time  [uint256(i)] = entry.getString("arbitration_time");
            arbitrationEntries.desc  [uint256(i)] = entry.getString("description");
            arbitrationEntries.comp  [uint256(i)] = entry.getString("complete");
            arbitrationEntries.u_id  [uint256(i)] = entry.getString("user_id");
        }

        return 
        (
            arbitrationEntries.a_id ,
            arbitrationEntries.t_id,
            arbitrationEntries.time,
            arbitrationEntries.desc,
            arbitrationEntries.comp,
            arbitrationEntries.u_id
        );
    }

}
