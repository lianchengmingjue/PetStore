package org.fisco.bcos.contract;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.fisco.bcos.channel.client.TransactionSucCallback;
import org.fisco.bcos.channel.event.filter.EventLogPushWithDecodeCallback;
import org.fisco.bcos.web3j.abi.EventEncoder;
import org.fisco.bcos.web3j.abi.FunctionReturnDecoder;
import org.fisco.bcos.web3j.abi.TypeReference;
import org.fisco.bcos.web3j.abi.datatypes.DynamicArray;
import org.fisco.bcos.web3j.abi.datatypes.Event;
import org.fisco.bcos.web3j.abi.datatypes.Function;
import org.fisco.bcos.web3j.abi.datatypes.Type;
import org.fisco.bcos.web3j.abi.datatypes.Utf8String;
import org.fisco.bcos.web3j.abi.datatypes.generated.Int256;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.protocol.Web3j;
import org.fisco.bcos.web3j.protocol.core.RemoteCall;
import org.fisco.bcos.web3j.protocol.core.methods.response.Log;
import org.fisco.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.fisco.bcos.web3j.tuples.generated.Tuple1;
import org.fisco.bcos.web3j.tuples.generated.Tuple2;
import org.fisco.bcos.web3j.tuples.generated.Tuple3;
import org.fisco.bcos.web3j.tx.Contract;
import org.fisco.bcos.web3j.tx.TransactionManager;
import org.fisco.bcos.web3j.tx.gas.ContractGasProvider;
import org.fisco.bcos.web3j.tx.txdecode.TransactionDecoder;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.fisco.bcos.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version none.
 */
@SuppressWarnings("unchecked")
public class Cart extends Contract {
    public static final String[] BINARY_ARRAY = {"60806040523480156200001157600080fd5b506200002b62000031640100000000026401000000009004565b6200027f565b6110016000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166356004b6a6040518163ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401620000e99062000229565b602060405180830381600087803b1580156200010457600080fd5b505af115801562000119573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052506200013f919081019062000158565b50565b600062000150825162000275565b905092915050565b6000602082840312156200016b57600080fd5b60006200017b8482850162000142565b91505092915050565b6000600682527f7065745f696400000000000000000000000000000000000000000000000000006020830152604082019050919050565b6000600682527f745f6361727400000000000000000000000000000000000000000000000000006020830152604082019050919050565b6000600782527f757365725f6964000000000000000000000000000000000000000000000000006020830152604082019050919050565b600060608201905081810360008301526200024481620001bb565b905081810360208301526200025981620001f2565b905081810360408301526200026e8162000184565b9050919050565b6000819050919050565b6118fe806200028f6000396000f300608060405260043610610062576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680632fe99bdc14610067578063aa623185146100a4578063da40fa77146100e1578063fcd7e3c11461011e575b600080fd5b34801561007357600080fd5b5061008e60048036036100899190810190611383565b61015b565b60405161009b91906115e8565b60405180910390f35b3480156100b057600080fd5b506100cb60048036036100c69190810190611317565b6104ff565b6040516100d891906115e8565b60405180910390f35b3480156100ed57600080fd5b5061010860048036036101039190810190611383565b610880565b60405161011591906115e8565b60405180910390f35b34801561012a57600080fd5b5061014560048036036101409190810190611295565b610ba0565b60405161015291906115c6565b60405180910390f35b60008060008060008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663f23f63c96040518163ffffffff167c01000000000000000000000000000000000000000000000000000000000281526004016101d7906116e8565b602060405180830381600087803b1580156101f157600080fd5b505af1158015610205573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052506102299190810190611243565b92508273ffffffffffffffffffffffffffffffffffffffff166313db93466040518163ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401602060405180830381600087803b15801561028f57600080fd5b505af11580156102a3573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052506102c7919081019061121a565b91508173ffffffffffffffffffffffffffffffffffffffff1663e942b516896040518263ffffffff167c010000000000000000000000000000000000000000000000000000000002815260040161031e9190611708565b600060405180830381600087803b15801561033857600080fd5b505af115801561034c573d6000803e3d6000fd5b505050508173ffffffffffffffffffffffffffffffffffffffff1663e942b516886040518263ffffffff167c01000000000000000000000000000000000000000000000000000000000281526004016103a591906116b3565b600060405180830381600087803b1580156103bf57600080fd5b505af11580156103d3573d6000803e3d6000fd5b505050508273ffffffffffffffffffffffffffffffffffffffff166331afac3689846040518363ffffffff167c010000000000000000000000000000000000000000000000000000000002815260040161042e929190611663565b602060405180830381600087803b15801561044857600080fd5b505af115801561045c573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250610480919081019061126c565b9050600181141561049457600093506104b8565b7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff93505b7f2df77d1c0628700695d470889494a081451cfa7020555d66c3a06851a69900cf84876040516104e9929190611603565b60405180910390a1839450505050509392505050565b60008060008060008093506000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663f23f63c96040518163ffffffff167c010000000000000000000000000000000000000000000000000000000002815260040161057e906116e8565b602060405180830381600087803b15801561059857600080fd5b505af11580156105ac573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052506105d09190810190611243565b92508273ffffffffffffffffffffffffffffffffffffffff16637857d7c96040518163ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401602060405180830381600087803b15801561063657600080fd5b505af115801561064a573d6000803e3d6000fd5b505050506040513d601f19601f8201168201806040525061066e91908101906111c8565b91508173ffffffffffffffffffffffffffffffffffffffff1663cd30a1d1876040518263ffffffff167c01000000000000000000000000000000000000000000000000000000000281526004016106c591906116b3565b600060405180830381600087803b1580156106df57600080fd5b505af11580156106f3573d6000803e3d6000fd5b505050508273ffffffffffffffffffffffffffffffffffffffff1663e8434e3988846040518363ffffffff167c010000000000000000000000000000000000000000000000000000000002815260040161074e929190611633565b602060405180830381600087803b15801561076857600080fd5b505af115801561077c573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052506107a091908101906111f1565b905060008173ffffffffffffffffffffffffffffffffffffffff1663949d225d6040518163ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401602060405180830381600087803b15801561080857600080fd5b505af115801561081c573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250610840919081019061126c565b141561084f5760009350610873565b7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff93505b8394505050505092915050565b60008060008060008093506000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663f23f63c96040518163ffffffff167c01000000000000000000000000000000000000000000000000000000000281526004016108ff906116e8565b602060405180830381600087803b15801561091957600080fd5b505af115801561092d573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052506109519190810190611243565b92508273ffffffffffffffffffffffffffffffffffffffff16637857d7c96040518163ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401602060405180830381600087803b1580156109b757600080fd5b505af11580156109cb573d6000803e3d6000fd5b505050506040513d601f19601f820116820180604052506109ef91908101906111c8565b91508173ffffffffffffffffffffffffffffffffffffffff1663cd30a1d1886040518263ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401610a4691906116b3565b600060405180830381600087803b158015610a6057600080fd5b505af1158015610a74573d6000803e3d6000fd5b505050508273ffffffffffffffffffffffffffffffffffffffff166328bb211789846040518363ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401610acf929190611633565b602060405180830381600087803b158015610ae957600080fd5b505af1158015610afd573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250610b21919081019061126c565b90506001811415610b355760009350610b59565b7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff93505b7f9a8f360a4c891d1196b4948969410831d13b470b333506968061453fd7d827248487604051610b8a929190611603565b60405180910390a1839450505050509392505050565b606060008060606000806000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663f23f63c96040518163ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401610c1e906116e8565b602060405180830381600087803b158015610c3857600080fd5b505af1158015610c4c573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250610c709190810190611243565b94508473ffffffffffffffffffffffffffffffffffffffff1663e8434e39888773ffffffffffffffffffffffffffffffffffffffff16637857d7c96040518163ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401602060405180830381600087803b158015610cf357600080fd5b505af1158015610d07573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250610d2b91908101906111c8565b6040518363ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401610d64929190611633565b602060405180830381600087","803b158015610d7e57600080fd5b505af1158015610d92573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250610db691908101906111f1565b93508373ffffffffffffffffffffffffffffffffffffffff1663949d225d6040518163ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401602060405180830381600087803b158015610e1c57600080fd5b505af1158015610e30573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250610e54919081019061126c565b604051908082528060200260200182016040528015610e8757816020015b6060815260200190600190039081610e725790505b509250600091505b8373ffffffffffffffffffffffffffffffffffffffff1663949d225d6040518163ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401602060405180830381600087803b158015610ef357600080fd5b505af1158015610f07573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250610f2b919081019061126c565b8212156110ab578373ffffffffffffffffffffffffffffffffffffffff1663846719e0836040518263ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401610f8791906115e8565b602060405180830381600087803b158015610fa157600080fd5b505af1158015610fb5573d6000803e3d6000fd5b505050506040513d601f19601f82011682018060405250610fd9919081019061121a565b90508073ffffffffffffffffffffffffffffffffffffffff16639c981fcb6040518163ffffffff167c010000000000000000000000000000000000000000000000000000000002815260040161102e90611693565b600060405180830381600087803b15801561104857600080fd5b505af115801561105c573d6000803e3d6000fd5b505050506040513d6000823e3d601f19601f8201168201806040525061108591908101906112d6565b838381518110151561109357fe5b90602001906020020181905250816001019150610e8f565b8295505050505050919050565b60006110c482516117fb565b905092915050565b60006110d8825161180d565b905092915050565b60006110ec825161181f565b905092915050565b60006111008251611831565b905092915050565b60006111148251611843565b905092915050565b600082601f830112151561112f57600080fd5b813561114261113d8261176a565b61173d565b9150808252602083016020830185838301111561115e57600080fd5b611169838284611871565b50505092915050565b600082601f830112151561118557600080fd5b81516111986111938261176a565b61173d565b915080825260208301602083018583830111156111b457600080fd5b6111bf838284611880565b50505092915050565b6000602082840312156111da57600080fd5b60006111e8848285016110b8565b91505092915050565b60006020828403121561120357600080fd5b6000611211848285016110cc565b91505092915050565b60006020828403121561122c57600080fd5b600061123a848285016110e0565b91505092915050565b60006020828403121561125557600080fd5b6000611263848285016110f4565b91505092915050565b60006020828403121561127e57600080fd5b600061128c84828501611108565b91505092915050565b6000602082840312156112a757600080fd5b600082013567ffffffffffffffff8111156112c157600080fd5b6112cd8482850161111c565b91505092915050565b6000602082840312156112e857600080fd5b600082015167ffffffffffffffff81111561130257600080fd5b61130e84828501611172565b91505092915050565b6000806040838503121561132a57600080fd5b600083013567ffffffffffffffff81111561134457600080fd5b6113508582860161111c565b925050602083013567ffffffffffffffff81111561136d57600080fd5b6113798582860161111c565b9150509250929050565b60008060006060848603121561139857600080fd5b600084013567ffffffffffffffff8111156113b257600080fd5b6113be8682870161111c565b935050602084013567ffffffffffffffff8111156113db57600080fd5b6113e78682870161111c565b925050604084013567ffffffffffffffff81111561140457600080fd5b6114108682870161111c565b9150509250925092565b6000611425826117a3565b8084526020840193508360208202850161143e85611796565b60005b848110156114775783830388526114598383516114eb565b9250611464826117c4565b9150602088019750600181019050611441565b508196508694505050505092915050565b6114918161184d565b82525050565b6114a08161185f565b82525050565b6114af816117f1565b82525050565b60006114c0826117b9565b8084526114d4816020860160208601611880565b6114dd816118b3565b602085010191505092915050565b60006114f6826117ae565b80845261150a816020860160208601611880565b611513816118b3565b602085010191505092915050565b6000600682527f7065745f696400000000000000000000000000000000000000000000000000006020830152604082019050919050565b6000600682527f745f6361727400000000000000000000000000000000000000000000000000006020830152604082019050919050565b6000600782527f757365725f6964000000000000000000000000000000000000000000000000006020830152604082019050919050565b600060208201905081810360008301526115e0818461141a565b905092915050565b60006020820190506115fd60008301846114a6565b92915050565b600060408201905061161860008301856114a6565b818103602083015261162a81846114b5565b90509392505050565b6000604082019050818103600083015261164d81856114b5565b905061165c6020830184611488565b9392505050565b6000604082019050818103600083015261167d81856114b5565b905061168c6020830184611497565b9392505050565b600060208201905081810360008301526116ac81611521565b9050919050565b600060408201905081810360008301526116cc81611521565b905081810360208301526116e081846114b5565b905092915050565b6000602082019050818103600083015261170181611558565b9050919050565b600060408201905081810360008301526117218161158f565b9050818103602083015261173581846114b5565b905092915050565b6000604051905081810181811067ffffffffffffffff8211171561176057600080fd5b8060405250919050565b600067ffffffffffffffff82111561178157600080fd5b601f19601f8301169050602081019050919050565b6000602082019050919050565b600081519050919050565b600081519050919050565b600081519050919050565b6000602082019050919050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000819050919050565b6000611806826117d1565b9050919050565b6000611818826117d1565b9050919050565b600061182a826117d1565b9050919050565b600061183c826117d1565b9050919050565b6000819050919050565b6000611858826117d1565b9050919050565b600061186a826117d1565b9050919050565b82818337600083830152505050565b60005b8381101561189e578082015181840152602081019050611883565b838111156118ad576000848401525b50505050565b6000601f19601f83011690509190505600a265627a7a7230582043c6c01122f2440ec8b563934355e96a421fbabba356825028fc442bdce13fbb6c6578706572696d656e74616cf50037"};

    public static final String BINARY = String.join("", BINARY_ARRAY);

    public static final String[] ABI_ARRAY = {"[{\"constant\":false,\"inputs\":[{\"name\":\"user_id\",\"type\":\"string\"},{\"name\":\"pet_id\",\"type\":\"string\"},{\"name\":\"emit_description\",\"type\":\"string\"}],\"name\":\"insert\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"user_id\",\"type\":\"string\"},{\"name\":\"pet_id\",\"type\":\"string\"}],\"name\":\"unique\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"user_id\",\"type\":\"string\"},{\"name\":\"pet_id\",\"type\":\"string\"},{\"name\":\"emit_description\",\"type\":\"string\"}],\"name\":\"remove\",\"outputs\":[{\"name\":\"\",\"type\":\"int256\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\"inputs\":[{\"name\":\"user_id\",\"type\":\"string\"}],\"name\":\"select\",\"outputs\":[{\"name\":\"\",\"type\":\"string[]\"}],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"ret\",\"type\":\"int256\"},{\"indexed\":false,\"name\":\"emit_describe\",\"type\":\"string\"}],\"name\":\"InsertEvent\",\"type\":\"event\"},{\"anonymous\":false,\"inputs\":[{\"indexed\":false,\"name\":\"ret\",\"type\":\"int256\"},{\"indexed\":false,\"name\":\"emit_describe\",\"type\":\"string\"}],\"name\":\"RemoveEvent\",\"type\":\"event\"}]"};

    public static final String ABI = String.join("", ABI_ARRAY);

    public static final TransactionDecoder transactionDecoder = new TransactionDecoder(ABI, BINARY);

    public static final String FUNC_INSERT = "insert";

    public static final String FUNC_UNIQUE = "unique";

    public static final String FUNC_REMOVE = "remove";

    public static final String FUNC_SELECT = "select";

    public static final Event INSERTEVENT_EVENT = new Event("InsertEvent", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}, new TypeReference<Utf8String>() {}));
    ;

    public static final Event REMOVEEVENT_EVENT = new Event("RemoveEvent", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}, new TypeReference<Utf8String>() {}));
    ;

    @Deprecated
    protected Cart(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Cart(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Cart(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Cart(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static TransactionDecoder getTransactionDecoder() {
        return transactionDecoder;
    }

    public RemoteCall<TransactionReceipt> insert(String user_id, String pet_id, String emit_description) {
        final Function function = new Function(
                FUNC_INSERT, 
                Arrays.<Type>asList(new org.fisco.bcos.web3j.abi.datatypes.Utf8String(user_id), 
                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(pet_id), 
                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(emit_description)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public void insert(String user_id, String pet_id, String emit_description, TransactionSucCallback callback) {
        final Function function = new Function(
                FUNC_INSERT, 
                Arrays.<Type>asList(new org.fisco.bcos.web3j.abi.datatypes.Utf8String(user_id), 
                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(pet_id), 
                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(emit_description)), 
                Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String insertSeq(String user_id, String pet_id, String emit_description) {
        final Function function = new Function(
                FUNC_INSERT, 
                Arrays.<Type>asList(new org.fisco.bcos.web3j.abi.datatypes.Utf8String(user_id), 
                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(pet_id), 
                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(emit_description)), 
                Collections.<TypeReference<?>>emptyList());
        return createTransactionSeq(function);
    }

    public Tuple3<String, String, String> getInsertInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_INSERT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());;
        return new Tuple3<String, String, String>(

                (String) results.get(0).getValue(), 
                (String) results.get(1).getValue(), 
                (String) results.get(2).getValue()
                );
    }

    public Tuple1<BigInteger> getInsertOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function = new Function(FUNC_INSERT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());;
        return new Tuple1<BigInteger>(

                (BigInteger) results.get(0).getValue()
                );
    }

    public RemoteCall<TransactionReceipt> unique(String user_id, String pet_id) {
        final Function function = new Function(
                FUNC_UNIQUE, 
                Arrays.<Type>asList(new org.fisco.bcos.web3j.abi.datatypes.Utf8String(user_id), 
                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(pet_id)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public void unique(String user_id, String pet_id, TransactionSucCallback callback) {
        final Function function = new Function(
                FUNC_UNIQUE, 
                Arrays.<Type>asList(new org.fisco.bcos.web3j.abi.datatypes.Utf8String(user_id), 
                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(pet_id)), 
                Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String uniqueSeq(String user_id, String pet_id) {
        final Function function = new Function(
                FUNC_UNIQUE, 
                Arrays.<Type>asList(new org.fisco.bcos.web3j.abi.datatypes.Utf8String(user_id), 
                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(pet_id)), 
                Collections.<TypeReference<?>>emptyList());
        return createTransactionSeq(function);
    }

    public Tuple2<String, String> getUniqueInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_UNIQUE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());;
        return new Tuple2<String, String>(

                (String) results.get(0).getValue(), 
                (String) results.get(1).getValue()
                );
    }

    public Tuple1<BigInteger> getUniqueOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function = new Function(FUNC_UNIQUE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());;
        return new Tuple1<BigInteger>(

                (BigInteger) results.get(0).getValue()
                );
    }

    public RemoteCall<TransactionReceipt> remove(String user_id, String pet_id, String emit_description) {
        final Function function = new Function(
                FUNC_REMOVE, 
                Arrays.<Type>asList(new org.fisco.bcos.web3j.abi.datatypes.Utf8String(user_id), 
                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(pet_id), 
                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(emit_description)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public void remove(String user_id, String pet_id, String emit_description, TransactionSucCallback callback) {
        final Function function = new Function(
                FUNC_REMOVE, 
                Arrays.<Type>asList(new org.fisco.bcos.web3j.abi.datatypes.Utf8String(user_id), 
                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(pet_id), 
                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(emit_description)), 
                Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String removeSeq(String user_id, String pet_id, String emit_description) {
        final Function function = new Function(
                FUNC_REMOVE, 
                Arrays.<Type>asList(new org.fisco.bcos.web3j.abi.datatypes.Utf8String(user_id), 
                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(pet_id), 
                new org.fisco.bcos.web3j.abi.datatypes.Utf8String(emit_description)), 
                Collections.<TypeReference<?>>emptyList());
        return createTransactionSeq(function);
    }

    public Tuple3<String, String, String> getRemoveInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_REMOVE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());;
        return new Tuple3<String, String, String>(

                (String) results.get(0).getValue(), 
                (String) results.get(1).getValue(), 
                (String) results.get(2).getValue()
                );
    }

    public Tuple1<BigInteger> getRemoveOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function = new Function(FUNC_REMOVE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());;
        return new Tuple1<BigInteger>(

                (BigInteger) results.get(0).getValue()
                );
    }

    public RemoteCall<TransactionReceipt> select(String user_id) {
        final Function function = new Function(
                FUNC_SELECT, 
                Arrays.<Type>asList(new org.fisco.bcos.web3j.abi.datatypes.Utf8String(user_id)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public void select(String user_id, TransactionSucCallback callback) {
        final Function function = new Function(
                FUNC_SELECT, 
                Arrays.<Type>asList(new org.fisco.bcos.web3j.abi.datatypes.Utf8String(user_id)), 
                Collections.<TypeReference<?>>emptyList());
        asyncExecuteTransaction(function, callback);
    }

    public String selectSeq(String user_id) {
        final Function function = new Function(
                FUNC_SELECT, 
                Arrays.<Type>asList(new org.fisco.bcos.web3j.abi.datatypes.Utf8String(user_id)), 
                Collections.<TypeReference<?>>emptyList());
        return createTransactionSeq(function);
    }

    public Tuple1<String> getSelectInput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getInput().substring(10);
        final Function function = new Function(FUNC_SELECT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());;
        return new Tuple1<String>(

                (String) results.get(0).getValue()
                );
    }

    public Tuple1<List<String>> getSelectOutput(TransactionReceipt transactionReceipt) {
        String data = transactionReceipt.getOutput();
        final Function function = new Function(FUNC_SELECT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Utf8String>>() {}));
        List<Type> results = FunctionReturnDecoder.decode(data, function.getOutputParameters());;
        return new Tuple1<List<String>>(

                convertToNative((List<Utf8String>) results.get(0).getValue())
                );
    }

    public List<InsertEventEventResponse> getInsertEventEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(INSERTEVENT_EVENT, transactionReceipt);
        ArrayList<InsertEventEventResponse> responses = new ArrayList<InsertEventEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            InsertEventEventResponse typedResponse = new InsertEventEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.ret = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.emit_describe = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void registerInsertEventEventLogFilter(String fromBlock, String toBlock, List<String> otherTopcs, EventLogPushWithDecodeCallback callback) {
        String topic0 = EventEncoder.encode(INSERTEVENT_EVENT);
        registerEventLogPushFilter(ABI,BINARY,topic0,fromBlock,toBlock,otherTopcs,callback);
    }

    public void registerInsertEventEventLogFilter(EventLogPushWithDecodeCallback callback) {
        String topic0 = EventEncoder.encode(INSERTEVENT_EVENT);
        registerEventLogPushFilter(ABI,BINARY,topic0,callback);
    }

    public List<RemoveEventEventResponse> getRemoveEventEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(REMOVEEVENT_EVENT, transactionReceipt);
        ArrayList<RemoveEventEventResponse> responses = new ArrayList<RemoveEventEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            RemoveEventEventResponse typedResponse = new RemoveEventEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.ret = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.emit_describe = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public void registerRemoveEventEventLogFilter(String fromBlock, String toBlock, List<String> otherTopcs, EventLogPushWithDecodeCallback callback) {
        String topic0 = EventEncoder.encode(REMOVEEVENT_EVENT);
        registerEventLogPushFilter(ABI,BINARY,topic0,fromBlock,toBlock,otherTopcs,callback);
    }

    public void registerRemoveEventEventLogFilter(EventLogPushWithDecodeCallback callback) {
        String topic0 = EventEncoder.encode(REMOVEEVENT_EVENT);
        registerEventLogPushFilter(ABI,BINARY,topic0,callback);
    }

    @Deprecated
    public static Cart load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Cart(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Cart load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Cart(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Cart load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Cart(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Cart load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Cart(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Cart> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Cart.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<Cart> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Cart.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Cart> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Cart.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Cart> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Cart.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static class InsertEventEventResponse {
        public Log log;

        public BigInteger ret;

        public String emit_describe;
    }

    public static class RemoveEventEventResponse {
        public Log log;

        public BigInteger ret;

        public String emit_describe;
    }
}
