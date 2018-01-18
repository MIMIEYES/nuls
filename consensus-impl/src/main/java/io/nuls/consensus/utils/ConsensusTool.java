package io.nuls.consensus.utils;

import io.nuls.account.entity.Account;
import io.nuls.account.service.intf.AccountService;
import io.nuls.consensus.constant.ConsensusStatusEnum;
import io.nuls.consensus.constant.PocConsensusConstant;
import io.nuls.consensus.entity.Consensus;
import io.nuls.consensus.entity.block.BlockData;
import io.nuls.consensus.entity.member.Agent;
import io.nuls.consensus.entity.member.Delegate;
import io.nuls.core.chain.entity.*;
import io.nuls.core.constant.ErrorCode;
import io.nuls.core.context.NulsContext;
import io.nuls.core.exception.NulsException;
import io.nuls.core.exception.NulsRuntimeException;
import io.nuls.core.utils.log.Log;
import io.nuls.db.entity.BlockHeaderPo;
import io.nuls.db.entity.DelegateAccountPo;
import io.nuls.db.entity.DelegatePo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Niels
 * @date 2017/12/6
 */
public class ConsensusTool {

    private static AccountService accountService = NulsContext.getInstance().getService(AccountService.class);

    public static final BlockHeaderPo toPojo(BlockHeader header) {
        BlockHeaderPo po = new BlockHeaderPo();
        po.setTxcount(header.getTxCount());
        if (header.getHeight() > 1) {
            po.setPreHash(header.getPreHash().getDigestHex());
        }
        po.setMerkleHash(header.getMerkleHash().getDigestHex());
        po.setHeight(header.getHeight());
        po.setCreateTime(header.getTime());
        po.setHash(header.getHash().getDigestHex());
        if (null != header.getSign()) {
            po.setSign(header.getSign().getSignBytes());
        }
        po.setTxcount(header.getTxCount());
        po.setConsensusAddress(header.getPackingAddress());
        return po;
    }

    public static final BlockHeader fromPojo(BlockHeaderPo po) {
        if (null == po) {
            return null;
        }
        BlockHeader header = new BlockHeader();
        //todo
        return header;
    }

    public static Consensus<Agent> fromPojo(DelegateAccountPo po) {
        if (null == po) {
            return null;
        }
        Agent agent = new Agent();
        agent.setStatus(ConsensusStatusEnum.WAITING.getCode());
        agent.setDeposit(Na.valueOf(po.getDeposit()));
        agent.setCommissionRate(po.getCommissionRate());
        agent.setDelegateAddress(po.getNodeAddress());
        agent.setIntroduction(po.getRemark());
        agent.setStartTime(po.getStartTime());
        agent.setStatus(po.getStatus());
        Consensus<Agent> ca = new Consensus<>();
        ca.setAddress(po.getAddress());
        ca.setExtend(agent);
        return ca;
    }

    public static Consensus<Delegate> fromPojo(DelegatePo po) {
        if (null == po) {
            return null;
        }
        Consensus<Delegate> ca = new Consensus<>();
        ca.setAddress(po.getAddress());
        Delegate delegate = new Delegate();
        delegate.setDelegateAddress(po.getAgentAddress());
        delegate.setDeposit(Na.valueOf(po.getDeposit()));
        delegate.setStartTime(po.getTime());
        delegate.setHash(po.getId());
        ca.setExtend(delegate);
        return ca;
    }

    public static DelegateAccountPo agentToPojo(Consensus<Agent> bean) {
        if (null == bean) {
            return null;
        }
        DelegateAccountPo po = new DelegateAccountPo();
        po.setAddress(bean.getAddress());
        po.setDeposit(bean.getExtend().getDeposit().getValue());
        po.setStartTime(bean.getExtend().getStartTime());
        po.setRemark(bean.getExtend().getIntroduction());
        po.setNodeAddress(bean.getExtend().getDelegateAddress());
        po.setId(bean.getAddress());
        po.setStatus(bean.getExtend().getStatus());
        return po;
    }

    public static DelegatePo delegateToPojo(Consensus<Delegate> bean) {
        if (null == bean) {
            return null;
        }
        DelegatePo po = new DelegatePo();
        po.setAddress(bean.getAddress());
        po.setDeposit(bean.getExtend().getDeposit().getValue());
        po.setTime(bean.getExtend().getStartTime());
        po.setAgentAddress(bean.getExtend().getDelegateAddress());
        po.setId(bean.getExtend().getHash());
        return po;
    }

    public static Block createBlock(BlockData blockData) {
        Account account = accountService.getDefaultAccount();
        if (null == account) {
            throw new NulsRuntimeException(ErrorCode.ACCOUNT_NOT_EXIST);
        }
        Block block = new Block();
        block.setTxs(blockData.getTxList());
        BlockHeader header = new BlockHeader();
        block.setHeader(header);
        try {
            block.getHeader().setExtend(blockData.getRoundData().serialize());
        } catch (IOException e) {
            Log.error(e);
        }
        header.setHeight(blockData.getHeight());
        header.setTime(blockData.getTime());
        header.setPreHash(blockData.getPreHash());
        header.setTxCount(blockData.getTxList().size());
        List<NulsDigestData> txHashList = new ArrayList<>();
        for (int i = 0; i < blockData.getTxList().size(); i++) {
            Transaction tx = blockData.getTxList().get(i);
            txHashList.add(tx.getHash());
        }
        header.setPackingAddress(account.getAddress().toString());
        header.setMerkleHash(NulsDigestData.calcMerkleDigestData(txHashList));
        header.setHash(NulsDigestData.calcDigestData(block));
        header.setSign(accountService.signData(header.getHash(), null));
        return block;
    }
}

