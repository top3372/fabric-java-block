package blockchain.dto;


public class BlockDto extends ChannelDto{

    private String txId;

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }
}
