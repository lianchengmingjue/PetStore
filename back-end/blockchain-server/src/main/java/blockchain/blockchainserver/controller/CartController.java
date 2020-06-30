package blockchain.blockchainserver.controller;

import org.fisco.bcos.channel.client.Service;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.Keys;
import org.fisco.bcos.web3j.precompile.cns.CnsService;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.channel.ChannelEthereumService;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.fisco.bcos.web3j.protocol.exceptions.TransactionException;
import org.fisco.bcos.web3j.tx.gas.StaticGasProvider;
import org.fisco.bcos.web3j.tx.txdecode.BaseException;
import org.fisco.bcos.web3j.tx.txdecode.TransactionDecoder;
import org.fisco.bcos.web3j.tx.txdecode.TransactionDecoderFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigInteger;

/**
 * @description:
 * @author: RG
 * @create: 2020-06-26 11:29
 **/
@RestController
public class CartController {


    String abi ="[{\"constant\":false,\"inputs\":[{\"name\":\"user_id\",\"type\":\"string\"},{\"name\":\"pet_id\",\"type\":\"string\"},{\"name\":\"emit_description\",\"type\":\"string\"}],\"name\":\"insert\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"user_id\",\"type\":\"string\"},{\"name\":\"pet_id\",\"type\":\"string\"}],\"name\":\"unique\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"user_id\",\"type\":\"string\"},{\"name\":\"pet_id\",\"type\":\"string\"},{\"name\":\"emit_description\",\"type\":\"string\"}],\"name\":\"remove\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"user_id\",\"type\":\"string\"}],\"name\":\"select\",\"outputs\":[{\"name\":\"\",\"type\":\"string[]\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"ret\",\"type\":\"int256\"},{\"indexed\":false,\"name\":\"emit_describe\",\"type\":\"string\"}],\"name\":\"InsertEvent\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"ret\",\"type\":\"int256\"},{\"indexed\":false,\"name\":\"emit_describe\",\"type\":\"string\"}],\"name\":\"RemoveEvent\",\"type\":\"event\"}]";
    String bin="";

    String address="0x145beff648d23b7e641931f09b65b938345f7651";

    String contractName="Cart";

    @Value("${ficos.cart-version}")
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

    public org.fisco.bcos.contract.Cart accountInitial() throws Exception {
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
        return org.fisco.bcos.contract.Cart.load(address,web3j,credentials,new StaticGasProvider(gasPrice, gasLimit));
    }


    public String decodeJson(TransactionReceipt receipt) throws IOException, BaseException, TransactionException {
        TransactionDecoder txDecodeSampleDecoder = TransactionDecoderFactory.buildTransactionDecoder(abi, bin);
        String jsonResult = txDecodeSampleDecoder.decodeEventReturnJson(receipt.getLogs());
        System.out.println("返回值jsonResult：\n"+jsonResult);

        String jsonResultFromOutput=txDecodeSampleDecoder.decodeOutputReturnJson(receipt.getInput(),receipt.getOutput());
        System.out.println("返回值jsonResultFromOutput：\n"+jsonResultFromOutput);
        return jsonResultFromOutput;
    }

    @RequestMapping("/cart/insert")
    public String insert(String user_id, String pet_id) throws Exception {
        org.fisco.bcos.contract.Cart arbitration = accountInitial();
        System.out.println("insert");
        String emit_description="insert"+" "+user_id+" "+pet_id;
        TransactionReceipt receipt=arbitration.insert(user_id, pet_id,emit_description).send();
        return decodeJson(receipt);
    }

    @RequestMapping("/cart/remove")
    public String remove(String user_id, String pet_id) throws Exception {
        org.fisco.bcos.contract.Cart arbitration = accountInitial();
        System.out.println("remove");
        String emit_description="remove"+" "+user_id+" "+pet_id;
        TransactionReceipt receipt=arbitration.remove(user_id, pet_id,emit_description).send();
        return decodeJson(receipt);
    }

    @RequestMapping("/cart/select")
    public String select(String user_id) throws Exception {
        org.fisco.bcos.contract.Cart arbitration = accountInitial();
        System.out.println("select");
        TransactionReceipt receipt=arbitration.select(user_id).send();
        return decodeJson(receipt);
    }

    @RequestMapping("/cart/unique")
    public String unique(String user_id, String pet_id) throws Exception {
        org.fisco.bcos.contract.Cart arbitration = accountInitial();
        System.out.println("unique");
        TransactionReceipt receipt=arbitration.unique(user_id, pet_id).send();
        return decodeJson(receipt);
    }

}

