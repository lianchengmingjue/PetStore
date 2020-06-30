package blockchain.blockchainserver.controller;


import org.fisco.bcos.channel.client.Service;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.Keys;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.channel.ChannelEthereumService;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.fisco.bcos.web3j.tx.gas.StaticGasProvider;
import org.fisco.bcos.web3j.tx.txdecode.TransactionDecoder;
import org.fisco.bcos.web3j.tx.txdecode.TransactionDecoderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.Collection;

/**
 * @description:
 * @author: RG
 * @create: 2020-06-19 15:40
 **/

@RestController
public class HelloWorld {



    String abi ="[{\"constant\":false,\"inputs\":[{\"name\":\"user_id\",\"type\":\"string\"},{\"name\":\"emit_description\",\"type\":\"string\"}],\"name\":\"remove\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"user_id\",\"type\":\"string\"}],\"name\":\"uniqueId\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"user_id\",\"type\":\"string\"},{\"name\":\"password\",\"type\":\"string\"},{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"balance\",\"type\":\"string\"},{\"name\":\"administrator\",\"type\":\"string\"},{\"name\":\"emit_description\",\"type\":\"string\"}],\"name\":\"regist\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"user_id\",\"type\":\"string\"},{\"name\":\"password\",\"type\":\"string\"},{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"balance\",\"type\":\"string\"},{\"name\":\"administrator\",\"type\":\"string\"},{\"name\":\"emit_description\",\"type\":\"string\"}],\"name\":\"update\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"user_id\",\"type\":\"string\"}],\"name\":\"select\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"string\"},{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"ret\",\"type\":\"int256\"},{\"indexed\":false,\"name\":\"emit_describe\",\"type\":\"string\"}],\"name\":\"RegisterEvent\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"ret\",\"type\":\"int256\"},{\"indexed\":false,\"name\":\"emit_describe\",\"type\":\"string\"}],\"name\":\"UpdateEvent\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"ret\",\"type\":\"int256\"},{\"indexed\":false,\"name\":\"emit_describe\",\"type\":\"string\"}],\"name\":\"RemoveEvent\",\"type\":\"event\"}]";

    String bin="";


    /**
     * description:
     * @params [user_id, password, name, balance, administrator]
     * @return java.lang.String
     */
    @RequestMapping("/testConnect")
    public String test(){
        return "1";
    }

    /**
     * description:
     * @params []
     * @return java.math.BigInteger
     */
    @RequestMapping("/getBlockNumber")
    public BigInteger getBlockNumber() throws Exception {
        System.out.println("开始与节点建立连接");
        //读取配置文件，SDK与区块链节点建立连接
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        System.out.println("开始与节点建立连接——2");
        Service service = context.getBean(Service.class);
        System.out.println("开始与节点建立连接——3");
        service.run();
        System.out.println("开始与节点建立连接——4");
        ChannelEthereumService channelEthereumService = new ChannelEthereumService();
        System.out.println("开始与节点建立连接——5");
        channelEthereumService.setChannelService(service);


        System.out.println("获取Web3j对象");
        //获取Web3j对象
        Web3j web3j = Web3j.build(channelEthereumService, service.getGroupId());
        System.out.println("调用API接口getBlockNumber");
        //通过Web3j对象调用API接口getBlockNumber
        BigInteger blockNumber = web3j.getBlockNumber().send().getBlockNumber();
        System.out.println("块高："+blockNumber);

        return blockNumber;
    }


}

