pragma solidity ^0.4.25;
pragma experimental ABIEncoderV2;
import "./Table.sol";

contract Market {
    // event
    //event RegisterPetEvent(int256 ret,string pet_id,string name,string birth,string variety,string description,string photo_url,int256 on_sell,int256 price,string user_id);
    //event UpdatePetEvent(int256 ret,string pet_id,string name,string birth,string variety,string description,string photo_url,int256 on_sell,int256 price,string user_id);
    //使用这种参数形式进行上链会导致参数过多，报错Stack too deep

    event RegisterPetEvent(int256 ret,string emit_describe);
    event UpdatePetEvent(int256 ret,string emit_describe);
    event RemovePetEvent(int256 ret,string emit_describe);

    //定义全局变量以减少函数体内变量
    TableFactory tableFactory;
    int ret_code;
    Condition condition;

    constructor() public {
        // 构造函数中创建t_pet表
        createTable();
    }

    function createTable() private {
        tableFactory = TableFactory(0x1001);
        //
        // | 占位(恒为"key") |    宠物id      |   宠物名  |  出生日期  |  品种   |    描述    |   图片url  |  是否上架    |   价格   |   所有人id   |   
        // |--------------- |--------------- |-----------|-----------|---------|------------|------------|-------------|----------|-------------|
        // |    key         |    pet_id      |  name     |    birth  | variety | description|  photo_url |   on_sell   |   price  |    user_id  | 
        // |--------------- |--------------- |-----------|-----------|---------|------------|------------|-------------|----------|-------------|
        //
        tableFactory.createTable("t_pet", "key","pet_id,name,birth,variety,description,photo_url,on_sell,price,user_id");
    }

    /*
    描述 :检查pet_id是否已经存在
    参数 ：
            pet_id : 宠物id
    返回值：
            0  不存在
            -1 已存在
     */
    function uniqueId(string pet_id) public returns(int256){
        Table table = tableFactory.openTable("t_pet");
        condition = table.newCondition();
        condition.EQ("pet_id",pet_id);
        Entries entries = table.select("key", condition); 

        if (entries.size()==0){
            ret_code=0;
        }else{
            ret_code=-1;
        }
        return ret_code;
    }
    /*
    描述 : 宠物注册
    参数 ：
            pet_id : 宠物id
            name : 宠物名
            birth : 出生日期
            variety : 品种
            description : 描述
            photo_url : 图片url
            on_sell : 是否上架
            price : 价格
            user_id : 所有人id
            emit_description ：提交事件描述，是一个涵盖上述参数的string
    返回值：
            0  宠物注册成功
            -1 失败
    */
    function registPet(string pet_id,string name,string birth,string variety,string description,string photo_url,string on_sell,string price,string user_id,string emit_description) public returns(int256){
        
        Table table = tableFactory.openTable("t_pet");
        //添加数据
        Entry entry = table.newEntry();
        entry.set("key", "key");
        entry.set("pet_id", pet_id);
        entry.set("name", name);
        entry.set("birth", birth);
        entry.set("variety", variety);
        entry.set("description",description);
        entry.set("photo_url", photo_url);
        entry.set("on_sell",on_sell);        
        entry.set("price", price);
        entry.set("user_id",user_id);
        if (table.insert("key", entry) == 1) {
        // 成功
        ret_code=0;
        } else {
        // 失败? 无权限或者其他错误
        ret_code=-1;
        }
        emit RegisterPetEvent(ret_code, emit_description);
        return ret_code;
    }

    /*
    描述 : 宠物信息修改
    参数 ：
            pet_id : 宠物id
            name : 宠物名
            birth : 出生日期
            variety : 品种
            description : 描述
            photo_url : 图片url
            on_sell : 是否上架
            price : 价格
            user_id : 所有人id
            emit_description ：提交事件描述，是一个涵盖上述参数的string
    返回值：
            0  成功
            -1 失败
    */
    function updatePet(string pet_id,string name,string birth,string variety,string description,string photo_url,string on_sell,string price,string user_id,string emit_description) public returns(int256){
 
        Table table = tableFactory.openTable("t_pet");
        //添加数据
        Entry entry = table.newEntry();
	    entry.set("key", "0");
        entry.set("pet_id", pet_id);
        entry.set("name", name);
        entry.set("birth", birth);
        entry.set("variety", variety);
        entry.set("description",description);
        entry.set("photo_url", photo_url);
        entry.set("on_sell",on_sell);        
        entry.set("price", price);
        entry.set("user_id",user_id);

        condition = table.newCondition();
        condition.EQ("pet_id",pet_id);
        //这样写是为了减少函数内变量，避免出现Stack too deep错误
        if (table.update("key", entry, condition) == 1) {
        // 成功
         ret_code=0;
        } else {
        // 失败? 无权限或者其他错误
         ret_code=-1;
        }
         emit UpdatePetEvent(ret_code,emit_description);
         return ret_code;
    }

    /*
    描述 : 删除宠物
    参数 ：
            pet_id : 宠物id
            emit_description ：提交事件描述，是一个涵盖上述参数的string
    返回值：
            0  成功
            -1 失败
    */
    function removePet(string pet_id,string emit_description)public returns(int256){
        Table table = tableFactory.openTable("t_pet");
        condition = table.newCondition();
        condition.EQ("pet_id", pet_id);
        if(table.remove("key", condition)==1){
            ret_code=0;
        }else{
            ret_code=-1;
        }
        emit RemovePetEvent(ret_code,emit_description);
        return ret_code;
    }

    //根据petid查找
    //solidity参数限制，分成两次获取
    function selectByPetId1(string pet_id)
       public
       returns (string,string ,string  ,string   ,string)
     {
        Table table = tableFactory.openTable("t_pet");
        condition = table.newCondition();
        condition.EQ("pet_id",pet_id);
        Entries entries = table.select("key", condition);        
        Entry entry = entries.get(0);

        return (entry.getString('name'),entry.getString('birth'),entry.getString('variety'),
        entry.getString('description'),entry.getString('photo_url'));
     }
    function selectByPetId2(string pet_id)
       public
       returns (string ,string ,string)
     {
        Table table = tableFactory.openTable("t_pet");
        condition = table.newCondition();
        condition.EQ("pet_id",pet_id);
        Entries entries = table.select("key", condition);        
        Entry entry = entries.get(0);

        return (entry.getString('on_sell'),entry.getString('price'),entry.getString('user_id'));
     }


  

    //根据user_id查找
    //solidity参数限制，分成两次获取
    function selectByUserId1(string user_id)
       public
       returns (string[],string[],string[],string[])
     {
        Table table = tableFactory.openTable("t_pet");
        condition = table.newCondition();
        condition.EQ("user_id",user_id);
        Entries entries = table.select("key", condition);        

        string[] memory pet_id_list = new string[](uint256(entries.size()));
        string[] memory name_list = new string[](uint256(entries.size()));
        string[] memory birth_list = new string[](uint256(entries.size()));
        string[] memory variety_list = new string[](uint256(entries.size()));

        for (int256 i = 0; i < entries.size(); ++i) {
            Entry entry = entries.get(i);

            pet_id_list[uint256(i)] = entry.getString("pet_id");
            name_list[uint256(i)] = entry.getString("name");
            birth_list[uint256(i)] = entry.getString("birth");
            variety_list[uint256(i)] = entry.getString("variety");
        }

        return (pet_id_list,name_list,birth_list,variety_list);
     }
    function selectByUserId2(string user_id)
       public
       returns (string[] ,string[] ,string[] ,string[])
     {
        Table table = tableFactory.openTable("t_pet");
        condition = table.newCondition();
        condition.EQ("user_id",user_id);
        Entries entries = table.select("key", condition);        

        string[] memory description_list = new string[](uint256(entries.size()));
        string[] memory photo_url_list = new string[](uint256(entries.size()));
        string[] memory on_sell_list = new string[](uint256(entries.size()));
        string[] memory price_list = new string[](uint256(entries.size()));

        for (int256 i = 0; i < entries.size(); ++i) {
            Entry entry = entries.get(i);

            description_list[uint256(i)] = entry.getString("description");
            photo_url_list[uint256(i)] = entry.getString("photo_url");
            on_sell_list[uint256(i)] = entry.getString("on_sell");
            price_list[uint256(i)] = entry.getString("price");
        }

        return (description_list,photo_url_list,on_sell_list,price_list);
     }


    //查找在售宠物
    //solidity参数限制，分成两次获取
    function selectOnSell1()
       public
       returns (string[],string[],string[],string[])
     {
        Table table = tableFactory.openTable("t_pet");
        condition = table.newCondition();
        condition.EQ("on_sell","1");
        Entries entries = table.select("key", condition);        

        string[] memory pet_id_list = new string[](uint256(entries.size()));
        string[] memory name_list = new string[](uint256(entries.size()));
        string[] memory birth_list = new string[](uint256(entries.size()));
        string[] memory variety_list = new string[](uint256(entries.size()));

        for (int256 i = 0; i < entries.size(); ++i) {
            Entry entry = entries.get(i);

            pet_id_list[uint256(i)] = entry.getString("pet_id");
            name_list[uint256(i)] = entry.getString("name");
            birth_list[uint256(i)] = entry.getString("birth");
            variety_list[uint256(i)] = entry.getString("variety");
        }

        return (pet_id_list,name_list,birth_list,variety_list);
     }
    function selectOnSell2()
       public
       returns (string[] ,string[] ,string[] ,string[])
     {
        Table table = tableFactory.openTable("t_pet");
        condition = table.newCondition();
        condition.EQ("on_sell","1");
        Entries entries = table.select("key", condition);        

        string[] memory description_list = new string[](uint256(entries.size()));
        string[] memory photo_url_list = new string[](uint256(entries.size()));
        string[] memory price_list = new string[](uint256(entries.size()));
        string[] memory user_id_list = new string[](uint256(entries.size()));

        for (int256 i = 0; i < entries.size(); ++i) {
            Entry entry = entries.get(i);
            description_list[uint256(i)] = entry.getString("description");
            photo_url_list[uint256(i)] = entry.getString("photo_url");
            price_list[uint256(i)] = entry.getString("price");
            user_id_list[uint256(i)] = entry.getString("user_id");
        }

        return (description_list,photo_url_list,price_list,user_id_list);
     }
}