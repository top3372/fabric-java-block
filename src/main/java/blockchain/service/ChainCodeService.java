package blockchain.service;


import org.hyperledger.fabric.sdk.BlockInfo;



public interface ChainCodeService {

    /**
     *
     * @param name
     * @param password
     * @return
     */
    public String register(String name, String password,String peerWithOrg) throws  Exception;


    String loadUserFromPersistence(String name,String password,String peerWithOrg) throws Exception;

    /**
     *
     * @param channelName
     * @return
     * @throws Exception
     */
    public String constructChannel(String channelName,String peerWithOrg) throws Exception;


    /**
     *
     * @param name
     * @param chaincodeName
     * @param
     * @return
     */
    public String installChaincode(String name,String peerWithOrg,String channelName,String chaincodeName,String chainCodeVersion) throws Exception ;


    /**
     *
     * @param name
     * @param chaincodeName
     * @param chaincodeFunction
     * @param chaincodeArgs
     * @param chainCodeVersion
     * @return
     */
    public String instantiateChaincode(String name,String peerWithOrg,String channelName,String chaincodeName, String chaincodeFunction, String[] chaincodeArgs,String chainCodeVersion) throws Exception ;


    /**
     *
     * @param name
     * @param chaincodeName
     * @param chaincodeFunction
     * @param chaincodeArgs
     * @return
     */
    public String invokeChaincode(String name,String belongWithOrg,String[] peerWithOrgs,String channelName,String chaincodeName, String chaincodeFunction, String[] chaincodeArgs,String chainCodeVersion) throws Exception ;

    /**
     *
     * @param name
     * @param chaincodeName
     * @param chaincodeFunction
     * @param chaincodeArgs
     * @return
     */
    String queryChaincode(String name,String peerWithOrg,String channelName,String chaincodeName, String chaincodeFunction, String[] chaincodeArgs,String chainCodeVersion) throws Exception ;

    /**
     *
     * @param name
     * @param peerWithOrg
     * @param channelName
     * @return
     * @throws Exception
     */
    BlockInfo blockchainInfo(String name,String peerWithOrg, String channelName) throws Exception ;


    String blockChainInfoByTxnId(String name, String peerWithOrg, String channelName, String txId) throws Exception;

    /**
     *
     * @param name
     * @param peerWithOrg
     * @param channelName
     * @param chaincodeName
     * @param chaincodeFunction
     * @param chaincodeArgs
     * @param chainCodeVersion
     * @return
     * @throws Exception
     */
    String updateChaincode(String name, String peerWithOrg, String channelName, String chaincodeName, String chaincodeFunction, String[] chaincodeArgs, String chainCodeVersion) throws Exception;

    String joinChannel( String channelName, String peerWithOrg) throws Exception;
}
