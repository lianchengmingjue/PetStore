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

    struct Pet{
        string pet_id;
        string name;
        string birth;
        string variety;
        string description;
        string photo_url;
        string on_sell;
        int256 price;
        string user_id;
    }
    Pet pet;

    struct Pets{
        string[] pet_id_list;
        string[] name_list;
        string[] birth_list;
        string[] variety_list;
        string[] description_list;
        string[] photo_url_list;
        string[] on_sell_list;
        int256[] price_list;
        string[] user_id_list;
    }
    Pets pets;

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
    function registPet(string pet_id,string name,string birth,string variety,string description,string photo_url,string on_sell,int256 price,string user_id,string emit_description) public returns(int256){
        
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
    function updatePet(string pet_id,string name,string birth,string variety,string description,string photo_url,string on_sell,int256 price,string user_id,string emit_description) public returns(int256){
 
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


	
    /*
    描述 : 按照pet_id查找宠物信息
    参数 ：
            pet_id : 宠物id
    返回值：
            该pet的全部信息
    */
    function selectByPetId(string pet_id) public returns (string,string,string,string,string,string,string,int256,string){
        Table table = tableFactory.openTable("t_pet");
        condition = table.newCondition();
        condition.EQ("pet_id",pet_id);
        Entries entries = table.select("key", condition);        
        Entry entry = entries.get(0);
        pet=Pet(entry.getString('pet_id'),entry.getString('name'),entry.getString('birth'),entry.getString('variety'),
           entry.getString('description'),entry.getString('photo_url'),entry.getString('on_sell'),entry.getInt('price')
       	   ,entry.getString('user_id'));
        return (pet.pet_id,pet.name,pet.birth,pet.variety,pet.description,pet.photo_url,pet.on_sell,pet.price,pet.user_id);
    }
  

	
    /*
    描述 : 按照user_id返回信息，即某用户所拥有的全部宠物
    参数 ：
            user_id : 用户id
    返回值：
            符合条件的pet的全部信息
    */
    function selectByUserId(string user_id)
       public
       returns (string[],string[],string[],string[],string[],string[],string[],int256[],string[])
    {
	    Table table = tableFactory.openTable("t_pet");
        condition = table.newCondition();
        condition.EQ("user_id",user_id);
        Entries entries = table.select("key", condition); 

        pets.pet_id_list = new string[](uint256(entries.size()));
        pets.name_list= new string[](uint256(entries.size()));
        pets.birth_list = new string[](uint256(entries.size()));
        pets.variety_list = new string[](uint256(entries.size()));
        pets.description_list = new string[](uint256(entries.size()));
        pets.photo_url_list = new string[](uint256(entries.size()));
        pets.on_sell_list = new string[](uint256(entries.size()));
        pets.price_list = new int256[](uint256(entries.size()));
        pets.user_id_list = new string[](uint256(entries.size()));

        for (int256 i = 0; i < entries.size(); ++i) {
            Entry entry = entries.get(i);

            pets.pet_id_list[uint256(i)] = entry.getString("pet_id");
            pets.name_list[uint256(i)] = entry.getString("name");
            pets.birth_list[uint256(i)] = entry.getString("birth");
            pets.variety_list[uint256(i)] = entry.getString("variety");
            pets.description_list[uint256(i)] = entry.getString("description");
            pets.photo_url_list[uint256(i)] = entry.getString("photo_url");
            pets.on_sell_list[uint256(i)] = entry.getString("on_sell");
            pets.price_list[uint256(i)] = entry.getInt("price");
            pets.user_id_list[uint256(i)] = entry.getString("user_id");
            }
        return (pets.pet_id_list,pets.name_list,pets.birth_list, pets.variety_list,pets.description_list
        ,pets.photo_url_list,pets.on_sell_list,pets.price_list,pets.user_id_list);
    }



    /*
    描述 : 按照on_sell返回信息，即在售的全部宠物
    参数 ：
            
    返回值：
            在售宠物的全部信息
    */
    function selectOnSell()
	public
       returns (string[],string[],string[],string[],string[],string[],string[],int256[],string[])
    {
        Table table = tableFactory.openTable("t_pet");
        condition = table.newCondition();
        condition.EQ("on_sell","1");
        Entries entries = table.select("key", condition);   

        pets.pet_id_list = new string[](uint256(entries.size()));
        pets.name_list= new string[](uint256(entries.size()));
        pets.birth_list = new string[](uint256(entries.size()));
        pets.variety_list = new string[](uint256(entries.size()));
        pets.description_list = new string[](uint256(entries.size()));
        pets.photo_url_list = new string[](uint256(entries.size()));
        pets.on_sell_list = new string[](uint256(entries.size()));
        pets.price_list = new int256[](uint256(entries.size()));
        pets.user_id_list = new string[](uint256(entries.size()));

        for (int256 i = 0; i < entries.size(); ++i) {
            Entry entry = entries.get(i);

            pets.pet_id_list[uint256(i)] = entry.getString("pet_id");
            pets.name_list[uint256(i)] = entry.getString("name");
            pets.birth_list[uint256(i)] = entry.getString("birth");
            pets.variety_list[uint256(i)] = entry.getString("variety");
            pets.description_list[uint256(i)] = entry.getString("description");
            pets.photo_url_list[uint256(i)] = entry.getString("photo_url");
            pets.on_sell_list[uint256(i)] = entry.getString("on_sell");
            pets.price_list[uint256(i)] = entry.getInt("price");
            pets.user_id_list[uint256(i)] = entry.getString("user_id");
            }
        return (pets.pet_id_list,pets.name_list,pets.birth_list, pets.variety_list,pets.description_list
        ,pets.photo_url_list,pets.on_sell_list,pets.price_list,pets.user_id_list);
     }


}