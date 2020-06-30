package blockchain.blockchainserver.controller;

import com.alibaba.fastjson.JSONObject;
import org.fisco.bcos.channel.client.Service;
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
public class TransactionController {
    String abi ="[{\"constant\":true,\"inputs\":[],\"name\":\"allTransactions\",\"outputs\":[{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"int256[]\"},{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"transaction_id\",\"type\":\"string\"},{\"name\":\"transaction_time\",\"type\":\"string\"},{\"name\":\"price\",\"type\":\"int256\"},{\"name\":\"seller_id\",\"type\":\"string\"},{\"name\":\"buyer_id\",\"type\":\"string\"},{\"name\":\"pet_id\",\"type\":\"string\"}],\"name\":\"transfer\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":true,\"inputs\":[{\"name\":\"name\",\"type\":\"string\"}],\"name\":\"select\",\"outputs\":[{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"int256[]\"},{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"},{\"name\":\"\",\"type\":\"string[]\"}],\"payable\":false,\"stateMutability\":\"view\",\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"ret\",\"type\":\"int256\"},{\"indexed\":false,\"name\":\"from_account\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"to_account\",\"type\":\"string\"},{\"indexed\":false,\"name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"TransferEvent\",\"type\":\"event\"}]";
    String bin="";

    String address="0x145beff648d23b7e641931f09b65b938345f7651";

    String contractName="Transaction";

    @Value("${ficos.transaction-version}")
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

    public org.fisco.bcos.contract.Transaction transactionInitial() throws Exception {
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
        System.out.println("Transaction地址: "+address);
        BigInteger gasPrice = new BigInteger("30000");
        BigInteger gasLimit = new BigInteger("30000");
        System.out.println("创建Transaction");
        return org.fisco.bcos.contract.Transaction.load(address,web3j,credentials,new StaticGasProvider(gasPrice, gasLimit));
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
     * description: 进行一次交易
     * @params [user_id, password, name, balance, administrator]
     * @return java.lang.String
     */
    @RequestMapping("/transaction/transfer")
    public String transfer(String transaction_id, String transaction_time, BigInteger price, String seller_id, String buyer_id, String pet_id) throws Exception {
        org.fisco.bcos.contract.Transaction transaction=transactionInitial();
        System.out.println("transfer");
        TransactionReceipt receipt=transaction.transfer(transaction_id, transaction_time, price, seller_id, buyer_id, pet_id).send();
        return decodeJson(receipt);
    }

    @RequestMapping("/transaction/select")
    public String select(String name) throws Exception {
        org.fisco.bcos.contract.Transaction transaction=transactionInitial();
        System.out.println("select");
        Tuple6<List<String>, List<String>, List<BigInteger>, List<String>, List<String>, List<String>> receipt = transaction.select(name).send();
        List<String> value1=receipt.getValue1();
        List<String> value2 = receipt.getValue2();
        List<BigInteger> value3 = receipt.getValue3();
        List<String> value4 = receipt.getValue4();
        List<String> value5 = receipt.getValue5();
        List<String> value6 = receipt.getValue6();

        JSONObject selectJson=new JSONObject();
        selectJson.put("transaction_id",value1);
        selectJson.put("transaction_time",value2);
        selectJson.put("price",value3);
        selectJson.put("seller_id",value4);
        selectJson.put("buyer_id",value5);
        selectJson.put("pet_id",value6);
        System.out.println("select的结果: /n"+selectJson.toString());
        return selectJson.toString();
    }


    @RequestMapping("/transaction/allTransactions")
    public String allTransactions() throws Exception {
        org.fisco.bcos.contract.Transaction transaction=transactionInitial();
        System.out.println("allTransactions");
        Tuple6<List<String>, List<String>, List<BigInteger>, List<String>, List<String>, List<String>> receipt=transaction.allTransactions().send();

        List<String> value1=receipt.getValue1();
        List<String> value2 = receipt.getValue2();
        List<BigInteger> value3 = receipt.getValue3();
        List<String> value4 = receipt.getValue4();
        List<String> value5 = receipt.getValue5();
        List<String> value6 = receipt.getValue6();

        JSONObject selectJson=new JSONObject();
        selectJson.put("transaction_id",value1);
        selectJson.put("transaction_time",value2);
        selectJson.put("price",value3);
        selectJson.put("seller_id",value4);
        selectJson.put("buyer_id",value5);
        selectJson.put("pet_id",value6);
        System.out.println("select的结果: /n"+selectJson.toString());
        return selectJson.toString();
    }
}

