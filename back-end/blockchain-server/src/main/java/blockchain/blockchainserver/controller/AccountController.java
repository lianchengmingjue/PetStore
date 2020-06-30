package blockchain.blockchainserver.controller;

import com.alibaba.fastjson.JSONObject;
import org.fisco.bcos.web3j.precompile.cns.CnsService;
import org.fisco.bcos.web3j.protocol.exceptions.TransactionException;

import org.fisco.bcos.web3j.tuples.generated.Tuple6;
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

import java.io.IOException;
import java.math.BigInteger;


/**
 * @description:
 * @author: RG
 * @create: 2020-06-19 15:40
 **/

@RestController
public class AccountController {

    String abi ="[{\"constant\":false,\"inputs\":[{\"name\":\"user_id\",\"type\":\"string\"},{\"name\":\"password\",\"type\":\"string\"},{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"balance\",\"type\":\"int256\"},{\"name\":\"photo_url\",\"type\":\"string\"},{\"name\":\"administrator\",\"type\":\"string\"},{\"name\":\"emit_description\",\"type\":\"string\"}],\"name\":\"update\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"user_id\",\"type\":\"string\"},{\"name\":\"emit_description\",\"type\":\"string\"}],\"name\":\"remove\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"user_id\",\"type\":\"string\"}],\"name\":\"uniqueId\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"user_id\",\"type\":\"string\"},{\"name\":\"password\",\"type\":\"string\"},{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"balance\",\"type\":\"int256\"},{\"name\":\"photo_url\",\"type\":\"string\"},{\"name\":\"administrator\",\"type\":\"string\"},{\"name\":\"emit_description\",\"type\":\"string\"}],\"name\":\"regist\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"user_id\",\"type\":\"string\"}],\"name\":\"select\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"int256\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"ret\",\"type\":\"int256\"},{\"indexed\":false,\"name\":\"emit_describe\",\"type\":\"string\"}],\"name\":\"RegisterEvent\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"ret\",\"type\":\"int256\"},{\"indexed\":false,\"name\":\"emit_describe\",\"type\":\"string\"}],\"name\":\"UpdateEvent\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"ret\",\"type\":\"int256\"},{\"indexed\":false,\"name\":\"emit_describe\",\"type\":\"string\"}],\"name\":\"RemoveEvent\",\"type\":\"event\"}]";
    String bin="";

    String address="0x145beff648d23b7e641931f09b65b938345f7651";

    String contractName="Account";

    @Value("${ficos.account-version}")
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

    public org.fisco.bcos.contract.Account accountInitial() throws Exception {
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
        address=contractAddress;
        System.out.println("account地址: "+address);
        BigInteger gasPrice = new BigInteger("30000");
        BigInteger gasLimit = new BigInteger("30000");
        System.out.println("创建account");
        return org.fisco.bcos.contract.Account.load(address,web3j,credentials,new StaticGasProvider(gasPrice, gasLimit));
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
     * description: 注册
     * @params [user_id, password, name, balance, administrator]
     * @return java.lang.String
     */
    @RequestMapping("/account/regist")
    public String regist(String user_id, String password, String name, BigInteger balance, String photo_url, String administrator) throws Exception {
        org.fisco.bcos.contract.Account account=accountInitial();
        String emit_description="regist"+" "+user_id+" "+password+" "+name+" "+ balance +" "+photo_url+" "+administrator;
        System.out.println("regist: "+emit_description);
        TransactionReceipt receipt=account.regist(user_id,password,name,balance,photo_url,administrator,emit_description).send();
        return decodeJson(receipt);
    }

    /**
     * description: 用于删除用户
     * @params [user_id]
     * @return java.lang.String
     */
    @RequestMapping("/account/remove")
    public String remove(String user_id) throws Exception {
        org.fisco.bcos.contract.Account account=accountInitial();
        System.out.println("remove");
        String emit_description="remove"+" "+user_id+" ";
        TransactionReceipt receipt=account.remove(user_id,emit_description).send();
        return decodeJson(receipt);
    }

    /**
     * description: 用于修改账户信息
     * @params [user_id, password, name, balance, administrator]
     * @return java.lang.String
     */
    @RequestMapping("/account/update")
    public String update(String user_id, String password, String name, BigInteger balance, String photo_url, String administrator) throws Exception {
        org.fisco.bcos.contract.Account account=accountInitial();
        System.out.println("update");
        String emit_description="update"+" "+user_id+" "+user_id+" "+ password+" "+name+" "+balance+" "+photo_url+" "+administrator;
        TransactionReceipt receipt=account.update(user_id, password, name, balance,photo_url, administrator,emit_description).send();
        return decodeJson(receipt);
    }

    /**
     * description:用于查询用户信息
     * @params [user_id]
     * @return java.lang.String
     */
    @RequestMapping("/account/select")
    public String select(String user_id) throws Exception {
        org.fisco.bcos.contract.Account account=accountInitial();
        System.out.println("select");
        Tuple6<BigInteger, String, String, BigInteger, String, String> receipt=account.select(user_id).send();
        BigInteger ret_code=receipt.getValue1();
        String value2 = receipt.getValue2();
        String value3 = receipt.getValue3();
        BigInteger value4 = receipt.getValue4();
        String value5 = receipt.getValue5();
        String value6 = receipt.getValue6();
        JSONObject selectJson=new JSONObject();
        selectJson.put("ret_code",ret_code);
        selectJson.put("password",value2);
        selectJson.put("name",value3);
        selectJson.put("balance",value4);
        selectJson.put("photo_url",value5);
        selectJson.put("administrator",value6);
        System.out.println("select的结果: /n"+selectJson.toString());
        return selectJson.toString();
    }

    /**
     * description:验证账户id是否存在
     * @params [user_id]
     * @return java.lang.String
     */
    @RequestMapping("/account/uniqueId")
    public String uniqueId(String user_id) throws Exception {
        org.fisco.bcos.contract.Account account=accountInitial();
        System.out.println("uniqueId");
        TransactionReceipt receipt=account.uniqueId(user_id).send();
        System.out.println("uniqueId返回： "+receipt);

        return decodeJson(receipt);
    }
}

