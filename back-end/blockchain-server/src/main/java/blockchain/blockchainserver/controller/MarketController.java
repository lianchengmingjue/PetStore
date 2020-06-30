package blockchain.blockchainserver.controller;
import org.fisco.bcos.web3j.protocol.exceptions.TransactionException;
import org.fisco.bcos.web3j.tx.txdecode.BaseException;
import org.springframework.beans.factory.annotation.Value;


import org.fisco.bcos.channel.client.Service;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.Keys;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.channel.ChannelEthereumService;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.fisco.bcos.web3j.tx.gas.StaticGasProvider;
import org.fisco.bcos.web3j.tx.txdecode.TransactionDecoder;
import org.fisco.bcos.web3j.tx.txdecode.TransactionDecoderFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.fisco.bcos.web3j.precompile.cns.*;
import java.io.IOException;
import java.math.BigInteger;


/**
 * @description:
 * @author: RG
 * @create: 2020-06-21 15:55
 **/
@RestController
public class MarketController {
    String abi ="[{\"constant\":false,\"inputs\":[],\"name\":\"selectOnSell\",\"outputs\":[{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"int256[]\"},{\"name\":\"\",\"type\":\"string[]\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"user_id\",\"type\":\"string\"}],\"name\":\"selectByUserId\",\"outputs\":[{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"int256[]\"},{\"name\":\"\",\"type\":\"string[]\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"pet_id\",\"type\":\"string\"},{\"name\":\"emit_description\",\"type\":\"string\"}],\"name\":\"removePet\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"pet_id\",\"type\":\"string\"}],\"name\":\"uniqueId\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"pet_id\",\"type\":\"string\"},{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"birth\",\"type\":\"string\"},{\"name\":\"variety\",\"type\":\"string\"},{\"name\":\"description\",\"type\":\"string\"},{\"name\":\"photo_url\",\"type\":\"string\"},{\"name\":\"on_sell\",\"type\":\"string\"},{\"name\":\"price\",\"type\":\"int256\"},{\"name\":\"user_id\",\"type\":\"string\"},{\"name\":\"emit_description\",\"type\":\"string\"}],\"name\":\"updatePet\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"pet_id\",\"type\":\"string\"}],\"name\":\"selectByPetId\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"int256\"},{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"pet_id\",\"type\":\"string\"},{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"birth\",\"type\":\"string\"},{\"name\":\"variety\",\"type\":\"string\"},{\"name\":\"description\",\"type\":\"string\"},{\"name\":\"photo_url\",\"type\":\"string\"},{\"name\":\"on_sell\",\"type\":\"string\"},{\"name\":\"price\",\"type\":\"int256\"},{\"name\":\"user_id\",\"type\":\"string\"},{\"name\":\"emit_description\",\"type\":\"string\"}],\"name\":\"registPet\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"ret\",\"type\":\"int256\"},{\"indexed\":false,\"name\":\"emit_describe\",\"type\":\"string\"}],\"name\":\"RegisterPetEvent\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"ret\",\"type\":\"int256\"},{\"indexed\":false,\"name\":\"emit_describe\",\"type\":\"string\"}],\"name\":\"UpdatePetEvent\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"ret\",\"type\":\"int256\"},{\"indexed\":false,\"name\":\"emit_describe\",\"type\":\"string\"}],\"name\":\"RemovePetEvent\",\"type\":\"event\"}]";

    String bin="";

    String marketAddress="0x3036e045fac55653f29f28135c86bc0635e23b17";

    String contractName="Market";

    @Value("${ficos.market-version}")
    String versionOfContract="1.0";

    public Web3j Web3jInitial() throws Exception {
        //读取配置文件，SDK与区块链节点建立连接
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        Service service = context.getBean(Service.class);
        service.run();
        ChannelEthereumService channelEthereumService = new ChannelEthereumService();
        channelEthereumService.setChannelService(service);
        System.out.println("获取Web3j对象");

        //获取Web3j对象
        return Web3j.build(channelEthereumService, service.getGroupId());

    }

    public org.fisco.bcos.contract.Market marketInitial() throws Exception {

        Web3j web3j=Web3jInitial();
        Credentials credentials = Credentials.create(Keys.createEcKeyPair());

        CnsService cnsResolver = new CnsService(web3j, credentials);
        String contractNameAndVersion=contractName+":"+versionOfContract;
        String contractAddress = "";
        try {
            contractAddress =
                    cnsResolver.getAddressByContractNameAndVersion(contractNameAndVersion);
        }catch (Exception e){
            System.out.println("获取地址失败");
        }
        marketAddress=contractAddress;
        System.out.println("market地址: "+marketAddress);
        BigInteger gasPrice = new BigInteger("30000");
        BigInteger gasLimit = new BigInteger("30000");
        System.out.println("创建account");
        return org.fisco.bcos.contract.Market.load(marketAddress,web3j,credentials,new StaticGasProvider(gasPrice, gasLimit));

    }


    public String decodeJson(TransactionReceipt receipt) throws IOException, BaseException, TransactionException {
        TransactionDecoder txDecodeSampleDecoder = TransactionDecoderFactory.buildTransactionDecoder(abi, bin);
        String jsonResult = txDecodeSampleDecoder.decodeEventReturnJson(receipt.getLogs());
        System.out.println("返回值jsonResult：\n"+jsonResult);

        String jsonResultFromOutput=txDecodeSampleDecoder.decodeOutputReturnJson(receipt.getInput(),receipt.getOutput());
        System.out.println("返回值jsonResultFromOutput：\n"+jsonResultFromOutput);
        return jsonResultFromOutput;
    }

    /**
     * description:用于宠物注册
     * @params [pet_id, name, birth, variety, description, photo_url, on_sell, price, user_id]
     * @return java.lang.String
     */
    @RequestMapping("/market/registPet")
    public String registPet(String pet_id, String name, String birth, String variety, String description, String photo_url, String on_sell, BigInteger price, String user_id) throws Exception {
        org.fisco.bcos.contract.Market market=marketInitial();
        System.out.println("registPet");
        String emit_description="registPet"+" "+pet_id+" "+name+" "+birth+" "+variety+" "+description+" "+photo_url+" "+on_sell+" "+price+" "+user_id;
        TransactionReceipt receipt=market.registPet(pet_id,name,birth,variety,description,photo_url,on_sell,price,user_id,emit_description).send();
        return decodeJson(receipt);
    }

    /**
     * description:用于删除宠物
     * @params [pet_id, name, birth, variety, description, photo_url, on_sell, price, user_id]
     * @return java.lang.String
     */
    @RequestMapping("/market/updatePet")
    public String updatePet(String pet_id, String name, String birth, String variety, String description, String photo_url, String on_sell, BigInteger price, String user_id) throws Exception {
        org.fisco.bcos.contract.Market market=marketInitial();
        System.out.println("updatePet");
        String emit_description="updatePet"+" "+pet_id+" "+name+" "+birth+" "+variety+" "+description+" "+photo_url+" "+on_sell+" "+price+" "+user_id;
        TransactionReceipt receipt=market.updatePet(pet_id,name,birth,variety,description,photo_url,on_sell,price,user_id,emit_description).send();
        return decodeJson(receipt);
    }

    /**
     * description:用于删除宠物
     * @params [pet_id]
     * @return java.lang.String
     */
    @RequestMapping("/market/removePet")
    public String removePet(String pet_id) throws Exception {
        org.fisco.bcos.contract.Market market=marketInitial();
        System.out.println("removePet");
        String emit_description="removePet"+" "+pet_id;
        TransactionReceipt receipt=market.removePet(pet_id,emit_description).send();
        return decodeJson(receipt);
    }

    /**
     * description:按照petID查询单条宠物信息
     * @params [pet_id]
     * @return java.lang.String
     */
    @RequestMapping("/market/selectByPetId")
    public String selectByPetId1(String pet_id) throws Exception {
        org.fisco.bcos.contract.Market market=marketInitial();
        System.out.println("selectByPetId");
        String emit_description="selectByPetId"+" "+pet_id;
        TransactionReceipt receipt=market.selectByPetId(pet_id).send();
        return decodeJson(receipt);
    }


    /**
     * description:按照userID查询某用户拥有的宠物信息
     * @params [user_id]
     * @return java.lang.String
     */
    @RequestMapping("/market/selectByUserId")
    public String selectByUserId(String user_id) throws Exception {
        org.fisco.bcos.contract.Market market=marketInitial();
        System.out.println("selectByUserId");
        String emit_description="selectByUserId"+" "+user_id;
        TransactionReceipt receipt=market.selectByUserId(user_id).send();
        return decodeJson(receipt);
    }

    /**
     * description:查询上架宠物信息
     * @params []
     * @return java.lang.String
     */
    @RequestMapping("/market/selectOnSell")
    public String selectOnSell() throws Exception {
        org.fisco.bcos.contract.Market market=marketInitial();
        System.out.println("selectOnSell");
        TransactionReceipt receipt=market.selectOnSell().send();
        return decodeJson(receipt);
    }

    /**
     * description:验证宠物id是否存在
     * @params [pet_id]
     * @return java.lang.String
     */
    @RequestMapping("/market/uniqueId")
    public String uniqueId(String pet_id) throws Exception {
        org.fisco.bcos.contract.Market market=marketInitial();
        System.out.println("uniqueId");
        TransactionReceipt receipt=market.uniqueId(pet_id).send();
        return decodeJson(receipt);
    }
}

