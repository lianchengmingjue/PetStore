pragma solidity ^0.4.25;
pragma experimental ABIEncoderV2;

import "./Table.sol";



contract Transaction
{
    //event
    event TransferEvent(int256 ret, string from_account, string to_account, uint256 amount);


    struct TransactionEntries
    {
        string[] t_id;
        string[] time;
        int256[] price;
        string[] b_id;
        string[] s_id;
        string[] p_id;
    }
    TransactionEntries transactionEntries;

    constructor() public {
        // 构造函数中创建t_transaction表
        createTable();
    }


        function createTable() private {
        TableFactory tf = TableFactory(0x1001);
        // 交易表：
        //transaction_id      unint256        订单号id       
        //transaction_time    unint256   订单时间
        //price               fixed     订单价格
        //buyer_id            string     买家id
        //seller_id           string     卖家id
        //pet_id              unint256        商品id       
        // 创建表
        tf.createTable("t_transaction","key","transaction_id,transaction_time,price,buyer_id,seller_id,pet_id");
    }


    /*
    描述 : 发起交易
    参数 ：       
        //transaction_time    datetime   订单时间
        //price               double     订单价格
        //buyer_id            string     买家id
        //seller_id           string     卖家id
        //pet_id              int        商品id  
    返回值1：
            0  资产转移成功
            -1 转移资产账户不存在
            -2 接收资产账户不存在
            -3 金额不足
            -4 金额溢出
            -5 宠物不存在
            -6 宠物已经被售出
            -7 其他错误
    返回值1：
            订单号id
    */
    function transfer(string transaction_id,string transaction_time, int256 price, string seller_id, string buyer_id, string pet_id) public returns(int256) {
        // 查询转移资产账户信息

        //打开table
        TableFactory tf = TableFactory(0x1001);

        //更新t_transaction:插入一行
        Table table = tf.openTable("t_transaction");
        Entry entry = table.newEntry();
        entry.set("key","");
        entry.set("transaction_id", transaction_id);
        entry.set("transaction_time",transaction_time);
        entry.set("price",price);
        entry.set("buyer_id",buyer_id);
        entry.set("seller_id",seller_id);
        entry.set("pet_id", pet_id);
        table.insert("",entry);


        //更新卖家
        table = tf.openTable("t_account");
        Entries entries = table.select(seller_id,table.newCondition());
        entry = entries.get(0);
        entry.set("balance",entry.getInt("balance")+price);
        table.update(seller_id, entry, table.newCondition());

        //更新买家
        entries= table.select(buyer_id,table.newCondition());
        entry = entries.get(0);
        entry.set("balance",entry.getInt("balance")-price);
        table.update(buyer_id, entry, table.newCondition());



        //更新宠物
        table = tf.openTable("t_pet");
        Condition condition = table.newCondition();
        condition.EQ("pet_id",pet_id);
        entries = table.select("key", condition);
        entry = entries.get(0);
        entry.set("on_sell","0");
        entry.set("user_id",buyer_id);
        table.update("key",entry,condition);
        
        //emit TransferEvent(ret_code, from_account, to_account, amount);

        return 0;
    }


    // 查询数据
    function select(string name) public constant returns(string[], string[], int256[],string[],string[],string[])
    {
        Table table = TableFactory(0x1001).openTable("t_transaction");

        // 条件为空表示不筛选 也可以根据需要使用条件筛选
        Condition condition = table.newCondition();
        condition.EQ("transaction_id",name);
        Entries entries = table.select("", condition);

        transactionEntries.t_id   = new string[](uint256(entries.size()));
        transactionEntries.time   = new string[](uint256(entries.size()));
        transactionEntries.price  = new int256[](uint256(entries.size()));
        transactionEntries.b_id   = new string[](uint256(entries.size()));
        transactionEntries.s_id   = new string[](uint256(entries.size()));
        transactionEntries.p_id   = new string[](uint256(entries.size()));


        for(int i=0; i<entries.size(); ++i) {
            Entry entry = entries.get(i);

            transactionEntries.t_id  [uint256(i)] = entry.getString("transaction_id");
            transactionEntries.time  [uint256(i)] = entry.getString("transaction_time");   
            transactionEntries.price [uint256(i)] = entry.getInt   ("price");
            transactionEntries.b_id  [uint256(i)] = entry.getString("buyer_id");
            transactionEntries.s_id  [uint256(i)] = entry.getString("seller_id");
            transactionEntries.p_id  [uint256(i)] = entry.getString("pet_id");
        }

        return(
        transactionEntries.t_id,
        transactionEntries.time,
        transactionEntries.price,
        transactionEntries.s_id,
        transactionEntries.b_id,
        transactionEntries.p_id
        );
    }


    // 查询数据
    function allTransactions() public constant returns(string[], string[], int256[],string[],string[],string[])
    {
        Table table = TableFactory(0x1001).openTable("t_transaction");

        // 条件为空表示不筛选 也可以根据需要使用条件筛选
        Condition condition = table.newCondition();
        Entries entries = table.select("", condition);

        transactionEntries.t_id   = new string[](uint256(entries.size()));
        transactionEntries.time   = new string[](uint256(entries.size()));
        transactionEntries.price  = new int256[](uint256(entries.size()));
        transactionEntries.b_id   = new string[](uint256(entries.size()));
        transactionEntries.s_id   = new string[](uint256(entries.size()));
        transactionEntries.p_id   = new string[](uint256(entries.size()));


        for(int i=0; i<entries.size(); ++i) {
            Entry entry = entries.get(i);

            transactionEntries.t_id  [uint256(i)] = entry.getString("transaction_id");
            transactionEntries.time  [uint256(i)] = entry.getString("transaction_time");   
            transactionEntries.price [uint256(i)] = entry.getInt   ("price");
            transactionEntries.b_id  [uint256(i)] = entry.getString("buyer_id");
            transactionEntries.s_id  [uint256(i)] = entry.getString("seller_id");
            transactionEntries.p_id  [uint256(i)] = entry.getString("pet_id");
        }

        return(
        transactionEntries.t_id,
        transactionEntries.time,
        transactionEntries.price,
        transactionEntries.s_id,
        transactionEntries.b_id,
        transactionEntries.p_id
        );
    }


}
