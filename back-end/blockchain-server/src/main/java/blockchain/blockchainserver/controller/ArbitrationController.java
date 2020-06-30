package blockchain.blockchainserver.controller;

import com.alibaba.fastjson.JSONObject;
import org.fisco.bcos.channel.client.Service;
import org.fisco.bcos.contract.Arbitration;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.Keys;
import org.fisco.bcos.web3j.precompile.cns.CnsService;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.channel.ChannelEthereumService;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.fisco.bcos.web3j.protocol.exceptions.TransactionException;
import org.fisco.bcos.web3j.tuples.generated.Tuple6;
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
import java.util.List;

/**
 * @description:
 * @author: RG
 * @create: 2020-06-23 14:55
 **/
@RestController
public class ArbitrationController {
    String abi ="[{\"constant\":false,\"inputs\":[{\"name\":\"a_id\",\"type\":\"string\"},{\"name\":\"t_id\",\"type\":\"string\"},{\"name\":\"time\",\"type\":\"string\"},{\"name\":\"desc\",\"type\":\"string\"},{\"name\":\"u_id\",\"type\":\"string\"}],\"name\":\"request\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"a_id\",\"type\":\"string\"},{\"name\":\"flag\",\"type\":\"int256\"}],\"name\":\"verify\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"allArbitrations\",\"outputs\":[{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"id\",\"type\":\"string\"}],\"name\":\"select\",\"outputs\":[{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"ret\",\"type\":\"int256\"},{\"indexed\":false,\"name\":\"from_account\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"to_account\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"ArbitrationEvent\",\"type\":\"event\"}]";
    String bin="";
    String address="0x145beff648d23b7e641931f09b65b938345f7651";

    String contractName="Arbitration";

    @Value("${ficos.arbitrationController-version}")
    String versionOfContract="1.0";


    public Web3j Web3jInitial() throws Exception {
        System.out.println("versionOfContract"+versionOfContract);
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

    public org.fisco.bcos.contract.Arbitration accountInitial() throws Exception {
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
        System.out.println("创建Arbitration");
        return org.fisco.bcos.contract.Arbitration.load(address,web3j,credentials,new StaticGasProvider(gasPrice, gasLimit));
    }

    public String decodeJson(TransactionReceipt receipt) throws IOException, BaseException, TransactionException {
        TransactionDecoder txDecodeSampleDecoder = TransactionDecoderFactory.buildTransactionDecoder(abi, bin);
        String jsonResult = txDecodeSampleDecoder.decodeEventReturnJson(receipt.getLogs());
        System.out.println("返回值jsonResult：\n"+jsonResult);

        String jsonResultFromOutput=txDecodeSampleDecoder.decodeOutputReturnJson(receipt.getInput(),receipt.getOutput());
        System.out.println("返回值jsonResultFromOutput：\n"+jsonResultFromOutput);
        return jsonResultFromOutput;
    }


    @RequestMapping("/arbitration/request")
    public String request(String a_id, String t_id, String time, String desc, String u_id) throws Exception {
        Arbitration arbitration = accountInitial();
        System.out.println("request");
        TransactionReceipt receipt=arbitration.request( a_id,  t_id,  time,  desc,  u_id).send();
        return decodeJson(receipt);
    }

    @RequestMapping("/arbitration/verify")
    public String verify(String a_id, BigInteger flag) throws Exception {
        Arbitration arbitration = accountInitial();
        System.out.println("verify");
        TransactionReceipt receipt=arbitration.verify( a_id,  flag).send();
        return decodeJson(receipt);
    }

    @RequestMapping("/arbitration/select")
    public String select(String id) throws Exception {
        Arbitration arbitration = accountInitial();
        System.out.println("select");
        Tuple6<List<String>, List<String>, List<String>, List<String>, List<String>, List<String>> receipt=arbitration.select(id).send();
        List<String> value1=receipt.getValue1();
        List<String> value2 = receipt.getValue2();
        List<String> value3 = receipt.getValue3();
        List<String> value4 = receipt.getValue4();
        List<String> value5 = receipt.getValue5();
        List<String> value6 = receipt.getValue6();
        JSONObject selectJson=new JSONObject();
        selectJson.put("a_id",value1);
        selectJson.put("t_id",value2);
        selectJson.put("time",value3);
        selectJson.put("desc",value4);
        selectJson.put("comp",value5);
        selectJson.put("u_id",value6);
        System.out.println("select的结果: /n"+selectJson.toString());
        return selectJson.toString();
    }

    @RequestMapping("/arbitration/allArbitrations")
    public String allArbitrations() throws Exception {
        Arbitration arbitration = accountInitial();
        System.out.println("allArbitrations");
        Tuple6<List<String>, List<String>, List<String>, List<String>, List<String>, List<String>> receipt=arbitration.allArbitrations().send();
        List<String> value1=receipt.getValue1();
        List<String> value2 = receipt.getValue2();
        List<String> value3 = receipt.getValue3();
        List<String> value4 = receipt.getValue4();
        List<String> value5 = receipt.getValue5();
        List<String> value6 = receipt.getValue6();
        JSONObject selectJson=new JSONObject();
        selectJson.put("a_id",value1);
        selectJson.put("t_id",value2);
        selectJson.put("time",value3);
        selectJson.put("desc",value4);
        selectJson.put("comp",value5);
        selectJson.put("u_id",value6);
        System.out.println("allArbitrations的结果: /n"+selectJson.toString());
        return selectJson.toString();
    }
}

